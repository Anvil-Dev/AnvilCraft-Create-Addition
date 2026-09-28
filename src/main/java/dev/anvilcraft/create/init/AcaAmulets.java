package dev.anvilcraft.create.init;

import dev.anvilcraft.create.AncCreateAddon;
import dev.anvilcraft.create.api.amulet.effect.IncreaseHandCrankSpeedAmuletEffect;
import dev.anvilcraft.create.api.amulet.effect.WearGogglesAmuletEffect;
import dev.anvilcraft.lib.v2.math.expression.IExpression;
import dev.anvilcraft.lib.v2.math.init.LibBuiltInFunctions;
import dev.dubhe.anvilcraft.api.amulet.Amulet;
import dev.dubhe.anvilcraft.api.amulet.effect.ImmuneTypedDamageAmuletEffect;
import dev.dubhe.anvilcraft.init.registry.ModRegistryKeys;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AcaAmulets {
    private static final DeferredRegister<Amulet> REGISTER = DeferredRegister.create(ModRegistryKeys.AMULET, AncCreateAddon.MOD_ID);

    public static final  DeferredHolder<Amulet, Amulet> COGWHEEL = REGISTER.register(
        "cogwheel",
        () -> Amulet.of(
            ImmuneTypedDamageAmuletEffect.of(AcaDamageTypeTags.COGWHEEL_AMULET_VALID),
            WearGogglesAmuletEffect.INSTANCE,
            new IncreaseHandCrankSpeedAmuletEffect(IExpression.of(
                LibBuiltInFunctions.MULTIPLY,
                IExpression.ref(IncreaseHandCrankSpeedAmuletEffect.VAR_ORIGINAL),
                IExpression.of(8)
            ))
        )
    );

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
