package dev.anvilcraft.addon.create.client;

import dev.anvilcraft.addon.create.AnvilCraftCreateAddition;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod(value = AnvilCraftCreateAddition.MOD_ID, dist = Dist.CLIENT)
public class AnvilCraftCreateAdditionClient {
    public AnvilCraftCreateAdditionClient(@NotNull IEventBus modBus, @NotNull ModContainer container) {
    }
}
