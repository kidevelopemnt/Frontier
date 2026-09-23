package frontier.engine.physics;

import frontier.engine.ecs.Entity;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Raycast {
    private Vector3f origin;
    private Vector3f direction;
    private double distance;

    private List<String> groupFilter = new ArrayList<>();  // Filter by entity group

    public class Hit {
        private Entity entity;
        private Vector3f location;
        private float distance;

        public Hit(Entity entity, Vector3f location, float distance) {
            this.entity = entity;
            this.location = location;
            this.distance = distance;
        }
    }

    public Raycast(Vector3f origin, Vector3f direction, double distance) {
        this.origin = origin;
        this.direction = direction;
        this.distance = distance;
    }

    public void setGroupFilter(List<String> groups) {
        groupFilter = groups;
    }

    public Hit cast() {
        // TODO: Raycast!

        Hit hit = new Hit(null, new Vector3f(0f, 0f, 0f), 0);
        return hit;
    }
}
