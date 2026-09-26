package dev.anvilcraft.create.client;

import dev.anvilcraft.create.AncCreateAddon;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = AncCreateAddon.MOD_ID, dist = Dist.CLIENT)
public class AncCreateAddonClient {
    public AncCreateAddonClient(IEventBus modBus, ModContainer container) {
    }
}
