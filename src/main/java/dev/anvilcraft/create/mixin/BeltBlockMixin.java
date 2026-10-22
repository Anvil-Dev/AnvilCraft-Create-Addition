package dev.anvilcraft.create.mixin;

import com.simibubi.create.content.kinetics.belt.BeltBlock;
import dev.anvilcraft.create.integration.belt.BeltWorkstation;
import dev.anvilcraft.create.integration.belt.IBeltWorkstationHost;
import dev.dubhe.anvilcraft.block.FishTankBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin类用于修改 {@link BeltBlock} 的行为。
 *
 * <p>
 * 装了工位的传送带格要额外提供外壳的碰撞与选中形状，否则外壳看起来悬空、点不到。
 */
@Mixin(BeltBlock.class)
abstract class BeltBlockMixin {
    /**
     * 把工位外壳的形状合并到传送带形状上。
     *
     * @param state   传送带方块状态
     * @param level   世界
     * @param pos     位置
     * @param context 碰撞上下文
     * @param cir     返回值
     */
    @Inject(method = {"getShape", "getCollisionShape"}, at = @At("RETURN"), cancellable = true)
    private void aca$workstationShape(
        BlockState state,
        BlockGetter level,
        BlockPos pos,
        CollisionContext context,
        CallbackInfoReturnable<VoxelShape> cir
    ) {
        if (!(level.getBlockEntity(pos) instanceof IBeltWorkstationHost host)) {
            return;
        }
        BeltWorkstation workstation = host.aca$workstation();
        if (workstation == null) {
            return;
        }
        VoxelShape shell = workstation.isFishTank()
            ? FishTankBlock.SHAPE
            : workstation.shellState().getShape(level, pos, context);
        cir.setReturnValue(Shapes.or(cir.getReturnValue(), shell));
    }
}
