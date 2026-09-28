package dev.anvilcraft.create.api.amulet.effect;

import dev.anvilcraft.create.init.AcaAmuletEffectContextKeys;
import dev.anvilcraft.lib.v2.math.expression.Arguments;
import dev.anvilcraft.lib.v2.math.expression.IExpression;
import dev.dubhe.anvilcraft.api.amulet.ctx.AmuletEffectContext;
import dev.dubhe.anvilcraft.api.amulet.effect.IAmuletEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 提高手摇曲柄摇动速度的护符效果，求值 {@link #speed} 时以原速作为传入值，
 * 因此表达式里写 {@code $(original)}，例如 {@code $(original)*8} 表示摇动速度为原速的八倍。
 */
public record IncreaseHandCrankSpeedAmuletEffect(IExpression speed) implements IAmuletEffect {
    /**
     * 求值 {@link #speed} 时按名字绑上的传入值：摇动速度原速，也就是表达式里的 {@code $(original)}。
     */
    public static final String VAR_ORIGINAL = "original";

    @Override
    public void trigger(LivingEntity entity, ItemStack amulet, AmuletEffectContext ctx) {
        double original = ctx.getOrDefault(AcaAmuletEffectContextKeys.SPEED, 0.0);
        Arguments inputs = Arguments.of(original).withAll(
            List.of(IncreaseHandCrankSpeedAmuletEffect.VAR_ORIGINAL),
            List.of(new Arguments.Value.Single(original))
        );
        ctx.set(AcaAmuletEffectContextKeys.SPEED, this.speed.evaluate(inputs));
    }
}
