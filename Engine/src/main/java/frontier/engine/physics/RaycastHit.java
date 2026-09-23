package frontier.engine.physics;

import frontier.engine.ecs.Entity;
import org.joml.Vector3f;

public class RaycastHit {

    private final Entity entity;
    private final Vector3f location;
    private final Vector3f normal;
    private final float distance;

    public RaycastHit(
            Entity entity,
            Vector3f location,
            Vector3f normal,
            float distance
    ) {
        this.entity = entity;
        this.location = location;
        this.normal = normal;
        this.distance = distance;
    }

    public Entity getEntity() {
        return entity;
    }

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
