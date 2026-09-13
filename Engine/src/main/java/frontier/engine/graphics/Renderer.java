package frontier.engine.graphics;

import org.lwjgl.opengl.GL11;

public class Renderer {
    public void initialize() {

    }

    public void clear() {
        GL11.glClear(
                GL11.GL_COLOR_BUFFER_BIT |
                        GL11.GL_DEPTH_BUFFER_BIT
        );
    }

    public void shutdown() {

    }

    public void fillColor(float r, float g, float b, float a) {
        GL11.glClearColor(
                r,
                g,
                b,
                a
        );
    }
}
