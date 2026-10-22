package dev.anvilcraft.create.integration.belt;

import javax.annotation.Nullable;

/**
 * 传送带工位上的加工台扩展。
 *
 * <p>
 * 由Mixin注入到 {@link dev.dubhe.anvilcraft.block.entity.ProcessingTableBlockEntity}，
 * 让普通加工台保持自己的库存，而挂到工位上的加工台改为读写工位存储。
 */
public interface IProcessingTableExtension {
    /**
     * 获取挂载的工位。
     *
     * @return 工位，未挂载时返回 {@code null}
     */
    @Nullable
    BeltWorkstation aca$workstation();

    /**
     * 挂载工位。
     *
     * @param workstation 工位，{@code null} 表示解绑
     */
    void aca$workstation(@Nullable BeltWorkstation workstation);
}
