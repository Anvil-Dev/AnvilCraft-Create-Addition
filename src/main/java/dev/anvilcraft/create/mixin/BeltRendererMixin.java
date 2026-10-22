package dev.anvilcraft.create.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltRenderer;
import dev.anvilcraft.create.integration.belt.BeltWorkstation;
import dev.anvilcraft.create.integration.belt.IBeltWorkstationHost;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin类用于修改 {@link BeltRenderer} 的行为。
 *
 * <p>
 * 传送带只负责画自己，工位外壳与外壳上的方块实体由这里补画，因此不需要额外的方块模型或贴图资源。
 */
@Mixin(BeltRenderer.class)
abstract class BeltRendererMixin {
    /**
     * 在传送带本体之后绘制工位外壳。
     *
     * @param belt        传送带方块实体
     * @param partialTick 部分刻
     * @param pose        变换栈
     * @param buffers     缓冲区
     * @param light       光照
     * @param overlay     覆盖层
     * @param cir         回调信息
     */
    @Inject(
        method = "renderSafe(Lcom/simibubi/create/content/kinetics/belt/BeltBlockEntity;F"
            + "Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
        at = @At("HEAD")
    )
    private void aca$renderWorkstation(
        BeltBlockEntity belt,
        float partialTick,
        PoseStack pose,
        MultiBufferSource buffers,
        int light,
        int overlay,
        CallbackInfo cir
    ) {
        if (!(belt instanceof IBeltWorkstationHost host)) {
            return;
        }
        BeltWorkstation workstation = host.aca$workstation();
        if (workstation == null) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        pose.pushPose();
        minecraft.getBlockRenderer().renderSingleBlock(workstation.shellState(), pose, buffers, light, overlay);
        minecraft.getBlockEntityRenderDispatcher().renderItem(
            workstation.recipeEntity(),
            pose,
            buffers,
            light,
            overlay
        );
        pose.popPose();
    }
}
