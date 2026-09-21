package frontier.engine.ecs.components;

import frontier.engine.ecs.Entity;
import frontier.engine.graphics.lighting.DirectionalLight;
import frontier.engine.graphics.lighting.Light;
import frontier.engine.graphics.lighting.LightType;
import frontier.engine.graphics.lighting.PointLight;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LightComponent extends Component {

    private LightType lightType;
    private Vector3f color;
    private float intensity;

    public LightComponent() {
        lightType = LightType.AMBIENT;
        color = new Vector3f(1, 1, 1);
        intensity = 1.0f;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("lightType", lightType.toString());
        data.put("color", List.of(color.x, color.y, color.z));
        data.put("intensity", intensity);

        return data;
    }

    @Override
    public void load(Map<String, Object> data) {
        lightType = LightType.valueOf((String) data.get("lightType"));
        List<Double> colorData = (List<Double>) data.get("color");
        color = new Vector3f(colorData.get(0).floatValue(), colorData.get(1).floatValue(), colorData.get(2).floatValue());
        intensity = ((Double) data.get("intensity")).floatValue();
    }

    public LightType getLightType() {
        return lightType;
    }

    public void setLightType(LightType lightType) {
        this.lightType = lightType;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public Vector3f getColorWithIntensity() {
        return new Vector3f(color).mul(intensity);
    }
}
