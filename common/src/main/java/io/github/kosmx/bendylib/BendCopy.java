package io.github.kosmx.bendylib;

import io.github.kosmx.bendylib.impl.BendableCuboid;
import io.github.kosmx.bendylib.impl.IBendable;
import io.github.kosmx.bendylib.impl.RememberingPos;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;

import java.util.List;
import java.util.Map;

/**
 * Copies an active {@link BendableCuboid} onto another cuboid that shares the same model space.
 * Used by 3D Skin Layers and by Entity Model Features, whose cubes are not the ones Player Animator bends.
 */
public final class BendCopy {
    private BendCopy() {}

    public static MutableCuboid findActive(Iterable<ModelPart.Cube> cubes) {
        if (cubes == null) return null;
        for (ModelPart.Cube cube : cubes) {
            if (cube instanceof MutableCuboid mutable && isBend(mutable)) {
                return mutable;
            }
        }
        return null;
    }

    /**
     * Registers the source bend on {@code target} and copies the current axis and angle.
     * Clears {@code target} when the source is not bending.
     */
    public static void syncBend(MutableCuboid source, MutableCuboid target) {
        if (source == target) return;
        var active = source.getActiveMutator();
        if (active == null || !(active.getB() instanceof BendableCuboid bendable)) {
            if (target.getActiveMutator() != null) {
                target.getAndActivateMutator(null);
            }
            return;
        }
        String key = active.getA();
        if (!target.hasMutator(key)) {
            target.registerMutator(key, data -> new BendableCuboid.Builder().setDirection(bendable.getBendDirection()).build(data,
                    (sides, positions, minX, minY, minZ, maxX, maxY, maxZ, fixX, fixY, fixZ,
                     direction, basePlane, otherPlane, fullSize) ->
                            new CopiedBendableCuboid(sides, positions, minX, minY, minZ, maxX, maxY, maxZ, fixX, fixY, fixZ, direction,
                                    bendable.getBasePlane().scaled(1f), bendable.getOtherSidePlane().scaled(1f), bendable.bendHeight())));
        }
        target.copyStateFrom(source);
    }

    public static void applyToPart(MutableCuboid source, ModelPart part) {
        if (part == null || source == null) return;
        List<ModelPart.Cube> cubes = ModelPartAccessor.getCuboids(part);
        if (cubes != null) {
            for (ModelPart.Cube cube : cubes) {
                if (cube instanceof MutableCuboid mutable) {
                    syncBend(source, mutable);
                }
            }
        }
        Map<String, ModelPart> children = ModelPartAccessor.getChildren(part);
        if (children == null) return;
        for (ModelPart child : children.values()) {
            applyToPart(source, child);
        }
    }

    public static void clearPart(ModelPart part) {
        if (part == null) return;
        List<ModelPart.Cube> cubes = ModelPartAccessor.getCuboids(part);
        if (cubes != null) {
            for (ModelPart.Cube cube : cubes) {
                if (cube instanceof MutableCuboid mutable && mutable.getActiveMutator() != null) {
                    mutable.getAndActivateMutator(null);
                }
            }
        }
        Map<String, ModelPart> children = ModelPartAccessor.getChildren(part);
        if (children == null) return;
        for (ModelPart child : children.values()) {
            clearPart(child);
        }
    }

    private static boolean isBend(MutableCuboid mutable) {
        var active = mutable.getActiveMutator();
        return active != null && active.getB() instanceof BendableCuboid;
    }

    private static final class CopiedBendableCuboid extends BendableCuboid {
        private CopiedBendableCuboid(BendableCuboid.Quad[] sides, RememberingPos[] positions, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float fixX, float fixY, float fixZ, Direction direction, IBendable.Plane basePlane, IBendable.Plane otherPlane, float fullSize) {
            super(sides, positions, minX, minY, minZ, maxX, maxY, maxZ, fixX, fixY, fixZ, direction, basePlane, otherPlane, fullSize);
        }
    }
}
