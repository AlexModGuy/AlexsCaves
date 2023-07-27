package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

class FlytrapBlock extends BushBlock {

    public static final BooleanProperty OPEN = BooleanProperty.create("open");
    public static final VoxelShape SHAPE = Block.box(3.5, 0, 3.5, 12.5, 21, 12.5);

    public FlytrapBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instabreak().sound(SoundType.ROOTS).randomTicks().offsetType(BlockBehaviour.OffsetType.XZ).noOcclusion().noCollission());
        this.registerDefaultState(this.defaultBlockState().setValue(OPEN, Boolean.valueOf(true)));
    }

    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        Vec3 vec3 = state.getOffset(getter, pos);
        return SHAPE.move(vec3.x, vec3.y, vec3.z);
    }

    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        if (state.getValue(OPEN)) {
            level.setBlock(pos, state.setValue(OPEN, false), 2);
            level.scheduleTick(pos, this, 100 + randomSource.nextInt(100));
        } else {
            level.setBlock(pos, state.setValue(OPEN, true), 2);

        }
    }

    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        level.setBlock(pos, state.setValue(OPEN, true), 2);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPEN);
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
        if (state.getValue(OPEN) && randomSource.nextInt(3) == 0) {
            Vec3 center = Vec3.upFromBottomCenterOf(pos, 1).add(state.getOffset(level, pos));
            level.addParticle(ACParticleRegistry.FLY.get(), center.x, center.y, center.z, center.x, center.y, center.z);
        }
    }
}