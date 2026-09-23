package frontier.engine.physics;

import frontier.engine.ecs.Entity;
import frontier.engine.ecs.components.physics.Collider;
import frontier.engine.scene.Scene;

public class Physics {
    private Scene scene;

    public Physics(Scene scene) {
        this.scene = scene;
    }

    public RaycastHit raycast(Ray ray) {
        RaycastHit closestHit = null;

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
}
