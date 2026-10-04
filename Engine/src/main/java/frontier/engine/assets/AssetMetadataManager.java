package frontier.engine.assets;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AssetMetadataManager {
    private static final String GUID_PREFIX = "guid=";

    public AssetMetadata read(Path assetPath) throws IOException {
        Path metadataPath = getMetadataPath(assetPath);
        if (!Files.isRegularFile(metadataPath)) return null;

        for (String line : Files.readAllLines(metadataPath, StandardCharsets.UTF_8)) {
            String trimmed = line.trim();
            if (trimmed.startsWith(GUID_PREFIX)) {
                try {
                    return new AssetMetadata(new AssetID(trimmed.substring(GUID_PREFIX.length()).trim()));
                } catch (IllegalArgumentException e) {
                    throw new IOException("Invalid asset GUID in metadata file: " + metadataPath, e);
                }
            }
        }
        throw new IOException("Asset metadata file does not contain a GUID: " + metadataPath);
    }

    public AssetMetadata create(Path assetPath) throws IOException {
        AssetMetadata metadata = new AssetMetadata(AssetID.generate());
        write(assetPath, metadata);
        return metadata;
    }

    public void write(Path assetPath, AssetMetadata metadata) throws IOException {
        Files.writeString(getMetadataPath(assetPath),
                GUID_PREFIX + metadata.getId() + System.lineSeparator(),
                StandardCharsets.UTF_8);
    }

    public Path getMetadataPath(Path assetPath) {
        return assetPath.resolveSibling(assetPath.getFileName() + ".meta");
    }
}
