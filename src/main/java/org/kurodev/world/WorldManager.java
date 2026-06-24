package org.kurodev.world;

import org.kurodev.jpixelgameengine.impl.ffm.PixelGameEngine;
import org.kurodev.jpixelgameengine.input.KeyBoardKey;
import org.kurodev.world.shape.Shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Owns the camera and all world objects that should be updated or rendered.
 * <p>
 * Shape geometry stays local to each shape. Object placement is handled by
 * {@link StaticObject} or {@link DynamicObject}, and camera movement uses
 * double precision through {@link Camera}.
 */
public final class WorldManager {
    private final Camera camera;
    private final List<WorldObject> objects = new ArrayList<>();
    private double cameraMoveSpeed = 4.0;
    private double cameraLookSensitivityRadians = 0.0025;

    public WorldManager() {
        this(new Camera());
    }

    public WorldManager(Camera camera) {
        this.camera = Objects.requireNonNull(camera, "camera");
    }

    private static double axis(PixelGameEngine engine, KeyBoardKey positive, KeyBoardKey negative) {
        double value = 0.0;
        if (engine.getKey(positive).isHeld()) {
            value += 1.0;
        }
        if (engine.getKey(negative).isHeld()) {
            value -= 1.0;
        }
        return value;
    }

    public Camera camera() {
        return camera;
    }

    public List<WorldObject> objects() {
        return Collections.unmodifiableList(objects);
    }

    /**
     * Adds an already constructed world object.
     * <p>
     * Prefer {@link #addStaticObject(String, WorldCoordinate, Shape)} or
     * {@link #addDynamicObject(String, WorldPosition, Shape)} when creating new
     * objects so the intended object type stays explicit at the call site.
     */
    public WorldObject addObject(WorldObject object) {
        objects.add(Objects.requireNonNull(object, "object"));
        return object;
    }

    public StaticObject addStaticObject(String name, WorldCoordinate coordinate, Shape shape) {
        return (StaticObject) addObject(new StaticObject(name, coordinate, shape));
    }

    public StaticObject addStaticObject(WorldCoordinate coordinate, Shape shape) {
        return (StaticObject) addObject(new StaticObject(coordinate, shape));
    }

    public DynamicObject addDynamicObject(String name, WorldPosition position, Shape shape) {
        return (DynamicObject) addObject(new DynamicObject(name, position, shape));
    }

    public DynamicObject addDynamicObject(WorldPosition position, Shape shape) {
        return (DynamicObject) addObject(new DynamicObject(position, shape));
    }

    public boolean removeObject(WorldObject object) {
        return objects.remove(object);
    }

    public void clearObjects() {
        objects.clear();
    }

    /**
     * Advances object-local animations and behaviors.
     */
    public void update(double deltaSeconds) {
        double frameDelta = Math.max(0.0, deltaSeconds);
        int size = objects.size();
        for (int i = 0; i < size; i++) {
            WorldObject object = objects.get(i);
            object.update(this, frameDelta);
        }
    }

    public double cameraMoveSpeed() {
        return cameraMoveSpeed;
    }

    public void setCameraMoveSpeed(double cameraMoveSpeed) {
        if (cameraMoveSpeed < 0.0) {
            throw new IllegalArgumentException("cameraMoveSpeed must be non-negative");
        }
        this.cameraMoveSpeed = cameraMoveSpeed;
    }

    public double cameraLookSensitivityRadians() {
        return cameraLookSensitivityRadians;
    }

    public void setCameraLookSensitivityRadians(double cameraLookSensitivityRadians) {
        if (cameraLookSensitivityRadians < 0.0) {
            throw new IllegalArgumentException("cameraLookSensitivityRadians must be non-negative");
        }
        this.cameraLookSensitivityRadians = cameraLookSensitivityRadians;
    }

    /**
     * Updates the camera from engine input. W/S move forward/backward, A/D
     * strafe left/right, and mouse deltas turn yaw/pitch.
     */
    public void updatePlayerControlledCamera(PixelGameEngine engine, double mouseDeltaX, double mouseDeltaY, double deltaSeconds) {
        Objects.requireNonNull(engine, "engine");

        double frameDelta = Math.max(0.0, deltaSeconds);
        double forwardAxis = axis(engine, KeyBoardKey.W, KeyBoardKey.S);
        double strafeAxis = axis(engine, KeyBoardKey.D, KeyBoardKey.A);

        updatePlayerControlledCamera(forwardAxis, strafeAxis, mouseDeltaX, mouseDeltaY, frameDelta);
    }

    /**
     * Updates the camera from normalized movement axes and raw mouse deltas.
     */
    public void updatePlayerControlledCamera(
            double forwardAxis,
            double strafeAxis,
            double mouseDeltaX,
            double mouseDeltaY,
            double deltaSeconds
    ) {
        double frameDelta = Math.max(0.0, deltaSeconds);

        if (mouseDeltaX != 0.0 || mouseDeltaY != 0.0) {
            camera.turn(
                    mouseDeltaX * cameraLookSensitivityRadians,
                    -mouseDeltaY * cameraLookSensitivityRadians
            );
        }

        double movementLength = Math.hypot(forwardAxis, strafeAxis);
        if (movementLength == 0.0) {
            return;
        }

        double frameDistance = cameraMoveSpeed * frameDelta;
        camera.moveForward((forwardAxis / movementLength) * frameDistance);
        camera.moveRight((strafeAxis / movementLength) * frameDistance);
    }
}
