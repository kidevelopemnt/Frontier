package frontier.engine.ecs.components;

import frontier.engine.graphics.Transform;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TransformComponent extends Component {
    private Transform transform;

    public TransformComponent() {
        transform = new Transform();
    }

    public Matrix4f getMatrix() {
        return transform.getMatrix();
    }

    public void setPosition(Vector3f position) {
        transform.position = position;
    }

    public Vector3f getPosition() {
        return transform.position;
    }

    public void setRotation(Vector3f rotation) {
        transform.rotation = rotation;
    }

    public Vector3f getRotation() {
        return transform.rotation;
    }

    public void setScale(Vector3f scale) {
        transform.scale = scale;
    }

    public Vector3f getScale() {
        return transform.scale;
    }
}
