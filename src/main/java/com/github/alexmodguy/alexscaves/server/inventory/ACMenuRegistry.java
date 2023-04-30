package com.github.alexmodguy.alexscaves.server.inventory;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ACMenuRegistry {

    public static final DeferredRegister<MenuType<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.MENU_TYPES, AlexsCaves.MODID);

    public static final RegistryObject<MenuType<SpelunkeryTableMenu>> SPELUNKERY_TABLE_MENU = DEF_REG.register("spelunkery_table_menu", () -> new MenuType<SpelunkeryTableMenu>(SpelunkeryTableMenu::new, FeatureFlags.DEFAULT_FLAGS));

}
