package org.kurodev.world.shape;

import org.kurodev.world.WorldCoordinate;
import org.kurodev.world.WorldLine;
import org.kurodev.world.obj.WorldPosition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Wireframe sphere approximation.
 * <p>
 * The {@code center} is the sphere's local center point, not a corner. The
 * {@code radius} extends equally along +X/-X, +Y/-Y, and +Z/-Z. When the sphere
 * is placed inside a {@code WorldObject}, the object's coordinate is added to
 * the center and all generated ring points.
 * <p>
 * The wireframe is made from three great-circle rings: one on the XY plane,
 * one on the XZ plane, and one on the YZ plane. {@code segments} controls how
 * many line pieces are used per ring.
 */
public final class Sphere implements Shape {
    private static final int DEFAULT_SEGMENTS = 24;

    private final WorldCoordinate center;
    private final int radius;
    private final int segments;
    private final List<WorldLine> wireframeLines;

    /**
     * Creates a sphere centered at local origin with the default ring detail.
     */
    public Sphere(int radius) {
        this(WorldCoordinate.ORIGIN, radius, DEFAULT_SEGMENTS);
    }

    /**
     * Creates a sphere centered at local origin.
     */
    public Sphere(int radius, int segments) {
        this(WorldCoordinate.ORIGIN, radius, segments);
    }

    /**
     * Creates a sphere centered at the given local coordinate with the default
     * ring detail.
     */
    public Sphere(WorldCoordinate center, int radius) {
        this(center, radius, DEFAULT_SEGMENTS);
    }

    /**
     * Creates a sphere centered at the given local coordinate with the default
     * ring detail.
     */
    public Sphere(int x, int y, int z, int radius) {
        this(new WorldCoordinate(x, y, z), radius, DEFAULT_SEGMENTS);
    }

    /**
     * Creates a sphere centered at the given local coordinate.
     */
    public Sphere(WorldCoordinate center, int radius, int segments) {
        this.center = Objects.requireNonNull(center, "center");
        this.radius = requirePositive(radius, "radius");
        this.segments = requireMinimum(segments, 8, "segments");
        this.wireframeLines = createWireframeLines();
    }

    /**
     * Local center of the sphere.
     */
    public WorldCoordinate center() {
        return center;
    }

    /**
     * Distance from the center to the edge on every axis.
     */
    public int radius() {
        return radius;
    }

    /**
     * Number of line pieces per great-circle ring.
     */
    public int segments() {
        return segments;
    }

    @Override
    public List<WorldLine> wireframeLines() {
        return wireframeLines;
    }

    private List<WorldLine> createWireframeLines() {
        List<WorldLine> lines = new ArrayList<>(segments * 3);
        addRing(lines, RingPlane.XY);
        addRing(lines, RingPlane.XZ);
        addRing(lines, RingPlane.YZ);
        return Collections.unmodifiableList(lines);
    }

    private void addRing(List<WorldLine> lines, RingPlane plane) {
        for (int i = 0; i < segments; i++) {
            double startAngle = (Math.PI * 2.0 * i) / segments;
            double endAngle = (Math.PI * 2.0 * (i + 1)) / segments;
            lines.add(new WorldLine(pointOnRing(startAngle, plane), pointOnRing(endAngle, plane)));
        }
    }

    private WorldPosition pointOnRing(double angle, RingPlane plane) {
        double first = Math.cos(angle) * radius;
        double second = Math.sin(angle) * radius;
        double x = center.x();
        double y = center.y();
        double z = center.z();

        return switch (plane) {
            case XY -> new WorldPosition(x + first, y + second, z);
            case XZ -> new WorldPosition(x + first, y, z + second);
            case YZ -> new WorldPosition(x, y + first, z + second);
        };
    }

    private static int requirePositive(int value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be positive");
        }
        return value;
    }

    private static int requireMinimum(int value, int minimum, String name) {
        if (value < minimum) {
            throw new IllegalArgumentException(name + " must be at least " + minimum);
        }
        return value;
    }

    private enum RingPlane {
        XY,
        XZ,
        YZ
    }
}
