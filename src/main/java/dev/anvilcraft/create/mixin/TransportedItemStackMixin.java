package dev.anvilcraft.create.mixin;

import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import dev.anvilcraft.create.integration.belt.IBeltTransportExtension;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin类用于修改 {@link TransportedItemStack} 的行为。
 *
 * <p>
 * 给传送带上的物品记录“产出它的工位”，避免工位把自己写回的产物再次加工，同时让该标记跟随NBT保存。
 */
@Mixin(TransportedItemStack.class)
abstract class TransportedItemStackMixin implements IBeltTransportExtension {
    @Unique
    private long aca$skip = Long.MIN_VALUE;

    @Override
    public long aca$skip() {
        return this.aca$skip;
    }

    @Override
    public void aca$skip(long position) {
        this.aca$skip = position;
    }

    /**
     * 保存工位标记。
     *
     * @param provider 注册表访问器
     * @param cir      返回值
     */
    @Inject(method = "serializeNBT", at = @At("RETURN"))
    private void aca$save(HolderLookup.Provider provider, CallbackInfoReturnable<CompoundTag> cir) {
        if (this.aca$skip != Long.MIN_VALUE) {
            cir.getReturnValue().putLong("AnvilCraftBeltOutput", this.aca$skip);
        }
    }

    /**
     * 读取工位标记。
     *
     * @param tag      物品数据
     * @param provider 注册表访问器
     * @param cir      返回值
     */
    @Inject(method = "read", at = @At("RETURN"))
    private static void aca$load(
        CompoundTag tag,
        HolderLookup.Provider provider,
        CallbackInfoReturnable<TransportedItemStack> cir
    ) {
        if (tag.contains("AnvilCraftBeltOutput")) {
            ((IBeltTransportExtension) cir.getReturnValue()).aca$skip(tag.getLong("AnvilCraftBeltOutput"));
        }
    }

    /**
     * 复制工位标记。
     *
     * @param cir 返回值
     */
    @Inject(method = "getSimilar", at = @At("RETURN"))
    private void aca$copy(CallbackInfoReturnable<TransportedItemStack> cir) {
        ((IBeltTransportExtension) cir.getReturnValue()).aca$skip(this.aca$skip);
    }
}
