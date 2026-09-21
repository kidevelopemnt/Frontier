package frontier.engine.input;

import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class GLFWInput {
    private final long window;
    private final Map<Key, Integer> keyMapping = new HashMap<>();

    private final Map<Key, Boolean> currentState = new HashMap<>();
    private final Map<Key, Boolean> previousState = new HashMap<>();

    public GLFWInput(long window) {
        this.window = window;

        initializeKeyMapping();
        for (Key key : Key.values()) {
            currentState.put(key, false);
            previousState.put(key, false);
        }
    }

    private void initializeKeyMapping() {
        keyMapping.put(Key.A, GLFW.GLFW_KEY_A);
        keyMapping.put(Key.B, GLFW.GLFW_KEY_B);
        keyMapping.put(Key.C, GLFW.GLFW_KEY_C);
        keyMapping.put(Key.D, GLFW.GLFW_KEY_D);
        keyMapping.put(Key.E, GLFW.GLFW_KEY_E);
        keyMapping.put(Key.F, GLFW.GLFW_KEY_F);
        keyMapping.put(Key.G, GLFW.GLFW_KEY_G);
        keyMapping.put(Key.H, GLFW.GLFW_KEY_H);
        keyMapping.put(Key.I, GLFW.GLFW_KEY_I);
        keyMapping.put(Key.J, GLFW.GLFW_KEY_J);
        keyMapping.put(Key.K, GLFW.GLFW_KEY_K);
        keyMapping.put(Key.L, GLFW.GLFW_KEY_L);
        keyMapping.put(Key.M, GLFW.GLFW_KEY_M);
        keyMapping.put(Key.N, GLFW.GLFW_KEY_N);
        keyMapping.put(Key.O, GLFW.GLFW_KEY_O);
        keyMapping.put(Key.P, GLFW.GLFW_KEY_P);
        keyMapping.put(Key.Q, GLFW.GLFW_KEY_Q);
        keyMapping.put(Key.R, GLFW.GLFW_KEY_R);
        keyMapping.put(Key.S, GLFW.GLFW_KEY_S);
        keyMapping.put(Key.T, GLFW.GLFW_KEY_T);
        keyMapping.put(Key.U, GLFW.GLFW_KEY_U);
        keyMapping.put(Key.V, GLFW.GLFW_KEY_V);
        keyMapping.put(Key.W, GLFW.GLFW_KEY_W);
        keyMapping.put(Key.X, GLFW.GLFW_KEY_X);
        keyMapping.put(Key.Y, GLFW.GLFW_KEY_Y);
        keyMapping.put(Key.Z, GLFW.GLFW_KEY_Z);

        keyMapping.put(Key.SPACE, GLFW.GLFW_KEY_SPACE);
        keyMapping.put(Key.LEFT_SHIFT, GLFW.GLFW_KEY_LEFT_SHIFT);
        keyMapping.put(Key.LEFT_CTRL, GLFW.GLFW_KEY_LEFT_CONTROL);
        keyMapping.put(Key.LEFT_ALT, GLFW.GLFW_KEY_LEFT_ALT);
        keyMapping.put(Key.RIGHT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT);
        keyMapping.put(Key.RIGHT_CTRL, GLFW.GLFW_KEY_RIGHT_CONTROL);
        keyMapping.put(Key.RIGHT_ALT, GLFW.GLFW_KEY_RIGHT_ALT);

        keyMapping.put(Key.ESCAPE, GLFW.GLFW_KEY_ESCAPE);

        keyMapping.put(Key.UP, GLFW.GLFW_KEY_UP);
        keyMapping.put(Key.DOWN, GLFW.GLFW_KEY_DOWN);
        keyMapping.put(Key.LEFT, GLFW.GLFW_KEY_LEFT);
        keyMapping.put(Key.RIGHT, GLFW.GLFW_KEY_RIGHT);
    }

    public void update() {
        for (Key key : Key.values()) {
            previousState.put(key, currentState.get(key));

            int glfwKey = keyMapping.get(key);

            boolean pressed = GLFW.glfwGetKey(window, glfwKey) == GLFW.GLFW_PRESS;

            currentState.put(key, pressed);
        }
    }

    public boolean isKeyHeld(Key key) {
        return currentState.get(key);
    }

    public boolean isKeyDown(Key key) {
        return currentState.get(key) && !previousState.get(key);
    }

    public boolean isKeyUp(Key key) {
        return !currentState.get(key) && previousState.get(key);
    }
}
