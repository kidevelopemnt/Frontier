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
        if (!Float.isFinite(mass) || mass <= 0.0f) {
            throw new IllegalArgumentException(
                    "Rigidbody mass must be finite and greater than zero."
            );
        }

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

    public float getDynamicFriction() {
        return dynamicFriction;
    }

    public void setStaticFriction(float staticFriction) {
        if (!Float.isFinite(staticFriction)) {
            throw new IllegalArgumentException(
                    "Static friction must be finite."
            );
        }

        this.staticFriction = Math.max(0.0f, staticFriction);
    }

    public void setDynamicFriction(float dynamicFriction) {
        if (!Float.isFinite(dynamicFriction)) {
            throw new IllegalArgumentException(
                    "Dynamic friction must be finite."
            );
        }

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
        if (!Float.isFinite(restitution)) {
            throw new IllegalArgumentException(
                    "Restitution must be finite."
            );
        }

        this.restitution = Math.clamp(restitution, 0.0f, 1.0f);
    }

    public float getLinearDamping() {
        return linearDamping;
    }

    public void setLinearDamping(float linearDamping) {
        if (!Float.isFinite(linearDamping)) {
            throw new IllegalArgumentException(
                    "Linear damping must be finite."
            );
        }

        this.linearDamping = Math.max(0.0f, linearDamping);
    }
}
