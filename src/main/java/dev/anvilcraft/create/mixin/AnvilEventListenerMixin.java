package dev.anvilcraft.create.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.anvilcraft.create.integration.belt.BeltWorkstation;
import dev.anvilcraft.create.integration.belt.BeltWorkstationAccess;
import dev.dubhe.anvilcraft.api.event.AnvilEvent;
import dev.dubhe.anvilcraft.event.anvil.AnvilEventListener;
import dev.dubhe.anvilcraft.init.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

/**
 * Mixin类用于修改 {@link AnvilEventListener} 的行为。
 *
 * <p>
 * 让铁砧配方在传送带工位上运行时，看到的是工位外壳而不是传送带本体，并把这一轮配方产生的掉落物收进工位。
 */
@Mixin(AnvilEventListener.class)
abstract class AnvilEventListenerMixin {
    /**
     * 把一次落砧配方处理包裹进工位作用域。
     *
     * @param event    落砧事件
     * @param original 原始实现
     */
    @WrapMethod(method = "handleNeoAnvilRecipe")
    private static void aca$inStrike(AnvilEvent.OnLand event, Operation<Void> original) {
        BeltWorkstationAccess.inStrike(event, () -> original.call(event));
    }

    /**
     * 方块状态查询改为读取工位外壳。
     *
     * @param level    世界
     * @param pos      位置
     * @param original 原始实现
     * @return 方块状态
     */
    @WrapOperation(
        method = "handleNeoAnvilRecipe",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)"
                + "Lnet/minecraft/world/level/block/state/BlockState;"
        )
    )
    private static BlockState aca$shellState(Level level, BlockPos pos, Operation<BlockState> original) {
        return BeltWorkstationAccess.state(level, pos);
    }

    /**
     * 鱼缸外壳的方块实体查询改回工位本体。
     *
     * @param level    世界
     * @param pos      位置
     * @param type     方块实体类型
     * @param original 原始实现
     * @return 方块实体
     */
    @WrapOperation(
        method = "handleNeoAnvilRecipe",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;"
                + "Lnet/minecraft/world/level/block/entity/BlockEntityType;)Ljava/util/Optional;"
        )
    )
    private static Optional<?> aca$fishTankEntity(
        Level level,
        BlockPos pos,
        BlockEntityType<?> type,
        Operation<Optional<?>> original
    ) {
        BeltWorkstation workstation = BeltWorkstationAccess.at(level, pos);
        if (workstation != null && workstation.isFishTank() && type == ModBlockEntities.FISH_TANK.get()) {
            return Optional.of(workstation);
        }
        return original.call(level, pos, type);
    }

    /**
     * 工位下方不再触发切石机分支。
     *
     * @param level    世界
     * @param pos      位置
     * @param state    方块状态
     * @param original 原始实现
     * @return 是否存在切石机
     */
    @WrapOperation(
        method = "onLand",
        at = @At(
            value = "INVOKE",
            target = "Ldev/dubhe/anvilcraft/event/anvil/AnvilEventListener;hasStonecutterBelow("
                + "Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;"
                + "Lnet/minecraft/world/level/block/state/BlockState;)Z"
        )
    )
    private static boolean aca$keepBelt(Level level, BlockPos pos, BlockState state, Operation<Boolean> original) {
        return BeltWorkstationAccess.at(level, pos) == null && original.call(level, pos, state);
    }
}
