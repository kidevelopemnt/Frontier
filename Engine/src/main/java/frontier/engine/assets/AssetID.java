package frontier.engine.assets;

import java.util.Objects;
import java.util.UUID;

public final class AssetID {
    private final UUID uuid;

    public AssetID(UUID uuid) {
        this.uuid = Objects.requireNonNull(uuid);
    }

    public AssetID(String value) {
        this(UUID.fromString(Objects.requireNonNull(value)));
    }

    public static AssetID generate() {
        return new AssetID(UUID.randomUUID());
    }

    public UUID getUUID() { return uuid; }
    public String getValue() { return uuid.toString(); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return uuid.equals(((AssetID) o).uuid);
    }

    @Override public int hashCode() { return uuid.hashCode(); }
    @Override public String toString() { return uuid.toString(); }
}
