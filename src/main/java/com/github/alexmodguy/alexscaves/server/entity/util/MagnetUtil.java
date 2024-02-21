package com.github.alexmodguy.alexscaves.server.entity.util;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.poi.ACPOIRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.AbstractMovingBlockEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.MovingMetalBlockEntity;
import com.github.alexmodguy.alexscaves.server.message.PlayerJumpFromMagnetMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.google.common.base.Predicates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MagnetUtil {

    private static Stream<BlockPos> getNearbyAttractingMagnets(BlockPos blockpos, ServerLevel world, int range) {
        PoiManager pointofinterestmanager = world.getPoiManager();
        return pointofinterestmanager.findAll(poiTypeHolder -> poiTypeHolder.is(ACPOIRegistry.ATTRACTING_MAGNETS.getKey()), Predicates.alwaysTrue(), blockpos, range, PoiManager.Occupancy.ANY);
    }

    private static Stream<BlockPos> getNearbyRepellingMagnets(BlockPos blockpos, ServerLevel world, int range) {
        PoiManager pointofinterestmanager = world.getPoiManager();
        return pointofinterestmanager.findAll(poiTypeHolder -> poiTypeHolder.is(ACPOIRegistry.REPELLING_MAGNETS.getKey()), Predicates.alwaysTrue(), blockpos, range, PoiManager.Occupancy.ANY);
    }

    public static void tickMagnetism(Entity entity) {
        if (!entity.level().isClientSide && entity.level() instanceof ServerLevel serverLevel) {
            int range = 5;
            Stream<BlockPos> attracts = getNearbyAttractingMagnets(entity.blockPosition(), serverLevel, range);
            Stream<BlockPos> repels = getNearbyRepellingMagnets(entity.blockPosition(), serverLevel, range);
            attracts.forEach((magnet) -> {
                Vec3 center = Vec3.atCenterOf(magnet);
                double distance = Mth.clamp(Math.sqrt(entity.distanceToSqr(center)) / range, 0, 1);
                Vec3 pull = Vec3.atCenterOf(magnet).subtract(entity.position());
                Vec3 pullNorm = pull.length() < 1.0F ? pull : pull.normalize();
                Vec3 pullScale = pullNorm.scale((1 - distance) * 0.25F);
                setEntityMagneticDelta(entity, getEntityMagneticDelta(entity).scale(0.9).add(pullScale));
            });
            repels.forEach((magnet) -> {
                Vec3 center = Vec3.atCenterOf(magnet);
                double distance = Mth.clamp(Math.sqrt(entity.distanceToSqr(center)) / range, 0, 1);
                Vec3 pull = entity.position().subtract(Vec3.atCenterOf(magnet));
                Vec3 pullNorm = pull.length() < 1.0F ? pull : pull.normalize();
                Vec3 pullScale = pullNorm.scale((1 - distance) * 0.25F);
                setEntityMagneticDelta(entity, getEntityMagneticDelta(entity).scale(0.9).add(pullScale));
            });
        }
        Vec3 vec3 = getEntityMagneticDelta(entity);
        Direction dir = getEntityMagneticDirection(entity);
        MagneticEntityAccessor magneticAccessor = (MagneticEntityAccessor) entity;
        boolean attatchesToMagnets = AlexsCaves.COMMON_CONFIG.walkingOnMagnets.get() && attachesToMagnets(entity);
        float progress = magneticAccessor.getAttachmentProgress(1.0F);
        if (vec3 != Vec3.ZERO) {
            Direction standingOnDirection = getStandingOnMagnetSurface(entity);
            float overrideByWalking = 1.0F;
            if (entity instanceof LivingEntity living) {
                if (living.jumping && standingOnDirection == dir) {
                    if (living.level().isClientSide) {
                        AlexsCaves.sendMSGToServer(new PlayerJumpFromMagnetMessage(living.getId(), living.jumping));
                    }
                    magneticAccessor.postMagnetJump();
                }
                float detract = living.xxa * living.xxa + living.yya * living.yya + living.zza * living.zza;
                overrideByWalking -= Math.min(1.0F, Math.sqrt(detract) * 0.7F);
            }
            if (!isEntityOnMovingMetal(entity)) {
                if (attatchesToMagnets) {
                    Vec3 vec31;
                    if (dir == Direction.DOWN && standingOnDirection == null) {
                        vec31 = vec3.multiply(overrideByWalking, overrideByWalking, overrideByWalking);
                        entity.setDeltaMovement(entity.getDeltaMovement().add(vec31));
                        entity.refreshDimensions();
                    } else {
                        magneticAccessor.stepOnMagnetBlock(getSamplePosForDirection(entity, dir, 0.5F));
                        float f1 = Math.abs(dir.getStepX());
                        float f2 = Math.abs(dir.getStepY());
                        float f3 = Math.abs(dir.getStepZ());
                        vec31 = vec3.multiply(overrideByWalking * f1, overrideByWalking * f2, overrideByWalking * f3);
                        if (entity.getPose() == Pose.SWIMMING) {
                            entity.setPose(Pose.STANDING);
                        }
                        if (entity instanceof LivingEntity living) {
                            vec31 = processMovementControls(0, living, dir);
                        }
                        entity.setDeltaMovement(vec31);
                    }
                    Direction closest = calculateClosestDirection(entity);
                    if (closest != null && closest != Direction.DOWN) {
                        entity.fallDistance = 0.0F;
                    }
                    if (closest != dir && magneticAccessor.canChangeDirection() && (progress == 1.0F || closest == Direction.UP)) {
                        entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.4F, 0));
                        setEntityMagneticDirection(entity, closest);
                        entity.refreshDimensions();
                        entity.setPose(Pose.STANDING);
                    }
                } else {
                    entity.setDeltaMovement(entity.getDeltaMovement().add(vec3));
                }
            }
            setEntityMagneticDelta(entity, vec3.scale(0.08F));
        }
        if (!attatchesToMagnets && dir != Direction.DOWN) {
            setEntityMagneticDirection(entity, Direction.DOWN);
            entity.refreshDimensions();
            entity.setPose(Pose.STANDING);
        }
    }

    public static boolean isEntityOnMovingMetal(Entity entity) {
        return !(entity instanceof MovingMetalBlockEntity) && !entity.level().getEntitiesOfClass(MovingMetalBlockEntity.class, entity.getBoundingBox().inflate(0.4F)).isEmpty();
    }

    private static Vec3 processMovementControls(float dist, LivingEntity living, Direction dir) {
        double dSpeed = living.getAttributeValue(Attributes.MOVEMENT_SPEED);
        float jump = living.jumping && getStandingOnMagnetSurface(living) != null ? 0.75F : -0.1F;

        if (dir == Direction.UP) {
            return new Vec3(living.getDeltaMovement().x * 0.98, -living.getDeltaMovement().y - jump, living.getDeltaMovement().z * 0.98);
        } else if (dir == Direction.NORTH) {
            return new Vec3(-living.xxa * dSpeed * 0.6F, living.zza * dSpeed, jump);
        } else if (dir == Direction.SOUTH) {
            return new Vec3(living.xxa * dSpeed * 0.6F, living.zza * dSpeed, -jump);
        } else if (dir == Direction.EAST) {
            return new Vec3(-jump, living.zza * dSpeed, -living.xxa * dSpeed * 0.6F);
        } else if (dir == Direction.WEST) {
            return new Vec3(jump, living.zza * dSpeed, living.xxa * dSpeed * 0.6F);
        }
        return living.getDeltaMovement();
    }

    public static Vec3 getEntityMagneticDelta(Entity entity) {
        if (entity instanceof MagneticEntityAccessor magnetic) {
            float f1 = magnetic.getMagneticDeltaX();
            float f2 = magnetic.getMagneticDeltaY();
            float f3 = magnetic.getMagneticDeltaZ();
            if (f1 != 0.0 || f2 != 0.0 || f3 != 0.0) {
                return new Vec3(f1, f2, f3);
            }
        }
        return Vec3.ZERO;
    }

    public static void setEntityMagneticDelta(Entity entity, Vec3 vec3) {
        if (entity instanceof MagneticEntityAccessor magnetic) {
            magnetic.setMagneticDeltaX((float) vec3.x);
            magnetic.setMagneticDeltaY((float) vec3.y);
            magnetic.setMagneticDeltaZ((float) vec3.z);
        }
    }

    public static Direction getEntityMagneticDirection(Entity entity) {
        if (entity instanceof MagneticEntityAccessor magnetic && AlexsCaves.COMMON_CONFIG.walkingOnMagnets.get()) {
            return magnetic.getMagneticAttachmentFace();
        }
        return Direction.DOWN;
    }

    public static void setEntityMagneticDirection(Entity entity, Direction direction) {
        if (entity instanceof MagneticEntityAccessor magnetic) {
            magnetic.setMagneticAttachmentFace(direction);
        }
    }

    private static Direction getStandingOnMagnetSurface(Entity entity) {
        if (entity.isShiftKeyDown()) {
            return Direction.DOWN;
        }
        for (Direction dir : Direction.values()) {
            BlockPos offsetPos = getSamplePosForDirection(entity, dir, 0.1F);
            BlockState blockState = entity.level().getBlockState(offsetPos);
            if (blockState.is(ACTagRegistry.MAGNETIC_ATTACHABLES)) {
                return dir;
            }
        }
        return null;
    }

    private static Direction calculateClosestDirection(Entity entity) {
        Direction closestDirection = Direction.DOWN;
        if (entity.isShiftKeyDown()) {
            return Direction.DOWN;
        }
        double closestDistance = entity.getBbHeight() + entity.getBbWidth();
        Vec3 sampleCenter = new Vec3(entity.getX(), entity.getY(0.5F), entity.getZ());
        for (Direction dir : Direction.values()) {
            BlockPos offsetPos = getSamplePosForDirection(entity, dir, 0.50001F);
            BlockState blockState = entity.level().getBlockState(offsetPos);
            Vec3 offset = Vec3.atCenterOf(offsetPos);
            double dist = sampleCenter.distanceTo(offset);

            if ((closestDistance > dist || dir == Direction.UP) && blockState.is(ACTagRegistry.MAGNETIC_ATTACHABLES)) {
                closestDistance = dist;
                closestDirection = dir;
                if (dir == Direction.UP) {
                    break;
                }
            }
        }
        return closestDirection;
    }

    private static BlockPos getSamplePosForDirection(Entity entity, Direction direction, float expand) {
        switch (direction) {
            case DOWN:
                return BlockPos.containing(entity.getX(), entity.getBoundingBox().minY - expand, entity.getZ());
            case UP:
                return BlockPos.containing(entity.getX(), entity.getBoundingBox().maxY + expand, entity.getZ());
            case EAST:
                return BlockPos.containing(entity.getBoundingBox().maxX + expand, entity.getY(), entity.getZ());
            case WEST:
                return BlockPos.containing(entity.getBoundingBox().minX - expand, entity.getY(), entity.getZ());
            case NORTH:
                return BlockPos.containing(entity.getX(), entity.getY(), entity.getBoundingBox().minZ - expand);
            case SOUTH:
                return BlockPos.containing(entity.getX(), entity.getY(), entity.getBoundingBox().maxZ + expand);
        }
        return entity.blockPosition();
    }

    private static boolean isDynamicallyMagnetic(LivingEntity entity, boolean legsOnly) {
        if (entity.hasEffect(ACEffectRegistry.MAGNETIZING.get())) {
            return true;
        } else if (legsOnly) {
            return entity.getItemBySlot(EquipmentSlot.FEET).is(ACTagRegistry.MAGNETIC_ITEMS);
        } else {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (entity.getItemBySlot(slot).is(ACTagRegistry.MAGNETIC_ITEMS)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isSpectatorPlayer(Entity entity){
        return entity instanceof Player player && player.isSpectator();
    }

    public static boolean isPulledByMagnets(Entity entity) {
        if (entity instanceof ItemEntity item) {
            return item.getItem() != null && item.getItem().is(ACTagRegistry.MAGNETIC_ITEMS);
        } else if (entity instanceof LivingEntity living && isDynamicallyMagnetic(living, false) && !isSpectatorPlayer(entity)) {
            return true;
        } else if (entity instanceof FallingBlockEntity block) {
            return block.getBlockState() != null && block.getBlockState().is(ACTagRegistry.MAGNETIC_BLOCKS);
        }
        return entity.getType().is(ACTagRegistry.MAGNETIC_ENTITIES);
    }

    public static boolean attachesToMagnets(Entity entity) {
        if (entity instanceof LivingEntity living && isDynamicallyMagnetic(living, true) && !living.isShiftKeyDown()) {
            return true;
        }
        return entity.getType().is(ACTagRegistry.MAGNETIC_ENTITIES);
    }

    public static void rotateHead(LivingEntity entity) {
        Direction direction = getEntityMagneticDirection(entity);

        if (direction == Direction.UP) {
            float f = entity.getYHeadRot() - entity.yBodyRot;
            float f1 = entity.yHeadRotO - entity.yBodyRotO;
            entity.setYHeadRot(entity.yBodyRot - f);
            entity.yHeadRotO = entity.yBodyRotO - f1;
            entity.setXRot(180 - entity.getXRot());
            entity.xRotO = 180 - entity.xRotO;
        } else if (direction != Direction.DOWN) {
            float f = entity instanceof Player ? 90 : 0;
            entity.setXRot(entity.getXRot() + f);
            entity.xRotO = entity.xRotO + f;

        }
    }

    public static Vec3 getEyePositionForAttachment(Entity entity, Direction face, float partialTicks) {
        float progress = ((MagneticEntityAccessor) entity).getAttachmentProgress(partialTicks);
        double eyeHeight = entity.getEyeHeight(Pose.STANDING);
        double d0 = Mth.lerp((double) partialTicks, entity.xo, entity.getX());
        double d1 = Mth.lerp((double) partialTicks, entity.yo, entity.getY());
        double d2 = Mth.lerp((double) partialTicks, entity.zo, entity.getZ());
        Vec3 offset = new Vec3(-face.getStepX() * eyeHeight, -face.getStepY() * eyeHeight, -face.getStepZ() * eyeHeight);
        Vec3 from = new Vec3(d0, d1 + eyeHeight, d2);
        Vec3 to = new Vec3(d0, d1, d2).add(offset);
        return from.add(to.subtract(from).scale(progress));
    }

    public static List<VoxelShape> getMovingBlockCollisions(@Nullable Entity entity, AABB aabb) {
        if (aabb.getSize() < 1.0E-7D) {
            return List.of();
        } else {
            List<AbstractMovingBlockEntity> list = entity.level().getEntitiesOfClass(AbstractMovingBlockEntity.class, aabb.inflate(1.0E-7D), AbstractMovingBlockEntity::movesEntities);
            if (list.isEmpty()) {
                return List.of();
            } else {
                List<VoxelShape> shapes = new ArrayList<>();
                for (AbstractMovingBlockEntity metalEntity : list) {
                    if (metalEntity != entity) {
                        shapes.add(metalEntity.getShape());
                    }
                }
                return shapes;
            }
        }
    }

    public static void turnEntityOnMagnet(Entity entity, double xBy, double yBy, Direction magneticAttachmentFace) {
        float progress = ((MagneticEntityAccessor) entity).getAttachmentProgress(AlexsCaves.PROXY.getPartialTicks());
        float f = (float) xBy * 0.15F;
        float f1 = (float) yBy * 0.15F * (magneticAttachmentFace == Direction.UP ? -1F : 1F);
        float magnetOffset = (magneticAttachmentFace == Direction.UP ? -180 : -90) * progress;
        if (progress > 0.0 && progress < 1.0F) {
            entity.setXRot(magnetOffset);
        } else {
            entity.setXRot(entity.getXRot() + f);
        }
        entity.setYRot(entity.getYRot() + f1);
        entity.setXRot(Mth.clamp(entity.getXRot(), -90.0F + magnetOffset, 90.0F + magnetOffset));
        entity.xRotO += f;
        entity.yRotO += f1;
        entity.xRotO = Mth.clamp(entity.xRotO, -90.0F + magnetOffset, 90.0F + magnetOffset);
    }

    public static AABB rotateBoundingBox(EntityDimensions dimensions, Direction dir, Vec3 position) {
        float usualWidth = dimensions.width * 0.5F;
        switch (dir) {
            case NORTH:
                return new AABB(dimensions.width * -0.5F, dimensions.width * -0.5F, -usualWidth, dimensions.width * 0.5F, dimensions.width * 0.5F, dimensions.height - usualWidth).move(position);
            case SOUTH:
                return new AABB(dimensions.width * -0.5F, dimensions.width * -0.5F, -dimensions.height + usualWidth, dimensions.width * 0.5F, dimensions.width * 0.5F, usualWidth).move(position);
            case EAST:
                return new AABB(-dimensions.height + usualWidth, dimensions.width * -0.5F, dimensions.width * -0.5F, usualWidth, dimensions.width * 0.5F, dimensions.width * 0.5F).move(position);
            case WEST:
                return new AABB(-usualWidth, dimensions.width * -0.5F, dimensions.width * -0.5F, dimensions.height - usualWidth, dimensions.width * 0.5F, dimensions.width * 0.5F).move(position);
        }
        return dimensions.makeBoundingBox(position);
    }
}