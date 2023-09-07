package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.citadel.server.message.PropertiesMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.checkerframework.checker.units.qual.C;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CaveBookProgress {

    public static final String PLAYER_CAVE_BOOK_PROGRESS_TAG = "AlexsCavesBookProgress";

    private Map<String, Subcategory> unlockedPages = new HashMap<>();

    private CaveBookProgress(CompoundTag tag) {
        if(tag.contains("Pages")){
            ListTag listTag = tag.getList("Pages", 10);
            for(int i = 0; i < listTag.size(); ++i) {
                CompoundTag compoundtag = listTag.getCompound(i);
                unlockedPages.put(compoundtag.getString("Category"), Subcategory.getByOrdinal(compoundtag.getInt("SubCategory")));
            }
        }
    }

    public static CaveBookProgress getCaveBookProgress(Player player){
        CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(player);
        CompoundTag tag1 = tag.contains(PLAYER_CAVE_BOOK_PROGRESS_TAG) ? tag.getCompound(PLAYER_CAVE_BOOK_PROGRESS_TAG) : new CompoundTag();
        return new CaveBookProgress(tag1);
    }

    public static void saveCaveBookProgress(CaveBookProgress caveBookProgress, Player player){
        CompoundTag savedTag = caveBookProgress.save();
        CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(player);
        tag.put(PLAYER_CAVE_BOOK_PROGRESS_TAG, savedTag);
        CitadelEntityData.setCitadelTag(player, tag);
        if (!player.level().isClientSide) {
            Citadel.sendMSGToAll(new PropertiesMessage("CitadelTagUpdate", tag, player.getId()));
        }else{
            Citadel.sendMSGToServer(new PropertiesMessage("CitadelTagUpdate", tag, player.getId()));
        }
    }

    private CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        for(Map.Entry<String, Subcategory> entry : unlockedPages.entrySet()){
            CompoundTag innerTag = new CompoundTag();
            innerTag.putString("Category", entry.getKey());
            innerTag.putInt("SubCategory", entry.getValue().ordinal());
            listTag.add(innerTag);
        }
        tag.put("Pages", listTag);
        return tag;
    }


    public boolean unlockNextFor(String biomeCategory){
        int prev = unlockedPages.getOrDefault(biomeCategory, Subcategory.EMPTY).ordinal();
        if(prev >= Subcategory.values().length - 1){
            return false;
        }else{
            unlockedPages.put(biomeCategory, Subcategory.getByOrdinal(prev + 1));
            return true;
        }
    }

    public boolean isUnlockedFor(String biomeCategory, Subcategory subcategory){
        int prev = unlockedPages.getOrDefault(biomeCategory, Subcategory.EMPTY).ordinal();
        return subcategory.ordinal() <= prev;
    }


    public Subcategory getLastUnlockedCategory(String biomeCategory){
        return unlockedPages.getOrDefault(biomeCategory, Subcategory.EMPTY);
    }

    public boolean isUnlockedFor(String key){
        int lastIndexOfUnderscore = key.lastIndexOf("_");
        if(lastIndexOfUnderscore >= 0 && lastIndexOfUnderscore + 1 < key.length()){
            String biomeCategory = key.substring(0, lastIndexOfUnderscore);
            String subCatStr = key.substring(lastIndexOfUnderscore + 1);
            Subcategory subcategory = Subcategory.valueOf(subCatStr.toUpperCase(Locale.ROOT));
            return isUnlockedFor(biomeCategory, subcategory);
        }
        return false;
    }

    public enum Subcategory {
        EMPTY,
        GENERAL,
        ITEMS,
        BLOCKS,
        MOBS;

        public static Subcategory getByOrdinal(int subCategory) {
            return Subcategory.values()[Mth.clamp(subCategory, 0, Subcategory.values().length - 1)];
        }
    }
}
