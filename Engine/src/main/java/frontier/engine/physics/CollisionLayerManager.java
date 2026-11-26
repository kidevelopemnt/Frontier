package frontier.engine.physics;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CollisionLayerManager {
    // FIXME: Currently, a Physics object is created for each scene
    //  This means that you can't manage collision layers until you assign a scene
    //  This also means that you have to redefine collision layers when you switch scenes

    private static final int MAX_LAYERS = 32;

    private final Map<String, Integer> layers = new HashMap<>();
    private final int[] collisionMasks = new int[MAX_LAYERS];

    public CollisionLayerManager() {
        layers.put(normalizeName("world"), 0);
        setCollision("world", "world", true);
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Layer name cannot be empty.");
        }

        return name.trim().toLowerCase(Locale.ROOT);
    }

    public int layerIndex(String layerName) {
        layerName = normalizeName(layerName);
        if (layers.containsKey(layerName)) {
            return layers.get(layerName);
        }
        throw new IllegalArgumentException("Layer not found: " + layerName);
    }

    public int getLayerMask(String layerName) {
        return 1 << layerIndex(layerName);
    }

    public int getCollisionMask(String layerName) {
        layerName = normalizeName(layerName);
        if (layers.containsKey(layerName)) {
            return collisionMasks[layers.get(layerName)];
        }
        throw new IllegalArgumentException("Layer not found: " + layerName);
    }

    public void setCollision(String layerA, String layerB, boolean collisionEnabled) {
        layerA = normalizeName(layerA);
        layerB = normalizeName(layerB);

        int bitmaskA = getCollisionMask(layerA);
        int bitmaskB = getCollisionMask(layerB);

        if (collisionEnabled) {
            bitmaskA |= getLayerMask(layerB);
            bitmaskB |= getLayerMask(layerA);
        } else {
            bitmaskA &= ~getLayerMask(layerB);
            bitmaskB &= ~getLayerMask(layerA);
        }

        collisionMasks[layerIndex(layerA)] = bitmaskA;
        collisionMasks[layerIndex(layerB)] = bitmaskB;
    }

    public int addLayer(String name) {
        int bitIndex = -1;

        name = normalizeName(name);
        if (layers.containsKey(name)) {
            throw new IllegalArgumentException("Layer already exists: " + name);
        }

        for (int i = 0; i < MAX_LAYERS; i++) {
            if (!layers.containsValue(i)) {
                bitIndex = i;
                break;
            }
        }

        if (bitIndex != -1) {
            layers.put(name, bitIndex);
            return bitIndex;
        }

        throw new IllegalStateException("Maximum number of collision layers reached.");
    }

    public boolean layerExists(String name) {
        name = normalizeName(name);
        return layers.containsKey(name);
    }
}
