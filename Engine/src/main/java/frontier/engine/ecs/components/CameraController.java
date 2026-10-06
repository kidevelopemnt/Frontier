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

    /* public void update(float deltaTime) {
        Engine engine = getEngine();

        float mouseY = engine.getInput().getMouse().getDeltaY();
        pitch -= mouseY * sensitivity * deltaTime;

        if (pitch > pitchLimit) pitch = pitchLimit;
        if (pitch < -pitchLimit) pitch = -pitchLimit;

        float totalAngle = lookAtTarget.getTransform().rotation.y;
        float horizontalDistance = (float) (-distance * Math.cos(Math.toRadians(pitch)));
        float verticalDistance = (float) (distance * Math.sin(Math.toRadians(pitch)));

        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(totalAngle)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(totalAngle)));

        // Set final camera position
        entity.getTransform().position.x = followTarget.getTransform().position.x - offsetX;
        entity.getTransform().position.y = followTarget.getTransform().position.y - verticalDistance;
        entity.getTransform().position.z = followTarget.getTransform().position.z - offsetZ;

        entity.getTransform().setRotation(pitch, totalAngle, 0f);
    } */

    public void update(float deltaTime) {
        Engine engine = getEngine();
        float mouseX = engine.getInput().getMouse().getDeltaX();
        float mouseY = engine.getInput().getMouse().getDeltaY();

        pitch -= mouseY * sensitivity * deltaTime;
        if (pitch > pitchMax) pitch = pitchMax;
        if (pitch < pitchMin) pitch = pitchMin;

        if (target != null) {
            float totalAngle = target.getTransform().rotation.y;
            float horizontalDistance = (float) (-distance * Math.cos(Math.toRadians(pitch)));
            float verticalDistance = (float) (distance * Math.sin(Math.toRadians(pitch)));

            float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(totalAngle)));
            float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(totalAngle)));

            // Set position
            Transform camTransform = entity.getTransform();
            camTransform.position.x = target.getTransform().position.x - offsetX;
            camTransform.position.y = target.getTransform().position.y - verticalDistance;
            camTransform.position.z = target.getTransform().position.z - offsetZ;

            // Store raw degrees safely
            camTransform.rotation.x = pitch;
            camTransform.rotation.y = totalAngle; // 180 flips it to face target
            camTransform.rotation.z = 0f;
        } else {
            Transform camTransform = entity.getTransform();
            camTransform.rotation.x = pitch;
            camTransform.rotation.y -= mouseX * sensitivity * deltaTime;
            camTransform.rotation.z = 0f;
        }
    }

    @Override
    public Map<String, Object> serialize() {
        return Map.of("sensitivity", sensitivity, "distance", distance);
    }

    public void setTarget(Entity target) {
        this.target = target;
    }
}
