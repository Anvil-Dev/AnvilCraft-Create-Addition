package dev.anvilcraft.create.data.provider;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.bearing.SailBlock;
import com.simibubi.create.content.equipment.armor.BacktankBlock;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlock;
import com.simibubi.create.content.trains.bogey.StandardBogeyBlock;
import dev.dubhe.anvilcraft.block.placement.BlockPlacementRuleSet;
import dev.dubhe.anvilcraft.block.placement.BlockPlacementRuleSet.StateRule;
import dev.dubhe.anvilcraft.block.placement.SimpleBlockPlacementRule;
import dev.dubhe.anvilcraft.init.registry.ModRegistryKeys;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;

/**
 * Generates built-in state-to-placement-item mappings for Create blocks.
 * Only blocks that neither a code-level Fallback nor {@link SimpleBlockPlacementRule}
 * can resolve at runtime get a rule file.
 */
public class AcaBlockPlacementRuleProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public AcaBlockPlacementRuleProvider(PackOutput output) {
        this.pathProvider = output.createRegistryElementsPathProvider(ModRegistryKeys.BLOCK_PLACEMENT_RULES);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        Map<Block, List<StateRule>> rulesByBlock = new LinkedHashMap<>();
        BuiltInRegistries.BLOCK.stream()
            .filter(block -> BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(Create.ID))
            .forEach(block -> addBlockRules(rulesByBlock, block));

