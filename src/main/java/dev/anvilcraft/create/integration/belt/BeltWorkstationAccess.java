package dev.anvilcraft.create.integration.belt;

import dev.anvilcraft.lib.v2.recipe.event.ItemCacheEvent;
import dev.anvilcraft.lib.v2.recipe.util.InWorldRecipeContext;
import dev.dubhe.anvilcraft.api.event.AnvilEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * 工位与配方事件之间的桥接工具。
 *
 * <p>
 * 铁砧落下的瞬间把工位登记为“当前激活的工位”，配方查询与掉落物收集都会优先看到这个工位，
 * 而不是只看得到传送带方块本身。
 */
public final class BeltWorkstationAccess {
    private static final ThreadLocal<BeltWorkstation> ACTIVE = new ThreadLocal<>();

    private BeltWorkstationAccess() {
    }

    /**
     * 获取指定位置的工位。
     *
     * @param level 世界访问器
     * @param pos   位置
     * @return 工位，该位置没有工位时返回 {@code null}
     */
    @Nullable
    public static BeltWorkstation at(LevelAccessor level, BlockPos pos) {
        if (!level.hasChunkAt(pos)) {
            return null;
        }
        if (level.getBlockEntity(pos) instanceof IBeltWorkstationHost host) {
            return host.aca$workstation();
        }
        return null;
    }

    /**
     * 获取指定位置对外暴露的方块实体。
     *
     * @param level 世界访问器
     * @param pos   位置
     * @return 工位替换后的方块实体，没有工位时返回世界中的原方块实体
     */
    @Nullable
    public static BlockEntity entity(LevelAccessor level, BlockPos pos) {
        BeltWorkstation workstation = at(level, pos);
        return workstation == null ? level.getBlockEntity(pos) : workstation.recipeEntity();
    }

    /**
     * 获取指定位置对外暴露的方块状态。
     *
     * @param level 世界访问器
     * @param pos   位置
     * @return 工位外壳状态，没有工位时返回世界中的原方块状态
     */
    public static BlockState state(LevelAccessor level, BlockPos pos) {
        BeltWorkstation workstation = at(level, pos);
        return workstation == null ? level.getBlockState(pos) : workstation.shellState();
    }

    /**
     * 判断配方上下文是否落在工位上。
     *
     * @param ctx 配方上下文
     * @return 上下文下方存在工位时返回 {@code true}
     */
    public static boolean nearby(InWorldRecipeContext ctx) {
        return at(ctx.getLevel(), BlockPos.containing(ctx.getPos()).below()) != null;
    }

    /**
     * 在工位作用域内执行一次铁砧配方处理。
     *
     * @param event  落砧事件
     * @param action 实际处理逻辑
     */
    public static void inStrike(AnvilEvent.OnLand event, Runnable action) {
        BeltWorkstation workstation = at(event.getLevel(), event.getPos().below());
        if (workstation != null && workstation.outputBlocked()) {
            return;
        }
        BeltWorkstation previous = ACTIVE.get();
        if (workstation == null) {
            ACTIVE.remove();
        } else {
            ACTIVE.set(workstation);
        }
        try {
            action.run();
        } finally {
            if (workstation != null) {
                workstation.setChanged();
            }
            if (previous == null) {
                ACTIVE.remove();
            } else {
                ACTIVE.set(previous);
            }
        }
    }

    /**
     * 把配方掉落物收进当前激活工位的待输出队列。
     *
     * @param event 掉落物生成事件
     */
    public static void spawned(ItemCacheEvent.SpawnItemEntity event) {
        BeltWorkstation workstation = ACTIVE.get();
        if (workstation == null || workstation.getLevel() != event.getCache().getLevel()) {
            return;
        }
        ItemEntity entity = event.getEntity();
        if (entity.isRemoved() || entity.getItem().isEmpty()) {
            return;
        }
        workstation.enqueue(entity.getItem());
        entity.setItem(ItemStack.EMPTY);
        entity.discard();
    }
}
