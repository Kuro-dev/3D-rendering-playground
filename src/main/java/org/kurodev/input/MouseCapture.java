package org.kurodev.input;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;

public final class MouseCapture {
    private final Robot robot;
    private Point capturePoint;

    public MouseCapture() {
        this.robot = createRobot();
    }

    public MouseDelta captureDelta(boolean active) {
        if (!active || robot == null) {
            release();
            return MouseDelta.NONE;
        }

        Point pointer = currentPointer();
        if (pointer == null) {
            release();
            return MouseDelta.NONE;
        }

        if (capturePoint == null) {
            capturePoint = pointer;
            return MouseDelta.NONE;
        }

        int dx = pointer.x - capturePoint.x;
        int dy = pointer.y - capturePoint.y;
        if (dx != 0 || dy != 0) {
            robot.mouseMove(capturePoint.x, capturePoint.y);
        }
        return new MouseDelta(dx, dy);
    }

    public boolean isAvailable() {
        return robot != null;
    }

    public void release() {
        capturePoint = null;
    }

    private static Point currentPointer() {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        return pointerInfo == null ? null : pointerInfo.getLocation();
    }

    private static Robot createRobot() {
        try {
            Robot created = new Robot();
            created.setAutoDelay(0);
            return created;
        } catch (AWTException | SecurityException e) {
            return null;
        }
    }

    public record MouseDelta(int x, int y) {
        public static final MouseDelta NONE = new MouseDelta(0, 0);
    }
}
