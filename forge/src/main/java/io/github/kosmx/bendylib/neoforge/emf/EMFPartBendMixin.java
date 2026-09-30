package io.github.kosmx.bendylib.neoforge.emf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.kosmx.bendylib.BendCopy;
import io.github.kosmx.bendylib.ModelPartAccessor;
import io.github.kosmx.bendylib.MutableCuboid;
import io.github.kosmx.bendylib.neoforge.BendMemory;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.parts.EMFModelPart;
import traben.entity_model_features.models.parts.EMFModelPartVanilla;
import traben.entity_model_features.models.parts.EMFModelPartWithState;

/**
 * EMF swaps a part's cube list for the CEM variant after it animates.
 * Player Animator has already bent the vanilla cubes, so copy that bend onto the cubes about to be drawn.
 */
@Mixin(EMFModelPart.class)
public abstract class EMFPartBendMixin {
    @Inject(method = "renderLikeVanilla", at = @At("HEAD"))
    private void bendylib$syncBend(PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, int color, CallbackInfo ci) {
        ModelPart self = (ModelPart) (Object) this;
        if (BendMemory.hasBend(self)) {
            BendMemory.apply(self);
            return;
        }
        if (!((Object) this instanceof EMFModelPartVanilla vanilla)) {
            return;
        }
        MutableCuboid source = findSource(vanilla);
        if (source == null) {
            return;
        }
        BendCopy.applyToPart(source, self);
    }

    private static MutableCuboid findSource(EMFModelPartVanilla part) {
        MutableCuboid current = BendCopy.findActive(ModelPartAccessor.getCuboids(part));
        if (current != null) {
            return current;
        }
        for (EMFModelPartWithState.EMFModelState state : part.allKnownStateVariants.values()) {
            MutableCuboid found = BendCopy.findActive(state.cuboids());
            if (found != null) {
                return found;
            }
        }
        return null;
    }
}
