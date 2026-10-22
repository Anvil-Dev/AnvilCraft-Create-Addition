package dev.anvilcraft.create.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.anvilcraft.create.integration.belt.BeltWorkstationAccess;
import dev.dubhe.anvilcraft.event.InWorldRecipeEventListener;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixin类用于修改 {@link InWorldRecipeEventListener} 的行为。
 *
 * <p>
 * 方块实体查找要能看见工位挂载的加工台，否则传送带上的加工台收不到“配方已执行”的反馈，动画与消耗都会停住。
 */
@Mixin(InWorldRecipeEventListener.class)
abstract class InWorldRecipeEventListenerMixin {
    /**
     * 方块实体查询改为优先返回工位里的加工台。
     *
     * @param level    世界
     * @param pos      位置
     * @param original 原始实现
     * @return 方块实体
     */
    @WrapOperation(
        method = "inWorldRecipe(Ldev/anvilcraft/lib/v2/recipe/event/InWorldRecipeEvent;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getBlockEntity(Lnet/minecraft/core/BlockPos;)"
                + "Lnet/minecraft/world/level/block/entity/BlockEntity;"
        )
    )
    private static BlockEntity aca$attached(ServerLevel level, BlockPos pos, Operation<BlockEntity> original) {
        return BeltWorkstationAccess.entity(level, pos);
    }
}
