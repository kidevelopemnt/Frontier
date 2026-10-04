package frontier.engine.assets;

import java.util.Objects;
import java.util.UUID;

public final class AssetID {
    private final UUID uuid;

    public AssetID(UUID uuid) {
        this.uuid = uuid;
    }

    public AssetID() {
        this(UUID.randomUUID());
    }

    public UUID get() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssetID assetId = (AssetID) o;
        return uuid.equals(assetId.get());
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}
