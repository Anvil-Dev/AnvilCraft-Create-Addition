package dev.anvilcraft.addon.create.util;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import dev.anvilcraft.addon.create.AnvilCraftCreateAddition;
import dev.dubhe.anvilcraft.api.chargecollector.ChargeCollectorManager;
import dev.dubhe.anvilcraft.block.entity.ChargeCollectorBlockEntity;
import dev.dubhe.anvilcraft.init.block.ModBlockTags;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@EventBusSubscriber(modid = AnvilCraftCreateAddition.MOD_ID)
public class ChargeMovementBehaviour implements MovementBehaviour {
    @Override
    public void tick(@NotNull MovementContext context) {
        Level level = context.world;
        double speed = context.motion.length();
        BlockPos blockPos = BlockPos.containing(context.position);
        ChargeCollectorManager instance = ChargeCollectorManager.getInstance(level);
        if (context.state.is(ModBlocks.MAGNET_BLOCK.get())) {
            magnetTick(instance, level, blockPos, speed);
        } else {
            metalTick(instance, level, blockPos, speed);
        }
    }

    public void magnetTick(@NotNull ChargeCollectorManager manager, @NotNull Level level, BlockPos blockPos, double speed) {
        for (Direction direction : Direction.values()) {
            BlockPos offsetPos = blockPos.relative(direction);
            BlockState offsetState = level.getBlockState(offsetPos);
            Collection<ChargeCollectorManager.Entry> chargeCollectorCollection = manager.getNearestChargeCollect(offsetPos);
            if (!ChargeMovementBehaviour.isMetal(offsetState)) {
                continue;
            }
            double surplus = 1 * speed;
            for (ChargeCollectorManager.Entry entry : chargeCollectorCollection) {
                ChargeCollectorBlockEntity chargeCollectorBlockEntity = entry.getBlockEntity();
                if (!ChargeCollectorManager.getInstance(level).canCollect(chargeCollectorBlockEntity, offsetPos)) return;
                surplus = chargeCollectorBlockEntity.incomingCharge(surplus, offsetPos);
                if (surplus == 0) break;
            }
        }
    }

    public void metalTick(@NotNull ChargeCollectorManager manager, @NotNull Level level, BlockPos blockPos, double speed) {
        Collection<ChargeCollectorManager.Entry> chargeCollectorCollection = manager.getNearestChargeCollect(blockPos);
        for (Direction direction : Direction.values()) {
            BlockPos offsetPos = blockPos.relative(direction);
            BlockState offsetState = level.getBlockState(offsetPos);
            if (!offsetState.is(ModBlockTags.MAGNET)) {
                continue;
            }
            double surplus = 1 * speed;
            for (ChargeCollectorManager.Entry entry : chargeCollectorCollection) {
                ChargeCollectorBlockEntity chargeCollectorBlockEntity = entry.getBlockEntity();
                if (!ChargeCollectorManager.getInstance(level).canCollect(chargeCollectorBlockEntity, blockPos)) return;
                surplus = chargeCollectorBlockEntity.incomingCharge(surplus, blockPos);
                if (surplus == 0) return;
            }
        }
    }

    @SubscribeEvent
    public static void register(FMLLoadCompleteEvent event) {
        for (Block block : BuiltInRegistries.BLOCK) {
            BlockState defaultState = block.defaultBlockState();
            if (defaultState.is(ModBlockTags.MAGNET)) {
                MovementBehaviour.movementBehaviour(new ChargeMovementBehaviour()).accept(block);
            } else if (ChargeMovementBehaviour.isMetal(defaultState)) {
                MovementBehaviour.movementBehaviour(new ChargeMovementBehaviour()).accept(block);
            }
        }
    }

    public static boolean isMetal(@NotNull BlockState state) {
        return state.is(Tags.Blocks.STORAGE_BLOCKS_COPPER) // 铜
               || state.is(Tags.Blocks.STORAGE_BLOCKS_IRON) // 铁
               || state.is(Tags.Blocks.STORAGE_BLOCKS_GOLD) // 金
               || state.is(Tags.Blocks.STORAGE_BLOCKS_NETHERITE) // 下界合金
               || state.is(Tags.Blocks.STORAGE_BLOCKS_RAW_COPPER) // 粗铜
               || state.is(Tags.Blocks.STORAGE_BLOCKS_RAW_IRON) // 粗铁
               || state.is(Tags.Blocks.STORAGE_BLOCKS_RAW_GOLD) // 粗金
               || state.is(ModBlockTags.STORAGE_BLOCKS_ZINC) // 锌
               || state.is(ModBlockTags.STORAGE_BLOCKS_TIN) //  锡
               || state.is(ModBlockTags.STORAGE_BLOCKS_LEAD) // 铅
               || state.is(ModBlockTags.STORAGE_BLOCKS_SILVER) // 银
               || state.is(ModBlockTags.STORAGE_BLOCKS_URANIUM) // 铀
               || state.is(ModBlockTags.STORAGE_BLOCKS_PLUTONIUM) // 钚
               || state.is(ModBlockTags.STORAGE_BLOCKS_BRONZE) // 青铜
               || state.is(ModBlockTags.STORAGE_BLOCKS_BRASS) //  黄铜
               || state.is(ModBlockTags.STORAGE_BLOCKS_URANIUM) // 铀
               || state.is(ModBlockTags.STORAGE_BLOCKS_TUNGSTEN) // 钨
               || state.is(ModBlockTags.STORAGE_BLOCKS_TITANIUM) // 钛
               || state.is(ModBlockTags.STORAGE_BLOCKS_RAW_URANIUM) // 粗铀
               || state.is(ModBlockTags.STORAGE_BLOCKS_RAW_TUNGSTEN) // 粗钨
               || state.is(ModBlockTags.STORAGE_BLOCKS_RAW_TITANIUM) // 粗钛
               || state.is(ModBlockTags.STORAGE_BLOCKS_RAW_ZINC) // 粗锌
               || state.is(ModBlockTags.STORAGE_BLOCKS_RAW_TIN) // 粗锡
               || state.is(ModBlockTags.STORAGE_BLOCKS_RAW_LEAD) // 粗铅
               || state.is(ModBlockTags.STORAGE_BLOCKS_RAW_SILVER) // 粗银
               || state.is(ModBlocks.ROYAL_STEEL_BLOCK) // 皇家钢
               || state.is(ModBlocks.CUT_ROYAL_STEEL_BLOCK) // 切制皇家钢
               || state.is(ModBlocks.SMOOTH_ROYAL_STEEL_BLOCK) // 平滑皇家钢
               || state.is(ModBlocks.EMBER_METAL_BLOCK) // 余烬金属
               || state.is(ModBlocks.CUT_EMBER_METAL_BLOCK) // 切制余烬金属
               || state.is(ModBlocks.FROST_METAL_BLOCK); // 浮霜金属
    }
}
