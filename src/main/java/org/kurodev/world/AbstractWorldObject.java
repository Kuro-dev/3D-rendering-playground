package org.kurodev.world;

import org.kurodev.world.shape.Shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

abstract class AbstractWorldObject implements WorldObject {
    private final String name;
    private final Shape shape;
    private boolean worldWireframeDirty = true;
    private List<WorldLine> cachedWorldWireframeLines = List.of();

    protected AbstractWorldObject(String name, Shape shape) {
        this.name = Objects.requireNonNull(name, "name");
        this.shape = Objects.requireNonNull(shape, "shape");
    }

    @Override
    public final String name() {
        return name;
    }

    @Override
    public final Shape shape() {
        return shape;
    }

    @Override
    public final List<WorldLine> worldWireframeLines() {
        if (!worldWireframeDirty) {
            return cachedWorldWireframeLines;
        }

        List<WorldLine> localLines = shape.wireframeLines();
        List<WorldLine> transformedLines = new ArrayList<>(localLines.size());
        for (int i = 0; i < localLines.size(); i++) {
            transformedLines.add(translateLine(localLines.get(i)));
        }
        cachedWorldWireframeLines = Collections.unmodifiableList(transformedLines);
        worldWireframeDirty = false;
        return cachedWorldWireframeLines;
    }

    protected final void markWorldWireframeDirty() {
        worldWireframeDirty = true;
    }

    protected final void cacheWorldWireframeLines(List<WorldLine> worldLines) {
        cachedWorldWireframeLines = Collections.unmodifiableList(new ArrayList<>(worldLines));
        worldWireframeDirty = false;
    }

    protected abstract WorldLine translateLine(WorldLine localLine);
}
