package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.PewenBranchBlock;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityDataRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.GroundPathNavigatorNoSpin;
import com.github.alexmodguy.alexscaves.server.entity.ai.GummyBearBackScratchGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.GummyBearMeleeGoal;
import com.github.alexmodguy.alexscaves.server.entity.util.GummyColors;
import com.github.alexmodguy.alexscaves.server.entity.util.HasGummyColors;
import com.github.alexmodguy.alexscaves.server.entity.util.PossessedByLicowitch;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.server.entity.IDancesToJukebox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Attr;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.EnumSet;
import java.util.function.Predicate;

public class GummyBearEntity extends Animal implements IDancesToJukebox, IAnimatedEntity, PossessedByLicowitch, HasGummyColors {

    private static final EntityDataAccessor<GummyColors> GUMMY_COLOR = SynchedEntityData.defineId(GummyBearEntity.class, ACEntityDataRegistry.GUMMY_COLOR.get());
    private static final EntityDataAccessor<Boolean> DANCING = SynchedEntityData.defineId(GummyBearEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(GummyBearEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> STANDING = SynchedEntityData.defineId(GummyBearEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(GummyBearEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DIGESTING = SynchedEntityData.defineId(GummyBearEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> STOMACH_RED = SynchedEntityData.defineId(GummyBearEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> STOMACH_GREEN = SynchedEntityData.defineId(GummyBearEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> STOMACH_BLUE = SynchedEntityData.defineId(GummyBearEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> HELD_MOB_ID = SynchedEntityData.defineId(GummyBearEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> POSSESSOR_LICOWITCH_ID = SynchedEntityData.defineId(GummyBearEntity.class, EntityDataSerializers.INT);

    public static final Animation ANIMATION_FISH = Animation.create(35);
    public static final Animation ANIMATION_EAT = Animation.create(40);
    public static final Animation ANIMATION_BACKSCRATCH = Animation.create(90);
    public static final Animation ANIMATION_MAUL = Animation.create(25);
    public static final Animation ANIMATION_SWIPE = Animation.create(25);
    private Animation currentAnimation;
    private int animationTick;
    private float prevSitProgress;
    private float sitProgress;
    private float prevStandProgress;
    private float standProgress;
    public float prevDanceProgress;
    public float danceProgress;
    public float prevSleepProgress;
    public float sleepProgress;
    public float prevStomachAlpha;
    public float stomachAlpha;
    public BlockPos jukeboxPosition;
    public boolean lookForTheGummyBearAlbumInStoresOnNovember13th = checkNovember13th();
    private ResourceLocation digestingEffect;
    private int standFor = 0;
    private int sitFor = 0;
    private int sleepFor = 0;

    private int jellybeansToMake = 5;

    public GummyBearEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(GUMMY_COLOR, GummyColors.RED);
        this.entityData.define(DANCING, false);
        this.entityData.define(SITTING, false);
        this.entityData.define(STANDING, false);
        this.entityData.define(SLEEPING, false);
        this.entityData.define(DIGESTING, false);
        this.entityData.define(STOMACH_RED, 0.0F);
        this.entityData.define(STOMACH_GREEN, 0.0F);
        this.entityData.define(STOMACH_BLUE, 0.0F);
        this.entityData.define(HELD_MOB_ID, -1);
        this.entityData.define(POSSESSOR_LICOWITCH_ID, -1);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.MAX_HEALTH, 36.0D).add(Attributes.ATTACK_DAMAGE, 4.0F);
    }

    public static boolean checkGummyBearSpawnRules(EntityType<? extends Animal> type, LevelAccessor levelAccessor, MobSpawnType mobType, BlockPos pos, RandomSource randomSource) {
        return levelAccessor.getBlockState(pos.below()).is(ACBlockRegistry.BLOCK_OF_FROSTED_CHOCOLATE.get()) && levelAccessor.getFluidState(pos).isEmpty() && levelAccessor.getFluidState(pos.below()).isEmpty();
    }

    public float getWalkTargetValue(BlockPos pos, LevelReader levelReader) {
        return levelReader.getBlockState(pos.below()).is(ACBlockRegistry.BLOCK_OF_FROSTED_CHOCOLATE.get()) ? 10.0F : super.getWalkTargetValue(pos, levelReader);
    }

    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigatorNoSpin(this, level);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitGoal());
        this.goalSelector.addGoal(2, new GummyBearMeleeGoal(this));
        this.goalSelector.addGoal(3, new PanicGoal(this, 1.0D) {
            @Override
            public boolean shouldPanic() {
                return GummyBearEntity.this.isBaby() && super.shouldPanic();
            }
        });
        this.goalSelector.addGoal(4, new GummyBearBackScratchGoal(this, 20));
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new TemptGoal(this, 1.1D, Ingredient.of(Items.POTION), false));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F) {
            public boolean canUse() {
                return !GummyBearEntity.this.stopRotating() && super.canUse();
            }
        });
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this) {
            public boolean canUse() {
                return !GummyBearEntity.this.stopRotating() && super.canUse();
            }
        });
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, SweetishFishEntity.class, 100, true, false, livingEntity -> livingEntity instanceof SweetishFishEntity sweetishFish && sweetishFish.getGummyColor() == this.getGummyColor()));
        this.targetSelector.addGoal(3, new ProtectBabiesGoal());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Husk.class, true, false));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("GummyColor", this.getGummyColor().ordinal());
        if (digestingEffect != null) {
            compound.putString("DigestingEffect", digestingEffect.toString());
        }
        compound.putBoolean("BearSleeping", this.isBearSleeping());
        compound.putBoolean("BearSitting", this.isSitting());
        compound.putInt("SleepTime", sleepFor);
        compound.putInt("SitTime", sitFor);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setGummyColor(GummyColors.fromOrdinal(compound.getInt("GummyColor")));
        if (compound.contains("DigestingEffect")) {
            digestingEffect = ResourceLocation.parse(compound.getString("DigestingEffect"));
        }
        this.setBearSleeping(compound.getBoolean("BearSleeping"));
        this.setStanding(compound.getBoolean("BearSitting"));
        this.sleepFor = compound.getInt("SleepTime");
        this.sitFor = compound.getInt("SitTime");
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        Item fishItem = ACItemRegistry.SWEETISH_FISH_RED.get();
        switch(this.getGummyColor()){
            case GREEN:
                fishItem = ACItemRegistry.SWEETISH_FISH_GREEN.get();
                break;
            case BLUE:
                fishItem = ACItemRegistry.SWEETISH_FISH_BLUE.get();
                break;
            case YELLOW:
                fishItem = ACItemRegistry.SWEETISH_FISH_YELLOW.get();
                break;
            case PINK:
                fishItem = ACItemRegistry.SWEETISH_FISH_PINK.get();
                break;
        }
        return itemStack.is(fishItem);
    }


    public GummyColors getGummyColor() {
        return this.entityData.get(GUMMY_COLOR);
    }

    public void setGummyColor(GummyColors color) {
        this.entityData.set(GUMMY_COLOR, color);
    }

    public float getStomachRed() {
        return this.entityData.get(STOMACH_RED);
    }

    public void setStomachRed(float stomachRed) {
        this.entityData.set(STOMACH_RED, stomachRed);
    }

    public float getStomachGreen() {
        return this.entityData.get(STOMACH_GREEN);
    }

    public void setStomachGreen(float stomachGreen) {
        this.entityData.set(STOMACH_GREEN, stomachGreen);
    }

    public float getStomachBlue() {
        return this.entityData.get(STOMACH_BLUE);
    }

    public void setStomachBlue(float stomachBlue) {
        this.entityData.set(STOMACH_BLUE, stomachBlue);
    }

    public float getStomachAlpha(float partialTicks) {
        return prevStomachAlpha + (stomachAlpha - prevStomachAlpha) * partialTicks;
    }

    @Override
    public void setPossessedByLicowitchId(int entityId) {
        this.entityData.set(POSSESSOR_LICOWITCH_ID, entityId);
    }

    @Override
    public int getPossessedByLicowitchId() {
        return this.entityData.get(POSSESSOR_LICOWITCH_ID);
    }

    @Override
    protected boolean shouldDropLoot() {
        return super.shouldDropLoot() && getPossessedByLicowitchId() == -1;
    }

    @Override
    public boolean canAttack(LivingEntity living) {
        if(this.getPossessedByLicowitchId() != -1){
            LicowitchEntity licowitch = this.getPossessingLicowitch(this.level());
            if(licowitch != null && licowitch.isFriendlyFire(living)){
                return false;
            }
        }
        return super.canAttack(living);
    }

    public boolean digestEffect(Potion potion) {
        digestingEffect = ForgeRegistries.POTIONS.getKey(potion);
        updateDigestionColors();
        return true;

    }

    private void updateDigestionColors() {
        Potion potion = ForgeRegistries.POTIONS.getValue(digestingEffect);
        if (potion != null) {
            int colorizer = PotionUtils.getColor(potion);
            if (colorizer != -1) {
                float f = (float) (colorizer >> 16 & 255) / 255.0F;
                float f1 = (float) (colorizer >> 8 & 255) / 255.0F;
                float f2 = (float) (colorizer & 255) / 255.0F;
                this.setStomachRed(f);
                this.setStomachGreen(f1);
                this.setStomachBlue(f2);
            }

        }
    }

    public boolean isDigestiblePotion(ItemStack itemStack) {
        if (itemStack.is(Items.POTION)) {
            Potion potion = PotionUtils.getPotion(itemStack);
            return !potion.hasInstantEffects() && !potion.getEffects().isEmpty();
        }
        return false;
    }

    public ItemStack createJellybean() {
        Potion potion = ForgeRegistries.POTIONS.getValue(digestingEffect);
        return potion == null ? new ItemStack(ACItemRegistry.JELLY_BEAN.get()) : ACEffectRegistry.createJellybean(potion);
    }

    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyIn, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnDataIn, @javax.annotation.Nullable CompoundTag dataTag) {
        if (spawnDataIn == null) {
            spawnDataIn = new AgeableMob.AgeableMobGroupData(0.25F);
        }
        this.setGummyColor(GummyColors.getRandom(random, true));
        return super.finalizeSpawn(level, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob ageableMob) {
        GummyBearEntity gummyBear = ACEntityRegistry.GUMMY_BEAR.get().create(level);
        gummyBear.setGummyColor(this.getGummyColor());
        return gummyBear;
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        boolean flag = currentAnimation != animation;
        currentAnimation = animation;
        if (isSittingAnimation() && flag) {
            sitFor += animation.getDuration();
        }
        if (isStandingAnimation() && flag) {
            standFor += animation.getDuration();
        }
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_FISH, ANIMATION_EAT, ANIMATION_BACKSCRATCH, ANIMATION_MAUL, ANIMATION_SWIPE};
    }

    private static boolean checkNovember13th() {
        LocalDate localdate = LocalDate.now();
        int i = localdate.get(ChronoField.DAY_OF_MONTH);
        int j = localdate.get(ChronoField.MONTH_OF_YEAR);
        return j == 11 && i > 12 && i < 15;
    }

    public float getSitProgress(float partialTicks) {
        return (prevSitProgress + (sitProgress - prevSitProgress) * partialTicks) * 0.1F;
    }

    public float getStandProgress(float partialTicks) {
        return (prevStandProgress + (standProgress - prevStandProgress) * partialTicks) * 0.1F;
    }

    public float getDanceProgress(float partialTicks) {
        return (prevDanceProgress + (danceProgress - prevDanceProgress) * partialTicks) * 0.2F;
    }

    public float getSleepProgress(float partialTicks) {
        return (prevSleepProgress + (sleepProgress - prevSleepProgress) * partialTicks) * 0.1F;
    }

    public void tick() {
        super.tick();
        prevDanceProgress = danceProgress;
        prevSitProgress = sitProgress;
        prevStandProgress = standProgress;
        prevSleepProgress = sleepProgress;
        prevStomachAlpha = stomachAlpha;
        if (this.jukeboxPosition == null || !this.jukeboxPosition.closerToCenterThan(this.position(), 15) || !this.level().getBlockState(this.jukeboxPosition).is(Blocks.JUKEBOX)) {
            this.setDancing(false);
            this.jukeboxPosition = null;
        }
        if (isDancing() && danceProgress < 5F) {
            danceProgress++;
        }
        if (!isDancing() && danceProgress > 0F) {
            danceProgress--;
        }
        if (isSitting() && sitProgress < 10F) {
            sitProgress++;
        }
        if (!isSitting() && sitProgress > 0F) {
            sitProgress--;
        }
        if (isStanding() && standProgress < 10F) {
            standProgress++;
        }
        if (!isStanding() && standProgress > 0F) {
            standProgress--;
        }
        if (isBearSleeping() && sleepProgress < 10F) {
            sleepProgress++;
        }
        if (!isBearSleeping() && sleepProgress > 0F) {
            sleepProgress--;
        }
        if (isDigesting()) {
            stomachAlpha = Mth.approach(stomachAlpha, 1.0F, 0.05F);
        } else {
            stomachAlpha = Mth.approach(stomachAlpha, 0.0F, 0.05F);
            if (stomachAlpha <= 0.0F && digestingEffect != null) {
                digestingEffect = null;
            }
        }
        if (level().isClientSide) {
            if (this.isBearSleeping()) {
                int sleepDiv = tickCount % 50;
                if (sleepDiv == 2 || sleepDiv == 10 || sleepDiv == 18) {
                    Vec3 headPos = this.getEyePosition().add(new Vec3(0.2F, -0.4F, 1.2F).yRot(-yBodyRot * ((float) Math.PI / 180F)));
                    this.level().addParticle(ACParticleRegistry.SLEEP.get(), headPos.x, headPos.y, headPos.z, 0, 0.1F, 0);
                }
            }
            spawnPossessedParticles(getRandomX(0.5D), getRandomY(), getRandomZ(0.5D), this.level());
        } else {
            int animationDurationLeft = currentAnimation == null ? 0 : currentAnimation.getDuration() - this.getAnimationTick();
            if (this.isSittingAnimation() && sitFor < animationDurationLeft) {
                sitFor = animationDurationLeft;
            }
            if (this.isStandingAnimation() && standFor < animationDurationLeft) {
                standFor = animationDurationLeft;
            }
            if (sleepFor > 0) {
                this.setStanding(false);
                this.setSitting(false);
                this.setBearSleeping(true);
                sleepFor--;
            } else {
                this.setBearSleeping(false);
                if (sitFor > 0) {
                    sitFor--;
                    this.setSitting(true);
                } else {
                    this.setSitting(false);
                    if (standFor > 0) {
                        standFor--;
                        this.setStanding(true);
                    } else {
                        this.setStanding(false);
                    }
                }
            }
            if (this.getAnimation() == ANIMATION_BACKSCRATCH) {
                if (this.getAnimationTick() % 15 == 0 && this.getAnimationTick() > 0) {
                    if (jellybeansToMake > 0) {
                        spawnAtLocation(createJellybean(), 1.0F + random.nextFloat());
                        jellybeansToMake--;
                    }
                }
                if (jellybeansToMake <= 0 || this.getAnimationTick() > 85) {
                    this.setDigesting(false);
                }
            }
        }
        Entity heldMob = getHeldMob();
        if (heldMob != null && heldMob.isAlive() && heldMob.distanceTo(this) < 10) {
            Vec3 heldPos = this.getEyePosition().add(new Vec3(0.0F, 0.25F, 0.5F).yRot(-yBodyRot * ((float) Math.PI / 180F)));
            Vec3 minus = new Vec3(heldPos.x - heldMob.getX(), heldPos.y - heldMob.getY(), heldPos.z - heldMob.getZ());
            heldMob.setDeltaMovement(minus);
            heldMob.fallDistance = 0.0F;
            heldMob.setYRot(0.0F);
            heldMob.setYBodyRot(0.0F);
            heldMob.setYHeadRot(0.0F);
            heldMob.setXRot(0.0F);
            heldMob.setAirSupply(40);
            this.setAnimation(ANIMATION_EAT);
            if (tickCount % 15 == 0) {
                heldMob.hurt(this.damageSources().mobAttack(this), random.nextBoolean() ? 0.0F : 1.0F);
            }
        } else if (!level().isClientSide) {
            this.setHeldMobId(-1);
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public boolean isDancing() {
        return this.entityData.get(DANCING);
    }

    public void setDancing(boolean bool) {
        this.entityData.set(DANCING, bool);
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING);
    }

    public void setSitting(boolean bool) {
        this.entityData.set(SITTING, bool);
    }

    public boolean isStanding() {
        return this.entityData.get(STANDING);
    }

    public void setStanding(boolean bool) {
        this.entityData.set(STANDING, bool);
    }

    public boolean isBearSleeping() {
        return this.entityData.get(SLEEPING);
    }

    public void setBearSleeping(boolean bool) {
        this.entityData.set(SLEEPING, bool);
    }

    public boolean isDigesting() {
        return this.entityData.get(DIGESTING);
    }

    public void setDigesting(boolean bool) {
        this.entityData.set(DIGESTING, bool);
    }

    public void setHeldMobId(int i) {
        this.entityData.set(HELD_MOB_ID, i);
    }

    public int getHeldMobId() {
        return this.entityData.get(HELD_MOB_ID);
    }

    public Entity getHeldMob() {
        int id = getHeldMobId();
        return id == -1 ? null : level().getEntity(id);
    }

    public boolean isStandingAnimation() {
        return this.getAnimation() == ANIMATION_BACKSCRATCH || this.getAnimation() == ANIMATION_MAUL;
    }

    public boolean isSittingAnimation() {
        return this.getAnimation() == ANIMATION_EAT;
    }

    public void setRecordPlayingNearby(BlockPos pos, boolean playing) {
        this.onClientPlayMusicDisc(this.getId(), pos, playing);
    }

    @Override
    public void setJukeboxPos(BlockPos blockPos) {
        this.jukeboxPosition = blockPos;
    }

    public boolean isMovementBlocked() {
        return this.isSitting() || this.isBearSleeping() || this.getAnimation() == ANIMATION_BACKSCRATCH;
    }

    @Override
    public boolean isPushable() {
        return this.getHeldMobId() == -1;
    }

    @Override
    public void travel(Vec3 vec3d) {
        if (this.isDancing() || this.isSitting() || this.isMovementBlocked()) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    private boolean stopRotating() {
        return this.isBearSleeping() || this.getAnimation() == ANIMATION_BACKSCRATCH;
    }

    public BlockPos getStandAtTreePos(BlockPos target) {
        Vec3 vec3 = Vec3.atCenterOf(target).subtract(this.position());
        float f = -((float) Mth.atan2(vec3.x, vec3.z)) * 180.0F / (float) Math.PI;
        BlockState state = level().getBlockState(target);
        Direction dir = Direction.fromYRot(f);
        if (state.is(ACBlockRegistry.PEWEN_BRANCH.get())) {
            dir = Direction.fromYRot(state.getValue(PewenBranchBlock.ROTATION) * 45);
        }
        if (level().getBlockState(target.below()).isAir()) {
            target = target.relative(dir);
        }
        return target.relative(dir.getOpposite(), 1).atY((int) this.getY());
    }

    public boolean lockTreePosition(BlockPos target) {
        Vec3 vec3 = Vec3.atCenterOf(target).subtract(this.position());
        float f = -((float) Mth.atan2(vec3.x, vec3.z)) * 180.0F / (float) Math.PI;
        BlockState state = level().getBlockState(target);
        Direction dir = Direction.fromYRot(f);
        if (state.is(ACBlockRegistry.PEWEN_BRANCH.get())) {
            dir = Direction.fromYRot(state.getValue(PewenBranchBlock.ROTATION) * 45);
        }
        float targetRot = Mth.approachDegrees(this.getYRot(), 180 + dir.toYRot(), 20);
        this.setYRot(targetRot);
        this.setYHeadRot(targetRot);
        this.yBodyRot = targetRot;
        if (level().getBlockState(target.below()).isAir()) {
            target = target.relative(dir);
        }
        Vec3 vec31 = Vec3.atBottomCenterOf(target);
        Vec3 vec32 = vec31.subtract(this.position());
        if (vec32.length() > 1.0F) {
            vec32 = vec32.normalize();
        }
        Vec3 delta = new Vec3(vec32.x * 0.25F, 0F, vec32.z * 0.25F);
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9F).add(delta));
        return this.distanceToSqr(vec31.x, this.getY(), vec31.z) < 4.0D && Mth.degreesDifferenceAbs(this.getYRot(), 180 + dir.toYRot()) < 7;
    }

    public boolean isSleepy() {
        return this.sleepFor > 0;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (isDigestiblePotion(itemstack) && !this.isDigesting()) {
            this.setDigesting(true);
            this.digestEffect(PotionUtils.getPotion(itemstack));
            this.usePlayerItem(player, hand, itemstack);
            if (!player.getAbilities().instabuild) {
                player.addItem(new ItemStack(Items.GLASS_BOTTLE));
            }
            this.sleepFor = 24000 * (2 + random.nextInt(2));
            this.jellybeansToMake = random.nextInt(2) + 3;
            this.playSound(ACSoundRegistry.GUMMY_BEAR_EAT.get(), this.getSoundVolume(), this.getVoicePitch());
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != MobEffects.HUNGER;
    }

    public int getAmbientSoundInterval() {
        return this.isBearSleeping() ? 45 : 80;
    }

    protected SoundEvent getAmbientSound() {
        return this.isBearSleeping() ? ACSoundRegistry.GUMMY_BEAR_SNORE.get() : ACSoundRegistry.GUMMY_BEAR_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.GUMMY_BEAR_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.GUMMY_BEAR_DEATH.get();
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        if (!this.isBaby()) {
            this.playSound(ACSoundRegistry.GUMMY_BEAR_STEP.get(), 0.3F, this.getVoicePitch());
        }
    }

    private class SitGoal extends Goal {

        public SitGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        public boolean canContinueToUse() {
            return GummyBearEntity.this.isMovementBlocked();
        }

        public boolean canUse() {
            if (GummyBearEntity.this.isInWaterOrBubble()) {
                return false;
            } else {
                return GummyBearEntity.this.isMovementBlocked();
            }
        }

        public void start() {
            GummyBearEntity.this.getNavigation().stop();
        }
    }

    private class ProtectBabiesGoal extends NearestAttackableTargetGoal<Player> {

        public ProtectBabiesGoal() {
            super(GummyBearEntity.this, Player.class, 20, true, true, (Predicate<LivingEntity>)null);
        }

        public boolean canUse() {
            if (GummyBearEntity.this.isBaby()) {
                return false;
            } else {
                if (super.canUse()) {
                    for(GummyBearEntity bear : GummyBearEntity.this.level().getEntitiesOfClass(GummyBearEntity.class, GummyBearEntity.this.getBoundingBox().inflate(8.0D, 4.0D, 8.0D))) {
                        if (bear.isBaby()) {
                            return true;
                        }
                    }
                }

                return false;
            }
        }

        protected double getFollowDistance() {
            return super.getFollowDistance() * 0.5D;
        }
    }

}
