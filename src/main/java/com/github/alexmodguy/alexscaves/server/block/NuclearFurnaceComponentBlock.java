package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.block.blockentity.NuclearFurnaceBlockEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class NuclearFurnaceComponentBlock extends Block implements WorldlyContainerHolder {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    private static final VoxelShape TOP_1_SHAPE = ACMath.buildShape(
            Block.box(0, 0, 0, 16, 16, 9),
            Block.box(0, 0, 0, 9, 16, 16)
    );
    private static final VoxelShape TOP_2_SHAPE = ACMath.buildShape(
            Block.box(0, 0, 7, 16, 16, 16),
            Block.box(0, 0, 0, 9, 16, 16)
    );
    private static final VoxelShape TOP_3_SHAPE = ACMath.buildShape(
            Block.box(0, 0, 0, 16, 16, 9),
            Block.box(7, 0, 0, 16, 16, 16)
    );
    private static final VoxelShape TOP_4_SHAPE = ACMath.buildShape(
            Block.box(0, 0, 7, 16, 16, 16),
            Block.box(7, 0, 0, 16, 16, 16)
    );

    public NuclearFurnaceComponentBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).pushReaction(PushReaction.BLOCK).strength(5, 1001).sound(ACSoundTypes.NUCLEAR_BOMB).noOcclusion().randomTicks());
        this.registerDefaultState(this.defaultBlockState().setValue(ACTIVE, Boolean.valueOf(false)));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        if (state.getValue(ACTIVE)) {
            BlockPos corner = getCornerForFurnace(getter, pos, true);
            if (corner != null && corner.getY() == pos.getY() - 1) { //top
                BlockPos sub = pos.subtract(corner);
                if (sub.getX() == 0 && sub.getZ() == 0) {
                    return TOP_1_SHAPE;
                } else if (sub.getX() == 0 && sub.getZ() == 1) {
                    return TOP_2_SHAPE;
                } else if (sub.getX() == 1 && sub.getZ() == 0) {
                    return TOP_3_SHAPE;
                } else if (sub.getX() == 1 && sub.getZ() == 1) {
                    return TOP_4_SHAPE;
                }
            }
        }
        return super.getShape(state, getter, pos, context);
    }

    public void entityInside(BlockState state, Level level, BlockPos blockPos, Entity entity) {
        if (state.getValue(ACTIVE)) {
            BlockPos corner = getCornerForFurnace(level, blockPos, true);
            if (corner != null && corner.getY() == blockPos.getY() - 1 && level.getBlockEntity(corner) instanceof NuclearFurnaceBlockEntity furnace && furnace.isUndergoingFission()) { //top
                if(entity instanceof LivingEntity living && !entity.getType().is(ACTagRegistry.RESISTS_RADIATION)){
                    living.addEffect(new MobEffectInstance(ACEffectRegistry.IRRADIATED.get(), 2000, 3));
                }
            }
        }
    }

    public boolean isPathfindable(BlockState state, BlockGetter getter, BlockPos blockPos, PathComputationType pathComputationType) {
        return false;
    }

    @Nullable
    public static BlockPos getCornerForFurnace(BlockGetter levelAccessor, BlockPos componentPos, boolean postConstruction) {
        if (postConstruction) {
            for (BlockPos pos : BlockPos.betweenClosed(componentPos.getX() - 1, componentPos.getY() - 1, componentPos.getZ() - 1, componentPos.getX() + 1, componentPos.getY() + 1, componentPos.getZ() + 1)) {
                if (levelAccessor.getBlockState(pos).is(ACBlockRegistry.NUCLEAR_FURNACE.get())) {
                    return pos;
                }
            }
            return null;
        } else {
            BlockPos furthest = componentPos;
            int j = 0;
            int maxDist = 1;
            while (canBecomeAComponent(levelAccessor, furthest.west(), false) && j < maxDist) {
                furthest = furthest.west();
                j++;
            }
            j = -1;
            while (canBecomeAComponent(levelAccessor, furthest.below(), false) && j < maxDist) {
                furthest = furthest.below();
                j++;
            }
            j = -1;
            while (canBecomeAComponent(levelAccessor, furthest.north(), false) && j < maxDist) {
                furthest = furthest.north();
                j++;
            }
            return canBecomeAComponent(levelAccessor, furthest, false) ? furthest : null;
        }
    }


    public static boolean isCornerForFurnace(LevelReader levelAccessor, BlockPos componentPos, boolean checkMiddle, boolean active) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int x = 0; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = 0; z <= 1; z++) {
                    mutableBlockPos.set(componentPos.getX() + x, componentPos.getY() + y, componentPos.getZ() + z);
                    if (checkMiddle || x != 0 || y != 0 || z != 0) {
                        BlockState state = levelAccessor.getBlockState(mutableBlockPos);
                        if (!state.is(ACBlockRegistry.NUCLEAR_FURNACE_COMPONENT.get()) || state.getValue(ACTIVE) != active) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public static void activateNeighbors(LevelAccessor levelAccessor, BlockPos cornerPos, boolean active) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int x = 0; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = 0; z <= 1; z++) {
                    mutableBlockPos.set(cornerPos.getX() + x, cornerPos.getY() + y, cornerPos.getZ() + z);
                    BlockState state = levelAccessor.getBlockState(mutableBlockPos);
                    if (state.is(ACBlockRegistry.NUCLEAR_FURNACE_COMPONENT.get())) {
                        levelAccessor.setBlock(mutableBlockPos, ACBlockRegistry.NUCLEAR_FURNACE_COMPONENT.get().defaultBlockState().setValue(ACTIVE, active), 3);
                    }
                }
            }
        }
    }

    public void setPlacedBy(Level level, BlockPos blockPos, BlockState state, @Nullable LivingEntity living, ItemStack itemStack) {
        BlockPos corner = getCornerForFurnace(level, blockPos, false);
        if (corner != null && isCornerForFurnace(level, corner, true, false)) {
            Direction facing = living == null ? Direction.NORTH : living.getDirection().getOpposite();
            level.setBlockAndUpdate(corner, ACBlockRegistry.NUCLEAR_FURNACE.get().defaultBlockState().setValue(NuclearFurnaceBlock.FACING, facing));
            activateNeighbors(level, corner, true);
        }
    }

    public static boolean canBecomeAComponent(BlockGetter levelAccessor, BlockPos componentPos, boolean postConstruction) {
        BlockState state = levelAccessor.getBlockState(componentPos);
        if (postConstruction) {
            return state.is(ACBlockRegistry.NUCLEAR_FURNACE_COMPONENT.get()) || state.is(ACBlockRegistry.NUCLEAR_FURNACE.get());
        } else {
            return state.is(ACBlockRegistry.NUCLEAR_FURNACE_COMPONENT.get()) && !state.getValue(ACTIVE);
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(ACTIVE);
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(ACTIVE)) {
            BlockPos corner = getCornerForFurnace(level, pos, true);
            return corner != null && level.getBlockState(corner).is(ACBlockRegistry.NUCLEAR_FURNACE.get()) && isCornerForFurnace(level, corner, false, true);
        } else {
            return true;
        }
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        if(!state.canSurvive(levelAccessor, blockPos)){
            checkCriticalityExplosion(levelAccessor, blockPos);
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, state1, levelAccessor, blockPos, blockPos1);
    }


    @Override
    public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState state, @javax.annotation.Nullable BlockEntity entity, ItemStack itemStack) {
        checkCriticalityExplosion(level, blockPos);
        super.playerDestroy(level, player, blockPos, state, entity, itemStack);
    }

    private void checkCriticalityExplosion(LevelReader level, BlockPos pos){
        BlockState state = level.getBlockState(pos);
        if (state.is(this) && state.getValue(ACTIVE)) {
            BlockPos corner = getCornerForFurnace(level, pos, true);
            if (corner != null && level.getBlockEntity(corner) instanceof NuclearFurnaceBlockEntity nuclearFurnaceBlockEntity && nuclearFurnaceBlockEntity.getCriticality() >= 2F) {
                nuclearFurnaceBlockEntity.destroyWhileCritical(false);
            }
        }
    }

    public InteractionResult use(BlockState state, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult result) {
        if(state.getValue(ACTIVE) && !player.isShiftKeyDown()){
            BlockPos corner = getCornerForFurnace(level, blockPos, true);
            if(corner != null && level.getBlockState(corner).is(ACBlockRegistry.NUCLEAR_FURNACE.get()) && isCornerForFurnace(level, corner, false, true) && level.getBlockEntity(corner) instanceof NuclearFurnaceBlockEntity nuclearFurnaceBlockEntity){
                if (level.isClientSide) {
                    return InteractionResult.SUCCESS;
                } else if(canSurvive(state, level, blockPos)){
                    player.openMenu(nuclearFurnaceBlockEntity);
                    nuclearFurnaceBlockEntity.onPlayerUse(player);
                    player.awardStat(Stats.INTERACT_WITH_FURNACE);
                    return InteractionResult.CONSUME;
                }
            }
        }
        return super.use(state, level, blockPos, player, hand, result);
    }

    @Override
    public WorldlyContainer getContainer(BlockState state, LevelAccessor levelAccessor, BlockPos blockPos) {
        if(state.getValue(ACTIVE)){
            BlockPos corner = getCornerForFurnace(levelAccessor, blockPos, true);
            if (corner != null && levelAccessor.getBlockEntity(corner) instanceof NuclearFurnaceBlockEntity nuclearFurnaceBlockEntity) {
                return nuclearFurnaceBlockEntity.getContainerFor(blockPos.subtract(corner));
            }
        }
        return null;
    }
}
