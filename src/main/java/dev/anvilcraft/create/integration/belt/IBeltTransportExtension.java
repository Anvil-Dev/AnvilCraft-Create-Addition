package dev.anvilcraft.create.integration.belt;

/**
 * 传送带物品的工位标记。
 *
 * <p>
 * 由Mixin注入到 {@link com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack}，
 * 记录该物品已经由哪个工位产出，避免同一工位反复加工自己的产物。
 */
public interface IBeltTransportExtension {
    /**
     * 获取产出该物品的工位坐标。
     *
     * @return 工位坐标压缩后的long值，未标记时返回 {@link Long#MIN_VALUE}
     */
    long aca$skip();

    /**
     * 标记产出该物品的工位坐标。
     *
     * @param position 工位坐标压缩后的long值
     */
    void aca$skip(long position);
}
