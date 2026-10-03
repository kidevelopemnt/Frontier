package frontier.game.scenes;

import frontier.engine.Engine;
import frontier.engine.assets.*;
import frontier.engine.ecs.Entity;
import frontier.engine.ecs.components.*;
import frontier.engine.ecs.components.physics.BoxCollider;
import frontier.engine.events.Event;
import frontier.engine.events.TriggerEnteredEvent;
import frontier.engine.graphics.lighting.LightType;
import frontier.engine.scene.Scene;
import frontier.game.ecs.Spinner;
import org.joml.Vector3f;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class PlaygroundScene {
    private final Engine engine;
    private Scene scene;

    Mesh cubeMesh;
    Mesh planeMesh;
    Material cubeMaterial;
    Material groundMaterial;

    public PlaygroundScene(Engine engine) {
        this.engine = engine;
    }

    public void initialize() {
        scene = engine.createScene("Playground");

        createRendererItems();
        createGround();
        createSpinningCube();
        createOtherObjects();
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

        float[] planeVertices = {
                -0.5f, 0f, -0.5f,  0f, 1f, 0f, 0f, 0f,
                0.5f, 0f, -0.5f,   0f, 1f, 0f, 1f, 0f,
                -0.5f, 0f, 0.5f,   0f, 1f, 0f, 0f, 1f,
                0.5f, 0f, 0.5f,    0f, 1f, 0f, 1f, 1f
        };

        int[] planeIndices = {
                0, 2, 1,
                2, 3, 1
        };

        cubeMesh = new Mesh(vertices, indices);

        planeMesh = new Mesh(planeVertices, planeIndices);

        Texture cubeTexture = engine.getAssets().loadTexture("textures/crate.jpg");

        cubeMaterial = new Material(
                cubeTexture
        );

        Texture groundTexture = engine.getAssets().loadTexture("textures/ground.jpg");

        groundMaterial = new Material(
                groundTexture
        );
    }

    private void createGround() {
        Entity ground = new Entity("ground", scene);
        MeshRenderer meshRenderer = ground.addComponent(MeshRenderer.class);
        Model groundModel = new Model(planeMesh, groundMaterial);
        meshRenderer.setModel(groundModel);
        ground.addComponent(BoxCollider.class);

        ground.getTransform().position.y -= .5f;
        ground.getTransform().scale = new Vector3f(150, 1f, 150);

        scene.addEntity(ground);
    }

    private void createSpinningCube() {
        try {
            Model character = engine.getAssets().loadModel("models/MaleBase.obj");
            Entity entity = new Entity("character", scene);
            MeshRenderer meshRenderer = entity.addComponent(MeshRenderer.class);
            meshRenderer.setModel(character);
            scene.addEntity(entity);
        } catch (IOException e) {
            engine.getLogger().logError("Failed to load model... " + e);
        }

        Entity cube = new Entity("cube", scene);
        MeshRenderer meshRenderer = cube.addComponent(MeshRenderer.class);
        Model cubeModel = new Model(cubeMesh, cubeMaterial);
        meshRenderer.setModel(cubeModel);
        cube.addComponent(Spinner.class);

        BoxCollider boxCollider = cube.addComponent(BoxCollider.class);
        boxCollider.setTrigger(true);
        engine.getEventBus().subscribe(boxCollider, TriggerEnteredEvent.class, this::spinningCubeClicked);

        scene.addEntity(cube);
    }

    private void createOtherObjects() {
        Entity shapes = new Entity("shapes", scene);
        MeshRenderer meshRenderer = shapes.addComponent(MeshRenderer.class);
        try {
            Model shapesModel = engine.getAssets().loadModel("models/solids.obj");
            meshRenderer.setModel(shapesModel);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        shapes.getTransform().rotation.x = 90f;
        shapes.getTransform().position.y += 5f;
        scene.addEntity(shapes);
    }

    private void spinningCubeClicked(Event e) {
        System.out.println("Clicked " + e);
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
