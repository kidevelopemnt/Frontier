package frontier.game;

import frontier.engine.Engine;
import frontier.engine.ecs.Entity;
import frontier.engine.ecs.components.LightComponent;
import frontier.engine.ecs.components.MeshRenderer;
import frontier.engine.game.IGame;
import frontier.engine.graphics.Material;
import frontier.engine.graphics.Mesh;
import frontier.engine.graphics.Texture;
import frontier.engine.graphics.lighting.LightType;

import frontier.engine.scene.Scene;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;


public class FrontierGame implements IGame {

    private Engine engine;

    private Scene scene;
    private Entity cube;
    private float cameraSpeed = 5.0f;
    private float sensitivity = 0.0025f;

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

        scene = new Scene();

        cube = new Entity("cube");
        MeshRenderer meshRenderer = cube.addComponent(MeshRenderer.class);
        meshRenderer.setMesh(cubeMesh);
        meshRenderer.setMaterial(cubeMaterial);
        cube.getTransform().rotation.y = (float) Math.toRadians(30);
        cube.getTransform().rotation.x = (float) Math.toRadians(20);

        Entity cube2 = new Entity("cube2");
        meshRenderer = cube2.addComponent(MeshRenderer.class);
        meshRenderer.setMesh(cubeMesh);
        meshRenderer.setMaterial(cubeMaterial);
        cube2.getTransform().position.x = 5f;
        cube2.getTransform().rotation.y = (float) Math.toRadians(30);
        cube2.getTransform().rotation.x = (float) Math.toRadians(20);


        Entity sun = new Entity("sun");
        sun.getTransform().rotation = new Vector3f(-1, -1, -1);
        LightComponent sunLight = sun.addComponent(LightComponent.class);
        sunLight.setLightType(LightType.DIRECTIONAL);
        sunLight.setColor(new Vector3f(1, 1, 1));
        sunLight.setIntensity(1.0f);

        Entity lamp = new Entity("lamp");
        lamp.getTransform().position = new Vector3f(2, 1, 2);
        LightComponent lampLight = sun.addComponent(LightComponent.class);
        lampLight.setLightType(LightType.DIRECTIONAL);
        lampLight.setColor(new Vector3f(1, 0, 0));
        lampLight.setIntensity(2.0f);

        scene.addEntity(cube);
        scene.addEntity(cube2);
        scene.addEntity(sun);
        scene.addEntity(lamp);

        engine.loadScene(scene);
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
        System.out.println(mouseX + " " + mouseY);
        engine.getRenderer().getCamera().rotate(
            mouseY * sensitivity,
            mouseX * sensitivity
        );
    }

    @Override
    public void shutdown() {

    }
}
