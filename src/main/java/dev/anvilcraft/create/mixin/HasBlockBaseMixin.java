package dev.anvilcraft.create.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.anvilcraft.create.integration.belt.IBeltWorkstationHost;
import dev.anvilcraft.lib.v2.recipe.predicate.block.HasBlockBase;
import dev.anvilcraft.lib.v2.util.predicate.BlockStatePredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixin类用于修改 {@link HasBlockBase} 的行为。
 *
 * <p>
 * 传送带上的工位外壳永远“浮”在传送带格子上，因此工位下方的方块也要参与底方块判定。
 */
@Mixin(HasBlockBase.class)
abstract class HasBlockBaseMixin {
    /**
     * 原始判定失败且命中的是工位外壳时，退一格再判定一次。
     *
     * @param predicate 方块状态谓词
     * @param level     世界
     * @param state     方块状态
     * @param entity    方块实体
     * @param original  原始实现
     * @return 是否匹配
     */
    @WrapOperation(
        method = "test",
        at = @At(
            value = "INVOKE",
            target = "Ldev/anvilcraft/lib/v2/util/predicate/BlockStatePredicate;test("
                + "Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/level/block/state/BlockState;"
                + "Lnet/minecraft/world/level/block/entity/BlockEntity;)Z"
        )
    )
    private boolean aca$base(
        BlockStatePredicate predicate,
        LevelAccessor level,
        BlockState state,
        BlockEntity entity,
        Operation<Boolean> original
    ) {
        if (original.call(predicate, level, state, entity)) {
            return true;
        }
        if (!(entity instanceof IBeltWorkstationHost)) {
            return false;
        }
        BlockPos below = entity.getBlockPos().below();
        return predicate.test(level, level.getBlockState(below), level.getBlockEntity(below));
    }
}
