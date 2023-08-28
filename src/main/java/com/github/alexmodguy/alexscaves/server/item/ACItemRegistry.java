package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.*;
import com.github.alexmodguy.alexscaves.server.entity.util.AlexsCavesBoat;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ACItemRegistry {
    private static Map<RegistryObject<Item>, ResourceKey<Biome>> creativeTabSpawnEggMap = new LinkedHashMap<>();
    public static final Rarity RARITY_DEMONIC = Rarity.create("alexscaves:demonic", ChatFormatting.DARK_RED);
    public static final ACArmorMaterial PRIMORDIAL_ARMOR_MATERIAL = new ACArmorMaterial("primordial", 20, new int[]{3, 4, 3, 2}, 25, SoundEvents.ARMOR_EQUIP_LEATHER, 0F);
    public static final ACArmorMaterial HAZMAT_SUIT_ARMOR_MATERIAL = new ACArmorMaterial("hazmat_suit", 20, new int[]{2, 4, 5, 2}, 25, SoundEvents.ARMOR_EQUIP_IRON, 0.5F);
    public static final ACArmorMaterial DIVING_SUIT_ARMOR_MATERIAL = new ACArmorMaterial("diving_suit", 20, new int[]{2, 5, 6, 2}, 25, SoundEvents.ARMOR_EQUIP_IRON, 1F);
    public static final ACArmorMaterial DARKNESS_ARMOR_MATERIAL = new ACArmorMaterial("darkness", 15, new int[]{4, 5, 1, 1}, 50, SoundEvents.ARMOR_EQUIP_LEATHER, 0.5F);
    public static final DeferredRegister<Item> DEF_REG = DeferredRegister.create(ForgeRegistries.ITEMS, AlexsCaves.MODID);
    public static final RegistryObject<Item> ADVANCEMENT_TAB_ICON = DEF_REG.register("advancement_tab_icon", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> CAVE_TABLET = DEF_REG.register("cave_tablet", () -> new CaveInfoItem(new Item.Properties(), true));
    public static final RegistryObject<Item> CAVE_CODEX = DEF_REG.register("cave_codex", () -> new CaveInfoItem(new Item.Properties(), false));
    public static final RegistryObject<Item> CAVE_MAP = DEF_REG.register("cave_map", () -> new CaveMapItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RAW_SCARLET_NEODYMIUM = DEF_REG.register("raw_scarlet_neodymium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_AZURE_NEODYMIUM = DEF_REG.register("raw_azure_neodymium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SCARLET_NEODYMIUM_INGOT = DEF_REG.register("scarlet_neodymium_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> AZURE_NEODYMIUM_INGOT = DEF_REG.register("azure_neodymium_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TELECORE = DEF_REG.register("telecore", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NOTOR_COMPONENT = DEF_REG.register("notor_gizmo", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HEAVYWEIGHT = DEF_REG.register("heavyweight", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FERROUSLIME_BALL = DEF_REG.register("ferrouslime_ball", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> QUARRY_SMASHER = DEF_REG.register("quarry_smasher", () -> new QuarrySmasherItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HOLOCODER = DEF_REG.register("holocoder", () -> new HolocoderItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SEEKING_ARROW = DEF_REG.register("seeking_arrow", () -> new SeekingArrowItem());
    public static final RegistryObject<Item> GALENA_GAUNTLET = DEF_REG.register("galena_gauntlet", () -> new GalenaGauntletItem());
    public static final RegistryObject<Item> RESISTOR_SHIELD = DEF_REG.register("resistor_shield", () -> new ResistorShieldItem());
    public static final RegistryObject<Item> POLARITY_ARMOR_TRIM_SMITHING_TEMPLATE = DEF_REG.register("polarity_armor_trim_smithing_template", () -> SmithingTemplateItem.createArmorTrimTemplate(new ResourceLocation("alexscaves:polarity")));
    public static final RegistryObject<Item> PEWEN_DOOR = DEF_REG.register("pewen_door", () -> new DoubleHighBlockItem(ACBlockRegistry.PEWEN_DOOR.get(), (new Item.Properties())));
    public static final RegistryObject<Item> PEWEN_SIGN = DEF_REG.register("pewen_sign", () -> new SignItem((new Item.Properties()).stacksTo(16), ACBlockRegistry.PEWEN_SIGN.get(), ACBlockRegistry.PEWEN_WALL_SIGN.get()));
    public static final RegistryObject<Item> PEWEN_HANGING_SIGN = DEF_REG.register("pewen_hanging_sign", () -> new HangingSignItem(ACBlockRegistry.PEWEN_HANGING_SIGN.get(), ACBlockRegistry.PEWEN_WALL_HANGING_SIGN.get(), (new Item.Properties()).stacksTo(16)));
    public static final RegistryObject<Item> PEWEN_BOAT = DEF_REG.register("pewen_boat", () -> new CaveBoatItem(false, AlexsCavesBoat.Type.PEWEN, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PEWEN_CHEST_BOAT = DEF_REG.register("pewen_chest_boat", () -> new CaveBoatItem(true, AlexsCavesBoat.Type.PEWEN, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> TRILOCARIS_BUCKET = DEF_REG.register("trilocaris_bucket", () -> new ModFishBucketItem(ACEntityRegistry.TRILOCARIS, () -> Fluids.WATER, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final RegistryObject<Item> TRILOCARIS_TAIL = DEF_REG.register("trilocaris_tail", () -> new Item(new Item.Properties().food(ACFoods.TRILOCARIS_TAIL)));
    public static final RegistryObject<Item> COOKED_TRILOCARIS_TAIL = DEF_REG.register("cooked_trilocaris_tail", () -> new Item(new Item.Properties().food(ACFoods.TRILOCARIS_TAIL_COOKED)));
    public static final RegistryObject<Item> PINE_NUTS = DEF_REG.register("pine_nuts", () -> new Item(new Item.Properties().food(ACFoods.PINE_NUTS)));
    public static final RegistryObject<Item> AMBER_CURIOSITY = DEF_REG.register("amber_curiosity", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DINOSAUR_NUGGET = DEF_REG.register("dinosaur_nugget", () -> new Item(new Item.Properties().food(ACFoods.DINOSAUR_NUGGETS)));
    public static final RegistryObject<Item> SERENE_SALAD = DEF_REG.register("serene_salad", () -> new PrehistoricMixtureItem(new Item.Properties().stacksTo(1).food(ACFoods.SERENE_SALAD)));
    public static final RegistryObject<Item> SEETHING_STEW = DEF_REG.register("seething_stew", () -> new PrehistoricMixtureItem(new Item.Properties().stacksTo(1).food(ACFoods.SEETHING_STEW)));
    public static final RegistryObject<Item> PRIMORDIAL_SOUP = DEF_REG.register("primordial_soup", () -> new PrehistoricMixtureItem(new Item.Properties().stacksTo(1).food(ACFoods.PRIMORDIAL_SOUP)));
    public static final RegistryObject<Item> TOUGH_HIDE = DEF_REG.register("tough_hide", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HEAVY_BONE = DEF_REG.register("heavy_bone", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> PRIMITIVE_CLUB = DEF_REG.register("primitive_club", () -> new PrimitiveClubItem(new Item.Properties().defaultDurability(120)));
    public static final RegistryObject<Item> PRIMITIVE_CLUB_SPRITE = DEF_REG.register("primitive_club_inventory", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PRIMORDIAL_HELMET = DEF_REG.register("primordial_helmet", () -> new PrimordialArmorItem(PRIMORDIAL_ARMOR_MATERIAL, ArmorItem.Type.HELMET));
    public static final RegistryObject<Item> PRIMORDIAL_TUNIC = DEF_REG.register("primordial_tunic", () -> new PrimordialArmorItem(PRIMORDIAL_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE));
    public static final RegistryObject<Item> PRIMORDIAL_PANTS = DEF_REG.register("primordial_pants", () -> new PrimordialArmorItem(PRIMORDIAL_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS));
    public static final RegistryObject<Item> LIMESTONE_SPEAR = DEF_REG.register("limestone_spear", () -> new LimestoneSpearItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> LIMESTONE_SPEAR_SPRITE = DEF_REG.register("limestone_spear_inventory", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DINOSAUR_POTTERY_SHERD = DEF_REG.register("dinosaur_pottery_sherd", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FOOTPRINT_POTTERY_SHERD = DEF_REG.register("footprint_pottery_sherd", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DINOSAUR_TRAIN = DEF_REG.register("dinosaur_train", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> ACID_BUCKET = DEF_REG.register("acid_bucket", () -> new BucketItem(ACFluidRegistry.ACID_FLUID_SOURCE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final RegistryObject<Item> RADGILL_BUCKET = DEF_REG.register("radgill_bucket", () -> new ModFishBucketItem(ACEntityRegistry.RADGILL, ACFluidRegistry.ACID_FLUID_SOURCE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final RegistryObject<Item> RADGILL = DEF_REG.register("radgill", () -> new Item(new Item.Properties().food(ACFoods.RADGILL)));
    public static final RegistryObject<Item> COOKED_RADGILL = DEF_REG.register("cooked_radgill", () -> new Item(new Item.Properties().food(ACFoods.RADGILL_COOKED)));
    public static final RegistryObject<Item> URANIUM = DEF_REG.register("uranium", () -> new RadioactiveItem(new Item.Properties(), 0.001F));
    public static final RegistryObject<Item> URANIUM_SHARD = DEF_REG.register("uranium_shard", () -> new RadioactiveItem(new Item.Properties(), 0.001F));
    public static final RegistryObject<Item> SULFUR_DUST = DEF_REG.register("sulfur_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RADON_BOTTLE = DEF_REG.register("radon_bottle", () -> new Item(new Item.Properties().craftRemainder(Items.GLASS_BOTTLE).stacksTo(16)));
    public static final RegistryObject<Item> CINDER_BRICK = DEF_REG.register("cinder_brick", () -> new ThrownProjectileItem(new Item.Properties(), player -> new CinderBrickEntity(player.level(), player), -20.0F, 0.65F, 0.9F));
    public static final RegistryObject<Item> SPELUNKIE = DEF_REG.register("spelunkie", () -> new RadiationRemovingFoodItem(new Item.Properties().food(ACFoods.SPELUNKIE)));
    public static final RegistryObject<Item> SLAM = DEF_REG.register("slam", () -> new RadiationRemovingFoodItem(new Item.Properties().food(ACFoods.SLAM)));
    public static final RegistryObject<Item> GREEN_SOYLENT = DEF_REG.register("green_soylent", () -> new RadiationRemovingFoodItem(new Item.Properties().food(ACFoods.SOYLENT_GREEN)));
    public static final RegistryObject<Item> TOXIC_PASTE = DEF_REG.register("toxic_paste", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> POLYMER_PLATE = DEF_REG.register("polymer_plate", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HAZMAT_MASK = DEF_REG.register("hazmat_mask", () -> new HazmatArmorItem(HAZMAT_SUIT_ARMOR_MATERIAL, ArmorItem.Type.HELMET));
    public static final RegistryObject<Item> HAZMAT_CHESTPLATE = DEF_REG.register("hazmat_chestplate", () -> new HazmatArmorItem(HAZMAT_SUIT_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE));
    public static final RegistryObject<Item> HAZMAT_LEGGINGS = DEF_REG.register("hazmat_leggings", () -> new HazmatArmorItem(HAZMAT_SUIT_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS));
    public static final RegistryObject<Item> HAZMAT_BOOTS = DEF_REG.register("hazmat_boots", () -> new HazmatArmorItem(HAZMAT_SUIT_ARMOR_MATERIAL, ArmorItem.Type.BOOTS));
    public static final RegistryObject<Item> FISSILE_CORE = DEF_REG.register("fissile_core", () -> new RadioactiveItem(new Item.Properties().rarity(Rarity.UNCOMMON), 0.001F));
    public static final RegistryObject<Item> CHARRED_REMNANT = DEF_REG.register("charred_remnant", () -> new RadioactiveItem(new Item.Properties(), 0.0005F));
    public static final RegistryObject<Item> REMOTE_DETONATOR = DEF_REG.register("remote_detonator", () -> new RemoteDetonatorItem());
    public static final RegistryObject<Item> RAYGUN = DEF_REG.register("raygun", () -> new RaygunItem());
    public static final RegistryObject<Item> LANTERNFISH_BUCKET = DEF_REG.register("lanternfish_bucket", () -> new ModFishBucketItem(ACEntityRegistry.LANTERNFISH, () -> Fluids.WATER, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final RegistryObject<Item> LANTERNFISH = DEF_REG.register("lanternfish", () -> new Item(new Item.Properties().food(ACFoods.LANTERNFISH)));
    public static final RegistryObject<Item> COOKED_LANTERNFISH = DEF_REG.register("cooked_lanternfish", () -> new Item(new Item.Properties().food(ACFoods.LANTERNFISH_COOKED)));
    public static final RegistryObject<Item> TRIPODFISH_BUCKET = DEF_REG.register("tripodfish_bucket", () -> new ModFishBucketItem(ACEntityRegistry.TRIPODFISH, () -> Fluids.WATER, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final RegistryObject<Item> TRIPODFISH = DEF_REG.register("tripodfish", () -> new Item(new Item.Properties().food(ACFoods.TRIPODFISH)));
    public static final RegistryObject<Item> COOKED_TRIPODFISH = DEF_REG.register("cooked_tripodfish", () -> new Item(new Item.Properties().food(ACFoods.TRIPODFISH_COOKED)));
    public static final RegistryObject<Item> SEA_PIG_BUCKET = DEF_REG.register("sea_pig_bucket", () -> new ModFishBucketItem(ACEntityRegistry.SEA_PIG, () -> Fluids.WATER, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final RegistryObject<Item> SEA_PIG = DEF_REG.register("sea_pig", () -> new Item(new Item.Properties().food(ACFoods.SEA_PIG)));
    public static final RegistryObject<Item> MARINE_SNOW = DEF_REG.register("marine_snow", () -> new MarineSnowItem());
    public static final RegistryObject<Item> GOSSAMER_WORM_BUCKET = DEF_REG.register("gossamer_worm_bucket", () -> new ModFishBucketItem(ACEntityRegistry.GOSSAMER_WORM, () -> Fluids.WATER, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final RegistryObject<Item> BIOLUMINESSCENCE = DEF_REG.register("bioluminesscence", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PEARL = DEF_REG.register("pearl", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COOKED_MUSSEL = DEF_REG.register("cooked_mussel", () -> new Item(new Item.Properties().food(ACFoods.MUSSEL_COOKED)));
    public static final RegistryObject<Item> DEEP_SEA_SUSHI_ROLL = DEF_REG.register("deep_sea_sushi_roll", () -> new Item(new Item.Properties().food(ACFoods.DEEP_SEA_SUSHI_ROLL)));
    public static final RegistryObject<Item> SEA_GLASS_SHARDS = DEF_REG.register("sea_glass_shards", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SUBMARINE = DEF_REG.register("submarine", () -> new SubmarineItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> DIVING_HELMET = DEF_REG.register("diving_helmet", () -> new DivingArmorItem(DIVING_SUIT_ARMOR_MATERIAL, ArmorItem.Type.HELMET));
    public static final RegistryObject<Item> DIVING_CHESTPLATE = DEF_REG.register("diving_chestplate", () -> new DivingArmorItem(DIVING_SUIT_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE));
    public static final RegistryObject<Item> DIVING_LEGGINGS = DEF_REG.register("diving_leggings", () -> new DivingArmorItem(DIVING_SUIT_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS));
    public static final RegistryObject<Item> DIVING_BOOTS = DEF_REG.register("diving_boots", () -> new DivingArmorItem(DIVING_SUIT_ARMOR_MATERIAL, ArmorItem.Type.BOOTS));
    public static final RegistryObject<Item> FLOATER = DEF_REG.register("floater", () -> new FloaterItem());
    public static final RegistryObject<Item> GAZING_PEARL = DEF_REG.register("gazing_pearl", () -> new GazingPearlItem());
    public static final RegistryObject<Item> INK_BOMB = DEF_REG.register("ink_bomb", () -> new InkBombItem(new Item.Properties(), false));
    public static final RegistryObject<Item> GLOW_INK_BOMB = DEF_REG.register("glow_ink_bomb", () -> new InkBombItem(new Item.Properties(), true));
    public static final RegistryObject<Item> MAGIC_CONCH = DEF_REG.register("magic_conch", () -> new MagicConchItem(new Item.Properties().defaultDurability(5).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> SEA_STAFF = DEF_REG.register("sea_staff", () -> new SeaStaffItem(new Item.Properties().defaultDurability(256).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> SEA_STAFF_SPRITE = DEF_REG.register("sea_staff_inventory", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ORTHOLANCE = DEF_REG.register("ortholance", () -> new OrtholanceItem(new Item.Properties().defaultDurability(340).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> ORTHOLANCE_SPRITE = DEF_REG.register("ortholance_inventory", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DEPTH_CHARGE = DEF_REG.register("depth_charge", () -> new ThrownProjectileItem(new Item.Properties(), player -> new DepthChargeEntity(player.level(), player), -10.0F, 0.65F, 1.5F));
    public static final RegistryObject<Item> GUARDIAN_POTTERY_SHERD = DEF_REG.register("guardian_pottery_sherd", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HERO_POTTERY_SHERD = DEF_REG.register("hero_pottery_sherd", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BIOLUMINESCENT_TORCH = DEF_REG.register("bioluminescent_torch", () -> new StandingAndWallBlockItem(ACBlockRegistry.BIOLUMINESCENT_TORCH.get(), ACBlockRegistry.BIOLUMINESCENT_WALL_TORCH.get(), new Item.Properties(), Direction.DOWN));
    public static final RegistryObject<Item> GUANO = DEF_REG.register("guano", () -> new ThrownProjectileItem(new Item.Properties(), player -> new GuanoEntity(player.level(), player), 0.0F, 1.0F, 1.0F));
    public static final RegistryObject<Item> MOTH_DUST = DEF_REG.register("moth_dust", () -> new MothDustItem());
    public static final RegistryObject<Item> FERTILIZER = DEF_REG.register("fertilizer", () -> new FertilizerItem());
    public static final RegistryObject<Item> DARK_TATTERS = DEF_REG.register("dark_tatters", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> OCCULT_GEM = DEF_REG.register("occult_gem", () -> new OccultGemItem());
    public static final RegistryObject<Item> TOTEM_OF_POSSESSION = DEF_REG.register("totem_of_possession", () -> new TotemOfPossessionItem());
    public static final RegistryObject<Item> DESOLATE_DAGGER = DEF_REG.register("desolate_dagger", () -> new DesolateDaggerItem());
    public static final RegistryObject<Item> CORRODENT_TEETH = DEF_REG.register("corrodent_teeth", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BURROWING_ARROW = DEF_REG.register("burrowing_arrow", () -> new BurrowingArrowItem());
    public static final RegistryObject<Item> VESPER_WING = DEF_REG.register("vesper_wing", () -> new Item(new Item.Properties().food(ACFoods.VESPER_WING)));
    public static final RegistryObject<Item> VESPER_STEW = DEF_REG.register("vesper_stew", () -> new Item(new Item.Properties().food(ACFoods.VESPER_SOUP)));
    public static final RegistryObject<Item> PURE_DARKNESS = DEF_REG.register("pure_darkness", () -> new Item(new Item.Properties().rarity(RARITY_DEMONIC)));
    public static final RegistryObject<Item> SHADOW_SILK = DEF_REG.register("shadow_silk", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HOOD_OF_DARKNESS = DEF_REG.register("hood_of_darkness", () -> new DarknessArmorItem(DARKNESS_ARMOR_MATERIAL, ArmorItem.Type.HELMET));
    public static final RegistryObject<Item> CLOAK_OF_DARKNESS = DEF_REG.register("cloak_of_darkness", () -> new DarknessArmorItem(DARKNESS_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE));
    public static final RegistryObject<Item> DARKENED_APPLE = DEF_REG.register("darkened_apple", () -> new DarkenedAppleItem());
    public static final RegistryObject<Item> DREADBOW = DEF_REG.register("dreadbow", () -> new DreadbowItem());
    public static final RegistryObject<Item> DREADBOW_SPRITE = DEF_REG.register("dreadbow_inventory", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DREADBOW_PULLING_0_SPRITE = DEF_REG.register("dreadbow_pulling_0_inventory", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DREADBOW_PULLING_1_SPRITE = DEF_REG.register("dreadbow_pulling_1_inventory", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DREADBOW_PULLING_2_SPRITE = DEF_REG.register("dreadbow_pulling_2_inventory", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> THORNWOOD_DOOR = DEF_REG.register("thornwood_door", () -> new DoubleHighBlockItem(ACBlockRegistry.THORNWOOD_DOOR.get(), (new Item.Properties())));
    public static final RegistryObject<Item> THORNWOOD_SIGN = DEF_REG.register("thornwood_sign", () -> new SignItem((new Item.Properties()).stacksTo(16), ACBlockRegistry.THORNWOOD_SIGN.get(), ACBlockRegistry.THORNWOOD_WALL_SIGN.get()));
    public static final RegistryObject<Item> THORNWOOD_HANGING_SIGN = DEF_REG.register("thornwood_hanging_sign", () -> new HangingSignItem(ACBlockRegistry.THORNWOOD_HANGING_SIGN.get(), ACBlockRegistry.THORNWOOD_WALL_HANGING_SIGN.get(), (new Item.Properties()).stacksTo(16)));
    public static final RegistryObject<Item> THORNWOOD_BOAT = DEF_REG.register("thornwood_boat", () -> new CaveBoatItem(false, AlexsCavesBoat.Type.THORNWOOD, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> THORNWOOD_CHEST_BOAT = DEF_REG.register("thornwood_chest_boat", () -> new CaveBoatItem(true, AlexsCavesBoat.Type.THORNWOOD, new Item.Properties().stacksTo(1)));

    static {
        spawnEgg("teletor", ACEntityRegistry.TELETOR, 0X433B4A, 0X0060EF, ACBiomeRegistry.MAGNETIC_CAVES);
        spawnEgg("magnetron", ACEntityRegistry.MAGNETRON, 0XFF002A, 0X203070, ACBiomeRegistry.MAGNETIC_CAVES);
        spawnEgg("boundroid", ACEntityRegistry.BOUNDROID, 0XBB1919, 0XFFFFFF, ACBiomeRegistry.MAGNETIC_CAVES);
        spawnEgg("ferrouslime", ACEntityRegistry.FERROUSLIME, 0X26272D, 0X53556C, ACBiomeRegistry.MAGNETIC_CAVES);
        spawnEgg("notor", ACEntityRegistry.NOTOR, 0X5F5369, 0XC6C6C6, ACBiomeRegistry.MAGNETIC_CAVES);
        spawnEgg("subterranodon", ACEntityRegistry.SUBTERRANODON, 0X00B1B2, 0XFFF11C, ACBiomeRegistry.PRIMORDIAL_CAVES);
        spawnEgg("vallumraptor", ACEntityRegistry.VALLUMRAPTOR, 0X22389A, 0XEEE5AB, ACBiomeRegistry.PRIMORDIAL_CAVES);
        spawnEgg("grottoceratops", ACEntityRegistry.GROTTOCERATOPS, 0XAC3B03, 0XD39B4E, ACBiomeRegistry.PRIMORDIAL_CAVES);
        spawnEgg("trilocaris", ACEntityRegistry.TRILOCARIS, 0X713E0D, 0X8B2010, ACBiomeRegistry.PRIMORDIAL_CAVES);
        spawnEgg("tremorsaurus", ACEntityRegistry.TREMORSAURUS, 0X53780E, 0XDFA211, ACBiomeRegistry.PRIMORDIAL_CAVES);
        spawnEgg("relicheirus", ACEntityRegistry.RELICHEIRUS, 0X6AE4F9, 0X5B2152, ACBiomeRegistry.PRIMORDIAL_CAVES);
        spawnEgg("nucleeper", ACEntityRegistry.NUCLEEPER, 0X95A1A5, 0X00FF00, ACBiomeRegistry.TOXIC_CAVES);
        spawnEgg("radgill", ACEntityRegistry.RADGILL, 0X43302C, 0XE8E400, ACBiomeRegistry.TOXIC_CAVES);
        spawnEgg("brainiac", ACEntityRegistry.BRAINIAC, 0X3E5136, 0XE87C9E, ACBiomeRegistry.TOXIC_CAVES);
        spawnEgg("gammaroach", ACEntityRegistry.GAMMAROACH, 0X56682A, 0X2A2B19, ACBiomeRegistry.TOXIC_CAVES);
        spawnEgg("raycat", ACEntityRegistry.RAYCAT, 0X67FF00, 0X030A00, ACBiomeRegistry.TOXIC_CAVES);
        spawnEgg("lanternfish", ACEntityRegistry.LANTERNFISH, 0X182538, 0XECA500, ACBiomeRegistry.ABYSSAL_CHASM);
        spawnEgg("sea_pig", ACEntityRegistry.SEA_PIG, 0XFFA3B9, 0XF88672, ACBiomeRegistry.ABYSSAL_CHASM);
        spawnEgg("hullbreaker", ACEntityRegistry.HULLBREAKER, 0X182538, 0X76FFFD, ACBiomeRegistry.ABYSSAL_CHASM);
        spawnEgg("gossamer_worm", ACEntityRegistry.GOSSAMER_WORM, 0XC8F1FF, 0X96DEF6, ACBiomeRegistry.ABYSSAL_CHASM);
        spawnEgg("tripodfish", ACEntityRegistry.TRIPODFISH, 0X34529D, 0X81A1CF, ACBiomeRegistry.ABYSSAL_CHASM);
        spawnEgg("deep_one", ACEntityRegistry.DEEP_ONE, 0X0D2547, 0X0A843B, ACBiomeRegistry.ABYSSAL_CHASM);
        spawnEgg("deep_one_knight", ACEntityRegistry.DEEP_ONE_KNIGHT, 0X472C3B, 0XD4CCC3, ACBiomeRegistry.ABYSSAL_CHASM);
        spawnEgg("deep_one_mage", ACEntityRegistry.DEEP_ONE_MAGE, 0X96DEF6, 0XD1FF00, ACBiomeRegistry.ABYSSAL_CHASM);
        spawnEgg("mine_guardian", ACEntityRegistry.MINE_GUARDIAN, 0X404253, 0XE62008, ACBiomeRegistry.ABYSSAL_CHASM);
        spawnEgg("gloomoth", ACEntityRegistry.GLOOMOTH, 0X5E463D, 0XEBD3BE, ACBiomeRegistry.FORLORN_HOLLOWS);
        spawnEgg("underzealot", ACEntityRegistry.UNDERZEALOT, 0X291C17, 0XF27C68, ACBiomeRegistry.FORLORN_HOLLOWS);
        spawnEgg("watcher", ACEntityRegistry.WATCHER, 0X291C17, 0XEC1900, ACBiomeRegistry.FORLORN_HOLLOWS);
        spawnEgg("corrodent", ACEntityRegistry.CORRODENT, 0X351A14, 0X593B33, ACBiomeRegistry.FORLORN_HOLLOWS);
        spawnEgg("vesper", ACEntityRegistry.VESPER, 0X884E2A, 0XA54A6B, ACBiomeRegistry.FORLORN_HOLLOWS);
        spawnEgg("forsaken", ACEntityRegistry.FORSAKEN, 0X000000, 0X110909, ACBiomeRegistry.FORLORN_HOLLOWS);
    }

    private static void spawnEgg(String entityName, RegistryObject type, int color1, int color2, ResourceKey<Biome> biomeTab) {
        RegistryObject<Item> item = DEF_REG.register("spawn_egg_" + entityName, () -> new ForgeSpawnEggItem(type, color1, color2, new Item.Properties()));
        creativeTabSpawnEggMap.put(item, biomeTab);
    }

    public static void setup() {
        PRIMORDIAL_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(TOUGH_HIDE.get()));
        HAZMAT_SUIT_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(POLYMER_PLATE.get()));
        DIVING_SUIT_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(Items.COPPER_INGOT));
        DARKNESS_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(DARK_TATTERS.get()));
        DispenserBlock.registerBehavior(SEEKING_ARROW.get(), new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                AbstractArrow abstractarrow = new SeekingArrowEntity(level, position.x(), position.y(), position.z());
                abstractarrow.pickup = AbstractArrow.Pickup.ALLOWED;
                return abstractarrow;
            }
        });
        DispenserBlock.registerBehavior(CINDER_BRICK.get(), new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                return new CinderBrickEntity(level, position.x(), position.y(), position.z());
            }
        });
        DispenserBlock.registerBehavior(GUANO.get(), new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                return new GuanoEntity(level, position.x(), position.y(), position.z());
            }
        });
        DispenserBlock.registerBehavior(BURROWING_ARROW.get(), new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                AbstractArrow abstractarrow = new BurrowingArrowEntity(level, position.x(), position.y(), position.z());
                abstractarrow.pickup = AbstractArrow.Pickup.ALLOWED;
                return abstractarrow;
            }
        });
    }

    public static List<RegistryObject<Item>> getSpawnEggsForTab(ResourceKey<Biome> tabName) {
        List<RegistryObject<Item>> list = new ArrayList();
        for (Map.Entry<RegistryObject<Item>, ResourceKey<Biome>> entry : creativeTabSpawnEggMap.entrySet()) {
            if (entry.getValue().equals(tabName)) {
                list.add(entry.getKey());
            }
        }
        return list;
    }
}
