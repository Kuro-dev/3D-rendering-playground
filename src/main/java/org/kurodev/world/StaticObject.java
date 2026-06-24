package org.kurodev.world;

import org.kurodev.world.shape.Shape;

import java.util.Objects;

/**
 * An immutable world object with integer-grid placement.
 * <p>
 * Static objects cannot be translated or rotated after construction. Their
 * local shape geometry is offset once by the stored {@link WorldCoordinate}.
 * This is the right choice for scenery, architecture, props, and anything else
 * that should stay fixed in the world.
 */
public final class StaticObject extends AbstractWorldObject {
    private final WorldCoordinate coordinate;

    public StaticObject(Shape shape) {
        this("object", WorldCoordinate.ORIGIN, shape);
    }

    public StaticObject(WorldCoordinate coordinate, Shape shape) {
        this("object", coordinate, shape);
    }

    public StaticObject(String name, WorldCoordinate coordinate, Shape shape) {
        super(name, shape);
        this.coordinate = Objects.requireNonNull(coordinate, "coordinate");
    }

    /**
     * Integer world coordinate of the object's local origin.
     */
    public WorldCoordinate coordinate() {
        return coordinate;
    }

    @Override
    protected WorldLine translateLine(WorldLine localLine) {
        return localLine.translated(coordinate);
    }
}
