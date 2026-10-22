package dev.anvilcraft.create.integration.belt;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;

/**
 * 传送带上的工位宿主。
 *
 * <p>
 * 由Mixin注入到 {@link com.simibubi.create.content.kinetics.belt.BeltBlockEntity}，让传送带本体负责持有工位、
 * 提供工位读取传送带中心物品的通道，并把产物写回传送带运输列表。
 */
public interface IBeltWorkstationHost {
    /**
     * 获取当前工位。
     *
     * @return 工位，未安装时返回 {@code null}
     */
    @Nullable
    BeltWorkstation aca$workstation();

    /**
     * 设置当前工位。
     *
     * @param workstation 工位，{@code null} 表示移除
     */
    void aca$setWorkstation(@Nullable BeltWorkstation workstation);

    /**
     * 获取只读传送到工位配方的输入通道。
     *
     * @return 单槽输入通道
     */
    IItemHandler aca$source();

    /**
     * 把方块实体变更同步到客户端。
     */
    void aca$sync();

    /**
     * 把产物写回传送带。
     *
     * @param stack 产物
     * @return 写入成功时返回 {@code true}
     */
    boolean aca$emit(ItemStack stack);

    /**
     * 解锁工位占用的传送带中心物品。
     */
    void aca$release();

    /**
     * 获取传送带工位专用的加工行为。
     *
     * @return 加工行为
     */
    Object aca$processor();
}
