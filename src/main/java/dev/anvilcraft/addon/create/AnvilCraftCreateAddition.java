package dev.anvilcraft.addon.create;

import com.mojang.logging.LogUtils;
import com.tterrag.registrate.Registrate;
import dev.anvilcraft.addon.create.data.AdditionDatagen;
import dev.anvilcraft.addon.create.init.AdditionBlocks;
import dev.anvilcraft.addon.create.init.AdditionItemGroups;
import dev.anvilcraft.addon.create.init.AdditionItems;
import dev.anvilcraft.lib.config.ConfigManager;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(AnvilCraftCreateAddition.MOD_ID)
public class AnvilCraftCreateAddition {
    public static final String MOD_ID = "anvilcraft_create_addition";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final AnvilCraftCreateAdditionConfig CONFIG = ConfigManager.register(AnvilCraftCreateAddition.MOD_ID, AnvilCraftCreateAdditionConfig::new);
    public static final Registrate REGISTRATE = Registrate.create(MOD_ID);

    public AnvilCraftCreateAddition(@NotNull IEventBus modEventBus, @NotNull ModContainer modContainer) {
        AdditionItemGroups.register(modEventBus);
        AdditionBlocks.register();
        AdditionItems.register();
        AdditionDatagen.init();
    }

    public static @NotNull ResourceLocation of(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
