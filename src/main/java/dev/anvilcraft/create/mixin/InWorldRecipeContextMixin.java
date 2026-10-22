package dev.anvilcraft.create.mixin;

import dev.anvilcraft.create.integration.belt.BeltWorkstationBlockCache;
import dev.anvilcraft.lib.v2.recipe.cache.BlockCache;
import dev.anvilcraft.lib.v2.recipe.util.InWorldRecipeContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin类用于修改 {@link InWorldRecipeContext} 的行为。
 *
 * <p>
 * 每个配方上下文都换上一个认得工位的方块缓存，这样配方规划阶段读到的就是工位外壳与挂载的加工台。
 */
@Mixin(InWorldRecipeContext.class)
abstract class InWorldRecipeContextMixin {
    /**
     * 构造时替换默认的方块缓存。
     *
     * @param level  服务端世界
     * @param pos    配方位置
     * @param entity 触发实体
     * @param ci     回调信息
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void aca$cache(ServerLevel level, Vec3 pos, Entity entity, CallbackInfo ci) {
        ((InWorldRecipeContext) (Object) this).put(
            BlockCache.BLOCK_CACHE,
            new BeltWorkstationBlockCache(level)
        );
    }
}
