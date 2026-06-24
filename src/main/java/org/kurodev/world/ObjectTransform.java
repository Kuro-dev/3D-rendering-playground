package org.kurodev.world;

/**
 * Local transform attached to a {@link DynamicObject}.
 * <p>
 * The transform stores the object's world-space anchor point plus Euler
 * rotation. Local shape points are rotated around a local pivot point first and
 * then translated by {@link #position()}.
 */
public final class ObjectTransform {
    private final Runnable dirtyListener;
    private WorldPosition position = WorldPosition.ORIGIN;
    private WorldPosition pivot = WorldPosition.ORIGIN;
    private double yawRadians;
    private double pitchRadians;
    private double rollRadians;
    private double yawVelocityRadians;
    private double pitchVelocityRadians;
    private double rollVelocityRadians;

    public ObjectTransform() {
        this(() -> { });
    }

    public ObjectTransform(Runnable dirtyListener) {
        this.dirtyListener = dirtyListener == null ? () -> { } : dirtyListener;
    }

    public WorldPosition position() {
        return position;
    }

    /**
     * Sets the object's world-space anchor point.
     * <p>
     * This does not change the shape geometry itself. It moves the whole object
     * in world space.
     */
    public void setPosition(WorldPosition position) {
        this.position = position == null ? WorldPosition.ORIGIN : position;
        dirtyListener.run();
    }

    /**
     * Moves the object by a world-space delta.
     */
    public void translate(double dx, double dy, double dz) {
        position = position.add(dx, dy, dz);
        dirtyListener.run();
    }

    public WorldPosition pivot() {
        return pivot;
    }

    /**
     * Sets the local point the object rotates around.
     * <p>
     * The pivot is expressed in the object's local shape coordinates. For a
     * box built from a local corner at {@code (0, 0, 0)} with size
     * {@code (2, 2, 2)}, a pivot of {@code (1, 1, 1)} rotates the box around
     * its center. A pivot of {@code (0, 0, 0)} rotates around the local origin
     * corner instead.
     */
    public void setPivot(WorldPosition pivot) {
        this.pivot = pivot == null ? WorldPosition.ORIGIN : pivot;
        dirtyListener.run();
    }

    public double yawRadians() {
        return yawRadians;
    }

    public double pitchRadians() {
        return pitchRadians;
    }

    public double rollRadians() {
        return rollRadians;
    }

    /**
     * Replaces the current absolute rotation.
     */
    public void setRotation(double yawRadians, double pitchRadians, double rollRadians) {
        this.yawRadians = yawRadians;
        this.pitchRadians = pitchRadians;
        this.rollRadians = rollRadians;
        dirtyListener.run();
    }

    /**
     * Adds a delta rotation in radians.
     */
    public void rotate(double yawDeltaRadians, double pitchDeltaRadians, double rollDeltaRadians) {
        yawRadians += yawDeltaRadians;
        pitchRadians += pitchDeltaRadians;
        rollRadians += rollDeltaRadians;
        dirtyListener.run();
    }

    /**
     * Sets a constant angular velocity in radians per second.
     */
    public void setAngularVelocity(double yawVelocityRadians, double pitchVelocityRadians, double rollVelocityRadians) {
        this.yawVelocityRadians = yawVelocityRadians;
        this.pitchVelocityRadians = pitchVelocityRadians;
        this.rollVelocityRadians = rollVelocityRadians;
    }

    /**
     * Advances the transform by the configured angular velocity.
     */
    public void update(double deltaSeconds) {
        if (yawVelocityRadians != 0.0 || pitchVelocityRadians != 0.0 || rollVelocityRadians != 0.0) {
            rotate(
                    yawVelocityRadians * deltaSeconds,
                    pitchVelocityRadians * deltaSeconds,
                    rollVelocityRadians * deltaSeconds
            );
        }
    }

    public WorldPosition transform(WorldPosition localPosition) {
        WorldPosition centered = localPosition.subtract(pivot);
        WorldPosition rotated = rotateLocal(centered);
        return rotated.add(pivot).add(position);
    }

    public WorldLine transform(WorldLine line) {
        return new WorldLine(transform(line.start()), transform(line.end()));
    }

    private WorldPosition rotateLocal(WorldPosition localPosition) {
        double x = localPosition.x();
        double y = localPosition.y();
        double z = localPosition.z();

        double cosYaw = Math.cos(yawRadians);
        double sinYaw = Math.sin(yawRadians);
        double yawX = x * cosYaw - z * sinYaw;
        double yawZ = x * sinYaw + z * cosYaw;

        double cosPitch = Math.cos(pitchRadians);
        double sinPitch = Math.sin(pitchRadians);
        double pitchY = y * cosPitch - yawZ * sinPitch;
        double pitchZ = y * sinPitch + yawZ * cosPitch;

        double cosRoll = Math.cos(rollRadians);
        double sinRoll = Math.sin(rollRadians);
        double rollX = yawX * cosRoll - pitchY * sinRoll;
        double rollY = yawX * sinRoll + pitchY * cosRoll;

        return new WorldPosition(rollX, rollY, pitchZ);
    }
}