        List<CompletableFuture<?>> saves = new ArrayList<>(rulesByBlock.size());
        rulesByBlock.keySet().stream()
            .sorted(Comparator.comparing(BuiltInRegistries.BLOCK::getKey))
            .forEach(block -> this.saveRuleSet(
                output,
                saves,
                BuiltInRegistries.BLOCK.getKey(block),
                new BlockPlacementRuleSet(rulesByBlock.get(block), Map.of())
            ));
        return CompletableFuture.allOf(saves.toArray(CompletableFuture[]::new));
    }

    /**
     * 台阶、楼梯与活板门由代码级 Fallback 接管；其余能被
     * {@link SimpleBlockPlacementRule} 动态解析的方块同样不需要数据规则。
     * 注意机械动力的染料帆、伪装板等方块没有任何可用的物品，
     * 无法被 Fallback 解析，必须生成规则。
     */
    private static boolean shouldSkipDataPack(Block block) {
        if (block instanceof SlabBlock || block instanceof StairBlock || block instanceof TrapDoorBlock) return true;
        return SimpleBlockPlacementRule.canTakeOver(block);
    }

    private static void addBlockRules(Map<Block, List<StateRule>> rulesByBlock, Block block) {
        if (shouldSkipDataPack(block)) return;
        switch (block) {
            case DoorBlock ignore -> { // 滑动门等两格高方块：仅下半部分可放置
                Item item = block.asItem();
                if (item != Items.AIR) {
                    addRule(rulesByBlock, block, "half=lower", item, 1);
                    addRule(rulesByBlock, block, "half=upper", item, -1);
                }
                return;
            }
            case BeltBlock ignore -> { // 传送带：中间段与末端由起始段延伸而成，需求同 BeltBlock#getRequiredItems
                String part = BeltBlock.PART.getName();
                addRule(rulesByBlock, block, part + "=" + BeltBlock.PART.getName(BeltPart.START), AllBlocks.SHAFT.asItem(), 1);
                addRule(rulesByBlock, block, part + "=" + BeltBlock.PART.getName(BeltPart.START), AllItems.BELT_CONNECTOR.asItem(), 1);
                addRule(rulesByBlock, block, part + "=" + BeltBlock.PART.getName(BeltPart.MIDDLE), Items.AIR, -1);
                addRule(rulesByBlock, block, part + "=" + BeltBlock.PART.getName(BeltPart.END), AllBlocks.SHAFT.asItem(), 1);
                addRule(rulesByBlock, block, part + "=" + BeltBlock.PART.getName(BeltPart.PULLEY), AllBlocks.SHAFT.asItem(), 1);
                return;
            }
            case SailBlock ignore -> { // 染料帆：放置消耗未染色的来源物品
                addRule(rulesByBlock, block, "", AllBlocks.SAIL.asItem(), 1);
                return;
            }
            case NixieTubeBlock ignore -> { // 染料霓虹灯：放置消耗未染色的来源物品
                addRule(rulesByBlock, block, "", AllBlocks.ORANGE_NIXIE_TUBE.asItem(), 1);
                return;
            }
            case BacktankBlock ignore -> { // 背罐：需求为护甲物品，而非作为 BlockItem 的放置物品
                Item item = block.asItem();
                if (item instanceof BacktankItem.BacktankBlockItem placeable) item = placeable.getActualItem();
                addRule(rulesByBlock, block, "", item, 1);
                return;
            }
            case BeltFunnelBlock ignore -> { // 传送带漏斗：需求为其所属的漏斗
                if (block == AllBlocks.ANDESITE_BELT_FUNNEL.get()) {
                    addRule(rulesByBlock, block, "", AllBlocks.ANDESITE_FUNNEL.asItem(), 1);
                } else if (block == AllBlocks.BRASS_BELT_FUNNEL.get()) {
                    addRule(rulesByBlock, block, "", AllBlocks.BRASS_FUNNEL.asItem(), 1);
                }
                return;
            }
            case LiquidBlock liquidBlock -> { // 流体方块：仅源头（level=0）可放置，放置后返还空桶
                Item bucket = liquidBlock.fluid.getBucket();
                if (bucket != Items.AIR) {
                    addRule(
                        rulesByBlock,
                        block,
                        LiquidBlock.LEVEL.getName() + "=" + LiquidBlock.LEVEL.getName(0),
                        bucket,
                        1,
                        Items.BUCKET
                    );
                }
                return;
            }
            case StandardBogeyBlock ignore -> { // 转向架：需求为列车机壳
                addRule(rulesByBlock, block, "", AllBlocks.RAILWAY_CASING.asItem(), 1);
                return;
            }
            default -> {
            }
        }

        // 无来源物品的结构方块：禁止放置
        if (
            block == AllBlocks.COPYCAT_BARS.get()
            || block == AllBlocks.COPYCAT_BASE.get()
            || block == AllBlocks.CRUSHING_WHEEL_CONTROLLER.get()
            || block == AllBlocks.FAKE_TRACK.get()
            || block == AllBlocks.MINECART_ANCHOR.get()
        ) {
            addRule(rulesByBlock, block, "", Items.AIR, -1);
            return;
        }

        if (block == AllBlocks.ENCASED_FLUID_PIPE.get() || block == AllBlocks.GLASS_FLUID_PIPE.get()) {
            addRule(rulesByBlock, block, "", AllBlocks.FLUID_PIPE.asItem(), 1);
            return;
        }
        if (block == AllBlocks.METAL_GIRDER_ENCASED_SHAFT.get()) {
            addRule(rulesByBlock, block, "", AllBlocks.SHAFT.asItem(), 1);
            addRule(rulesByBlock, block, "", AllBlocks.METAL_GIRDER.asItem(), 1);
            return;
        }
        if (block == AllBlocks.POWERED_SHAFT.get()) {
            addRule(rulesByBlock, block, "", AllBlocks.SHAFT.asItem(), 1);
            return;
        }
        if (block == AllBlocks.LIT_BLAZE_BURNER.get()) {
            addRule(rulesByBlock, block, "", AllItems.EMPTY_BLAZE_BURNER.asItem(), 1);
            return;
        }
        if (block == AllBlocks.MECHANICAL_PISTON_HEAD.get()) {
            addRule(rulesByBlock, block, "", AllBlocks.PISTON_EXTENSION_POLE.asItem(), 1);
            return;
        }
        if (block == AllBlocks.LECTERN_CONTROLLER.get()) {
            addRule(rulesByBlock, block, "", Items.LECTERN, 1);
            addRule(rulesByBlock, block, "", AllItems.LINKED_CONTROLLER.asItem(), 1);
            return;
        }
        if (block == AllBlocks.ROPE.get() || block == AllBlocks.PULLEY_MAGNET.get()) {
            addRule(rulesByBlock, block, "", AllBlocks.ROPE_PULLEY.asItem(), 1);
            return;
        }
        if (block == AllBlocks.STEAM_WHISTLE_EXTENSION.get()) {
            addRule(rulesByBlock, block, "", AllBlocks.STEAM_WHISTLE.asItem(), 1);
            return;
        }
        if (block == AllBlocks.WATER_WHEEL_STRUCTURAL.get()) {
            addRule(rulesByBlock, block, "", AllBlocks.LARGE_WATER_WHEEL.asItem(), 1);
        }
    }

    private static void addRule(
        Map<Block, List<StateRule>> rulesByBlock,
        Block block,
        String properties,
        Item item,
        int count
    ) {
        addRule(rulesByBlock, block, properties, item, count, null);
    }

    private static void addRule(
        Map<Block, List<StateRule>> rulesByBlock,
        Block block,
        String properties,
        Item item,
        int count,
        @Nullable Item returnItem
    ) {
        rulesByBlock.computeIfAbsent(block, ignored -> new ArrayList<>())
            .add(new StateRule(List.of(properties), item, count, returnItem));
    }

    private void saveRuleSet(
        CachedOutput output,
        List<CompletableFuture<?>> saves,
        ResourceLocation id,
        BlockPlacementRuleSet ruleSet
    ) {
        JsonElement json = BlockPlacementRuleSet.CODEC.encodeStart(JsonOps.INSTANCE, ruleSet).getOrThrow();
        saves.add(DataProvider.saveStable(output, json, this.pathProvider.json(id)));
    }

    @Override
    public String getName() {
        return "AnvilCraft Create Addition Block Placement Rules";
    }
}
