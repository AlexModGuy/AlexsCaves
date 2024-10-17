package com.github.alexmodguy.alexscaves.server.block.fluid;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidInteractionRegistry;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ACFluidRegistry {
    public static final DeferredRegister<FluidType> FLUID_TYPE_DEF_REG = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, AlexsCaves.MODID);
    public static final DeferredRegister<Fluid> FLUID_DEF_REG = DeferredRegister.create(ForgeRegistries.FLUIDS, AlexsCaves.MODID);

    private static ForgeFlowingFluid.Properties acidProperties() {
        return new ForgeFlowingFluid.Properties(ACID_FLUID_TYPE, ACID_FLUID_SOURCE, ACID_FLUID_FLOWING).bucket(ACItemRegistry.ACID_BUCKET).block(ACBlockRegistry.ACID);
    }

    private static ForgeFlowingFluid.Properties purpleSodaProperties() {
        return new ForgeFlowingFluid.Properties(PURPLE_SODA_FLUID_TYPE, PURPLE_SODA_FLUID_SOURCE, PURPLE_SODA_FLUID_FLOWING).bucket(ACItemRegistry.PURPLE_SODA_BUCKET).block(ACBlockRegistry.PURPLE_SODA);
    }

    public static final RegistryObject<FluidType> ACID_FLUID_TYPE = FLUID_TYPE_DEF_REG.register("acid", () -> new AcidFluidType(FluidType.Properties.create().lightLevel(5).density(1024).viscosity(1024).pathType(BlockPathTypes.LAVA).adjacentPathType(BlockPathTypes.DANGER_OTHER).sound(SoundActions.BUCKET_EMPTY, ACSoundRegistry.ACID_UNSUBMERGE.get()).sound(SoundActions.BUCKET_FILL, ACSoundRegistry.ACID_SUBMERGE.get())));
    public static final RegistryObject<FlowingFluid> ACID_FLUID_SOURCE = FLUID_DEF_REG.register("acid", () -> new ForgeFlowingFluid.Source(acidProperties()));
    public static final RegistryObject<FlowingFluid> ACID_FLUID_FLOWING = FLUID_DEF_REG.register("acid_flowing", () -> new ForgeFlowingFluid.Flowing(acidProperties()));

    public static final RegistryObject<FluidType> PURPLE_SODA_FLUID_TYPE = FLUID_TYPE_DEF_REG.register("purple_soda", () -> new PurpleSodaFluidType(FluidType.Properties.create().density(1000).viscosity(1000).pathType(BlockPathTypes.WATER).adjacentPathType(BlockPathTypes.WATER_BORDER).sound(SoundActions.BUCKET_EMPTY, ACSoundRegistry.PURPLE_SODA_UNSUBMERGE.get()).sound(SoundActions.BUCKET_FILL, ACSoundRegistry.PURPLE_SODA_SUBMERGE.get())));
    public static final RegistryObject<FlowingFluid> PURPLE_SODA_FLUID_SOURCE = FLUID_DEF_REG.register("purple_soda", () -> new ForgeFlowingFluid.Source(purpleSodaProperties()));
    public static final RegistryObject<FlowingFluid> PURPLE_SODA_FLUID_FLOWING = FLUID_DEF_REG.register("purple_soda_flowing", () -> new ForgeFlowingFluid.Flowing(purpleSodaProperties()));

    public static void postInit() {
        FluidInteractionRegistry.addInteraction(ACID_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                ForgeMod.WATER_TYPE.get(),
                fluidState -> Blocks.MUD.defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(ACID_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                ForgeMod.LAVA_TYPE.get(),
                fluidState -> ACBlockRegistry.RADROCK.get().defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(ForgeMod.WATER_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                ACID_FLUID_TYPE.get(),
                fluidState -> Blocks.MUD.defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                ACID_FLUID_TYPE.get(),
                fluidState -> ACBlockRegistry.RADROCK.get().defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(PURPLE_SODA_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                ForgeMod.WATER_TYPE.get(),
                fluidState -> ACBlockRegistry.BLUE_ROCK_CANDY.get().defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(PURPLE_SODA_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                ForgeMod.LAVA_TYPE.get(),
                fluidState -> ACBlockRegistry.ORANGE_ROCK_CANDY.get().defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(PURPLE_SODA_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                ACID_FLUID_TYPE.get(),
                fluidState -> ACBlockRegistry.GREEN_ROCK_CANDY.get().defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(ForgeMod.WATER_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                PURPLE_SODA_FLUID_TYPE.get(),
                fluidState -> ACBlockRegistry.BLUE_ROCK_CANDY.get().defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                PURPLE_SODA_FLUID_TYPE.get(),
                fluidState -> ACBlockRegistry.ORANGE_ROCK_CANDY.get().defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(ACID_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                PURPLE_SODA_FLUID_TYPE.get(),
                fluidState -> ACBlockRegistry.GREEN_ROCK_CANDY.get().defaultBlockState()
        ));
    }
}
