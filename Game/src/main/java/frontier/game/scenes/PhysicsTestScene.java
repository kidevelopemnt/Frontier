package frontier.game.scenes;

import frontier.engine.Engine;
import frontier.engine.assets.Material;
import frontier.engine.ecs.Entity;
import frontier.engine.ecs.components.Camera;
import frontier.engine.ecs.components.CameraController;
import frontier.engine.ecs.components.LightComponent;
import frontier.engine.ecs.components.MeshRenderer;
import frontier.engine.ecs.components.physics.BoxCollider;
import frontier.engine.ecs.components.physics.RigidBody;
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

    private float groundLength = 20f;
    private float groundWidth = 30f;
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
                25, 0, 0
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

        // Test 1: Equal mass, no bounce
        /* createBox(
                "No Bounce A",
                new Vector3f(0, 1, 0),
                1.0f,
                0.0f,
                0.5f,
                0.3f,
                new Vector3f(2, 0, 0)
        );

        createBox(
                "No Bounce B",
                new Vector3f(4, 1, 0),
                1.0f,
                0.0f,
                new Vector3f(-2, 0, 0)
        );


        // Test 2: Equal mass, full bounce
        createBox(
                "Bounce A",
                new Vector3f(0, 1, 6),
                1.0f,
                1.0f,
                new Vector3f(2, 0, 0)
        );

        createBox(
                "Bounce B",
                new Vector3f(4, 1, 6),
                1.0f,
                1.0f,
                new Vector3f(-2, 0, 0)
        );


        // Test 3: Different masses
        createBox(
                "Light",
                new Vector3f(0, 1, 12),
                1.0f,
                1.0f,
                new Vector3f(4, 0, 0)
        );

        createBox(
                "Heavy",
                new Vector3f(4, 1, 12),
                10.0f,
                1.0f,
                new Vector3f(-1, 0, 0)
        ); */

        // Friction tests
        createBox(
                "No Friction",
                new Vector3f(2, 1, 6),
                1.0f,
                0.0f,
                0.0f,
                0.0f,
                new Vector3f(5, 0, 0)
        );

        createBox(
                "Low Friction",
                new Vector3f(2, 1, 9),
                1.0f,
                0.0f,
                0.2f,
                0.1f,
                new Vector3f(5, 0, 0)
        );

        createBox(
                "Medium Friction",
                new Vector3f(2, 1, 12),
                1.0f,
                0.0f,
                0.5f,
                0.3f,
                new Vector3f(5, 0, 0)
        );

        createBox(
                "High Friction",
                new Vector3f(2, 1, 15),
                1.0f,
                0.0f,
                0.9f,
                0.7f,
                new Vector3f(5, 0, 0)
        );

        createBox(
                "Static Friction",
                new Vector3f(2, 1, 18),
                1.0f,
                0.0f,
                0.8f,
                0.5f,
                new Vector3f(0.5f, 0, 0)
        );

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

    private Entity createBox(
            String name,
            Vector3f position,
            float mass,
            float restitution,
            float staticFriction,
            float dynamicFriction,
            Vector3f velocity
    ) {
        Entity entity = new Entity(name, scene);

        entity.addComponent(BoxCollider.class);

        RigidBody rigidBody = entity.addComponent(RigidBody.class);
        rigidBody.setMass(mass);
        rigidBody.setRestitution(restitution);
        rigidBody.setStaticFriction(staticFriction);
        rigidBody.setDynamicFriction(dynamicFriction);
        rigidBody.setVelocity(velocity);

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
