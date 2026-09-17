package frontier.engine.application;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

public class Window {
    private long window;

    private String title;

    private int[] savedX = new int[1];
    private int[] savedY = new int[1];
    private int[] savedWidth = new int[1];
    private int[] savedHeight = new int[1];

    private int counter = 0;

    public Window() {
        this.title = "";
    }

    public void create() {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Failed to initialize GLFW.");
        }

        GLFW.glfwDefaultWindowHints();

        window = GLFW.glfwCreateWindow(
                1280,
                720,
                title,
                0,
                0
        );

        saveSizeAndPosition();

        if (window == 0) {
            GLFW.glfwTerminate();
            throw new IllegalStateException("Failed to create window.");
        }

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        GL.createCapabilities();

        // TODO: Move this to be a toggleable show/hide mouse pointer, and add helper method
        GLFW.glfwSetInputMode(
                window,
                GLFW.GLFW_CURSOR,
                GLFW.GLFW_CURSOR_DISABLED
        );
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(window);
    }

    public void update() {
        GLFW.glfwPollEvents();

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == 1) {

        }
    }

    public void draw() {
        GLFW.glfwSwapBuffers(window);
    }

    public void setFullscreen() {
        long monitor = GLFW.glfwGetPrimaryMonitor();
        GLFWVidMode videoMode = GLFW.glfwGetVideoMode(monitor);

        saveSizeAndPosition();

        if (videoMode != null) {
            GLFW.glfwSetWindowMonitor(window, monitor, 0, 0, videoMode.width(), videoMode.height(), videoMode.refreshRate());
        }
    }

    public void setWindowed() {
        GLFW.glfwSetWindowMonitor(window, 0, savedX[0], savedY[0], savedWidth[0], savedHeight[0], GLFW.GLFW_DONT_CARE);
    }

    public void destroy() {
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    private void saveSizeAndPosition() {
        GLFW.glfwGetWindowPos(window, savedX, savedY);
        GLFW.glfwGetWindowSize(window, savedWidth, savedHeight);
    }

    public Vector2f getSize() {
        saveSizeAndPosition();
        return new Vector2f(savedWidth[0], savedHeight[0]);
    }

    public long getHandle() {
        return window;
    }
}
