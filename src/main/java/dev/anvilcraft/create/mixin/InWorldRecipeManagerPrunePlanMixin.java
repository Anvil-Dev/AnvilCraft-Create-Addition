package dev.anvilcraft.create.mixin;

import dev.anvilcraft.create.integration.belt.BeltWorkstationAccess;
import dev.anvilcraft.lib.v2.recipe.InWorldRecipe;
import dev.anvilcraft.lib.v2.recipe.util.InWorldRecipeContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Mixin类用于修改 {@code InWorldRecipeManager.PrunePlan} 的行为。
 *
 * <p>
 * 工位周围的配方可能挂在传送带格子上，剪枝阶段直接放行全部候选，避免把可执行的配方提前裁掉。
 */
@Mixin(targets = "dev.anvilcraft.lib.v2.recipe.util.InWorldRecipeManager$PrunePlan")
abstract class InWorldRecipeManagerPrunePlanMixin {
    /**
     * 工位周围不做剪枝。
     *
     * @param holders 候选配方
     * @param context 配方上下文
     * @param cir     返回值
     */
    @Inject(method = "resolve", at = @At("HEAD"), cancellable = true)
    private void aca$keepAll(
        Collection<RecipeHolder<InWorldRecipe>> holders,
        InWorldRecipeContext context,
        CallbackInfoReturnable<List<RecipeHolder<InWorldRecipe>>> cir
    ) {
        if (BeltWorkstationAccess.nearby(context)) {
            cir.setReturnValue(new ArrayList<>(holders));
        }
    }
}
