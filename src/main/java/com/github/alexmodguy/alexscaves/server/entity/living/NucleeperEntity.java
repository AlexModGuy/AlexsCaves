package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.GroundPathNavigatorNoSpin;
import com.github.alexmodguy.alexscaves.server.entity.item.NuclearExplosionEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public class NucleeperEntity extends Monster {

    private float closeProgress;
    private float prevCloseProgress;
    private float explodeProgress;
    private float prevExplodeProgress;
    private float sirenAngle;
    private float prevSirenAngle;

    private static final EntityDataAccessor<Boolean> TRIGGERED = SynchedEntityData.defineId(NucleeperEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> CLOSE_TIME = SynchedEntityData.defineId(NucleeperEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> EXPLODING = SynchedEntityData.defineId(NucleeperEntity.class, EntityDataSerializers.BOOLEAN);
    public static int CLOSE_MAX_TIME = AlexsCaves.COMMON_CONFIG.nucleeperFuseTime.get();


    public NucleeperEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.maxUpStep = 1.1F;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeGoal());
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D, 45));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Husk.class, true, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.2D).add(Attributes.MAX_HEALTH, 30.0D);
    }

    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigatorNoSpin(this, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TRIGGERED, false);
        this.entityData.define(CLOSE_TIME, 0);
        this.entityData.define(EXPLODING, false);
    }

    public int getCloseTime() {
        return this.entityData.get(CLOSE_TIME);
    }

    public void setCloseTime(int time) {
        this.entityData.set(CLOSE_TIME, time);
    }

    public boolean isTriggered() {
        return this.entityData.get(TRIGGERED);
    }

    public void setTriggered(boolean triggered) {
        this.entityData.set(TRIGGERED, triggered);
    }

    public boolean isExploding() {
        return this.entityData.get(EXPLODING);
    }

    public void setExploding(boolean explode) {
        this.entityData.set(EXPLODING, explode);
    }

    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.is(ItemTags.CREEPER_IGNITERS)) {
            SoundEvent soundevent = itemstack.is(Items.FIRE_CHARGE) ? SoundEvents.FIRECHARGE_USE : SoundEvents.FLINTANDSTEEL_USE;
            this.level.playSound(player, this.getX(), this.getY(), this.getZ(), soundevent, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
            if (!this.level.isClientSide) {
                this.setTriggered(true);
                itemstack.hurtAndBreak(1, player, (p_32290_) -> {
                    p_32290_.broadcastBreakEvent(hand);
                });
            }

            return InteractionResult.sidedSuccess(this.level.isClientSide);
        } else {
            return super.mobInteract(player, hand);
        }
    }

    @Override
    public void tick() {
        super.tick();
        prevCloseProgress = closeProgress;
        prevExplodeProgress = explodeProgress;
        prevSirenAngle = sirenAngle;
        int time = this.getCloseTime();
        if(this.isExploding() && explodeProgress < 5F){
            explodeProgress++;
        }
        if(!this.isExploding() && explodeProgress > 0F){
            explodeProgress--;
        }
        if(this.isTriggered()){
            if(time < CLOSE_MAX_TIME){
                this.setCloseTime(time + 1);
            }else if(this.isAlive()){
                this.setExploding(true);
            }
        }
        sirenAngle += (10F + 30F * closeProgress) % 360F;
        closeProgress = (float)time / CLOSE_MAX_TIME;

        if(this.isExploding() && explodeProgress >= 5F){
            this.discard();
            if (!this.level.isClientSide) {
                this.explode();
            }
        }
    }

    private void explode() {
        NuclearExplosionEntity explosion = ACEntityRegistry.NUCLEAR_EXPLOSION.get().create(level);
        explosion.copyPosition(this);
        explosion.setSize(1F);
        level.addFreshEntity(explosion);
    }

    public float getCloseProgress(float partialTick) {
        return (prevCloseProgress + (closeProgress - prevCloseProgress) * partialTick) ;
    }

    public float getSirenAngle(float partialTick) {
        return (prevSirenAngle + (sirenAngle - prevSirenAngle) * partialTick) ;
    }
    public float getExplodeProgress(float partialTick) {
        return (prevExplodeProgress + (explodeProgress - prevExplodeProgress) * partialTick) * 0.2F;
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
    private class MeleeGoal extends Goal {

        public MeleeGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = NucleeperEntity.this.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void tick() {
            LivingEntity target = NucleeperEntity.this.getTarget();
            if(target != null && target.isAlive()){
                NucleeperEntity.this.getNavigation().moveTo(target, 1.0D);
                if(NucleeperEntity.this.distanceTo(target) < 3.5F + target.getBbWidth()){
                    NucleeperEntity.this.setTriggered(true);
                }
            }
        }
    }

}
