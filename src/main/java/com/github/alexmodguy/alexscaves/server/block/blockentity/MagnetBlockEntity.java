package com.github.alexmodguy.alexscaves.server.block.blockentity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.MagnetBlock;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.MovingMetalBlockEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.FallingBlockEntityAccessor;
import com.github.alexmodguy.alexscaves.server.entity.util.MagnetUtil;
import com.github.alexmodguy.alexscaves.server.entity.util.MovingBlockData;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class MagnetBlockEntity extends BlockEntity {

    public int age = 0;
    private float prevRangeVisuality;
    private float rangeVisuality;

    private int extenderIngots = 0;
    private int retracterIngots = 0;
    private static final int MAXIMUM_BLOCKS_PUSHED = 27;

    private boolean locallyActive;

    public MagnetBlockEntity(BlockPos pos, BlockState state) {
        super(ACBlockEntityRegistry.MAGNET.get(), pos, state);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, MagnetBlockEntity entity) {
        entity.prevRangeVisuality = entity.rangeVisuality;
        entity.age++;
        if (level.isClientSide) {
            if (entity.showRangeBox(AlexsCaves.PROXY.getClientSidePlayer())) {
                if (entity.rangeVisuality < 1.0F) {
                    entity.rangeVisuality += 0.2F;
                }
            } else {
                if (entity.rangeVisuality > 0.0F) {
                    entity.rangeVisuality -= 0.2F;
                }
            }
            entity.locallyActive = false;
            if(state.getValue(MagnetBlock.POWERED)){
                entity.locallyActive = true;
            }
            if(entity.locallyActive && !entity.isRemoved()){
                AlexsCaves.PROXY.playWorldSound(entity, (byte)4);
            }
        } else {
            Direction direction = entity.getDirection();
            if (state.getValue(MagnetBlock.POWERED)) {
                int distance = entity.getEffectiveRange();
                for (int i = 1; i <= distance; i++) {
                    BlockPos checkMetalAt = blockPos.relative(direction, i);
                    BlockState metalState = level.getBlockState(checkMetalAt);
                    if (metalState.is(ACTagRegistry.MAGNETIC_BLOCKS)) {
                        List<BlockPos> gathered = new ArrayList<>();
                        entity.gatherAttachedBlocks(checkMetalAt, gathered);
                        if (!gathered.isEmpty()) {
                            List<MovingBlockData> allData = new ArrayList<>();
                            for (BlockPos pos : gathered) {
                                BlockState moveState = level.getBlockState(pos);
                                BlockEntity te = level.getBlockEntity(pos);
                                BlockPos offset = pos.subtract(checkMetalAt);
                                MovingBlockData data = new MovingBlockData(moveState, moveState.getShape(level, pos), offset, te == null ? null : te.saveWithoutMetadata());
                                level.removeBlockEntity(pos);
                                allData.add(data);
                            }
                            //sort to break the top blocks first
                            gathered.sort(((blockPos1, blockPos2) -> sortGatheredBlocks(level, blockPos1, blockPos2)));
                            for (BlockPos pos : gathered) {
                                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                            }
                            MovingMetalBlockEntity metalBlockEntity = ACEntityRegistry.MOVING_METAL_BLOCK.get().create(level);
                            metalBlockEntity.moveTo(Vec3.atCenterOf(checkMetalAt));
                            metalBlockEntity.setAllBlockData(MovingMetalBlockEntity.createTagFromData(allData));
                            metalBlockEntity.setPlacementCooldown(1);
                            level.addFreshEntity(metalBlockEntity);
                        }
                    }
                }
            }
        }
        if (state.getValue(MagnetBlock.POWERED)) {
            for (Entity entity1 : level.getEntitiesOfClass(Entity.class, entity.getRangeBB(entity.getEffectiveRange(), false), EntitySelector.NO_SPECTATORS)) {
                if (MagnetUtil.isPulledByMagnets(entity1) || entity1 instanceof MovingMetalBlockEntity) {
                    entity.pushEntity(entity1);
                }
            }
            if (level.isClientSide && level.random.nextFloat() < 0.1F) {
                Direction dir = state.getValue(MagnetBlock.FACING);
                Vec3 blockVec = Vec3.atCenterOf(entity.getBlockPos());
                Vec3 edgeVec = blockVec.relative(dir, entity.getEffectiveRange() - 1.5F);
                if (entity.isAzure()) {
                    Vec3 directionVec = edgeVec.add(level.random.nextFloat() - 0.5F, level.random.nextFloat() - 0.5F, level.random.nextFloat() - 0.5F);
                    level.addParticle(ACParticleRegistry.AZURE_MAGNETIC_FLOW.get(), blockVec.x, blockVec.y, blockVec.z, directionVec.x, directionVec.y, directionVec.z);
                } else {
                    blockVec = blockVec.relative(dir, 2);
                    edgeVec = edgeVec.relative(dir, 2);
                    Vec3 directionVec = edgeVec.add(level.random.nextFloat() - 0.5F, level.random.nextFloat() - 0.5F, level.random.nextFloat() - 0.5F);
                    level.addParticle(ACParticleRegistry.SCARLET_MAGNETIC_FLOW.get(), directionVec.x, directionVec.y, directionVec.z, blockVec.x, blockVec.y, blockVec.z);
                }
            }
        }
    }

    private static int sortGatheredBlocks(Level level, BlockPos blockPos1, BlockPos blockPos2) {
        BlockState blockState1 = level.getBlockState(blockPos1);
        BlockState blockState2 = level.getBlockState(blockPos2);
        int order = blockState1.is(ACTagRegistry.MAGNET_REMOVES_LAST) || blockState2.is(ACTagRegistry.MAGNET_REMOVES_LAST) ?  1 : -1;
        return order * Integer.compare(blockPos1.getY(), blockPos2.getY());
    }

    private void pushEntity(Entity entity) {
        float strength = 0.2F;
        Vec3 prev = entity.getDeltaMovement();
        Vec3 blockVec = Vec3.atCenterOf(this.getBlockPos());
        Vec3 pull = this.isAzure() ? entity.position().subtract(blockVec) : blockVec.subtract(entity.position());
        if (pull.length() > 1F) {
            pull = pull.normalize();
        }
        if (entity instanceof MovingMetalBlockEntity metalBlockEntity) {
            double distance = Math.sqrt(entity.distanceToSqr(blockVec));
            strength = 0.04F;
            if(this.isAzure()){
                float f = Math.max(getEffectiveRange() - 1F, 1F);
                float f1 = (float)(f - (distance + 1)) / f;
                strength *= Math.max(f1, 0F);
            }else if (distance <= 1) {
                strength = 0;
            }
            metalBlockEntity.setPlacementCooldown(2);

        }
        if (entity instanceof FallingBlockEntityAccessor fallingBlockEntity) {
            fallingBlockEntity.setFallBlockingTime();
            strength = 0.04F;
            entity.setDeltaMovement(prev.multiply(0.5F, 0.5F, 0.5F));
        }
        if (entity instanceof LivingEntity) {
            strength = 0.2F;
            if (Math.abs(pull.x) > Math.abs(pull.y) && Math.abs(pull.x) > Math.abs(pull.z)) {
                pull = new Vec3(pull.x, 0, 0);
            }
            if (Math.abs(pull.y) > Math.abs(pull.x) && Math.abs(pull.y) > Math.abs(pull.z)) {
                pull = new Vec3(0, pull.y, 0);
            }
            if (Math.abs(pull.z) > Math.abs(pull.x) && Math.abs(pull.z) > Math.abs(pull.y)) {
                pull = new Vec3(0, 0, pull.z);
            }
            entity.fallDistance = 0.0F;
        }
        if (!MagnetUtil.isEntityOnMovingMetal(entity)) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(strength * pull.x, strength * pull.y, strength * pull.z));
        }
    }

    public void gatherAttachedBlocks(BlockPos pos, List<BlockPos> list) {
        if (list.size() < MAXIMUM_BLOCKS_PUSHED && !list.contains(pos)) {
            list.add(pos);
            for (Direction dir : Direction.values()) {
                BlockPos offset = pos.relative(dir);
                if (!this.getBlockPos().equals(offset) && canMove(pos, offset)) {
                    gatherAttachedBlocks(offset, list);
                }
            }
        }
    }

    public void setRemoved() {
        AlexsCaves.PROXY.clearSoundCacheFor(this);
        super.setRemoved();
    }

    public boolean canMove(BlockPos from, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        BlockState other = level.getBlockState(from);
        if (state.isAir() || state.is(ACTagRegistry.UNMOVEABLE)) {
            return false;
        } else if (state.is(ACTagRegistry.MAGNETIC_BLOCKS)) {
            return true;
        } else if (state.isStickyBlock()) {
            return state.canStickTo(other);
        }
        return other.isStickyBlock();
    }

    public Direction getDirection() {
        if (this.getBlockState().getBlock() instanceof MagnetBlock) {
            return this.getBlockState().getValue(MagnetBlock.FACING);
        }
        return Direction.UP;
    }

    public boolean isLocallyActive(){
        return locallyActive;
    }

    public AABB getRangeBB(double effectiveRange, boolean includeMagnet) {
        AABB blockAABB = AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(includeMagnet ? this.getBlockPos() : this.getBlockPos().relative(getDirection())));
        double i = effectiveRange - 1;
        switch (getDirection()) {
            case UP:
                return blockAABB.setMaxY(blockAABB.maxY + i);
            case DOWN:
                return blockAABB.setMinY(blockAABB.minY - i);
            case EAST:
                return blockAABB.setMaxX(blockAABB.maxX + i);
            case WEST:
                return blockAABB.setMinX(blockAABB.minX - i);
            case SOUTH:
                return blockAABB.setMaxZ(blockAABB.maxZ + i);
            case NORTH:
                return blockAABB.setMinZ(blockAABB.minZ - i);
        }
        return blockAABB;
    }

    public boolean isAzure() {
        return this.getBlockState().getBlock() == ACBlockRegistry.AZURE_MAGNET.get();
    }

    public int getEffectiveRange() {
        int rangeModifier = extenderIngots - retracterIngots;
        int total = Mth.clamp(5 + rangeModifier, 1, 64);
        return this.isAzure() ? total : total + 1;
    }


    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return getRangeBB(this.getEffectiveRange() + 2, true);
    }


    public boolean canAddRange() {
        int rangeModifier = extenderIngots - retracterIngots;
        return 5 + rangeModifier < 64 && extenderIngots + retracterIngots < 64;
    }

    public boolean canRemoveRange() {
        int rangeModifier = extenderIngots - retracterIngots;
        return 5 + rangeModifier > 1 && extenderIngots + retracterIngots < 64;
    }

    public void increaseRange(int by) {
        if (by > 0) {
            extenderIngots += by;
        } else {
            retracterIngots -= by;
        }
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        this.extenderIngots = tag.getInt("ExtenderIngots");
        this.retracterIngots = tag.getInt("RetractorIngots");
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("ExtenderIngots", this.extenderIngots);
        tag.putInt("RetractorIngots", this.retracterIngots);
    }

    public float getRangeVisuality(float partialTicks) {
        return prevRangeVisuality + (rangeVisuality - prevRangeVisuality) * partialTicks;
    }

    public boolean showRangeBox(Player entity) {
        return entity != null && (isExtenderItem(entity.getMainHandItem()) || isExtenderItem(entity.getOffhandItem()) || isRetracterItem(entity.getMainHandItem()) || isRetracterItem(entity.getOffhandItem()));
    }

    public boolean isExtenderItem(ItemStack stack) {
        return isAzure() ? stack.is(ACItemRegistry.AZURE_NEODYMIUM_INGOT.get()) : stack.is(ACItemRegistry.SCARLET_NEODYMIUM_INGOT.get());
    }

    public boolean isRetracterItem(ItemStack stack) {
        return isAzure() ? stack.is(ACItemRegistry.SCARLET_NEODYMIUM_INGOT.get()) : stack.is(ACItemRegistry.AZURE_NEODYMIUM_INGOT.get());
    }

    public void dropIngots(boolean azure) {
        Vec3 vec = Vec3.atCenterOf(this.getBlockPos());
        if (extenderIngots > 0) {
            ItemStack extenderIngot = new ItemStack(azure ? ACItemRegistry.AZURE_NEODYMIUM_INGOT.get() : ACItemRegistry.SCARLET_NEODYMIUM_INGOT.get(), extenderIngots);
            ItemEntity itemEntity = new ItemEntity(level, vec.x, vec.y, vec.z, extenderIngot);
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);
        }
        if (retracterIngots > 0) {
            ItemStack retractorIngot = new ItemStack(azure ? ACItemRegistry.SCARLET_NEODYMIUM_INGOT.get() : ACItemRegistry.AZURE_NEODYMIUM_INGOT.get(), retracterIngots);
            ItemEntity itemEntity = new ItemEntity(level, vec.x, vec.y, vec.z, retractorIngot);
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);
        }
    }
}
