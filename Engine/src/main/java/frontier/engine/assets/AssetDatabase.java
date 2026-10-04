package frontier.engine.assets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class AssetDatabase {
    private final Path assetRoot;
    private final AssetMetadataManager metadataManager;
    private final Map<AssetID, AssetRecord> assetsById = new HashMap<>();
    private final Map<Path, AssetID> idsByPath = new HashMap<>();

    public AssetDatabase(Path assetRoot) {
        this.assetRoot = Objects.requireNonNull(assetRoot).toAbsolutePath().normalize();
        this.metadataManager = new AssetMetadataManager();
    }

    public void refresh() throws IOException {
        assetsById.clear();
        idsByPath.clear();

        if (!Files.isDirectory(assetRoot))
            throw new IOException("Asset root does not exist or is not a directory: " + assetRoot);

        try (var stream = Files.walk(assetRoot)) {
            for (Path path : stream.filter(Files::isRegularFile).toList()) {
                if (isMetadataFile(path) || AssetType.fromPath(path.toString()) == AssetType.UNKNOWN) continue;
                register(path);
            }
        }
    }

    public AssetID getOrCreateId(Path path) throws IOException {
        Path absolutePath = normalizeAbsolute(path);
        Path relativePath = toRelativePath(absolutePath);
        AssetID existing = idsByPath.get(relativePath);
        if (existing != null) return existing;

        AssetMetadata metadata = metadataManager.read(absolutePath);
        if (metadata == null) metadata = metadataManager.create(absolutePath);

        AssetRecord record = createRecord(metadata.getId(), relativePath);
        register(record);
        return record.getId();
    }

    public AssetRecord get(AssetID id) { return assetsById.get(id); }
    public AssetRecord get(Path path) { return assetsById.get(idsByPath.get(normalizeRelative(path))); }
    public AssetID getId(Path path) { return idsByPath.get(normalizeRelative(path)); }
    public Path getPath(AssetID id) {
        AssetRecord record = get(id);
        return record == null ? null : record.getPath();
    }
    public Map<AssetID, AssetRecord> getAssets() {
        return Collections.unmodifiableMap(assetsById);
    }
    public Path getAssetRoot() { return assetRoot; }

    private void register(Path absolutePath) throws IOException {
        Path relativePath = toRelativePath(absolutePath);
        AssetMetadata metadata = metadataManager.read(absolutePath);
        if (metadata == null) metadata = metadataManager.create(absolutePath);
        register(createRecord(metadata.getId(), relativePath));
    }

    private AssetRecord createRecord(AssetID id, Path relativePath) {
        AssetType type = AssetType.fromPath(relativePath.toString());
        if (type == AssetType.UNKNOWN)
            throw new IllegalArgumentException("Unknown asset type: " + relativePath);
        return new AssetRecord(id, relativePath, type);
    }

    private void register(AssetRecord record) {
        AssetRecord existing = assetsById.putIfAbsent(record.getId(), record);
        if (existing != null && !existing.getPath().equals(record.getPath()))
            throw new IllegalStateException("Duplicate AssetID " + record.getId() +
                    " found in " + existing.getPath() + " and " + record.getPath());

        AssetID existingId = idsByPath.putIfAbsent(record.getPath(), record.getId());
        if (existingId != null && !existingId.equals(record.getId()))
            throw new IllegalStateException("Asset path has multiple AssetIDs: " + record.getPath());
    }

    private Path normalizeAbsolute(Path path) {
        return (path.isAbsolute() ? path : assetRoot.resolve(path)).toAbsolutePath().normalize();
    }

    private Path normalizeRelative(Path path) {
        return toRelativePath(normalizeAbsolute(path));
    }

    private Path toRelativePath(Path path) {
        Path normalized = path.toAbsolutePath().normalize();
        if (!normalized.startsWith(assetRoot))
            throw new IllegalArgumentException("Path is outside the asset root: " + normalized);
        return assetRoot.relativize(normalized);
    }

    private boolean isMetadataFile(Path path) {
        return path.getFileName().toString().endsWith(".meta");
    }
}
