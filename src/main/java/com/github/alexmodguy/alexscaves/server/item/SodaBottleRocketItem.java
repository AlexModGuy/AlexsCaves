package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.server.entity.item.SodaBottleRocketEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SodaBottleRocketItem extends Item {

    public SodaBottleRocketItem() {
        super(new Item.Properties());
    }

    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (!world.isClientSide) {
            ItemStack itemstack = context.getItemInHand();
            Vec3 vector3d = context.getClickLocation();
            Direction direction = context.getClickedFace();
            SodaBottleRocketEntity fireworkrocketentity = new SodaBottleRocketEntity(world, context.getPlayer(), vector3d.x + (double)direction.getStepX() * 0.15D, vector3d.y + (double)direction.getStepY() * 0.15D, vector3d.z + (double)direction.getStepZ() * 0.15D, itemstack);
            world.addFreshEntity(fireworkrocketentity);
            if(!context.getPlayer().isCreative()){
                itemstack.shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (playerIn.isFallFlying()) {
            ItemStack itemstack = playerIn.getItemInHand(handIn);
            if (!worldIn.isClientSide) {
                worldIn.addFreshEntity(new SodaBottleRocketEntity(worldIn, itemstack, playerIn));
                if (!playerIn.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
            }

            return InteractionResultHolder.sidedSuccess(playerIn.getItemInHand(handIn), worldIn.isClientSide());
        } else {
            return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
        }
    }

}
