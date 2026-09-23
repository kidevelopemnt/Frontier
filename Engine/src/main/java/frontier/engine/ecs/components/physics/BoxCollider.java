package frontier.engine.ecs.components.physics;

import frontier.engine.physics.RaycastHit;
import org.joml.Vector3f;

public class BoxCollider extends Collider {
    private Vector3f size = new Vector3f(1, 1, 1);
    private Vector3f center = new Vector3f(0, 0, 0);

    @Override
    public RaycastHit raycast(Vector3f origin, Vector3f direction, float maxDistance) {
        Vector3f position = getEntity().getTransform().position;
        Vector3f halfSize = new Vector3f(size).mul(0.5f);

        Vector3f min = new Vector3f(position).sub(halfSize);
        Vector3f max = new Vector3f(position).add(halfSize);

        float tMin = 0.0f;
        float tMax = maxDistance;

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

            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);

            if (tMin > tMax) {
                return null;
            }
        }

        Vector3f hitLocation = new Vector3f(direction)
                .mul(tMin)
                .add(origin);

        return new RaycastHit(
                getEntity(),
                hitLocation,
                new Vector3f(), // TODO: normal
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
