package frontier.engine.graphics.lighting;

import org.joml.Vector3f;

public class PointLight extends Light {
    private Vector3f position;

    public PointLight(Vector3f position, Vector3f color, float intensity) {
        super(color, intensity);
        this.position = position;
    }

    public PointLight() {
        super(new Vector3f(255f, 255f, 255f), 1);
        this.position = new Vector3f(0.0f, 0.0f, 0.0f);
    }

    public Vector3f getPosition() {
        return position;
    }
}
