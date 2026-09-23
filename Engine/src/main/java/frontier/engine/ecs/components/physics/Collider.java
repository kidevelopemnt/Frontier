package frontier.engine.ecs.components.physics;

import frontier.engine.ecs.components.Component;
import frontier.engine.physics.RaycastHit;
import org.joml.Vector3f;

import java.util.Map;

public abstract class Collider extends Component {
    private boolean isTrigger = false;

    public abstract RaycastHit raycast(
        Vector3f origin,
        Vector3f direction,
        float maxDistance
    );

    public boolean isTrigger() {
        return isTrigger;
    }

    public void setTrigger(boolean trigger) {
        isTrigger = trigger;
    }

    @Override
    public Map<String, Object> serialize() {
        return Map.of();
    }
}
