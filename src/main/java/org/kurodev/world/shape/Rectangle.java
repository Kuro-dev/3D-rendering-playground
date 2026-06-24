package org.kurodev.world.shape;

import org.kurodev.world.WorldCoordinate;
import org.kurodev.world.WorldLine;
import org.kurodev.world.obj.WorldPosition;

import java.util.List;
import java.util.Objects;

/**
 * Axis-aligned 3D rectangle, effectively a cuboid or box.
 * <p>
 * The {@code origin} is the local corner at minimum X, minimum Y, and minimum Z.
 * From that corner, {@code width} extends along +X, {@code height} extends
 * along +Y, and {@code depth} extends along +Z. With the default camera
 * orientation this means X goes right, Y goes up, and Z goes deeper into the
 * scene.
 * <p>
 * When the rectangle is placed inside a world object, that object's coordinate
 * is added to every corner. For example, a static object at
 * {@code (3, 0, 7)} with {@code new Rectangle(2, 4, 1)} puts the local origin
 * corner at world coordinate {@code (3, 0, 7)} and the opposite corner at
 * {@code (5, 4, 8)}.
 */
@SuppressWarnings("ClassCanBeRecord") //Cannot be a Record in the current form.
public final class Rectangle implements Shape {
    private final WorldCoordinate origin;
    private final int width;
    private final int height;
    private final int depth;
    private final List<WorldLine> wireframeLines;

    /**
     * Creates a box at local origin with depth 1.
     */
    public Rectangle(int width, int height) {
        this(WorldCoordinate.ORIGIN, width, height, 1);
    }

    /**
     * Creates a box at local origin.
     */
    public Rectangle(int width, int height, int depth) {
        this(WorldCoordinate.ORIGIN, width, height, depth);
    }

    /**
     * Creates a box whose local minimum corner starts at {@code (x, y, z)} and
     * whose depth is 1.
     */
    public Rectangle(int x, int y, int z, int width, int height) {
        this(new WorldCoordinate(x, y, z), width, height, 1);
    }

    /**
     * Creates a box whose local minimum corner is {@code origin} and whose
     * depth is 1.
     */
    public Rectangle(WorldCoordinate origin, int width, int height) {
        this(origin, width, height, 1);
    }

    /**
     * Creates a box whose local minimum corner starts at {@code (x, y, z)}.
     */
    public Rectangle(int x, int y, int z, int width, int height, int depth) {
        this(new WorldCoordinate(x, y, z), width, height, depth);
    }

    /**
     * Creates a box whose local minimum corner is {@code origin}.
     */
    public Rectangle(WorldCoordinate origin, int width, int height, int depth) {
        this.origin = Objects.requireNonNull(origin, "origin");
        this.width = requirePositive(width, "width");
        this.height = requirePositive(height, "height");
        this.depth = requirePositive(depth, "depth");
        this.wireframeLines = createWireframeLines();
    }

    private static int requirePositive(int value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be positive");
        }
        return value;
    }

    /**
     * Local corner at minimum X, minimum Y, and minimum Z.
     */
    public WorldCoordinate origin() {
        return origin;
    }

    /**
     * Size along the +X axis.
     */
    public int width() {
        return width;
    }

    /**
     * Size along the +Y axis.
     */
    public int height() {
        return height;
    }

    /**
     * Size along the +Z axis.
     */
    public int depth() {
        return depth;
    }

    @Override
    public List<WorldLine> wireframeLines() {
        return wireframeLines;
    }

    private List<WorldLine> createWireframeLines() {
        WorldPosition frontTopLeft = origin.toPosition();
        WorldPosition frontTopRight = origin.offset(width, 0, 0).toPosition();
        WorldPosition frontBottomRight = origin.offset(width, height, 0).toPosition();
        WorldPosition frontBottomLeft = origin.offset(0, height, 0).toPosition();

        WorldPosition backTopLeft = origin.offset(0, 0, depth).toPosition();
        WorldPosition backTopRight = origin.offset(width, 0, depth).toPosition();
        WorldPosition backBottomRight = origin.offset(width, height, depth).toPosition();
        WorldPosition backBottomLeft = origin.offset(0, height, depth).toPosition();

        return List.of(
                new WorldLine(frontTopLeft, frontTopRight),
                new WorldLine(frontTopRight, frontBottomRight),
                new WorldLine(frontBottomRight, frontBottomLeft),
                new WorldLine(frontBottomLeft, frontTopLeft),
                new WorldLine(backTopLeft, backTopRight),
                new WorldLine(backTopRight, backBottomRight),
                new WorldLine(backBottomRight, backBottomLeft),
                new WorldLine(backBottomLeft, backTopLeft),
                new WorldLine(frontTopLeft, backTopLeft),
                new WorldLine(frontTopRight, backTopRight),
                new WorldLine(frontBottomRight, backBottomRight),
                new WorldLine(frontBottomLeft, backBottomLeft)
        );
    }
}
