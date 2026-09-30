package io.github.kosmx.bendylib.impl;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;

import java.util.List;

/**
 * Minecraft Cuboid object
 * define it as you wish, render it as you wish!
 * You can use {@link BendableCuboid} to bend parts.
 */
public interface ICuboid {

    /**
     * See {@link BendableCuboid#render(PoseStack.Pose, VertexConsumer, int, int, int)} how to do it
     * Or you can check the original MC code {@link ModelPart#compile(PoseStack.Pose, VertexConsumer, int, int, int)}
     *
     * @param pose Minecraft's Matrix transformation
     * @param vertexConsumer Minecraft Vertex consumer, add vertices to render
     * @param light light
     * @param overlay overlay
     */
    void render(PoseStack.Pose pose, VertexConsumer vertexConsumer, int light, int overlay, int color);

    /**
     * Copy custom state from another cuboid
     * @param other other ICuboid
     */
    void copyState(ICuboid other);

    /**
     * Disable mutation after invoking {@link ICuboid#render(PoseStack.Pose, VertexConsumer, int, int, int)}
     * @return true or false...
     */
    default boolean disableAfterDraw(){
        return true;
    }

    /**
     * Convert custom Quads to {@link net.minecraft.client.model.geom.ModelPart.Polygon}
     * Needed for Shader fix
     * @return list of converted quads
     */
    default List<ModelPart.Polygon> getQuads(){
        return null;
    }
}
