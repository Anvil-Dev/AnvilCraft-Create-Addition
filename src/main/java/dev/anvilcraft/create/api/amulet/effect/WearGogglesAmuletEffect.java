package dev.anvilcraft.create.api.amulet.effect;

import dev.anvilcraft.create.init.AcaAmuletEffectContextKeys;
import dev.dubhe.anvilcraft.api.amulet.ctx.AmuletEffectContext;
import dev.dubhe.anvilcraft.api.amulet.effect.IAmuletEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record WearGogglesAmuletEffect() implements IAmuletEffect {
    public static final WearGogglesAmuletEffect INSTANCE = new WearGogglesAmuletEffect();

    @Override
    public void trigger(LivingEntity entity, ItemStack amulet, AmuletEffectContext ctx) {
        ctx.set(AcaAmuletEffectContextKeys.WEARING_GOGGLES, true);
    }
}
