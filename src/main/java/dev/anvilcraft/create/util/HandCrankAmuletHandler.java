package dev.anvilcraft.create.util;

import com.simibubi.create.content.kinetics.crank.HandCrankBlock;
import com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity;
import dev.anvilcraft.create.AncCreateAddon;
import dev.anvilcraft.create.api.injection.block.entity.IHandCrankBlockEntityExtension;
import dev.anvilcraft.create.init.AcaAmuletEffectContextKeys;
import dev.dubhe.anvilcraft.api.amulet.AmuletManager;
import dev.dubhe.anvilcraft.api.amulet.ctx.AmuletEffectContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * 处理齿轮护符的手摇曲柄加速效果：玩家摇动手摇曲柄类方块（手摇曲柄、阀门手轮等）时，
 * 按护符求值出的摇动速度调整该方块产出的转速。
 */
@EventBusSubscriber(modid = AncCreateAddon.MOD_ID)
public final class HandCrankAmuletHandler {
    private HandCrankAmuletHandler() {
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (player.isSpectator()) {
            return;
        }
        Level level = event.getLevel();
        BlockState state = level.getBlockState(event.getPos());
        if (!(state.getBlock() instanceof HandCrankBlock crank)) {
            return;
        }
        if (!(level.getBlockEntity(event.getPos()) instanceof HandCrankBlockEntity crankEntity)) {
            return;
        }
        double multiplier = HandCrankAmuletHandler.getSpeedMultiplier(player, crank.getRotationSpeed());
        ((IHandCrankBlockEntityExtension) crankEntity).aca$setSpeedMultiplier(multiplier);
    }

    /**
     * 求值玩家身上护符对摇动速度的影响。
     *
     * @param player        摇动曲柄的玩家
     * @param rotationSpeed 曲柄的原速
     * @return 摇动速度倍率，无护符生效时为 1
     */
    private static double getSpeedMultiplier(Player player, int rotationSpeed) {
        if (!AmuletManager.shouldEvaluate(player)) {
            return 1.0;
        }
        AmuletEffectContext ctx = new AmuletEffectContext();
        ctx.set(AcaAmuletEffectContextKeys.SPEED, (double) rotationSpeed);
        AmuletManager.get(player.registryAccess()).trigger(player, ctx);
        return ctx.getOrDefault(AcaAmuletEffectContextKeys.SPEED, (double) rotationSpeed) / rotationSpeed;
    }
}
