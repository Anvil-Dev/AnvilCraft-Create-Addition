package dev.anvilcraft.create.data;

import dev.anvilcraft.create.AncCreateAddon;
import dev.anvilcraft.create.data.lang.LangHandler;
import dev.anvilcraft.create.data.provider.AcaBlockPlacementRuleProvider;
import dev.anvilcraft.create.data.tags.TagsHandler;
import dev.anvilcraft.create.init.AcaAmuletDefinitions;
import dev.anvilcraft.lib.v2.registrum.providers.DataProviderInitializer;
import dev.anvilcraft.lib.v2.registrum.providers.ProviderType;
import dev.dubhe.anvilcraft.init.registry.ModRegistryKeys;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import static dev.anvilcraft.create.AncCreateAddon.REGISTRUM;

@EventBusSubscriber(modid = AncCreateAddon.MOD_ID)
public class AncCreateAddonDatagen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
            event.includeServer(),
            new AcaBlockPlacementRuleProvider(event.getGenerator().getPackOutput())
        );
    }

    /**
     * 初始化生成器
     */
    public static void init() {
        DataProviderInitializer init = REGISTRUM.getDataGenInitializer();
        init.add(ModRegistryKeys.AMULET_DEF, AcaAmuletDefinitions::bootstrap);

        REGISTRUM.addDataGenerator(ProviderType.LANG, LangHandler::init);
        REGISTRUM.addDataGenerator(ProviderType.DAMAGE_TYPE_TAGS, TagsHandler::initDamageType);
    }
}
