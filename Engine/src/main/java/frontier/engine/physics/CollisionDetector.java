package frontier.engine.physics;

import frontier.engine.ecs.components.physics.BoxCollider;
import frontier.engine.ecs.components.physics.Collider;
import frontier.engine.graphics.Transform;
import org.joml.Vector3f;

public final class CollisionDetector {

    private static final float EPSILON = 0.000001f;

    private CollisionDetector() {
    }

    public static CollisionResult check(Collider a, Collider b) {
        if (a instanceof BoxCollider boxA && b instanceof BoxCollider boxB) {
            return boxVsBox(boxA, boxB);
        }

        return null;
    }

    /**
     * Separating Axis Theorem for two oriented boxes.
     *
     * The returned normal points from A toward B.
     */
    private static CollisionResult boxVsBox(BoxCollider a, BoxCollider b) {
        Vector3f centerA = a.getWorldCenter();
        Vector3f centerB = b.getWorldCenter();

        Vector3f halfA = a.getWorldHalfSize();
        Vector3f halfB = b.getWorldHalfSize();

        Vector3f[] axesA = getAxes(a.getEntity().getTransform());
        Vector3f[] axesB = getAxes(b.getEntity().getTransform());

        // Rotation matrix: dot(Ai, Bj).
        float[][] rotation = new float[3][3];
        float[][] absRotation = new float[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                rotation[i][j] = axesA[i].dot(axesB[j]);
                absRotation[i][j] = Math.abs(rotation[i][j]) + EPSILON;
            }
        }

        Vector3f centerDelta = new Vector3f(centerB).sub(centerA);

        // Express the center delta in A's coordinate system.
        float[] translation = new float[] {
                centerDelta.dot(axesA[0]),
                centerDelta.dot(axesA[1]),
                centerDelta.dot(axesA[2])
        };

        float minimumPenetration = Float.POSITIVE_INFINITY;
        Vector3f minimumAxis = null;

        // Test A's local axes.
        for (int i = 0; i < 3; i++) {
            float radiusA = getComponent(halfA, i);
            float radiusB =
                    halfB.x * absRotation[i][0] +
                    halfB.y * absRotation[i][1] +
                    halfB.z * absRotation[i][2];

            float distance = Math.abs(translation[i]);
            float overlap = radiusA + radiusB - distance;

            if (overlap < 0.0f) {
                return null;
            }

            if (overlap < minimumPenetration) {
                minimumPenetration = overlap;
                minimumAxis = new Vector3f(axesA[i]);
            }
        }

        // Test B's local axes.
        for (int j = 0; j < 3; j++) {
            float radiusA =
                    halfA.x * absRotation[0][j] +
                    halfA.y * absRotation[1][j] +
                    halfA.z * absRotation[2][j];

            float radiusB = getComponent(halfB, j);
            float distance = Math.abs(centerDelta.dot(axesB[j]));
            float overlap = radiusA + radiusB - distance;

            if (overlap < 0.0f) {
                return null;
            }

            if (overlap < minimumPenetration) {
                minimumPenetration = overlap;
                minimumAxis = new Vector3f(axesB[j]);
            }
        }

        // Test the nine cross-product axes.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Vector3f axis = new Vector3f(axesA[i]).cross(axesB[j]);
                float axisLengthSquared = axis.lengthSquared();

                // Parallel axes do not create a useful SAT axis.
                if (axisLengthSquared < EPSILON) {
                    continue;
                }

                axis.normalize();

                float radiusA =
                        halfA.x * Math.abs(axis.dot(axesA[0])) +
                        halfA.y * Math.abs(axis.dot(axesA[1])) +
                        halfA.z * Math.abs(axis.dot(axesA[2]));

                float radiusB =
                        halfB.x * Math.abs(axis.dot(axesB[0])) +
                        halfB.y * Math.abs(axis.dot(axesB[1])) +
                        halfB.z * Math.abs(axis.dot(axesB[2]));

                float distance = Math.abs(centerDelta.dot(axis));
                float overlap = radiusA + radiusB - distance;

                if (overlap < 0.0f) {
                    return null;
                }

                if (overlap < minimumPenetration) {
                    minimumPenetration = overlap;
                    minimumAxis = axis;
                }
            }
        }

        if (minimumAxis == null) {
            return null;
        }

        // Always return the normal from A toward B.
        if (minimumAxis.dot(centerDelta) < 0.0f) {
            minimumAxis.negate();
        }

        return new CollisionResult(
                a,
                b,
                minimumAxis,
                minimumPenetration
        );
    }

    private static Vector3f[] getAxes(Transform transform) {
        return new Vector3f[] {
                transform.getRight(),
                transform.getUp(),
                transform.getForward()
        };
    }

    private static float getComponent(Vector3f vector, int index) {
        return switch (index) {
            case 0 -> vector.x;
            case 1 -> vector.y;
            case 2 -> vector.z;
            default -> throw new IllegalArgumentException("Invalid vector component: " + index);
        };
    }
}
