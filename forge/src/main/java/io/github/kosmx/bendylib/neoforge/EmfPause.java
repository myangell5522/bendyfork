package io.github.kosmx.bendylib.neoforge;

import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import org.slf4j.Logger;
import traben.entity_model_features.EMFAnimationApi;
import traben.entity_model_features.utils.EMFEntity;

/**
 * Fresh Animations rewrites arm and leg rotations every frame.
 * While a Player Animator layer is playing, pause that script so the pose and the bend stay on the EMF mesh.
 * This class stays outside the EMF mixin package: Mixin forbids loading those classes from the mod constructor.
 */
public final class EmfPause {
    private EmfPause() {}

    public static void register(Logger logger) {
        try {
            boolean registered = EMFAnimationApi.registerPauseCondition(EmfPause::shouldPause);
            logger.info("Registered EMF pause for Player Animator ({})", registered);
        } catch (Throwable e) {
            logger.error("Failed to register EMF animation pause", e);
        }
    }

    private static boolean shouldPause(EMFEntity entity) {
        if (!(entity instanceof IAnimatedPlayer animated)) {
            return false;
        }
        AnimationApplier animation = animated.playerAnimator_getAnimation();
        return animation != null && animation.isActive();
    }
}
