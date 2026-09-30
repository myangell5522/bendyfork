package io.github.kosmx.bendylib.neoforge.emf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.kosmx.bendylib.MutableCuboid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * {@code EMFCube.compile} overrides {@code ModelPart.Cube.compile}, so the bend redirect in
 * {@code CuboidMutator} never runs for Fresh Animations boxes.
 */
@Mixin(targets = "traben.entity_model_features.models.parts.EMFModelPartCustom$EMFCube")
public class EMFCubeCompileMixin {
    @Inject(method = "compile", at = @At("HEAD"), cancellable = true)
    private void bendylib$renderBend(PoseStack.Pose pose, VertexConsumer vertexConsumer, int light, int overlay, int color, CallbackInfo ci) {
        if (this instanceof MutableCuboid mutable && mutable.getActiveMutator() != null) {
            mutable.getActiveMutator().getB().render(pose, vertexConsumer, light, overlay, color);
            ci.cancel();
        }
    }
}
