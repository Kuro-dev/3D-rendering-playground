package org.kurodev.world;

/**
 * Camera orientation expressed as yaw and pitch.
 * <p>
 * Yaw rotates around the Y axis. A yaw of 0 looks along +Z. Pitch rotates the
 * view up/down and is clamped to avoid flipping over.
 */
public final class ViewAngle {
    private static final double FULL_TURN = Math.PI * 2.0;
    private static final double MAX_PITCH_RADIANS = Math.toRadians(89.0);

    private double yawRadians;
    private double pitchRadians;

    public ViewAngle() {
        this(0.0, 0.0);
    }

    public ViewAngle(double yawRadians, double pitchRadians) {
        setYawRadians(yawRadians);
        setPitchRadians(pitchRadians);
    }

    public static ViewAngle fromDegrees(double yawDegrees, double pitchDegrees) {
        return new ViewAngle(Math.toRadians(yawDegrees), Math.toRadians(pitchDegrees));
    }

    public double yawRadians() {
        return yawRadians;
    }

    public double pitchRadians() {
        return pitchRadians;
    }

    public double yawDegrees() {
        return Math.toDegrees(yawRadians);
    }

    public double pitchDegrees() {
        return Math.toDegrees(pitchRadians);
    }

    public void setYawRadians(double yawRadians) {
        this.yawRadians = normalizeRadians(yawRadians);
    }

    public void setPitchRadians(double pitchRadians) {
        this.pitchRadians = Math.max(-MAX_PITCH_RADIANS, Math.min(MAX_PITCH_RADIANS, pitchRadians));
    }

    public void addYawRadians(double deltaRadians) {
        setYawRadians(yawRadians + deltaRadians);
    }

    public void addPitchRadians(double deltaRadians) {
        setPitchRadians(pitchRadians + deltaRadians);
    }

    /**
     * Unit-length movement vector for forward/backward walking on the XZ
     * plane. Pitch is intentionally ignored.
     */
    public WorldPosition flatForwardVector() {
        return new WorldPosition(Math.sin(yawRadians), 0.0, Math.cos(yawRadians));
    }

    /**
     * Unit-length movement vector for strafing on the XZ plane.
     */
    public WorldPosition flatRightVector() {
        return new WorldPosition(Math.cos(yawRadians), 0.0, -Math.sin(yawRadians));
    }

    private static double normalizeRadians(double radians) {
        double normalized = radians % FULL_TURN;
        if (normalized > Math.PI) {
            normalized -= FULL_TURN;
        } else if (normalized < -Math.PI) {
            normalized += FULL_TURN;
        }
        return normalized;
    }
}
