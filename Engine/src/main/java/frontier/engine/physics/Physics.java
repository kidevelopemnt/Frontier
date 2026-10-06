package frontier.engine.physics;

import frontier.engine.Engine;
import frontier.engine.ecs.Entity;
import frontier.engine.ecs.components.physics.CharacterController;
import frontier.engine.ecs.components.physics.Collider;
import frontier.engine.ecs.components.physics.RigidBody;
import frontier.engine.graphics.DebugRenderer;
import frontier.engine.scene.Scene;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Physics {
    private final Engine engine;
    private final CollisionDetector collisionDetector;

    private Scene scene;

    private final Vector3f gravity = new Vector3f(0f, -9.81f, 0f);
    private final List<CollisionResult> collisions = new ArrayList<>();

    private static final float FIXED_TIME_STEP = 1.0f / 60.0f;
    private static final float COLLISION_SKIN = 0.001f;
    private float physicsAccumulator = 0.0f;
    private float maxVelocity = Float.POSITIVE_INFINITY;

    public Physics(Engine engine, Scene scene) {
        this.engine = engine;
        this.scene = scene;

        collisionDetector = new CollisionDetector();
    }

    public void update(float deltaTime) {
        physicsAccumulator += deltaTime;

        while (physicsAccumulator >= FIXED_TIME_STEP) {
            simulate(FIXED_TIME_STEP);
            physicsAccumulator -= FIXED_TIME_STEP;
        }
    }

    private void simulate(float deltaTime) {
        // Character controllers
        for (Entity entity : scene.getEntities()) {
            if (!entity.isEnabled()) {
                continue;
            }

            CharacterController controller =
                    entity.getComponent(CharacterController.class);

            if (controller == null) {
                continue;
            }

            controller.setGrounded(false);
            controller.applyGravity(deltaTime);

            Vector3f desiredVelocity = controller.getDesiredVelocity();
            controller.getVelocity().x = desiredVelocity.x;
            controller.getVelocity().z = desiredVelocity.z;

            entity.getTransform().position.fma(
                    deltaTime,
                    controller.getVelocity()
            );
        }

        // Apply rigidbody forces
        for (Entity entity : scene.getEntities()) {
            if (!entity.isEnabled()) {
                continue;
            }

            RigidBody rigidBody = entity.getComponent(RigidBody.class);

            if (rigidBody == null) {
                continue;
            }

            if (rigidBody.usesGravity()) {
                rigidBody.addForce(new Vector3f(gravity).mul(rigidBody.getMass()));
            }

            Vector3f acceleration = new Vector3f(rigidBody.getForce()).div(rigidBody.getMass());
            rigidBody.getVelocity().fma(deltaTime, acceleration);
            rigidBody.getVelocity().mul(
                    1.0f / (1.0f + rigidBody.getLinearDamping() * deltaTime)
            );

            if (rigidBody.getVelocity().lengthSquared() > maxVelocity * maxVelocity) {
                rigidBody.getVelocity().normalize().mul(maxVelocity);
            }

            entity.getTransform().position.fma(
                    deltaTime,
                    rigidBody.getVelocity()
            );

            rigidBody.clearForces();
        }

        // Detect Collisions
        collisions.clear();
        List<Collider> colliders = new ArrayList<>();

        for (Entity entity : scene.getEntities()) {
            Collider collider = entity.getCollider();

            if (collider != null) {
                colliders.add(collider);
            }
        }

        for (int i = 0; i < colliders.size(); i++) {
            for (int j = i + 1; j < colliders.size(); j++) {
                Collider a = colliders.get(i);
                Collider b = colliders.get(j);

                CollisionResult result = collisionDetector.detect(a, b);

                if (result != null) {
                    System.out.println(
                            "COLLISION: "
                                    + a.getEntity().getName()
                                    + " <-> "
                                    + b.getEntity().getName()
                    );
                    collisions.add(result);

                    Entity entityA = result.getColliderA().getEntity();
                    Entity entityB = result.getColliderB().getEntity();

                    boolean characterA = entityA.hasComponent(CharacterController.class);
                    boolean characterB = entityB.hasComponent(CharacterController.class);

                    if (characterA || characterB) {
                        resolveCharacterCollision(result);
                    } else {
                        resolveCollision(result);
                    }
                }
            }
        }
    }

    private void resolveCollision(CollisionResult collision) {
        Entity entityA = collision.getColliderA().getEntity();
        Entity entityB = collision.getColliderB().getEntity();

        boolean dynamicA = entityA.hasComponent(RigidBody.class);
        boolean dynamicB = entityB.hasComponent(RigidBody.class);

        if (!dynamicA && !dynamicB) {
            return;
        }

        RigidBody bodyA = dynamicA
                ? entityA.getComponent(RigidBody.class)
                : null;

        RigidBody bodyB = dynamicB
                ? entityB.getComponent(RigidBody.class)
                : null;

        float inverseMassA = dynamicA
                ? 1.0f / bodyA.getMass()
                : 0.0f;

        float inverseMassB = dynamicB
                ? 1.0f / bodyB.getMass()
                : 0.0f;

        // Positional correction
        float totalInverseMass = inverseMassA + inverseMassB;

        if (totalInverseMass > 0.0f) {
            Vector3f correction = new Vector3f(collision.getNormal())
                    .mul(collision.getPenetration() / totalInverseMass);

            entityA.getTransform().position.fma(
                    -inverseMassA,
                    correction
            );

            entityB.getTransform().position.fma(
                    inverseMassB,
                    correction
            );
        }

        // Velocity response
        Vector3f velocityA = dynamicA
                ? bodyA.getVelocity()
                : new Vector3f();

        Vector3f velocityB = dynamicB
                ? bodyB.getVelocity()
                : new Vector3f();

        Vector3f relativeVelocity = new Vector3f(velocityB)
                .sub(velocityA);

        Vector3f normal = collision.getNormal();

        float velocityAlongNormal = relativeVelocity.dot(normal);

        if (velocityAlongNormal > 0.0f) {
            return;
        }

        Vector3f tangent = new Vector3f(relativeVelocity)
                .fma(-velocityAlongNormal, normal);

        if (tangent.lengthSquared() > 0.000001f) {
            tangent.normalize();
        }

        float restitution;

        if (dynamicA && dynamicB) {
            restitution = Math.min(
                    bodyA.getRestitution(),
                    bodyB.getRestitution()
            );
        } else if (dynamicA) {
            restitution = bodyA.getRestitution();
        } else {
            restitution = bodyB.getRestitution();
        }

        float impulseMagnitude = -(1.0f + restitution) * velocityAlongNormal / (inverseMassA + inverseMassB);
        Vector3f impulse = new Vector3f(normal).mul(impulseMagnitude);
        float frictionImpulseMagnitude = -relativeVelocity.dot(tangent) / (inverseMassA + inverseMassB);

        float maxStaticFriction = impulseMagnitude * getStaticFriction(bodyA, bodyB);
        if (Math.abs(frictionImpulseMagnitude) > maxStaticFriction) {
            frictionImpulseMagnitude = -impulseMagnitude * getDynamicFriction(bodyA, bodyB);
        }

        Vector3f frictionImpulse = new Vector3f(tangent).mul(frictionImpulseMagnitude);

        Vector3f totalImpulse = new Vector3f(impulse).add(frictionImpulse);

        if (dynamicA) {
            bodyA.addImpulse(new Vector3f(totalImpulse).negate());
        }

        if (dynamicB) {
            bodyB.addImpulse(totalImpulse);
        }
    }

    private void resolveCharacterCollision(CollisionResult collision) {
        Entity entityA = collision.getColliderA().getEntity();
        Entity entityB = collision.getColliderB().getEntity();

        Entity characterEntity;
        Entity otherEntity;

        if (entityA.hasComponent(CharacterController.class)) {
            characterEntity = entityA;
            otherEntity = entityB;
        } else {
            characterEntity = entityB;
            otherEntity = entityA;
        }

        CharacterController controller = characterEntity.getComponent(CharacterController.class);

        Vector3f normal = new Vector3f(collision.getNormal());
        if (characterEntity == entityA) {
            normal.negate();
        }

        System.out.println(
                "Character collision: normal=" + normal +
                        " penetration=" + collision.getPenetration() +
                        " velocity=" + controller.getVelocity()
        );

        System.out.println(
                "Character position=" +
                        characterEntity.getTransform().position
        );

        Vector3f correction = new Vector3f(normal).mul(collision.getPenetration());
        characterEntity.getTransform().position.add(correction);

        float velocityIntoSurface = controller.getVelocity().dot(normal);
        if (velocityIntoSurface < 0.0f) {
            controller.getVelocity().fma(-velocityIntoSurface, normal);
        }

        float upDot = normal.dot(new Vector3f(0, 1, 0));
        if (upDot >= controller.getGroundNormalThreshold()) {
            controller.setGrounded(true);

            if (controller.getVelocity().y < 0.0f) {
                // Prevents gravity from continuing to accumulate downward velocity while standing on the ground.
                controller.getVelocity().y = 0.0f;
            }
        }
    }

    private float getDynamicFriction(RigidBody bodyA, RigidBody bodyB) {
        if (bodyA == null) {
            return bodyB.getDynamicFriction();
        }

        if (bodyB == null) {
            return bodyA.getDynamicFriction();
        }

        return (float) Math.sqrt(
                bodyA.getDynamicFriction() *
                        bodyB.getDynamicFriction()
        );
    }

    private float getStaticFriction(RigidBody bodyA, RigidBody bodyB) {
        if (bodyA == null) {
            return bodyB.getStaticFriction();
        }

        if (bodyB == null) {
            return bodyA.getStaticFriction();
        }

        return (float) Math.sqrt(
                bodyA.getStaticFriction() *
                        bodyB.getStaticFriction()
        );
    }

    public RaycastHit raycast(Ray ray) {
        RaycastHit closestHit = null;

        /*
        // DEBUG: Draw  TODO: Add DebugMode flag or toggle
        DebugRenderer.drawRay(
                ray.getOrigin(),
                ray.getDirection(),
                ray.getDistance()
        );
        */

        for (Entity entity : scene.getEntities()) {
            Collider collider = entity.getCollider();

            if (collider == null) {
                continue;
            }

            RaycastHit hit = collider.raycast(
                ray.getOrigin(),
                ray.getDirection(),
                ray.getDistance()
            );

            if (hit == null) {
                continue;
            }

            if (closestHit == null ||
                    hit.getDistance() < closestHit.getDistance()) {

                closestHit = hit;
            }
        }

        return closestHit;
    }

    public List<CollisionResult> getCollisions() {
        return Collections.unmodifiableList(collisions);
    }

    public Vector3f getGravity() {
        return gravity;
    }

    public void setGravity(Vector3f gravity) {
        this.gravity.set(gravity);
    }
}
