package frontier.engine.assets;

import java.util.Objects;

public final class AssetMetadata {
    private final AssetID id;

    public AssetMetadata(AssetID id) {
        this.id = Objects.requireNonNull(id);
    }

    public AssetID getId() { return id; }
}
