package frontier.game.ecs;

import frontier.engine.ecs.components.Component;
import frontier.engine.input.Input;
import frontier.engine.input.Key;
import org.joml.Vector3f;

import java.util.Map;

public class PlayerController extends Component {
    private float speed = 5f;
    private float sensitivity = 0.1f;

    @Override
    public void initialize() {

    }

    @Override
    public void update(float deltaTime) {
        Input input = entity.getScene().getEngine().getInput();

        if (input.isKeyHeld(Key.W)) {
            move(
                    speed * deltaTime,
                    entity.getTransform().getForward()
            );
        }

        if (input.isKeyHeld(Key.S)) {
            move(
                    speed * deltaTime,
                    entity.getTransform().getForward().negate()
            );
        }

        if (input.isKeyHeld(Key.A)) {
            move(
                    speed * deltaTime,
                    entity.getTransform().getRight().negate()
            );
        }

        if (input.isKeyHeld(Key.D)) {
            move(
                    speed * deltaTime,
                    entity.getTransform().getRight()
            );
        }

        float mouseX = input.getMouse().getDeltaX();

        entity.getTransform().rotate(
                0f,
                mouseX * sensitivity
        );
    }

    private void move(float amount, Vector3f direction) {
        Vector3f velocity = direction.mul(amount);
        entity.getTransform().position.add(velocity);
    }

    @Override
    public Map<String, Object> serialize() {
        return Map.of("speed", speed);
    }
}
