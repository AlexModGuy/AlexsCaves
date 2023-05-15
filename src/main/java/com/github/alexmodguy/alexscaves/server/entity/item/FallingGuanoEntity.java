package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.GuanoLayerBlock;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;

public class FallingGuanoEntity extends FallingBlockEntity {

    private BlockState guanoState = ACBlockRegistry.GUANO_LAYER.get().defaultBlockState();
    public FallingGuanoEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public FallingGuanoEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.FALLING_GUANO.get(), level);
        this.setBoundingBox(this.makeBoundingBox());
    }

    private FallingGuanoEntity(Level level, double x, double y, double z, BlockState state) {
        this(ACEntityRegistry.FALLING_GUANO.get(), level);
        this.guanoState = state;
        this.blocksBuilding = true;
        this.setPos(x, y, z);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.setStartPos(this.blockPosition());
    }

    public static FallingGuanoEntity fall(Level level, BlockPos pos, BlockState state) {
        FallingGuanoEntity fallingblockentity = new FallingGuanoEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, state.hasProperty(BlockStateProperties.WATERLOGGED) ? state.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false)) : state);
        level.setBlock(pos, state.getFluidState().createLegacyBlock(), 3);
        level.addFreshEntity(fallingblockentity);
        return fallingblockentity;
    }


    public void tick() {
        if (this.getBlockState().isAir()) {
            this.discard();
        } else {
            Block block = this.getBlockState().getBlock();
            ++this.time;
            if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
            }
            this.move(MoverType.SELF, this.getDeltaMovement());
            if (!this.level.isClientSide) {
                BlockPos blockpos = this.blockPosition();
                if (!this.onGround) {
                    if (!this.level.isClientSide && (this.time > 100 && (blockpos.getY() <= this.level.getMinBuildHeight() || blockpos.getY() > this.level.getMaxBuildHeight()) || this.time > 600)) {
                        if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                            this.spawnAtLocation(block);
                        }

                        this.discard();
                    }
                } else {
                    BlockState blockstate = this.level.getBlockState(blockpos);
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
                    if (!blockstate.is(Blocks.MOVING_PISTON)) {
                        boolean flag2 = blockstate.canBeReplaced(new DirectionalPlaceContext(this.level, blockpos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
                        boolean flag3 = FallingBlock.isFree(this.level.getBlockState(blockpos.below()));
                        boolean flag4 = this.guanoState.canSurvive(this.level, blockpos) && (!flag3 || this.level.getBlockState(blockpos.below()).is(ACBlockRegistry.GUANO_LAYER.get()));
                        BlockState setState = this.getBlockState();
                        BlockState setAboveState = null;
                        if(blockstate.is(ACBlockRegistry.GUANO_LAYER.get())){
                            flag2 = true;
                            flag4 = true;
                            int belowLayers = blockstate.getValue(GuanoLayerBlock.LAYERS);
                            int fallingLayers = guanoState.is(ACBlockRegistry.GUANO_LAYER.get()) ? guanoState.getValue(GuanoLayerBlock.LAYERS) : 8;
                            int together = belowLayers + fallingLayers;
                            setState = ACBlockRegistry.GUANO_LAYER.get().defaultBlockState().setValue(GuanoLayerBlock.LAYERS, Math.min(together, 8));
                            if(together > 8){
                                int prev = 0;
                                if(level.getBlockState(blockpos.above()).is(ACBlockRegistry.GUANO_LAYER.get())){
                                    prev = level.getBlockState(blockpos.above()).getValue(GuanoLayerBlock.LAYERS);
                                }
                                setAboveState = ACBlockRegistry.GUANO_LAYER.get().defaultBlockState().setValue(GuanoLayerBlock.LAYERS, Math.min(together - 8 + prev, 8));
                            }
                        }
                        if (flag2 && flag4) {
                            boolean flag5 = false;
                            if (this.level.setBlockAndUpdate(blockpos, setState)) {
                                this.discard();
                                if (block instanceof Fallable) {
                                    ((Fallable) block).onLand(this.level, blockpos, setState, blockstate, this);
                                }
                                flag5 = true;
                            }
                            if(setAboveState != null){
                                BlockPos abovePos = blockpos.above();
                                BlockState aboveState = this.level.getBlockState(abovePos);
                                if(aboveState.canBeReplaced(new DirectionalPlaceContext(this.level, abovePos, Direction.DOWN, ItemStack.EMPTY, Direction.UP))){
                                    this.level.setBlockAndUpdate(abovePos, setAboveState);
                                }else{
                                    this.spawnAtLocation(block);
                                }
                                this.discard();
                                flag5 = true;
                            }
                            if (!flag5 && this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                this.discard();
                                this.callOnBrokenAfterFall(block, blockpos);
                                this.spawnAtLocation(block);
                            }
                        } else {
                            this.discard();
                            if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                this.callOnBrokenAfterFall(block, blockpos);
                                this.spawnAtLocation(block);
                            }
                        }
                    } else {
                        this.discard();
                        this.callOnBrokenAfterFall(block, blockpos);
                    }

                }
            }

            this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
        }
    }

    private boolean isFullGuanoBlock(BlockState state){
        return state.is(ACBlockRegistry.GUANO_LAYER.get()) && state.getValue(GuanoLayerBlock.LAYERS) == 8;
    }

    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("BlockState", NbtUtils.writeBlockState(this.guanoState));
    }

    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.guanoState = NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), tag.getCompound("BlockState"));
    }

    public BlockState getBlockState() {
        return this.guanoState;
    }


    public void recreateFromPacket(ClientboundAddEntityPacket clientboundAddEntityPacket) {
        super.recreateFromPacket(clientboundAddEntityPacket);
        this.guanoState = Block.stateById(clientboundAddEntityPacket.getData());
    }

}
