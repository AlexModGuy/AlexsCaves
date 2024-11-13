package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.entity.ai.GroundPathNavigatorNoSpin;
import com.github.alexmodguy.alexscaves.server.entity.item.GumballEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.PossessedByLicowitch;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GumbeeperEntity extends Monster implements PowerableMob, PossessedByLicowitch {

    private float explodeProgress;
    private float prevExplodeProgress;
    private float prevDialRot;
    private float dialRot;
    private float shootProgress;
    private float prevShootProgress;
    private static final int DEFAULT_GUMBALLS = 6;
    private static final float MAX_DIAL_ROT = 450;
    private int catScareTime = 0;
    private int postShootTime = 0;
    private boolean hasExploded;
    private static final EntityDataAccessor<Boolean> EXPLODING = SynchedEntityData.defineId(GumbeeperEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHOOTING = SynchedEntityData.defineId(GumbeeperEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> ATTACK_CHARGE = SynchedEntityData.defineId(GumbeeperEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> GUMBALLS_LEFT = SynchedEntityData.defineId(GumbeeperEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> CHARGED = SynchedEntityData.defineId(GumbeeperEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> POSSESSOR_LICOWITCH_ID = SynchedEntityData.defineId(GumbeeperEntity.class, EntityDataSerializers.INT);

    public GumbeeperEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Cat.class, 10.0F, 1.0D, 1.2D) {
            public void tick() {
                super.tick();
                GumbeeperEntity.this.catScareTime = 20;
            }
        });
        this.goalSelector.addGoal(2, new GumbeeperEntity.AttackGoal());
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0D, 45));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Husk.class, true, false));
    }

    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigatorNoSpin(this, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.2D).add(Attributes.MAX_HEALTH, 14.0D).add(Attributes.ARMOR, 4.0D).add(Attributes.ATTACK_DAMAGE, 4.0D).add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public static boolean checkGumbeeperSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        return checkMonsterSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource) && randomSource.nextInt(10) == 0;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EXPLODING, false);
        this.entityData.define(ATTACK_CHARGE, 0.0F);
        this.entityData.define(GUMBALLS_LEFT, DEFAULT_GUMBALLS);
        this.entityData.define(SHOOTING, false);
        this.entityData.define(CHARGED, false);
        this.entityData.define(POSSESSOR_LICOWITCH_ID, -1);
    }

    @Override
    public void tick(){
        super.tick();
        prevExplodeProgress = explodeProgress;
        prevDialRot = dialRot;
        prevShootProgress = shootProgress;
        float attackCharge = getAttackCharge();
        if (this.isExploding() && explodeProgress < 20F) {
            explodeProgress++;
        }
        if (!this.isExploding() && explodeProgress > 0F) {
            explodeProgress--;
        }
        if (this.isShooting() && shootProgress < 5F) {
            shootProgress = Math.min(5F, shootProgress + 2.5F);
        }
        if (!this.isShooting() && shootProgress > 0F) {
            shootProgress = Math.max(0F, shootProgress - 1);
        }
        if(attackCharge == 0){
            if(Mth.wrapDegrees(dialRot) != 0){
                dialRot = Mth.approachDegrees(dialRot, 0, 30);
            }else{
                dialRot = 0;
            }
        }else{
            dialRot = Mth.approach(dialRot, MAX_DIAL_ROT * attackCharge, 10);
        }
        if(postShootTime > 0){
            postShootTime--;
        }else{
            this.setShooting(false);
        }
        if(this.isExploding()){
            if(level().isClientSide && explodeProgress >= 18.0F){
                for(int i = 0; i < 3 + random.nextInt(2); i++){
                    level().addParticle(ParticleTypes.EXPLOSION, this.getRandomX(0.3F), this.getRandomY(), this.getRandomZ(0.3F), 0, 0, 0);
                }
            }
            if(explodeProgress >= 20.0F){
                if(!level().isClientSide && !hasExploded){
                    int gumballs = this.isCharged() ? 30 : 15;
                    for(int i = 0; i < gumballs + random.nextInt(5); i++){
                        GumballEntity gumball = new GumballEntity(this.level(), this);
                        gumball.setPos(new Vec3(this.getRandomX(0.3F), this.getY() + 0.7F + random.nextFloat() * 0.5F, this.getRandomZ(0.3F)));
                        Vec3 delta = new Vec3(random.nextFloat() - 0.5F, random.nextFloat() - 0.25F, random.nextFloat() - 0.5F).normalize().scale(random.nextFloat() * 0.25F + 0.75F);
                        gumball.setDeltaMovement(delta);
                        this.level().addFreshEntity(gumball);
                        if(this.isCharged()){
                            gumball.setMaximumBounces(10);
                            gumball.setDamage((float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() + 2);
                        }else{
                            gumball.setDamage((float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                        }
                    }
                    hasExploded = true;
                    this.discard();
                }
                this.playSound(ACSoundRegistry.GUMBEEPER_EXPLODE.get());
            }
        }
        if(this.isCharged() && this.isAlive() && this.tickCount % 150 == 0){
            this.heal(1);
        }
        if(level().isClientSide){
            spawnPossessedParticles(getRandomX(0.5D), getRandomY(), getRandomZ(0.5D), this.level());
        }
    }

    @Override
    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, flying ? this.getY() - this.yo : 0, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 8.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.is(ItemTags.CREEPER_IGNITERS)) {
            SoundEvent soundevent = itemstack.is(Items.FIRE_CHARGE) ? SoundEvents.FIRECHARGE_USE : SoundEvents.FLINTANDSTEEL_USE;
            this.level().playSound(player, this.getX(), this.getY(), this.getZ(), soundevent, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
            if (!this.level().isClientSide) {
                this.setExploding(true);
                itemstack.hurtAndBreak(1, player, (p_32290_) -> {
                    p_32290_.broadcastBreakEvent(hand);
                });
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            return super.mobInteract(player, hand);
        }
    }

    public void thunderHit(ServerLevel serverLevel, LightningBolt lightningBolt) {
        super.thunderHit(serverLevel, lightningBolt);
        this.setCharged(true);
    }

    public boolean isExploding() {
        return this.entityData.get(EXPLODING);
    }

    public void setExploding(boolean explode) {
        this.entityData.set(EXPLODING, explode);
    }

    public void setGumballsLeft(int i) {
        this.entityData.set(GUMBALLS_LEFT, i);
    }

    public int getGumballsLeft() {
        return this.entityData.get(GUMBALLS_LEFT);
    }

    public void setAttackCharge(float f) {
        this.entityData.set(ATTACK_CHARGE, f);
    }

    public float getAttackCharge() {
        return this.entityData.get(ATTACK_CHARGE);
    }

    public boolean isShooting() {
        return this.entityData.get(SHOOTING);
    }

    public void setShooting(boolean shooting) {
        this.entityData.set(SHOOTING, shooting);
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
    public boolean canAttack(LivingEntity living) {
        if(this.getPossessedByLicowitchId() != -1){
            LicowitchEntity licowitch = this.getPossessingLicowitch(this.level());
            if(licowitch != null && licowitch.isFriendlyFire(living)){
                return false;
            }
        }
        return super.canAttack(living);
    }

    public boolean isCharged() {
        return this.entityData.get(CHARGED);
    }

    public void setCharged(boolean explode) {
        this.entityData.set(CHARGED, explode);
    }

    public float getExplodeProgress(float partialTick) {
        return (prevExplodeProgress + (explodeProgress - prevExplodeProgress) * partialTick) * 0.05F;
    }

    public float getShootProgress(float partialTick) {
        return (prevShootProgress + (shootProgress - prevShootProgress) * partialTick) * 0.2F;
    }

    public boolean canShootGumball(){
        return getGumballsLeft() > 0 && dialRot >= MAX_DIAL_ROT && getAttackCharge() == 1.0F;
    }

    public void shootGumball(LivingEntity target) {
        Vec3 spawnGumballFrom = new Vec3(0F, 0.3F, 0.4F).yRot(-this.yBodyRot * ((float) Math.PI / 180F)).add(position());
        int shotCount = this.isCharged() ? 3 : 1;
        this.playSound(ACSoundRegistry.GUMBALL_LAUNCH.get());
        for(int i = 0; i < shotCount; i++){
            GumballEntity gumball = new GumballEntity(this.level(), this);
            gumball.setPos(spawnGumballFrom);
            Vec3 targetVec = new Vec3(target.getX(), target.getY(0.6D), target.getZ());
            if(isCharged() && i != shotCount / 2){
                Vec3 vec3 = new Vec3(i < shotCount / 2 ? 3.0F : -3.0F, 0.0F, 0.0F).yRot(-this.yBodyRot * ((float) Math.PI / 180F));
                targetVec = targetVec.add(vec3);
            }
            double d0 = targetVec.x() - spawnGumballFrom.x;
            double d1 = targetVec.y() - spawnGumballFrom.y;
            double d2 = targetVec.z() - spawnGumballFrom.z;
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            gumball.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.2F, (float) (14 - this.level().getDifficulty().getId() * 4));
            this.level().addFreshEntity(gumball);
            if(isCharged()){
                gumball.setMaximumBounces(10);
                gumball.setDamage((float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() + 2.0F);
            }else{
                gumball.setDamage((float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
            }
        }
        this.playSound(SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, 1.0F, this.getRandom().nextFloat() * 0.4F + 0.8F);
        if(!this.isCharged() || random.nextFloat() < 0.33F){
            this.setGumballsLeft(this.getGumballsLeft() - 1);
        }
        this.setAttackCharge(0.0F);
        this.setShooting(true);
        this.postShootTime = 5;
    }

    public double getDialRot(float partialTick) {
        return (prevDialRot + (dialRot - prevDialRot) * partialTick);
    }

    public boolean hasLineOfSightToGumballHole(Entity entity) {
        if (entity.level() != this.level()) {
            return false;
        } else {
            Vec3 vec3 = new Vec3(this.getX(), this.getY() + 0.3F, this.getZ());
            Vec3 vec31 = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
            if (vec31.distanceTo(vec3) > 128.0D) {
                return false;
            } else {
                return this.level().clip(new ClipContext(vec3, vec31, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
            }
        }
    }

    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("Charged", this.isCharged());
        compoundTag.putInt("Gumballs", this.getGumballsLeft());
    }

    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setCharged(compoundTag.getBoolean("Charged"));
        this.setGumballsLeft(compoundTag.getInt("Gumballs"));
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource damageSource, int experience, boolean idk) {
        super.dropCustomDeathLoot(damageSource, experience, idk);
        if (damageSource.getEntity() instanceof CaniacEntity) {
            this.spawnAtLocation(ACItemRegistry.MUSIC_DISC_TASTY_FRAGMENT.get());
        }
    }

    @Override
    public boolean isPowered() {
        return this.isCharged();
    }

    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != MobEffects.HUNGER;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.GUMBEEPER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.GUMBEEPER_DEATH.get();
    }

    public class AttackGoal extends Goal {

        private int seeTime;
        private int strafingTime = -1;
        private boolean strafingClockwise;
        private boolean strafingBackwards;

        public AttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = GumbeeperEntity.this.getTarget();
            return target != null && target.isAlive();
        }


        public void stop() {
            super.stop();
            this.seeTime = 0;
            this.strafingTime = -1;
            GumbeeperEntity.this.setAttackCharge(0.0F);
            GumbeeperEntity.this.setExploding(false);
        }

        @Override
        public void tick() {
            LivingEntity target = GumbeeperEntity.this.getTarget();
            boolean canRange = GumbeeperEntity.this.getGumballsLeft() > 0;
            if(target != null){
                double dist = GumbeeperEntity.this.distanceTo(target);
                if(!canRange){
                    if(dist < target.getBbWidth() + 1.5F){
                        GumbeeperEntity.this.setExploding(true);
                    }else{
                        GumbeeperEntity.this.getNavigation().moveTo(target, 1.5F);
                    }
                }else if(dist < 16.0F && hasLineOfSightToGumballHole(target)){
                    GumbeeperEntity.this.getNavigation().stop();
                    strafingTime++;
                }else{
                    GumbeeperEntity.this.getNavigation().moveTo(target, 1F);
                    strafingTime = -1;
                }
                if (this.strafingTime >= 20) {
                    if ((double)GumbeeperEntity.this.getRandom().nextFloat() < 0.3D) {
                        this.strafingClockwise = !this.strafingClockwise;
                    }
                    if ((double)GumbeeperEntity.this.getRandom().nextFloat() < 0.3D) {
                        this.strafingBackwards = !this.strafingBackwards;
                    }
                    this.strafingTime = 0;
                }
                if(this.strafingTime > -1){
                    if (dist > 12.0F) {
                        this.strafingBackwards = false;
                    } else if (dist < 5.0F) {
                        this.strafingBackwards = true;
                    }
                    GumbeeperEntity.this.getMoveControl().strafe(this.strafingBackwards ? -1F : 1F, this.strafingClockwise ? 0.5F : -0.5F);
                    GumbeeperEntity.this.lookAt(target, 30.0F, 30.0F);
                }
                if(canRange && GumbeeperEntity.this.hasLineOfSightToGumballHole(target)){
                    GumbeeperEntity.this.setAttackCharge(Math.min(1F, GumbeeperEntity.this.getAttackCharge() + (GumbeeperEntity.this.isCharged() ? 0.3F : 0.1F)));
                    if(GumbeeperEntity.this.canShootGumball()){
                        GumbeeperEntity.this.shootGumball(target);
                    }
                }
            }
        }
    }
}
