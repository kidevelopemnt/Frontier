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

    public Transform getTransform() {
        return transform;
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
    @SuppressWarnings("unchecked")
    public void load(Map<String, Object> data) {
        ArrayList<Double> position = (ArrayList<Double>) data.get("position");
        transform.position = new Vector3f(position.get(0).floatValue(), position.get(1).floatValue(), position.get(2).floatValue());
        ArrayList<Double> rotation = (ArrayList<Double>) data.get("rotation");
        transform.rotation = new Vector3f(rotation.get(0).floatValue(), rotation.get(1).floatValue(), rotation.get(2).floatValue());
        ArrayList<Double> scale = (ArrayList<Double>) data.get("scale");
        transform.scale = new Vector3f(scale.get(0).floatValue(), scale.get(1).floatValue(), scale.get(2).floatValue());
    }
}
