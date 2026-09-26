package dev.anvilcraft.create.config;

import dev.anvilcraft.create.AncCreateAddon;
import dev.anvilcraft.lib.v2.config.BoundedDiscrete;
import dev.anvilcraft.lib.v2.config.Comment;
import dev.anvilcraft.lib.v2.config.Config;
import net.neoforged.fml.config.ModConfig;

@Config(name = AncCreateAddon.MOD_ID, type = ModConfig.Type.SERVER)
public class AncCreateAddonServerConfig {
    @Comment("The ratio of the relative linear speed of the copper block and the magnet to the amount of charge generated")
    @BoundedDiscrete(max = 255.0, min = 0.01)
    public double chargeGeneratedEfficiency = 0.72;

    @Comment("The mysterious coefficient, perhaps related to the stress required by the generator?")
    @BoundedDiscrete(max = 255.0, min = 0.01)
    public float stressDissipationCoefficient = 1.76f;
}
