package com.github.alexmodguy.alexscaves.server.potion;


import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
    public static final RegistryObject<MobEffect> DARKNESS_INCARNATE = DEF_REG.register("darkness_incarnate", () -> new DarknessIncarnateEffect());
    public static final RegistryObject<MobEffect> SUGAR_RUSH = DEF_REG.register("sugar_rush", () -> new SugarRushEffect());
    public static final RegistryObject<Potion> MAGNETIZING_POTION = POTION_DEF_REG.register("magnetizing", () -> new Potion(new MobEffectInstance(MAGNETIZING.get(), 3600)));
    public static final RegistryObject<Potion> LONG_MAGNETIZING_POTION = POTION_DEF_REG.register("long_magnetizing", () -> new Potion(new MobEffectInstance(MAGNETIZING.get(), 9600)));
    public static final RegistryObject<Potion> DEEPSIGHT_POTION = POTION_DEF_REG.register("deepsight", () -> new Potion(new MobEffectInstance(DEEPSIGHT.get(), 3600)));
    public static final RegistryObject<Potion> LONG_DEEPSIGHT_POTION = POTION_DEF_REG.register("long_deepsight", () -> new Potion(new MobEffectInstance(DEEPSIGHT.get(), 9600)));
    public static final RegistryObject<Potion> GLOWING_POTION = POTION_DEF_REG.register("glowing", () -> new Potion(new MobEffectInstance(MobEffects.GLOWING, 3600)));
    public static final RegistryObject<Potion> LONG_GLOWING_POTION = POTION_DEF_REG.register("long_glowing", () -> new Potion(new MobEffectInstance(MobEffects.GLOWING, 9600)));
    public static final RegistryObject<Potion> HASTE_POTION = POTION_DEF_REG.register("haste", () -> new Potion(new MobEffectInstance(MobEffects.DIG_SPEED, 3600)));
    public static final RegistryObject<Potion> LONG_HASTE_POTION = POTION_DEF_REG.register("long_haste", () -> new Potion(new MobEffectInstance(MobEffects.DIG_SPEED, 9600)));
    public static final RegistryObject<Potion> STRONG_HASTE_POTION = POTION_DEF_REG.register("strong_haste", () -> new Potion(new MobEffectInstance(MobEffects.DIG_SPEED, 1800, 1)));
    public static final RegistryObject<Potion> STRONG_HUNGER_POTION = POTION_DEF_REG.register("strong_hunger", () -> new Potion(new MobEffectInstance(MobEffects.HUNGER, 1800, 4)));
    public static final RegistryObject<Potion> SUGAR_RUSH_POTION = POTION_DEF_REG.register("sugar_rush", () -> new Potion(new MobEffectInstance(SUGAR_RUSH.get(), 1800)));
    public static final RegistryObject<Potion> LONG_SUGAR_RUSH_POTION = POTION_DEF_REG.register("long_sugar_rush", () -> new Potion(new MobEffectInstance(SUGAR_RUSH.get(), 3600)));


    public static void setup() {
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(Potions.AWKWARD)), Ingredient.of(ACItemRegistry.FERROUSLIME_BALL.get()), createPotion(MAGNETIZING_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(MAGNETIZING_POTION)), Ingredient.of(Items.REDSTONE), createPotion(LONG_MAGNETIZING_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(Potions.AWKWARD)), Ingredient.of(ACItemRegistry.LANTERNFISH.get()), createPotion(DEEPSIGHT_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(DEEPSIGHT_POTION)), Ingredient.of(Items.REDSTONE), createPotion(LONG_DEEPSIGHT_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(Potions.AWKWARD)), Ingredient.of(ACItemRegistry.BIOLUMINESSCENCE.get()), createPotion(GLOWING_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(GLOWING_POTION)), Ingredient.of(Items.REDSTONE), createPotion(LONG_GLOWING_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(Potions.AWKWARD)), Ingredient.of(ACItemRegistry.CORRODENT_TEETH.get()), createPotion(HASTE_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(HASTE_POTION)), Ingredient.of(Items.REDSTONE), createPotion(LONG_HASTE_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(HASTE_POTION)), Ingredient.of(Items.GLOWSTONE_DUST), createPotion(STRONG_HASTE_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(Potions.STRONG_SWIFTNESS)), Ingredient.of(ACItemRegistry.SWEET_TOOTH.get()), createPotion(SUGAR_RUSH_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(SUGAR_RUSH_POTION)), Ingredient.of(Items.REDSTONE), createPotion(LONG_SUGAR_RUSH_POTION)));
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

    public static ItemStack createLingeringPotion(Potion potion) {
        return PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), potion);
    }

    public static ItemStack createJellybean(Potion potion) {
        return PotionUtils.setPotion(new ItemStack(ACItemRegistry.JELLY_BEAN.get()), potion);
    }
}
