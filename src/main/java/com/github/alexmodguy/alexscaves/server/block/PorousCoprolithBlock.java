package com.github.alexmodguy.alexscaves.server.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class PorousCoprolithBlock extends Block {
    public PorousCoprolithBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.DIRT).requiresCorrectToolForDrops().strength(1.75F, 4.0F).sound(SoundType.CALCITE).noOcclusion());
    }

}
