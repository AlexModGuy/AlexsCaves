package com.github.alexmodguy.alexscaves.server.level.storage;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.living.LuxtructosaurusEntity;
import com.github.alexmodguy.alexscaves.server.level.map.CaveBiomeMapWorldWorker;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.common.WorldWorkerManager;
import net.minecraftforge.common.world.ForgeChunkManager;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ACWorldData extends SavedData {

    private static final String IDENTIFIER = "alexscaves_world_data";
    private Map<UUID, Integer> deepOneReputations = new HashMap<>();
    private boolean primordialBossDefeatedOnce = false;
    private long firstPrimordialBossDefeatTimestamp = -1;
    private Set<Integer> trackedLuxtructosaurusIds = new ObjectArraySet();

    private CaveBiomeMapWorldWorker lastMapWorker = null;

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
        data.primordialBossDefeatedOnce = nbt.getBoolean("PrimordialBossDefeatedOnce");
        data.firstPrimordialBossDefeatTimestamp = nbt.getLong("FirstPrimordialBossDefeatTimestamp");
        data.trackedLuxtructosaurusIds = Arrays.stream(nbt.getIntArray("TrackedLuxtructosaurusIds")).boxed().collect(Collectors.toSet());
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        if (!this.deepOneReputations.isEmpty()) {
            ListTag listTag = new ListTag();
            for (Map.Entry<UUID, Integer> reputations : deepOneReputations.entrySet()) {
                CompoundTag tag = new CompoundTag();
                tag.putUUID("UUID", reputations.getKey());
                tag.putInt("Reputation", reputations.getValue());
                listTag.add(tag);
            }
            compound.put("DeepOneReputations", listTag);
        }
        compound.putBoolean("PrimordialBossDefeatedOnce", primordialBossDefeatedOnce);
        compound.putLong("FirstPrimordialBossDefeatTimestamp", firstPrimordialBossDefeatTimestamp);
        compound.putIntArray("TrackedLuxtructosaurusIds", trackedLuxtructosaurusIds.stream().mapToInt(Integer::intValue).toArray());
        return compound;
    }

    public int getDeepOneReputation(@Nullable UUID uuid) {
        return uuid == null ? 0 : deepOneReputations.getOrDefault(uuid, 0);
    }

    public void setDeepOneReputation(UUID uuid, int reputation) {
        deepOneReputations.put(uuid, Mth.clamp(reputation, -100, 100));
    }

    public boolean isPrimordialBossActive(Level level){
        for(int i : trackedLuxtructosaurusIds){
            if(level.getEntity(i) instanceof LuxtructosaurusEntity lux && lux.isAlive() && lux.isLoadedInWorld()){
                return true;
            }
        }
        return false;
    }

    public void trackPrimordialBoss(int id, boolean add){
        if(add){
            trackedLuxtructosaurusIds.add(id);
        }else{
            trackedLuxtructosaurusIds.remove(id);
        }
    }


    public boolean isPrimordialBossDefeatedOnce(){
        return primordialBossDefeatedOnce;
    }

    public void setPrimordialBossDefeatedOnce(boolean defeatedOnce){
        this.primordialBossDefeatedOnce = defeatedOnce;
    }

    public long getFirstPrimordialBossDefeatTimestamp(){
        return firstPrimordialBossDefeatTimestamp;
    }

    public void setFirstPrimordialBossDefeatTimestamp(long time){
        this.firstPrimordialBossDefeatTimestamp = time;
    }

    public void fillOutCaveMap(UUID uuid, ItemStack map, ServerLevel serverLevel, BlockPos center, Player player){
        if(lastMapWorker != null){
            lastMapWorker.onWorkComplete(lastMapWorker.getLastFoundBiome());
        }
        lastMapWorker = new CaveBiomeMapWorldWorker(map, serverLevel, center, player, uuid);
        WorldWorkerManager.addWorker(lastMapWorker);
    }

    public boolean isCaveMapTicking(){
        return lastMapWorker != null && lastMapWorker.hasWork();
    }

    public static void clearLoadedChunksCallback(ServerLevel serverLevel, ForgeChunkManager.TicketHelper ticketHelper) {
        //remove all forced chunks on server relog
        int i = 0;
        for(Map.Entry<UUID, Pair<LongSet, LongSet>> entry : ticketHelper.getEntityTickets().entrySet()){
            ticketHelper.removeAllTickets(entry.getKey());
            i++;
        }
        for(Map.Entry<BlockPos, Pair<LongSet, LongSet>> entry : ticketHelper.getBlockTickets().entrySet()){
            ticketHelper.removeAllTickets(entry.getKey());
            i++;
        }
        if(i > 0){
            AlexsCaves.LOGGER.debug("unloaded {} forced chunks", i);
        }
    }
}
