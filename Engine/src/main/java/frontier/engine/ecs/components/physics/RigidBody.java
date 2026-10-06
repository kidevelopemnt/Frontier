package frontier.engine.ecs.components.physics;

import frontier.engine.ecs.components.Component;
import org.joml.Vector3f;

import java.util.Map;

public class RigidBody extends Component {
    private float mass = 1.0f;
    private boolean useGravity = true;
    private float restitution = 0.0f;    // 0 = no bounce; 1 = perfectly elastic; 0.5 = some bounce

    private float staticFriction = 0.5f;
    private float dynamicFriction = 0.3f;
    private float linearDamping = 0.0f;

    private Vector3f force = new Vector3f();
    private Vector3f velocity = new Vector3f();

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public boolean usesGravity() {
        return useGravity;
    }

    public void setUseGravity(boolean useGravity) {
        this.useGravity = useGravity;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity.set(velocity);
    }

    public void addForce(Vector3f force) {
        this.force.add(force);
    }

    public Vector3f getForce() {
        return force;
    }

    public void clearForces() {
        force.zero();
    }

    public void addImpulse(Vector3f impulse) {
        velocity.fma(1.0f / mass, impulse);
    }

    public float getStaticFriction() {
        return staticFriction;
    }

    public void setStaticFriction(float staticFriction) {
        this.staticFriction = Math.max(0.0f, staticFriction);
    }

    public float getDynamicFriction() {
        return dynamicFriction;
    }

    public void setDynamicFriction(float dynamicFriction) {
        this.dynamicFriction = Math.max(0.0f, dynamicFriction);
    }

    @Override
    public Map<String, Object> serialize() {
        return Map.of();
    }

    public float getRestitution() {
        return restitution;
    }

    public void setRestitution(float restitution) {
        this.restitution = restitution;
    }

    public float getLinearDamping() {
        return linearDamping;
    }

    public void setLinearDamping(float linearDamping) {
        this.linearDamping = linearDamping;
    }
}
