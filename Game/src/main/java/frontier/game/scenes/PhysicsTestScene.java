
package frontier.game.scenes;

import frontier.engine.Engine;
import frontier.engine.assets.Material;
import frontier.engine.ecs.Entity;
import frontier.engine.ecs.components.CameraController;
import frontier.engine.ecs.components.LightComponent;
import frontier.engine.ecs.components.MeshRenderer;
import frontier.engine.ecs.components.physics.BoxCollider;
import frontier.engine.ecs.components.physics.CapsuleCollider;
import frontier.engine.ecs.components.physics.RigidBody;
import frontier.engine.ecs.components.physics.SphereCollider;
import frontier.engine.events.Event;
import frontier.engine.events.UpdateEvent;
import frontier.engine.graphics.lighting.LightType;
import frontier.engine.input.Key;
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

    private boolean physicsEnabled = false;

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
        engine.getPhysics().setDoPhysics(physicsEnabled);

        scene.getCamera().getEntity().getTransform().position.set(8, 6, 12);
        scene.getCamera().getEntity().getTransform().rotation.set(25, 0, 0);
        scene.getCamera().getEntity().addComponent(CameraController.class);

        createGround();

        // Positional correction tests
        // createFallingBoxTest();
        // createBoxStackTest();
        // createUnevenStackTest();

        // Future collision resolution tests
        // createWallCollisionTest();
        // createMultipleContactTest();

        // Restitution tests
        // createNoBounceTest();
        // createFullBounceTest();
        // createDifferentMassBounceTest();
        // createStaticWallTest();

        // Collision tests
        testBoxBox();
        testSphereSphere();
        testBoxSphere();
        testCapsuleCapsule();
        testSphereCapsule();
        testBoxCapsule();

        // Future friction tests
        // createFrictionTests();
        // createStaticFrictionTest();

        createLights();

        engine.getEventBus().subscribe(null, UpdateEvent.class, this::update);
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

    private void update(Event event) {
        float deltaTime = (float) event.getContext();

        if (engine.getInput().isKeyUp(Key.SPACE)) {
            physicsEnabled = !physicsEnabled;
            engine.getPhysics().setDoPhysics(physicsEnabled);
        }
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
                    mass + (massStep * i),
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
        // Equal masses, zero restitution.
        // Expected: both boxes stop after the collision.

        createBox(
                "No Bounce - A",
                new Vector3f(3, 0.5f, 4),
                1.0f,
                0.0f,
                0.0f,
                0.0f,
                new Vector3f(2, 0, 0)
        );

        createBox(
                "No Bounce - B",
                new Vector3f(6, 0.5f, 4),
                1.0f,
                0.0f,
                0.0f,
                0.0f,
                new Vector3f(-2, 0, 0)
        );
    }

    private void createFullBounceTest() {
        // Equal masses, maximum restitution.
        // Expected: boxes exchange velocities.

        createBox(
                "Full Bounce - A",
                new Vector3f(3, 0.5f, 8),
                1.0f,
                1.0f,
                0.0f,
                0.0f,
                new Vector3f(2, 0, 0)
        );

        createBox(
                "Full Bounce - B",
                new Vector3f(6, 0.5f, 8),
                1.0f,
                1.0f,
                0.0f,
                0.0f,
                new Vector3f(-2, 0, 0)
        );
    }

    private void createDifferentMassBounceTest() {
        // Light body: 1 kg.
        // Heavy body: 10 kg.
        // Expected: the lighter body experiences a much larger
        // velocity change than the heavier body.

        createBox(
                "Different Mass - Light",
                new Vector3f(3, 0.5f, 12),
                1.0f,
                1.0f,
                0.0f,
                0.0f,
                new Vector3f(4, 0, 0)
        );

        createBox(
                "Different Mass - Heavy",
                new Vector3f(6, 0.5f, 12),
                10.0f,
                1.0f,
                0.0f,
                0.0f,
                new Vector3f(-1, 0, 0)
        );
    }

    private void createStaticWallTest() {
        // A dynamic box colliding with an immovable wall.
        // Expected: the box rebounds, while the wall remains stationary.

        createBox(
                "Static Wall Test - Moving Box",
                new Vector3f(20, 0.5f, 16),
                1.0f,
                1.0f,
                0.0f,
                0.0f,
                new Vector3f(4, 0, 0)
        );

        // TODO: Create a static box collider at (23, 0.5, 16).
        // It must have a BoxCollider but NO RigidBody component.
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
    // COLLISION TESTS
    // --------------------------------------------------

    private void testBoxBox() {
        // Two boxes approach each other along X.
        createBox(
                "Box-Box A",
                new Vector3f(13.5f, 0.5f, 2),
                1.0f, 0.4f, 0.6f, 0.4f,
                new Vector3f(2, 0, 0)
        );

        createBox(
                "Box-Box B",
                new Vector3f(16.5f, 0.5f, 2),
                1.0f, 0.4f, 0.6f, 0.4f,
                new Vector3f(-2, 0, 0)
        );
    }

    private void testSphereSphere() {
        // Two spheres approach each other along X.
        createSphere(
                "Sphere-Sphere A",
                new Vector3f(13.5f, 0.5f, 5),
                0.5f, 1.0f, 0.4f, 0.6f, 0.4f,
                new Vector3f(2, 0, 0)
        );

        createSphere(
                "Sphere-Sphere B",
                new Vector3f(16.5f, 0.5f, 5),
                0.5f, 1.0f, 0.4f, 0.6f, 0.4f,
                new Vector3f(-2, 0, 0)
        );
    }

    private void testBoxSphere() {
        // A sphere approaches a box along X.
        createBox(
                "Box-Sphere Box",
                new Vector3f(16.5f, 0.5f, 8),
                1.0f, 0.4f, 0.6f, 0.4f,
                new Vector3f(0, 0, 0)
        );

        createSphere(
                "Box-Sphere Sphere",
                new Vector3f(13.5f, 0.5f, 8),
                0.5f, 1.0f, 0.4f, 0.6f, 0.4f,
                new Vector3f(2, 0, 0)
        );
    }

    private void testCapsuleCapsule() {
        // Two vertical capsules approach each other along X.
        createCapsule(
                "Capsule-Capsule A",
                new Vector3f(13.5f, 0.9f, 11),
                0.4f, 1.8f,
                1.0f, 0.4f, 0.6f, 0.4f,
                new Vector3f(2, 0, 0)
        );

        createCapsule(
                "Capsule-Capsule B",
                new Vector3f(16.5f, 0.9f, 11),
                0.4f, 1.8f,
                1.0f, 0.4f, 0.6f, 0.4f,
                new Vector3f(-2, 0, 0)
        );
    }

    private void testSphereCapsule() {
        // A sphere approaches a vertical capsule along X.
        createSphere(
                "Sphere-Capsule Sphere",
                new Vector3f(13.5f, 0.5f, 14),
                0.5f, 1.0f, 0.4f, 0.6f, 0.4f,
                new Vector3f(2, 0, 0)
        );

        createCapsule(
                "Sphere-Capsule Capsule",
                new Vector3f(16.5f, 0.9f, 14),
                0.4f, 1.8f,
                1.0f, 0.4f, 0.6f, 0.4f,
                new Vector3f(0, 0, 0)
        );
    }

    private void testBoxCapsule() {
        // A capsule approaches a box along X.
        createBox(
                "Box-Capsule Box",
                new Vector3f(16.5f, 0.5f, 17),
                1.0f, 0.4f, 0.6f, 0.4f,
                new Vector3f(0, 0, 0)
        );

        createCapsule(
                "Box-Capsule Capsule",
                new Vector3f(13.5f, 0.9f, 17),
                0.4f, 1.8f,
                1.0f, 0.4f, 0.6f, 0.4f,
                new Vector3f(2, 0, 0)
        );
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

        System.out.println(entity.getTransform().position);
        entity.getTransform().position.set(position);
        System.out.println(entity.getTransform().position);
        System.out.println("-----");
        scene.addEntity(entity);

        return entity;
    }

    private Entity createSphere(
            String name,
            Vector3f position,
            float radius,
            float mass,
            float restitution,
            float staticFriction,
            float dynamicFriction,
            Vector3f velocity
    ) {
        Entity entity = new Entity(name, scene);
        entity.getTransform().position.set(position);

        MeshRenderer meshRenderer = entity.addComponent(MeshRenderer.class);

        try {
            meshRenderer.setModel(
                    engine.getAssets().loadModel("models/sphere.obj")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SphereCollider collider = entity.addComponent(SphereCollider.class);
        collider.setRadius(radius);

        RigidBody rigidBody = entity.addComponent(RigidBody.class);
        rigidBody.setMass(mass);
        rigidBody.setRestitution(restitution);
        rigidBody.setStaticFriction(staticFriction);
        rigidBody.setDynamicFriction(dynamicFriction);
        rigidBody.setVelocity(velocity);

        scene.addEntity(entity);

        return entity;
    }

    private Entity createCapsule(
            String name,
            Vector3f position,
            float radius,
            float height,
            float mass,
            float restitution,
            float staticFriction,
            float dynamicFriction,
            Vector3f velocity
    ) {
        Entity entity = new Entity(name, scene);
        entity.getTransform().position.set(position);

        CapsuleCollider collider = entity.addComponent(CapsuleCollider.class);
        collider.setRadius(radius);
        collider.setHeight(height);

        RigidBody rigidBody = entity.addComponent(RigidBody.class);
        rigidBody.setMass(mass);
        rigidBody.setRestitution(restitution);
        rigidBody.setStaticFriction(staticFriction);
        rigidBody.setDynamicFriction(dynamicFriction);
        rigidBody.setVelocity(velocity);

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

    public Scene getScene() {
        return scene;
    }
}
