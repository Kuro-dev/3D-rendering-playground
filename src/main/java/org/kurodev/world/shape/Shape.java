package org.kurodev.world.shape;

import org.kurodev.world.WorldLine;

import java.util.List;

/**
 * Wireframe-renderable 3D shape.
 * <p>
 * Shape coordinates are local to the shape. They are not automatically in
 * world space until a {@code WorldObject} applies its object coordinate as an
 * offset.
 */
public interface Shape {
    /**
     * Returns the shape edges in local shape coordinates.
     */
    List<WorldLine> wireframeLines();
}
