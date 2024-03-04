package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.*;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.animation.LegSolverQuadruped;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class GrottoceratopsEntity extends DinosaurEntity implements IAnimatedEntity {

    private static final EntityDataAccessor<Float> TAIL_SWING_ROT = SynchedEntityData.defineId(GrottoceratopsEntity.class, EntityDataSerializers.FLOAT);
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
    private int resetAttackerCooldown = 0;

    public GrottoceratopsEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.ATTACK_DAMAGE, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.2D).add(Attributes.KNOCKBACK_RESISTANCE, 0.9D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.MAX_HEALTH, 50.0D).add(Attributes.ARMOR, 8.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TAIL_SWING_ROT, 0F);
    }

    protected PathNavigation createNavigation(Level level) {
        return new AdvancedPathNavigateNoTeleport(this, level);
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        if (!this.isBaby()) {
            this.playSound(ACSoundRegistry.GROTTOCERATOPS_STEP.get(), 0.7F, 0.85F);
        }
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new GrottoceratopsMeleeGoal(this));
        this.goalSelector.addGoal(2, new AnimalBreedEggsGoal(this, 1));
        this.goalSelector.addGoal(3, new AnimalLayEggGoal(this, 100, 1));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.1D, Ingredient.of(ACBlockRegistry.TREE_STAR.get()), false));
        this.goalSelector.addGoal(5, new GrottoceratopsEatPlantsGoal(this, 16));
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1.0D, 45));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
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
        if (this.getAnimation() == ANIMATION_MELEE_TAIL_1 || this.getAnimation() == ANIMATION_MELEE_TAIL_2) {
            float start = this.getAnimation() == ANIMATION_MELEE_TAIL_1 ? 30 : -30;
            float end = this.getAnimation() == ANIMATION_MELEE_TAIL_1 ? -180 : 180;
            if (this.getAnimationTick() <= 7) {
                this.setTailSwingRot(Mth.approachDegrees(tailSwing, start, 5));
            } else {
                this.setTailSwingRot(Mth.approachDegrees(tailSwing, end, 25));
            }
            this.walkAnimation.setSpeed(1);
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
        if (this.getAnimation() == ANIMATION_CHEW && this.getAnimationTick() == 2 || this.getAnimation() == ANIMATION_CHEW_FROM_GROUND && this.getAnimationTick() == 10) {
            this.playSound(ACSoundRegistry.GROTTOCERATOPS_GRAZE.get());
        }
        if (this.tickCount % 100 == 0 && this.getHealth() < this.getMaxHealth()) {
            this.heal(2);
        }
        if (resetAttackerCooldown > 0) {
            resetAttackerCooldown--;
        } else if (!level().isClientSide && !this.isBaby() && (this.getLastHurtByMob() == null || !this.getLastHurtByMob().isAlive())) {
            this.setTarget(this.getLastHurtByMob());
            resetAttackerCooldown = 600;
        }
        if (this.getAnimation() == ANIMATION_SPEAK_1 && this.getAnimationTick() == 5 || this.getAnimation() == ANIMATION_SPEAK_2 && this.getAnimationTick() == 2) {
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

    @Override
    public BlockState createEggBlockState() {
        return ACBlockRegistry.GROTTOCERATOPS_EGG.get().defaultBlockState();
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
        if (this.getAnimation() == NO_ANIMATION && !level().isClientSide) {
            this.setAnimation(random.nextBoolean() ? ANIMATION_SPEAK_2 : ANIMATION_SPEAK_1);
        }
    }

    public void actuallyPlayAmbientSound() {
        SoundEvent soundevent = this.getAmbientSound();
        float volume = this.getSoundVolume();
        if (this.getAnimation() == ANIMATION_SPEAK_2) {
            soundevent = ACSoundRegistry.GROTTOCERATOPS_CALL.get();
            volume += 1.0F;
        }
        if (soundevent != null) {
            this.playSound(soundevent, volume, this.getVoicePitch());
        }
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(ACBlockRegistry.TREE_STAR.get().asItem());
    }

    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, flying ? this.getY() - this.yo : 0, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 8.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }


    @Override
    public void setInLove(@javax.annotation.Nullable Player player) {
        super.setInLove(player);
        if (this.getAnimation() == null || this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_CHEW);
        }
    }

    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.GROTTOCERATOPS_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.GROTTOCERATOPS_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.GROTTOCERATOPS_DEATH.get();
    }

    public float getStepHeight() {
        return 1.1F;
    }
}
