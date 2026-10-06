package frontier.engine.graphics;

import frontier.engine.Engine;
import frontier.engine.application.Window;
import frontier.engine.assets.Material;
import frontier.engine.assets.Mesh;
import frontier.engine.assets.Texture;
import frontier.engine.ecs.Entity;
import frontier.engine.ecs.components.Camera;
import frontier.engine.ecs.components.LightComponent;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.*;

import java.nio.file.Path;

public class Renderer {
    private Engine engine;

    private Material defaultMaterial;
    private Shader defaultShader;
    private Vector3f ambientLight = new Vector3f(0.5f, 0.5f, 0.5f);

    public void initialize(Engine engine) {
        this.engine = engine;

        GL11.glEnable(GL11.GL_DEPTH_TEST);

        defaultShader = new Shader("shaders/basic.vert", "shaders/basic.frag");
        defaultMaterial = new Material(defaultShader, engine.getAssets().loadTexture("textures/default.png"));
    }

    public void beginFrame() {
        clear();
    }

    public void render(Mesh mesh, Material material, Transform transform, Camera camera, Entity directionalLight, Entity pointLight, float alpha) {
        if (material == null) {
            material = defaultMaterial;
        }
        if (material.getShader() == null) {
            material.setShader(defaultShader);
        }

        material.bind();

        Shader shader = material.getShader();
        Vector3f interpPos = transform.getInterpolatedPosition(alpha);
        Matrix4f model = new Matrix4f()
                .translation(interpPos)
                .rotateXYZ(transform.rotation.x, transform.rotation.y, transform.rotation.z)
                .scale(transform.scale);

        shader.setMatrix4f("model", model);
        shader.setMatrix4f("view", camera.getViewMatrix());
        Window mainWindow = engine.getApp().getMainWindow();
        Vector2f winSize = mainWindow.getSize();
        shader.setMatrix4f(
                "projection",
                camera.getProjectionMatrix(winSize.x / winSize.y)
        );

        shader.setVector3f("ambientLight", ambientLight);

        // TODO: Handle lighting from scenes
        if (directionalLight != null) {
            LightComponent directionalLC = directionalLight.getComponent(LightComponent.class);
            shader.setVector3f(
                    "lightDirection",
                    directionalLight.getTransform().rotation
            );

            shader.setVector3f(
                    "directionalLight",
                    directionalLC.getColorWithIntensity()
            );
        }

        if (pointLight != null) {
            LightComponent pointLC = pointLight.getComponent(LightComponent.class);
            shader.setVector3f(
                    "pointLightPosition",
                    pointLight.getTransform().position
            );

            shader.setVector3f(
                    "pointLightColor",
                    pointLC.getColor()
            );

            shader.setFloat(
                    "pointLightIntensity",
                    pointLC.getIntensity()
            );
        }

        Matrix3f normalMatrix = new Matrix3f(model).invert().transpose();
        shader.setMatrix3f("normalMatrix", normalMatrix);

        mesh.bind();

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
