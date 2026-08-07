package com.github.alexmodguy.alexscaves.server.enchantment;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Enchantments are now data-driven in NeoForge 1.21.
 * Custom enchantments must be defined in JSON files under:
 * data/alexscaves/enchantment/
 * 
 * The actual enchantment definitions should be placed in:
 * src/main/resources/data/alexscaves/enchantment/*.json
 * 
 * Enchantment behavior that requires custom code should be handled via:
 * - minecraft:post_attack effect component (for attack-related effects)
 * - minecraft:tick effect component (for tick-based effects)
 * - minecraft:attributes effect component (for attribute modifiers)
 * - Or by checking for enchantment presence in existing code and applying effects manually
 */
public class ACEnchantmentRegistry {
    
    // ===== Galena Gauntlet Enchantments =====
    public static final ResourceKey<Enchantment> FIELD_EXTENSION = createKey("field_extension");
    public static final ResourceKey<Enchantment> CRYSTALLIZATION = createKey("crystallization");
    public static final ResourceKey<Enchantment> FERROUS_HASTE = createKey("ferrous_haste");
    
    // ===== Resistor Shield Enchantments =====
    public static final ResourceKey<Enchantment> ARROW_INDUCTING = createKey("arrow_inducting");
    public static final ResourceKey<Enchantment> HEAVY_SLAM = createKey("heavy_slam");
    
    // ===== Primitive Club Enchantments =====
    public static final ResourceKey<Enchantment> SWIFTWOOD = createKey("swiftwood");
    public static final ResourceKey<Enchantment> BONKING = createKey("bonking");
    public static final ResourceKey<Enchantment> DAZING_SWEEP = createKey("dazing_sweep");
    
    // ===== Extinction Spear Enchantments =====
    public static final ResourceKey<Enchantment> PLUMMETING_FLIGHT = createKey("plummeting_flight");
    public static final ResourceKey<Enchantment> HERD_PHALANX = createKey("herd_phalanx");
    public static final ResourceKey<Enchantment> CHOMPING_SPIRIT = createKey("chomping_spirit");
    
    // ===== Raygun Enchantments =====
    public static final ResourceKey<Enchantment> ENERGY_EFFICIENCY = createKey("energy_efficiency");
    public static final ResourceKey<Enchantment> SOLAR = createKey("solar");
    public static final ResourceKey<Enchantment> X_RAY = createKey("x_ray");
    public static final ResourceKey<Enchantment> GAMMA_RAY = createKey("gamma_ray");
    
    // ===== Ortholance Enchantments =====
    public static final ResourceKey<Enchantment> SECOND_WAVE = createKey("second_wave");
    public static final ResourceKey<Enchantment> FLINGING = createKey("flinging");
    public static final ResourceKey<Enchantment> SEA_SWING = createKey("sea_swing");
    public static final ResourceKey<Enchantment> TSUNAMI = createKey("tsunami");
    
    // ===== Magic Conch Enchantments =====
    public static final ResourceKey<Enchantment> CHARTING_CALL = createKey("charting_call");
    public static final ResourceKey<Enchantment> LASTING_MORALE = createKey("lasting_morale");
    public static final ResourceKey<Enchantment> TAXING_BELLOW = createKey("taxing_bellow");
    
    // ===== Sea Staff Enchantments =====
    public static final ResourceKey<Enchantment> ENVELOPING_BUBBLE = createKey("enveloping_bubble");
    public static final ResourceKey<Enchantment> BOUNCING_BOLT = createKey("bouncing_bolt");
    public static final ResourceKey<Enchantment> SEAPAIRING = createKey("seapairing");
    public static final ResourceKey<Enchantment> TRIPLE_SPLASH = createKey("triple_splash");
    public static final ResourceKey<Enchantment> SOAK_SEEKING = createKey("soak_seeking");
    
    // ===== Totem of Possession Enchantments =====
    public static final ResourceKey<Enchantment> DETONATING_DEATH = createKey("detonating_death");
    public static final ResourceKey<Enchantment> RAPID_POSSESSION = createKey("rapid_possession");
    public static final ResourceKey<Enchantment> SIGHTLESS = createKey("sightless");
    public static final ResourceKey<Enchantment> ASTRAL_TRANSFERRING = createKey("astral_transferring");
    
    // ===== Desolate Dagger Enchantments =====
    public static final ResourceKey<Enchantment> IMPENDING_STAB = createKey("impending_stab");
    public static final ResourceKey<Enchantment> SATED_BLADE = createKey("sated_blade");
    public static final ResourceKey<Enchantment> DOUBLE_STAB = createKey("double_stab");
    
