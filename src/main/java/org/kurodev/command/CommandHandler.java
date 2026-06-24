package org.kurodev.command;

import org.kurodev.jpixelgameengine.gfx.Pixel;
import org.kurodev.world.obj.DynamicObject;
import org.kurodev.world.obj.StaticObject;
import org.kurodev.world.WorldManager;
import org.kurodev.world.obj.WorldObject;
import org.kurodev.world.obj.WorldPosition;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Simple command registry and parser for the in-game console.
 */
public final class CommandHandler {
    private final WorldManager worldManager;
    private final Map<String, Consumer<ParsedArgs>> commands = new LinkedHashMap<>();

    public CommandHandler(WorldManager worldManager) {
        this.worldManager = Objects.requireNonNull(worldManager, "worldManager");
    }

    public CommandHandler register(String command, Consumer<ParsedArgs> callback) {
        commands.put(normalize(command), Objects.requireNonNull(callback, "callback"));
        return this;
    }

    public CommandHandler registerDefaults() {
        register("help", this::help);
        register("ls", this::listObjects);
        register("mv", this::moveObject);
        register("tp", this::teleportObject);
        register("pos", this::printPosition);
        register("cam", this::printCamera);
        register("color", this::setColor);
        register("rot", this::rotateObject);
        register("pivot", this::setPivot);
        register("rm", this::removeObject);
        return this;
    }

    public boolean handle(String rawInput, Consumer<String> output) {
        Objects.requireNonNull(output, "output");
        String input = rawInput == null ? "" : rawInput.trim();
        if (input.isEmpty()) {
            return true;
        }

        String[] parts = input.split("\\s+");
        String command = normalize(parts[0]);
        Consumer<ParsedArgs> callback = commands.get(command);
        if (callback == null) {
            output.accept("Unknown command: " + parts[0]);
            return false;
        }

        ParsedArgs parsedArgs = new ParsedArgs(command, List.of(parts).subList(1, parts.length), worldManager, output);
        try {
            callback.accept(parsedArgs);
            return true;
        } catch (RuntimeException ex) {
            output.accept("Command failed: " + ex.getMessage());
            return false;
        }
    }

    private void listObjects(ParsedArgs args) {
        if (worldManager.objects().isEmpty()) {
            args.reply("No objects in the world.");
            return;
        }

        for (WorldObject object : worldManager.objects()) {
            args.reply(describe(object));
        }
    }

    private void moveObject(ParsedArgs args) {
        if (args.args().size() != 4) {
            throw new IllegalArgumentException("Usage: mv [objectname] x y z");
        }

        String objectName = args.string(0);
        args.findDynamicObject(objectName).ifPresentOrElse(dynamic -> {
            WorldPosition current = dynamic.position();
            WorldPosition next = new WorldPosition(
                    ParsedArgs.parseCoordinateToken(args.string(1), current.x()),
                    ParsedArgs.parseCoordinateToken(args.string(2), current.y()),
                    ParsedArgs.parseCoordinateToken(args.string(3), current.z())
            );
            dynamic.setPosition(next);
            args.reply(String.format(Locale.ROOT, "%s moved to %s", dynamic.name(), format(next)));
        }, () -> {
            if (args.findObject(objectName).isPresent()) {
                args.reply("Object is static and cannot move: " + objectName);
            } else {
                args.reply("Object not found: " + objectName);
            }
        });
    }

    private void teleportObject(ParsedArgs args) {
        moveObject(args);
    }

    private void printPosition(ParsedArgs args) {
        if (args.args().size() != 1) {
            throw new IllegalArgumentException("Usage: pos [objectname]");
        }
        args.findObject(args.string(0)).ifPresentOrElse(object -> {
            if (object instanceof DynamicObject dynamic) {
                args.reply(dynamic.name() + " @ " + format(dynamic.position()));
                return;
            }
            StaticObject staticObject = (StaticObject) object;
            args.reply(staticObject.name() + " @ " + format(staticObject.coordinate().toPosition()));
        }, () -> args.reply("Object not found: " + args.string(0)));
    }

    private void rotateObject(ParsedArgs args) {
        if (args.args().size() != 4) {
            throw new IllegalArgumentException("Usage: rot [objectname] yaw pitch roll");
        }
        String objectName = args.string(0);
        args.findDynamicObject(objectName).ifPresentOrElse(dynamic -> {
            double yaw = ParsedArgs.parseCoordinateToken(args.string(1), dynamic.transform().yawRadians());
            double pitch = ParsedArgs.parseCoordinateToken(args.string(2), dynamic.transform().pitchRadians());
            double roll = ParsedArgs.parseCoordinateToken(args.string(3), dynamic.transform().rollRadians());
            dynamic.setRotation(yaw, pitch, roll);
            args.reply(String.format(Locale.ROOT, "%s rotation set to (%.3f, %.3f, %.3f)",
                    dynamic.name(), yaw, pitch, roll));
        }, () -> {
            if (args.findObject(objectName).isPresent()) {
                args.reply("Object is static and cannot rotate: " + objectName);
            } else {
                args.reply("Object not found: " + objectName);
            }
        });
    }

