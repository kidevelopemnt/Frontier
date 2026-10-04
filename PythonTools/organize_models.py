#!/usr/bin/env python3
"""
Organize an OBJ model and its dependencies into separate model/texture folders.

Example:
    python organize_model.py \
        --input downloads/OakTree \
        --models Game/src/main/resources/models/trees \
        --textures Game/src/main/resources/textures/trees \
        --dry-run

The script copies files; it does not modify the originals.
"""

from __future__ import annotations

import argparse
import re
import shutil
import sys
from pathlib import Path, PurePosixPath

# Common image formats used by MTL files.
TEXTURE_EXTENSIONS = {
    ".png", ".jpg", ".jpeg", ".tga", ".bmp", ".tif", ".tiff",
    ".dds", ".webp", ".exr", ".hdr",
}

# MTL texture-map directives that may reference image files.
MTL_TEXTURE_DIRECTIVES = {
    "map_Ka", "map_Kd", "map_Ks", "map_Ns", "map_d",
    "decal", "disp", "bump", "map_bump", "norm", "refl",
}

# Options accepted by common MTL map directives, including the number of
# following arguments. Unknown options are handled conservatively.
MTL_OPTIONS = {
    "-blendu": 1, "-blendv": 1, "-boost": 1, "-cc": 1,
    "-clamp": 1, "-texres": 1, "-bm": 1, "-imfchan": 1,
    "-type": 1, "-mm": 2, "-o": 3, "-s": 3, "-t": 3,
}


def normalized_reference(value: str) -> str:
    """Normalize slash direction and remove surrounding quotes."""
    return value.strip().strip('"').strip("'").replace("\\", "/")


def safe_resolve(root: Path, reference: str) -> Path | None:
    """Resolve a relative reference and reject paths escaping the input root."""
    ref = normalized_reference(reference)
    candidate = (root / Path(ref)).resolve()
    root_resolved = root.resolve()
    try:
        candidate.relative_to(root_resolved)
    except ValueError:
        return None
    return candidate if candidate.is_file() else None


def find_case_insensitive(root: Path, reference: str) -> Path | None:
    """Try the exact relative path, then a case-insensitive relative lookup."""
    exact = safe_resolve(root, reference)
    if exact:
        return exact

    parts = [p for p in PurePosixPath(normalized_reference(reference)).parts if p not in (".", "")]
    current = root
    for part in parts:
        if not current.is_dir():
            return None
        match = next((p for p in current.iterdir() if p.name.casefold() == part.casefold()), None)
        if match is None:
            return None
        current = match
    return current if current.is_file() else None


def relative_reference(source_file: Path, reference: str, root: Path) -> Path | None:
    """Resolve a file reference relative to the file containing that reference."""
    ref = normalized_reference(reference)
    if not ref:
        return None

    # Most OBJ/MTL references are relative to the referring file's directory.
    found = find_case_insensitive(source_file.parent, ref)
    if found:
        return found

    # Some packs incorrectly write paths relative to the pack root.
    return find_case_insensitive(root, ref)


def parse_map_filename(arguments: list[str]) -> str | None:
    """Extract the filename from an MTL map directive while preserving options."""
    if not arguments:
        return None

    i = 0
    while i < len(arguments) and arguments[i].startswith("-"):
        option = arguments[i]
        i += 1
        count = MTL_OPTIONS.get(option, 1)
        i += min(count, len(arguments) - i)

    if i >= len(arguments):
        # If an unfamiliar option confused parsing, try the final token.
        return arguments[-1] if arguments[-1] else None

    # Paths containing spaces should be quoted in MTL files; shlex parsing below
    # preserves quoted paths as one token.
    return " ".join(arguments[i:]).strip()


def format_mtl_map_line(directive: str, original_args: str, new_path: str) -> str:
    """Replace only the final map filename, retaining map options."""
    tokens = split_mtl_args(original_args)
    old_path = parse_map_filename(tokens)
    if old_path is None:
        return f"{directive} {original_args}".rstrip()

    # Locate the filename portion by reconstructing the option prefix.
    i = 0
    while i < len(tokens) and tokens[i].startswith("-"):
        option = tokens[i]
        i += 1
        i += min(MTL_OPTIONS.get(option, 1), len(tokens) - i)
    prefix = tokens[:i]
    replacement = prefix + [new_path]
    return directive + " " + " ".join(quote_mtl_token(t) for t in replacement)


def split_mtl_args(value: str) -> list[str]:
    """Tokenize MTL arguments, supporting quoted filenames."""
    import shlex
    try:
        return shlex.split(value, posix=True)
    except ValueError:
        return value.split()


def quote_mtl_token(value: str) -> str:
    if any(ch.isspace() for ch in value):
        return '"' + value.replace('"', r'\"') + '"'
    return value


