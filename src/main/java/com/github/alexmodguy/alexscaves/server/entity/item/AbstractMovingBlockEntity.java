package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.util.MovingBlockData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMovingBlockEntity extends Entity {
    private static final EntityDataAccessor<CompoundTag> BLOCK_DATA_TAG = SynchedEntityData.defineId(AbstractMovingBlockEntity.class, EntityDataSerializers.COMPOUND_TAG);
    private List<MovingBlockData> data;
    private VoxelShape shape = null;
    private int placementCooldown = 40;
    private static boolean destroyErrorMessage;

    public AbstractMovingBlockEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        super.onSyncedDataUpdated(entityDataAccessor);
        if (BLOCK_DATA_TAG.equals(entityDataAccessor)) {
            data = buildDataFromTrackerTag();
            shape = getShape();
            this.setBoundingBox(this.makeBoundingBox());
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(BLOCK_DATA_TAG, new CompoundTag());
    }

    public void tick() {
        super.tick();
        if (this.movesEntities() && this.getDeltaMovement().length() > 0) {
            moveEntitiesOnTop();
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        if (!level().isClientSide && canBePlaced()) {
            if (placementCooldown > 0) {
                placementCooldown--;
            } else {
                boolean clearance = true;
                BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
                for (MovingBlockData dataBlock : this.getData()) {
                    BlockPos set = pos.offset(dataBlock.getOffset());
                    BlockState at = level().getBlockState(set);
                    if (at.isAir()) {
                        continue;
                    } else if (at.canBeReplaced()) {
                        level().destroyBlock(set, true);
                        continue;
                    }
                    clearance = false;
                }
                if (clearance) {
                    for (MovingBlockData dataBlock : this.getData()) {
                        BlockPos set = pos.offset(dataBlock.getOffset());
                        level().setBlockAndUpdate(set, dataBlock.getState());
                        if (dataBlock.blockData != null && dataBlock.getState().hasBlockEntity()) {
                            BlockEntity blockentity = this.level().getBlockEntity(set);
                            if (blockentity != null) {
                                CompoundTag compoundtag = blockentity.saveWithoutMetadata();
                                for (String s : dataBlock.blockData.getAllKeys()) {
                                    compoundtag.put(s, dataBlock.blockData.get(s).copy());
                                }
                                try {
                                    blockentity.load(compoundtag);
                                } catch (Exception exception) {
                                }
                                blockentity.setChanged();
                            }
                        }
                    }
                    this.remove(RemovalReason.KILLED);

                } else {
                    placementCooldown = 5 + random.nextInt(10);
                }
            }
        }
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
    }

    public boolean canBePlaced() {
        return true;
    }

    public abstract boolean movesEntities();

    public void moveEntitiesOnTop() {
        for (Entity entity : this.level().getEntities(this, this.getBoundingBox().inflate(0F, 0.01F, 0F), EntitySelector.NO_SPECTATORS.and((entity) -> {
            return !entity.isPassengerOfSameVehicle(this);
        }))) {
            if (!entity.noPhysics && !(entity instanceof MovingMetalBlockEntity)) {
                double gravity = entity.isNoGravity() ? 0 : 0.08D;
                if (entity instanceof LivingEntity living) {
                    AttributeInstance attribute = living.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get());
                    gravity = attribute.getValue();
                }
                float f2 = 1.0F;
                entity.move(MoverType.SHULKER, new Vec3((double) (f2 * (float) this.getDeltaMovement().x), (double) (f2 * (float) this.getDeltaMovement().y), (double) (f2 * (float) this.getDeltaMovement().z)));
                if(this.getDeltaMovement().y >= 0){
                    entity.setDeltaMovement(entity.getDeltaMovement().add(0, gravity, 0));
                }
            }
        }
    }

    protected void createBlockDropAt(BlockPos crushPos, BlockState state, CompoundTag blockData) {
        if(this.level() instanceof ServerLevel serverLevel){
            LootParams.Builder lootparams$builder = (new LootParams.Builder(serverLevel)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(crushPos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY);
            try{
                List<ItemStack> drops = state.getDrops(lootparams$builder);
                for(ItemStack drop : drops){
                    Block.popResource(serverLevel, crushPos, drop);
                }
                state.spawnAfterBreak(serverLevel, crushPos, ItemStack.EMPTY, true);
            }catch (Exception e){
                if(!destroyErrorMessage){
                    destroyErrorMessage = true;
                    AlexsCaves.LOGGER.warn("Stopped crash when trying to destroy fake block entity for {}", state.getBlock());
                }
            }
        }
    }


    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    public boolean isAttackable() {
        return false;
    }

    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("BlockDataContainer", 10)) {
            this.setAllBlockData(compound.getCompound("BlockDataContainer"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.getAllBlockData() != null) {
            compound.put("BlockDataContainer", this.getAllBlockData());
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    private List<MovingBlockData> buildDataFromTrackerTag() {
        List<MovingBlockData> list = new ArrayList<>();
        CompoundTag data = getAllBlockData();
        if (data.contains("BlockData")) {
            ListTag listTag = data.getList("BlockData", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag innerTag = listTag.getCompound(i);
                list.add(new MovingBlockData(level(), innerTag));
            }
        }
        return list;
    }


    public void setPlacementCooldown(int cooldown) {
        placementCooldown = cooldown;
    }

    public CompoundTag getAllBlockData() {
        return this.entityData.get(BLOCK_DATA_TAG);
    }

    public void setAllBlockData(CompoundTag tag) {
        this.entityData.set(BLOCK_DATA_TAG, tag);
    }

    public List<MovingBlockData> getData() {
        if (data == null) {
            data = buildDataFromTrackerTag();
        }
        return data;
    }

    public VoxelShape getShape() {
        Vec3 leftMostCorner = new Vec3(this.getX() - 0.5F, this.getY() - 0.5F, this.getZ() - 0.5F);
        if (data == null || data.isEmpty()) {
            VoxelShape building = Shapes.create(leftMostCorner.x, leftMostCorner.y, leftMostCorner.z, leftMostCorner.x + 1F, leftMostCorner.y + 1F, leftMostCorner.z + 1F);
            return building;
        }
        VoxelShape building = Shapes.create(leftMostCorner.x, leftMostCorner.y, leftMostCorner.z, leftMostCorner.x + 1F, leftMostCorner.y + 1F, leftMostCorner.z + 1F);
        for (MovingBlockData data : getData()) {
            building = Shapes.join(building, data.getShape().move(leftMostCorner.x + data.getOffset().getX(), leftMostCorner.y + data.getOffset().getY(), leftMostCorner.z + data.getOffset().getZ()), BooleanOp.OR);
        }
        return building;
    }

    @Override
    protected AABB makeBoundingBox() {
        List<AABB> aabbs = getShape().toAabbs();
        AABB minMax = new AABB(this.getX() - 0.5F, this.getY() - 0.5F, this.getZ() - 0.5F, this.getX() + 0.5F, this.getY() + 0.5F, this.getZ() + 0.5F);
        for (AABB aabb : aabbs) {
            minMax = minMax.minmax(aabb);
        }
        return minMax;
    }

    @Override
    public Vec3 getLightProbePosition(float f) {
        return this.getPosition(f);
    }


    public static CompoundTag createTagFromData(List<MovingBlockData> blocks) {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        for (MovingBlockData data : blocks) {
            listTag.add(data.toTag());
        }
        tag.put("BlockData", listTag);
        return tag;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }
}
