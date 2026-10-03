package frontier.engine.physics;

import frontier.engine.ecs.Entity;
import frontier.engine.ecs.components.physics.Collider;
import org.joml.Vector3f;

public class RaycastHit {

    private final Entity entity;
    private final Collider collider;
    private final Vector3f location;
    private final Vector3f normal;
    private final float distance;

    public RaycastHit(
            Entity entity,
            Collider collider,
            Vector3f location,
            Vector3f normal,
            float distance
    ) {
        this.entity = entity;
        this.collider = collider;
        this.location = location;
        this.normal = normal;
        this.distance = distance;
    }

    public Entity getEntity() {
        return entity;
    }

    public Collider getCollider() { return collider; }

    public Vector3f getLocation() {
        return location;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public float getDistance() {
        return distance;
    }
}
