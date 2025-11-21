package dev.anvilcraft.addon.create;

import dev.anvilcraft.lib.config.BoundedDiscrete;
import dev.anvilcraft.lib.config.Comment;
import dev.anvilcraft.lib.config.Config;

@Config(name = AnvilCraftCreateAddition.MOD_ID)
public class AnvilCraftCreateAdditionConfig {
    @Comment("Whether to log the dirt block on common setup")
    public boolean logDirtBlock = false;

    @Comment("A magic number")
    @BoundedDiscrete(max = 24, min = 2)
    public int magicNumber = 2;

    @Comment("What you want the introduction message to be for the magic number")
    public String magicNumberIntroduction = "";
}
