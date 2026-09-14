package frontier.engine.graphics;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;

public class Renderer {
    private VertexBuffer vertexBuffer;
    private VertexArray vertexArray;
    private Shader shader;
    private Transform transform;

    public void initialize() {
        float[] vertices = {
            0.5f,  0.5f,
            -0.5f, -0.5f,
            0.5f, -0.5f
        };

        transform = new Transform();

        shader = new Shader("shaders/basic.vert", "shaders/basic.frag");
        vertexBuffer = new VertexBuffer(vertices);
        vertexArray = new VertexArray();
        vertexArray.addVertexBuffer(vertexBuffer);
    }

    public void beginFrame() {
        clear();
        // transform.position.set(transform.position.x + 0.01f, 0.0f, 0.0f);
    }

    public void render() {
        vertexArray.bind();
        shader.bind();

        shader.setMatrix4f("model", transform.getMatrix());

        GL11.glDrawArrays(
            GL11.GL_TRIANGLES,
            0,
            3
        );
    }

    public void endFrame() {

    }

    public void shutdown() {
        shader.delete();
        vertexBuffer.delete();
        vertexArray.delete();
    }

    public void clear() {
        GL11.glClear(
                GL11.GL_COLOR_BUFFER_BIT |
                        GL11.GL_DEPTH_BUFFER_BIT
        );
    }

    public void setFillColor(
            float r,
            float g,
            float b,
            float a
    ) {
        GL11.glClearColor(r, g, b, a);
    }
}
