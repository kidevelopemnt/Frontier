package frontier.game.scenes;

import frontier.engine.Engine;
import frontier.engine.assets.Material;
import frontier.engine.ecs.Entity;
import frontier.engine.ecs.components.Camera;
import frontier.engine.ecs.components.CameraController;
import frontier.engine.ecs.components.LightComponent;
import frontier.engine.ecs.components.MeshRenderer;
import frontier.engine.ecs.components.physics.BoxCollider;
import frontier.engine.events.Event;
import frontier.engine.events.UpdateEvent;
import frontier.engine.graphics.lighting.LightType;
import frontier.engine.scene.Scene;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.Random;

public class PhysicsTestScene {
    private Engine engine;
    private Scene scene;

    private float groundLength = 25f;
    private float groundWidth = 25f;
    private float groundHeight = 0.1f;

    private Material boxMaterial;
    private Material groundMaterial;

    public PhysicsTestScene(Engine engine) {
        this.engine = engine;
        scene = engine.createScene("Physics Test Scene");

        boxMaterial = new Material(engine.getAssets().loadTexture("textures/crate.jpg"));
        groundMaterial = new Material(engine.getAssets().loadTexture("textures/ground.jpg"));

        scene.getCamera().getEntity().getTransform().position.set(
                8, 6, 12
        );

        scene.getCamera().getEntity().getTransform().rotation.set(
                25, 145, 0
        );
        scene.getCamera().getEntity().addComponent(CameraController.class);

        Entity ground = new Entity("Ground", scene);
        ground.addComponent(BoxCollider.class);
        MeshRenderer meshRenderer = ground.addComponent(MeshRenderer.class);
        try {
            meshRenderer.setModel(engine.getAssets().loadModel("models/cube.obj").copy());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        meshRenderer.setMaterial(groundMaterial);

        ground.getTransform().position.set(groundWidth/2, -groundHeight / 2f, groundLength/2);
        ground.getTransform().scale.set(groundWidth, groundHeight, groundLength);
        scene.addEntity(ground);

        Entity box1 = createBox("Box 1", new Vector3f(0, 3, 0));
        Entity box2 = createBox("Box 2", new Vector3f(3, 0.25f, 0));  // Intersecting the ground
        Entity box3 = createBox("Box 3", new Vector3f(0, 3.5f, 0));   // Intersecting box 1

        createLights();

        engine.getEventBus().subscribe(null, UpdateEvent.class, this::update);
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

    private Entity createBox(String name, Vector3f position) {
        Entity entity = new Entity(name, scene);
        entity.addComponent(BoxCollider.class);
        MeshRenderer mr = entity.addComponent(MeshRenderer.class);
        try {
            mr.setModel(engine.getAssets().loadModel("models/cube.obj"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mr.setMaterial(boxMaterial);

        entity.getTransform().position.set(position);

        scene.addEntity(entity);

        return entity;
    }

    private void update(Event event) {
        float deltaTime = (float) event.getContext();


    }

    public Scene getScene() {
        return scene;
    }
}
