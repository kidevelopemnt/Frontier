package frontier.game.scenes;

import frontier.engine.Engine;
import frontier.engine.ecs.Entity;
import frontier.engine.ecs.components.Camera;
import frontier.engine.ecs.components.CameraController;
import frontier.engine.ecs.components.LightComponent;
import frontier.engine.ecs.components.MeshRenderer;
import frontier.engine.graphics.Material;
import frontier.engine.graphics.Mesh;
import frontier.engine.graphics.Texture;
import frontier.engine.graphics.lighting.LightType;
import frontier.engine.scene.Scene;
import frontier.game.ecs.Spinner;
import org.joml.Vector3f;

public class PlaygroundScene {
    private final Engine engine;
    private Scene scene;

    Mesh cubeMesh;
    Material cubeMaterial;

    public PlaygroundScene(Engine engine) {
        this.engine = engine;
    }

    public void initialize() {
        scene = engine.createScene("Playground");

        createRendererItems();
        createStationaryCube();
        createSpinningCube();
        createLights();
        createCamera();
    }

    public Scene getScene() {
        return scene;
    }

    private void createRendererItems() {
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

        cubeMesh = new Mesh(vertices, indices);
        cubeMesh.setName("cube");

        Texture cubeTexture = new Texture(
                "textures/crate.jpg"
        );

        cubeMaterial = new Material(
                engine.getRenderer().getDefaultShader(),
                cubeTexture
        );
        cubeMaterial.setName("cubeMaterial");
    }

    private void createStationaryCube() {
        Entity cube2 = new Entity("cube2", scene);
        MeshRenderer meshRenderer = cube2.addComponent(MeshRenderer.class);
        meshRenderer.setMesh(cubeMesh);
        meshRenderer.setMaterial(cubeMaterial);
        cube2.getTransform().position.x = 5f;
        cube2.getTransform().rotation.y = (float) Math.toRadians(30);
        cube2.getTransform().rotation.x = (float) Math.toRadians(20);

        scene.addEntity(cube2);
    }

    private void createSpinningCube() {
        Entity cube = new Entity("cube", scene);
        MeshRenderer meshRenderer = cube.addComponent(MeshRenderer.class);
        meshRenderer.setMesh(cubeMesh);
        meshRenderer.setMaterial(cubeMaterial);
        cube.getTransform().rotation.y = (float) Math.toRadians(30);
        cube.getTransform().rotation.x = (float) Math.toRadians(20);

        cube.addComponent(Spinner.class);

        scene.addEntity(cube);
    }

    private void createLights() {
        Entity sun = new Entity("sun", scene);
        sun.getTransform().rotation = new Vector3f(-1, -1, -1);
        LightComponent sunLight = sun.addComponent(LightComponent.class);
        sunLight.setLightType(LightType.DIRECTIONAL);
        sunLight.setColor(new Vector3f(1, 1, 1));
        sunLight.setIntensity(1.0f);

        Entity lamp = new Entity("lamp", scene);
        lamp.getTransform().position = new Vector3f(2, 1, 2);
        LightComponent lampLight = lamp.addComponent(LightComponent.class);
        lampLight.setLightType(LightType.POINT);
        lampLight.setColor(new Vector3f(1, 0, 0));
        lampLight.setIntensity(2.0f);

        scene.addEntity(sun);
        scene.addEntity(lamp);
    }

    private void createCamera() {
        Camera camera = scene.getCamera();
        camera.getEntity().addComponent(CameraController.class);
    }
}
