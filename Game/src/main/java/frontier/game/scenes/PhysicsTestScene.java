
package frontier.game.scenes;

import frontier.engine.Engine;
import frontier.engine.assets.Material;
import frontier.engine.ecs.Entity;
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

public class PhysicsTestScene {
    private final Engine engine;
    private final Scene scene;

    private final float groundLength = 20f;
    private final float groundWidth = 30f;
    private final float groundHeight = 0.1f;

    private Material boxMaterial;
    private Material groundMaterial;

    public PhysicsTestScene(Engine engine) {
        this.engine = engine;
        scene = engine.createScene("Physics Test Scene");

        boxMaterial = new Material(
                engine.getAssets().loadTexture("textures/crate.jpg")
        );

        groundMaterial = new Material(
                engine.getAssets().loadTexture("textures/ground.jpg")
        );
    }

    public void initialize() {
        scene.getCamera().getEntity().getTransform().position.set(8, 6, 12);
        scene.getCamera().getEntity().getTransform().rotation.set(25, 0, 0);
        scene.getCamera().getEntity().addComponent(CameraController.class);

        createGround();

        // Positional correction tests
        createFallingBoxTest();
        createBoxStackTest();
        createUnevenStackTest();

        // Future collision resolution tests
        // createWallCollisionTest();
        // createMultipleContactTest();

        // Future restitution tests
        // createNoBounceTest();
        // createFullBounceTest();
        // createDifferentMassBounceTest();

        // Future friction tests
        // createFrictionTests();
        // createStaticFrictionTest();

        createLights();

        engine.getEventBus().subscribe(
                null,
                UpdateEvent.class,
                this::update
        );
    }

    private void createGround() {
        Entity ground = new Entity("Ground", scene);
        ground.addComponent(BoxCollider.class);

        MeshRenderer meshRenderer = ground.addComponent(MeshRenderer.class);

        try {
            meshRenderer.setModel(
                    engine.getAssets().loadModel("models/cube.obj").copy()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        meshRenderer.setMaterial(groundMaterial);

        ground.getTransform().position.set(
                groundWidth / 2,
                -groundHeight / 2f,
                groundLength / 2
        );

        ground.getTransform().scale.set(
                groundWidth,
                groundHeight,
                groundLength
        );

        scene.addEntity(ground);
    }

    // --------------------------------------------------
    // POSITIONAL CORRECTION TESTS
    // --------------------------------------------------

    private void createFallingBoxTest() {
        createBox(
                "Falling Box",
                new Vector3f(4, 5, 4),
                1.0f,
                0.0f,
                0.5f,
                0.3f,
                new Vector3f()
        );
    }

    private void createBoxStackTest() {
        createBoxStack(
                "Box Stack",
                new Vector3f(10, 0.5f, 4),
                5,
                1.0f
        );
    }

    private void createUnevenStackTest() {
        // TODO: Create a stack with different masses.
        createBoxStack(
                "Uneven Stack",
                new Vector3f(16, 0.5f, 4),
                5,
                1.0f,
                3.0f
        );
    }

    private void createBoxStack(
            String name,
            Vector3f basePosition,
            int boxCount,
            float mass,
            float massStep
    ) {
        for (int i = 0; i < boxCount; i++) {
            createBox(
                    name + " " + (i + 1),
                    new Vector3f(
                            basePosition.x,
                            basePosition.y + i,
                            basePosition.z
                    ),
                    mass + (massStep * 1),
                    0.0f,
                    0.5f,
                    0.3f,
                    new Vector3f()
            );
        }
    }

    private void createBoxStack(
            String name,
            Vector3f basePosition,
            int boxCount,
            float mass
    ) {
        createBoxStack(name, basePosition, boxCount, mass, 0f);
    }

    // --------------------------------------------------
    // FUTURE COLLISION TESTS
    // --------------------------------------------------

    private void createWallCollisionTest() {
        // TODO: Test a dynamic box colliding with a static wall.
    }

    private void createMultipleContactTest() {
        // TODO: Test multiple simultaneous collision contacts.
    }

    // --------------------------------------------------
    // FUTURE RESTITUTION TESTS
    // --------------------------------------------------

    private void createNoBounceTest() {
        // TODO: Test two equal-mass objects with zero restitution.
    }

    private void createFullBounceTest() {
        // TODO: Test two equal-mass objects with full restitution.
    }

    private void createDifferentMassBounceTest() {
        // TODO: Test collisions between objects of different masses.
    }

    // --------------------------------------------------
    // FUTURE FRICTION TESTS
    // --------------------------------------------------

    private void createFrictionTests() {
        // TODO: Compare objects with different friction coefficients.
    }

    private void createStaticFrictionTest() {
        // TODO: Test the transition between static and dynamic friction.
    }

    // --------------------------------------------------
    // ENTITY CREATION
    // --------------------------------------------------

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

        MeshRenderer meshRenderer = entity.addComponent(MeshRenderer.class);

        try {
            meshRenderer.setModel(
                    engine.getAssets().loadModel("models/cube.obj")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        meshRenderer.setMaterial(boxMaterial);

        entity.getTransform().position.set(position);
        scene.addEntity(entity);

        return entity;
    }

    // --------------------------------------------------
    // LIGHTING AND UPDATES
    // --------------------------------------------------

    private void createLights() {
        Entity sun = new Entity("sun", scene);
        sun.getTransform().rotation.set(-1, -1, -1);

        LightComponent sunLight = sun.addComponent(LightComponent.class);
        sunLight.setLightType(LightType.DIRECTIONAL);
        sunLight.setColor(new Vector3f(1, 1, 1));
        sunLight.setIntensity(1.0f);

        Entity lamp = new Entity("lamp", scene);
        lamp.getTransform().position.set(2, 1, 2);

        LightComponent lampLight = lamp.addComponent(LightComponent.class);
        lampLight.setLightType(LightType.POINT);
        lampLight.setColor(new Vector3f(1, 0, 0));
        lampLight.setIntensity(2.0f);

        scene.addEntity(sun);
        scene.addEntity(lamp);
    }

    private void update(Event event) {
        float deltaTime = (float) event.getContext();
    }

    public Scene getScene() {
        return scene;
    }
}
