package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.FrostmintBlock;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.util.FrostmintExplosion;
import com.github.alexmodguy.alexscaves.server.misc.ACAdvancementTriggerRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;

public class FallingFrostmintEntity extends FallingBlockEntity {

    private BlockState frostMintState = ACBlockRegistry.FROSTMINT.get().defaultBlockState();

    public FallingFrostmintEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public FallingFrostmintEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.FALLING_FROSTMINT.get(), level);
        this.setBoundingBox(this.makeBoundingBox());
    }

    private FallingFrostmintEntity(Level level, double x, double y, double z, BlockState state) {
        this(ACEntityRegistry.FALLING_FROSTMINT.get(), level);
        this.frostMintState = state;
        this.blocksBuilding = true;
        this.setPos(x, y, z);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.setStartPos(this.blockPosition());
    }

    public static FallingFrostmintEntity fall(Level level, BlockPos pos, BlockState state) {
        FallingFrostmintEntity fallingblockentity = new FallingFrostmintEntity(level, (double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, state.hasProperty(BlockStateProperties.WATERLOGGED) ? state.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false)) : state);
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
            BlockPos blockpos = this.blockPosition();
            if(level().getFluidState(blockpos).getFluidType() == ACFluidRegistry.PURPLE_SODA_FLUID_TYPE.get()){
                if(!level().isClientSide){
                    FrostmintExplosion explosion = new FrostmintExplosion(level(), this, this.getX(), this.getY() + 0.5F, this.getZ(), 4.0F, Explosion.BlockInteraction.DESTROY_WITH_DECAY, false);
                    explosion.explode();
                    explosion.finalizeExplosion(true);
                }
                this.discard();
                for (Player player : level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(32))) {
                    ACAdvancementTriggerRegistry.FROSTMINT_EXPLOSION.triggerForEntity(player);
                }
                return;
            }
            this.move(MoverType.SELF, this.getDeltaMovement());
            if (!this.level().isClientSide) {
                if (!this.onGround()) {
                    if (!this.level().isClientSide && (this.time > 100 && (blockpos.getY() <= this.level().getMinBuildHeight() || blockpos.getY() > this.level().getMaxBuildHeight()) || this.time > 600)) {
                        if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                            this.spawnAtLocation(block);
                        }

                        this.discard();
                    }
                } else {
                    BlockState blockstate = this.level().getBlockState(blockpos);
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
                    if (!blockstate.is(Blocks.MOVING_PISTON)) {
                        boolean flag2 = blockstate.canBeReplaced(new DirectionalPlaceContext(this.level(), blockpos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
                        boolean flag3 = FallingBlock.isFree(this.level().getBlockState(blockpos.below()));
                        boolean flag4 = this.frostMintState.canSurvive(this.level(), blockpos) && (!flag3 || this.level().getBlockState(blockpos.below()).is(ACBlockRegistry.FROSTMINT.get()));
                        BlockState setState = this.getBlockState();
                        BlockState setAboveState = null;
                        if(setState.is(ACBlockRegistry.FROSTMINT.get()) && setState.getValue(FrostmintBlock.TYPE) == SlabType.TOP){
                            setState = setState.setValue(FrostmintBlock.TYPE, SlabType.BOTTOM);
                        }
                        if (blockstate.is(ACBlockRegistry.FROSTMINT.get())) {
                            flag2 = true;
                            flag4 = true;
                            SlabType slabType = frostMintState.getValue(FrostmintBlock.TYPE);
                            SlabType belowSlabType = blockstate.getValue(FrostmintBlock.TYPE);
                            if(belowSlabType != SlabType.DOUBLE){
                                setState = blockstate.setValue(FrostmintBlock.TYPE, SlabType.DOUBLE);
                            }else{
                                if(slabType == SlabType.TOP){
                                    slabType =SlabType.BOTTOM;
                                }
                                setAboveState = ACBlockRegistry.FROSTMINT.get().defaultBlockState().setValue(FrostmintBlock.TYPE, slabType);
                            }
                        }
                        if (flag2 && flag4) {
                            boolean flag5 = false;
                            if (this.level().setBlockAndUpdate(blockpos, setState)) {
                                this.discard();
                                if (block instanceof Fallable) {
                                    ((Fallable) block).onLand(this.level(), blockpos, setState, blockstate, this);
                                }
                                flag5 = true;
                            }
                            if (setAboveState != null) {
                                BlockPos abovePos = blockpos.above();
                                BlockState aboveState = this.level().getBlockState(abovePos);
                                if (aboveState.canBeReplaced(new DirectionalPlaceContext(this.level(), abovePos, Direction.DOWN, ItemStack.EMPTY, Direction.UP))) {
                                    this.level().setBlockAndUpdate(abovePos, setAboveState);
                                } else {
                                    this.spawnAtLocation(block);
                                }
                                this.discard();
                                flag5 = true;
                            }
                            if (!flag5 && this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                this.discard();
                                this.callOnBrokenAfterFall(block, blockpos);
                                this.spawnAtLocation(block);
                            }
                        } else {
                            this.discard();
                            if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
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

    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("BlockState", NbtUtils.writeBlockState(this.frostMintState));
    }

    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.frostMintState = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), tag.getCompound("BlockState"));
    }

    public BlockState getBlockState() {
        return this.frostMintState;
    }


    public void recreateFromPacket(ClientboundAddEntityPacket clientboundAddEntityPacket) {
        super.recreateFromPacket(clientboundAddEntityPacket);
        this.frostMintState = Block.stateById(clientboundAddEntityPacket.getData());
    }

}
