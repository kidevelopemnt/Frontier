package frontier.game.scenes;

import frontier.engine.Engine;
import frontier.engine.assets.Model;
import frontier.engine.ecs.Entity;
import frontier.engine.ecs.components.*;
import frontier.engine.ecs.components.physics.BoxCollider;
import frontier.engine.assets.Material;
import frontier.engine.assets.Mesh;
import frontier.engine.assets.Texture;
import frontier.engine.graphics.lighting.LightType;
import frontier.engine.scene.Scene;
import frontier.game.ecs.Spinner;
import org.joml.Vector3f;

import java.io.IOException;
import java.nio.file.Path;

public class PlaygroundScene {
    private final Engine engine;
    private Scene scene;

    Mesh cubeMesh;
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

        Texture groundTexture = new Texture(
                "textures/ground.jpg"
        );

        groundMaterial = new Material(
                engine.getRenderer().getDefaultShader(),
                groundTexture
        );
        groundMaterial.setName("groundMaterial");
    }

    private void createGround() {
        Entity ground = new Entity("ground", scene);
        MeshRenderer meshRenderer = ground.addComponent(MeshRenderer.class);
        meshRenderer.setMesh(cubeMesh);
        meshRenderer.setMaterial(groundMaterial);
        ground.addComponent(BoxCollider.class);

        ground.getTransform().position.y -= .5f;
        ground.getTransform().scale = new Vector3f(150, .1f, 150);

        scene.addEntity(ground);
    }

    private void createSpinningCube() {
        try {
            Model character = engine.getAssets().loadModel(Path.of("models/MaleBase.obj"));
            Entity entity = new Entity("character", scene);
            MeshRenderer meshRenderer = entity.addComponent(MeshRenderer.class);
            for (Mesh mesh : character.getMeshes()) {
                meshRenderer.setMesh(mesh);  // TODO: Handle multiple meshes
            }
            scene.addEntity(entity);
        } catch (IOException e) {
            engine.getLogger().logError("Failed to load model... " + e);
        }

        Entity cube = new Entity("cube", scene);
        MeshRenderer meshRenderer = cube.addComponent(MeshRenderer.class);
        meshRenderer.setMesh(cubeMesh);
        meshRenderer.setMaterial(cubeMaterial);
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
