package dev.anvilcraft.create.init;

import dev.anvilcraft.create.AncCreateAddon;
import dev.dubhe.anvilcraft.api.amulet.def.AmuletDefinition;
import dev.dubhe.anvilcraft.api.amulet.def.IAmuletDefinition;
import dev.dubhe.anvilcraft.init.registry.ModRegistryKeys;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public class AcaAmuletDefinitions {
    public static final ResourceKey<IAmuletDefinition> COGWHEEL = AcaAmuletDefinitions.key("cogwheel");

    public static void bootstrap(BootstrapContext<IAmuletDefinition> ctx) {
        ctx.register(
            AcaAmuletDefinitions.COGWHEEL,
            AmuletDefinition.builder(AcaItems.COGWHEEL_AMULET)
                .obtain(AcaDamageTypeTags.COGWHEEL_AMULET_VALID)
                .build()
        );
    }

    private static ResourceKey<IAmuletDefinition> key(String name) {
        return ResourceKey.create(ModRegistryKeys.AMULET_DEF, AncCreateAddon.of(name));
    }
}
