package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.BeholderBlockEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACAdvancementTriggerRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.world.ForgeChunkManager;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OccultGemItem extends Item {

    public OccultGemItem() {
        super(new Properties());
    }

    public static boolean isActive(ItemStack itemStack) {
        CompoundTag compoundtag = itemStack.getTag();
        return compoundtag != null && (compoundtag.contains("BeholderDimension") || compoundtag.contains("BeholderPos"));
    }

    private static Optional<ResourceKey<Level>> getBeholderDimension(CompoundTag tag) {
        return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, tag.get("BeholderDimension")).result();
    }

    @Nullable
    public static GlobalPos getBeholderPosition(CompoundTag tag) {
        boolean flag = tag.contains("BeholderPos");
        boolean flag1 = tag.contains("BeholderDimension");
        if (flag && flag1) {
            Optional<ResourceKey<Level>> optional = getBeholderDimension(tag);
            if (optional.isPresent()) {
                BlockPos blockpos = NbtUtils.readBlockPos(tag.getCompound("BeholderPos"));
                return GlobalPos.of(optional.get(), blockpos);
            }
        }

        return null;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (isActive(itemstack)) {
            CompoundTag tag = itemstack.getOrCreateTag();
            GlobalPos globalPos = getBeholderPosition(tag);
            if (globalPos != null && globalPos.dimension() != null && globalPos.dimension().equals(level.dimension()) && !level.isClientSide && level instanceof ServerLevel serverLevel) {
                ServerLevel dimensionLevel = serverLevel.getServer().getLevel(globalPos.dimension());
                if (dimensionLevel != null) {
                    loadChunksAround(dimensionLevel, player.getUUID(), globalPos.pos(), true);
                    BlockState blockState = dimensionLevel.getBlockState(globalPos.pos());
                    if (blockState.is(ACBlockRegistry.BEHOLDER.get())) {
                        if(dimensionLevel.getBlockEntity(globalPos.pos()) instanceof BeholderBlockEntity blockEntity){
                            blockEntity.startObserving(dimensionLevel, player);
                        }
                    }
                }
            }
            return InteractionResultHolder.success(itemstack);
        }
        return InteractionResultHolder.pass(itemstack);
    }


    private static void loadChunksAround(ServerLevel serverLevel, UUID ticket, BlockPos center, boolean load) {
        ChunkPos chunkPos = new ChunkPos(center);
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                ForgeChunkManager.forceChunk(serverLevel, AlexsCaves.MODID, ticket, chunkPos.x + i, chunkPos.z + j, load, true);
            }
        }
    }


    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean b) {
        if (!level.isClientSide) {
            if (isActive(itemStack)) {
                CompoundTag compoundtag = itemStack.getOrCreateTag();
                if (compoundtag.contains("BeholderTracked") && !compoundtag.getBoolean("BeholderTracked")) {
                    return;
                }
                Optional<ResourceKey<Level>> optional = getBeholderDimension(compoundtag);
                if (optional.isPresent() && optional.get() == level.dimension() && compoundtag.contains("BeholderPos")) {
                    BlockPos blockpos = NbtUtils.readBlockPos(compoundtag.getCompound("BeholderPos"));
                    boolean flag = false;
                    if ((entity.tickCount + entity.getId()) % 20 == 0) {
                        if (level.isLoaded(blockpos) && !level.getBlockState(blockpos).is(ACBlockRegistry.BEHOLDER.get())) {
                            flag = true;
                        }
                    }
                    if (!level.isInWorldBounds(blockpos) || flag) {
                        compoundtag.remove("BeholderPos");
                        compoundtag.remove("BeholderDimension");
                    }
                }
            }
        }
    }

    public InteractionResult useOn(UseOnContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        if (!level.getBlockState(blockpos).is(ACBlockRegistry.BEHOLDER.get())) {
            return super.useOn(context);
        } else {
            level.playSound((Player) null, blockpos, SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
            Player player = context.getPlayer();
            ItemStack itemstack = context.getItemInHand();
            boolean flag = !player.getAbilities().instabuild && itemstack.getCount() == 1;
            if (flag) {
                this.addBeholderTags(level.dimension(), blockpos, itemstack.getOrCreateTag());
            } else {
                ItemStack itemstack1 = new ItemStack(ACItemRegistry.OCCULT_GEM.get(), 1);
                CompoundTag compoundtag = itemstack.hasTag() ? itemstack.getTag().copy() : new CompoundTag();
                itemstack1.setTag(compoundtag);
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                this.addBeholderTags(level.dimension(), blockpos, compoundtag);
                if (!player.getInventory().add(itemstack1)) {
                    player.drop(itemstack1, false);
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    private void addBeholderTags(ResourceKey<Level> levelResourceKey, BlockPos blockPos, CompoundTag tag) {
        tag.put("BeholderPos", NbtUtils.writeBlockPos(blockPos));
        Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, levelResourceKey).result().ifPresent((p_40731_) -> {
            tag.put("BeholderDimension", p_40731_);
        });
        tag.putBoolean("BeholderTracked", true);
    }

    public boolean isFoil(ItemStack stack) {
        if (stack.getTag() != null && stack.getTag().contains("BeholderPos")) {
            Optional<ResourceKey<Level>> optional = getBeholderDimension(stack.getTag());
            BlockPos blockpos = NbtUtils.readBlockPos(stack.getTag().getCompound("BeholderPos"));
            return optional.isPresent() && blockpos != null;
        }
        return super.isFoil(stack);
    }

    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.getTag() != null && stack.getTag().contains("BeholderPos")) {
            Optional<ResourceKey<Level>> optional = getBeholderDimension(stack.getTag());
            BlockPos blockpos = NbtUtils.readBlockPos(stack.getTag().getCompound("BeholderPos"));
            if (optional.isPresent() && blockpos != null) {
                Component untranslated = Component.translatable("item.alexscaves.occult_gem.desc", blockpos.getX(), blockpos.getY(), blockpos.getZ()).withStyle(ChatFormatting.GRAY);
                tooltip.add(untranslated);
            }
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
