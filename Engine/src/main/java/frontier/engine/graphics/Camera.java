package frontier.engine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f position = new Vector3f(0, 0, 3);;
    private Vector3f rotation = new Vector3f(0, 0, 3);;

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
}
