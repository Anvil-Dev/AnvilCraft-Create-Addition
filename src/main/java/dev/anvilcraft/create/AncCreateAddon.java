package dev.anvilcraft.create;

import com.mojang.logging.LogUtils;
import com.simibubi.create.api.boiler.BoilerHeater;
import com.simibubi.create.api.packager.unpacking.UnpackingHandler;
import dev.anvilcraft.create.config.AncCreateAddonServerConfig;
import dev.anvilcraft.create.data.AncCreateAddonDatagen;
import dev.anvilcraft.create.init.AcaAmulets;
import dev.anvilcraft.create.init.AcaBlocks;
import dev.anvilcraft.create.init.AcaItemGroup;
import dev.anvilcraft.create.init.AcaItems;
import dev.anvilcraft.create.integration.BatchCrafterUnpackingHandler;
import dev.anvilcraft.create.integration.CreateBoilerHeaterProvider;
import dev.anvilcraft.create.integration.GogglesAmuletIntegration;
import dev.anvilcraft.lib.v2.config.ConfigManager;
import dev.anvilcraft.lib.v2.registrum.Registrum;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(AncCreateAddon.MOD_ID)
public class AncCreateAddon {
    public static final String MOD_ID = "anvilcraft_create_addition";
    public static final String MOD_NAME = "AnvilCraft: Create Addition";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final AncCreateAddonServerConfig CONFIG = ConfigManager.register(
        AncCreateAddon.MOD_ID,
        AncCreateAddonServerConfig::new
    );
    public static final Registrum REGISTRUM = Registrum.create(MOD_ID).defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

    public AncCreateAddon(IEventBus modEventBus, ModContainer ignore) {
        AcaItemGroup.register(modEventBus);
        AcaBlocks.register();
        AcaItems.register();
        AncCreateAddonDatagen.init();
        AcaAmulets.register(modEventBus);
        BoilerHeater.REGISTRY.registerProvider(new CreateBoilerHeaterProvider());
        GogglesAmuletIntegration.register();
        // noinspection UnstableApiUsage
        UnpackingHandler.REGISTRY.registerProvider(BatchCrafterUnpackingHandler.INSTANCE);
    }

    public static ResourceLocation of(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
