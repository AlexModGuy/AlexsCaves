package com.github.alexmodguy.alexscaves.server.potion;


import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ACEffectRegistry {

    public static final DeferredRegister<MobEffect> DEF_REG = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, AlexsCaves.MODID);
    public static final DeferredRegister<Potion> POTION_DEF_REG = DeferredRegister.create(ForgeRegistries.POTIONS, AlexsCaves.MODID);
    public static final RegistryObject<MobEffect> MAGNETIZING = DEF_REG.register("magnetizing", () -> new MagnetizedEffect());
    public static final RegistryObject<MobEffect> STUNNED = DEF_REG.register("stunned", () -> new StunnedEffect());
    public static final RegistryObject<MobEffect> RAGE = DEF_REG.register("rage", () -> new RageEffect());
    public static final RegistryObject<MobEffect> IRRADIATED = DEF_REG.register("irradiated", () -> new IrradiatedEffect());
    public static final RegistryObject<MobEffect> BUBBLED = DEF_REG.register("bubbled", () -> new BubbledEffect());
    public static final RegistryObject<MobEffect> DEEPSIGHT = DEF_REG.register("deepsight", () -> new DeepsightEffect());
    public static final RegistryObject<Potion> MAGNETIZING_POTION = POTION_DEF_REG.register("magnetizing", () -> new Potion(new MobEffectInstance(MAGNETIZING.get(), 3600)));
    public static final RegistryObject<Potion> LONG_MAGNETIZING_POTION = POTION_DEF_REG.register("long_magnetizing", () -> new Potion(new MobEffectInstance(MAGNETIZING.get(), 9600)));
    public static final RegistryObject<Potion> DEEPSIGHT_POTION = POTION_DEF_REG.register("deepsight", () -> new Potion(new MobEffectInstance(DEEPSIGHT.get(), 3600)));
    public static final RegistryObject<Potion> LONG_DEEPSIGHT_POTION = POTION_DEF_REG.register("long_deepsight", () -> new Potion(new MobEffectInstance(DEEPSIGHT.get(), 9600)));

    public static void setup() {
        BrewingRecipeRegistry.addRecipe(Ingredient.of(createPotion(Potions.AWKWARD)), Ingredient.of(ACItemRegistry.FERROUSLIME_BALL.get()), createPotion(MAGNETIZING_POTION));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(MAGNETIZING_POTION)), Ingredient.of(Items.REDSTONE), createPotion(LONG_MAGNETIZING_POTION)));
        BrewingRecipeRegistry.addRecipe(Ingredient.of(createPotion(Potions.AWKWARD)), Ingredient.of(ACItemRegistry.PEARL.get()), createPotion(DEEPSIGHT_POTION));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(DEEPSIGHT_POTION)), Ingredient.of(Items.REDSTONE), createPotion(LONG_DEEPSIGHT_POTION)));
    }

    public static ItemStack createPotion(RegistryObject<Potion> potion) {
        return createPotion(potion.get());
    }

    public static ItemStack createPotion(Potion potion) {
        return PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
    }

    public static ItemStack createSplashPotion(Potion potion) {
        return PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion);
    }
}