    // ===== Dreadbow Enchantments =====
    public static final ResourceKey<Enchantment> PRECISE_VOLLEY = createKey("precise_volley");
    public static final ResourceKey<Enchantment> DARK_NOCK = createKey("dark_nock");
    public static final ResourceKey<Enchantment> RELENTLESS_DARKNESS = createKey("relentless_darkness");
    public static final ResourceKey<Enchantment> TWILIGHT_PERFECTION = createKey("twilight_perfection");
    public static final ResourceKey<Enchantment> SHADED_RESPITE = createKey("shaded_respite");
    
    // ===== Shot Gum Enchantments =====
    public static final ResourceKey<Enchantment> TARGETED_RICOCHET = createKey("targeted_ricochet");
    public static final ResourceKey<Enchantment> TRIPLE_SPLIT = createKey("triple_split");
    public static final ResourceKey<Enchantment> BOUNCY_BALL = createKey("bouncy_ball");
    public static final ResourceKey<Enchantment> EXPLOSIVE_FLAVOR = createKey("explosive_flavor");
    
    // ===== Candy Cane Hook Enchantments =====
    public static final ResourceKey<Enchantment> FAR_FLUNG = createKey("far_flung");
    public static final ResourceKey<Enchantment> SHARP_CANE = createKey("sharp_cane");
    public static final ResourceKey<Enchantment> STRAIGHT_HOOK = createKey("straight_hook");
    
    // ===== Sugar Staff Enchantments =====
    public static final ResourceKey<Enchantment> SPELL_LASTING = createKey("spell_lasting");
    public static final ResourceKey<Enchantment> PEPPERMINT_PUNTING = createKey("peppermint_punting");
    public static final ResourceKey<Enchantment> HUMUNGOUS_HEX = createKey("humungous_hex");
    public static final ResourceKey<Enchantment> MULTIPLE_MINT = createKey("multiple_mint");
    public static final ResourceKey<Enchantment> SEEKCANDY = createKey("seekcandy");
    
    private static ResourceKey<Enchantment> createKey(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, name));
    }
    
    /**
     * Safely get the enchantment level for an item, returning 0 if the enchantment doesn't exist.
     * This avoids crashes when enchantment data files are missing.
     * 
     * @param level The level to get registry access from
     * @param stack The item stack to check
     * @param enchantmentKey The enchantment resource key
     * @return The enchantment level, or 0 if not present or enchantment doesn't exist
     */
    public static int getEnchantmentLevel(net.minecraft.world.level.Level level, net.minecraft.world.item.ItemStack stack, ResourceKey<Enchantment> enchantmentKey) {
        if (level == null || stack == null || stack.isEmpty()) {
            return 0;
        }
        var lookup = level.registryAccess().lookup(Registries.ENCHANTMENT);
        if (lookup.isEmpty()) {
            return 0;
        }
        var holder = lookup.get().get(enchantmentKey);
        if (holder.isEmpty()) {
            return 0;
        }
        return stack.getEnchantmentLevel(holder.get());
    }
    
    /**
     * Safely get an enchantment holder, returning Optional.empty() if the enchantment doesn't exist.
     * 
     * @param level The level to get registry access from
     * @param enchantmentKey The enchantment resource key
     * @return Optional containing the enchantment holder, or empty if not found
     */
    public static java.util.Optional<net.minecraft.core.Holder<Enchantment>> getEnchantmentHolder(net.minecraft.world.level.Level level, ResourceKey<Enchantment> enchantmentKey) {
        if (level == null) {
            return java.util.Optional.empty();
        }
        var lookup = level.registryAccess().lookup(Registries.ENCHANTMENT);
        if (lookup.isEmpty()) {
            return java.util.Optional.empty();
        }
        var reference = lookup.get().get(enchantmentKey);
        if (reference.isEmpty()) {
            return java.util.Optional.empty();
        }
        // Reference extends Holder, so we can cast it
        return java.util.Optional.of(reference.get());
    }
    
    /**
     * In 1.21, enchantments are data-driven and EnchantmentCategory no longer exists.
     * This method is now a no-op. Enchanted books should be added through data packs
     * or by manually iterating through the enchantment registry at runtime.
     * 
     * @param output The creative tab output
     * @param itemKey A marker parameter (previously EnchantmentCategory, now unused)
     */
    public static void addAllEnchantsToCreativeTab(net.minecraft.world.item.CreativeModeTab.Output output, Object itemKey) {
        // No-op in 1.21 - enchantments are now data-driven
        // Enchanted books for custom enchantments would need to be added through datapack or manual registry iteration
    }
}
