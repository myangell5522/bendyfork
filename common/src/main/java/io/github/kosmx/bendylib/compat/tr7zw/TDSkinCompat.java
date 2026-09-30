package io.github.kosmx.bendylib.compat.tr7zw;

import dev.tr7zw.skinlayers.api.MeshTransformer;
import dev.tr7zw.skinlayers.api.SkinLayersAPI;
import io.github.kosmx.bendylib.BendCopy;
import io.github.kosmx.bendylib.ModelPartAccessor;
import io.github.kosmx.bendylib.MutableCuboid;
import io.github.kosmx.bendylib.impl.BendableCuboid;
import io.github.kosmx.bendylib.impl.IBendable;
import io.github.kosmx.bendylib.impl.IPosWithOrigin;
import io.github.kosmx.bendylib.impl.RememberingPos;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.Logger;

import java.util.function.Consumer;

/**
 * 3D Skin Layers compatibility, ported from the {@code 3d_layer_compat} branch onto the 1.21 bend math.
 * Targets Skin Layers 1.11.x on Minecraft 1.21.1 ({@code skinlayers3d}).
 * <p>
 * Mesh vertices are in block space (1/16 of cuboid space). {@link BendyMeshTransformer} scales the bend
 * down, then each vertex is run through the same {@link IBendable#applyBend} used by {@link BendableCuboid}.
 */
public class TDSkinCompat {
    public static void init(Logger logger) throws ClassNotFoundException, NoClassDefFoundError {
        logger.info("Initializing 3D Skin Layers compatibility");

        SkinLayersAPI.setupMeshTransformerProvider(modelPart -> {
            var sourceCuboidOptional = ModelPartAccessor.optionalGetCuboid(modelPart, 0);
            if (sourceCuboidOptional.isPresent()
                    && sourceCuboidOptional.get().getActiveMutator() != null
                    && sourceCuboidOptional.get().getActiveMutator().getB() instanceof BendableCuboid bendableSource) {

                class Bender extends BendyMeshTransformer implements MeshTransformer {

                    private Consumer<IPosWithOrigin> transform = null;

                    private Bender(BendableCuboid cuboid) {
                        super(cuboid);
                        // Capture the bend function instead of iterating the source cuboid's vertices.
                        applyBend(bendableSource.getBendAxis(), bendableSource.getBend(), consumer -> transform = consumer);
                        if (transform == null) {
                            throw new IllegalStateException("bend transform was not captured");
                        }
                    }

                    /**
                     * @param normal quad normal, rewritten from the bent vertices
                     * @param vertices quad vertices in block space
                     */
                    @Override
                    public void transform(Vector3f normal, Vector4f[] vertices) {
                        for (Vector4f vertex : vertices) {
                            RememberingPos pos = new RememberingPos(vertex.x(), vertex.y(), vertex.z());
                            transform.accept(pos);
                            Vector3f bent = pos.getPos();
                            vertex.set(bent.x(), bent.y(), bent.z(), vertex.w());
                        }
                        writeNormal(normal, vertices);
                    }

                    @Override
                    public void transform(ModelPart.Cube cuboid) {
                        if (cuboid instanceof MutableCuboid mutableCuboid) {
                            BendCopy.syncBend(sourceCuboidOptional.get(), mutableCuboid);
                        }
                    }
                }
                return new Bender(bendableSource);
            }
            return new MeshTransformer() {
                @Override
                public void transform(Vector3f normal, Vector4f[] vertices) {
                }

                @Override
                public void transform(ModelPart.Cube cuboid) {
                    if (cuboid instanceof MutableCuboid mutableCuboid) {
                        mutableCuboid.getAndActivateMutator(null);
                    }
                }
            };
        });
    }

    /**
     * Same cross-product normal as {@link BendableCuboid.Quad}.
     */
    private static void writeNormal(Vector3f dest, Vector4f[] vertices) {
        Vector3f vecB = new Vector3f(vertices[1].x(), vertices[1].y(), vertices[1].z());
        vecB.sub(vertices[3].x(), vertices[3].y(), vertices[3].z());
        Vector3f vecA = new Vector3f(vertices[0].x(), vertices[0].y(), vertices[0].z());
        vecA.sub(vertices[2].x(), vertices[2].y(), vertices[2].z());
        vecA.cross(vecB);
        if (!vecA.normalize().isFinite()) {
            dest.set(Direction.NORTH.step());
        } else {
            dest.set(vecA);
        }
    }

    /**
     * Bend parameters copied from the player-model cuboid, scaled into skin-mesh space.
     */
    private static class BendyMeshTransformer implements IBendable {
        private final Direction bendDirection;
        private final float bendX, bendY, bendZ;
        private final Plane basePlane, otherSidePlane;
        private final float bendHeight;

        private BendyMeshTransformer(Direction bendDirection, float bendX, float bendY, float bendZ, Plane basePlane, Plane otherSidePlane, float bendHeight) {
            this.bendDirection = bendDirection;
            this.bendX = bendX / 16;
            this.bendY = bendY / 16;
            this.bendZ = bendZ / 16;
            this.basePlane = basePlane.scaled(1 / 16f);
            this.otherSidePlane = otherSidePlane.scaled(1 / 16f);
            this.bendHeight = bendHeight / 16;
        }

        private BendyMeshTransformer(BendableCuboid cuboid) {
            this(cuboid.getBendDirection(), cuboid.getBendX(), cuboid.getBendY(), cuboid.getBendZ(),
                    cuboid.getBasePlane(), cuboid.getOtherSidePlane(), cuboid.bendHeight());
        }

        @Override
        public float bendHeight() {
            return bendHeight;
        }

        @Override
        public Direction getBendDirection() {
            return bendDirection;
        }

        @Override
        public float getBendX() {
            return bendX;
        }

        @Override
        public float getBendY() {
            return bendY;
        }

        @Override
        public float getBendZ() {
            return bendZ;
        }

        @Override
        public Plane getBasePlane() {
            return basePlane;
        }

        @Override
        public Plane getOtherSidePlane() {
            return otherSidePlane;
        }
    }
}
