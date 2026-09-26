package dev.anvilcraft.create.init;

import com.simibubi.create.AllItems;
import dev.anvilcraft.lib.v2.registrum.util.entry.ItemEntry;
import dev.dubhe.anvilcraft.init.item.ModComponents;
import dev.dubhe.anvilcraft.init.item.ModItems;
import dev.dubhe.anvilcraft.recipe.JewelCraftingRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static dev.anvilcraft.create.AncCreateAddon.REGISTRUM;


public class AcaItems {
    public static final ItemEntry<Item> COGWHEEL_AMULET = REGISTRUM
        .item("cogwheel_amulet", Item::new)
        .properties(properties -> properties
            .stacksTo(1)
            .component(ModComponents.AMULET, AcaAmulets.COGWHEEL.getKey())
        )
        .recipe((ctx, provider) -> JewelCraftingRecipe.builder()
            .requires(ModItems.SILVER_INGOT, 1)
            .requires(AllItems.PRECISION_MECHANISM, 16)
            .result(new ItemStack(ctx.get()))
            .save(provider)
        )
        .register();

    public static void register() {
    }
}
