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
            .add(AllDamageTypes.CRUSH)
            .add(AllDamageTypes.CUCKOO_SURPRISE)
            .add(AllDamageTypes.DRILL)
            .add(AllDamageTypes.FAN_FIRE)
            .add(AllDamageTypes.FAN_LAVA)
            .add(AllDamageTypes.POTATO_CANNON)
            .add(AllDamageTypes.ROLLER)
            .add(AllDamageTypes.RUN_OVER)
            .add(AllDamageTypes.SAW);
    }
}
