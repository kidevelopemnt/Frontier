package frontier.engine.ecs.components;

import frontier.engine.Engine;
import frontier.engine.input.Key;

import java.util.Map;

public class CameraController extends Component {
    public float cameraSpeed = 5.0f;
    public float sensitivity = 0.0025f;

    public void update(float deltaTime) {
        Engine engine = getEngine();

        if (engine.getInput().isKeyHeld(Key.W)) {
            engine.getCamera().moveForward(cameraSpeed * deltaTime);
        }
        if (engine.getInput().isKeyHeld(Key.A)) {
            engine.getCamera().moveLeft(cameraSpeed * deltaTime);
        }
        if (engine.getInput().isKeyHeld(Key.S)) {
            engine.getCamera().moveBackward(cameraSpeed * deltaTime);
        }
        if (engine.getInput().isKeyHeld(Key.D)) {
            engine.getCamera().moveRight(cameraSpeed * deltaTime);
        }
        if (engine.getInput().isKeyHeld(Key.Q)) {
            engine.getCamera().moveDown(cameraSpeed * deltaTime);
        }
        if (engine.getInput().isKeyHeld(Key.E)) {
            engine.getCamera().moveUp(cameraSpeed * deltaTime);
        }

        float mouseX = engine.getInput().getMouse().getDeltaX();
        float mouseY = engine.getInput().getMouse().getDeltaY();
        engine.getCamera().rotate(
                mouseY * sensitivity,
                mouseX * sensitivity
        );
    }

    @Override
    public Map<String, Object> serialize() {
        return Map.of("cameraSpeed", cameraSpeed, "sensitivity", sensitivity);
    }
}
