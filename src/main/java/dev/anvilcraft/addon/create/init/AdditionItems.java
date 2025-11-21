package dev.anvilcraft.addon.create.init;

import dev.dubhe.anvilcraft.AnvilCraft;

public class AdditionItems {
    static {
        AnvilCraft.REGISTRATE.defaultCreativeTab(AdditionItemGroups.ADDON_ITEMS.getKey());
    }

    public static void register() {
    }
}
