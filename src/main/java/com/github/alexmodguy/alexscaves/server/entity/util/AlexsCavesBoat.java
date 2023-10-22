package com.github.alexmodguy.alexscaves.server.entity.util;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.function.Supplier;

public interface AlexsCavesBoat {

    AlexsCavesBoat.Type getACBoatType();

    enum Type {
        PEWEN("pewen", ACBlockRegistry.PEWEN_PLANKS, ACItemRegistry.PEWEN_BOAT, ACItemRegistry.PEWEN_CHEST_BOAT),
        THORNWOOD("thornwood", ACBlockRegistry.PEWEN_PLANKS, ACItemRegistry.THORNWOOD_BOAT, ACItemRegistry.THORNWOOD_CHEST_BOAT);

        private final String name;
        private final Supplier<Block> plankSupplier;
        private final Supplier<Item> dropSupplier;
        private final Supplier<Item> chestDropSupplier;

        Type(String name, Supplier<Block> plankSupplier, Supplier<Item> dropSupplier, Supplier<Item> chestDropSupplier) {
            this.name = name;
            this.plankSupplier = plankSupplier;
            this.dropSupplier = dropSupplier;
            this.chestDropSupplier = chestDropSupplier;
        }

        public String getName() {
            return this.name;
        }

        public Supplier<Block> getPlankSupplier() {
            return this.plankSupplier;
        }


        public Supplier<Item> getDropSupplier() {
            return this.dropSupplier;
        }

        public Supplier<Item> getChestDropSupplier() {
            return this.chestDropSupplier;
        }

        public String toString() {
            return this.name;
        }

        public static Type byName(String name) {
            return Arrays.stream(values()).filter(t -> t.getName().equals(name)).findFirst().orElse(values()[0]);
        }

        public static Type byId(int id) {
            return values()[id < 0 || id >= values().length ? 0 : id];
        }
    }
}
