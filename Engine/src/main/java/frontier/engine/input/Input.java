package frontier.engine.input;

import frontier.engine.application.Window;
import org.lwjgl.glfw.GLFW;

public class Input {
    private Window window;

    private double mouseX;
    private double mouseY;

    private double mouseDeltaX;
    private double mouseDeltaY;
    private double lastMouseX;
    private double lastMouseY;

    private boolean firstMouse = true;

    public Input(Window window) {
        this.window = window;
    }

    public boolean isKeyDown(int key) {
        return GLFW.glfwGetKey(window.getHandle(), key) == GLFW.GLFW_PRESS;
    }

    public void update() {
        double[] x = new double[1];
        double[] y = new double[1];

        GLFW.glfwGetCursorPos(window.getHandle(), x, y);

        mouseX = x[0];
        mouseY = y[0];

        if (firstMouse) {
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            firstMouse = false;

            mouseDeltaX = 0;
            mouseDeltaY = 0;

            return;
        }

        mouseDeltaX = mouseX - lastMouseX;
        mouseDeltaY = mouseY - lastMouseY;

        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    public float getMouseDeltaX() {
        return (float) mouseDeltaX;
    }

    public float getMouseDeltaY() {
        return (float) mouseDeltaY;
    }

    public void endFrame() {

    }
}
