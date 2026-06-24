package org.kurodev.world.obj;

import org.kurodev.world.WorldCoordinate;

/**
 * Double precision position in world space.
 * <p>
 * Use this for objects that may sit between integer grid tiles, especially the
 * camera. Calling {@link #toCoordinate()} floors each axis to find the occupied
 * integer tile.
 */
public record WorldPosition(double x, double y, double z) {
    public static final WorldPosition ORIGIN = new WorldPosition(0.0, 0.0, 0.0);

    /**
     * Creates a ground-plane position with {@code y = 0}.
     */
    public WorldPosition(double x, double z) {
        this(x, 0.0, z);
    }

    /**
     * Floors each axis to get the integer tile that contains this position.
     */
    public WorldCoordinate toCoordinate() {
        return new WorldCoordinate(floorToInt(x), floorToInt(y), floorToInt(z));
    }

    public WorldPosition add(double dx, double dy, double dz) {
        return new WorldPosition(x + dx, y + dy, z + dz);
    }

    public WorldPosition add(WorldPosition other) {
        return add(other.x, other.y, other.z);
    }

    public WorldPosition subtract(WorldPosition other) {
        return new WorldPosition(x - other.x, y - other.y, z - other.z);
    }

    public WorldPosition multiply(double scalar) {
        return new WorldPosition(x * scalar, y * scalar, z * scalar);
    }

    private static int floorToInt(double value) {
        return (int) Math.floor(value);
    }
}
