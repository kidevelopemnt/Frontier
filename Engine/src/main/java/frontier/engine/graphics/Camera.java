package frontier.engine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f position = new Vector3f(0, 0, 3);
    private Vector3f rotation = new Vector3f(0, 0, 3);
    float limit = (float) Math.toRadians(89.0);

    public Matrix4f getViewMatrix() {
        return new Matrix4f()
            .rotateXYZ(rotation.x, rotation.y, rotation.z)
            .translate(-position.x, -position.y, -position.z)
        ;
    }

    public Matrix4f getProjectionMatrix(float aspectRatio) {
        return new Matrix4f().perspective(
            (float) Math.toRadians(60.0f),  // FOV
            aspectRatio,
            0.1f,  // near clipping plane
            100.0f  // Far clipping plane
        );
    }

    public Vector3f getForward() {
        float yaw = rotation.y;
        float pitch = rotation.x;

        return new Vector3f(
                (float) (-Math.sin(yaw) * Math.cos(pitch)),
                (float) Math.sin(pitch),
                (float) (-Math.cos(yaw) * Math.cos(pitch))
        ).normalize();
    }

    public Vector3f getRight() {
        Vector3f forward = getForward();

        return new Vector3f(
                forward.z,
                0,
                forward.x
        ).normalize();
    }

    // TODO: Up/down with Q and E

    public void moveForward(float amount) {
        position.add(getForward().mul(amount));
    }

    public void moveBackward(float amount) {
        position.sub(getForward().mul(amount));
    }

    public void moveRight(float amount) {
        position.add(
                getRight().mul(amount)
        );
    }

    public void moveLeft(float amount) {
        position.sub(
                getRight().mul(amount)
        );
    }

    public void moveUp(float amount) {
        position.y += amount;
    }

    public void moveDown(float amount) {
        position.y -= amount;
    }

    public void rotate(float pitch, float yaw) {
        rotation.x += pitch;
        rotation.y += yaw;

        rotation.x = Math.max(
                -limit,
                Math.min(limit, rotation.x)
        );
    }
}
