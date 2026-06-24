package org.kurodev.world;

import org.kurodev.world.obj.WorldPosition;

import java.util.Objects;

/**
 * Player/view camera with double precision position and a yaw/pitch view angle.
 * <p>
 * The position may sit between integer world tiles. {@link #currentTile()}
 * floors that position back to a {@link WorldCoordinate} when tile precision is
 * needed.
 */
public final class Camera {
    private WorldPosition position;
    private final ViewAngle viewAngle;

    /**
     * Creates a camera at world origin looking along +Z.
     */
    public Camera() {
        this(WorldPosition.ORIGIN, new ViewAngle());
    }

    public Camera(WorldPosition position) {
        this(position, new ViewAngle());
    }

    public Camera(double x, double y, double z) {
        this(new WorldPosition(x, y, z), new ViewAngle());
    }

    public Camera(WorldPosition position, ViewAngle viewAngle) {
        this.position = Objects.requireNonNull(position, "position");
        this.viewAngle = Objects.requireNonNull(viewAngle, "viewAngle");
    }

    public WorldPosition position() {
        return position;
    }

    public void setPosition(WorldPosition position) {
        this.position = Objects.requireNonNull(position, "position");
    }

    public ViewAngle viewAngle() {
        return viewAngle;
    }

    public WorldCoordinate currentTile() {
        return position.toCoordinate();
    }

    /**
     * Moves by a raw world-space delta.
     */
    public void move(double dx, double dy, double dz) {
        position = position.add(dx, dy, dz);
    }

    public void move(WorldPosition delta) {
        move(delta.x(), delta.y(), delta.z());
    }

    /**
     * Moves along the camera's flat forward vector. Pitch is ignored so walking
     * while looking up or down does not climb or sink.
     */
    public void moveForward(double distance) {
        move(viewAngle.flatForwardVector().multiply(distance));
    }

    /**
     * Moves sideways along the camera's flat right vector. Pitch is ignored.
     */
    public void moveRight(double distance) {
        move(viewAngle.flatRightVector().multiply(distance));
    }

    /**
     * Turns by yaw and pitch deltas in radians.
     */
    public void turn(double yawDeltaRadians, double pitchDeltaRadians) {
        viewAngle.addYawRadians(yawDeltaRadians);
        viewAngle.addPitchRadians(pitchDeltaRadians);
    }
}
