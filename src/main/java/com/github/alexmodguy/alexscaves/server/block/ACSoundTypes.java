package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.util.ForgeSoundType;

public class ACSoundTypes {

    public static final SoundType AMBER = new ForgeSoundType(1.0F, 1.0F, () -> ACSoundRegistry.AMBER_BREAK.get(), () -> ACSoundRegistry.AMBER_STEP.get(), () -> ACSoundRegistry.AMBER_PLACE.get(), () -> ACSoundRegistry.AMBER_BREAKING.get(), () -> ACSoundRegistry.AMBER_STEP.get());

}
