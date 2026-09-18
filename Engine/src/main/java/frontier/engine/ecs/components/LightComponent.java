package frontier.engine.ecs.components;

import frontier.engine.ecs.Entity;
import frontier.engine.graphics.lighting.DirectionalLight;
import frontier.engine.graphics.lighting.Light;
import frontier.engine.graphics.lighting.LightType;
import frontier.engine.graphics.lighting.PointLight;
import org.joml.Vector3f;

public class LightComponent extends Component {

    private LightType lightType;
    private Vector3f color;
    private float intensity;

    public LightComponent() {
        lightType = LightType.AMBIENT;
        color = new Vector3f(1, 1, 1);
        intensity = 1.0f;
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
