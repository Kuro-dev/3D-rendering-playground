package org.kurodev.render;

import org.kurodev.jpixelgameengine.gfx.Pixel;
import org.kurodev.jpixelgameengine.impl.ffm.BatchedPixelGameEngine;
import org.kurodev.jpixelgameengine.pos.Vector2D;
import org.kurodev.world.Camera;
import org.kurodev.world.WorldCoordinate;
import org.kurodev.world.WorldLine;
import org.kurodev.world.WorldManager;
import org.kurodev.world.obj.WorldObject;
import org.kurodev.world.obj.WorldPosition;
import org.kurodev.world.ViewAngle;

import java.util.Locale;

/**
 * Handles world rendering for a {@link BatchedPixelGameEngine}.
 * <p>
 * The demo can keep its own logic thin by delegating all world drawing to this
 * class. The renderer projects the world from the camera in {@link WorldManager}
 * and draws every registered object.
 */
public final class Renderer {
    private static final float FOCAL_LENGTH = 180.0f;
    private static final double NEAR_PLANE = 0.05;

    private final BatchedPixelGameEngine engine;
    private final int screenWidth;
    private final int screenHeight;

    public Renderer(BatchedPixelGameEngine engine) {
        this.engine = engine;
        this.screenWidth = engine.screenWidth();
        this.screenHeight = engine.screenHeight();
    }

    public void render(WorldManager worldManager) {
        engine.clear(Pixel.BLACK);
        for (WorldObject object : worldManager.objects()) {
            drawObject(worldManager.camera(), object);
        }
        drawHud(worldManager);
    }

    private void drawObject(Camera camera, WorldObject object) {
        Pixel color = object.color();
        for (WorldLine line : object.worldWireframeLines()) {
            drawWorldLine(camera, line, color);
        }
    }

    private void drawWorldLine(Camera camera, WorldLine line, Pixel color) {
        Vector2D<Float> a = project(camera, line.start());
        Vector2D<Float> b = project(camera, line.end());
        if (a == null || b == null) {
            return;
        }

        engine.drawLine(
                Math.round(a.getX()),
                Math.round(a.getY()),
                Math.round(b.getX()),
                Math.round(b.getY()),
                color,
                0xFFFFFFFF
        );
    }

    private void drawHud(WorldManager worldManager) {
        WorldPosition position = worldManager.camera().position();
        WorldCoordinate tile = worldManager.camera().currentTile();
        ViewAngle angle = worldManager.camera().viewAngle();

        engine.drawString(10, 10, String.format(Locale.ROOT, "Cam %.1f %.1f %.1f",
                position.x(), position.y(), position.z()), Pixel.WHITE, 1);
        engine.drawString(10, 20, String.format(Locale.ROOT, "Tile %d %d %d",
                tile.x(), tile.y(), tile.z()), Pixel.WHITE, 1);
        engine.drawString(10, 30, String.format(Locale.ROOT, "Yaw %.0f Pitch %.0f Objects %d",
                angle.yawDegrees(), angle.pitchDegrees(), worldManager.objects().size()), Pixel.WHITE, 1);
    }

    private Vector2D<Float> project(Camera camera, WorldPosition worldPoint) {
        WorldPosition p = toCameraSpace(camera, worldPoint);
        if (p.z() <= NEAR_PLANE) {
            return null;
        }

        float screenX = (float) (screenWidth / 2.0 + p.x() * FOCAL_LENGTH / p.z());
        float screenY = (float) (screenHeight / 2.0 - p.y() * FOCAL_LENGTH / p.z());
        return Vector2D.ofFloat(screenX, screenY);
    }

    private WorldPosition toCameraSpace(Camera camera, WorldPosition worldPoint) {
        WorldPosition relative = worldPoint.subtract(camera.position());
        ViewAngle viewAngle = camera.viewAngle();

        double cosYaw = Math.cos(viewAngle.yawRadians());
        double sinYaw = Math.sin(viewAngle.yawRadians());
        double yawX = relative.x() * cosYaw - relative.z() * sinYaw;
        double yawZ = relative.x() * sinYaw + relative.z() * cosYaw;

        double cosPitch = Math.cos(viewAngle.pitchRadians());
        double sinPitch = Math.sin(viewAngle.pitchRadians());
        double pitchY = relative.y() * cosPitch - yawZ * sinPitch;
        double pitchZ = relative.y() * sinPitch + yawZ * cosPitch;

        return new WorldPosition(yawX, pitchY, pitchZ);
    }

}
