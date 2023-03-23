package com.github.alexmodguy.alexscaves.server.level.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import javax.annotation.Nullable;
import java.util.*;

public class ACWorldData extends SavedData {

    private static final String IDENTIFIER = "alexscaves_world_data";
    private Map<UUID, Integer> deepOneReputations = new HashMap<>();
    private ACWorldData() {
        super();
    }

    public static ACWorldData get(Level world) {
        if (world instanceof ServerLevel) {
            ServerLevel overworld = world.getServer().getLevel(Level.OVERWORLD);
            DimensionDataStorage storage = overworld.getDataStorage();
            ACWorldData data = storage.computeIfAbsent(ACWorldData::load, ACWorldData::new, IDENTIFIER);
            if (data != null) {
                data.setDirty();
            }
            return data;
        }
        return null;
    }

    public static ACWorldData load(CompoundTag nbt) {
        ACWorldData data = new ACWorldData();
        if (nbt.contains("DeepOneReputations")) {
            ListTag listtag = nbt.getList("DeepOneReputations", 10);
            for (int i = 0; i < listtag.size(); ++i) {
                CompoundTag innerTag = listtag.getCompound(i);
                data.deepOneReputations.put(innerTag.getUUID("UUID"), innerTag.getInt("Reputation"));
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        if (!this.deepOneReputations.isEmpty()) {
            ListTag listTag = new ListTag();
            for(Map.Entry<UUID, Integer> reputations : deepOneReputations.entrySet()){
                CompoundTag tag = new CompoundTag();
                tag.putUUID("UUID", reputations.getKey());
                tag.putInt("Reputation", reputations.getValue());
                listTag.add(tag);
            }
            compound.put("DeepOneReputations", listTag);
        }
        return compound;
    }

    public int getDeepOneReputation(@Nullable UUID uuid){
        return uuid == null ? 0 : deepOneReputations.getOrDefault(uuid, 0);
    }

    public void setDeepOneReputation(UUID uuid, int reputation){
        deepOneReputations.put(uuid, Mth.clamp(reputation, -100, 100));
    }
}
