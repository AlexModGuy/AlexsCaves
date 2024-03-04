package com.github.alexmodguy.alexscaves.server.block.blockentity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.AbyssalAltarBlock;
import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneBaseEntity;
import com.github.alexmodguy.alexscaves.server.message.WorldEventMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import javax.annotation.Nullable;
import java.util.UUID;

public class AbyssalAltarBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {

    net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers = net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.UP, Direction.DOWN);
    private NonNullList<ItemStack> stacks = NonNullList.withSize(1, ItemStack.EMPTY);

    private ItemStack displayCopyStack = ItemStack.EMPTY;
    private float itemAngle = 0;
    private float slideProgress;
    private float prevSlideProgress;
    private int popDelay = 0;
    private static final int[] slotsTop = new int[]{0};
    private ItemStack popStack;
    private LivingEntity lastInteracter;
    private UUID placingPlayer = null;
    private long lastInteractionTime = 0;
    private boolean slideImpulse;

    public AbyssalAltarBlockEntity(BlockPos pos, BlockState state) {
        super(ACBlockEntityRegistry.ABYSSAL_ALTAR.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AbyssalAltarBlockEntity entity) {
        if (level.isClientSide) {
            ItemStack itemStack = entity.getItem(0);
            if (!itemStack.equals(entity.displayCopyStack, false) || entity.slideImpulse) {
                if (entity.slideProgress > 0.0F) {
                    entity.prevSlideProgress = entity.slideProgress;
                    entity.slideProgress--;
                } else {
                    entity.prevSlideProgress = entity.slideProgress;
                    entity.slideImpulse = false;
                    entity.displayCopyStack = itemStack.copy();
                }
            }
        }
        if (entity.popStack != null && !level.isClientSide) {
            if (entity.popDelay++ > 6) {
                ItemStack drop = entity.popStack.copy();
                Vec3 angleAdd = new Vec3(0, 0, 1).yRot(entity.itemAngle * ((float) Math.PI / 180F));
                Vec3 vec3 = Vec3.atCenterOf(entity.worldPosition).add(0, 0.5F, 0).add(angleAdd);
                ItemEntity itemEntity = new ItemEntity(level, vec3.x, vec3.y, vec3.z, drop);
                if (entity.lastInteracter != null) {
                    itemEntity.setThrower(entity.lastInteracter.getUUID());
                    boolean kill = true;
                    if (entity.lastInteracter instanceof Player player) {
                        boolean fullInv = !hasInventorySpaceFor(player.getInventory(), drop);
                        if (!player.addItem(drop.copy()) || player.getAbilities().instabuild && fullInv) {
                            kill = false;
                        }
                    } else if (entity.lastInteracter instanceof DeepOneBaseEntity deepOne) {
                        deepOne.swapItemsForAnimation(itemEntity.getItem());
                        deepOne.setItemInHand(InteractionHand.MAIN_HAND, itemEntity.getItem());
                        deepOne.setAnimation(deepOne.getTradingAnimation());
                        deepOne.playSound(deepOne.getAdmireSound());
                        if (entity.placingPlayer != null) {
                            deepOne.addReputation(entity.placingPlayer, 5);
                            entity.placingPlayer = null;
                        }
                        kill = true;
                    }
                    itemEntity.setItem(drop);
                    if (kill) {
                        entity.lastInteracter.onItemPickup(itemEntity);
                        entity.lastInteracter.take(itemEntity, drop.getCount());
                        itemEntity.discard();
                    }else{
                        level.addFreshEntity(itemEntity);
                        itemEntity.setDefaultPickUpDelay();
                    }
                }
                entity.popStack = null;
                entity.lastInteracter = null;
            }
        }
    }

    private static boolean hasInventorySpaceFor(Inventory inventory, ItemStack drop) {
        return inventory.getSlotWithRemainingSpace(drop) != -1 || inventory.getFreeSlot() != -1;
    }

    public void onEntityInteract(LivingEntity entity, boolean flip) {
        displayCopyStack = this.getItem(0).copy();
        if (flip) {
            if (this.getBlockState().getValue(AbyssalAltarBlock.ACTIVE)) {
                level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(AbyssalAltarBlock.ACTIVE, false));
            }
        } else {
            if (entity instanceof DeepOneBaseEntity) {
                level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(AbyssalAltarBlock.ACTIVE, true));
            }
        }
        Vec3 vec3 = entity.position().subtract(Vec3.atCenterOf(this.worldPosition));
        itemAngle = Mth.wrapDegrees((float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)));
        lastInteracter = entity;
        popDelay = 0;
        resetSlideAnimation();
        if(!level.isClientSide){
            BlockPos blockPos = this.getBlockPos();
            AlexsCaves.sendMSGToAll(new WorldEventMessage(6, blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        }
        if (entity instanceof Player) {
            placingPlayer = entity.getUUID();
        } else {
            lastInteractionTime = entity.level().getGameTime();
        }
    }

    public void resetSlideAnimation(){
        prevSlideProgress = 5.0F;
        slideProgress = 5.0F;
        slideImpulse = true;
    }

    public float getSlideProgress(float partialTick) {
        return (prevSlideProgress + (slideProgress - prevSlideProgress) * partialTick) * 0.2F;
    }

    public float getItemAngle() {
        return itemAngle;
    }

    public ItemStack getDisplayStack() {
        return displayCopyStack;
    }

    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition, worldPosition.offset(1, 2, 1));
    }

    @Override
    public int getContainerSize() {
        return this.stacks.size();
    }

    @Override
    public ItemStack getItem(int index) {
        return this.stacks.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        if (!this.stacks.get(index).isEmpty()) {
            ItemStack itemstack;

            if (this.stacks.get(index).getCount() <= count) {
                itemstack = this.stacks.get(index);
                this.stacks.set(index, ItemStack.EMPTY);
                return itemstack;
            } else {
                itemstack = this.stacks.get(index).split(count);

                if (this.stacks.get(index).isEmpty()) {
                    this.stacks.set(index, ItemStack.EMPTY);
                }

                return itemstack;
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    public ItemStack getStackInSlotOnClosing(int index) {
        if (!this.stacks.get(index).isEmpty()) {
            ItemStack itemstack = this.stacks.get(index);
            this.stacks.set(index, itemstack);
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameTags(stack, this.stacks.get(index));
        this.stacks.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.markUpdated();
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compound, this.stacks);
        if (compound.contains("PopStack")) {
            this.popStack = ItemStack.of(compound.getCompound("PopStack"));
        }
        if (compound.hasUUID("PlayerUUID")) {
            placingPlayer = compound.getUUID("PlayerUUID");
        }
        slideProgress = compound.getFloat("SlideAmount");
        prevSlideProgress = compound.getFloat("PrevSlideAmount");

        itemAngle = compound.getFloat("Angle");
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        ContainerHelper.saveAllItems(compound, this.stacks);
        if (this.popStack != null && !this.popStack.isEmpty()) {
            CompoundTag stackTag = new CompoundTag();
            this.popStack.save(stackTag);
            compound.put("PopStack", stackTag);
        }
        if (this.placingPlayer != null) {
            compound.putUUID("PlayerUUID", this.placingPlayer);
        }
        compound.putFloat("Angle", itemAngle);
        compound.putFloat("SlideAmount", slideProgress);
        compound.putFloat("PrevSlideAmount", prevSlideProgress);
    }

    @Override
    public void startOpen(Player player) {
    }

    @Override
    public void stopOpen(Player player) {
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        this.stacks.clear();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return slotsTop;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return false;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return true;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        if (packet != null && packet.getTag() != null) {
            this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
            ContainerHelper.loadAllItems(packet.getTag(), this.stacks);
            this.itemAngle = packet.getTag().getFloat("Angle");
        }
    }

    public CompoundTag getUpdateTag() {
        CompoundTag compoundtag = new CompoundTag();
        ContainerHelper.saveAllItems(compoundtag, this.stacks, true);
        compoundtag.putFloat("Angle", itemAngle);
        return compoundtag;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack lvt_2_1_ = this.stacks.get(index);
        if (lvt_2_1_.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.stacks.set(index, ItemStack.EMPTY);
            return lvt_2_1_;
        }
    }

    @Override
    public Component getDisplayName() {
        return getDefaultName();
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.alexsmobs.capsid");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return null;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < this.getContainerSize(); i++) {
            if (!this.getItem(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
        if (!this.remove && facing != null && capability == ForgeCapabilities.ITEM_HANDLER) {
            if (facing == Direction.DOWN)
                return handlers[0].cast();
            else
                return handlers[1].cast();
        }
        return super.getCapability(capability, facing);
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public boolean queueItemDrop(ItemStack copy) {
        if (popStack != null) {
            return false;
        }
        popStack = copy;
        return true;
    }

    public long getLastInteractionTime() {
        return lastInteractionTime;
    }
}