    private void setPivot(ParsedArgs args) {
        if (args.args().size() != 4) {
            throw new IllegalArgumentException("Usage: pivot [objectname] x y z");
        }
        String objectName = args.string(0);
        args.findDynamicObject(objectName).ifPresentOrElse(dynamic -> {
            WorldPosition current = dynamic.transform().pivot();
            WorldPosition pivot = new WorldPosition(
                    ParsedArgs.parseCoordinateToken(args.string(1), current.x()),
                    ParsedArgs.parseCoordinateToken(args.string(2), current.y()),
                    ParsedArgs.parseCoordinateToken(args.string(3), current.z())
            );
            dynamic.setPivot(pivot);
            args.reply(String.format(Locale.ROOT, "%s pivot set to %s", dynamic.name(), format(pivot)));
        }, () -> {
            if (args.findObject(objectName).isPresent()) {
                args.reply("Object is static and cannot change pivot: " + objectName);
            } else {
                args.reply("Object not found: " + objectName);
            }
        });
    }

    private void removeObject(ParsedArgs args) {
        if (args.args().size() != 1) {
            throw new IllegalArgumentException("Usage: rm [objectname]");
        }
        args.findObject(args.string(0)).ifPresentOrElse(object -> {
            worldManager.removeObject(object);
            args.reply("Removed " + object.name());
        }, () -> args.reply("Object not found: " + args.string(0)));
    }

    private void help(ParsedArgs args) {
        args.reply("help - list commands");
        args.reply("ls - list all objects");
        args.reply("cam - show camera position and angle");
        args.reply("pos [objectname] - show one object's position");
        args.reply("color [objectname] [named-color] - change an object's color");
        args.reply("mv [objectname] x y z - move a dynamic object");
        args.reply("tp [objectname] x y z - alias for mv");
        args.reply("rot [objectname] yaw pitch roll - set a dynamic object's rotation");
        args.reply("pivot [objectname] x y z - set a dynamic object's pivot");
        args.reply("rm [objectname] - remove an object from the world");
    }

    private void printCamera(ParsedArgs args) {
        WorldPosition position = worldManager.camera().position();
        args.reply(String.format(Locale.ROOT, "Camera @ %s yaw=%.3f pitch=%.3f",
                format(position),
                worldManager.camera().viewAngle().yawDegrees(),
                worldManager.camera().viewAngle().pitchDegrees()));
    }

    private static String format(WorldPosition position) {
        return String.format(Locale.ROOT, "(%.3f, %.3f, %.3f)", position.x(), position.y(), position.z());
    }

    private static String describe(WorldObject object) {
        String type = object instanceof DynamicObject ? "dynamic" : "static";
        String position = object instanceof DynamicObject dynamic
                ? format(dynamic.position())
                : format(((StaticObject) object).coordinate().toPosition());
        return String.format(Locale.ROOT, "%s [%s] %s @ %s",
                object.name(), type, object.shape().getClass().getSimpleName(), position) + " color=" + colorName(object.color());
    }

    private void setColor(ParsedArgs args) {
        if (args.args().size() != 2) {
            throw new IllegalArgumentException("Usage: color [objectname] [named-color]");
        }
        String objectName = args.string(0);
        args.findObject(objectName).ifPresentOrElse(object -> {
            Pixel color = parseColor(args.string(1));
            object.setColor(color);
            args.reply(object.name() + " color set to " + colorName(color));
        }, () -> args.reply("Object not found: " + objectName));
    }

    private static String normalize(String command) {
        return command.toLowerCase(Locale.ROOT);
    }

    private static Pixel parseColor(String token) {
        String color = token.trim().toLowerCase(Locale.ROOT);
        return switch (color) {
            case "black" -> Pixel.BLACK;
            case "white" -> Pixel.WHITE;
            case "red" -> Pixel.RED;
            case "green" -> Pixel.GREEN;
            case "blue" -> Pixel.BLUE;
            case "cyan" -> Pixel.CYAN;
            case "magenta" -> Pixel.MAGENTA;
            case "yellow" -> Pixel.YELLOW;
            case "gray", "grey" -> Pixel.GRAY;
            case "orange" -> Pixel.ORANGE;
            case "pink" -> Pixel.PINK;
            default -> throw new IllegalArgumentException("Unknown color: " + token);
        };
    }

    private static String colorName(Pixel color) {
        if (Pixel.BLACK.equals(color)) return "black";
        if (Pixel.WHITE.equals(color)) return "white";
        if (Pixel.RED.equals(color)) return "red";
        if (Pixel.GREEN.equals(color)) return "green";
        if (Pixel.BLUE.equals(color)) return "blue";
        if (Pixel.CYAN.equals(color)) return "cyan";
        if (Pixel.MAGENTA.equals(color)) return "magenta";
        if (Pixel.YELLOW.equals(color)) return "yellow";
        if (Pixel.GRAY.equals(color)) return "gray";
        if (Pixel.ORANGE.equals(color)) return "orange";
        if (Pixel.PINK.equals(color)) return "pink";
        return String.format(Locale.ROOT, "#%08X", color.getRGBA());
    }
}
