package dev.anvilcraft.create.mixin;

import dev.anvilcraft.create.integration.belt.BeltWorkstation;
import dev.anvilcraft.create.integration.belt.IProcessingTableExtension;
import dev.dubhe.anvilcraft.block.entity.ProcessingTableBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

/**
 * Mixin类用于修改 {@link ProcessingTableBlockEntity} 的行为。
 *
 * <p>
 * 普通加工台继续使用自己的库存，只有被挂到传送带工位上的加工台才把库存访问转交给工位，
 * 这样配方与渲染都能看到传送带里的物品。
 */
@Mixin(ProcessingTableBlockEntity.class)
abstract class ProcessingTableBlockEntityMixin implements IProcessingTableExtension {
    @Unique
    @Nullable
    private BeltWorkstation aca$workstation;

    @Override
    @Nullable
    public BeltWorkstation aca$workstation() {
        return this.aca$workstation;
    }

    @Override
    public void aca$workstation(@Nullable BeltWorkstation workstation) {
        this.aca$workstation = workstation;
    }

    /**
     * 挂载工位时改用工位的输入。
     *
     * @param cir 返回值
     */
    @Inject(method = "getInput", at = @At("HEAD"), cancellable = true)
    private void aca$input(CallbackInfoReturnable<IItemHandler> cir) {
        if (this.aca$workstation != null) {
            cir.setReturnValue(this.aca$workstation.getInput());
        }
    }

    /**
     * 挂载工位时改用工位的输出。
     *
     * @param cir 返回值
     */
    @Inject(method = "getOutput", at = @At("HEAD"), cancellable = true)
    private void aca$output(CallbackInfoReturnable<IItemHandler> cir) {
        if (this.aca$workstation != null) {
            cir.setReturnValue(this.aca$workstation.getOutput());
        }
    }

    /**
     * 挂载工位时改用工位的输入槽展示库存。
     *
     * @param cir 返回值
     */
    @Inject(method = "getItemHandler", at = @At("HEAD"), cancellable = true)
    private void aca$display(CallbackInfoReturnable<IItemHandler> cir) {
        if (this.aca$workstation != null) {
            cir.setReturnValue(this.aca$workstation.getInputHandler());
        }
    }
}
