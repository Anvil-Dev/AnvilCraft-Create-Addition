package dev.anvilcraft.create.init;

import dev.anvilcraft.create.AncCreateAddon;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public class AcaDamageTypeTags {
    public static final TagKey<DamageType> COGWHEEL_AMULET_VALID = bind("amulet_valid/cogwheel");

    private static TagKey<DamageType> bind(String id) {
        return TagKey.create(Registries.DAMAGE_TYPE, AncCreateAddon.of(id));
    }
}
