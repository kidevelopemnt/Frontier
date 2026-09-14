package frontier.engine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform {
    public Vector3f position = new Vector3f(0, 0, 0);
    public Vector3f rotation = new Vector3f(0, 0, 0);
    public Vector3f scale = new Vector3f(1, 1, 1);

    public Matrix4f getMatrix() {
        return new Matrix4f()
            .translate(position)
            .rotateXYZ(rotation.x, rotation.y, rotation.z)
            .scale(scale)
        ;
    }
}
