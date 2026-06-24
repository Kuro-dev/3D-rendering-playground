package org.kurodev.world.shape;

import org.kurodev.world.WorldCoordinate;
import org.kurodev.world.WorldLine;

import java.util.List;
import java.util.Objects;

/**
 * Triangular pyramid, the 3D counterpart to a triangle.
 * <p>
 * A pyramid is described by three base vertices ({@code a}, {@code b},
 * {@code c}) and one {@code apex}. All coordinates are local to the shape
 * until a {@code WorldObject} adds its world coordinate offset.
 * <p>
 * The size-based constructors use {@code origin} as the first base corner.
 * From there:
 * <ul>
 *     <li>{@code a = origin}</li>
 *     <li>{@code b = origin + (width, 0, 0)}</li>
 *     <li>{@code c = origin + (width / 2, 0, depth)}</li>
 *     <li>{@code apex = origin + (width / 2, height, depth / 2)}</li>
 * </ul>
 * Integer division is used for the midpoint, so odd widths/depths round down.
 */
public class Pyramid implements Shape {
    private final WorldCoordinate a;
    private final WorldCoordinate b;
    private final WorldCoordinate c;
    private final WorldCoordinate apex;
    private final List<WorldLine> wireframeLines;

    /**
     * Creates a pyramid at local origin.
     */
    public Pyramid(int width, int height, int depth) {
        this(WorldCoordinate.ORIGIN, width, height, depth);
    }

    /**
     * Creates a pyramid whose first base corner starts at {@code (x, y, z)}.
     */
    public Pyramid(int x, int y, int z, int width, int height, int depth) {
        this(new WorldCoordinate(x, y, z), width, height, depth);
    }

    /**
     * Creates a pyramid using {@code origin} as base vertex {@code a}. Width
     * extends along +X, height extends toward the apex along +Y, and depth
     * extends along +Z.
     */
    public Pyramid(WorldCoordinate origin, int width, int height, int depth) {
        this(pointsFromSize(origin, width, height, depth));
    }

    /**
     * Creates a pyramid from explicit local-space vertices.
     */
    public Pyramid(WorldCoordinate a, WorldCoordinate b, WorldCoordinate c, WorldCoordinate apex) {
        this.a = Objects.requireNonNull(a, "a");
        this.b = Objects.requireNonNull(b, "b");
        this.c = Objects.requireNonNull(c, "c");
        this.apex = Objects.requireNonNull(apex, "apex");
        this.wireframeLines = List.of(
                new WorldLine(a.toPosition(), b.toPosition()),
                new WorldLine(b.toPosition(), c.toPosition()),
                new WorldLine(c.toPosition(), a.toPosition()),
                new WorldLine(a.toPosition(), apex.toPosition()),
                new WorldLine(b.toPosition(), apex.toPosition()),
                new WorldLine(c.toPosition(), apex.toPosition())
        );
    }

    /**
     * Creates a pyramid from explicit local-space vertex coordinates.
     */
    public Pyramid(
            int ax, int ay, int az,
            int bx, int by, int bz,
            int cx, int cy, int cz,
            int apexX, int apexY, int apexZ
    ) {
        this(
                new WorldCoordinate(ax, ay, az),
                new WorldCoordinate(bx, by, bz),
                new WorldCoordinate(cx, cy, cz),
                new WorldCoordinate(apexX, apexY, apexZ)
        );
    }

    private Pyramid(WorldCoordinate[] points) {
        this(points[0], points[1], points[2], points[3]);
    }

    /**
     * First base vertex.
     */
    public WorldCoordinate a() {
        return a;
    }

    /**
     * Second base vertex.
     */
    public WorldCoordinate b() {
        return b;
    }

    /**
     * Third base vertex.
     */
    public WorldCoordinate c() {
        return c;
    }

    /**
     * Tip of the pyramid.
     */
    public WorldCoordinate apex() {
        return apex;
    }

    @Override
    public List<WorldLine> wireframeLines() {
        return wireframeLines;
    }

    private static WorldCoordinate[] pointsFromSize(WorldCoordinate origin, int width, int height, int depth) {
        Objects.requireNonNull(origin, "origin");
        int checkedWidth = requirePositive(width, "width");
        int checkedHeight = requirePositive(height, "height");
        int checkedDepth = requirePositive(depth, "depth");
        int halfWidth = checkedWidth / 2;
        int halfDepth = checkedDepth / 2;

        return new WorldCoordinate[]{
                origin,
                origin.offset(checkedWidth, 0, 0),
                origin.offset(halfWidth, 0, checkedDepth),
                origin.offset(halfWidth, checkedHeight, halfDepth)
        };
    }

    private static int requirePositive(int value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be positive");
        }
        return value;
    }
}
