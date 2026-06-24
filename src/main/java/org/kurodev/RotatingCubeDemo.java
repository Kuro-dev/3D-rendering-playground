package org.kurodev;

import org.kurodev.input.MouseCapture;
import org.kurodev.jpixelgameengine.gfx.Pixel;
import org.kurodev.jpixelgameengine.impl.ffm.BatchedPixelGameEngine;
import org.kurodev.jpixelgameengine.input.KeyBoardKey;
import org.kurodev.jpixelgameengine.pos.Vector2D;
import org.kurodev.world.*;
import org.kurodev.world.DynamicObject;
import org.kurodev.world.StaticObject;
import org.kurodev.world.shape.Pyramid;
import org.kurodev.world.shape.Rectangle;
import org.kurodev.world.shape.Sphere;

import java.util.Locale;

public class RotatingCubeDemo extends BatchedPixelGameEngine {

    private static final float FOCAL_LENGTH = 180.0f;
    private static final double NEAR_PLANE = 0.05;

    private final int screenWidth;
    private final int screenHeight;
    private final WorldManager worldManager;
    private final MouseCapture mouseCapture = new MouseCapture();

    public RotatingCubeDemo(int width, int height) {
        super(width, height, 4, 4);
        this.screenWidth = width;
        this.screenHeight = height;
        this.worldManager = new WorldManager(new Camera(new WorldPosition(0.0, 0.0, -6.0), new ViewAngle()));
    }

    @Override
    public boolean onUserCreate() {
        DynamicObject spinningCube = worldManager.addDynamicObject("cube", new WorldPosition(-5.0, 1.0, 10.0), new Rectangle(2, 2, 2));
        spinningCube.setPivot(new WorldPosition(1.0, 1.0, 1.0));
        spinningCube.setBehavior((object, world, deltaSeconds) -> object.rotate(0.9 * deltaSeconds, 0.55 * deltaSeconds, 0.0));

        worldManager.addStaticObject("rectangle",
                new WorldCoordinate(-3, -1, 7),
                new Rectangle(3, 2, 5));
        worldManager.addStaticObject(
                "pyramid",
                new WorldCoordinate(2, -1, 8),
                new Pyramid(2, 2, 5)
        );
        worldManager.addStaticObject(new WorldCoordinate(0, 1, 10), new Sphere(1, 24));
        return true;
    }

    @Override
    public boolean onUserUpdate(float delta) {
        if (!isFocussed()) {
            mouseCapture.release();
            return true;
        }
        if (getKey(KeyBoardKey.ESCAPE).isPressed()) {
            return false;
        }
        if (getKey(KeyBoardKey.TAB).isPressed()) {
            consoleShow(KeyBoardKey.TAB, true);
        }
        MouseCapture.MouseDelta mouseDelta = mouseCapture.captureDelta(!isConsoleShowing());
        worldManager.updatePlayerControlledCamera(this, mouseDelta.x(), mouseDelta.y(), delta);
        worldManager.update(delta);
        clear(Pixel.BLACK);

        renderWorldObjects();

        renderCameraReadout();
        return true;
    }

    @Override
    protected boolean onConsoleCommand(String command) {
        return super.onConsoleCommand(command);
    }

    private void renderWorldObjects() {
        for (WorldObject object : worldManager.objects()) {
            Pixel color = colorFor(object);
            for (WorldLine line : object.worldWireframeLines()) {
                drawWorldLine(line, color);
            }
        }
    }

    private void drawWorldLine(WorldLine line, Pixel color) {
        Vector2D<Float> a = project(line.start());
        Vector2D<Float> b = project(line.end());

        if (a == null || b == null) {
            return;
        }

        drawLine(
                Math.round(a.getX()),
                Math.round(a.getY()),
                Math.round(b.getX()),
                Math.round(b.getY()),
                color,
                0xFFFFFFFF
        );
    }

    private Pixel colorFor(WorldObject object) {
        if (object.shape() instanceof Rectangle) {
            return Pixel.CYAN;
        }
        if (object.shape() instanceof Pyramid) {
            return Pixel.YELLOW;
        }
        if (object.shape() instanceof Sphere) {
            return Pixel.GREEN;
        }
        return Pixel.WHITE;
    }

    private void renderCameraReadout() {
        WorldPosition position = worldManager.camera().position();
        WorldCoordinate tile = worldManager.camera().currentTile();
        double yaw = worldManager.camera().viewAngle().yawDegrees();
        double pitch = worldManager.camera().viewAngle().pitchDegrees();

        drawString(10, 10, String.format(Locale.ROOT, "Cam %.1f %.1f %.1f",
                position.x(), position.y(), position.z()), Pixel.WHITE, 1);
        drawString(10, 20, String.format(Locale.ROOT, "Tile %d %d %d",
                tile.x(), tile.y(), tile.z()), Pixel.WHITE, 1);
        drawString(10, 30, String.format(Locale.ROOT, "Yaw %.0f Pitch %.0f%s",
                yaw, pitch, mouseCapture.isAvailable() ? "" : " no mouse capture"), Pixel.WHITE, 1);
    }

    private Vector2D<Float> project(WorldPosition worldPoint) {
        WorldPosition p = toCameraSpace(worldPoint);
        if (p.z() <= NEAR_PLANE) {
            return null;
        }

        float screenX = (float) (screenWidth / 2.0 + p.x() * FOCAL_LENGTH / p.z());
        float screenY = (float) (screenHeight / 2.0 - p.y() * FOCAL_LENGTH / p.z());

        return Vector2D.ofFloat(screenX, screenY);
    }

    private WorldPosition toCameraSpace(WorldPosition worldPoint) {
        WorldPosition relative = worldPoint.subtract(worldManager.camera().position());
        ViewAngle viewAngle = worldManager.camera().viewAngle();

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
