package frontier.engine.graphics;

import frontier.engine.Engine;
import frontier.engine.application.Window;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;

public class Renderer {
    private Engine engine;
    private Camera camera;

    private Mesh mesh;
    private Shader shader;
    private Transform transform;

    public void initialize(Engine engine) {
        this.engine = engine;
        camera = new Camera();
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        float[] vertices = {
                // Front
                -0.5f, -0.5f,  0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,

                // Back
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                -0.5f,  0.5f, -0.5f
        };

        int[] indices = {
                // Front
                0, 1, 2,
                0, 2, 3,

                // Back
                5, 4, 7,
                5, 7, 6,

                // Left
                4, 0, 3,
                4, 3, 7,

                // Right
                1, 5, 6,
                1, 6, 2,

                // Top
                3, 2, 6,
                3, 6, 7,

                // Bottom
                4, 5, 1,
                4, 1, 0
        };

        transform = new Transform();
        transform.position.z = 0;
        transform.rotation.y = (float) Math.toRadians(30);
        transform.rotation.x = (float) Math.toRadians(20);

        shader = new Shader("shaders/basic.vert", "shaders/basic.frag");
        mesh = new Mesh(vertices, indices);
        mesh.bind();
    }

    public void beginFrame() {
        clear();
        // transform.position.set(transform.position.x + 0.01f, 0.0f, 0.0f);
    }

    public void render() {
        shader.bind();

        shader.setMatrix4f("model", transform.getMatrix());
        shader.setMatrix4f("view", camera.getViewMatrix());
        Window mainWindow = engine.getApp().getMainWindow();
        Vector2f winSize = mainWindow.getSize();
        shader.setMatrix4f(
                "projection",
                camera.getProjectionMatrix(winSize.x / winSize.y)
        );

        GL11.glDrawElements(
            GL11.GL_TRIANGLES,
            mesh.getIndexCount(),
            GL11.GL_UNSIGNED_INT,
            0
        );
    }

    public void endFrame() {

    }

    public void shutdown() {
        shader.delete();
        mesh.delete();
    }

    public void clear() {
        GL11.glClear(
        GL11.GL_COLOR_BUFFER_BIT |
                GL11.GL_DEPTH_BUFFER_BIT
        );
    }

    public void setFillColor(float r, float g, float b, float a) {
        GL11.glClearColor(r, g, b, a);
    }
}
