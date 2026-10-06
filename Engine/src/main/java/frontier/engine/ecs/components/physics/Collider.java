package frontier.engine.ecs.components.physics;

import frontier.engine.ecs.components.Component;
import frontier.engine.physics.RaycastHit;
import org.joml.Vector3f;

import java.util.Map;

public abstract class Collider extends Component {
    protected Vector3f size = new Vector3f(1, 1, 1);
    protected Vector3f center = new Vector3f(0, 0, 0);

    protected boolean isTrigger = false;

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

    public Vector3f getCenter() {
        return center;
    }

    public Vector3f getWorldSize() {
        return new Vector3f(size)
                .mul(entity.getTransform().scale);
    }

    @Override
    public Map<String, Object> serialize() {
        return Map.of();
    }
}
