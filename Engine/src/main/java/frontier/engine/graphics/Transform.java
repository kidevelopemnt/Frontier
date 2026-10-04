package frontier.engine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform {
    public Vector3f position = new Vector3f(0, 0, 0);
    public Vector3f rotation = new Vector3f(0, 0, 0);
    public Vector3f scale = new Vector3f(1, 1, 1);

    private Transform parent;

    public void setParent(Transform parent) {
        this.parent = parent;
    }

    public Transform getParent() {
        return parent;
    }

    public Matrix4f getLocalMatrix() {
        return new Matrix4f()
                .translate(position)
                .rotateXYZ(
                        (float) Math.toRadians(rotation.x),
                        (float) Math.toRadians(rotation.y),
                        (float) Math.toRadians(rotation.z)
                )
                .scale(scale);
    }

    public Matrix4f getWorldMatrix() {
        if (parent == null) {
            return getLocalMatrix();
        }

        return new Matrix4f(parent.getWorldMatrix())
                .mul(getLocalMatrix());
    }

    public Vector3f getWorldPosition() {
        return getWorldMatrix()
                .getTranslation(new Vector3f());
    }

    public Vector3f getWorldScale() {
        return getWorldMatrix()
                .getScale(new Vector3f());
    }

    public void setWorldPosition(Vector3f pos) {
        if (getParent() != null) {
            // Transform the target world position into the parent's local space
            Matrix4f parentInverse = getParent().getWorldMatrix().invert(new Matrix4f());
            position = parentInverse.transformPosition(pos, new Vector3f());
        } else {
            position = pos;
        }
    }

    public Vector3f getForward() {
        Vector3f forward = new Vector3f(0, 0, -1);

        getWorldMatrix().transformDirection(forward);

        return forward.normalize();
    }

    public Vector3f getRight() {
        Vector3f right = new Vector3f(1, 0, 0);

        getWorldMatrix().transformDirection(right);

        return right.normalize();
    }

    public Vector3f getUp() {
        Vector3f up = new Vector3f(0, 1, 0);

        getWorldMatrix().transformDirection(up);

        return up.normalize();
    }

    public void rotate(float pitch, float yaw) {
        rotation.x -= pitch;
        rotation.y -= yaw;
    }

    public void rotate(float pitch, float yaw, float pitchLimit) {
        rotate(pitch, yaw);

        rotation.x = Math.max(
                -pitchLimit,
                Math.min(pitchLimit, rotation.x)
        );
    }

    public void lookAt(Vector3f target) {
        Vector3f direction = target.sub(getWorldPosition(), new Vector3f()).normalize();

        float yaw = (float) Math.toDegrees(
                Math.atan2(-direction.x, -direction.z)
        );

        float pitch = (float) Math.toDegrees(
                Math.asin(direction.y)
        );

        rotation.x = pitch;
        rotation.y = yaw;
        rotation.z = 0f;
    }
}