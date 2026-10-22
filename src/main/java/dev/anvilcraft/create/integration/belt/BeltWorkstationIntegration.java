package dev.anvilcraft.create.integration.belt;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import dev.dubhe.anvilcraft.block.ProcessingTableBlock;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

/**
 * 传送带工位的安装、交互与运输逻辑。
 *
 * <p>
 * 工位以传送带附件的形式存在：右键传送带装上鱼缸或加工台，扳手Shift右键拆下，
 * 装好后铁砧的配方会直接在传送带上运行，产物重新回到传送带继续运输。
 */
public final class BeltWorkstationIntegration {
    private BeltWorkstationIntegration() {
    }

    /**
     * 注册工位相关的事件监听。
     *
     * @param modEventBus 模组事件总线
     */
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(BeltWorkstationIntegration::capabilities);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, BeltWorkstationIntegration::interact);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, BeltWorkstationAccess::spawned);
    }

    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK,
            AllBlockEntityTypes.BELT.get(),
            BeltWorkstationIntegration::fluidHandler
        );
    }

    @Nullable
    private static IFluidHandler fluidHandler(BeltBlockEntity belt, Direction side) {
        BeltWorkstation workstation = ((IBeltWorkstationHost) belt).aca$workstation();
        if (workstation == null || !workstation.isFishTank()) {
            return null;
        }
        return workstation.getFluidHandler();
    }

    private static void interact(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        if (!(level.getBlockEntity(pos) instanceof BeltBlockEntity belt) || !(belt instanceof IBeltWorkstationHost host)) {
            return;
        }
        ItemStack held = event.getItemStack();
        BeltWorkstation workstation = host.aca$workstation();
        Block block = Block.byItem(held.getItem());
        if (held.is(ModBlocks.FISH_TANK.asItem()) || block instanceof ProcessingTableBlock) {
            install(event, level, pos, held, block, belt, host, workstation);
            return;
        }
        if (workstation == null) {
            return;
        }
        if (event.getEntity().isShiftKeyDown() && AllItems.WRENCH.isIn(held)) {
            if (!level.isClientSide) {
                detach(belt, true);
            }
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }
        if (AllItems.WRENCH.isIn(held)) {
            return;
        }
        if (held.is(AllBlocks.ANDESITE_CASING.asItem()) || held.is(AllBlocks.BRASS_CASING.asItem())) {
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
            return;
        }
        if (toggleWaterlogged(event, level, pos, held, host, workstation)) {
            return;
        }
        if (workstation.isFishTank() && FluidUtil.getFluidHandler(held).isPresent()) {
            if (!level.isClientSide) {
                FluidUtil.interactWithFluidHandler(event.getEntity(), event.getHand(), workstation.getFluidHandler());
            }
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }
        if (workstation.tryInteractWithTank(event.getEntity(), event.getHand(), event.getHitVec())) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    private static void install(
        PlayerInteractEvent.RightClickBlock event,
        Level level,
        BlockPos pos,
        ItemStack held,
        Block block,
        BeltBlockEntity belt,
        IBeltWorkstationHost host,
        @Nullable BeltWorkstation existing
    ) {
        if (existing != null) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }
        if (belt.getBlockState().getValue(BeltBlock.SLOPE) != BeltSlope.HORIZONTAL
            || belt.casing != BeltBlockEntity.CasingType.NONE) {
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
            return;
        }
        if (!level.isClientSide) {
            BeltWorkstation workstation = new BeltWorkstation(belt);
            if (block instanceof ProcessingTableBlock) {
                workstation.setShellState(applyProperties(block, held));
            }
            host.aca$setWorkstation(workstation);
            CustomData data = held.get(DataComponents.BLOCK_ENTITY_DATA);
            if (data != null) {
                workstation.loadAttached(data.copyTag(), level.registryAccess());
            }
            Player player = event.getEntity();
            if (!player.isCreative()) {
                held.shrink(1);
            }
            host.aca$sync();
            level.invalidateCapabilities(pos);
            BlockState shell = workstation.shellState();
            level.playSound(
                null,
                pos,
                shell.getSoundType(level, pos, player).getPlaceSound(),
                SoundSource.BLOCKS,
                1,
                1
            );
        }
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    private static BlockState applyProperties(Block block, ItemStack held) {
        BlockState state = block.defaultBlockState();
        if (held.get(DataComponents.BLOCK_STATE) != null) {
            state = held.get(DataComponents.BLOCK_STATE).apply(state);
        }
        return state;
    }

    private static boolean toggleWaterlogged(
        PlayerInteractEvent.RightClickBlock event,
        Level level,
        BlockPos pos,
        ItemStack held,
        IBeltWorkstationHost host,
        BeltWorkstation workstation
    ) {
        if (workstation.isFishTank() || !(held.is(Items.WATER_BUCKET) || held.is(Items.BUCKET))) {
            return false;
        }
        BlockState shell = workstation.shellState();
        boolean wet = shell.getValue(ProcessingTableBlock.WATERLOGGED);
        if (held.is(Items.WATER_BUCKET) == wet) {
            return false;
        }
        if (!level.isClientSide) {
            workstation.setShellState(shell.setValue(ProcessingTableBlock.WATERLOGGED, !wet));
            Player player = event.getEntity();
            if (!player.isCreative()) {
                ItemStack emptied = new ItemStack(wet ? Items.WATER_BUCKET : Items.BUCKET);
                player.setItemInHand(event.getHand(), ItemUtils.createFilledResult(held, player, emptied));
            }
            host.aca$sync();
        }
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
        return true;
    }

    /**
     * 拆下传送带上的工位。
     *
     * @param owner 传送带方块实体
     * @param drop  是否把工位内容与外壳掉落出来
     */
    public static void detach(BlockEntity owner, boolean drop) {
        IBeltWorkstationHost host = (IBeltWorkstationHost) owner;
        BeltWorkstation workstation = host.aca$workstation();
        if (workstation == null) {
            return;
        }
        Level level = owner.getLevel();
        if (level == null) {
            return;
        }
        final BlockPos pos = owner.getBlockPos();
        final List<ItemStack> stored = workstation.takeStoredItems();
        CompoundTag data = workstation.saveAttached(level.registryAccess());
        data.remove("Inputs");
        data.remove("Outputs");
        data.remove("BeltPending");
        data.remove("BeltShellState");
        data.remove("BeltTableData");
        data.putString("id", "anvilcraft:fish_tank");
        ItemStack shell = new ItemStack(workstation.shellState().getBlock().asItem());
        if (workstation.isFishTank()) {
            shell.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(data));
        }
        // 装上的加工台按实际变体返还；水是方块状态，和原版加工台一致不单独返还。
        AuxiliaryLightManager light = level.getAuxLightManager(pos);
        if (light != null) {
            light.setLightAt(pos, 0);
        }
        workstation.removeAttached();
        host.aca$release();
        host.aca$setWorkstation(null);
        host.aca$sync();
        level.invalidateCapabilities(pos);
        if (!drop || level.isClientSide) {
            return;
        }
        stored.add(shell);
        for (ItemStack stack : stored) {
            if (stack.isEmpty()) {
                continue;
            }
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1;
            double z = pos.getZ() + 0.5;
            level.addFreshEntity(new ItemEntity(level, x, y, z, stack));
        }
    }

    /**
     * 查找传送带中心被工位锁定的物品。
     *
     * @param belt 传送带方块实体
     * @return 被锁定的物品，没有时返回 {@code null}
     */
    @Nullable
    public static TransportedItemStack source(BeltBlockEntity belt) {
        BeltBlockEntity controller = belt.getControllerBE();
        if (controller == null) {
            return null;
        }
        long skip = belt.getBlockPos().asLong();
        for (TransportedItemStack item : controller.getInventory().getTransportedItems()) {
            if (!item.locked || item.stack.isEmpty()) {
                continue;
            }
            if (Math.abs(item.beltPosition - belt.index - 0.5f) >= 0.1f) {
                continue;
            }
            if (((IBeltTransportExtension) item).aca$skip() == skip) {
                continue;
            }
            return item;
        }
        return null;
    }

    /**
     * 把产物写回传送带中心之后的格子。
     *
     * @param belt  传送带方块实体
     * @param stack 产物
     * @return 写入成功时返回 {@code true}
     */
    public static boolean emit(BeltBlockEntity belt, ItemStack stack) {
        BeltBlockEntity controller = belt.getControllerBE();
        if (controller == null || controller.getSpeed() == 0) {
            return false;
        }
        BeltInventory inventory = controller.getInventory();
        boolean forward = controller.getDirectionAwareBeltMovementSpeed() > 0;
        float position = belt.index + 0.5f + (forward ? 0.53f : -0.53f);
        // 立刻把产物放到本格加工中心之后，同时避开下游已经排队的物品。
        if (position <= 0 || position >= controller.beltLength) {
            position = belt.index + 0.5f + (forward ? 0.02f : -0.02f);
        }
        for (TransportedItemStack other : inventory.getTransportedItems()) {
            if (!other.stack.isEmpty() && Math.abs(other.beltPosition - position) < 0.45f) {
                return false;
            }
        }
        TransportedItemStack output = new TransportedItemStack(stack);
        output.beltPosition = position;
        output.prevBeltPosition = position;
        output.insertedAt = belt.index;
        output.insertedFrom = Direction.UP;
        output.sideOffset = 0;
        output.prevSideOffset = 0;
        ((IBeltTransportExtension) output).aca$skip(belt.getBlockPos().asLong());
        inventory.addItem(output);
        controller.notifyUpdate();
        return true;
    }
}
