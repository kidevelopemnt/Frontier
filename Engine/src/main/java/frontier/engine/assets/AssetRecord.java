package frontier.engine.assets;

import java.nio.file.Path;
import java.util.Objects;

public final class AssetRecord {
    private final AssetID id;
    private final Path path;
    private final AssetType type;

    public AssetRecord(AssetID id, Path path, AssetType type) {
        this.id = Objects.requireNonNull(id);
        this.path = Objects.requireNonNull(path);
        this.type = Objects.requireNonNull(type);
    }

    public AssetID getId() { return id; }
    public Path getPath() { return path; }
    public AssetType getType() { return type; }

    @Override public String toString() {
        return "AssetRecord{" + "id=" + id + ", path=" + path + ", type=" + type + '}';
    }
}
