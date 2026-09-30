package io.github.kosmx.bendylib.neoforge.emf;

import io.github.kosmx.bendylib.neoforge.BendMemory;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Records the bend Player Animator just applied. The target class is named as a string so this
 * mixin can be skipped when Player Animator is not installed.
 */
@Mixin(targets = "dev.kosmx.playerAnim.impl.animation.BendHelper")
public class BendRememberMixin {
    @Inject(method = "initBend(Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/core/Direction;)V", at = @At("TAIL"))
    private void bendylib$rememberInit(ModelPart part, Direction direction, CallbackInfo ci) {
        BendMemory.rememberDirection(part, direction);
    }

    @Inject(method = "bend(Lnet/minecraft/client/model/geom/ModelPart;FF)V", at = @At("TAIL"))
    private void bendylib$rememberBend(ModelPart part, float bendAxis, float bendValue, CallbackInfo ci) {
        BendMemory.rememberBend(part, bendAxis, bendValue);
    }
}
