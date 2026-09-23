package frontier.engine.input;

import frontier.engine.application.Window;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.InvocationTargetException;

public class Input {
    private final Window window;
    private final ActionRegistry actionRegistry;

    private final GLFWInput glfwInput;
    private final Mouse mouse;

    public Input(Window window) {
        this.window = window;
        actionRegistry = new ActionRegistry(this);
        glfwInput = new GLFWInput(window.getHandle());
        mouse = new Mouse(window.getHandle(), glfwInput);
    }

    public void update() {
        glfwInput.update();
        mouse.update();

        if (mouse.isButtonDown(MouseButton.LEFT)) {
            // Check if any objects with the Clickable component were clicked
        }
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
