package frontier.engine.ecs.components.physics;

import frontier.engine.physics.RaycastHit;
import org.joml.Vector3f;

public class SphereCollider extends Collider {

    private float radius = 0.5f;

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        if (radius <= 0.0f) {
            throw new IllegalArgumentException(
                    "Sphere radius must be greater than zero."
            );
        }

        this.radius = radius;
    }

    public float getWorldRadius() {
        Vector3f scale = entity.getTransform().getWorldScale();

        float maxScale = Math.max(
                Math.abs(scale.x),
                Math.max(
                        Math.abs(scale.y),
                        Math.abs(scale.z)
                )
        );

        return radius * maxScale;
    }

    public Vector3f getWorldCenter() {
        return new Vector3f(
                entity.getTransform().getWorldPosition()
        ).add(center);
    }

    @Override
    public RaycastHit raycast(
            Vector3f origin,
            Vector3f direction,
            float maxDistance
    ) {
        // TODO: Sphere raycasting
        return null;
    }
}