class Organizer:
    def __init__(self, input_dir: Path, model_dir: Path, texture_dir: Path, dry_run: bool):
        self.input_dir = input_dir.resolve()
        self.model_dir = model_dir.resolve()
        self.texture_dir = texture_dir.resolve()
        self.dry_run = dry_run
        self.actions: list[tuple[Path, Path]] = []
        self.warnings: list[str] = []
        self.destination_sources: dict[Path, Path] = {}
        self.obj_seen: set[Path] = set()
        self.mtl_seen: set[Path] = set()
        self.texture_destinations: dict[Path, Path] = {}

    def plan_copy(self, source: Path, destination: Path) -> None:
        source = source.resolve()
        destination = destination.resolve()

        prior_source = self.destination_sources.get(destination)
        if prior_source is not None and prior_source != source:
            raise RuntimeError(
                f"Filename collision: {prior_source} and {source} both map to {destination}. "
                "Use separate output folders or rename one of the files."
            )
        self.destination_sources[destination] = source

        if source == destination:
            return
        if not any(src == source and dst == destination for src, dst in self.actions):
            self.actions.append((source, destination))

    def destination_for_texture(self, source: Path) -> Path:
        return self.texture_dir / source.name

    def process_mtl(self, mtl_path: Path) -> None:
        mtl_path = mtl_path.resolve()
        if mtl_path in self.mtl_seen:
            return
        self.mtl_seen.add(mtl_path)

        try:
            lines = mtl_path.read_text(encoding="utf-8-sig").splitlines()
        except UnicodeDecodeError:
            lines = mtl_path.read_text(encoding="latin-1").splitlines()

        rewritten: list[str] = []
        for line in lines:
            stripped = line.strip()
            if not stripped or stripped.startswith("#"):
                rewritten.append(line)
                continue

            match = re.match(r"^(\s*)(\S+)(?:\s+(.*))?$", line)
            if not match:
                rewritten.append(line)
                continue

            indent, directive, args = match.groups()
            args = args or ""

            if directive in MTL_TEXTURE_DIRECTIVES:
                tokens = split_mtl_args(args)
                filename = parse_map_filename(tokens)
                source_texture = relative_reference(mtl_path, filename or "", self.input_dir)
                if source_texture is None:
                    self.warnings.append(
                        f"Texture not found: {filename!r} referenced by {mtl_path}"
                    )
                    rewritten.append(line)
                    continue

                destination = self.destination_for_texture(source_texture)
                self.plan_copy(source_texture, destination)
                self.texture_destinations[source_texture] = destination

                # Compute path from the output MTL to the output texture.
                output_mtl = self.model_dir / mtl_path.name
                relative = Path(
                    __import__("os").path.relpath(destination, output_mtl.parent)
                ).as_posix()
                rewritten.append(indent + format_mtl_map_line(directive, args, relative))
            else:
                rewritten.append(line)

        output_mtl = self.model_dir / mtl_path.name
        content = "\n".join(rewritten) + ("\n" if lines else "")
        self.plan_text(mtl_path, output_mtl, content)

    def plan_text(self, source: Path, destination: Path, content: str) -> None:
        # Store rewritten content as a deferred write. A source==destination
        # still needs rewriting, so it is represented separately.
        self.text_writes.append((source, destination, content))

    def process_obj(self, obj_path: Path) -> None:
        obj_path = obj_path.resolve()
        if obj_path in self.obj_seen:
            return
        self.obj_seen.add(obj_path)

        try:
            lines = obj_path.read_text(encoding="utf-8-sig").splitlines()
        except UnicodeDecodeError:
            lines = obj_path.read_text(encoding="latin-1").splitlines()

        rewritten: list[str] = []
        for line in lines:
            stripped = line.strip()
            if not stripped or stripped.startswith("#"):
                rewritten.append(line)
                continue

            match = re.match(r"^(\s*)mtllib\s+(.+?)\s*$", line)
            if not match:
                rewritten.append(line)
                continue

            indent, refs_text = match.groups()
            refs = split_mtl_args(refs_text)
            rewritten_refs: list[str] = []

            for ref in refs:
                mtl_path = relative_reference(obj_path, ref, self.input_dir)
                if mtl_path is None:
                    self.warnings.append(f"MTL not found: {ref!r} referenced by {obj_path}")
                    rewritten_refs.append(ref)
                    continue

                self.process_mtl(mtl_path)
                rewritten_refs.append(mtl_path.name)

            rewritten.append(indent + "mtllib " + " ".join(quote_mtl_token(r) for r in rewritten_refs))

        output_obj = self.model_dir / obj_path.name
        content = "\n".join(rewritten) + ("\n" if lines else "")
        self.plan_text(obj_path, output_obj, content)

    def run(self) -> None:
        self.text_writes: list[tuple[Path, Path, str]] = []

        if not self.input_dir.is_dir():
            raise ValueError(f"Input directory does not exist: {self.input_dir}")

        model_files = sorted(
            p for p in self.input_dir.rglob("*")
            if p.is_file() and p.suffix.lower() == ".obj"
        )
        if not model_files:
            raise ValueError(f"No .obj files found under {self.input_dir}")

        for obj in model_files:
            self.process_obj(obj)

        # Dry-run lists all planned work without creating directories or files.
        print(f"{'DRY RUN — ' if self.dry_run else ''}Found {len(model_files)} OBJ file(s).")
        for source, destination in self.actions:
            print(f"COPY  {source} -> {destination}")
        for source, destination, _ in self.text_writes:
            print(f"REWRITE  {source} -> {destination}")
        for warning in self.warnings:
            print(f"WARNING: {warning}", file=sys.stderr)

        if self.dry_run:
            return

        # Copy binary files first.
        for source, destination in self.actions:
            destination.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(source, destination)

        # Then write rewritten OBJ/MTL files.
        for source, destination, content in self.text_writes:
            destination.parent.mkdir(parents=True, exist_ok=True)
            destination.write_text(content, encoding="utf-8")


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Copy OBJ/MTL files and textures into Frontier resource folders, rewriting references."
    )
    parser.add_argument("--input", required=True, type=Path, help="Input folder containing downloaded model files.")
    parser.add_argument("--models", required=True, type=Path, help="Output folder for .obj and .mtl files.")
    parser.add_argument("--textures", required=True, type=Path, help="Output folder for texture images.")
    parser.add_argument("--dry-run", action="store_true", help="Print planned operations without writing files.")
    args = parser.parse_args()

    try:
        organizer = Organizer(args.input, args.models, args.textures, args.dry_run)
        organizer.run()
    except (OSError, RuntimeError, ValueError) as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
