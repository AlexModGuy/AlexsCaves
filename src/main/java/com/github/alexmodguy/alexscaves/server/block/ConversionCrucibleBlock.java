package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.block.blockentity.ACBlockEntityRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.ConversionCrucibleBlockEntity;
import com.github.alexmodguy.alexscaves.server.block.blockentity.CopperValveBlockEntity;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.item.BiomeTreatItem;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ConversionCrucibleBlock extends BaseEntityBlock {


    private static final VoxelShape INSIDE = box(3.0D, 2.0D, 3.0D, 13.0D, 16.0D, 13.0D);
    private static final VoxelShape SHAPE = Shapes.join(Shapes.block(), Shapes.or(
            box(0.0D, 0.0D, 5.0D, 16.0D, 2.0D, 11.0D),
            box(5.0D, 0.0D, 0.0D, 11.0D, 2.0D, 16.0D),
            box(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D),
            INSIDE), BooleanOp.ONLY_FIRST);
    private static final VoxelShape ABOVE = Block.box(0.0D, 16.0D, 0.0D, 16.0D, 20.0D, 16.0D);
    private static final VoxelShape SUCK = Shapes.or(INSIDE, ABOVE);

    public ConversionCrucibleBlock() {
        super(Properties.of().mapColor(MapColor.GOLD).requiresCorrectToolForDrops().strength(5F, 12.0F).sound(SoundType.METAL));
    }

    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public VoxelShape getInteractionShape(BlockState blockState, BlockGetter blockGetter, BlockPos pos) {
        return INSIDE;
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
        return createTickerHelper(p_152182_, ACBlockEntityRegistry.CONVERSION_CRUCIBLE.get(), ConversionCrucibleBlockEntity::tick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ConversionCrucibleBlockEntity(pos, state);
    }


    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        ItemStack playerItem = player.getItemInHand(handIn);
        if (worldIn.getBlockEntity(pos) instanceof ConversionCrucibleBlockEntity crucible && !player.isShiftKeyDown()) {
            if(crucible.getConvertingToBiome() != null){
                if(crucible.getWantItem().isEmpty()){
                    crucible.rerollWantedItem();
                    crucible.markUpdated();
                }else if(!crucible.getWantItem().isEmpty() && crucible.getWantItem().is(playerItem.getItem())){
                    if(!worldIn.isClientSide){
                        ItemStack copy = playerItem.copy();
                        copy.setCount(1);
                        crucible.consumeItem(copy);
                        if(!player.getAbilities().instabuild){
                            playerItem.shrink(1);
                        }
                        crucible.markUpdated();
                    }
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.PASS;
            }else if(playerItem.is(ACItemRegistry.BIOME_TREAT.get()) && BiomeTreatItem.getCaveBiome(playerItem) != null){
                if(!worldIn.isClientSide){
                    crucible.setConvertingToBiome(BiomeTreatItem.getCaveBiome(playerItem));
                    crucible.setFilledLevel(1);
                    crucible.rerollWantedItem();
                    crucible.markUpdated();
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }


    public static VoxelShape getSuckShape() {
        return SUCK;
    }
}
