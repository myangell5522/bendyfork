package io.github.kosmx.bendylib.neoforge;

import io.github.kosmx.bendylib.ModelPartAccessor;
import io.github.kosmx.bendylib.MutableCuboid;
import io.github.kosmx.bendylib.impl.BendableCuboid;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;

import java.util.List;
import java.util.WeakHashMap;

/**
 * Player Animator bends cuboid 0 during setupAnim. EMF then swaps that list for the CEM cubes.
 * The angles stay on the model part so the cubes about to be drawn can be bent with their own planes.
 */
public final class BendMemory {
    private static final String KEY = "bend";
    private static final WeakHashMap<ModelPart, State> BY_PART = new WeakHashMap<>();

    private BendMemory() {}

    public static void rememberDirection(ModelPart part, Direction direction) {
        if (part == null || direction == null) return;
        state(part).direction = direction;
    }

    public static void rememberBend(ModelPart part, float bendAxis, float bendValue) {
        if (part == null) return;
        State state = state(part);
        state.bendAxis = bendAxis;
        state.bendValue = bendValue;
        state.hasBend = true;
    }

    public static boolean hasBend(ModelPart part) {
        State state = BY_PART.get(part);
        return state != null && state.hasBend;
    }

    /**
     * Registers a bend on the part's current cubes and applies the remembered axis and angle.
     * Does not remove a mutator when the angle is zero.
     */
    public static void apply(ModelPart part) {
        State state = BY_PART.get(part);
        if (state == null || !state.hasBend) return;
        List<ModelPart.Cube> cubes = ModelPartAccessor.getCuboids(part);
        if (cubes == null) return;
        for (ModelPart.Cube cube : cubes) {
            if (!(cube instanceof MutableCuboid mutable)) continue;
            if (!mutable.hasMutator(KEY)) {
                if (state.direction == null) continue;
                Direction direction = state.direction;
                mutable.registerMutator(KEY, data -> new BendableCuboid.Builder().setDirection(direction).build(data));
            }
            if (mutable.getAndActivateMutator(KEY) instanceof BendableCuboid bendable) {
                bendable.applyBend(state.bendAxis, state.bendValue);
            }
        }
    }

    private static State state(ModelPart part) {
        return BY_PART.computeIfAbsent(part, ignored -> new State());
    }

    private static final class State {
        private Direction direction;
        private float bendAxis;
        private float bendValue;
        private boolean hasBend;
    }
}
