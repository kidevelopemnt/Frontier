package frontier.engine.graphics;

import frontier.engine.Engine;
import frontier.engine.GameObject;
import frontier.engine.application.Window;
import frontier.engine.graphics.lighting.DirectionalLight;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;

public class Renderer {
    private Engine engine;
    private Camera camera;

    private Shader defaultShader;
    private Vector3f ambientLight = new Vector3f(0.5f, 0.5f, 0.5f);

    public void initialize(Engine engine) {
        this.engine = engine;
        camera = new Camera();

        GL11.glEnable(GL11.GL_DEPTH_TEST);

        defaultShader = new Shader("shaders/basic.vert", "shaders/basic.frag");
    }

    public void beginFrame() {
        clear();
    }

    public void render(GameObject object) {
        Material material = object.getMaterial();

        material.bind();

        Shader shader = material.getShader();
        Matrix4f model = object.getTransform().getMatrix();

        shader.setMatrix4f("model", model);
        shader.setMatrix4f("view", camera.getViewMatrix());
        Window mainWindow = engine.getApp().getMainWindow();
        Vector2f winSize = mainWindow.getSize();
        shader.setMatrix4f(
                "projection",
                camera.getProjectionMatrix(winSize.x / winSize.y)
        );

        shader.setVector3f("ambientLight", ambientLight);
        DirectionalLight light = engine.lightObject;  // TODO: This is temporary, add scenes!
        shader.setVector3f(
                "lightDirection",
                light.getDirection()
        );

        shader.setVector3f(
                "directionalLight",
                light.getColorWithIntensity()
        );

        Matrix3f normalMatrix = new Matrix3f(model).invert().transpose();
        shader.setMatrix3f("normalMatrix", normalMatrix);

        object.getMesh().bind();

        GL11.glDrawElements(
            GL11.GL_TRIANGLES,
            object.getMesh().getIndexCount(),
            GL11.GL_UNSIGNED_INT,
            0
        );
    }


    public void endFrame() {

    }

    public void shutdown() {
        defaultShader.delete();
        // cube.delete();  TODO: Store a list of objects and delete all
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

    public Shader getDefaultShader() {
        return defaultShader;
    }
}
