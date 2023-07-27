package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;

public class PottedFlytrapBlock extends FlowerPotBlock {

    public static final BooleanProperty OPEN = BooleanProperty.create("open");

    public PottedFlytrapBlock() {
        super(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> ACBlockRegistry.FLYTRAP.get(), BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY).randomTicks());
        this.registerDefaultState(this.defaultBlockState().setValue(OPEN, Boolean.valueOf(true)));
    }

    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        super.randomTick(state, level, pos, randomSource);
        if (state.getValue(OPEN)) {
            level.setBlock(pos, state.setValue(OPEN, false), 2);
            level.scheduleTick(pos, this, 100 + randomSource.nextInt(100));
        } else {
            level.setBlock(pos, state.setValue(OPEN, true), 2);
        }
    }

    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        super.tick(state, level, pos, randomSource);
        level.setBlock(pos, state.setValue(OPEN, true), 2);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN);
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
        if (state.getValue(OPEN) && randomSource.nextInt(3) == 0) {
            Vec3 center = Vec3.upFromBottomCenterOf(pos, 0.75F).add(state.getOffset(level, pos));
            level.addParticle(ACParticleRegistry.FLY.get(), center.x, center.y, center.z, center.x, center.y, center.z);
        }
    }
}
