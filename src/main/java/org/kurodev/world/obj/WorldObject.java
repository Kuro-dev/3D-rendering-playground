package org.kurodev.world.obj;

import org.kurodev.jpixelgameengine.gfx.Pixel;
import org.kurodev.world.WorldLine;
import org.kurodev.world.WorldManager;
import org.kurodev.world.shape.Shape;

import java.util.List;

/**
 * Common world-scene object contract.
 * <p>
 * Implementations are either {@link StaticObject}, which never moves, or
 * {@link DynamicObject}, which owns a double-precision transform and optional
 * per-frame behavior.
 */
public interface WorldObject {
    String name();

    Shape shape();

    Pixel color();

    void setColor(Pixel color);

    List<WorldLine> worldWireframeLines();

    default void update(WorldManager world, double deltaSeconds) {
        // Static objects do nothing by default.
    }
}
