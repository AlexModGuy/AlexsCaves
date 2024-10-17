package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.poi.ACPOIRegistry;
import com.github.alexmodguy.alexscaves.server.message.SundropRainbowMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

public class SundropBlock extends DirectionalBlock {
    public SundropBlock() {
        super(Properties.of().mapColor(DyeColor.YELLOW).strength(2.0F, 5.0F).sound(ACSoundTypes.SQUISHY_CANDY).lightLevel((i) -> 15).emissiveRendering((state, level, pos) -> true).randomTicks().noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(FACING);
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        if (randomSource.nextInt(2) == 0) {
            if (AlexsCaves.PROXY.checkIfParticleAt(ACParticleRegistry.SUNDROP.get(), blockPos)) {
                level.addParticle(ACParticleRegistry.SUNDROP.get(), (double) blockPos.getX() + 0.5F, (double) blockPos.getY() + 0.5F, (double) blockPos.getZ() + 0.5F, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public void fallOn(Level level, BlockState blockState, BlockPos blockPos, Entity entity, float fallAmount) {
        if (entity.isSuppressingBounce()) {
            super.fallOn(level, blockState, blockPos, entity, fallAmount);
        } else {
            entity.causeFallDamage(fallAmount, 0.0F, level.damageSources().fall());
        }
    }

    public void updateEntityAfterFallOn(BlockGetter getter, Entity entity) {
        if (entity.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(getter, entity);
        } else {
            this.bounceUp(entity);
        }
    }

    private void bounceUp(Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        if (vec3.y < 0.0D) {
            double d0 = entity instanceof LivingEntity ? 1.0D : 0.8D;
            entity.setDeltaMovement(vec3.x, -vec3.y * d0, vec3.z);
        }
    }

    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos posIn, RandomSource randomSource) {
        if (randomSource.nextInt(2) != 0 && serverLevel.hasChunkAt(posIn)) {
            PoiManager pointofinterestmanager = serverLevel.getPoiManager();
            int range = 30;
            Optional<BlockPos> rainbowTarget = Optional.ofNullable(pointofinterestmanager.getRandom(poiTypeHolder -> poiTypeHolder.is(ACPOIRegistry.SUNDROP.getKey()), blockPos -> canSendRainbowTo(serverLevel, posIn, blockPos, range), PoiManager.Occupancy.ANY, posIn, range, randomSource).orElse(null));
            if (rainbowTarget.isPresent() && serverLevel.hasChunkAt(rainbowTarget.get())) {
                BlockPos target = rainbowTarget.get();
                AlexsCaves.sendMSGToAll(new SundropRainbowMessage(posIn.getX(), posIn.getY(), posIn.getZ(), target.getX(), target.getY(), target.getZ()));
            }
        }
    }

    private boolean canSendRainbowTo(ServerLevel serverLevel, BlockPos from, BlockPos to, int range) {
        if(from.equals(to) || !serverLevel.hasChunkAt(to)){
            return false;
        }
        int distance = (int) Math.sqrt(from.distSqr(to));
        // hit result
        /*
        HitResult raytraceresult = serverLevel.clip(new ClipContext(from.getCenter(), to.getCenter(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
        if (raytraceresult instanceof BlockHitResult) {
            BlockHitResult blockRayTraceResult = (BlockHitResult) raytraceresult;
            BlockPos pos = blockRayTraceResult.getBlockPos();
            return pos.equals(from) || serverLevel.isEmptyBlock(pos);
        }
        */
        return distance < range;
    }


    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {

        super.stepOn(level, blockPos, blockState, entity);
    }
}
