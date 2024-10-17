package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.util.LaysEggs;
import com.github.alexmodguy.alexscaves.server.entity.util.RidingMeterMount;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.level.storage.ACWorldData;
import com.github.alexmodguy.alexscaves.server.misc.ACAdvancementTriggerRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.server.entity.IDancesToJukebox;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.IAdvancedPathingMob;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public abstract class DinosaurEntity extends TamableAnimal implements IDancesToJukebox, LaysEggs, IAdvancedPathingMob, RidingMeterMount {

    private static final EntityDataAccessor<Boolean> DANCING = SynchedEntityData.defineId(DinosaurEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_EGG = SynchedEntityData.defineId(DinosaurEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(DinosaurEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ALT_SKIN = SynchedEntityData.defineId(DinosaurEntity.class, EntityDataSerializers.INT);
    public float prevDanceProgress;
    public float danceProgress;
    private BlockPos jukeboxPosition;
    private float prevSitProgress;
    private float sitProgress;
    private float prevBuryEggsProgress;
    private float buryEggsProgress;
    public boolean buryingEggs;

    public DinosaurEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DANCING, false);
        this.entityData.define(HAS_EGG, false);
        this.entityData.define(COMMAND, 0);
        this.entityData.define(ALT_SKIN, 0);
    }

    public static boolean checkPrehistoricSpawnRules(EntityType<? extends Animal> type, LevelAccessor levelAccessor, MobSpawnType mobType, BlockPos pos, RandomSource randomSource) {
        return levelAccessor.getBlockState(pos.below()).is(ACTagRegistry.DINOSAURS_SPAWNABLE_ON) && levelAccessor.getFluidState(pos).isEmpty() && levelAccessor.getFluidState(pos.below()).isEmpty();
    }

    public static boolean checkPrehistoricPostBossSpawnRules(EntityType<? extends Animal> type, LevelAccessor levelAccessor, MobSpawnType mobType, BlockPos pos, RandomSource randomSource) {
        if (checkPrehistoricSpawnRules(type, levelAccessor, mobType, pos, randomSource) && levelAccessor instanceof ServerLevel serverLevel) {
            ACWorldData data = ACWorldData.get(serverLevel);
            return data != null && data.isPrimordialBossDefeatedOnce();
        }
        return false;
    }

    public void tick() {
        super.tick();
        prevDanceProgress = danceProgress;
        prevSitProgress = sitProgress;
        prevBuryEggsProgress = buryEggsProgress;
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
        if (isInSittingPose() && sitProgress < maxSitTicks()) {
            sitProgress++;
        }
        if (!isInSittingPose() && sitProgress > 0F) {
            sitProgress--;
        }
        if (buryingEggs && buryEggsProgress < 5F) {
            buryEggsProgress++;
        }
        if (!buryingEggs && buryEggsProgress > 0F) {
            buryEggsProgress--;
        }
    }

    public float maxSitTicks() {
        return 10.0F;
    }

    public boolean isDancing() {
        return this.entityData.get(DANCING);
    }

    public void setDancing(boolean bool) {
        this.entityData.set(DANCING, bool);
    }

    public boolean hasEgg() {
        return this.entityData.get(HAS_EGG);
    }

    public void setHasEgg(boolean hasEgg) {
        this.entityData.set(HAS_EGG, hasEgg);
    }

    public int getCommand() {
        return this.entityData.get(COMMAND);
    }

    public void setCommand(int command) {
        this.entityData.set(COMMAND, command);
    }

    public int getAltSkin() {
        return this.entityData.get(ALT_SKIN);
    }

    public void setAltSkin(int skinIndex) {
        this.entityData.set(ALT_SKIN, skinIndex);
    }

    public void setRecordPlayingNearby(BlockPos pos, boolean playing) {
        this.onClientPlayMusicDisc(this.getId(), pos, playing);
    }

    @Override
    public void setJukeboxPos(BlockPos blockPos) {
        this.jukeboxPosition = blockPos;
    }

    public float getDanceProgress(float partialTicks) {
        return (prevDanceProgress + (danceProgress - prevDanceProgress) * partialTicks) * 0.2F;
    }

    public float getSitProgress(float partialTicks) {
        return (prevSitProgress + (sitProgress - prevSitProgress) * partialTicks) / maxSitTicks();
    }

    public float getBuryEggsProgress(float partialTicks) {
        return (prevBuryEggsProgress + (buryEggsProgress - prevBuryEggsProgress) * partialTicks) * 0.2F;
    }

    @Override
    public void travel(Vec3 vec3d) {
        if (this.isDancing() || this.isInSittingPose()) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Command", this.getCommand());
        compound.putBoolean("Egg", this.hasEgg());
        compound.putInt("AltSkin", this.getAltSkin());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setCommand(compound.getInt("Command"));
        this.setHasEgg(compound.getBoolean("Egg"));
        int altSkin = compound.getInt("AltSkin");
        //compatibility with pre 1.1.0 saves
        if (compound.contains("Retro") && compound.getBoolean("Retro")) {
            altSkin = 1;
        }
        this.setAltSkin(altSkin);
    }

    public boolean tamesFromHatching() {
        return false;
    }

    public boolean hasRidingMeter() {
        return false;
    }

    public float getMeterAmount() {
        return 0.0F;
    }

    protected void clampRotation(LivingEntity livingEntity, float clampRange) {
        livingEntity.setYBodyRot(this.getYRot());
        float f = Mth.wrapDegrees(livingEntity.getYRot() - this.getYRot());
        float f1 = Mth.clamp(f, -clampRange, clampRange);
        livingEntity.yRotO += f1 - f;
        livingEntity.yBodyRotO += f1 - f;
        livingEntity.setYRot(livingEntity.getYRot() + f1 - f);
        livingEntity.setYHeadRot(livingEntity.getYRot());
    }

    public void onLayEggTick(BlockPos belowEgg, int time) {
        this.walkAnimation.update(0.5F, 0.4F);
        this.level().broadcastEntityEvent(this, (byte) 77);
    }

    public void handleEntityEvent(byte b) {
        if (b == 77) {
            this.buryingEggs = true;
            float radius = this.getBbWidth() * 0.55F;
            float particleCount = (5 + random.nextInt(5)) * radius;
            for (int i1 = 0; i1 < particleCount; i1++) {
                double motionX = (getRandom().nextFloat() - 0.5F) * 0.7D;
                double motionY = getRandom().nextFloat() * 0.7D + 0.8F;
                double motionZ = (getRandom().nextFloat() - 0.5F) * 0.7D;
                float angle = (0.01745329251F * (this.yBodyRot + (i1 / particleCount) * 360F));
                double extraX = radius * Mth.sin((float) (Math.PI + angle));
                double extraY = 1.2F;
                double extraZ = radius * Mth.cos(angle);
                BlockPos ground = BlockPos.containing(ACMath.getGroundBelowPosition(level(), new Vec3(Mth.floor(this.getX() + extraX), Mth.floor(this.getY() + extraY), Mth.floor(this.getZ() + extraZ))));
                BlockState groundState = this.level().getBlockState(ground.below());
                if (groundState.isSolid()) {
                    if (level().isClientSide) {
                        level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, groundState), true, this.getX() + extraX, ground.getY(), this.getZ() + extraZ, motionX, motionY, motionZ);
                    }
                }
            }
        } else if (b == 78) {
            this.buryingEggs = false;
        } else if (b == 82 || b == 83) {
            ParticleOptions options;
            if(b == 82){
                options = ACParticleRegistry.DINOSAUR_TRANSFORMATION_AMBER.get();
            }else {
                options = ACParticleRegistry.DINOSAUR_TRANSFORMATION_TECTONIC.get();
            }
            for (int i = 0; i < 15 + level().random.nextInt(5); i++) {
                this.level().addParticle(options, this.getX(), this.getY(0.5F), this.getZ(), this.getId(), 0, 0);
            }
        } else {
            super.handleEntityEvent(b);
        }
    }

    public boolean onFeedMixture(ItemStack itemStack, Player player) {
        return false;
    }

    public boolean isInSittingPose() {
        return super.isInSittingPose() && !(this.isVehicle() || this.isPassenger());
    }

    public int getAltSkinForItem(ItemStack stack) {
        if (stack.is(ACItemRegistry.AMBER_CURIOSITY.get())) {
            return 1;
        } else if (stack.is(ACItemRegistry.TECTONIC_SHARD.get())) {
            return 2;
        } else {
            return 0;
        }
    }

    public BlockState createEggBeddingBlockState() {
        return ACBlockRegistry.FERN_THATCH.get().defaultBlockState();
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
        InteractionResult type = super.mobInteract(player, hand);
        if (!interactionresult.consumesAction() && !type.consumesAction()) {
            int altSkinFromItem = getAltSkinForItem(itemstack);
            if (altSkinFromItem > 0) {
                this.usePlayerItem(player, hand, itemstack);
                this.playSound(altSkinFromItem == 2 ? ACSoundRegistry.TECTONIC_SHARD_TRANSFORM.get() : ACSoundRegistry.AMBER_MONOLITH_SUMMON.get());
                if (!level().isClientSide) {
                    if(altSkinFromItem == this.getAltSkin()){
                        this.setAltSkin(0);
                    }else{
                        this.setAltSkin(altSkinFromItem);
                    }
                    this.level().broadcastEntityEvent(this, (byte) (altSkinFromItem == 2 ? 83 : 82));
                }
                return InteractionResult.SUCCESS;
            } else if (isTame() && isOwnedBy(player) && !isFood(itemstack)) {
                if (canOwnerCommand(player)) {
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
                } else if (canOwnerMount(player)) {
                    if (this.getType() == ACEntityRegistry.SUBTERRANODON.get() && this.canAddPassenger(player)) {
                        this.moveTo(this.getX(), this.getY() + player.getBbHeight() + 0.5F, this.getZ());
                    }
                    if (!level().isClientSide && player.startRiding(this)) {
                        return InteractionResult.CONSUME;
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return type;
    }

    public boolean startRiding(Entity entity, boolean force) {
        boolean flag = super.startRiding(entity, force);
        if (flag && entity instanceof AbstractMinecart) {
            List<EntityType> nearbyDinosaurEntityTypes = new ArrayList<>();
            double advancementRange = 30.0D;
            for (DinosaurEntity dinosaur : this.level().getEntitiesOfClass(DinosaurEntity.class, this.getBoundingBox().inflate(advancementRange, advancementRange, advancementRange))) {
                if (dinosaur.getRootVehicle() instanceof AbstractMinecart && !nearbyDinosaurEntityTypes.contains(dinosaur.getType())) {
                    nearbyDinosaurEntityTypes.add(dinosaur.getType());
                }
            }
            if (nearbyDinosaurEntityTypes.size() >= 5) {
                for (Player player : level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(advancementRange))) {
                    if (player.distanceTo(this) < advancementRange) {
                        ACAdvancementTriggerRegistry.DINOSAURS_MINECART.triggerForEntity(player);
                    }
                }

            }
        }
        return flag;
    }

    @Override
    public boolean isAlliedTo(Entity entityIn) {
        if (this.isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity) {
                return true;
            }
            if (entityIn instanceof TamableAnimal) {
                return ((TamableAnimal) entityIn).isOwnedBy(livingentity);
            }
            if (livingentity != null) {
                return livingentity.isAlliedTo(entityIn);
            }
        }
        return super.isAlliedTo(entityIn);
    }

    public boolean canOwnerMount(Player player) {
        return false;
    }

    public boolean canOwnerCommand(Player ownerPlayer) {
        return false;
    }

    public boolean stopTickingPathing() {
        return this.isInSittingPose() || this.isVehicle() || this.isDancing();
    }

}
