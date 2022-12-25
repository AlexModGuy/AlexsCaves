package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.GrottoceratopsEatPlantsGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.GrottoceratopsMeleeGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.GroundPathNavigatorNoSpin;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.animation.LegSolverQuadruped;
import com.github.alexthe666.citadel.server.entity.IDancesToJukebox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class GrottoceratopsEntity extends Animal implements IAnimatedEntity, IDancesToJukebox {

    private static final EntityDataAccessor<Float> TAIL_SWING_ROT = SynchedEntityData.defineId(GrottoceratopsEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DANCING = SynchedEntityData.defineId(GrottoceratopsEntity.class, EntityDataSerializers.BOOLEAN);
    public LegSolverQuadruped legSolver = new LegSolverQuadruped(0.0F, 1.1F, 1.15F, 1.15F, 1);
    private Animation currentAnimation;
    private int animationTick;
    public static final Animation ANIMATION_SPEAK_1 = Animation.create(15);
    public static final Animation ANIMATION_SPEAK_2 = Animation.create(20);
    public static final Animation ANIMATION_CHEW_FROM_GROUND = Animation.create(60);
    public static final Animation ANIMATION_CHEW = Animation.create(40);
    public static final Animation ANIMATION_MELEE_RAM = Animation.create(20);
    public static final Animation ANIMATION_MELEE_TAIL_1 = Animation.create(20);
    public static final Animation ANIMATION_MELEE_TAIL_2 = Animation.create(20);
    private float prevTailSwingRot;
    public float prevDanceProgress;
    public float danceProgress;
    private BlockPos jukeboxPosition;
    private int resetAttackerCooldown = 0;

    public GrottoceratopsEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        maxUpStep = 1.1F;
    }

    public static boolean checkPrehistoricSpawnRules(EntityType<? extends Animal> type, LevelAccessor levelAccessor, MobSpawnType mobType, BlockPos pos, RandomSource randomSource) {
        return levelAccessor.getBlockState(pos.below()).is(ACTagRegistry.DINOSAURS_SPAWNABLE_ON) && levelAccessor.getFluidState(pos).isEmpty() && levelAccessor.getFluidState(pos.below()).isEmpty();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.ATTACK_DAMAGE, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.2D).add(Attributes.KNOCKBACK_RESISTANCE, 0.9D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.MAX_HEALTH, 50.0D).add(Attributes.ARMOR, 8.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TAIL_SWING_ROT, 0F);
        this.entityData.define(DANCING, false);
    }

    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigatorNoSpin(this, level);
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.COW_STEP, 0.7F, 0.85F);
    }

    public void travel(Vec3 vec3d) {
        if (this.isDancing()) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new GrottoceratopsMeleeGoal(this));
        this.goalSelector.addGoal(2, new GrottoceratopsEatPlantsGoal(this, 16));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0D, 45));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, GrottoceratopsEntity.class)).setAlertOthers());
    }

    public boolean hurt(DamageSource damageSource, float f) {
        if (damageSource.getDirectEntity() instanceof VallumraptorEntity) {
            f *= 0.75F;
        }
        return super.hurt(damageSource, f);
    }

    public void tick() {
        super.tick();
        float tailSwing = getTailSwingRot();
        this.prevTailSwingRot = tailSwing;
        prevDanceProgress = danceProgress;
        if (this.jukeboxPosition == null || !this.jukeboxPosition.closerToCenterThan(this.position(), 15) || !this.level.getBlockState(this.jukeboxPosition).is(Blocks.JUKEBOX)) {
            this.setDancing(false);
            this.jukeboxPosition = null;
        }
        if (isDancing() && danceProgress < 5F) {
            danceProgress++;
        }
        if (!isDancing() && danceProgress > 0F) {
            danceProgress--;
        }
        if (this.getAnimation() == ANIMATION_MELEE_TAIL_1 || this.getAnimation() == ANIMATION_MELEE_TAIL_2) {
            float start = this.getAnimation() == ANIMATION_MELEE_TAIL_1 ? 30 : -30;
            float end = this.getAnimation() == ANIMATION_MELEE_TAIL_1 ? -180 : 180;
            if (this.getAnimationTick() <= 7) {
                this.setTailSwingRot(Mth.approachDegrees(tailSwing, start, 5));
            } else {
                this.setTailSwingRot(Mth.approachDegrees(tailSwing, end, 25));
            }
            this.animationSpeed = 1;
        } else {
            if (Math.abs(tailSwing) > 0.0F) {
                this.setTailSwingRot(Mth.approachDegrees(tailSwing, 0, 20));
            }
            this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, yBodyRot, getHeadRotSpeed());
        }
        if (this.getAnimation() == ANIMATION_CHEW || this.getAnimation() == ANIMATION_CHEW_FROM_GROUND) {
            if (this.getAnimationTick() > this.getAnimation().getDuration() - 1) {
                this.heal(5);
            }
        }
        if (this.tickCount % 100 == 0 && this.getHealth() < this.getMaxHealth()) {
            this.heal(2);
        }
        if (resetAttackerCooldown > 0) {
            resetAttackerCooldown--;
        } else if (!level.isClientSide && !this.isBaby() && (this.getLastHurtByMob() == null || !this.getLastHurtByMob().isAlive())) {
            this.setTarget(this.getLastHurtByMob());
            resetAttackerCooldown = 30;
        }
        if (this.getAnimation() == ANIMATION_SPEAK_1 && this.getAnimationTick() == 5 || this.getAnimation() == ANIMATION_SPEAK_2 && this.getAnimationTick() == 8) {
            actuallyPlayAmbientSound();
        }
        this.legSolver.update(this, this.yBodyRot + getTailSwingRot(), this.getScale());
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    private float getTailSwingRot() {
        return entityData.get(TAIL_SWING_ROT);
    }

    public float getTailSwingRot(float f) {
        return prevTailSwingRot + (getTailSwingRot() - prevTailSwingRot) * f;
    }

    public void setTailSwingRot(float rot) {
        entityData.set(TAIL_SWING_ROT, rot);
    }

    public boolean isDancing() {
        return this.entityData.get(DANCING);
    }

    public void setDancing(boolean bool) {
        this.entityData.set(DANCING, bool);
    }

    public void setRecordPlayingNearby(BlockPos pos, boolean playing) {
        this.onClientPlayMusicDisc(this.getId(), pos, playing);
    }

    @Override
    public void setJukeboxPos(BlockPos blockPos) {
        this.jukeboxPosition = blockPos;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        return ACEntityRegistry.GROTTOCERATOPS.get().create(level);
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
        return new Animation[]{ANIMATION_SPEAK_1, ANIMATION_SPEAK_2, ANIMATION_CHEW_FROM_GROUND, ANIMATION_CHEW, ANIMATION_MELEE_RAM, ANIMATION_MELEE_TAIL_1, ANIMATION_MELEE_TAIL_2};
    }

    public void playAmbientSound() {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(random.nextBoolean() ? ANIMATION_SPEAK_2 : ANIMATION_SPEAK_1);
        }
    }

    public float getDanceProgress(float partialTicks) {
        return (prevDanceProgress + (danceProgress - prevDanceProgress) * partialTicks) * 0.2F;
    }
    public void actuallyPlayAmbientSound() {
        SoundEvent soundevent = this.getAmbientSound();
        if (soundevent != null) {
            this.playSound(soundevent, this.getSoundVolume(), this.getVoicePitch());
        }
    }

    public void calculateEntityAnimation(LivingEntity living, boolean flying) {
        living.animationSpeedOld = living.animationSpeed;
        double d0 = living.getX() - living.xo;
        double d1 = flying ? living.getY() - living.yo : 0.0D;
        double d2 = living.getZ() - living.zo;
        float f = (float) Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 8.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        living.animationSpeed += (f - living.animationSpeed) * 0.4F;
        living.animationPosition += living.animationSpeed;
    }

}
