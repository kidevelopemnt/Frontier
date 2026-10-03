package frontier.engine.assets;

public abstract class Asset {
    private final AssetID assetId;

    protected Asset(AssetID assetId) {
        this.assetId = assetId;
    }

    public AssetID getAssetId() {
        return assetId;
    }

    public void unload() {
        // Assets do not unload their dependencies
    };
}
