package dev.anvilcraft.create.integration;

import com.simibubi.create.content.equipment.goggles.GogglesItem;
import dev.anvilcraft.create.init.AcaAmuletEffectContextKeys;
import dev.dubhe.anvilcraft.api.amulet.AmuletManager;
import dev.dubhe.anvilcraft.api.amulet.ctx.AmuletEffectContext;
import net.minecraft.world.entity.player.Player;

/**
 * 齿轮护符「自带护目镜」效果与机械动力的对接：身上有护符给出该效果时，
 * 该玩家在机械动力眼里等同于戴着工程师护目镜。
 */
public final class GogglesAmuletIntegration {
    private GogglesAmuletIntegration() {
    }

    /**
     * 把护符的护目镜效果注册进机械动力的护目镜判定。
     */
    public static void register() {
        GogglesItem.addIsWearingPredicate(GogglesAmuletIntegration::isWearingGoggles);
    }

    /**
     * 查询玩家身上的护目镜效果。
     *
     * <p>机械动力只在客户端用护目镜判定（方块信息浮层、物品提示、旋转指示粒子），
     * 因此这里按当前侧求值，并只触发给出该效果的护符，不在客户端牵动其它护符效果。
     *
     * @param player 被判定是否戴着护目镜的玩家
     * @return 有护符给出护目镜效果时返回 {@code true}
     */
    private static boolean isWearingGoggles(Player player) {
        AmuletEffectContext ctx = new AmuletEffectContext();
        AmuletManager.get(player.registryAccess()).trigger(player, ctx);
        return ctx.getOrDefault(AcaAmuletEffectContextKeys.WEARING_GOGGLES, false);
    }
}
