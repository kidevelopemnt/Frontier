package frontier.engine.physics;

import frontier.engine.ecs.components.physics.BoxCollider;
import frontier.engine.ecs.components.physics.Collider;
import frontier.engine.ecs.components.physics.SphereCollider;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class CollisionDetector {
    public CollisionResult detect(Collider a, Collider b) {
        CollisionResult result = null;

        if (a instanceof BoxCollider boxA && b instanceof BoxCollider boxB) {
            result = detectBoxBox(boxA, boxB);
        } else if (a instanceof SphereCollider sphereA && b instanceof SphereCollider sphereB) {
            result = detectSphereSphere(sphereA, sphereB);
        } else if (a instanceof BoxCollider boxA && b instanceof SphereCollider sphereB) {
            result = detectBoxSphere(boxA, sphereB);
        } else if (a instanceof SphereCollider sphereA && b instanceof BoxCollider boxB) {
            result = detectBoxSphere(boxB, sphereA);

            if (result != null) {
                result.getNormal().negate();
            }
        }

        if (result == null) {
            return result;
        }

        if (result.getColliderA().isTrigger() || result.getColliderB().isTrigger()) {
            result.setIsTrigger(true);
        }

        return result;
    }

    private CollisionResult detectBoxBox(BoxCollider a, BoxCollider b) {
        // TODO: This might not support rotated boxes...

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

    private CollisionResult detectSphereSphere(
            SphereCollider a,
            SphereCollider b
    ) {
        Vector3f centerA = a.getWorldCenter();
        Vector3f centerB = b.getWorldCenter();

        float radiusA = a.getWorldRadius();
        float radiusB = b.getWorldRadius();

        Vector3f direction = new Vector3f(centerB)
                .sub(centerA);

        float distanceSquared = direction.lengthSquared();

        float combinedRadius = radiusA + radiusB;

        if (distanceSquared > combinedRadius * combinedRadius) {
            return null;
        }

        float distance = (float) Math.sqrt(distanceSquared);

        Vector3f normal;

        if (distance > 0.000001f) {
            normal = direction.div(distance);
        } else {
            // The spheres are occupying the exact same position.
            normal = new Vector3f(1, 0, 0);
            distance = 0.0f;
        }

        float penetration = combinedRadius - distance;

        return new CollisionResult(
                a,
                b,
                normal,
                penetration
        );
    }

    private CollisionResult detectBoxSphere(
            BoxCollider box,
            SphereCollider sphere
    ) {
        Vector3f boxHalfSize = new Vector3f(box.getWorldSize()).mul(0.5f);
        Vector3f boxMin = new Vector3f(box.getWorldCenter()).sub(boxHalfSize);
        Vector3f boxMax = new Vector3f(box.getWorldCenter()).add(boxHalfSize);

        Vector3f sphereCenter = sphere.getWorldCenter();

        Vector3f closestPoint = new Vector3f();
        closestPoint.x = Math.clamp(sphereCenter.x, boxMin.x, boxMax.x);
        closestPoint.y = Math.clamp(sphereCenter.y, boxMin.y, boxMax.y);
        closestPoint.z = Math.clamp(sphereCenter.z, boxMin.z, boxMax.z);

        Vector3f direction = new Vector3f(sphereCenter).sub(closestPoint);
        float distanceSquared = direction.lengthSquared();
        float radiusSquared = sphere.getWorldRadius() * sphere.getWorldRadius();

        if (distanceSquared > radiusSquared) {
            return null;  // No collision
        }

        float distance = (float) Math.sqrt(distanceSquared);
        Vector3f normal;
        float penetration;

        if (distance < 0.0000001f) {
            float posX = boxMax.x - sphereCenter.x;
            float negX = sphereCenter.x - boxMin.x;
            float posY = boxMax.y - sphereCenter.y;
            float negY = sphereCenter.y - boxMin.y;
            float posZ = boxMax.z - sphereCenter.z;
            float negZ = sphereCenter.z - boxMin.z;

            float nearestDistance = posX;
            normal = new Vector3f(1, 0, 0);

            if (negX < nearestDistance) {
                nearestDistance = negX;
                normal.set(-1, 0, 0);
            }
            if (posY < nearestDistance) {
                nearestDistance = posY;
                normal.set(0, 1, 0);
            }
            if (negY < nearestDistance) {
                nearestDistance = negY;
                normal.set(0, -1, 0);
            }
            if (posZ < nearestDistance) {
                nearestDistance = posZ;
                normal.set(0, 0, 1);
            }
            if (negZ < nearestDistance) {
                nearestDistance = negZ;
                normal.set(0, 0, -1);
            }

            penetration = sphere.getWorldRadius() + nearestDistance;

        } else {
            normal = new Vector3f(direction).div(distance);
            penetration = sphere.getWorldRadius() - distance;
        }

        return new CollisionResult(
                box,
                sphere,
                normal,
                penetration
        );
    }
}
