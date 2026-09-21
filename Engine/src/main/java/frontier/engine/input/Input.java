package frontier.engine.input;

import frontier.engine.application.Window;
import org.lwjgl.glfw.GLFW;

public class Input {
    private final Window window;

    private final GLFWInput glfwInput;
    private final Mouse mouse;

    public Input(Window window) {
        this.window = window;
        glfwInput = new GLFWInput(window.getHandle());
        mouse = new Mouse(window.getHandle(), glfwInput);
    }

    public void update() {
        glfwInput.update();
        mouse.update();
    }

    public void endFrame() {
        glfwInput.endFrame();
    }

    public boolean isKeyPressed(Key key) {
        return glfwInput.isKeyPressed(key);
    }

    public boolean isKeyHeld(Key key) {
        return glfwInput.isKeyHeld(key);
    }

    public boolean isKeyReleased(Key key) {
        return glfwInput.isKeyReleased(key);
    }

    public Mouse getMouse() {
        return mouse;
    }
}
