package frontier.engine.ecs.components;

import frontier.engine.Engine;
import frontier.engine.ecs.Entity;
import frontier.engine.input.Key;
import org.joml.Vector3f;

import java.util.Map;

public class CameraController extends Component {
    private float sensitivity = 0.1f;

    private float pitch = 20f;
    private float yaw = 0f;

    private float distance = -5f;
    private float height = 2f;

    private float pitchLimit = 89f;

    private Entity lookAtTarget;
    private Entity followTarget;

    public void update(float deltaTime) {
        Engine engine = getEngine();

        float mouseX = engine.getInput().getMouse().getDeltaX();
        float mouseY = engine.getInput().getMouse().getDeltaY();

        yaw -= mouseX * sensitivity * deltaTime;
        pitch -= mouseY * sensitivity * deltaTime;

        pitch = Math.max(
                -pitchLimit,
                Math.min(pitchLimit, pitch)
        );

        if (followTarget != null) {
            updateOrbit();
        }
    }

    private void updateOrbit() {
        Vector3f targetPosition =
                followTarget.getTransform().getWorldPosition();

        float yawRadians = (float) Math.toRadians(yaw);
        float pitchRadians = (float) Math.toRadians(pitch);

        float horizontalDistance =
                distance * (float) Math.cos(pitchRadians);

        float verticalDistance =
                distance * (float) Math.sin(pitchRadians);

        float x =
                (float) Math.sin(yawRadians) * horizontalDistance;

        float z =
                (float) Math.cos(yawRadians) * horizontalDistance;

        Vector3f cameraPosition = new Vector3f(
                targetPosition.x - x,
                targetPosition.y + height + verticalDistance,
                targetPosition.z - z
        );

        entity.getTransform().setWorldPosition(cameraPosition);

        entity.getTransform().lookAt(targetPosition);
    }

    @Override
    public Map<String, Object> serialize() {
        return Map.of("sensitivity", sensitivity, "distance", distance, "height", height);
    }

    public void setLookAtTarget(Entity target) {
        lookAtTarget = target;
    }

    public void setFollowTarget(Entity target) {
        followTarget = target;
    }
}
