package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.LuxtructosaurusEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FissurePrimalMagmaBlock extends Block {

    public static final IntegerProperty REGEN_HEIGHT = IntegerProperty.create("regen_height", 0, 4);

    public FissurePrimalMagmaBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().lightLevel((state) -> {
            return 5;
        }).strength(0.5F).isValidSpawn((state, getter, pos, entityType) -> {
            return entityType.fireImmune();
        }).hasPostProcess((state, getter, pos) -> true).emissiveRendering((state, getter, pos) -> true).sound(ACSoundTypes.FLOOD_BASALT).randomTicks());
        this.registerDefaultState(this.defaultBlockState().setValue(REGEN_HEIGHT, 0));
    }

    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        if (!entity.isSteppingCarefully() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) entity)) {
            entity.hurt(level.damageSources().hotFloor(), 1.0F);
            entity.setSecondsOnFire(6);
        }
        super.stepOn(level, blockPos, blockState, entity);
    }

    public void entityInside(BlockState state, Level level, BlockPos blockPos, Entity entity) {
        if (!(entity instanceof LuxtructosaurusEntity)) {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.9D, 0.1D, 0.9D));
            entity.hurt(level.damageSources().hotFloor(), 1.0F);
            entity.setSecondsOnFire(6);
        }
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        if(context instanceof EntityCollisionContext entityCollisionContext && !(entityCollisionContext.getEntity() instanceof LuxtructosaurusEntity)){
            return entityCollisionContext.getEntity() instanceof ItemEntity ? Shapes.empty() : PrimalMagmaBlock.SINK_SHAPE;
        }
        return super.getCollisionShape(state, level, blockPos, context);
    }

    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos blockPos) {
        return Shapes.block();
    }

    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        return Shapes.empty();
    }

    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if(!PrimalMagmaBlock.isBossActive(serverLevel)){
            int regenHeight = blockState.getValue(REGEN_HEIGHT);
            for(int i = 0; i < regenHeight + 1; i++){
                BlockState regenState = ACBlockRegistry.FLOOD_BASALT.get().defaultBlockState();
                BlockPos regenPos = blockPos.above(i);
                List<BlockState> neighbors = new ArrayList<>();
                if(i != 0 && !serverLevel.getBlockState(regenPos).isAir()){
                    continue;
                }
                for(Direction direction : Direction.values()){
                    BlockState offsetState = serverLevel.getBlockState(regenPos.relative(direction));
                    if(offsetState.is(ACTagRegistry.REGENERATES_AFTER_PRIMORDIAL_BOSS_FIGHT) && !offsetState.is(this)){
                        neighbors.add(offsetState);
                    }
                }
                if(!neighbors.isEmpty()){
                    if(neighbors.stream().anyMatch(state -> state.is(Blocks.GRASS_BLOCK))){
                        regenState = Blocks.GRASS_BLOCK.defaultBlockState();
                    }else if(neighbors.stream().anyMatch(state -> state.is(Blocks.MOSS_BLOCK))){
                        regenState = Blocks.MOSS_BLOCK.defaultBlockState();
                    }else if(neighbors.stream().anyMatch(state -> state.is(Blocks.DIRT))){
                        regenState = Blocks.DIRT.defaultBlockState();
                    }else{
                        regenState = Util.getRandom(neighbors, randomSource);
                    }
                }else{
                    BlockState lowestNonMagma = findNonMagmaBlockBeneath(serverLevel, blockPos);
                    if(lowestNonMagma.is(ACTagRegistry.REGENERATES_AFTER_PRIMORDIAL_BOSS_FIGHT)){
                        regenState = lowestNonMagma;
                    }
                }
                serverLevel.setBlockAndUpdate(regenPos, regenState);
            }
            if(serverLevel.random.nextInt(2) == 0){
                serverLevel.playSound((Player)null, blockPos, ACSoundRegistry.PRIMAL_MAGMA_FISSURE_CLOSE.get(), SoundSource.BLOCKS);
            }
        }
    }

    private BlockState findNonMagmaBlockBeneath(Level level, BlockPos blockPos) {
        while (blockPos.getY() > level.getMinBuildHeight() && level.getBlockState(blockPos).is(this)){
            blockPos = blockPos.below();
        }
        return level.getBlockState(blockPos);
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        BlockState newState = super.updateShape(state, direction, state1, levelAccessor, blockPos, blockPos1);
        levelAccessor.scheduleTick(blockPos, this, 2);
        return newState;
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
        Vec3 center = Vec3.upFromBottomCenterOf(pos, 1).add(randomSource.nextFloat() - 0.5F, 0, randomSource.nextFloat() - 0.5F);
        Vec3 delta = new Vec3(randomSource.nextFloat() - 0.5F, randomSource.nextFloat() - 0.5F, randomSource.nextFloat() - 0.5F);
        if(randomSource.nextFloat() <= 0.33F) {
            level.addParticle(ACParticleRegistry.RED_VENT_SMOKE.get(), center.x, center.y, center.z, delta.x * 0.3F, 0.15F, delta.z * 0.3F);
        }
        if(randomSource.nextFloat() < 0.1F){
            level.addParticle(ParticleTypes.LAVA, center.x, center.y, center.z, delta.x, 0.7F + delta.y, delta.z);
            level.playLocalSound(center.x, center.y, center.z, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.2F + randomSource.nextFloat() * 0.2F, 0.75F + randomSource.nextFloat() * 0.25F, false);
        }
    }

    public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        return new ItemStack(ACBlockRegistry.PRIMAL_MAGMA.get());
    }

    public boolean isPathfindable(BlockState blockState, BlockGetter getter, BlockPos blockPos, PathComputationType computationType) {
        return false;
    }

    @Override
    public boolean isBurning(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public BlockPathTypes getAdjacentBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob, BlockPathTypes originalType) {
        return BlockPathTypes.DANGER_FIRE;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(REGEN_HEIGHT);
    }
}

