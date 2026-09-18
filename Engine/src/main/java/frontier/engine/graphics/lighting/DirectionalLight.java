package frontier.engine.graphics.lighting;

import org.joml.Vector3f;

public class DirectionalLight extends Light {
    private Vector3f direction;

    public DirectionalLight(Vector3f direction, Vector3f color, float intensity) {
        super(color, intensity);
        this.direction = direction;
    }

    public DirectionalLight() {
        super(new Vector3f(255f, 255f, 255f), 1);
        this.direction = new Vector3f(1.0f, 0.0f, 0.0f);
    }

    public Vector3f getDirection() {
        return direction;
    }
}
