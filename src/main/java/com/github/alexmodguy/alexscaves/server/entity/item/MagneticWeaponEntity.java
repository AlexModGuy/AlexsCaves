package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.TeletorEntity;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACAdvancementTriggerRegistry;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class MagneticWeaponEntity extends Entity {

    private static final EntityDataAccessor<ItemStack> ITEMSTACK = SynchedEntityData.defineId(MagneticWeaponEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Optional<UUID>> CONTROLLER_UUID = SynchedEntityData.defineId(MagneticWeaponEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> CONTROLLER_ID = SynchedEntityData.defineId(MagneticWeaponEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(MagneticWeaponEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IDLING = SynchedEntityData.defineId(MagneticWeaponEntity.class, EntityDataSerializers.BOOLEAN);
    private float prevStrikeProgress;
    private float strikeProgress;
    private float prevReturnProgress;
    private float returnProgress;
    private int playerUseCooldown = 0;
    private boolean comingBack = false;
    private float destroyBlockProgress;
    private BlockPos lastSelectedBlock;
    private int totalMiningTime = 0;
    private boolean hadPlayerController = false;

    private boolean spawnedItem = false;
    public boolean returnFlag = false;

    public MagneticWeaponEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public MagneticWeaponEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.MAGNETIC_WEAPON.get(), level);
        this.setBoundingBox(this.makeBoundingBox());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ITEMSTACK, new ItemStack(Items.IRON_SWORD));
        this.entityData.define(CONTROLLER_UUID, Optional.empty());
        this.entityData.define(CONTROLLER_ID, -1);
        this.entityData.define(TARGET_ID, -1);
        this.entityData.define(IDLING, true);
    }


    public void tick() {
        super.tick();
        prevStrikeProgress = strikeProgress;
        prevReturnProgress = returnProgress;
        Entity controller = getController();
        Entity target = getTarget();
        if (!level().isClientSide) {
            if (this.comingBack || controller instanceof TeletorEntity && (target == null || !target.isAlive())) {
                this.noPhysics = true;
            } else {
                this.noPhysics = false;
            }
            if (controller == null && this.tickCount > 20 || this.getItemStack().isEmpty()) {
                if(hadPlayerController){
                    this.plopItem();
                    hadPlayerController = false;
                }
                this.remove(RemovalReason.DISCARDED);
            }
        }
        if ((this.getTarget() == null || comingBack || playerUseCooldown > 0) && strikeProgress > 0) {
            strikeProgress = Math.max(0, strikeProgress - 0.1F);
        }
        if (controller instanceof TeletorEntity teletor) {
            this.entityData.set(CONTROLLER_ID, teletor.getId());
            teletor.setWeaponUUID(this.getUUID());
            if (!level().isClientSide) {
                Entity e = teletor.getTarget();
                this.entityData.set(TARGET_ID, e != null && e.isAlive() ? e.getId() : -1);
            }
            boolean attacking = !comingBack && target != null && target.isAlive();
            Vec3 vec3 = attacking ? target.getEyePosition() : teletor.getWeaponPosition();
            Vec3 want = vec3.subtract(this.position());
            if (target != null && !comingBack) {
                this.entityData.set(IDLING, false);
                if (want.length() < target.getBbWidth() + 1F) {
                    if (strikeProgress < 1F) {
                        strikeProgress = Math.max(0, strikeProgress + 0.35F);
                    } else {
                        hurtEntity(teletor, target);
                        this.comingBack = true;
                    }
                } else if (want.length() > 32) {
                    this.comingBack = true;
                }
            }
            directMovementTowards(vec3, 0.1F);
            if (this.distanceTo(controller) < 2.5F && this.getY() > controller.getY()) {
                this.entityData.set(IDLING, true);
                if (this.comingBack) {
                    this.comingBack = false;
                }
            }
        } else if (controller instanceof Player player) {
            Vec3 moveTo = null;
            this.entityData.set(CONTROLLER_ID, controller.getId());
            this.entityData.set(IDLING, false);
            this.comingBack = !isOwnerWearingGauntlet();
            float speed = 0.1F;
            boolean haste = false;
            if (isOwnerWearingGauntlet()) {
                hadPlayerController = true;
                float maxDist = 30F;
                if(getController() instanceof LivingEntity living && living.getUseItem().is(ACItemRegistry.GALENA_GAUNTLET.get())){
                    ItemStack useItem = living.getUseItem();
                    haste = useItem.getEnchantmentLevel(ACEnchantmentRegistry.FERROUS_HASTE.get()) > 0;
                    int fieldExtension = useItem.getEnchantmentLevel(ACEnchantmentRegistry.FIELD_EXTENSION.get());
                    maxDist += fieldExtension * 5F;
                }
                BlockPos miningBlock = null;
                HitResult hitresult = ProjectileUtil.getHitResultOnViewVector(player, Entity::canBeHitByProjectile, maxDist);
                if (hitresult instanceof EntityHitResult entityHitResult && playerUseCooldown == 0) {
                    Entity entity = entityHitResult.getEntity();
                    moveTo = entity.position().add(0, entity.getBbHeight() * 0.5F, 0);
                    speed = 0.2F;
                    if (this.distanceTo(entity) < entity.getBbWidth() + 1.5F) {
                        if (strikeProgress < 1F) {
                            strikeProgress = Math.max(0, strikeProgress + 0.35F);
                        } else {
                            hurtEntity(player, entity);

                            playerUseCooldown = haste ? 3 : 5 + random.nextInt(5);
                        }
                    }
                } else {
                    moveTo = player.getEyePosition().add(player.getViewVector(1.0F).scale(maxDist - 20F));
                    if (hitresult.getType() == HitResult.Type.BLOCK || hitresult.getLocation().subtract(player.getEyePosition()).length() < maxDist) {
                        if (hitresult instanceof BlockHitResult blockHitResult) {
                            if (this.distanceToSqr(Vec3.atCenterOf(blockHitResult.getBlockPos())) < 2.25F) {
                                miningBlock = blockHitResult.getBlockPos();
                            }
                            if (!level().getBlockState(blockHitResult.getBlockPos()).isAir()) {
                                moveTo = hitresult.getLocation();
                            }
                        }
                    }
                }
                if (miningBlock != null) {
                    if (lastSelectedBlock == null || !lastSelectedBlock.equals(miningBlock)) {
                        if (lastSelectedBlock != null) {
                            this.level().destroyBlockProgress(player.getId(), lastSelectedBlock, -1);
                        }
                        lastSelectedBlock = miningBlock;
                        destroyBlockProgress = 0.0F;
                    }
                    BlockState miningState = level().getBlockState(miningBlock);
                    SoundType soundType = miningState.getSoundType();
                    float f = miningState.getDestroySpeed(level(), miningBlock);
                    float itemDestroySpeed = getDigSpeed(player, miningState, miningBlock);
                    if (itemDestroySpeed > 1.0F) {
                        if (totalMiningTime % 4 == 0) {
                            this.playSound(soundType.getHitSound(), (soundType.getVolume() + 1.0F) / 8.0F, soundType.getPitch() * 0.5F);
                        }
                        totalMiningTime++;
                        strikeProgress = (float) Math.abs(Math.sin(tickCount * 0.6F) * 1.2F - 0.2F);
                        float j = itemDestroySpeed / f / (float) (haste ? 8 : 10);
                        destroyBlockProgress += j;
                        this.level().destroyBlockProgress(player.getId(), lastSelectedBlock, (int) (destroyBlockProgress * 10F));
                        if (destroyBlockProgress >= 1.0F && !level().isClientSide) {
                            damageItem(1);
                            ItemStack itemStack = getItemStack();
                            itemStack.mineBlock(this.level(), miningState, miningBlock, player);
                            int fortuneLevel = itemStack.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);
                            int silkTouchLevel = itemStack.getEnchantmentLevel(Enchantments.SILK_TOUCH);
                            int exp = miningState.getExpDrop(level(), level().random, miningBlock, fortuneLevel, silkTouchLevel);
                            net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, itemStack, InteractionHand.MAIN_HAND);
                            boolean flag = level().destroyBlock(miningBlock, false);
                            miningState.getBlock().playerDestroy(level(), player, miningBlock, miningState, level().getBlockEntity(miningBlock), itemStack);
                            if (flag && exp > 0 && level() instanceof ServerLevel serverLevel) {
                                miningState.getBlock().popExperience(serverLevel, miningBlock, exp);
                            }
                            destroyBlockProgress = 0.0F;
                        }
                    }
                }
            } else {
                if (returnProgress < 1F) {
                    returnProgress = Math.min(1, returnProgress + 0.2F);
                }
                if (lastSelectedBlock != null) {
                    this.level().destroyBlockProgress(player.getId(), lastSelectedBlock, -1);
                    lastSelectedBlock = null;
                }
                moveTo = player.position().add(0, 1, 0);
                if (distanceTo(controller) < 1.4) {
                    if (!this.isRemoved()) {
                        if(!spawnedItem && player.addItem(this.getItemStack())){
                            spawnedItem = true;
                            this.remove(RemovalReason.DISCARDED);
                        }else{
                            plopItem();
                        }
                    }
                }
            }
            if (moveTo != null) {
                directMovementTowards(moveTo, speed);
            }
        }
        if (playerUseCooldown > 0) {
            playerUseCooldown--;
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9F));
    }

    private void plopItem(){
        if(!spawnedItem){
            spawnedItem = true;
            ItemEntity itementity = this.spawnAtLocation(this.getItemStack());
            if (itementity != null) {
                itementity.setNoPickUpDelay();
            }
        }
        this.remove(RemovalReason.DISCARDED);
    }

    public void damageItem(int damageAmount) {
        if (getController() instanceof LivingEntity living && !(living instanceof Player player && player.isCreative())) {
            ItemStack stack = getItemStack();
            if(stack.isDamageableItem()){
                stack.hurtAndBreak(damageAmount, living, (player1) -> {
                    player1.broadcastBreakEvent(player1.getUsedItemHand());
                });
                if(stack.getDamageValue() >= stack.getMaxDamage()){
                    this.remove(RemovalReason.DISCARDED);
                }
            }
        }
    }

    public float getDigSpeed(Player player, BlockState state, @Nullable BlockPos pos) {
        ItemStack stack = getItemStack();
        float f = stack.getDestroySpeed(state);
        if (f > 1.0F) {
            int i = EnchantmentHelper.getBlockEfficiency(player);
            if (i > 0 && !stack.isEmpty()) {
                f += (float) (i * i + 1);
            }
        }

        if (MobEffectUtil.hasDigSpeed(player)) {
            f *= 1.0F + (float) (MobEffectUtil.getDigSpeedAmplification(player) + 1) * 0.2F;
        }

        if (player.hasEffect(MobEffects.DIG_SLOWDOWN)) {
            float f1;
            switch (player.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) {
                case 0:
                    f1 = 0.3F;
                    break;
                case 1:
                    f1 = 0.09F;
                    break;
                case 2:
                    f1 = 0.0027F;
                    break;
                case 3:
                default:
                    f1 = 8.1E-4F;
            }

            f *= f1;
        }

        if (this.isEyeInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(player)) {
            f /= 5.0F;
        }

        if (!this.onGround()) {
            f /= 5.0F;
        }

        f = net.minecraftforge.event.ForgeEventFactory.getBreakSpeed(player, state, f, pos);
        return f;
    }

    private void hurtEntity(LivingEntity holder, Entity target) {
        ItemStack itemStack = this.getItemStack();
        float f = (float)holder.getAttributeValue(Attributes.ATTACK_DAMAGE) + (float) getDamageForItem(itemStack);
        float f1 = (float)holder.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        if (target instanceof LivingEntity) {
            f += EnchantmentHelper.getDamageBonus(itemStack, ((LivingEntity)target).getMobType());
            f1 += (float)EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, holder);
        }
        int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, itemStack);
        if (i > 0) {
            target.setSecondsOnFire(i * 4);
        }
        if(target.hurt(damageSources().mobAttack(holder), f)){
            holder.doEnchantDamageEffects(holder, target);
            damageItem(1);
            if (f1 > 0.0F && target instanceof LivingEntity) {
                ((LivingEntity)target).knockback((double)(f1 * 0.5F), (double)Mth.sin(this.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(this.getYRot() * ((float)Math.PI / 180F))));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
            }
        }
        if(this.isOnFire()){
            target.setSecondsOnFire(5);
        }
        if (holder instanceof Player player && target instanceof LivingEntity living) {
            itemStack.hurtEnemy(living, player);
            living.setLastHurtByPlayer(player);
            if(living.getHealth() <= 0.0F && player.distanceTo(target) >= 19.5F){
                ACAdvancementTriggerRegistry.KILL_MOB_WITH_GALENA_GAUNTLET.triggerForEntity(player);
            }
        }
    }

    private void directMovementTowards(Vec3 moveTo, float speed) {
        Vec3 want = moveTo.subtract(this.position());
        if (want.length() > 1F) {
            want = want.normalize();
        }
        float targetXRot = (float) (-(Mth.atan2(want.y, want.horizontalDistance()) * (double) (180F / (float) Math.PI)));
        float targetYRot = (float) (-Mth.atan2(want.x, want.z) * (double) (180F / (float) Math.PI));
        if (isIdling()) {
            targetXRot = this.getXRot();
            targetYRot = this.getYRot() + 5;
        }
        this.setXRot(Mth.approachDegrees(this.getXRot(), targetXRot, 5F));
        this.setYRot(Mth.approachDegrees(this.getYRot(), targetYRot, 5F));
        this.setDeltaMovement(this.getDeltaMovement().add(want.scale(speed)));

    }

    private boolean isOwnerWearingGauntlet() {
        return getController() instanceof LivingEntity living && living.getUseItem().is(ACItemRegistry.GALENA_GAUNTLET.get()) && living.isAlive() && !returnFlag;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("WeaponStack")) {
            this.setItemStack(ItemStack.of(tag.getCompound("WeaponStack")));
        }
        if (tag.hasUUID("ControllerUUID")) {
            this.setControllerUUID(tag.getUUID("ControllerUUID"));
        }
    }

    public double getDamageForItem(ItemStack itemStack) {
        Multimap<Attribute, AttributeModifier> map = itemStack.getAttributeModifiers(EquipmentSlot.MAINHAND);
        if (!map.isEmpty()) {
            double d = 0;
            for (AttributeModifier mod : map.get(Attributes.ATTACK_DAMAGE)) {
                d += mod.getAmount();
            }
            return d;
        }
        return 0;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (!this.getItemStack().isEmpty()) {
            CompoundTag stackTag = new CompoundTag();
            this.getItemStack().save(stackTag);
            tag.put("WeaponStack", stackTag);
        }
        if (this.getControllerUUID() != null) {
            tag.putUUID("ControllerUUID", this.getControllerUUID());
        }
    }

    public ItemStack getItemStack() {
        return this.entityData.get(ITEMSTACK);
    }

    public void setItemStack(ItemStack item) {
        this.entityData.set(ITEMSTACK, item);
    }

    public boolean isIdling() {
        return this.entityData.get(IDLING);
    }

    @Nullable
    public UUID getControllerUUID() {
        return this.entityData.get(CONTROLLER_UUID).orElse(null);
    }

    public void setControllerUUID(@Nullable UUID uniqueId) {
        this.entityData.set(CONTROLLER_UUID, Optional.ofNullable(uniqueId));
    }

    public Entity getController() {
        if (!level().isClientSide) {
            final UUID id = getControllerUUID();
            return id == null ? null : ((ServerLevel) level()).getEntity(id);
        } else {
            int id = this.entityData.get(CONTROLLER_ID);
            return id == -1 ? null : level().getEntity(id);
        }
    }

    public Entity getTarget() {
        int id = this.entityData.get(TARGET_ID);
        return id == -1 ? null : level().getEntity(id);
    }

    public float getStrikeProgress(float partialTick) {
        return prevStrikeProgress + (strikeProgress - prevStrikeProgress) * partialTick;
    }

    public float getReturnProgress(float partialTick) {
        return prevReturnProgress + (returnProgress - prevReturnProgress) * partialTick;
    }

    public Vec3 getControllerHandPos(Player controller, float partialTicks) {
        float yBodyRot = Mth.lerp(partialTicks, controller.yBodyRotO, controller.yBodyRot);
        boolean mainHand = controller.getItemInHand(InteractionHand.MAIN_HAND).is(ACItemRegistry.GALENA_GAUNTLET.get());
        Vec3 offset = new Vec3(controller.getBbWidth()  * (mainHand ? -0.75F : 0.75F), controller.getBbHeight() * 0.68F, controller.getBbWidth() * -0.1F).yRot((float) Math.toRadians(-yBodyRot));
        Vec3 armViewExtra = controller.getViewVector(partialTicks).normalize().scale(0.75F);
        return controller.getPosition(partialTicks).add(offset).add(armViewExtra);
    }
}
