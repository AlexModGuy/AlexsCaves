package com.github.alexmodguy.alexscaves.server.block.fluid;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class AcidFluidType extends FluidType {

    public static final ResourceLocation FLUID_STILL = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "block/acid_still");
    public static final ResourceLocation FLUID_FLOWING = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "block/acid_flowing");
    public static final ResourceLocation OVERLAY = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/misc/under_acid.png");

    public AcidFluidType(Properties properties) {
        super(properties);
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return FLUID_STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return FLUID_FLOWING;
            }

            @Override
            public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
                return OVERLAY;
            }
        });
    }

    public boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity) {
        double d9 = entity.getY();
        float f4 = 0.8F;
        float f5 = 0.02F;
        float f6 = (float) EnchantmentHelper.getDepthStrider(entity);
        double d0 = 0.08D;
        boolean flag = entity.getDeltaMovement().y <= 0.0D;
        if (f6 > 3.0F) {
            f6 = 3.0F;
        }

        if (!entity.onGround()) {
            f6 *= 0.5F;
        }

        if (f6 > 0.0F) {
            f4 += (0.54600006F - f4) * f6 / 3.0F;
            f5 += (entity.getSpeed() - f5) * f6 / 3.0F;
        }

        if (entity.hasEffect(MobEffects.DOLPHINS_GRACE)) {
            f4 = 0.96F;
        }

        f5 *= (float) entity.getAttribute(net.minecraftforge.common.ForgeMod.SWIM_SPEED.get()).getValue();
        entity.moveRelative(f5, movementVector);
        entity.move(MoverType.SELF, entity.getDeltaMovement());
        Vec3 vec36 = entity.getDeltaMovement();
        if (entity.horizontalCollision && entity.onClimbable()) {
            vec36 = new Vec3(vec36.x, 0.2D, vec36.z);
        }

        entity.setDeltaMovement(vec36.multiply((double) f4, (double) 0.8F, (double) f4));
        Vec3 vec32 = entity.getFluidFallingAdjustedMovement(d0, flag, entity.getDeltaMovement());
        entity.setDeltaMovement(vec32);
        if (entity.horizontalCollision && entity.isFree(vec32.x, vec32.y + (double) 0.6F - entity.getY() + d9, vec32.z)) {
            entity.setDeltaMovement(vec32.x, (double) 0.3F, vec32.z);
        }
        return true;
    }

    @Override
    public boolean isVaporizedOnPlacement(Level level, BlockPos pos, FluidStack stack) {
        return level.dimensionType().ultraWarm();
    }

    @Override
    public void onVaporize(@Nullable Player player, Level level, BlockPos pos, FluidStack stack) {
        SoundEvent sound = this.getSound(player, level, pos, SoundActions.FLUID_VAPORIZE);
        level.playSound(player, pos, sound != null ? sound : SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);

        for (int l = 0; l < 8; ++l) {
            level.addAlwaysVisibleParticle(ACParticleRegistry.RADGILL_SPLASH.get(), (double) pos.getX() + Math.random(), (double) pos.getY() + Math.random(), (double) pos.getZ() + Math.random(), (Math.random() - 0.5F) * 0.25F, Math.random() * 0.25F, (Math.random() - 0.5F) * 0.25F);
        }
        level.setBlockAndUpdate(pos, ACBlockRegistry.UNREFINED_WASTE.get().defaultBlockState());
    }
}
