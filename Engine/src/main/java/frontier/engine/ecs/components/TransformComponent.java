package frontier.engine.ecs.components;

import frontier.engine.graphics.Transform;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransformComponent extends Component {
    private Transform transform;

    public TransformComponent() {
        transform = new Transform();
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("position", List.of(
                transform.position.x,
                transform.position.y,
                transform.position.z
        ));

        data.put("rotation", List.of(
                transform.rotation.x,
                transform.rotation.y,
                transform.rotation.z
        ));

        data.put("scale", List.of(
                transform.scale.x,
                transform.scale.y,
                transform.scale.z
        ));

        return data;
    }

    @Override
    public void load(Map<String, Object> data) {
        ArrayList<Double> position = (ArrayList<Double>) data.get("position");
        transform.position = new Vector3f(position.get(0).floatValue(), position.get(1).floatValue(), position.get(2).floatValue());
        ArrayList<Double> rotation = (ArrayList<Double>) data.get("rotation");
        transform.rotation = new Vector3f(rotation.get(0).floatValue(), rotation.get(1).floatValue(), rotation.get(2).floatValue());
        ArrayList<Double> scale = (ArrayList<Double>) data.get("scale");
        transform.scale = new Vector3f(scale.get(0).floatValue(), scale.get(1).floatValue(), scale.get(2).floatValue());
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

    public Transform getTransform() {
        return transform;
    }
}
