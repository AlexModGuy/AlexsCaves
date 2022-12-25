package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.item.CustomTabBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.CreativeModeTabEvent;

import java.util.List;

public class ACCreativeTabs {

    public static final ResourceLocation MAGNETIC_CAVES = new ResourceLocation("alexscaves:magnetic_caves");
    public static final ResourceLocation PRIMORDIAL_CAVES = new ResourceLocation("alexscaves:primordial_caves");

    public static final ResourceLocation TOXIC_CAVES = new ResourceLocation("alexscaves:toxic_caves");


    private static void add(CreativeModeTab.Output tab, ItemLike itemLike){
        if(itemLike instanceof CustomTabBehavior customTabBehavior){
            customTabBehavior.fillItemCategory(tab);
        }else{
            tab.accept(itemLike);
        }
    }
    public static void registerTabs(CreativeModeTabEvent.Register event){
        event.registerCreativeModeTab(MAGNETIC_CAVES, builder -> builder.title(Component.translatable("itemGroup.alexscaves.magnetic_caves")).icon(() -> new ItemStack(ACBlockRegistry.SCARLET_MAGNET.get())).displayItems((flags, output, isOp) -> {
            ACItemRegistry.getSpawnEggsForTab(MAGNETIC_CAVES).forEach((spawnEgg -> add(output, spawnEgg.get())));
            add(output, ACItemRegistry.RAW_SCARLET_NEODYMIUM.get());
            add(output, ACItemRegistry.SCARLET_NEODYMIUM_INGOT.get());
            add(output, ACItemRegistry.RAW_AZURE_NEODYMIUM.get());
            add(output, ACItemRegistry.AZURE_NEODYMIUM_INGOT.get());
            add(output, ACBlockRegistry.GALENA.get());
            add(output, ACBlockRegistry.GALENA_STAIRS.get());
            add(output, ACBlockRegistry.GALENA_SLAB.get());
            add(output, ACBlockRegistry.GALENA_BRICKS.get());
            add(output, ACBlockRegistry.GALENA_WALL.get());
            add(output, ACBlockRegistry.PACKED_GALENA.get());
            add(output, ACBlockRegistry.GALENA_BRICKS.get());
            add(output, ACBlockRegistry.GALENA_BRICK_STAIRS.get());
            add(output, ACBlockRegistry.GALENA_BRICK_SLAB.get());
            add(output, ACBlockRegistry.GALENA_BRICK_WALL.get());
            add(output, ACBlockRegistry.GALENA_IRON_ORE.get());
            add(output, ACBlockRegistry.SCRAP_METAL.get());
            add(output, ACBlockRegistry.METAL_SWARF.get());
            add(output, ACBlockRegistry.SCARLET_NEODYMIUM_NODE.get());
            add(output, ACBlockRegistry.SCARLET_NEODYMIUM_PILLAR.get());
            add(output, ACBlockRegistry.BLOCK_OF_SCARLET_NEODYMIUM.get());
            add(output, ACBlockRegistry.SCARLET_MAGNET.get());
            add(output, ACBlockRegistry.AZURE_NEODYMIUM_NODE.get());
            add(output, ACBlockRegistry.AZURE_NEODYMIUM_PILLAR.get());
            add(output, ACBlockRegistry.BLOCK_OF_AZURE_NEODYMIUM.get());
            add(output, ACBlockRegistry.AZURE_MAGNET.get());
        }));
        event.registerCreativeModeTab(PRIMORDIAL_CAVES, builder -> builder.title(Component.translatable("itemGroup.alexscaves.primordial_caves")).icon(() -> new ItemStack(ACBlockRegistry.FLYTRAP.get())).displayItems((flags, output, isOp) -> {
            ACItemRegistry.getSpawnEggsForTab(PRIMORDIAL_CAVES).forEach((spawnEgg -> add(output, spawnEgg.get())));
            add(output, ACBlockRegistry.AMBER.get());
            add(output, ACBlockRegistry.AMBERSOL.get());
            add(output, ACBlockRegistry.LIMESTONE.get());
            add(output, ACBlockRegistry.LIMESTONE_STAIRS.get());
            add(output, ACBlockRegistry.LIMESTONE_SLAB.get());
            add(output, ACBlockRegistry.LIMESTONE_WALL.get());
            add(output, ACBlockRegistry.SMOOTH_LIMESTONE.get());
            add(output, ACBlockRegistry.SMOOTH_LIMESTONE_STAIRS.get());
            add(output, ACBlockRegistry.SMOOTH_LIMESTONE_SLAB.get());
            add(output, ACBlockRegistry.SMOOTH_LIMESTONE_WALL.get());
            add(output, ACBlockRegistry.CAVE_PAINTING_AMBERSOL.get());
            add(output, ACBlockRegistry.CAVE_PAINTING_DARK.get());
            add(output, ACBlockRegistry.CAVE_PAINTING_FOOTPRINT.get());
            add(output, ACBlockRegistry.CAVE_PAINTING_FOOTPRINTS.get());
            add(output, ACBlockRegistry.CAVE_PAINTING_TREE_STARS.get());
            add(output, ACBlockRegistry.CAVE_PAINTING_PEWEN.get());
            add(output, ACBlockRegistry.CAVE_PAINTING_TRILOCARIS.get());
            add(output, ACBlockRegistry.CAVE_PAINTING_GROTTOCERATOPS.get());
            add(output, ACBlockRegistry.CAVE_PAINTING_GROTTOCERATOPS_FRIEND.get());
            add(output, ACBlockRegistry.CAVE_PAINTING_DINO_NUGGETS.get());
            add(output, ACBlockRegistry.CAVE_PAINTING_VALLUMRAPTOR_CHEST.get());
            add(output, ACBlockRegistry.CAVE_PAINTING_VALLUMRAPTOR_FRIEND.get());
            add(output, ACBlockRegistry.CAVE_PAINTING_RELICHEIRUS.get());
            add(output, ACBlockRegistry.CAVE_PAINTING_RELICHEIRUS_SLASH.get());
            add(output, ACBlockRegistry.CAVE_PAINTING_ENDERMAN.get());
            add(output, ACBlockRegistry.CAVE_PAINTING_PORTAL.get());
            add(output, ACBlockRegistry.CAVE_PAINTING_SUBTERRANODON.get());
            add(output, ACBlockRegistry.CAVE_PAINTING_SUBTERRANODON_RIDE.get());
            add(output, ACBlockRegistry.CAVE_PAINTING_TREMORSAURUS.get());
            add(output, ACBlockRegistry.CAVE_PAINTING_TREMORSAURUS_FRIEND.get());
            add(output, ACBlockRegistry.PEWEN_LOG.get());
            add(output, ACBlockRegistry.PEWEN_BRANCH.get());
            add(output, ACBlockRegistry.PEWEN_SAPLING.get());
            add(output, ACBlockRegistry.PEWEN_PINES.get());
            add(output, ACBlockRegistry.PEWEN_WOOD.get());
            add(output, ACBlockRegistry.STRIPPED_PEWEN_LOG.get());
            add(output, ACBlockRegistry.STRIPPED_PEWEN_WOOD.get());
            add(output, ACBlockRegistry.PEWEN_PLANKS.get());
            add(output, ACBlockRegistry.PEWEN_PLANKS_STAIRS.get());
            add(output, ACBlockRegistry.PEWEN_PLANKS_SLAB.get());
            add(output, ACBlockRegistry.PEWEN_PLANKS_FENCE.get());
            add(output, ACItemRegistry.PEWEN_SIGN.get());
            add(output, ACBlockRegistry.PEWEN_PRESSURE_PLATE.get());
            add(output, ACItemRegistry.PEWEN_DOOR.get());
            add(output, ACBlockRegistry.PEWEN_TRAPDOOR.get());
            add(output, ACBlockRegistry.PEWEN_BUTTON.get());
            add(output, ACBlockRegistry.PEWEN_FENCE_GATE.get());
            add(output, ACBlockRegistry.CURLY_FERN.get());
            add(output, ACBlockRegistry.FLYTRAP.get());
            add(output, ACBlockRegistry.CYCAD.get());
            add(output, ACBlockRegistry.ARCHAIC_VINE.get());
            add(output, ACBlockRegistry.ANCIENT_LEAVES.get());
            add(output, ACBlockRegistry.TREE_STAR.get());
        }));
        event.registerCreativeModeTab(TOXIC_CAVES, builder -> builder.title(Component.translatable("itemGroup.alexscaves.toxic_caves")).icon(() -> new ItemStack(ACBlockRegistry.RADROCK_URANIUM_ORE.get())).displayItems((flags, output, isOp) -> {
            ACItemRegistry.getSpawnEggsForTab(TOXIC_CAVES).forEach((spawnEgg -> add(output, spawnEgg.get())));
            add(output, ACItemRegistry.ACID_BUCKET.get());
            add(output, ACBlockRegistry.RADROCK.get());
            add(output, ACBlockRegistry.RADROCK_URANIUM_ORE.get());
        }));
    }

}
