package frontier.engine.ecs.components.physics;

import frontier.engine.graphics.Transform;
import frontier.engine.physics.RaycastHit;
import org.joml.Matrix3f;
import org.joml.Vector3f;

public class BoxCollider extends Collider {
    private Vector3f size = new Vector3f(1, 1, 1);
    private Vector3f center = new Vector3f(0, 0, 0);

    @Override
    public RaycastHit raycast(Vector3f origin, Vector3f direction, float maxDistance) {
        Transform transform = entity.getTransform();
        Matrix3f inverseRotation = new Matrix3f()
                .rotateXYZ(
                        -transform.rotation.x,
                        -transform.rotation.y,
                        -transform.rotation.z
                );

        Vector3f localOrigin = new Vector3f(origin)
                .sub(transform.position)
                .sub(center);

        Vector3f localDirection = new Vector3f(direction);

        inverseRotation.transform(localOrigin);
        inverseRotation.transform(localDirection);

        Vector3f halfSize = new Vector3f(size).mul(0.5f);
        Vector3f min = new Vector3f(halfSize).negate();
        Vector3f max = new Vector3f(halfSize);

        float tMin = 0.0f;
        float tMax = maxDistance;
        int hitAxis = -1;
        float hitNormalSign = 0.0f;

        // X axis
        if (direction.x == 0.0f) {
            if (origin.x < min.x || origin.x > max.x) {
                return null;
            }
        } else {
            float t1 = (min.x - origin.x) / direction.x;
            float t2 = (max.x - origin.x) / direction.x;

            if (t1 > t2) {
                float temp = t1;
                t1 = t2;
                t2 = temp;
            }

            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);

            if (tMin > tMax) {
                return null;
            }
        }

        // Y axis
        if (direction.y == 0.0f) {
            if (origin.y < min.y || origin.y > max.y) {
                return null;
            }
        } else {
            float t1 = (min.y - origin.y) / direction.y;
            float t2 = (max.y - origin.y) / direction.y;

            if (t1 > t2) {
                float temp = t1;
                t1 = t2;
                t2 = temp;
            }

            if (t1 > tMin) {
                tMin = t1;
                hitAxis = 1;
                hitNormalSign = localDirection.y > 0 ? -1.0f : 1.0f;
            }

            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);

            if (tMin > tMax) {
                return null;
            }
        }

        // Z axis
        if (direction.z == 0.0f) {
            if (origin.z < min.z || origin.z > max.z) {
                return null;
            }
        } else {
            float t1 = (min.z - origin.z) / direction.z;
            float t2 = (max.z - origin.z) / direction.z;

            if (t1 > t2) {
                float temp = t1;
                t1 = t2;
                t2 = temp;
            }

            if (t1 > tMin) {
                tMin = t1;
                hitAxis = 2;
                hitNormalSign = localDirection.z > 0 ? -1.0f : 1.0f;
            }

            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);

            if (tMin > tMax) {
                return null;
            }
        }

        Vector3f localHit = new Vector3f(localDirection)
                .mul(tMin)
                .add(localOrigin);

        Matrix3f rotation = new Matrix3f()
                .rotateXYZ(
                        transform.rotation.x,
                        transform.rotation.y,
                        transform.rotation.z
                );

        Vector3f worldHit = new Vector3f(localHit);

        rotation.transform(worldHit);

        worldHit
                .add(center)
                .add(transform.position);

        Vector3f localNormal = new Vector3f();

        switch (hitAxis) {
            case 0 -> localNormal.x = hitNormalSign;
            case 1 -> localNormal.y = hitNormalSign;
            case 2 -> localNormal.z = hitNormalSign;
        }

        Vector3f worldNormal = new Vector3f(localNormal);

        rotation.transform(worldNormal);

        worldNormal.normalize();

        return new RaycastHit(
                getEntity(),
                worldHit,
                worldNormal,
                tMin
        );
    }

    public Vector3f getSize() {
        return size;
    }

    public void setSize(Vector3f size) {
        this.size.set(size);
    }

    public Vector3f getCenter() {
        return center;
    }

    public void setCenter(Vector3f center) {
        this.center = center;
    }
}
