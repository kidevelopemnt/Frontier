package frontier.engine.physics;

import frontier.engine.ecs.components.physics.Collider;


public final class CollisionPair {
    private final Collider colliderA;
    private final Collider colliderB;

    public CollisionPair(Collider colliderA, Collider colliderB) {
        if (colliderA == null || colliderB == null) {
            throw new IllegalArgumentException("Colliders cannot be null.");
        }

        this.colliderA = colliderA;
        this.colliderB = colliderB;
    }

    public Collider getColliderA() {
        return colliderA;
    }

    public Collider getColliderB() {
        return colliderB;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof CollisionPair other)) {
            return false;
        }

        return (colliderA == other.colliderA && colliderB == other.colliderB)
                || (colliderA == other.colliderB && colliderB == other.colliderA);
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(colliderA)
                + System.identityHashCode(colliderB);
    }
}
