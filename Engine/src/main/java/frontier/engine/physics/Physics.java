package frontier.engine.physics;

import frontier.engine.Engine;
import frontier.engine.ecs.Entity;
import frontier.engine.ecs.components.physics.Collider;
import frontier.engine.graphics.DebugRenderer;
import frontier.engine.scene.Scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Physics {
    private final Engine engine;
    private final CollisionDetector collisionDetector;

    private Scene scene;

    private final List<CollisionResult> collisions = new ArrayList<>();

    public Physics(Engine engine, Scene scene) {
        this.engine = engine;
        this.scene = scene;

        collisionDetector = new CollisionDetector();
    }

    public void update() {
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
                    // Collision detected
                    collisions.add(result);
                    System.out.println("Collision " + a.getEntity().getName() + " " + b.getEntity().getName());
                }
            }
        }
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
}
