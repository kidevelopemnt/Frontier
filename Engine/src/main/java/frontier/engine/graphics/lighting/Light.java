package frontier.engine.graphics.lighting;

import org.joml.Vector3f;

public abstract class Light {
    private Vector3f color;
    private float intensity;

    public Light(Vector3f color, float intensity) {
        this.color = color;
        this.intensity = intensity;
    }

    public Vector3f getColor() {
        return color;
    }

    public Vector3f getColorWithIntensity() {
        return new Vector3f(color).mul(intensity);
    }

    public float getIntensity() {
        return intensity;
    }
}
