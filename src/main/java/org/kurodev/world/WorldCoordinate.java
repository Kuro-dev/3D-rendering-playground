package org.kurodev.world;

import org.kurodev.world.obj.WorldPosition;

/**
 * Integer precision coordinate on the world grid.
 * <p>
 * Use this for tile/object placement. In the current renderer, +X is right,
 * +Y is up, and +Z is forward/deeper into the scene when the camera yaw is 0.
 * Entities that need sub-tile placement, such as the camera, should use
 * {@link WorldPosition} instead.
 */
public record WorldCoordinate(int x, int y, int z) {
    public static final WorldCoordinate ORIGIN = new WorldCoordinate(0, 0, 0);

    /**
     * Creates a ground-plane coordinate with {@code y = 0}.
     */
    public WorldCoordinate(int x, int z) {
        this(x, 0, z);
    }

    /**
     * Converts a double precision position into the tile it currently occupies.
     */
    public static WorldCoordinate from(WorldPosition position) {
        return position.toCoordinate();
    }

    /**
     * Returns a coordinate moved by the given integer grid distance.
     */
    public WorldCoordinate offset(int dx, int dy, int dz) {
        return new WorldCoordinate(x + dx, y + dy, z + dz);
    }

    /**
     * Returns a coordinate moved by another coordinate treated as an offset.
     */
    public WorldCoordinate offset(WorldCoordinate other) {
        return offset(other.x, other.y, other.z);
    }

    /**
     * Converts this exact grid point to a double precision position.
     */
    public WorldPosition toPosition() {
        return new WorldPosition(x, y, z);
    }
}
