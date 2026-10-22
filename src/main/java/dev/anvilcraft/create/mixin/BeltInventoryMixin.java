package dev.anvilcraft.create.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import dev.anvilcraft.create.integration.belt.IBeltWorkstationHost;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin类用于修改 {@link BeltInventory} 的行为。
 *
 * <p>
 * 让装了工位的传送带段使用工位自己的加工行为，并且始终视为“未被阻挡”，
 * 否则传送带会停下并跳过加工。
 */
@Mixin(BeltInventory.class)
abstract class BeltInventoryMixin {
    @Shadow
    @Final
    private BeltBlockEntity belt;

    /**
     * 让工位所在的传送带段返回工位的加工行为。
     *
     * @param segment 传送带段序号
     * @param cir     返回值
     */
    @Inject(method = "getBeltProcessingAtSegment", at = @At("HEAD"), cancellable = true)
    private void aca$workstationProcessor(int segment, CallbackInfoReturnable<BeltProcessingBehaviour> cir) {
        BeltBlockEntity segmentBelt = BeltHelper.getSegmentBE(
            this.belt.getLevel(),
            BeltHelper.getPositionForOffset(this.belt, segment)
        );
        if (!(segmentBelt instanceof IBeltWorkstationHost host) || host.aca$workstation() == null) {
            return;
        }
        cir.setReturnValue((BeltProcessingBehaviour) host.aca$processor());
    }

    /**
     * 工位所在的传送带段不再被判定为堵塞。
     *
     * @param level    世界
     * @param pos      位置
     * @param original 原始实现
     * @return 是否堵塞
     */
    @WrapOperation(
        method = "handleBeltProcessingAndCheckIfRemoved",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/belt/behaviour/BeltProcessingBehaviour;"
                + "isBlocked(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z"
        )
    )
    private boolean aca$keepBeltRunning(BlockGetter level, BlockPos pos, Operation<Boolean> original) {
        if (level.getBlockEntity(pos) instanceof IBeltWorkstationHost host && host.aca$workstation() != null) {
            return false;
        }
        return original.call(level, pos);
    }
}
