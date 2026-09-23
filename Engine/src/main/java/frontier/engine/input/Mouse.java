package frontier.engine.input;

import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class Mouse {
    private final long window;
    private GLFWInput glfwInput;

    private double x;
    private double y;

    private double deltaX;
    private double deltaY;
    private double lastX;
    private double lastY;

    private boolean firstTime = true;

    private boolean isCursorLocked = false;
    // TODO: Add option to replace cursor with icon (or show icon at center if cursorLocked)
    // Need to figure out where to do this

    public Mouse(long window, GLFWInput glfwInput) {
        this.window = window;
        this.glfwInput = glfwInput;
    }

    public void update() {
        double[] xBuffer = new double[1];
        double[] yBuffer = new double[1];

        GLFW.glfwGetCursorPos(window, xBuffer, yBuffer);

        x = xBuffer[0];
        y = yBuffer[0];

        if (firstTime) {
            lastX = x;
            lastY = y;
            firstTime = false;

            deltaX = 0;
            deltaY = 0;

            return;
        }

        deltaX = x - lastX;
        deltaY = y - lastY;

        lastX = x;
        lastY = y;
    }

    public float getX() { return (float) x; }
    public float getY() { return (float) y; }

    public float getDeltaX() {
        return (float) deltaX;
    }
    public float getDeltaY() {
        return (float) deltaY;
    }

    public float getScrollX() { return (float) glfwInput.getMouseScrollX(); }
    public float getScrollY() { return (float) glfwInput.getMouseScrollY(); }

    public boolean isCursorLocked() {
        return this.isCursorLocked;
    }

    public void setCursorLocked(boolean locked) {
        if (locked) {
            glfwInput.lockMouse();
        } else {
            glfwInput.unlockMouse();
        }
        isCursorLocked = locked;
    }

    public boolean isButtonDown(MouseButton button) {
        return glfwInput.isMouseButtonDown(button);
    }

    public boolean isButtonHeld(MouseButton button) {
        return glfwInput.isMouseButtonHeld(button);
    }

    public boolean isButtonUp(MouseButton button) {
        return glfwInput.isMouseButtonUp(button);
    }
}
