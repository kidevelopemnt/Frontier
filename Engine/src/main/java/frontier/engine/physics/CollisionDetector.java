package frontier.engine.physics;

import frontier.engine.ecs.components.physics.BoxCollider;
import frontier.engine.ecs.components.physics.Collider;
import org.joml.Vector3f;

public class CollisionDetector {
    public CollisionResult detect(Collider a, Collider b) {
        if (a instanceof BoxCollider boxA && b instanceof BoxCollider boxB) {
            return detectBoxBox(boxA, boxB);
        }

        return null;
    }

    private CollisionResult detectBoxBox(BoxCollider a, BoxCollider b) {
        Vector3f sizeA = a.getWorldSize();
        Vector3f centerA = new Vector3f(a.getEntity().getTransform().getWorldPosition())
                .add(a.getCenter());

        Vector3f sizeB = b.getWorldSize();
        Vector3f centerB = new Vector3f(b.getEntity().getTransform().getWorldPosition())
                .add(b.getCenter());

        Vector3f halfA = new Vector3f(sizeA).mul(0.5f);
        Vector3f halfB = new Vector3f(sizeB).mul(0.5f);

        Vector3f minA = new Vector3f(centerA).sub(halfA);
        Vector3f maxA = new Vector3f(centerA).add(halfA);

        Vector3f minB = new Vector3f(centerB).sub(halfB);
        Vector3f maxB = new Vector3f(centerB).add(halfB);

        // Check for overlapping
        boolean overlapX = minA.x <= maxB.x && maxA.x >= minB.x;
        boolean overlapY = minA.y <= maxB.y && maxA.y >= minB.y;
        boolean overlapZ = minA.z <= maxB.z && maxA.z >= minB.z;

        if (!overlapX || !overlapY || !overlapZ) {
            return null;
        }

        // Collision confirmed, find penetration amount on each axis
        float penetrationX = Math.min(maxA.x, maxB.x)
                - Math.max(minA.x, minB.x);

        float penetrationY = Math.min(maxA.y, maxB.y)
                - Math.max(minA.y, minB.y);

        float penetrationZ = Math.min(maxA.z, maxB.z)
                - Math.max(minA.z, minB.z);

        // Find smallest penetration
        float penetration = penetrationX;
        Vector3f normal = new Vector3f(1, 0, 0);

        if (penetrationY < penetration) {
            penetration = penetrationY;
            normal.set(0, 1, 0);
        }

        if (penetrationZ < penetration) {
            penetration = penetrationZ;
            normal.set(0, 0, 1);
        }

        // Find normal direction
        Vector3f direction = new Vector3f(centerB).sub(centerA);

        if (direction.dot(normal) < 0) {
            normal.negate();
        }

        return new CollisionResult(
                a,
                b,
                normal,
                penetration
        );
    }
}
