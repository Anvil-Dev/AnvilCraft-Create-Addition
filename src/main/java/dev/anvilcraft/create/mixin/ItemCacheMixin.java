package dev.anvilcraft.create.mixin;

import dev.anvilcraft.create.integration.belt.BeltWorkstationAccess;
import dev.anvilcraft.lib.v2.recipe.cache.ItemCache;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

/**
 * Mixin类用于修改 {@link ItemCache} 的行为。
 *
 * <p>
 * 配方扫描物品时，传送带格子上看到的应该是工位挂载的加工台，这样才能把工位里的物品算进配方输入。
 */
@Mixin(ItemCache.class)
abstract class ItemCacheMixin {
    /**
     * 方块实体查询改为优先返回工位里的加工台。
     *
     * @param level 世界
     * @param pos   位置
     * @return 方块实体
     */
    @Redirect(
        method = "grow",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)"
                + "Lnet/minecraft/world/level/block/entity/BlockEntity;"
        )
    )
    @Nullable
    private BlockEntity aca$attached(Level level, BlockPos pos) {
        return BeltWorkstationAccess.entity(level, pos);
    }
}
