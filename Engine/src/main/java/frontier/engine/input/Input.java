package frontier.engine.input;

import frontier.engine.Engine;
import frontier.engine.application.Window;
import frontier.engine.ecs.components.Camera;
import frontier.engine.events.TriggerEnteredEvent;
import frontier.engine.graphics.DebugRenderer;
import frontier.engine.physics.Ray;
import frontier.engine.physics.RaycastHit;
import org.joml.Vector3f;

public class Input {
    private final Engine engine;
    private final Window window;
    private final ActionRegistry actionRegistry;

    private final GLFWInput glfwInput;
    private final Mouse mouse;

    public Input(Engine engine, Window window) {
        this.engine = engine;
        this.window = window;
        actionRegistry = new ActionRegistry(this);
        glfwInput = new GLFWInput(window.getHandle());
        mouse = new Mouse(window, glfwInput);
    }

    public void update() {
        glfwInput.update();
        mouse.update();

        if (mouse.isButtonDown(MouseButton.LEFT) && engine.getActiveScene() != null) {
            Ray ray = getRay();

            DebugRenderer.drawRay(
                    ray.getOrigin(),
                    ray.getDirection(),
                    ray.getDistance()
            );

            RaycastHit hit = engine.getPhysics().raycast(ray);
            if (hit != null) {
                engine.getEventBus().trigger(
                        hit.getCollider(),
                        TriggerEnteredEvent.class
                );
            }
        }
    }

    private Ray getRay() {
        Camera camera = engine.getActiveScene().getCamera();

        float rayX;
        float rayY;

        if (mouse.isCursorLocked()) {
            rayX = window.getSize().x / 2.0f;
            rayY = window.getSize().y / 2.0f;
        } else {
            rayX = mouse.getX();
            rayY = mouse.getY();
        }

        Ray ray = camera.getRay(
                rayX,
                rayY,
                window.getSize().x,
                window.getSize().y
        );
        return ray;
    }

    public void endFrame() {
        glfwInput.endFrame();
    }

    public void registerAction(String name, InputAction action) {
        actionRegistry.register(name, action);
    }

    public boolean isDown(String actionName) {
        return actionRegistry.isDown(actionName);
    }

    public boolean isHeld(String actionName) {
        return actionRegistry.isHeld(actionName);
    }

    public boolean isUp(String actionName) {
        return actionRegistry.isUp(actionName);
    }

    public boolean isKeyDown(Key key) {
        return glfwInput.isKeyDown(key);
    }

    public boolean isKeyHeld(Key key) {
        return glfwInput.isKeyHeld(key);
    }

    public boolean isKeyUp(Key key) {
        return glfwInput.isKeyUp(key);
    }

    public Mouse getMouse() {
        return mouse;
    }
}
