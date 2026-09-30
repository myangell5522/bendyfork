package io.github.kosmx.bendylib;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.kosmx.bendylib.impl.ICuboid;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * ModelPart to support ICuboids
 * <p>
 * If you want to mutate existing Cuboids, see {@link ModelPartAccessor} and {@link MutableCuboid}
 *
 * This can be used with {@link ICuboid}.
 */
public abstract class MutableModelPart extends ModelPart {

    @Nullable
    @Deprecated
    private MutableModelPart last = null;

    protected final ObjectList<ICuboid> iCuboids = new ObjectArrayList<>();

    public MutableModelPart(List<Cube> cuboids, Map<String, ModelPart> children) {
        super(cuboids, children);
    }


    @Override
    public void render(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        super.render(matrices, vertices, light, overlay);
        if(!iCuboids.isEmpty()){
            matrices.pushPose();
            this.translateAndRotate(matrices);
            this.renderICuboids(matrices.last(), vertices, light, overlay, color);
            matrices.popPose();
        }
    }

    protected void renderICuboids(PoseStack.Pose matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        this.iCuboids.forEach((cuboid)-> cuboid.render(matrices, vertexConsumer, light, overlay, color));
    }

    public void addICuboid(ICuboid cuboid){
        this.iCuboids.add(cuboid);
    }

}
