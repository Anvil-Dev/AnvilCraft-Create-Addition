package dev.anvilcraft.addon.create;

import dev.anvilcraft.lib.config.BoundedDiscrete;
import dev.anvilcraft.lib.config.Comment;
import dev.anvilcraft.lib.config.Config;

@Config(name = AnvilCraftCreateAddition.MOD_ID)
public class AnvilCraftCreateAdditionConfig {
    @Comment("The ratio of the relative linear speed of the copper block and the magnet to the amount of charge generated")
    @BoundedDiscrete(max = 255.0, min = 0.01)
    public double chargeGeneratedEfficiency = 1.0;
}
