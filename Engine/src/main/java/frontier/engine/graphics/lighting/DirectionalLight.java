package frontier.engine.graphics.lighting;

import org.joml.Vector3f;

public class DirectionalLight {
    private Vector3f direction;
    private Vector3f color;
    private float intensity;

    public DirectionalLight(Vector3f direction, Vector3f color, float intensity) {
        this.direction = direction;
        this.color = color;
        this.intensity = intensity;
    }

    public Vector3f getDirection() {
        return direction;
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
