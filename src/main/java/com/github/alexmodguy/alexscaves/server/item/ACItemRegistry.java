package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ACItemRegistry {
    public static final DeferredRegister<Item> DEF_REG = DeferredRegister.create(ForgeRegistries.ITEMS, AlexsCaves.MODID);

    public static final RegistryObject<Item> RAW_SCARLET_NEODYMIUM = DEF_REG.register("raw_scarlet_neodymium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_AZURE_NEODYMIUM = DEF_REG.register("raw_azure_neodymium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SCARLET_NEODYMIUM_INGOT = DEF_REG.register("scarlet_neodymium_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> AZURE_NEODYMIUM_INGOT = DEF_REG.register("azure_neodymium_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> PEWEN_DOOR = DEF_REG.register("pewen_door", () -> new DoubleHighBlockItem(ACBlockRegistry.PEWEN_DOOR.get(), (new Item.Properties())));
    public static final RegistryObject<Item> PEWEN_SIGN = DEF_REG.register("pewen_sign", () -> new SignItem((new Item.Properties()).stacksTo(16), ACBlockRegistry.PEWEN_SIGN.get(), ACBlockRegistry.PEWEN_WALL_SIGN.get()));

    static {
        spawnEgg("teletor", ACEntityRegistry.TELETOR, 0X433B4A,0X0060EF);
        spawnEgg("magnetron", ACEntityRegistry.MAGNETRON, 0XFF002A,0X203070);
        spawnEgg("boundroid", ACEntityRegistry.BOUNDROID, 0XBB1919,0XFFFFFF);
        spawnEgg("subterranodon", ACEntityRegistry.SUBTERRANODON, 0X00B1B2,0XFFF11C);
        spawnEgg("vallumraptor", ACEntityRegistry.VALLUMRAPTOR, 0X22389A,0XEEE5AB);
        spawnEgg("grottoceratops", ACEntityRegistry.GROTTOCERATOPS, 0XAC3B03,0XD39B4E);
        spawnEgg("trilocaris", ACEntityRegistry.TRILOCARIS, 0X713E0D,0X8B2010);
        spawnEgg("tremorsaurus", ACEntityRegistry.TREMORSAURUS, 0X53780E,0XDFA211);
        spawnEgg("relicheirus", ACEntityRegistry.RELICHEIRUS, 0X6AE4F9,0X5B2152);
    }

    private static void spawnEgg(String entityName, RegistryObject type, int color1, int color2){
        DEF_REG.register("spawn_egg_" + entityName, () -> new ForgeSpawnEggItem(type, color1,color2, new Item.Properties()));

    }
}
