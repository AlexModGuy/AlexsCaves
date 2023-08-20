package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.PlayMessages;

public class DarkArrowEntity extends AbstractArrow {

    private float fadeOut = 0;
    private float prevFadeOut = 0;
    private boolean startFading = false;

    public DarkArrowEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public DarkArrowEntity(Level level, LivingEntity shooter) {
        super(ACEntityRegistry.DARK_ARROW.get(), shooter, level);
    }

    public DarkArrowEntity(Level level, double x, double y, double z) {
        super(ACEntityRegistry.DARK_ARROW.get(), x, y, z, level);
    }

    public DarkArrowEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.DARK_ARROW.get(), level);
        this.setBoundingBox(this.makeBoundingBox());
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void startFalling() {
        this.inGround = false;
    }

    @Override
    public void tick() {
        super.tick();
        this.prevFadeOut = this.fadeOut;
        if(this.inGround){
            this.startFading = true;
        }
        if(this.startFading){
            this.noPhysics = true;
            this.setDeltaMovement(this.getDeltaMovement().scale(0.7F));
            if(this.fadeOut++ > 5F){
                this.discard();
            }
        }
    }

    protected float getWaterInertia() {
        return 0.9F;
    }

    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        Entity owner = this.getOwner();
        DamageSource damageSource = ACDamageTypes.causeDarkArrowDamage(entity.level().registryAccess(), owner);
        if(owner == null || !entity.is(owner) && !entity.isAlliedTo(owner) && !owner.isAlliedTo(entity)){
            entity.hurt(damageSource, 0.45F);
        }
    }

    public float getFadeOut(float partialTicks){
        return prevFadeOut + (fadeOut - prevFadeOut) * partialTicks;
    }
}