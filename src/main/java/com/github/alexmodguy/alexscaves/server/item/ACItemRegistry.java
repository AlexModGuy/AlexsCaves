package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.biome.Biome;
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
    public static final ACArmorMaterial DIVING_SUIT_ARMOR_MATERIAL = new ACArmorMaterial("diving_suit", 20, new int[]{2, 5, 6, 2}, 25, SoundEvents.ARMOR_EQUIP_IRON, 1F);
    public static final DeferredRegister<Item> DEF_REG = DeferredRegister.create(ForgeRegistries.ITEMS, AlexsCaves.MODID);

    public static final RegistryObject<Item> CAVE_TABLET = DEF_REG.register("cave_tablet", () -> new CaveInfoItem(new Item.Properties(), true));
    public static final RegistryObject<Item> CAVE_CODEX = DEF_REG.register("cave_codex", () -> new CaveInfoItem(new Item.Properties(), false));
    public static final RegistryObject<Item> CAVE_MAP = DEF_REG.register("cave_map", () -> new CaveMapItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RAW_SCARLET_NEODYMIUM = DEF_REG.register("raw_scarlet_neodymium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_AZURE_NEODYMIUM = DEF_REG.register("raw_azure_neodymium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SCARLET_NEODYMIUM_INGOT = DEF_REG.register("scarlet_neodymium_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> AZURE_NEODYMIUM_INGOT = DEF_REG.register("azure_neodymium_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PEWEN_DOOR = DEF_REG.register("pewen_door", () -> new DoubleHighBlockItem(ACBlockRegistry.PEWEN_DOOR.get(), (new Item.Properties())));
    public static final RegistryObject<Item> PEWEN_SIGN = DEF_REG.register("pewen_sign", () -> new SignItem((new Item.Properties()).stacksTo(16), ACBlockRegistry.PEWEN_SIGN.get(), ACBlockRegistry.PEWEN_WALL_SIGN.get()));
    public static final RegistryObject<Item> ACID_BUCKET = DEF_REG.register("acid_bucket", () -> new BucketItem(ACFluidRegistry.ACID_FLUID_SOURCE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final RegistryObject<Item> RADGILL_BUCKET = DEF_REG.register("radgill_bucket", () -> new ModFishBucketItem(ACEntityRegistry.RADGILL, ACFluidRegistry.ACID_FLUID_SOURCE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final RegistryObject<Item> RADON_BOTTLE = DEF_REG.register("radon_bottle", () -> new Item(new Item.Properties().craftRemainder(Items.GLASS_BOTTLE).stacksTo(16)));
    public static final RegistryObject<Item> CINDER_BRICK = DEF_REG.register("cinder_brick", () -> new CinderBrickItem(new Item.Properties()));
    public static final RegistryObject<Item> SUBMARINE = DEF_REG.register("submarine", () -> new SubmarineItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> DIVING_HELMET = DEF_REG.register("diving_helmet", () -> new DivingArmorItem(DIVING_SUIT_ARMOR_MATERIAL, ArmorItem.Type.HELMET));
    public static final RegistryObject<Item> DIVING_CHESTPLATE = DEF_REG.register("diving_chestplate", () -> new DivingArmorItem(DIVING_SUIT_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE));
    public static final RegistryObject<Item> DIVING_LEGGINGS = DEF_REG.register("diving_leggings", () -> new DivingArmorItem(DIVING_SUIT_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS));
    public static final RegistryObject<Item> DIVING_BOOTS = DEF_REG.register("diving_boots", () -> new DivingArmorItem(DIVING_SUIT_ARMOR_MATERIAL, ArmorItem.Type.BOOTS));
    public static final RegistryObject<Item> PEARL = DEF_REG.register("pearl", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INK_BOMB = DEF_REG.register("ink_bomb", () -> new InkBombItem(new Item.Properties(), false));
    public static final RegistryObject<Item> GLOW_INK_BOMB = DEF_REG.register("glow_ink_bomb", () -> new InkBombItem(new Item.Properties(), true));
    public static final RegistryObject<Item> MAGIC_CONCH = DEF_REG.register("magic_conch", () -> new MagicConchItem(new Item.Properties().defaultDurability(5).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> SEA_STAFF = DEF_REG.register("sea_staff", () -> new SeaStaffItem(new Item.Properties().defaultDurability(256).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> SEA_STAFF_SPRITE = DEF_REG.register("sea_staff_inventory", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ORTHOLANCE = DEF_REG.register("ortholance", () -> new OrtholanceItem(new Item.Properties().defaultDurability(340)));
    public static final RegistryObject<Item> ORTHOLANCE_SPRITE = DEF_REG.register("ortholance_inventory", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> THORNWOOD_DOOR = DEF_REG.register("thornwood_door", () -> new DoubleHighBlockItem(ACBlockRegistry.THORNWOOD_DOOR.get(), (new Item.Properties())));
    public static final RegistryObject<Item> THORNWOOD_SIGN = DEF_REG.register("thornwood_sign", () -> new SignItem((new Item.Properties()).stacksTo(16), ACBlockRegistry.THORNWOOD_SIGN.get(), ACBlockRegistry.THORNWOOD_WALL_SIGN.get()));

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
        DIVING_SUIT_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(Items.COPPER_INGOT));
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
