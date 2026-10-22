package dev.anvilcraft.create.integration.belt;

import dev.dubhe.anvilcraft.block.ProcessingTableBlock;
import dev.dubhe.anvilcraft.block.entity.FishTankBlockEntity;
import dev.dubhe.anvilcraft.block.entity.ProcessingTableBlockEntity;
import dev.dubhe.anvilcraft.init.block.ModBlockEntities;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

/**
 * 传送带工位本体。
 *
 *
 * <p>
 * 复刻原版鱼缸的存储与配方上下文，但外壳既可以是鱼缸，也可以是被替换成加工台的外壳；
 * 工位是传送带的附件，绝不下落或替换传送带方块，输入来自传送带中心被锁定的物品，产物重新写回传送带。
 */
public class BeltWorkstation extends FishTankBlockEntity {
    private final IBeltWorkstationHost host;
    private final IItemHandler recipeInput;
    private final List<ItemStack> pending = new ArrayList<>();
    private boolean restoring;
    @Nullable
    private ProcessingTableBlockEntity table;

    /**
     * 在给定传送带上创建工位。
     *
     * @param owner 传送带方块实体
     */
    public BeltWorkstation(BlockEntity owner) {
        super(ModBlockEntities.FISH_TANK.get(), owner.getBlockPos(), ModBlocks.FISH_TANK.getDefaultState());
        this.host = (IBeltWorkstationHost) owner;
        this.setLevel(owner.getLevel());
        this.recipeInput = new IItemHandler() {
            private IItemHandler handler(int slot) {
                return slot == 0 ? BeltWorkstation.this.host.aca$source() : BeltWorkstation.this.getInputHandler();
            }

            private int index(int slot) {
                return slot == 0 ? 0 : slot - 1;
            }

            @Override
            public int getSlots() {
                return 1 + BeltWorkstation.this.getInputHandler().getSlots();
            }

            @Override
            public ItemStack getStackInSlot(int slot) {
                return this.handler(slot).getStackInSlot(this.index(slot));
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                return this.handler(slot).insertItem(this.index(slot), stack, simulate);
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return this.handler(slot).extractItem(this.index(slot), amount, simulate);
            }

            @Override
            public int getSlotLimit(int slot) {
                return this.handler(slot).getSlotLimit(this.index(slot));
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return this.handler(slot).isItemValid(this.index(slot), stack);
            }
        };
    }

    /**
     * 判断外壳是否为鱼缸。
     *
     * @return 外壳为鱼缸时返回 {@code true}
     */
    public boolean isFishTank() {
        return this.table == null;
    }

    /**
     * 获取外壳方块的当前状态。
     *
     * @return 外壳方块状态
     */
    public BlockState shellState() {
        return this.table == null ? this.getBlockState() : this.table.getBlockState();
    }

    /**
     * 获取参与配方与渲染的方块实体。
     *
     * @return 加工台实体或工位本身
     */
    public BlockEntity recipeEntity() {
        return this.table == null ? this : this.table;
    }

    /**
     * 设置外壳方块状态。
     *
     * <p>
     * 加工台外壳会创建（或复用）一个原版加工台方块实体，并由它代理工位的库存访问。
     *
     * @param state 外壳方块状态
     */
    public void setShellState(BlockState state) {
        if (!(state.getBlock() instanceof ProcessingTableBlock block)) {
            if (this.table != null) {
                this.table.setRemoved();
            }
            this.table = null;
            this.setBlockState(state);
            return;
        }
        if (this.table != null && this.table.getBlockState().is(state.getBlock())) {
            this.table.setBlockState(state);
            return;
        }
        BlockEntity entity = block.newBlockEntity(this.worldPosition, state);
        if (!(entity instanceof ProcessingTableBlockEntity nativeTable)) {
            throw new IllegalArgumentException("Unsupported processing table: " + state);
        }
        if (this.table != null) {
            this.table.setRemoved();
        }
        this.table = nativeTable;
        this.table.setLevel(this.getLevel());
        ((IProcessingTableExtension) this.table).aca$workstation(this);
    }

    /**
     * 移除挂载的加工台并标记自身已移除。
     */
    public void removeAttached() {
        if (this.table != null) {
            this.table.setRemoved();
        }
        this.setRemoved();
    }

    @Override
    public IItemHandler getInput() {
        return this.recipeInput;
    }

    @Override
    public IItemHandler getOutput() {
        return this.getOutputHandler();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (!this.restoring) {
            this.host.aca$sync();
        }
    }

