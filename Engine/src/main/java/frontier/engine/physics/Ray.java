package frontier.engine.physics;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Ray {
    private Vector3f origin;
    private Vector3f direction;
    private float distance;

    private List<String> groupFilter = new ArrayList<>();  // Filter by entity group

    public Ray(Vector3f origin, Vector3f direction, float distance) {
        this.origin = new Vector3f(origin);
        this.direction = new Vector3f(direction).normalize();
        this.distance = distance;
    }

    public void setGroupFilter(List<String> groups) {
        groupFilter = groups;
    }

    public Vector3f getOrigin() {
        return origin;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public float getDistance() {
        return distance;
    }
}
