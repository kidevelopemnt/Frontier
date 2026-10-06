package frontier.engine.ecs.components;

import frontier.engine.graphics.Transform;
import frontier.engine.physics.Ray;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;

public class Camera extends Component {

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        return data;
    }

    /* public Matrix4f getViewMatrix() {
        return getEntity()
                .getTransform()
                .getWorldMatrix()
                .invert(new Matrix4f());
    } */

    public Matrix4f getProjectionMatrix(float aspectRatio) {
        return new Matrix4f().perspective(
            (float) Math.toRadians(60.0f),  // FOV
            aspectRatio,
            0.1f,  // near clipping plane
            100.0f  // Far clipping plane
        );
    }

    public Matrix4f getViewMatrix(float alpha) {
        Transform transform = getEntity().getTransform();

        CameraController controller =
                getEntity().getComponent(CameraController.class);

        Vector3f position;
        Vector3f rotation;

        if (controller != null) {
            position = controller.getRenderPosition(alpha);
            rotation = controller.getRenderRotation(alpha);
        } else {
            position = transform.getInterpolatedPosition(alpha);
            rotation = transform.getInterpolatedRotation(alpha);
        }

        Matrix4f cameraWorldMatrix = new Matrix4f();

        if (transform.getParent() != null) {
            cameraWorldMatrix.set(
                    transform.getParent().getWorldMatrix()
            );
        }

        cameraWorldMatrix
                .translate(position)
                .rotateYXZ(
                        (float) Math.toRadians(rotation.y),
                        (float) Math.toRadians(rotation.x),
                        0f
                )
                .scale(transform.scale);

        return cameraWorldMatrix.invert(new Matrix4f());
    }

    public Ray getRay(float mouseX, float mouseY,
                      float screenWidth, float screenHeight, float alpha) {

        // Convert mouse coordinates to normalized device coordinates.
        float x = (2.0f * mouseX) / screenWidth - 1.0f;
        float y = 1.0f - (2.0f * mouseY) / screenHeight;

        // Clip space -> view space.
        Vector4f clipCoords = new Vector4f(x, y, -1.0f, 1.0f);

        Matrix4f inverseProjection =
                getProjectionMatrix(screenWidth / screenHeight)
                        .invert(new Matrix4f());

        Vector4f viewCoords = new Vector4f(clipCoords);
        inverseProjection.transform(viewCoords);

        // Convert the near-plane position into a view-space direction.
        viewCoords.z = -1.0f;
        viewCoords.w = 0.0f;

        // View space -> world space.
        Matrix4f inverseView = getViewMatrix(alpha).invert(new Matrix4f());

        Vector4f worldCoords = new Vector4f(viewCoords);
        inverseView.transform(worldCoords);

        Vector3f direction = new Vector3f(
                worldCoords.x,
                worldCoords.y,
                worldCoords.z
        ).normalize();

        Vector3f origin;

        CameraController controller = entity.getComponent(CameraController.class);

        if (controller != null) {
            origin = controller.getRenderPosition(alpha);
        } else {
            origin = entity.getTransform().getInterpolatedPosition(alpha);
        }

        return new Ray(
                origin,
                direction,
                10000.0f
        );
    }

    public void move(float amount, Vector3f direction) {
        Vector3f velocity = direction.mul(amount);
        entity.getTransform().position.add(velocity);
    }

    public void moveForward(float amount) {
        getEntity().getTransform().position.add(entity.getTransform().getForward().mul(amount));
    }

    public void moveBackward(float amount) {
        getEntity().getTransform().position.sub(entity.getTransform().getForward().mul(amount));
    }

    public void moveRight(float amount) {
        entity.getTransform().position.add(
                entity.getTransform().getRight().mul(amount)
        );
    }

    public void moveLeft(float amount) {
        getEntity().getTransform().position.sub(
                entity.getTransform().getRight().mul(amount)
        );
    }

    public void moveUp(float amount) {
        getEntity().getTransform().position.add(entity.getTransform().getUp().mul(amount));
    }

    public void moveDown(float amount) {
        getEntity().getTransform().position.sub(entity.getTransform().getUp().mul(amount));
    }
}
