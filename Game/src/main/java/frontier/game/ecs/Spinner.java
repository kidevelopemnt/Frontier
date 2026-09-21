package frontier.game.ecs;

import frontier.engine.ecs.components.Component;

import java.util.Map;

public class Spinner extends Component {
    public float speed = 1f;

    @Override
    public void update(float deltaTime) {
        getEntity().getTransform().rotation.y += speed * deltaTime;
    }

    @Override
    public Map<String, Object> serialize() {
        return Map.of();
    }
}
