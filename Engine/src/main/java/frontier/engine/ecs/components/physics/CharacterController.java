package frontier.engine.ecs.components.physics;

import frontier.engine.ecs.components.Component;
import org.joml.Vector3f;

import java.util.Map;

public class CharacterController extends Component {
    private float moveSpeed = 5.0f;
    private float jumpVelocity = 5.0f;
    private float gravity = -9.8f;
    private Vector3f moveDirection = new Vector3f();

    private final Vector3f velocity = new Vector3f();
    private boolean grounded;
    private float groundNormalThreshold = 0.7f;

    public void applyGravity(float deltaTime) {
        if (!grounded) {
            velocity.y += gravity * deltaTime;
        }
    }

    public void jump() {
        if (!grounded) {
            return;
        }

        velocity.y = jumpVelocity;
        grounded = false;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public float getJumpVelocity() {
        return jumpVelocity;
    }

    public void setJumpVelocity(float jumpVelocity) {
        this.jumpVelocity = jumpVelocity;
    }

    public float getGravity() {
        return gravity;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public void setMoveDirection(Vector3f movement) {
        this.moveDirection.set(movement);
    }

    public Vector3f getMoveDirection() {
        return moveDirection;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public boolean isGrounded() {
        return grounded;
    }

    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
    }

    public Vector3f getDesiredVelocity() {
        if (moveDirection.lengthSquared() == 0.0f) {
            return new Vector3f();
        }

        return new Vector3f(moveDirection)
                .normalize()
                .mul(moveSpeed);
    }

    @Override
    public Map<String, Object> serialize() {
        return Map.of();
    }

    public float getGroundNormalThreshold() {
        return groundNormalThreshold;
    }

    public void setGroundNormalThreshold(float groundNormalThreshold) {
        this.groundNormalThreshold = groundNormalThreshold;
    }
}
