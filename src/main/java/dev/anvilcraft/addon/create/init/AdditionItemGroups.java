package dev.anvilcraft.addon.create.init;

import dev.anvilcraft.addon.create.AnvilCraftCreateAddition;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.init.item.ModItemGroups;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static dev.anvilcraft.addon.create.AnvilCraftCreateAddition.REGISTRATE;


public class AdditionItemGroups {
    private static final DeferredRegister<CreativeModeTab> DEFERRED_REGISTER = DeferredRegister.create(
        Registries.CREATIVE_MODE_TAB,
        AnvilCraftCreateAddition.MOD_ID
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ADDON_ITEMS = DEFERRED_REGISTER.register(
        "addition_items",
        () -> CreativeModeTab.builder()
            .icon(ModBlocks.MAGNETO_ELECTRIC_CORE_BLOCK::asStack)
            .displayItems((ctx, entries) -> {
            })
            .title(
                REGISTRATE.addLang(
                    "itemGroup",
                    AnvilCraftCreateAddition.of("addition_items"),
                    "AnvilCraft: Create Addition"
                )
            )
            .withTabsBefore(ModItemGroups.ANVILCRAFT_BUILD_BLOCK.getId())
            .build()
    );

    public static void register(IEventBus modEventBus) {
        DEFERRED_REGISTER.register(modEventBus);
    }
}
