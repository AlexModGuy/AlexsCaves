package com.github.alexmodguy.alexscaves.server.enchantment;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ACEnchantmentRegistry {
    public static final DeferredRegister<Enchantment> DEF_REG = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, AlexsCaves.MODID);
    public static final EnchantmentCategory GALENA_GAUNTLET = EnchantmentCategory.create("galena_gauntlet", (item -> item == ACItemRegistry.GALENA_GAUNTLET.get()));
    public static final EnchantmentCategory RESISTOR_SHIELD = EnchantmentCategory.create("resistor_shield", (item -> item == ACItemRegistry.RESISTOR_SHIELD.get()));
    public static final EnchantmentCategory PRIMITIVE_CLUB = EnchantmentCategory.create("primitive_club", (item -> item == ACItemRegistry.PRIMITIVE_CLUB.get()));
    public static final EnchantmentCategory EXTINCTION_SPEAR = EnchantmentCategory.create("extinction_spear", (item -> item == ACItemRegistry.EXTINCTION_SPEAR.get()));
    public static final EnchantmentCategory RAYGUN = EnchantmentCategory.create("raygun", (item -> item == ACItemRegistry.RAYGUN.get()));
    public static final EnchantmentCategory ORTHOLANCE = EnchantmentCategory.create("ortholance", (item -> item == ACItemRegistry.ORTHOLANCE.get()));
    public static final EnchantmentCategory MAGIC_CONCH = EnchantmentCategory.create("magic_conch", (item -> item == ACItemRegistry.MAGIC_CONCH.get()));
    public static final EnchantmentCategory SEA_STAFF = EnchantmentCategory.create("sea_staff", (item -> item == ACItemRegistry.SEA_STAFF.get()));
    public static final EnchantmentCategory TOTEM_OF_POSSESSION = EnchantmentCategory.create("totem_of_possession", (item -> item == ACItemRegistry.TOTEM_OF_POSSESSION.get()));
    public static final EnchantmentCategory DESOLATE_DAGGER = EnchantmentCategory.create("desolate_dagger", (item -> item == ACItemRegistry.DESOLATE_DAGGER.get()));
    public static final EnchantmentCategory DREADBOW = EnchantmentCategory.create("dreadbow", (item -> item == ACItemRegistry.DREADBOW.get()));
    public static final EnchantmentCategory SHOT_GUM = EnchantmentCategory.create("shot_gum", (item -> item == ACItemRegistry.SHOT_GUM.get()));
    public static final EnchantmentCategory CANDY_CANE_HOOK = EnchantmentCategory.create("candy_cane_hook", (item -> item == ACItemRegistry.CANDY_CANE_HOOK.get()));
    public static final EnchantmentCategory SUGAR_STAFF = EnchantmentCategory.create("sugar_staff", (item -> item == ACItemRegistry.SUGAR_STAFF.get()));
    public static final RegistryObject<Enchantment> FIELD_EXTENSION = DEF_REG.register("field_extension", () -> new ACWeaponEnchantment("field_extension", Enchantment.Rarity.COMMON, GALENA_GAUNTLET, 4, 6, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND));
    public static final RegistryObject<Enchantment> CRYSTALLIZATION = DEF_REG.register("crystallization", () -> new ACWeaponEnchantment("crystallization", Enchantment.Rarity.RARE, GALENA_GAUNTLET, 1, 15, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND));
    public static final RegistryObject<Enchantment> FERROUS_HASTE = DEF_REG.register("ferrous_haste", () -> new ACWeaponEnchantment("ferrous_haste", Enchantment.Rarity.RARE, GALENA_GAUNTLET, 1, 15, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND));
    public static final RegistryObject<Enchantment> ARROW_INDUCTING = DEF_REG.register("arrow_inducting", () -> new ACWeaponEnchantment("arrow_inducting", Enchantment.Rarity.RARE, RESISTOR_SHIELD, 1, 18, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND));
    public static final RegistryObject<Enchantment> HEAVY_SLAM = DEF_REG.register("heavy_slam", () -> new ACWeaponEnchantment("heavy_slam", Enchantment.Rarity.COMMON, RESISTOR_SHIELD, 3, 6, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND));
    public static final RegistryObject<Enchantment> SWIFTWOOD = DEF_REG.register("swiftwood", () -> new ACWeaponEnchantment("swiftwood", Enchantment.Rarity.RARE, PRIMITIVE_CLUB, 3, 8, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> BONKING = DEF_REG.register("bonking", () -> new ACWeaponEnchantment("bonking", Enchantment.Rarity.VERY_RARE, PRIMITIVE_CLUB, 1, 18, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> DAZING_SWEEP = DEF_REG.register("dazing_sweep", () -> new ACWeaponEnchantment("dazing_sweep", Enchantment.Rarity.RARE, PRIMITIVE_CLUB, 2, 10, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> PLUMMETING_FLIGHT = DEF_REG.register("plummeting_flight", () -> new ACWeaponEnchantment("plummeting_flight", Enchantment.Rarity.RARE, EXTINCTION_SPEAR, 3, 13, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> HERD_PHALANX = DEF_REG.register("herd_phalanx", () -> new ACWeaponEnchantment("herd_phalanx", Enchantment.Rarity.RARE, EXTINCTION_SPEAR, 3, 13, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> CHOMPING_SPIRIT = DEF_REG.register("chomping_spirit", () -> new ACWeaponEnchantment("chomping_spirit", Enchantment.Rarity.RARE, EXTINCTION_SPEAR, 2, 10, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> ENERGY_EFFICIENCY = DEF_REG.register("energy_efficiency", () -> new ACWeaponEnchantment("energy_efficiency", Enchantment.Rarity.COMMON, RAYGUN, 3, 5, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> SOLAR = DEF_REG.register("solar", () -> new ACWeaponEnchantment("solar", Enchantment.Rarity.COMMON, RAYGUN, 1, 14, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> X_RAY = DEF_REG.register("x_ray", () -> new ACWeaponEnchantment("x_ray", Enchantment.Rarity.COMMON, RAYGUN, 1, 12, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> GAMMA_RAY = DEF_REG.register("gamma_ray", () -> new ACWeaponEnchantment("gamma_ray", Enchantment.Rarity.RARE, RAYGUN, 1, 18, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> SECOND_WAVE = DEF_REG.register("second_wave", () -> new ACWeaponEnchantment("second_wave", Enchantment.Rarity.RARE, ORTHOLANCE, 1, 12, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> FLINGING = DEF_REG.register("flinging", () -> new ACWeaponEnchantment("flinging", Enchantment.Rarity.COMMON, ORTHOLANCE, 3, 8, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> SEA_SWING = DEF_REG.register("sea_swing", () -> new ACWeaponEnchantment("sea_swing", Enchantment.Rarity.RARE, ORTHOLANCE, 1, 10, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> TSUNAMI = DEF_REG.register("tsunami", () -> new ACWeaponEnchantment("tsunami", Enchantment.Rarity.VERY_RARE, ORTHOLANCE, 1, 20, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> CHARTING_CALL = DEF_REG.register("charting_call", () -> new ACWeaponEnchantment("charting_call", Enchantment.Rarity.COMMON, MAGIC_CONCH, 4, 7, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> LASTING_MORALE = DEF_REG.register("lasting_morale", () -> new ACWeaponEnchantment("lasting_morale", Enchantment.Rarity.RARE, MAGIC_CONCH, 3, 8, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> TAXING_BELLOW = DEF_REG.register("taxing_bellow", () -> new ACWeaponEnchantment("taxing_bellow", Enchantment.Rarity.RARE, MAGIC_CONCH, 1, 19, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> ENVELOPING_BUBBLE = DEF_REG.register("enveloping_bubble", () -> new ACWeaponEnchantment("enveloping_bubble", Enchantment.Rarity.RARE, SEA_STAFF, 1, 13, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> BOUNCING_BOLT = DEF_REG.register("bouncing_bolt", () -> new ACWeaponEnchantment("bouncing_bolt", Enchantment.Rarity.RARE, SEA_STAFF, 1, 13, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> SEAPAIRING = DEF_REG.register("seapairing", () -> new ACWeaponEnchantment("seapairing", Enchantment.Rarity.VERY_RARE, SEA_STAFF, 1, 10, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> TRIPLE_SPLASH = DEF_REG.register("triple_splash", () -> new ACWeaponEnchantment("triple_splash", Enchantment.Rarity.RARE, SEA_STAFF, 1, 15, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> SOAK_SEEKING = DEF_REG.register("soak_seeking", () -> new ACWeaponEnchantment("soak_seeking", Enchantment.Rarity.COMMON, SEA_STAFF, 3, 5, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> DETONATING_DEATH = DEF_REG.register("detonating_death", () -> new ACWeaponEnchantment("detonating_death", Enchantment.Rarity.RARE, TOTEM_OF_POSSESSION, 1, 11, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> RAPID_POSSESSION = DEF_REG.register("rapid_possession", () -> new ACWeaponEnchantment("rapid_possession", Enchantment.Rarity.COMMON, TOTEM_OF_POSSESSION, 3, 5, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> SIGHTLESS = DEF_REG.register("sightless", () -> new ACWeaponEnchantment("sightless", Enchantment.Rarity.RARE, TOTEM_OF_POSSESSION, 1, 13, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> ASTRAL_TRANSFERRING = DEF_REG.register("astral_transferring", () -> new ACWeaponEnchantment("astral_transferring", Enchantment.Rarity.RARE, TOTEM_OF_POSSESSION, 1, 12, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> IMPENDING_STAB = DEF_REG.register("impending_stab", () -> new ACWeaponEnchantment("impending_stab", Enchantment.Rarity.COMMON, DESOLATE_DAGGER, 3, 6, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> SATED_BLADE = DEF_REG.register("sated_blade", () -> new ACWeaponEnchantment("sated_blade", Enchantment.Rarity.COMMON, DESOLATE_DAGGER, 2, 11, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> DOUBLE_STAB = DEF_REG.register("double_stab", () -> new ACWeaponEnchantment("double_stab", Enchantment.Rarity.RARE, DESOLATE_DAGGER, 1, 12, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> PRECISE_VOLLEY = DEF_REG.register("precise_volley", () -> new ACWeaponEnchantment("precise_volley", Enchantment.Rarity.RARE, DREADBOW, 1, 18, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> DARK_NOCK = DEF_REG.register("dark_nock", () -> new ACWeaponEnchantment("dark_nock", Enchantment.Rarity.RARE, DREADBOW, 3, 10, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> RELENTLESS_DARKNESS = DEF_REG.register("relentless_darkness", () -> new ACWeaponEnchantment("relentless_darkness", Enchantment.Rarity.VERY_RARE, DREADBOW, 1, 20, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> TWILIGHT_PERFECTION = DEF_REG.register("twilight_perfection", () -> new ACWeaponEnchantment("twilight_perfection", Enchantment.Rarity.RARE, DREADBOW, 3, 7, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> SHADED_RESPITE = DEF_REG.register("shaded_respite", () -> new ACWeaponEnchantment("shaded_respite", Enchantment.Rarity.VERY_RARE, DREADBOW, 1, 9, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> TARGETED_RICOCHET = DEF_REG.register("targeted_ricochet", () -> new ACWeaponEnchantment("targeted_ricochet", Enchantment.Rarity.RARE, SHOT_GUM, 1, 16, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> TRIPLE_SPLIT = DEF_REG.register("triple_split", () -> new ACWeaponEnchantment("triple_split", Enchantment.Rarity.RARE, SHOT_GUM, 1, 12, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> BOUNCY_BALL = DEF_REG.register("bouncy_ball", () -> new ACWeaponEnchantment("bouncy_ball", Enchantment.Rarity.COMMON, SHOT_GUM, 3, 7, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> EXPLOSIVE_FLAVOR = DEF_REG.register("explosive_flavor", () -> new ACWeaponEnchantment("explosive_flavor", Enchantment.Rarity.VERY_RARE, SHOT_GUM, 1, 16, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> FAR_FLUNG = DEF_REG.register("far_flung", () -> new ACWeaponEnchantment("far_flung", Enchantment.Rarity.COMMON, CANDY_CANE_HOOK, 3, 6, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> SHARP_CANE = DEF_REG.register("sharp_cane", () -> new ACWeaponEnchantment("sharp_cane", Enchantment.Rarity.COMMON, CANDY_CANE_HOOK, 2, 8, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> STRAIGHT_HOOK = DEF_REG.register("straight_hook", () -> new ACWeaponEnchantment("straight_hook", Enchantment.Rarity.RARE, CANDY_CANE_HOOK, 1, 12, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> SPELL_LASTING = DEF_REG.register("spell_lasting", () -> new ACWeaponEnchantment("spell_lasting", Enchantment.Rarity.COMMON, SUGAR_STAFF, 3, 8, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> PEPPERMINT_PUNTING = DEF_REG.register("peppermint_punting", () -> new ACWeaponEnchantment("peppermint_punting", Enchantment.Rarity.RARE, SUGAR_STAFF, 1, 12, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> HUMUNGOUS_HEX = DEF_REG.register("humungous_hex", () -> new ACWeaponEnchantment("humungous_hex", Enchantment.Rarity.UNCOMMON, SUGAR_STAFF, 2, 9, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> MULTIPLE_MINT = DEF_REG.register("multiple_mint", () -> new ACWeaponEnchantment("multiple_mint", Enchantment.Rarity.UNCOMMON, SUGAR_STAFF, 2, 9, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> SEEKCANDY = DEF_REG.register("seekcandy", () -> new ACWeaponEnchantment("seekcandy", Enchantment.Rarity.RARE, SUGAR_STAFF, 1, 16, EquipmentSlot.MAINHAND));

    public static boolean areCompatible(ACWeaponEnchantment enchantment1, Enchantment enchantment2) {
        if(enchantment1 == X_RAY.get() && enchantment2 == GAMMA_RAY.get()){
            return false;
        }
        if(enchantment1 == GAMMA_RAY.get() && enchantment2 == X_RAY.get()){
            return false;
        }
        if(enchantment1 == SECOND_WAVE.get() && enchantment2 == TSUNAMI.get()){
            return false;
        }
        if(enchantment1 == TSUNAMI.get() && enchantment2 == SECOND_WAVE.get()){
            return false;
        }
        if(enchantment1 == TAXING_BELLOW.get() && (enchantment2 == Enchantments.UNBREAKING || enchantment2 == Enchantments.MENDING)){
            return false;
        }
        if((enchantment1 == Enchantments.UNBREAKING || enchantment1 == Enchantments.MENDING) && enchantment2 == TAXING_BELLOW.get()){
            return false;
        }
        if(enchantment1 == BOUNCING_BOLT.get() && enchantment2 == TRIPLE_SPLASH.get()){
            return false;
        }
        if(enchantment1 == TRIPLE_SPLASH.get() && enchantment2 == BOUNCING_BOLT.get()){
            return false;
        }
        if(enchantment1 == DETONATING_DEATH.get() && enchantment2 == ASTRAL_TRANSFERRING.get()){
            return false;
        }
        if(enchantment1 == ASTRAL_TRANSFERRING.get() && enchantment2 == DETONATING_DEATH.get()){
            return false;
        }
        if(enchantment1 == IMPENDING_STAB.get() && enchantment2 == DOUBLE_STAB.get()){
            return false;
        }
        if(enchantment1 == DOUBLE_STAB.get() && enchantment2 == IMPENDING_STAB.get()){
            return false;
        }
        if(enchantment1 == RELENTLESS_DARKNESS.get() && (enchantment2 == PRECISE_VOLLEY.get() || enchantment2 == DARK_NOCK.get() || enchantment2 == TWILIGHT_PERFECTION.get())){
            return false;
        }
        if((enchantment1 == PRECISE_VOLLEY.get() || enchantment1 == DARK_NOCK.get()  || enchantment1 == TWILIGHT_PERFECTION.get()) && enchantment2 == RELENTLESS_DARKNESS.get()){
            return false;
        }
        if(enchantment1 == TARGETED_RICOCHET.get() && enchantment2 == TRIPLE_SPLIT.get()){
            return false;
        }
        if(enchantment1 == TRIPLE_SPLIT.get() && enchantment2 == TARGETED_RICOCHET.get()){
            return false;
        }
        return true;
    }

    public static void addAllEnchantsToCreativeTab(CreativeModeTab.Output output, EnchantmentCategory enchantmentCategory){
        for (RegistryObject<Enchantment> enchantObject : DEF_REG.getEntries()) {
            if (enchantObject.isPresent()) {
                Enchantment enchant = enchantObject.get();
                if(enchant.category == enchantmentCategory){
                    EnchantmentInstance instance = new EnchantmentInstance(enchant, enchant.getMaxLevel());
                    output.accept(EnchantedBookItem.createForEnchantment(instance));
                }
            }
        }
    }
}
