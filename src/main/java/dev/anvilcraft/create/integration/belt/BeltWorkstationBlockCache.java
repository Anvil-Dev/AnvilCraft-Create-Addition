package dev.anvilcraft.create.integration.belt;

import dev.anvilcraft.lib.v2.recipe.cache.BlockCache;
import dev.dubhe.anvilcraft.block.FishTankBlock;
import dev.dubhe.anvilcraft.block.ProcessingTableBlock;
import dev.dubhe.anvilcraft.block.entity.FishTankBlockEntity;
import dev.dubhe.anvilcraft.block.entity.ProcessingTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * 只服务于配方的方块视图。
 *
 * <p>
 * 配方读写工位所在的位置时看到的是工位外壳，而真正的世界（Create传送带与下方方块）保持不变；
 * 需要落到世界上的方块操作会转移到传送带下方的方块上。
 */
public class BeltWorkstationBlockCache extends BlockCache {
    private final LevelAccessor world;
    private final Map<BlockPos, BlockEntity> changedEntities = new HashMap<>();
    private final Map<BlockPos, BlockState> changedStates = new HashMap<>();

    /**
     * 创建配方视图。
     *
     * @param world 世界访问器
     */
    public BeltWorkstationBlockCache(LevelAccessor world) {
        super(world);
        this.world = world;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        BeltWorkstation workstation = BeltWorkstationAccess.at(this.world, pos);
        if (workstation == null) {
            return super.getBlockState(pos);
        }
        return this.changedStates.getOrDefault(pos, workstation.shellState());
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        BeltWorkstation workstation = BeltWorkstationAccess.at(this.world, pos);
        if (workstation == null) {
            return super.getBlockEntity(pos);
        }
        return this.changedEntities.getOrDefault(pos, workstation.recipeEntity());
    }

    @Override
    public void setBlock(BlockPos pos, BlockState state) {
        if (BeltWorkstationAccess.at(this.world, pos) == null) {
            super.setBlock(pos, state);
            return;
        }
        // 流体等状态留在附件上，真正的方块修改落到传送带下方的方块。
        if (state.getBlock() instanceof FishTankBlock || state.getBlock() instanceof ProcessingTableBlock) {
            this.changedStates.put(pos.immutable(), state);
        } else {
            super.setBlock(pos.below(), state);
        }
    }

    @Override
    public void setBlockEntity(BlockPos pos, BlockEntity entity) {
        if (BeltWorkstationAccess.at(this.world, pos) == null) {
            super.setBlockEntity(pos, entity);
            return;
        }
        if (entity instanceof FishTankBlockEntity || entity instanceof ProcessingTableBlockEntity) {
            this.changedEntities.put(pos.immutable(), entity);
        } else {
            super.setBlockEntity(pos.below(), entity);
        }
    }

    @Override
    public void accept() {
        super.accept();
        for (Map.Entry<BlockPos, BlockState> entry : this.changedStates.entrySet()) {
            BeltWorkstation workstation = BeltWorkstationAccess.at(this.world, entry.getKey());
            if (workstation != null) {
                workstation.setShellState(entry.getValue());
            }
        }
        for (Map.Entry<BlockPos, BlockEntity> entry : this.changedEntities.entrySet()) {
            BeltWorkstation workstation = BeltWorkstationAccess.at(this.world, entry.getKey());
            BlockEntity entity = entry.getValue();
            if (workstation != null && workstation.recipeEntity() != entity) {
                workstation.recipeEntity().loadWithComponents(
                    entity.saveWithoutMetadata(this.world.registryAccess()),
                    this.world.registryAccess()
                );
            }
        }
        this.changedStates.clear();
        this.changedEntities.clear();
    }
}
