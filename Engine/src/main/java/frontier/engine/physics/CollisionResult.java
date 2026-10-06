package frontier.engine.physics;

import frontier.engine.ecs.components.physics.Collider;
import org.joml.Vector3f;

public class CollisionResult {
    private final Collider colliderA;
    private final Collider colliderB;
    private final Vector3f normal;  // Normal points of Collider A toward Collider B
    private final float penetration;

    public CollisionResult(
            Collider colliderA,
            Collider colliderB,
            Vector3f normal,
            float penetration
    ) {
        this.colliderA = colliderA;
        this.colliderB = colliderB;
        this.normal = normal;
        this.penetration = penetration;
    }

    public Collider getColliderA() {
        return colliderA;
    }

    public Collider getColliderB() {
        return colliderB;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public float getPenetration() {
        return penetration;
    }
}
