package frontier.engine.graphics.lighting;

import org.joml.Vector3f;

public class PointLight extends Light {
    private Vector3f position;

    public PointLight(Vector3f position, Vector3f color, float intensity) {
        super(color, intensity);
        this.position = position;
    }

    public Vector3f getPosition() {
        return position;
    }
}
