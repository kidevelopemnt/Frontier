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

        // Handle looking straight up/down to prevent movement from breaking (Gimbal Lock)
        if (Math.abs(forward.y) > 0.99f) {
            float yaw = rotation.y;
            return new Vector3f((float) -Math.cos(yaw), 0, (float) Math.sin(yaw)).normalize();
        }

        // Global reference up vector
        Vector3f globalUp = new Vector3f(0, 1, 0);

        // Cross Product: Right = GlobalUp x Forward (gives the correct unflipped Right)
        return new Vector3f(
                globalUp.y * forward.z - globalUp.z * forward.y,
                globalUp.z * forward.x - globalUp.x * forward.z,
                globalUp.x * forward.y - globalUp.y * forward.x
        ).normalize();
    }

    public Vector3f getUp() {
        Vector3f forward = getForward();
        Vector3f right = getRight();

        // Cross Product: Up = Forward x Right
        // This gives you the true tilted up vector relative to where you look
        return new Vector3f(
                right.y * forward.z - right.z * forward.y,
                right.z * forward.x - right.x * forward.z,
                right.x * forward.y - right.y * forward.x
        ).normalize();
    }

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
        position.add(getUp().mul(amount));
    }

    public void moveDown(float amount) {
        position.sub(getUp().mul(amount));
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
