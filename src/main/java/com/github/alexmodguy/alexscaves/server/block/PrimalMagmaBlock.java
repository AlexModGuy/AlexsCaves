package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.entity.living.LuxtructosaurusEntity;
import com.github.alexmodguy.alexscaves.server.level.storage.ACWorldData;
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
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PrimalMagmaBlock extends Block {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty PERMANENT = BooleanProperty.create("permanent");
    public static final VoxelShape SINK_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    public PrimalMagmaBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().lightLevel((state) -> {
            return 5;
        }).strength(0.5F).isValidSpawn((state, getter, pos, entityType) -> {
            return entityType.fireImmune();
        }).hasPostProcess((state, getter, pos) -> true).emissiveRendering((state, getter, pos) -> true).sound(ACSoundTypes.FLOOD_BASALT).randomTicks());
        this.registerDefaultState(this.defaultBlockState().setValue(ACTIVE, Boolean.valueOf(false)).setValue(PERMANENT, Boolean.valueOf(false)));
    }

    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        if (!entity.isSteppingCarefully() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) entity)) {
            entity.hurt(level.damageSources().hotFloor(), 1.0F);
            entity.setSecondsOnFire(3);
        }
        super.stepOn(level, blockPos, blockState, entity);
    }

    public void entityInside(BlockState state, Level level, BlockPos blockPos, Entity entity) {
        if (!(entity instanceof LuxtructosaurusEntity)) {
            if(state.getValue(ACTIVE)){
                if(!(entity instanceof ItemEntity)){
                    entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.9D, 0.1D, 0.9D));
                    entity.hurt(level.damageSources().hotFloor(), 1.0F);
                    entity.setSecondsOnFire(3);
                }
            }else{
                entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.1D, 0));
            }
        }
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        if(state.getValue(ACTIVE)){
            if(context instanceof EntityCollisionContext entityCollisionContext && !(entityCollisionContext.getEntity() instanceof LuxtructosaurusEntity)){
                return entityCollisionContext.getEntity() instanceof ItemEntity ? Shapes.empty() : SINK_SHAPE;
            }
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
        if(blockState.getValue(ACTIVE)){
            if(!blockState.getValue(PERMANENT) && !isBossActive(serverLevel)){
                serverLevel.setBlockAndUpdate(blockPos, blockState.setValue(ACTIVE, false));
            }
        }else if(!blockState.getValue(PERMANENT) && isBossActive(serverLevel)){
            serverLevel.setBlockAndUpdate(blockPos, blockState.setValue(ACTIVE, true));
        }
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        BlockState newState = super.updateShape(state, direction, state1, levelAccessor, blockPos, blockPos1);
        levelAccessor.scheduleTick(blockPos, this, 2);
        return newState;
    }

    public static boolean isBossActive(Level level) {
        ACWorldData worldData = ACWorldData.get(level);
        if(worldData != null){
            return worldData.isPrimordialBossActive(level);
        }
        return false;
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
        if (state.getValue(ACTIVE) && randomSource.nextInt(5) == 0 && level.getBlockState(pos.above()).isAir()) {
            Vec3 center = Vec3.upFromBottomCenterOf(pos, 1).add(randomSource.nextFloat() - 0.5F, 0, randomSource.nextFloat() - 0.5F);
            Vec3 delta = new Vec3(randomSource.nextFloat() - 0.5F, randomSource.nextFloat() - 0.5F, randomSource.nextFloat() - 0.5F);
            if(randomSource.nextFloat() < 0.4F){
                level.addParticle(ParticleTypes.LAVA, center.x, center.y, center.z, delta.x, 0.4F + delta.y, delta.z);
                level.playLocalSound(center.x, center.y, center.z, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.2F + randomSource.nextFloat() * 0.2F, 0.75F + randomSource.nextFloat() * 0.25F, false);
            }else{
                level.addParticle(ParticleTypes.LARGE_SMOKE, center.x, center.y, center.z, delta.x * 0.1F, 0.35F, delta.z * 0.1F);
            }
        }
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
        builder.add(ACTIVE, PERMANENT);
    }
}
