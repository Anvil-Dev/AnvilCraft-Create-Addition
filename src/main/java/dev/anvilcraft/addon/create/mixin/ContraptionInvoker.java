package dev.anvilcraft.addon.create.mixin;

import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Contraption.class)
public interface ContraptionInvoker {
    @Invoker
    BlockEntity invokeReadBlockEntity(Level level, StructureTemplate.StructureBlockInfo info, CompoundTag tag);
}
