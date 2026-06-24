package org.kurodev.world;

import org.kurodev.world.obj.WorldObject;
import org.kurodev.world.obj.WorldPosition;

import java.util.Objects;

/**
 * A straight wireframe edge between two world-space positions.
 * <p>
 * Shapes expose lines in their own local coordinate space. A
 * {@link WorldObject} translates those lines into world space by adding the
 * object's coordinate.
 */
public record WorldLine(WorldPosition start, WorldPosition end) {
    public WorldLine {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(end, "end");
    }

    /**
     * Returns this line moved by an integer grid offset.
     */
    public WorldLine translated(WorldCoordinate offset) {
        return translated(offset.toPosition());
    }

    /**
     * Returns this line moved by a double precision offset.
     */
    public WorldLine translated(WorldPosition offset) {
        return new WorldLine(start.add(offset), end.add(offset));
    }
}
