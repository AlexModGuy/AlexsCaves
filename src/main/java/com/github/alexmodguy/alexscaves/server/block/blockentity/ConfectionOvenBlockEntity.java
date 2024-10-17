package com.github.alexmodguy.alexscaves.server.block.blockentity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.ConfectionOvenBlock;
import com.github.alexmodguy.alexscaves.server.block.poi.ACPOIRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.GingerbreadManEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.ActivatesSirens;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ConfectionOvenBlockEntity extends BlockEntity {


    private int cooldown;
    private int gingerbreadSpawns;
    private int spawnIteratesBy = 1;

    private int gingerbreadTeamColor;
    private boolean wasPowered;

    private static final int COOKING_TIME = 100;
    private static final int COOLDOWN_TIME = 300;
    private static final int ARMY_SIZE = 5;

    private List<GingerbreadManEntity> spawnedGingerbreads = new ArrayList<>();

    public ConfectionOvenBlockEntity(BlockPos pos, BlockState state) {
        super(ACBlockEntityRegistry.CONFECTION_OVEN.get(), pos, state);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, ConfectionOvenBlockEntity entity) {
        boolean powered = entity.isActivated(state);
        if(powered && entity.cooldown == 0){
            entity.cooldown = -COOKING_TIME;
            DyeColor dyeColor = getDyeColorFromRockCandy(level, blockPos, state);
            if(dyeColor != null){
                entity.gingerbreadTeamColor = dyeColor.getTextColor();
            }else{
                entity.gingerbreadTeamColor = (int)(Math.random() * 0XFFFFFF);
            }
            entity.gingerbreadSpawns = ARMY_SIZE;
            entity.spawnIteratesBy = COOKING_TIME / ARMY_SIZE;
        }
        if (entity.wasPowered != powered) {
            entity.wasPowered = powered;
            entity.setChanged();
        }
        boolean flag = false;
        if(entity.cooldown < 0){
            entity.cooldown++;
            entity.makeGingerbreadMen();
            if(level.random.nextFloat() < 0.55F){
                makeParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, level, blockPos, state, true);
            }
            if(entity.cooldown == 0){
                entity.restoreAllGingerbreadMenCooldown();
                entity.cooldown = COOLDOWN_TIME;
            }
        }
        if(entity.cooldown > 0){
            if(level.random.nextFloat() < 0.55F) {
                makeParticles(ParticleTypes.SMOKE, level, blockPos, state, false);
            }
            entity.cooldown--;
        }

        int cookState = state.getValue(ConfectionOvenBlock.COOKSTATE);
        int targetCookState = entity.cooldown < 0 ? 1 : entity.cooldown > 0 ? 2 : 0;
        if(cookState != targetCookState){
            level.setBlock(blockPos, state.setValue(ConfectionOvenBlock.COOKSTATE, targetCookState), 3);
        }
    }
    private void makeGingerbreadMen() {
        if(cooldown % spawnIteratesBy == 0 && gingerbreadSpawns > 0){
            GingerbreadManEntity gingerbreadMan = ACEntityRegistry.GINGERBREAD_MAN.get().create(level);
            Direction facing = getBlockState().getValue(ConfectionOvenBlock.FACING);
            Vec3 spawnVec = rotateCenteredVec(new Vec3(0, -0.25F, -0.7F), facing);
            gingerbreadMan.setPos(spawnVec.add(this.getBlockPos().getCenter()));
            gingerbreadMan.setDeltaMovement(spawnVec.scale(0.25F).add(0, 0.3, 0));
            gingerbreadMan.setYRot(facing.toYRot());
            gingerbreadMan.setVariant(level.random.nextInt(GingerbreadManEntity.MAX_VARIANTS + 1));
            gingerbreadMan.setOvenSpawned(true);
            gingerbreadMan.setGingerbreadTeamColor(gingerbreadTeamColor);
            gingerbreadMan.setDespawnFromOvenCooldown(COOKING_TIME);
            if(!level.isClientSide){
                gingerbreadMan.hasImpulse = true;
                level.addFreshEntity(gingerbreadMan);
            }
            spawnedGingerbreads.add(gingerbreadMan);
            gingerbreadSpawns--;
        }
    }

    private void restoreAllGingerbreadMenCooldown() {
        for(GingerbreadManEntity gingerbreadMan : spawnedGingerbreads){
            gingerbreadMan.setDespawnFromOvenCooldown(COOLDOWN_TIME);
        }
        spawnedGingerbreads.clear();
    }


    public boolean isActivated(BlockState state) {
        return state.is(ACBlockRegistry.CONFECTION_OVEN.get()) && state.getValue(ConfectionOvenBlock.POWERED);
    }

    @Nullable
    public static DyeColor getDyeColorFromRockCandy(Level level, BlockPos pos, BlockState blockState){
        Direction facing = blockState.getValue(ConfectionOvenBlock.FACING);
        BlockPos beneathPos = facing == Direction.UP ? pos.south() : facing == Direction.DOWN ? pos.north() : pos.below();
        BlockState state = level.getBlockState(beneathPos);
        if(state.is(ACTagRegistry.ROCK_CANDIES)){
            if(state.is(ACBlockRegistry.WHITE_ROCK_CANDY.get())){
                return DyeColor.WHITE;
            }
            if(state.is(ACBlockRegistry.ORANGE_ROCK_CANDY.get())){
                return DyeColor.ORANGE;
            }
            if(state.is(ACBlockRegistry.MAGENTA_ROCK_CANDY.get())){
                return DyeColor.MAGENTA;
            }
            if(state.is(ACBlockRegistry.LIGHT_BLUE_ROCK_CANDY.get())){
                return DyeColor.LIGHT_BLUE;
            }
            if(state.is(ACBlockRegistry.YELLOW_ROCK_CANDY.get())){
                return DyeColor.YELLOW;
            }
            if(state.is(ACBlockRegistry.PINK_ROCK_CANDY.get())){
                return DyeColor.PINK;
            }
            if(state.is(ACBlockRegistry.GRAY_ROCK_CANDY.get())){
                return DyeColor.GRAY;
            }
            if(state.is(ACBlockRegistry.LIGHT_GRAY_ROCK_CANDY.get())){
                return DyeColor.LIGHT_GRAY;
            }
            if(state.is(ACBlockRegistry.CYAN_ROCK_CANDY.get())){
                return DyeColor.CYAN;
            }
            if(state.is(ACBlockRegistry.PURPLE_ROCK_CANDY.get())){
                return DyeColor.PURPLE;
            }
            if(state.is(ACBlockRegistry.BLUE_ROCK_CANDY.get())){
                return DyeColor.BLUE;
            }
            if(state.is(ACBlockRegistry.BROWN_ROCK_CANDY.get())){
                return DyeColor.BROWN;
            }
            if(state.is(ACBlockRegistry.GREEN_ROCK_CANDY.get())){
                return DyeColor.GREEN;
            }
            if(state.is(ACBlockRegistry.RED_ROCK_CANDY.get())){
                return DyeColor.RED;
            }
            if(state.is(ACBlockRegistry.BLACK_ROCK_CANDY.get())){
                return DyeColor.BLACK;
            }
        }
        return null;
    }

    private static void makeParticles(ParticleOptions particleOptions, Level level, BlockPos pos, BlockState blockState, boolean top){
        Direction facing = blockState.getValue(ConfectionOvenBlock.FACING);
        Vec3 offset = top ? new Vec3(0.4F * (level.random.nextFloat() - 0.5F), 0.5F, 0.4F * 0.4F * (level.random.nextFloat() - 0.5F)) : new Vec3(0.4F * (level.random.nextFloat() - 0.5F), -0.15F, -0.5F - 0.1F * level.random.nextFloat());
        Vec3 rotate = rotateCenteredVec(offset, facing);
        level.addParticle(particleOptions, rotate.x + 0.5F + pos.getX(), rotate.y + 0.5F + pos.getY(), rotate.z + 0.5F + pos.getZ(), 0, 0.025F + level.random.nextFloat() * 0.01F, 0);
    }

    private static Vec3 rotateCenteredVec(Vec3 offset, Direction facing){
        Vec3 rotate = offset;
        switch (facing){
            case DOWN:
                rotate = offset.xRot((float) (Math.PI / 2F));
                break;
            case UP:
                rotate = offset.xRot(-(float) (Math.PI / 2F));
                break;
            case NORTH:
                rotate = offset;
                break;
            case SOUTH:
                rotate = offset.yRot((float) (Math.PI));
                break;
            case WEST:
                rotate = offset.yRot((float) (Math.PI / 2F));
                break;
            case EAST:
                rotate = offset.yRot(-(float) (Math.PI / 2F));
                break;
        }
        return rotate;
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        this.cooldown = tag.getInt("Cooldown");
        this.gingerbreadTeamColor = tag.getInt("TeamColor");
        this.gingerbreadSpawns = tag.getInt("GingerbreadSpawns");
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Cooldown", this.cooldown);
        tag.putInt("TeamColor", this.gingerbreadTeamColor);
        tag.putInt("GingerbreadSpawns", this.gingerbreadSpawns);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        if (packet != null && packet.getTag() != null) {
            this.cooldown = packet.getTag().getInt("Cooldown");
            this.gingerbreadTeamColor = packet.getTag().getInt("TeamColor");
            this.gingerbreadSpawns = packet.getTag().getInt("GingerbreadSpawns");
        }
    }
}
