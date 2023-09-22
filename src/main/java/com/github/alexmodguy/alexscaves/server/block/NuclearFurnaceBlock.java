package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.block.blockentity.ACBlockEntityRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.NuclearFurnaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class NuclearFurnaceBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public NuclearFurnaceBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5, 1001).sound(ACSoundTypes.NUCLEAR_BOMB).noOcclusion());
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NuclearFurnaceBlockEntity(pos, state);
    }

    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        return createTickerHelper(entityType, ACBlockEntityRegistry.NUCLEAR_FURNACE.get(), NuclearFurnaceBlockEntity::tick);
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return NuclearFurnaceComponentBlock.isCornerForFurnace(level, pos, false, true);
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        if(!state.canSurvive(levelAccessor, blockPos)){
            checkCriticalityExplosion(levelAccessor, blockPos);
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, state1, levelAccessor, blockPos, blockPos1);
    }

    public InteractionResult use(BlockState state, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult result) {
        if(!player.isShiftKeyDown()){
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            } else if(canSurvive(state, level, blockPos)){
                BlockEntity blockentity = level.getBlockEntity(blockPos);
                if (blockentity instanceof NuclearFurnaceBlockEntity nuclearFurnaceBlockEntity) {
                    player.openMenu(nuclearFurnaceBlockEntity);
                    nuclearFurnaceBlockEntity.onPlayerUse(player);
                    player.awardStat(Stats.INTERACT_WITH_FURNACE);
                }
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState state, @javax.annotation.Nullable BlockEntity entity, ItemStack itemStack) {
        checkCriticalityExplosion(level, blockPos);
        super.playerDestroy(level, player, blockPos, state, entity, itemStack);
    }

    private void checkCriticalityExplosion(LevelReader level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof NuclearFurnaceBlockEntity nuclearFurnaceBlockEntity && nuclearFurnaceBlockEntity.getCriticality() >= 2F) {
            nuclearFurnaceBlockEntity.destroyWhileCritical(false);
        }

    }
}