    @Override
    public void updateFishState() {
        // 工位是传送带的附件，绝不下落或替换传送带方块。
    }

    @Override
    public void tryAutoOutputResults() {
        // 产物由传送带运输负责导出，工位不接触侧面溜槽。
    }

    /**
     * 把掉落物暂存到待输出队列。
     *
     * @param stack 待暂存的物品
     */
    public void enqueue(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        ItemStack remaining = stack.copy();
        for (ItemStack old : this.pending) {
            if (!ItemStack.isSameItemSameComponents(old, remaining)) {
                continue;
            }
            int add = Math.min(old.getMaxStackSize() - old.getCount(), remaining.getCount());
            old.grow(add);
            remaining.shrink(add);
            if (remaining.isEmpty()) {
                break;
            }
        }
        while (!remaining.isEmpty()) {
            int count = Math.min(remaining.getMaxStackSize(), remaining.getCount());
            this.pending.add(remaining.copyWithCount(count));
            remaining.shrink(count);
        }
        this.setChanged();
    }

    /**
     * 推进挂载工位的一tick。
     */
    public void tickAttached() {
        Level level = this.getLevel();
        if (this.isFishTank() && level != null) {
            FishTankBlockEntity.serverTick(level, this.worldPosition, this.getBlockState(), this);
        }
        // 原版配方完全写入输出槽之后才允许导出，避免半成品被运输走。
        for (int slot = 0; slot < this.getOutputHandler().getSlots(); slot++) {
            ItemStack stack = this.getOutputHandler().getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (this.host.aca$emit(stack.copy())) {
                this.getOutputHandler().setStackInSlot(slot, ItemStack.EMPTY);
            } else {
                return;
            }
        }
        while (!this.pending.isEmpty()) {
            if (!this.host.aca$emit(this.pending.getFirst().copy())) {
                break;
            }
            this.pending.removeFirst();
            this.setChanged();
        }
    }

    /**
     * 判断待输出队列是否已经堵塞。
     *
     * @return 待输出物品超过64组时返回 {@code true}
     */
    public boolean outputBlocked() {
        return this.pending.size() > 64;
    }

    /**
     * 序列化工位数据，供传送带方块实体保存。
     *
     * @param registries 注册表访问器
     * @return 工位的独立数据
     */
    public CompoundTag saveAttached(HolderLookup.Provider registries) {
        CompoundTag tag = this.saveWithoutMetadata(registries);
        ListTag queue = new ListTag();
        for (ItemStack stack : this.pending) {
            queue.add(stack.save(registries));
        }
        tag.put("BeltPending", queue);
        if (this.table != null) {
            tag.put("BeltShellState", NbtUtils.writeBlockState(this.table.getBlockState()));
            tag.put("BeltTableData", this.table.saveWithoutMetadata(registries));
        }
        return tag;
    }

    /**
     * 反序列化工位数据，供传送带方块实体加载。
     *
     * @param tag        工位数据
     * @param registries 注册表访问器
     */
    public void loadAttached(CompoundTag tag, HolderLookup.Provider registries) {
        this.restoring = true;
        try {
            this.loadWithComponents(tag, registries);
            if (tag.contains("BeltShellState")) {
                BlockState state = NbtUtils.readBlockState(
                    registries.lookupOrThrow(Registries.BLOCK),
                    tag.getCompound("BeltShellState")
                );
                this.setShellState(state);
                if (this.table != null) {
                    this.table.loadWithComponents(tag.getCompound("BeltTableData"), registries);
                }
            }
            this.pending.clear();
            for (Tag entry : tag.getList("BeltPending", Tag.TAG_COMPOUND)) {
                ItemStack.parse(registries, entry).ifPresent(this.pending::add);
            }
        } finally {
            this.restoring = false;
        }
    }

    /**
     * 取出工位保存的全部物品。
     *
     * @return 物品列表
     */
    public List<ItemStack> takeStoredItems() {
        List<ItemStack> result = new ArrayList<>();
        List<IItemHandler> inventories = List.of(this.getInputHandler(), this.getOutputHandler());
        for (IItemHandler inventory : inventories) {
            for (int slot = 0; slot < inventory.getSlots(); slot++) {
                ItemStack stack = inventory.extractItem(slot, Integer.MAX_VALUE, false);
                if (!stack.isEmpty()) {
                    result.add(stack);
                }
            }
        }
        result.addAll(this.pending);
        this.pending.clear();
        return result;
    }
}
