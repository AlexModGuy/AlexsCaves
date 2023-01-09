package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACCreativeTabs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ACItemRegistry {
    private static Map<RegistryObject<Item>, ResourceLocation> creativeTabSpawnEggMap = new HashMap<>();
    public static final DeferredRegister<Item> DEF_REG = DeferredRegister.create(ForgeRegistries.ITEMS, AlexsCaves.MODID);

    public static final RegistryObject<Item> RAW_SCARLET_NEODYMIUM = DEF_REG.register("raw_scarlet_neodymium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_AZURE_NEODYMIUM = DEF_REG.register("raw_azure_neodymium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SCARLET_NEODYMIUM_INGOT = DEF_REG.register("scarlet_neodymium_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> AZURE_NEODYMIUM_INGOT = DEF_REG.register("azure_neodymium_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PEWEN_DOOR = DEF_REG.register("pewen_door", () -> new DoubleHighBlockItem(ACBlockRegistry.PEWEN_DOOR.get(), (new Item.Properties())));
    public static final RegistryObject<Item> PEWEN_SIGN = DEF_REG.register("pewen_sign", () -> new SignItem((new Item.Properties()).stacksTo(16), ACBlockRegistry.PEWEN_SIGN.get(), ACBlockRegistry.PEWEN_WALL_SIGN.get()));
    public static final RegistryObject<Item> ACID_BUCKET = DEF_REG.register("acid_bucket", () -> new BucketItem(ACFluidRegistry.ACID_FLUID_SOURCE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    static {
        spawnEgg("teletor", ACEntityRegistry.TELETOR, 0X433B4A,0X0060EF, ACCreativeTabs.MAGNETIC_CAVES);
        spawnEgg("magnetron", ACEntityRegistry.MAGNETRON, 0XFF002A,0X203070, ACCreativeTabs.MAGNETIC_CAVES);
        spawnEgg("boundroid", ACEntityRegistry.BOUNDROID, 0XBB1919,0XFFFFFF, ACCreativeTabs.MAGNETIC_CAVES);
        spawnEgg("ferrouslime", ACEntityRegistry.FERROUSLIME, 0X26272D,0X53556C, ACCreativeTabs.MAGNETIC_CAVES);
        spawnEgg("subterranodon", ACEntityRegistry.SUBTERRANODON, 0X00B1B2,0XFFF11C, ACCreativeTabs.PRIMORDIAL_CAVES);
        spawnEgg("vallumraptor", ACEntityRegistry.VALLUMRAPTOR, 0X22389A,0XEEE5AB, ACCreativeTabs.PRIMORDIAL_CAVES);
        spawnEgg("grottoceratops", ACEntityRegistry.GROTTOCERATOPS, 0XAC3B03,0XD39B4E, ACCreativeTabs.PRIMORDIAL_CAVES);
        spawnEgg("trilocaris", ACEntityRegistry.TRILOCARIS, 0X713E0D,0X8B2010, ACCreativeTabs.PRIMORDIAL_CAVES);
        spawnEgg("tremorsaurus", ACEntityRegistry.TREMORSAURUS, 0X53780E,0XDFA211, ACCreativeTabs.PRIMORDIAL_CAVES);
        spawnEgg("relicheirus", ACEntityRegistry.RELICHEIRUS, 0X6AE4F9,0X5B2152, ACCreativeTabs.PRIMORDIAL_CAVES);
        spawnEgg("nucleeper", ACEntityRegistry.NUCLEEPER, 0X95A1A5,0X00FF00, ACCreativeTabs.TOXIC_CAVES);
        spawnEgg("radgill", ACEntityRegistry.RADGILL, 0X43302C,0XE8E400, ACCreativeTabs.TOXIC_CAVES);
        spawnEgg("brainiac", ACEntityRegistry.BRAINIAC, 0X3E5136,0XE87C9E, ACCreativeTabs.TOXIC_CAVES);
        spawnEgg("gammaroach", ACEntityRegistry.GAMMAROACH, 0X56682A,0X2A2B19, ACCreativeTabs.TOXIC_CAVES);
    }

    private static void spawnEgg(String entityName, RegistryObject type, int color1, int color2, ResourceLocation tabName){
        RegistryObject<Item> item = DEF_REG.register("spawn_egg_" + entityName, () -> new ForgeSpawnEggItem(type, color1,color2, new Item.Properties()));
        creativeTabSpawnEggMap.put(item, tabName);
    }

    public static List<RegistryObject<Item>> getSpawnEggsForTab(ResourceLocation tabName){
        List<RegistryObject<Item>> list = new ArrayList();
        for(Map.Entry<RegistryObject<Item>, ResourceLocation> entry : creativeTabSpawnEggMap.entrySet()){
            if(entry.getValue().equals(tabName)){
                list.add(entry.getKey());
            }
        }
        return list;
    }
}
