package frontier.game.ecs;

import frontier.engine.ecs.Entity;
import frontier.engine.ecs.components.Camera;
import frontier.engine.ecs.components.Component;
import frontier.engine.graphics.Transform;
import frontier.engine.input.Input;
import frontier.engine.input.Key;
import frontier.engine.scene.Scene;
import org.joml.Vector3f;

import java.util.Map;

public class ThirdPersonPlayerController extends Component {
    private float speed = 5f;
    public float sensitivity = 0.5f;

    private float distanceBehind = 15.0f;
    private float heightAbove = 5.0f;

    private Entity cameraPivot;
    private Camera camera;

    @Override
    public void initialize() {
        Scene scene = entity.getScene();
        camera = scene.getCamera();

        cameraPivot = new Entity("cameraPivot", scene);
        scene.addEntity(cameraPivot);

        cameraPivot.setParent(entity);
        cameraPivot.getTransform().position.set(0, heightAbove, 0);

        camera.getEntity().setParent(cameraPivot);
        camera.getEntity().getTransform().position.set(
                0,
                0,
                -distanceBehind
        );
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
        float mouseY = input.getMouse().getDeltaY();

        Transform cameraTransform = camera.getEntity().getTransform();

        cameraTransform.rotate(
                mouseY * sensitivity,
                0f,
                89f
        );

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
