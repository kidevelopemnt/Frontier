package frontier.game;

import frontier.engine.Engine;
import frontier.engine.GameObject;
import frontier.engine.game.IGame;
import frontier.engine.graphics.Material;
import frontier.engine.graphics.Mesh;
import frontier.engine.graphics.Texture;
import frontier.engine.graphics.lighting.DirectionalLight;
import frontier.engine.graphics.lighting.PointLight;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;


public class FrontierGame implements IGame {

    private Engine engine;

    private GameObject cube;
    private float cameraSpeed = 5.0f;
    private float sensitivity = 1.0f;

    @Override
    public void initialize(Engine engine) {
        this.engine = engine;
        engine.getRenderer().setFillColor(.1f, .2f, .1f, 1.0f);

        float[] vertices = {
                // Front (+Z)
                // position             normal          UV
                -0.5f, -0.5f,  0.5f,    0, 0, 1,       0, 0,
                0.5f, -0.5f,  0.5f,    0, 0, 1,       1, 0,
                0.5f,  0.5f,  0.5f,    0, 0, 1,       1, 1,
                -0.5f,  0.5f,  0.5f,    0, 0, 1,       0, 1,

                // Back (-Z)
                -0.5f, -0.5f, -0.5f,    0, 0, -1,      1, 0,
                0.5f, -0.5f, -0.5f,    0, 0, -1,      0, 0,
                0.5f,  0.5f, -0.5f,    0, 0, -1,      0, 1,
                -0.5f,  0.5f, -0.5f,    0, 0, -1,      1, 1,

                // Left (-X)
                -0.5f, -0.5f, -0.5f,   -1, 0, 0,       0, 0,
                -0.5f, -0.5f,  0.5f,   -1, 0, 0,       1, 0,
                -0.5f,  0.5f,  0.5f,   -1, 0, 0,       1, 1,
                -0.5f,  0.5f, -0.5f,   -1, 0, 0,       0, 1,

                // Right (+X)
                0.5f, -0.5f,  0.5f,    1, 0, 0,       0, 0,
                0.5f, -0.5f, -0.5f,    1, 0, 0,       1, 0,
                0.5f,  0.5f, -0.5f,    1, 0, 0,       1, 1,
                0.5f,  0.5f,  0.5f,    1, 0, 0,       0, 1,

                // Top (+Y)
                -0.5f,  0.5f,  0.5f,    0, 1, 0,       0, 0,
                0.5f,  0.5f,  0.5f,    0, 1, 0,       1, 0,
                0.5f,  0.5f, -0.5f,    0, 1, 0,       1, 1,
                -0.5f,  0.5f, -0.5f,    0, 1, 0,       0, 1,

                // Bottom (-Y)
                -0.5f, -0.5f, -0.5f,    0, -1, 0,      0, 1,
                0.5f, -0.5f, -0.5f,    0, -1, 0,      1, 1,
                0.5f, -0.5f,  0.5f,    0, -1, 0,      1, 0,
                -0.5f, -0.5f,  0.5f,    0, -1, 0,      0, 0
        };

        int[] indices = {
                0, 1, 2,    0, 2, 3,
                4, 5, 6,    4, 6, 7,
                8, 9, 10,   8, 10, 11,
                12, 13, 14, 12, 14, 15,
                16, 17, 18, 16, 18, 19,
                20, 21, 22, 20, 22, 23
        };

        Mesh cubeMesh = new Mesh(vertices, indices);

        Texture cubeTexture = new Texture(
                "textures/crate.jpg"
        );

        Material cubeMaterial = new Material(
                engine.getRenderer().getDefaultShader(),
                cubeTexture
        );

        cube = new GameObject(
                cubeMesh,
                cubeMaterial
        );

        cube.getTransform().rotation.y = (float) Math.toRadians(30);
        cube.getTransform().rotation.x = (float) Math.toRadians(20);

        DirectionalLight sun = new DirectionalLight(
                new Vector3f(-1, -1, -1),
                new Vector3f(1, 1, 1),
                1.0f
        );

        PointLight lamp = new PointLight(
                new Vector3f(2, 1, 2),
                new Vector3f(1, 1, 1),
                2.0f
        );

        engine.object = cube;
        engine.directionalLight = sun;
        engine.pointLight = lamp;
    }

    @Override
    public void update(float deltaTime) {
        cube.getTransform().rotation.y += 0.5f * deltaTime;

        if (engine.getInput().isKeyDown(GLFW.GLFW_KEY_W)) {
            engine.getRenderer().getCamera().moveForward(cameraSpeed * deltaTime);
        }
        if (engine.getInput().isKeyDown(GLFW.GLFW_KEY_A)) {
            engine.getRenderer().getCamera().moveLeft(cameraSpeed * deltaTime);
        }
        if (engine.getInput().isKeyDown(GLFW.GLFW_KEY_S)) {
            engine.getRenderer().getCamera().moveBackward(cameraSpeed * deltaTime);
        }
        if (engine.getInput().isKeyDown(GLFW.GLFW_KEY_D)) {
            engine.getRenderer().getCamera().moveRight(cameraSpeed * deltaTime);
        }
        if (engine.getInput().isKeyDown(GLFW.GLFW_KEY_Q)) {
            engine.getRenderer().getCamera().moveDown(cameraSpeed * deltaTime);
        }
        if (engine.getInput().isKeyDown(GLFW.GLFW_KEY_E)) {
            engine.getRenderer().getCamera().moveUp(cameraSpeed * deltaTime);
        }

        float mouseX = engine.getInput().getMouseDeltaX();
        float mouseY = engine.getInput().getMouseDeltaY();
        // FIXME: ROTATION NOT WORKING
        engine.getRenderer().getCamera().rotate(
            -mouseY * sensitivity,
            -mouseX * sensitivity
        );
    }

    @Override
    public void shutdown() {

    }
}
