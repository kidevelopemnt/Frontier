package frontier.engine.ecs.components;

import frontier.engine.graphics.Transform;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class Camera extends Component {
    float limit = (float) Math.toRadians(89.0);

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        return data;
    }

    public Matrix4f getViewMatrix() {
        Transform transform = getEntity().getTransform();

        Vector3f position = transform.position;
        Vector3f rotation = transform.rotation;

        return new Matrix4f()
                .rotateXYZ(
                        -rotation.x,
                        -rotation.y,
                        -rotation.z
                )
                .translate(
                        -position.x,
                        -position.y,
                        -position.z
                );
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
        Transform transform = getEntity().getTransform();
        Vector3f rotation = transform.rotation;

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
        Vector3f globalUp = new Vector3f(0, 1, 0);

        return forward.cross(globalUp).normalize();
    }

    public Vector3f getUp() {
        Vector3f forward = getForward();
        Vector3f right = getRight();

        return right.cross(forward).normalize();
    }

    public void moveForward(float amount) {
        getEntity().getTransform().position.add(getForward().mul(amount));
    }

    public void moveBackward(float amount) {
        getEntity().getTransform().position.sub(getForward().mul(amount));
    }

    public void moveRight(float amount) {
        getEntity().getTransform().position.add(
                getRight().mul(amount)
        );
    }

    public void moveLeft(float amount) {
        getEntity().getTransform().position.sub(
                getRight().mul(amount)
        );
    }

    public void moveUp(float amount) {
        getEntity().getTransform().position.add(getUp().mul(amount));
    }

    public void moveDown(float amount) {
        getEntity().getTransform().position.sub(getUp().mul(amount));
    }

    public void rotate(float pitch, float yaw) {
        Transform transform = getEntity().getTransform();
        Vector3f rotation = transform.rotation;

        rotation.x -= pitch;
        rotation.y -= yaw;

        rotation.x = Math.max(
                -limit,
                Math.min(limit, rotation.x)
        );
    }
}
