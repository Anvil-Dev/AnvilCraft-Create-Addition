package dev.anvilcraft.create.init;

import dev.anvilcraft.create.AncCreateAddon;
import dev.dubhe.anvilcraft.init.registry.ModRegistryKeys;
import dev.dubhe.anvilcraft.item.property.component.amulet.IAmulet;
import dev.dubhe.anvilcraft.item.property.component.amulet.ImmuneDamageAmulet;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AcaAmulets {
    private static final DeferredRegister<IAmulet> REGISTER = DeferredRegister.create(ModRegistryKeys.AMULET, AncCreateAddon.MOD_ID);

    public static final  DeferredHolder<IAmulet, ImmuneDamageAmulet> COGWHEEL = REGISTER.register(
        "cogwheel",
        () ->ImmuneDamageAmulet.builder()
            .immune(AcaDamageTypeTags.COGWHEEL_AMULET_VALID)
            .build()
    );

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
