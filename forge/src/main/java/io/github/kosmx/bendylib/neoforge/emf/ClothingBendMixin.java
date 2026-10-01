package io.github.kosmx.bendylib.neoforge.emf;

import io.github.kosmx.bendylib.neoforge.BendMemory;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Player Animator bends the limb after vanilla setupAnim has already copied it onto the sleeve.
 * Priority is below Player Animator so this runs after that emote and the outer mesh follows.
 */
@Mixin(value = PlayerModel.class, priority = 500)
public abstract class ClothingBendMixin {
    @Shadow public ModelPart rightArm;
    @Shadow public ModelPart leftArm;
    @Shadow public ModelPart rightLeg;
    @Shadow public ModelPart leftLeg;
    @Shadow public ModelPart body;
    @Shadow public ModelPart rightSleeve;
    @Shadow public ModelPart leftSleeve;
    @Shadow public ModelPart rightPants;
    @Shadow public ModelPart leftPants;
    @Shadow public ModelPart jacket;

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("RETURN"))
    private void bendylib$outerLayers(CallbackInfo ci) {
        follow(this.rightArm, this.rightSleeve);
        follow(this.leftArm, this.leftSleeve);
        follow(this.rightLeg, this.rightPants);
        follow(this.leftLeg, this.leftPants);
        follow(this.body, this.jacket);
    }

    private static void follow(ModelPart limb, ModelPart outer) {
        if (limb == null || outer == null) return;
        outer.copyFrom(limb);
        BendMemory.mirror(limb, outer);
    }
}
