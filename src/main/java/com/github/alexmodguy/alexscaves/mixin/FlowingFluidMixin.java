package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMixin extends Fluid {

    @Inject(
            method = {"Lnet/minecraft/world/level/material/FlowingFluid;canHoldFluid(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/Fluid;)Z"},
            cancellable = true,
            remap = true,
            at = @At(value = "HEAD")
    )
    public void ac_canHoldFluid(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
        if (blockState.getBlock() instanceof LiquidBlockContainer && this.is(ACTagRegistry.DOES_NOT_FLOW_INTO_WATERLOGGABLE_BLOCKS)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = {"Lnet/minecraft/world/level/material/FlowingFluid;spreadTo(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/material/FluidState;)V"},
            cancellable = true,
            remap = true,
            at = @At(value = "HEAD")
    )
    public void ac_spreadTo(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState, CallbackInfo ci) {
        if (blockState.getBlock() instanceof LiquidBlockContainer && this.is(ACTagRegistry.DOES_NOT_FLOW_INTO_WATERLOGGABLE_BLOCKS)) {
            ci.cancel();
        }
    }
}
