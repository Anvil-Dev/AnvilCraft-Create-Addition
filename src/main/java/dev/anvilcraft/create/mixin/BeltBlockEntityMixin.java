package dev.anvilcraft.create.mixin;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import dev.anvilcraft.create.integration.belt.BeltWorkstation;
import dev.anvilcraft.create.integration.belt.BeltWorkstationIntegration;
import dev.anvilcraft.create.integration.belt.IBeltTransportExtension;
import dev.anvilcraft.create.integration.belt.IBeltWorkstationHost;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

/**
 * Mixin类用于修改 {@link BeltBlockEntity} 的行为。
 *
 * <p>
 * 让传送带本体持有工位：保存与加载工位数据、随传送带一起tick、被破坏时拆下工位，
 * 并提供工位读取传送带中心物品与写回产物的通道。
 */
@Mixin(BeltBlockEntity.class)
abstract class BeltBlockEntityMixin implements IBeltWorkstationHost {
    @Unique
    @Nullable
    private BeltWorkstation aca$workstation;

    @Unique
    @Nullable
    private CompoundTag aca$pendingLoad;

    @Unique
    @Nullable
    private BeltProcessingBehaviour aca$processor;

    @Unique
    private final IItemHandler aca$source = new IItemHandler() {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            TransportedItemStack item = BeltWorkstationIntegration.source(self());
            return item == null ? ItemStack.EMPTY : item.stack;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            TransportedItemStack item = BeltWorkstationIntegration.source(self());
            if (item == null) {
                return ItemStack.EMPTY;
            }
            int count = Math.min(amount, item.stack.getCount());
            ItemStack output = item.stack.copyWithCount(count);
            if (!simulate) {
                item.stack.shrink(count);
                sync();
            }
            return output;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }

        private BeltBlockEntity self() {
            return (BeltBlockEntity) (Object) BeltBlockEntityMixin.this;
        }

        private void sync() {
            BeltBlockEntityMixin.this.aca$sync();
        }
    };

    @Override
    @Nullable
    public BeltWorkstation aca$workstation() {
        BeltBlockEntity self = (BeltBlockEntity) (Object) this;
        if (this.aca$pendingLoad != null && self.getLevel() != null) {
            CompoundTag tag = this.aca$pendingLoad;
            this.aca$pendingLoad = null;
            BeltWorkstation restored = new BeltWorkstation(self);
            restored.loadAttached(tag, self.getLevel().registryAccess());
            this.aca$workstation = restored;
        }
        return this.aca$workstation;
    }

    @Override
    public void aca$setWorkstation(@Nullable BeltWorkstation workstation) {
        this.aca$workstation = workstation;
        this.aca$pendingLoad = null;
    }

    @Override
    public IItemHandler aca$source() {
        return this.aca$source;
    }

    @Override
    public void aca$sync() {
        BeltBlockEntity self = (BeltBlockEntity) (Object) this;
        if (self.getLevel() != null) {
            self.setChanged();
            self.notifyUpdate();
        }
    }

    @Override
    public boolean aca$emit(ItemStack stack) {
        return BeltWorkstationIntegration.emit((BeltBlockEntity) (Object) this, stack);
    }

    @Override
    public void aca$release() {
        TransportedItemStack item = BeltWorkstationIntegration.source((BeltBlockEntity) (Object) this);
        if (item != null) {
            item.locked = false;
        }
    }

    @Override
    public Object aca$processor() {
        if (this.aca$processor == null) {
            this.aca$processor = new BeltProcessingBehaviour((BeltBlockEntity) (Object) this)
                .whenItemEnters(this::aca$onItemEnters)
                .whileItemHeld(this::aca$onItemHeld);
        }
        return this.aca$processor;
    }

    private ProcessingResult aca$onItemEnters(TransportedItemStack stack, TransportedItemStackHandlerBehaviour handler) {
        if (this.aca$workstation() == null) {
            return ProcessingResult.PASS;
        }
        long position = ((BeltBlockEntity) (Object) this).getBlockPos().asLong();
        return ((IBeltTransportExtension) stack).aca$skip() == position ? ProcessingResult.PASS : ProcessingResult.HOLD;
    }

    private ProcessingResult aca$onItemHeld(TransportedItemStack stack, TransportedItemStackHandlerBehaviour handler) {
        return this.aca$workstation() == null ? ProcessingResult.PASS : ProcessingResult.HOLD;
    }

    /**
     * 工位存在时始终渲染传送带本体，外壳由渲染Mixin额外绘制。
     *
     * @param cir 返回值
     */
    @Inject(method = "shouldRenderNormally", at = @At("RETURN"), cancellable = true)
    private void aca$renderWorkstation(CallbackInfoReturnable<Boolean> cir) {
        if (this.aca$workstation() != null) {
            cir.setReturnValue(true);
        }
    }

    /**
     * 保存工位数据。
     *
     * @param tag        方块实体数据
     * @param registries 注册表访问器
     * @param client     是否为客户端
     * @param cir        回调信息
     */
    @Inject(method = "write", at = @At("TAIL"))
    private void aca$saveWorkstation(
        CompoundTag tag,
        HolderLookup.Provider registries,
        boolean client,
        CallbackInfo cir
    ) {
        BeltWorkstation workstation = this.aca$workstation();
        if (workstation != null) {
            tag.put("AnvilCraftBeltWorkstation", workstation.saveAttached(registries));
        } else if (this.aca$pendingLoad != null) {
            tag.put("AnvilCraftBeltWorkstation", this.aca$pendingLoad);
        }
    }

    /**
     * 加载工位数据。
     *
     * @param tag        方块实体数据
     * @param registries 注册表访问器
     * @param client     是否为客户端
     * @param cir        回调信息
     */
    @Inject(method = "read", at = @At("RETURN"))
    private void aca$loadWorkstation(
        CompoundTag tag,
        HolderLookup.Provider registries,
        boolean client,
        CallbackInfo cir
    ) {
        this.aca$workstation = null;
        this.aca$pendingLoad = tag.contains("AnvilCraftBeltWorkstation")
            ? tag.getCompound("AnvilCraftBeltWorkstation").copy()
            : null;
        this.aca$workstation();
    }

    /**
     * 服务端推进挂载工位。
     *
     * @param cir 回调信息
     */
    @Inject(method = "tick", at = @At("RETURN"))
    private void aca$tickWorkstation(CallbackInfo cir) {
        BeltBlockEntity self = (BeltBlockEntity) (Object) this;
        Level level = self.getLevel();
        BeltWorkstation workstation = this.aca$workstation();
        if (workstation == null || level == null || level.isClientSide || self.isRemoved()) {
            return;
        }
        if (!AllBlocks.BELT.has(level.getBlockState(self.getBlockPos()))) {
            return;
        }
        workstation.tickAttached();
    }

    /**
     * 传送带被破坏时把工位拆下来。
     *
     * @param cir 回调信息
     */
    @Inject(method = "destroy", at = @At("HEAD"))
    private void aca$destroyWorkstation(CallbackInfo cir) {
        BeltBlockEntity self = (BeltBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level != null && !level.isClientSide) {
            BeltWorkstationIntegration.detach(self, true);
        }
    }
}
