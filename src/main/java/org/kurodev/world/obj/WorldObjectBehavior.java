package org.kurodev.world.obj;

import org.kurodev.world.WorldManager;

/**
 * Per-frame behavior attached to a world object.
 * <p>
 * Use this for custom motion, scripted movement, or any other object-specific
 * logic you want to apply each frame. The behavior receives the
 * {@link DynamicObject}, so it can call
 * {@link DynamicObject#translate(double, double, double)},
 * {@link DynamicObject#rotate(double, double, double)}, or adjust the
 * transform directly.
 */
@FunctionalInterface
public interface WorldObjectBehavior {
    void update(DynamicObject object, WorldManager world, double deltaSeconds);
}
