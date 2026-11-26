package frontier.engine.physics;

import frontier.engine.ecs.components.physics.BoxCollider;
import frontier.engine.ecs.components.physics.CapsuleCollider;
import frontier.engine.ecs.components.physics.Collider;
import frontier.engine.ecs.components.physics.SphereCollider;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class CollisionDetector {
    private final CollisionLayerManager collisionLayers;

    public CollisionDetector(CollisionLayerManager collisionLayerManager) {
        this.collisionLayers = collisionLayerManager;
    }

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
                result = new CollisionResult(
                        sphereA,
                        boxB,
                        new Vector3f(result.getNormal()).negate(),
                        result.getPenetration()
                );
            }
        } else if (a instanceof SphereCollider sphere && b instanceof CapsuleCollider capsule) {
            result = detectSphereCapsule(sphere, capsule);
        } else if (a instanceof CapsuleCollider capsule && b instanceof SphereCollider sphere) {
            result = detectSphereCapsule(sphere, capsule);

            if (result != null) {
                result = new CollisionResult(
                        capsule,
                        sphere,
                        new Vector3f(result.getNormal()).negate(),
                        result.getPenetration()
                );
            }
        } else if (a instanceof CapsuleCollider capsuleA && b instanceof CapsuleCollider capsuleB) {
            result = detectCapsuleCapsule(capsuleA, capsuleB);
        } else if (a instanceof BoxCollider box && b instanceof CapsuleCollider capsule) {
            result = detectBoxCapsule(box, capsule);
        } else if (a instanceof CapsuleCollider capsule && b instanceof BoxCollider box) {
            result = detectBoxCapsule(box, capsule);

            if (result != null) {
                result = new CollisionResult(
                        capsule,
                        box,
                        new Vector3f(result.getNormal()).negate(),
                        result.getPenetration()
                );
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
        // FIXME: This does not support rotated boxes...

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
        float penetrationX = Math.min(
                maxA.x - minB.x,
                maxB.x - minA.x
        );

        float penetrationY = Math.min(
                maxA.y - minB.y,
                maxB.y - minA.y
        );

        float penetrationZ = Math.min(
                maxA.z - minB.z,
                maxB.z - minA.z
        );

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

        Vector3f closestPoint = closestPointOnBox(sphereCenter, boxMin, boxMax);

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

    private CollisionResult detectSphereCapsule(SphereCollider sphere, CapsuleCollider capsule) {
        Vector3f sphereCenter = sphere.getWorldCenter();

        Vector3f segmentStart = capsule.getSegmentStart();
        Vector3f segmentEnd = capsule.getSegmentEnd();

        Vector3f closestPoint = closestPointOnSegment(
                sphereCenter,
                segmentStart,
                segmentEnd
        );

        Vector3f direction = new Vector3f(closestPoint).sub(sphereCenter);
        float distanceSquared = direction.lengthSquared();

        float sphereRadius = sphere.getWorldRadius();
        float capsuleRadius = capsule.getWorldRadius();

        float combinedRadius = sphereRadius + capsuleRadius;

        if (distanceSquared > combinedRadius * combinedRadius) {
            return null;
        }

        float distance = (float) Math.sqrt(distanceSquared);

        Vector3f normal;

        if (distance > 0.000001f) {
            normal = direction.div(distance);
        } else {
            normal = getFallbackNormal(sphere, capsule);
        }

        float penetration = combinedRadius - distance;

        return new CollisionResult(
                sphere,
                capsule,
                normal,
                penetration
        );
    }

    private CollisionResult detectCapsuleCapsule(CapsuleCollider capsuleA, CapsuleCollider capsuleB) {
        Vector3f[] closestPoints = closestPointsBetweenSegments(
                capsuleA.getSegmentStart(),
                capsuleA.getSegmentEnd(),
                capsuleB.getSegmentStart(),
                capsuleB.getSegmentEnd()
        );

        Vector3f direction = new Vector3f(closestPoints[1])
                .sub(closestPoints[0]);

        float distanceSquared = direction.lengthSquared();

        float combinedRadius = capsuleA.getWorldRadius() + capsuleB.getWorldRadius();

        if (distanceSquared > combinedRadius * combinedRadius) {
            return null;
        }

        float distance = (float) Math.sqrt(distanceSquared);

        Vector3f normal;

        if (distance > 0.000001f) {
            normal = direction.div(distance);
        } else {
            normal = getFallbackNormal(capsuleA, capsuleB);
        }

        return new CollisionResult(
                capsuleA,
                capsuleB,
                normal,
                combinedRadius - distance
        );
    }

    private CollisionResult detectBoxCapsule(
            BoxCollider box,
            CapsuleCollider capsule
    ) {
        Vector3f halfSize = new Vector3f(box.getWorldSize()).mul(0.5f);

        Vector3f boxMin = new Vector3f(box.getWorldCenter()).sub(halfSize);
        Vector3f boxMax = new Vector3f(box.getWorldCenter()).add(halfSize);

        Vector3f segmentStart = capsule.getSegmentStart();
        Vector3f segmentEnd = capsule.getSegmentEnd();

        Vector3f closestSegmentPoint = closestPointOnSegmentToBox(
                segmentStart,
                segmentEnd,
                boxMin,
                boxMax
        );

        Vector3f closestBoxPoint = closestPointOnBox(
                closestSegmentPoint,
                boxMin,
                boxMax
        );

        Vector3f direction = new Vector3f(closestSegmentPoint)
                .sub(closestBoxPoint);

        float distanceSquared = direction.lengthSquared();
        float radius = capsule.getWorldRadius();

        if (distanceSquared > radius * radius) {
            return null;
        }

        float distance = (float) Math.sqrt(distanceSquared);

        Vector3f normal;
        float penetration;

        if (distance < 0.000001f) {
            return createBoxCapsuleInsideResult(
                    box,
                    capsule,
                    segmentStart,
                    segmentEnd,
                    radius
            );
        } else {
            normal = direction.div(distance);
            penetration = radius - distance;
        }

        return new CollisionResult(
                box,
                capsule,
                normal,
                penetration
        );
    }

    private Vector3f closestPointOnSegment(
            Vector3f point,
            Vector3f start,
            Vector3f end
    ) {
        Vector3f segment = new Vector3f(end).sub(start);
        float lengthSquared = segment.lengthSquared();

        if (lengthSquared < 0.000001f) {
            return new Vector3f(start);
        }

        float t = new Vector3f(point)
                .sub(start)
                .dot(segment) / lengthSquared;

        t = Math.clamp(t, 0.0f, 1.0f);

        return new Vector3f(start).add(segment.mul(t));
    }

    private Vector3f[] closestPointsBetweenSegments(
            Vector3f p1,
            Vector3f q1,
            Vector3f p2,
            Vector3f q2
    ) {
        Vector3f d1 = new Vector3f(q1).sub(p1);
        Vector3f d2 = new Vector3f(q2).sub(p2);
        Vector3f r = new Vector3f(p1).sub(p2);

        float a = d1.dot(d1);
        float e = d2.dot(d2);
        float f = d2.dot(r);

        float s;
        float t;

        if (a <= 0.000001f && e <= 0.000001f) {
            return new Vector3f[] {
                    new Vector3f(p1),
                    new Vector3f(p2)
            };
        }

        if (a <= 0.000001f) {
            s = 0.0f;
            t = Math.clamp(f / e, 0.0f, 1.0f);
        } else {
            float c = d1.dot(r);

            if (e <= 0.000001f) {
                t = 0.0f;
                s = Math.clamp(-c / a, 0.0f, 1.0f);
            } else {
                float b = d1.dot(d2);
                float denominator = a * e - b * b;

                if (Math.abs(denominator) > 0.000001f) {
                    s = Math.clamp(
                            (b * f - c * e) / denominator,
                            0.0f,
                            1.0f
                    );
                } else {
                    s = 0.0f;
                }

                t = (b * s + f) / e;

                if (t < 0.0f) {
                    t = 0.0f;
                    s = Math.clamp(-c / a, 0.0f, 1.0f);
                } else if (t > 1.0f) {
                    t = 1.0f;
                    s = Math.clamp((b - c) / a, 0.0f, 1.0f);
                }
            }
        }

        Vector3f closestA = new Vector3f(p1).add(new Vector3f(d1).mul(s));
        Vector3f closestB = new Vector3f(p2).add(new Vector3f(d2).mul(t));

        return new Vector3f[] { closestA, closestB };
    }

    private Vector3f closestPointOnBox(
            Vector3f point,
            Vector3f min,
            Vector3f max
    ) {
        return new Vector3f(
                Math.clamp(point.x, min.x, max.x),
                Math.clamp(point.y, min.y, max.y),
                Math.clamp(point.z, min.z, max.z)
        );
    }

    private Vector3f closestPointOnSegmentToBox(
            Vector3f start,
            Vector3f end,
            Vector3f min,
            Vector3f max
    ) {
        Vector3f segment = new Vector3f(end).sub(start);

        float low = 0.0f;
        float high = 1.0f;

        for (int i = 0; i < 40; i++) {
            float t1 = (2.0f * low + high) / 3.0f;
            float t2 = (low + 2.0f * high) / 3.0f;

            Vector3f point1 = new Vector3f(start)
                    .add(new Vector3f(segment).mul(t1));

            Vector3f point2 = new Vector3f(start)
                    .add(new Vector3f(segment).mul(t2));

            float distance1 = point1.distanceSquared(
                    closestPointOnBox(point1, min, max)
            );

            float distance2 = point2.distanceSquared(
                    closestPointOnBox(point2, min, max)
            );

            if (distance1 < distance2) {
                high = t2;
            } else {
                low = t1;
            }
        }

        float t = (low + high) * 0.5f;

        return new Vector3f(start).add(segment.mul(t));
    }

    private CollisionResult createBoxCapsuleInsideResult(
            BoxCollider box,
            CapsuleCollider capsule,
            Vector3f segmentStart,
            Vector3f segmentEnd,
            float radius
    ) {
        Vector3f boxMin = new Vector3f(box.getWorldCenter())
                .sub(new Vector3f(box.getWorldSize()).mul(0.5f));

        Vector3f boxMax = new Vector3f(box.getWorldCenter())
                .add(new Vector3f(box.getWorldSize()).mul(0.5f));

        // Capsule bounds, including its radius.
        Vector3f capsuleMin = new Vector3f(
                Math.min(segmentStart.x, segmentEnd.x) - radius,
                Math.min(segmentStart.y, segmentEnd.y) - radius,
                Math.min(segmentStart.z, segmentEnd.z) - radius
        );

        Vector3f capsuleMax = new Vector3f(
                Math.max(segmentStart.x, segmentEnd.x) + radius,
                Math.max(segmentStart.y, segmentEnd.y) + radius,
                Math.max(segmentStart.z, segmentEnd.z) + radius
        );

        float[] depths = {
                capsuleMax.x - boxMin.x,
                boxMax.x - capsuleMin.x,
                capsuleMax.y - boxMin.y,
                boxMax.y - capsuleMin.y,
                capsuleMax.z - boxMin.z,
                boxMax.z - capsuleMin.z
        };

        Vector3f[] normals = {
                new Vector3f(-1, 0, 0),
                new Vector3f( 1, 0, 0),
                new Vector3f(0, -1, 0),
                new Vector3f(0,  1, 0),
                new Vector3f(0, 0, -1),
                new Vector3f(0, 0,  1)
        };

        int smallestIndex = 0;

        for (int i = 1; i < depths.length; i++) {
            if (depths[i] < depths[smallestIndex]) {
                smallestIndex = i;
            }
        }

        return new CollisionResult(
                box,
                capsule,
                normals[smallestIndex],
                depths[smallestIndex]
        );
    }

    private Vector3f getFallbackNormal(Collider a, Collider b) {
        Vector3f direction = new Vector3f(
                b.getEntity().getTransform().getWorldPosition()
        ).sub(a.getEntity().getTransform().getWorldPosition());

        if (direction.lengthSquared() > 1e-6f) {
            return direction.normalize();
        }

        return new Vector3f(0, 1, 0);
    }
}
