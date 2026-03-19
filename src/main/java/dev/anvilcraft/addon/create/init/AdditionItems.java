package dev.anvilcraft.addon.create.init;

import com.simibubi.create.AllItems;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.dubhe.anvilcraft.api.amulet.type.AmuletType;
import dev.dubhe.anvilcraft.init.item.ModItemGroups;
import dev.dubhe.anvilcraft.init.item.ModItems;
import dev.dubhe.anvilcraft.item.amulet.AmuletItem;
import dev.dubhe.anvilcraft.recipe.JewelCraftingRecipe;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import static dev.anvilcraft.addon.create.AnvilCraftCreateAddition.REGISTRATE;


public class AdditionItems {
    static {
        REGISTRATE.defaultCreativeTab(AdditionItemGroups.ADDON_ITEMS.getKey());
    }

    public static final ItemEntry<? extends AmuletItem> COGWHEEL_AMULET = REGISTRATE
        .item(
            "cogwheel_amulet",
            properties -> new AmuletItem(properties) {
                @Override
                public Holder<AmuletType> getType() {
                    return AdditionAmuletTypes.COGWHEEL;
                }
            }
        )
        .properties(properties -> properties.stacksTo(1))
        .removeTab(ModItemGroups.ANVILCRAFT_INGREDIENTS.getKey())
        .recipe((ctx, provider) -> JewelCraftingRecipe.builder()
            .withCondition(new ModLoadedCondition("create"))
            .requires(ModItems.SILVER_INGOT, 1)
            .requires(AllItems.PRECISION_MECHANISM, 16)
            .result(new ItemStack(ctx.get()))
            .save(provider)
        )
        .register();

    public static void register() {
    }
}
