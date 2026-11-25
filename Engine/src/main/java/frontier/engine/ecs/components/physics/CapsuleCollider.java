package frontier.engine.ecs.components.physics;

import frontier.engine.physics.RaycastHit;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class CapsuleCollider extends Collider {
    private float radius = 0.5f;
    private float height = 2f;

    public float getRadius() { return radius; }
    public void setRadius(float radius) { this.radius = radius; }

    public float getHeight() { return height; }
    public void setHeight(float height) {
        if (height < radius * 2) {
            throw new IllegalArgumentException("Capsule height must be more than 2*radius.");
        }

        this.height = height;
    }

    public float getWorldRadius() {
        Vector3f worldScale = entity.getTransform().getWorldScale();
        float maxXZScale = Math.max(Math.abs(worldScale.x), Math.abs(worldScale.z));

        return radius * maxXZScale;
    }
    public Vector3f getSegmentStart() {
        float halfSegment = Math.max(0.0f, (height * 0.5f) - radius);
        Vector3f localStart = new Vector3f(center.x, center.y - halfSegment, center.z);

        Matrix4f worldMatrix = entity.getTransform().getWorldMatrix();
        return worldMatrix.transformPosition(localStart);
    }

    public Vector3f getSegmentEnd() {
        float halfSegment = Math.max(0.0f, (height * 0.5f) - radius);
        Vector3f localEnd = new Vector3f(center.x, center.y + halfSegment, center.z);

        Matrix4f worldMatrix = entity.getTransform().getWorldMatrix();
        return worldMatrix.transformPosition(localEnd);
    }

    @Override
    public RaycastHit raycast(Vector3f origin, Vector3f direction, float maxDistance) {
        return null;
    }
}
