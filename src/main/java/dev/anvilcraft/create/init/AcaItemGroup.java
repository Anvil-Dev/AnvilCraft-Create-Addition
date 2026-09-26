package dev.anvilcraft.create.init;

import dev.anvilcraft.create.AncCreateAddon;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.init.item.ModItemGroups;
import dev.dubhe.anvilcraft.init.item.tabs.DisplayItemsGenerator;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static dev.anvilcraft.create.AncCreateAddon.REGISTRUM;


public class AcaItemGroup extends DisplayItemsGenerator {
    private static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(
        Registries.CREATIVE_MODE_TAB,
        AncCreateAddon.MOD_ID
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> INSTANCE = REGISTER.register(
        "items",
        () -> CreativeModeTab.builder()
            .icon(ModBlocks.MAGNETO_ELECTRIC_CORE_BLOCK::asStack)
            .displayItems(new AcaItemGroup())
            .title(
                REGISTRUM.addLang(
                    "itemGroup",
                    AncCreateAddon.of("items"),
                    "AnvilCraft: Create Addition"
                )
            )
            .withTabsBefore(ModItemGroups.ANVILCRAFT_BUILDING_BLOCKS.getId(), ModItemGroups.ANVILCRAFT_ITEMS.getId())
            .build()
    );

    @Override
    public void accept() {
        this.plain(AcaItems.COGWHEEL_AMULET);
    }

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
