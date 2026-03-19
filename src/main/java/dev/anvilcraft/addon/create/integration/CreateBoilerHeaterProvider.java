package dev.anvilcraft.addon.create.integration;

import com.simibubi.create.api.boiler.BoilerHeater;
import com.simibubi.create.api.registry.SimpleRegistry;
import dev.dubhe.anvilcraft.block.HeaterBlock;
import dev.dubhe.anvilcraft.block.heatable.GlowingBlock;
import dev.dubhe.anvilcraft.block.heatable.IncandescentBlock;
import dev.dubhe.anvilcraft.block.heatable.RedhotBlock;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CreateBoilerHeaterProvider implements SimpleRegistry.Provider<Block, BoilerHeater> {
    private static final BoilerHeater HEATER = CreateBoilerHeaterProvider::heater;
    private static final BoilerHeater REDHOT = new ConstantValueHeater(1);
    private static final BoilerHeater GLOWING = new ConstantValueHeater(2);
    private static final BoilerHeater INCANDESCENT = new ConstantValueHeater(3);

    private static float heater(Level level, BlockPos blockPos, BlockState blockState) {
        if (blockState.is(ModBlocks.HEATER) && !blockState.getValue(HeaterBlock.OVERLOAD)) {
            return 1;
        }
        return -1;
    }

    @Override
    public @Nullable BoilerHeater get(Block block) {
        return switch (block) {
            case HeaterBlock ignored -> HEATER;
            case IncandescentBlock ignored -> INCANDESCENT;
            case GlowingBlock ignored -> GLOWING;
            case RedhotBlock ignored -> REDHOT;
            default -> null;
        };
    }

    private record ConstantValueHeater(float level) implements BoilerHeater {

        @Override
        public float getHeat(Level level, BlockPos blockPos, BlockState blockState) {
            return this.level;
        }
    }
}
