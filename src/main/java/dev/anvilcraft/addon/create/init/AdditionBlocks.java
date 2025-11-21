package dev.anvilcraft.addon.create.init;

import static dev.anvilcraft.addon.create.AnvilCraftCreateAddition.REGISTRATE;

public class AdditionBlocks {
    static {
        REGISTRATE.defaultCreativeTab(AdditionItemGroups.ADDON_ITEMS.getKey());
    }

    public static void register() {
    }
}
