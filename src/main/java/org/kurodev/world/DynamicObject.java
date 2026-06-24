package org.kurodev.world;

import org.kurodev.world.shape.Shape;

import java.util.Objects;

/**
 * A movable world object with double-precision position and rotation.
 * <p>
 * Dynamic objects can translate, rotate, and run a per-frame behavior. Their
 * local shape is transformed by the attached {@link ObjectTransform}. The
 * transform's {@linkplain ObjectTransform#setPivot(WorldPosition) pivot}
 * controls the local point the object rotates around. If the pivot is
 * {@code (0, 0, 0)}, the shape rotates around its local origin. If the pivot is
 * the shape's center, the shape spins in place around its center.
 */
public final class DynamicObject extends AbstractWorldObject {
    private final ObjectTransform transform = new ObjectTransform(this::markWorldWireframeDirty);
    private WorldObjectBehavior behavior;

    public DynamicObject(Shape shape) {
        this("object", WorldPosition.ORIGIN, shape);
    }

    public DynamicObject(WorldPosition position, Shape shape) {
        this("object", position, shape);
    }

    public DynamicObject(String name, WorldPosition position, Shape shape) {
        super(name, shape);
        transform.setPosition(Objects.requireNonNull(position, "position"));
    }

    public ObjectTransform transform() {
        return transform;
    }

    /**
     * Optional callback executed after the transform updates each frame.
     * The behavior may translate or rotate the object by calling methods on
     * {@link #transform()}.
     */
    public WorldObjectBehavior behavior() {
        return behavior;
    }

    public void setBehavior(WorldObjectBehavior behavior) {
        this.behavior = behavior;
    }

    public WorldPosition position() {
        return transform.position();
    }

    public void setPosition(WorldPosition position) {
        transform.setPosition(Objects.requireNonNull(position, "position"));
    }

    public void translate(double dx, double dy, double dz) {
        transform.translate(dx, dy, dz);
    }

    public void setPivot(WorldPosition pivot) {
        transform.setPivot(pivot);
    }

    public void setRotation(double yawRadians, double pitchRadians, double rollRadians) {
        transform.setRotation(yawRadians, pitchRadians, rollRadians);
    }

    public void rotate(double yawDeltaRadians, double pitchDeltaRadians, double rollDeltaRadians) {
        transform.rotate(yawDeltaRadians, pitchDeltaRadians, rollDeltaRadians);
    }

    @Override
    public void update(WorldManager world, double deltaSeconds) {
        transform.update(deltaSeconds);
        if (behavior != null) {
            behavior.update(this, world, deltaSeconds);
        }
    }

    @Override
    protected WorldLine translateLine(WorldLine localLine) {
        return transform.transform(localLine);
    }
}
