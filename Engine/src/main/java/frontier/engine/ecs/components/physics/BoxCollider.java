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

        Matrix3f rotation = new Matrix3f()
                .rotateXYZ(
                        (float) Math.toRadians(transform.rotation.x),
                        (float) Math.toRadians(transform.rotation.y),
                        (float) Math.toRadians(transform.rotation.z)
                );

        Matrix3f inverseRotation = new Matrix3f(rotation).invert();

        Vector3f worldCenter = getWorldCenter();

        Vector3f localOrigin = new Vector3f(origin)
                .sub(worldCenter);

        Vector3f localDirection = new Vector3f(direction);

        inverseRotation.transform(localOrigin);
        inverseRotation.transform(localDirection);

        Vector3f halfSize = getWorldHalfSize();
        Vector3f min = new Vector3f(halfSize).negate();
        Vector3f max = new Vector3f(halfSize);

        float tMin = 0.0f;
        float tMax = maxDistance;
        int hitAxis = -1;
        float hitNormalSign = 0.0f;

        float[] originValues = {localOrigin.x, localOrigin.y, localOrigin.z};
        float[] directionValues = {localDirection.x, localDirection.y, localDirection.z};
        float[] minValues = {min.x, min.y, min.z};
        float[] maxValues = {max.x, max.y, max.z};

        for (int axis = 0; axis < 3; axis++) {
            if (Math.abs(directionValues[axis]) <= 0.000001f) {
                if (originValues[axis] < minValues[axis] ||
                        originValues[axis] > maxValues[axis]) {
                    return null;
                }
                continue;
            }

            float t1 = (minValues[axis] - originValues[axis]) / directionValues[axis];
            float t2 = (maxValues[axis] - originValues[axis]) / directionValues[axis];

            if (t1 > t2) {
                float temp = t1;
                t1 = t2;
                t2 = temp;
            }

            if (t1 > tMin) {
                tMin = t1;
                hitAxis = axis;
                hitNormalSign = directionValues[axis] > 0 ? -1.0f : 1.0f;
            }

            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);

            if (tMin > tMax) {
                return null;
            }
        }

        if (hitAxis == -1) {
            hitAxis = 0;
            hitNormalSign = localDirection.x > 0 ? -1.0f : 1.0f;
        }

        Vector3f localHit = new Vector3f(localDirection)
                .mul(tMin)
                .add(localOrigin);

        Vector3f worldHit = new Vector3f(localHit);
        rotation.transform(worldHit);
        worldHit.add(worldCenter);

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
                this,
                worldHit,
                worldNormal,
                tMin
        );
    }

    @Override
    public Vector3f getWorldCenter() {
        Transform transform = entity.getTransform();

        Vector3f worldCenter = new Vector3f(center)
                .mul(transform.getWorldScale());

        transform.getWorldMatrix().transformPosition(
                new Vector3f(center),
                worldCenter
        );

        return worldCenter;
    }

    @Override
    public Vector3f getWorldSize() {
        return new Vector3f(size).mul(entity.getTransform().getWorldScale());
    }

    public Vector3f getWorldHalfSize() {
        return getWorldSize().mul(0.5f);
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
        this.center.set(center);
    }
}
