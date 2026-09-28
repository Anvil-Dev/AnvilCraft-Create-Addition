package dev.anvilcraft.create.api.injection.block.entity;

/**
 * 手摇曲柄摇动速度倍率的访问接口，由 {@code HandCrankBlockEntityMixin} 实现。
 */
public interface IHandCrankBlockEntityExtension {
    /**
     * 设置手摇曲柄的摇动速度倍率，摇动时曲柄产出的转速为该倍率与原速的乘积。
     *
     * @param multiplier 摇动速度倍率，1 为原速
     */
    void aca$setSpeedMultiplier(double multiplier);
}
