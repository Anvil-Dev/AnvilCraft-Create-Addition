package dev.anvilcraft.create.mixin;

import com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity;
import dev.anvilcraft.create.api.injection.block.entity.IHandCrankBlockEntityExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin类用于修改 {@link HandCrankBlockEntity} 的行为。
 * 此类记录摇动速度倍率并作用于曲柄产出的转速，使得手摇曲柄与阀门手轮都能被护符加速。
 */
@Mixin(HandCrankBlockEntity.class)
abstract class HandCrankBlockEntityMixin implements IHandCrankBlockEntityExtension {
    /**
     * 曲柄正在被摇动的剩余tick数（由Mixin注入）。
     */
    @Shadow
    public int inUse;

    /**
     * 摇动速度倍率，停止摇动后恢复为原速。
     */
    @Unique
    private double aca$speedMultiplier = 1.0;

    @Override
    public void aca$setSpeedMultiplier(double multiplier) {
        this.aca$speedMultiplier = multiplier;
    }

    /**
     * 按摇动速度倍率放大曲柄产出的转速。
     *
     * @param cir 原方法返回值
     */
    @Inject(method = "getGeneratedSpeed", at = @At("RETURN"), cancellable = true)
    private void aca$applySpeedMultiplier(CallbackInfoReturnable<Float> cir) {
        float generatedSpeed = cir.getReturnValue();
        if (generatedSpeed == 0 || this.aca$speedMultiplier == 1.0) {
            return;
        }
        cir.setReturnValue((float) (generatedSpeed * this.aca$speedMultiplier));
    }

    /**
     * 摇动停止后清除摇动速度倍率，避免影响其它方式驱动的曲柄。
     *
     * @param ci 回调信息
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void aca$resetSpeedMultiplier(CallbackInfo ci) {
        if (this.inUse == 0) {
            this.aca$speedMultiplier = 1.0;
        }
    }
}
