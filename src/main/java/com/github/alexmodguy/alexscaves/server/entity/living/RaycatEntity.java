package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.AnimalFollowOwnerGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.RaycatSitOnBlockGoal;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexthe666.citadel.server.entity.IComandableMob;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

public class RaycatEntity extends TamableAnimal implements IComandableMob {

    private static final EntityDataAccessor<Integer> ABSORB_TARGET_ID = SynchedEntityData.defineId(RaycatEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(RaycatEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LAY_TIME = SynchedEntityData.defineId(RaycatEntity.class, EntityDataSerializers.INT);
    private float sitProgress;
    private float prevSitProgress;
    private float layProgress;
    private float prevLayProgress;
    private float absorbAmount;
    private float prevAbsorbAmount;

    private int absorbCooldown = 300 + random.nextInt(300);

    public RaycatEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.0D, Ingredient.of(Items.COD), false));
        this.goalSelector.addGoal(3, new AnimalFollowOwnerGoal(this, 1.2D, 5.0F, 2.0F, false) {
            @Override
            public boolean shouldFollow() {
                return RaycatEntity.this.getCommand() == 2;
            }
        });
        this.goalSelector.addGoal(4, new RaycatSitOnBlockGoal(this, 1));
        this.goalSelector.addGoal(5, new LeapAtTargetGoal(this, 0.3F));
        this.goalSelector.addGoal(6, new OcelotAttackGoal(this));
        this.goalSelector.addGoal(7, new BreedGoal(this, 0.8D));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
    }

    public static boolean checkRaycatSpawnRules(EntityType<? extends Animal> type, LevelAccessor levelAccessor, MobSpawnType mobType, BlockPos pos, RandomSource randomSource) {
        return levelAccessor.getBlockState(pos.below()).is(ACBlockRegistry.RADROCK.get()) && levelAccessor.getFluidState(pos).isEmpty() && levelAccessor.getFluidState(pos.below()).isEmpty();
    }

    public float getWalkTargetValue(BlockPos pos, LevelReader levelReader) {
        return levelReader.getBlockState(pos.below()).is(ACBlockRegistry.RADROCK.get()) ? 10.0F : super.getWalkTargetValue(pos, levelReader);
    }

    public int getMaxSpawnClusterSize() {
        return 1;
    }

    public boolean isMaxGroupSizeReached(int i) {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ABSORB_TARGET_ID, -1);
        this.entityData.define(LAY_TIME, 0);
        this.entityData.define(COMMAND, 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.3D).add(Attributes.MAX_HEALTH, 24.0D);
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ACItemRegistry.RADGILL.get());
    }

    public void tick() {
        super.tick();
        prevSitProgress = sitProgress;
        prevLayProgress = layProgress;
        prevAbsorbAmount = absorbAmount;
        if (this.isInSittingPose() && sitProgress < 5F) {
            sitProgress++;
        }
        if (!this.isInSittingPose() && sitProgress > 0) {
            sitProgress--;
        }
        if (this.getLayTime() > 0 && layProgress < 5F) {
            layProgress++;
        }
        if (this.getLayTime() <= 0 && layProgress > 0) {
            layProgress--;
        }
        Entity absorbTarget = getAbsorbTarget();
        if (this.hasEffect(ACEffectRegistry.IRRADIATED.get()) && this.tickCount % 10 == 0) {
            this.heal(1);
        }
        if (absorbCooldown > 0) {
            absorbCooldown--;
        } else {
            LivingEntity owner = getOwner();
            if (absorbTarget == null) {
                Entity closestIrradiated = null;
                if (owner != null && owner.distanceTo(this) < 20 && owner.hasEffect(ACEffectRegistry.IRRADIATED.get())) {
                    closestIrradiated = owner;
                } else {
                    for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(9D))) {
                        if (!(entity instanceof RaycatEntity) && entity.hasEffect(ACEffectRegistry.IRRADIATED.get()) && (closestIrradiated == null || closestIrradiated.distanceTo(this) > entity.distanceTo(this))) {
                            closestIrradiated = entity;
                        }
                    }
                }
                if (closestIrradiated != null) {
                    setAbsorbTargetId(closestIrradiated.getId());
                } else if (!level().isClientSide) {
                    resetAbsorbCooldown();
                }
            } else {
                if (absorbAmount <= 0) {
                    absorbAmount = 1.0F;
                } else {
                    absorbAmount = Math.max(0, absorbAmount - 0.05F);
                    if (absorbAmount <= 0) {
                        int currentRad = this.hasEffect(ACEffectRegistry.IRRADIATED.get()) ? this.getEffect(ACEffectRegistry.IRRADIATED.get()).getAmplifier() + 1 : 0;
                        this.heal(10);
                        this.addEffect(new MobEffectInstance(ACEffectRegistry.IRRADIATED.get(), 200, currentRad));
                        this.lookAt(absorbTarget, 30, 30);
                        if (absorbTarget instanceof LivingEntity living) {
                            MobEffectInstance effectInstance = living.getEffect(ACEffectRegistry.IRRADIATED.get());
                            if (effectInstance != null) {
                                int timeLeft = effectInstance.getDuration();
                                int level = effectInstance.getAmplifier();
                                living.removeEffect(ACEffectRegistry.IRRADIATED.get());
                                if (level > 0) {
                                    living.addEffect(new MobEffectInstance(ACEffectRegistry.IRRADIATED.get(), timeLeft, level - 1));
                                }
                            }
                        }
                        setAbsorbTargetId(-1);
                        if (!level().isClientSide) {
                            resetAbsorbCooldown();
                        }
                    }
                }
            }
        }
    }

    private void resetAbsorbCooldown() {
        absorbCooldown = 300 + random.nextInt(300);
    }

    public Entity getAbsorbTarget() {
        int id = this.entityData.get(ABSORB_TARGET_ID);
        return id == -1 ? null : level().getEntity(id);
    }

    public void setAbsorbTargetId(int id) {
        this.entityData.set(ABSORB_TARGET_ID, id);
    }

    public float getSitProgress(float partialTicks) {
        return (prevSitProgress + (sitProgress - prevSitProgress) * partialTicks) * 0.2F;
    }

    public float getLayProgress(float partialTicks) {
        return (prevLayProgress + (layProgress - prevLayProgress) * partialTicks) * 0.2F;
    }

    public float getAbsorbAmount(float partialTicks) {
        return (prevAbsorbAmount + (absorbAmount - prevAbsorbAmount) * partialTicks);
    }

    public int getCommand() {
        return this.entityData.get(COMMAND);
    }

    public void setCommand(int command) {
        this.entityData.set(COMMAND, command);
    }

    public int getLayTime() {
        return this.entityData.get(LAY_TIME);
    }

    public void setLayTime(int layTime) {
        this.entityData.set(LAY_TIME, layTime);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Command", this.getCommand());
        compound.putInt("LayTime", this.getLayTime());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setCommand(compound.getInt("Command"));
        this.setLayTime(compound.getInt("LayTime"));
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.is(ACItemRegistry.RADGILL.get())) {
            if (!isTame()) {
                this.usePlayerItem(player, hand, itemstack);
                if (getRandom().nextInt(3) == 0) {
                    this.tame(player);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }
                return InteractionResult.SUCCESS;
            }
        }
        InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
        InteractionResult type = super.mobInteract(player, hand);
        if (!interactionresult.consumesAction() && !type.consumesAction() && isTame() && isOwnedBy(player) && !isFood(itemstack) && !player.isShiftKeyDown()) {
            this.setCommand(this.getCommand() + 1);
            if (this.getCommand() == 3) {
                this.setCommand(0);
            }
            player.displayClientMessage(Component.translatable("entity.alexscaves.all.command_" + this.getCommand(), this.getName()), true);
            boolean sit = this.getCommand() == 1;
            if (sit) {
                this.setOrderedToSit(true);
            } else {
                this.setOrderedToSit(false);
            }
            return InteractionResult.SUCCESS;
        }
        return type;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob mob) {
        return ACEntityRegistry.RAYCAT.get().create(serverLevel);
    }
}
