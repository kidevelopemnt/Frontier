package frontier.engine.ecs.components;

import frontier.engine.Engine;
import frontier.engine.ecs.Entity;
import frontier.engine.graphics.Transform;
import frontier.engine.input.Key;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Map;

public class CameraController extends Component {
    private float sensitivity = 5f;

    private float pitch = 20f;

    private float distance = 10f;

    private float pitchMax = 0f;
    private float pitchMin = -70f;

    private Entity target;

    public void update(float deltaTime) {
        Engine engine = getEngine();
        float mouseX = engine.getInput().getMouse().getDeltaX();
        float mouseY = engine.getInput().getMouse().getDeltaY();

        pitch -= mouseY * sensitivity * deltaTime;
        if (pitch > pitchMax) pitch = pitchMax;
        if (pitch < pitchMin) pitch = pitchMin;

        if (target == null) {
            Transform camTransform = entity.getTransform();
            camTransform.rotation.x = pitch;
            camTransform.rotation.y -= mouseX * sensitivity * deltaTime;
            camTransform.rotation.z = 0f;
        }
    }

    public Vector3f getRenderPosition(float alpha) {
        if (target == null) {
            return entity.getTransform().getInterpolatedPosition(alpha);
        }

        Transform targetTransform = target.getTransform();

        Vector3f targetPosition =
                targetTransform.getInterpolatedPosition(alpha);

        Vector3f targetRotation =
                targetTransform.getInterpolatedRotation(alpha);

        float totalAngle = targetRotation.y;

        float horizontalDistance =
                (float) (-distance * Math.cos(Math.toRadians(pitch)));

        float verticalDistance =
                (float) (distance * Math.sin(Math.toRadians(pitch)));

        float offsetX =
                (float) (
                        horizontalDistance *
                                Math.sin(Math.toRadians(totalAngle))
                );

        float offsetZ =
                (float) (
                        horizontalDistance *
                                Math.cos(Math.toRadians(totalAngle))
                );

        return new Vector3f(
                targetPosition.x - offsetX,
                targetPosition.y - verticalDistance,
                targetPosition.z - offsetZ
        );
    }

    public Vector3f getRenderRotation(float alpha) {
        if (target == null) {
            return entity.getTransform().getInterpolatedRotation(alpha);
        }

        Vector3f targetRotation =
                target.getTransform().getInterpolatedRotation(alpha);

        return new Vector3f(
                pitch,
                targetRotation.y,
                0f
        );
    }

    @Override
    public Map<String, Object> serialize() {
        return Map.of("sensitivity", sensitivity, "distance", distance);
    }

    public void setTarget(Entity target) {
        this.target = target;
    }
}
