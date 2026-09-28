package dev.anvilcraft.create.data.tags;

import com.simibubi.create.AllDamageTypes;
import dev.anvilcraft.create.init.AcaDamageTypeTags;
import dev.anvilcraft.lib.v2.registrum.providers.RegistrumTagsProvider;
import dev.dubhe.anvilcraft.init.entity.ModDamageTypeTags;
import net.minecraft.world.damagesource.DamageType;

public class TagsHandler {
    public static void initDamageType(RegistrumTagsProvider<DamageType> provider) {
        provider.addTag(ModDamageTypeTags.AMULET_VALID)
            .addTag(AcaDamageTypeTags.COGWHEEL_AMULET_VALID);

        provider.addTag(AcaDamageTypeTags.COGWHEEL_AMULET_VALID)
            .addOptional(AllDamageTypes.CRUSH.location())
            .addOptional(AllDamageTypes.CUCKOO_SURPRISE.location())
            .addOptional(AllDamageTypes.DRILL.location())
            .addOptional(AllDamageTypes.FAN_FIRE.location())
            .addOptional(AllDamageTypes.FAN_LAVA.location())
            .addOptional(AllDamageTypes.POTATO_CANNON.location())
            .addOptional(AllDamageTypes.ROLLER.location())
            .addOptional(AllDamageTypes.RUN_OVER.location())
            .addOptional(AllDamageTypes.SAW.location());
    }
}
