package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.ai.*;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DeepOneEntity extends DeepOneBaseEntity {

    public static final Animation ANIMATION_THROW = Animation.create(20);
    public static final Animation ANIMATION_BITE = Animation.create(8);
    public static final Animation ANIMATION_SCRATCH = Animation.create(22);
    public static final Animation ANIMATION_TRADE = Animation.create(55);

    private static final EntityDimensions SWIMMING_SIZE = new EntityDimensions(0.99F, 0.99F, false);
    public static final ResourceLocation BARTER_LOOT = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "gameplay/deep_one_barter");

    public DeepOneEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new DeepOneAttackGoal(this));
        this.goalSelector.addGoal(1, new DeepOneBarterGoal(this));
        this.goalSelector.addGoal(2, new DeepOneReactToPlayerGoal(this));
        this.goalSelector.addGoal(3, new DeepOneDisappearGoal(this));
        this.goalSelector.addGoal(4, new DeepOneWanderGoal(this, 12, 1D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByHostileTargetGoal());
        this.targetSelector.addGoal(2, new DeepOneTargetHostilePlayersGoal(this));
    }

    public EntityDimensions getSwimmingSize() {
        return SWIMMING_SIZE;
    }

    public void tick() {
        super.tick();
    }

    @Override
    protected ResourceLocation getBarterLootTable() {
        return BARTER_LOOT;
    }

    @Override
    public void startAttackBehavior(LivingEntity target) {
        double dist = this.distanceTo(target);
        float f = this.getBbWidth() + target.getBbWidth();
        if (dist < f + 1.0D) {
            if (this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                setAnimation(this.getRandom().nextBoolean() ? ANIMATION_SCRATCH : ANIMATION_BITE);
                this.playSound(ACSoundRegistry.DEEP_ONE_ATTACK.get());
            }
        }
        if (dist > f + 4) {
            this.getNavigation().moveTo(target, 1.3D);
        }
        if (this.getAnimation() == ANIMATION_SCRATCH) {
            if (this.getAnimationTick() > 5 && this.getAnimationTick() < 9 || this.getAnimationTick() > 12 && this.getAnimationTick() < 16) {
                checkAndDealMeleeDamage(target, 1.0F);
            }
        }
        if (this.getAnimation() == ANIMATION_BITE) {
            if (this.getAnimationTick() > 3 && this.getAnimationTick() <= 7) {
                checkAndDealMeleeDamage(target, 1.0F);
            }
        }
    }

    @Override
    public Animation getTradingAnimation() {
        return ANIMATION_TRADE;
    }

    @Override
    public SoundEvent getAdmireSound() {
        return ACSoundRegistry.DEEP_ONE_ADMIRE.get();
    }

    protected SoundEvent getAmbientSound() {
        return soundsAngry() ? ACSoundRegistry.DEEP_ONE_HOSTILE.get() : ACSoundRegistry.DEEP_ONE_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.DEEP_ONE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.DEEP_ONE_DEATH.get();
    }

    @Override
    public boolean startDisappearBehavior(Player player) {
        this.getLookControl().setLookAt(player.getX(), player.getEyeY(), player.getZ(), 20.0F, (float) this.getMaxHeadXRot());
        if (this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ACItemRegistry.INK_BOMB.get()));
        }
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_THROW);
        } else if (this.getAnimation() == ANIMATION_THROW) {
            if (this.getAnimationTick() > 10) {
                if (this.getItemInHand(InteractionHand.MAIN_HAND).is(ACItemRegistry.INK_BOMB.get())) {
                    this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                }
                return super.startDisappearBehavior(player);
            }
        }
        return false;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_THROW, ANIMATION_BITE, ANIMATION_SCRATCH, ANIMATION_TRADE};
    }
}
