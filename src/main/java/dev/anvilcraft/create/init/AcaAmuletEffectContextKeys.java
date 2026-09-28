package dev.anvilcraft.create.init;

import dev.anvilcraft.create.AncCreateAddon;
import dev.dubhe.anvilcraft.api.amulet.ctx.AmuletEffectContextKey;

public class AcaAmuletEffectContextKeys {
    public static final AmuletEffectContextKey<Double> SPEED = AmuletEffectContextKey.ofDouble(
        AncCreateAddon.of("speed")
    );

    public static final AmuletEffectContextKey<Boolean> WEARING_GOGGLES = AmuletEffectContextKey.ofBool(
        AncCreateAddon.of("wearing_goggles")
    );
}
