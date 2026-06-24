package org.kurodev;

import org.kurodev.command.CommandHandler;
import org.kurodev.input.MouseCapture;
import org.kurodev.jpixelgameengine.gfx.Pixel;
import org.kurodev.jpixelgameengine.impl.ffm.BatchedPixelGameEngine;
import org.kurodev.jpixelgameengine.input.KeyBoardKey;
import org.kurodev.render.Renderer;
import org.kurodev.world.Camera;
import org.kurodev.world.ViewAngle;
import org.kurodev.world.WorldCoordinate;
import org.kurodev.world.WorldManager;
import org.kurodev.world.obj.DynamicObject;
import org.kurodev.world.obj.WorldPosition;
import org.kurodev.world.shape.Pyramid;
import org.kurodev.world.shape.Rectangle;
import org.kurodev.world.shape.Sphere;

public class RotatingCubeDemo extends BatchedPixelGameEngine {
    private final WorldManager worldManager;
    private final MouseCapture mouseCapture = new MouseCapture();
    private final Renderer renderer;
    private final CommandHandler commandHandler;

    public RotatingCubeDemo(int width, int height) {
        super(width, height, 4, 4);
        this.worldManager = new WorldManager(new Camera(new WorldPosition(0.0, 0.0, -6.0), new ViewAngle()));
        this.renderer = new Renderer(this);
        this.commandHandler = new CommandHandler(worldManager).registerDefaults();
    }

    @Override
    public boolean onUserCreate() {
        DynamicObject spinningCube = worldManager.addDynamicObject("cube", new WorldPosition(-5.0, 1.0, 10.0), new Rectangle(2, 2, 2), Pixel.CYAN);
        spinningCube.setPivot(new WorldPosition(1.0, 1.0, 1.0));
        spinningCube.setBehavior((object, world, deltaSeconds) -> object.rotate(0.9 * deltaSeconds, 0.55 * deltaSeconds, 0.0));

        worldManager.addStaticObject("rectangle", new WorldCoordinate(-3, -1, 7), new Rectangle(3, 2, 5), Pixel.YELLOW);
        worldManager.addDynamicObject("pyramid", new WorldCoordinate(2, -1, 8), new Pyramid(2, 2, 5), Pixel.MAGENTA);
        worldManager.addStaticObject("sphere", new WorldCoordinate(0, 1, 10), new Sphere(1, 24), Pixel.GREEN);
        worldManager.getObject("rectangle").ifPresent(o-> o.setColor(Pixel.CYAN));
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
        renderer.render(worldManager);
        return true;
    }

    @Override
    protected boolean onConsoleCommand(String command) {
        return commandHandler.handle(command, this::consoleWriteln);
    }
}
