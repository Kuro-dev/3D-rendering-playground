package org.kurodev.command;

import org.kurodev.world.obj.DynamicObject;
import org.kurodev.world.obj.StaticObject;
import org.kurodev.world.WorldManager;
import org.kurodev.world.obj.WorldObject;
import org.kurodev.world.obj.WorldPosition;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Parsed command arguments plus access to the world and reply sink.
 */
public final class ParsedArgs {
    private final String command;
    private final List<String> args;
    private final WorldManager worldManager;
    private final Consumer<String> output;

    ParsedArgs(String command, List<String> args, WorldManager worldManager, Consumer<String> output) {
        this.command = Objects.requireNonNull(command, "command");
        this.args = List.copyOf(Objects.requireNonNull(args, "args"));
        this.worldManager = Objects.requireNonNull(worldManager, "worldManager");
        this.output = Objects.requireNonNull(output, "output");
    }

    public String command() {
        return command;
    }

    public List<String> args() {
        return args;
    }

    public WorldManager world() {
        return worldManager;
    }

    public Optional<WorldObject> findObject(String name) {
        return worldManager.getObject(name);
    }

    public Optional<DynamicObject> findDynamicObject(String name) {
        return findObject(name).filter(DynamicObject.class::isInstance).map(DynamicObject.class::cast);
    }

    public Optional<StaticObject> findStaticObject(String name) {
        return findObject(name).filter(StaticObject.class::isInstance).map(StaticObject.class::cast);
    }

    public void reply(String message) {
        output.accept(message);
    }

    public String string(int index) {
        return args.get(index);
    }

    public double doubleValue(int index, double currentValue) {
        return parseCoordinateToken(string(index), currentValue);
    }

    public WorldPosition parsedPosition(WorldPosition current) {
        if (args.size() < 3) {
            throw new IllegalArgumentException("Expected 3 coordinates");
        }
        return new WorldPosition(
                parseCoordinateToken(args.get(0), current.x()),
                parseCoordinateToken(args.get(1), current.y()),
                parseCoordinateToken(args.get(2), current.z())
        );
    }

    public static double parseCoordinateToken(String token, double currentValue) {
        String trimmed = token.trim();
        if (trimmed.equals("~")) {
            return currentValue;
        }
        if (trimmed.startsWith("+") || trimmed.startsWith("-")) {
            return currentValue + Double.parseDouble(trimmed);
        }
        return Double.parseDouble(trimmed);
    }

    @Override
    public String toString() {
        return "ParsedArgs{" +
                "command='" + command + '\'' +
                ", args=" + Arrays.toString(args.toArray()) +
                '}';
    }
}
