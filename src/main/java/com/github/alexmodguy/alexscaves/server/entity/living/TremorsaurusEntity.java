package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.TremorsaurusMeleeGoal;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TremorsaurusEntity extends Animal implements IAnimatedEntity {

    private static final EntityDataAccessor<Boolean> RUNNING = SynchedEntityData.defineId(TremorsaurusEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> HELD_MOB_ID = SynchedEntityData.defineId(TremorsaurusEntity.class, EntityDataSerializers.INT);

    public LegSolver legSolver = new LegSolver(new LegSolver.Leg(-0.45F, 0.75F, 1.0F, false), new LegSolver.Leg(-0.45F, -0.75F, 1.0F, false));
    private Animation currentAnimation;
    private int animationTick;
    private float prevScreenShakeAmount;
    private float screenShakeAmount;
    private int lastScareTimestamp = 0;
    private boolean hasRunningAttributes = false;
    private int roarCooldown = 0;
    public static final Animation ANIMATION_SNIFF = Animation.create(30);
    public static final Animation ANIMATION_SPEAK = Animation.create(15);
    public static final Animation ANIMATION_ROAR = Animation.create(55);
    public static final Animation ANIMATION_BITE = Animation.create(15);
    public static final Animation ANIMATION_SHAKE_PREY = Animation.create(40);

    public TremorsaurusEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        maxUpStep = 1.1F;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TremorsaurusMeleeGoal(this));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0D, 30));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, TremorsaurusEntity.class)));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, GrottoceratopsEntity.class, true, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, SubterranodonEntity.class, true, false));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Husk.class, true, false));
    }

    public static boolean checkPrehistoricSpawnRules(EntityType<? extends Animal> type, LevelAccessor levelAccessor, MobSpawnType mobType, BlockPos pos, RandomSource randomSource) {
        return levelAccessor.getBlockState(pos.below()).is(ACTagRegistry.DINOSAURS_SPAWNABLE_ON) && levelAccessor.getFluidState(pos).isEmpty() && levelAccessor.getFluidState(pos.below()).isEmpty();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RUNNING, false);
        this.entityData.define(HELD_MOB_ID, -1);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.ATTACK_DAMAGE, 14.0D).add(Attributes.MOVEMENT_SPEED, 0.2D).add(Attributes.KNOCKBACK_RESISTANCE, 0.9D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.MAX_HEALTH, 150.0D).add(Attributes.ARMOR, 8.0D);
    }

    public void tick() {
        super.tick();
        prevScreenShakeAmount = screenShakeAmount;
        this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, yBodyRot, getHeadRotSpeed());
        this.legSolver.update(this, this.yBodyRot, this.getScale());
        // this.setAnimation(ANIMATION_SHAKE_PREY);
        AnimationHandler.INSTANCE.updateAnimations(this);
        if (screenShakeAmount > 0) {
            screenShakeAmount = Math.max(0, screenShakeAmount - 0.34F);
        }
        if (this.isOnGround() && !this.isInFluidType() && animationSpeed > 0.1F) {
            float f = (float) Math.cos(animationPosition * 0.8F - 1.5F);
            if (Math.abs(f) < 0.2) {
                if (screenShakeAmount <= 0.3) {
                    this.playSound(SoundEvents.PACKED_MUD_STEP, 8, 0.5F);
                    this.shakeWater();
                }
                screenShakeAmount = 1F;
            }
        }
        if (isRunning() && !hasRunningAttributes) {
            hasRunningAttributes = true;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35D);
        }
        if (!isRunning() && hasRunningAttributes) {
            hasRunningAttributes = false;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2D);
        }
        if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() >= 5 && this.getAnimationTick() <= 40) {
            screenShakeAmount = 1F;
            if (this.getAnimationTick() % 5 == 0 && level.isClientSide) {
                this.shakeWater();
            }
            scareMobs();
        }
        if (this.getAnimation() == ANIMATION_SPEAK && this.getAnimationTick() == 5) {
            actuallyPlayAmbientSound();
        }
        if (!level.isClientSide) {
            if (this.getDeltaMovement().horizontalDistance() < 0.05 && this.getAnimation() == NO_ANIMATION) {
                if (random.nextInt(180) == 0) {
                    this.setAnimation(ANIMATION_SNIFF);
                }
                if (random.nextInt(600) == 0) {
                    this.tryRoar();
                }
            }
            boolean held = false;
            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive() && target.distanceTo(this) < 5.5F) {
                if (this.getAnimation() == ANIMATION_SHAKE_PREY && this.getAnimationTick() <= 35) {
                    Vec3 shakePreyPos = getShakePreyPos();
                    Vec3 minus = new Vec3(shakePreyPos.x - this.getTarget().getX(), shakePreyPos.y - this.getTarget().getY(), shakePreyPos.z - this.getTarget().getZ());
                    target.setDeltaMovement(minus);
                    if (this.getAnimationTick() % 10 == 0) {
                        target.hurt(DamageSource.mobAttack(this), 5 + this.getRandom().nextInt(2));
                    }
                    held = true;
                    this.setHeldMobId(target.getId());
                }
            }
            if (!held && getHeldMobId() != -1) {
                this.setHeldMobId(-1);
            }
        }
        if (roarCooldown > 0) {
            roarCooldown--;
        }
    }

    private void scareMobs() {
        if (this.tickCount - lastScareTimestamp > 3) {
            lastScareTimestamp = this.tickCount;
        }
        List<PathfinderMob> list = this.level.getEntitiesOfClass(PathfinderMob.class, this.getBoundingBox().inflate(30, 10, 30));
        for (PathfinderMob e : list) {
            e.setTarget(null);
            e.setLastHurtByMob(null);
            if (!e.getType().is(ACTagRegistry.RESISTS_TREMORSAURUS_ROAR)) {
                if (e.isOnGround()) {
                    Vec3 randomShake = new Vec3(random.nextFloat() - 0.5F, 0, random.nextFloat() - 0.5F).scale(0.6F);
                    e.setDeltaMovement(e.getDeltaMovement().multiply(0.7F, 1, 0.7F).add(randomShake));
                    if (lastScareTimestamp == this.tickCount) {
                        Vec3 vec = LandRandomPos.getPosAway(e, 8, 7, this.position());
                        if (vec != null && e.getMoveControl().getSpeedModifier() != 1.5D) {
                            e.getNavigation().moveTo(vec.x, vec.y, vec.z, 1.5D);
                        }
                    }
                }
            }

        }
    }

    private void shakeWater() {
        if (level.isClientSide) {
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            int radius = 8;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + z * z <= radius * radius) {
                        mutableBlockPos.set(this.getX() + x, this.getY() + 5, this.getZ() + z);
                        while (mutableBlockPos.getY() > level.getMinBuildHeight() && level.getBlockState(mutableBlockPos).isAir()) {
                            mutableBlockPos.move(Direction.DOWN);
                        }
                        float water = getWaterLevelForBlock(level, mutableBlockPos);
                        if (water > 0.0F) {
                            level.addParticle(ACParticleRegistry.WATER_TREMOR.get(), mutableBlockPos.getX() + 0.5F, mutableBlockPos.getY() + water + 0.01, mutableBlockPos.getZ() + 0.5F, 0, 0, 0);
                        }

                    }
                }
            }
        }
    }

    public boolean isRunning() {
        return this.entityData.get(RUNNING);
    }

    public void setRunning(boolean bool) {
        this.entityData.set(RUNNING, bool);
    }

    public void setHeldMobId(int i) {
        this.entityData.set(HELD_MOB_ID, i);
    }


    public int getHeldMobId() {
        return this.entityData.get(HELD_MOB_ID);
    }

    public Entity getHeldMob() {
        int id = getHeldMobId();
        return id == -1 ? null : level.getEntity(id);
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    public float getScreenShakeAmount(float partialTicks) {
        return prevScreenShakeAmount + (screenShakeAmount - prevScreenShakeAmount) * partialTicks;
    }

    public Vec3 getShakePreyPos() {
        Vec3 jaw = new Vec3(0, -0.75, 3F);
        if (this.getAnimation() == ANIMATION_SHAKE_PREY) {
            if (this.getAnimationTick() <= 5) {
                jaw = jaw.subtract(0, 1.5F * (getAnimationTick() / 5F), 0);
            } else if (this.getAnimationTick() < 35) {
                jaw = jaw.yRot(0.8F * (float) Math.cos(this.tickCount * 0.6F));
            }
        }
        Vec3 head = jaw.xRot(-this.getXRot() * ((float) Math.PI / 180F)).yRot(-this.getYHeadRot() * ((float) Math.PI / 180F));
        return this.getEyePosition().add(head);
    }

    public void tryRoar() {
        if (roarCooldown == 0 && this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_ROAR);
            this.roarCooldown = 200 + random.nextInt(200);
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        return null;
    }


    public void travel(Vec3 vec3d) {
        if (this.getAnimation() == ANIMATION_ROAR || this.getAnimation() == ANIMATION_SHAKE_PREY) {
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    public void calculateEntityAnimation(LivingEntity living, boolean flying) {
        living.animationSpeedOld = living.animationSpeed;
        double d0 = living.getX() - living.xo;
        double d2 = living.getZ() - living.zo;
        float f = (float) Math.sqrt(d0 * d0 + d2 * d2) * (isRunning() ? 2.0F : 4.0F);
        if (f > 1.0F) {
            f = 1.0F;
        }

        living.animationSpeed += (f - living.animationSpeed) * 0.4F;
        living.animationPosition += living.animationSpeed;
    }

    public void playAmbientSound() {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_SPEAK);
        }
    }

    public void actuallyPlayAmbientSound() {
        SoundEvent soundevent = this.getAmbientSound();
        if (soundevent != null) {
            this.playSound(soundevent, this.getSoundVolume(), this.getVoicePitch());
        }
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
        currentAnimation = animation;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_SNIFF, ANIMATION_SPEAK, ANIMATION_ROAR, ANIMATION_BITE, ANIMATION_SHAKE_PREY};
    }


    private float getWaterLevelForBlock(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.is(Blocks.WATER_CAULDRON)) {
            return (6.0F + (float) state.getValue(LayeredCauldronBlock.LEVEL).intValue() * 3.0F) / 16.0F;
        } else if (random.nextFloat() < 0.33F && state.getFluidState().is(FluidTags.WATER)) {
            return state.getFluidState().getHeight(level, pos);
        } else {
            return 0;
        }
    }

}
