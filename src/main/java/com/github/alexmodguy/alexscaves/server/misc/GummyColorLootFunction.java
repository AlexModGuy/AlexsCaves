package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.util.GummyColors;
import com.github.alexmodguy.alexscaves.server.entity.util.HasGummyColors;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

public class GummyColorLootFunction extends LootItemConditionalFunction {

    protected GummyColorLootFunction(LootItemCondition[] lootItemConditions) {
        super(lootItemConditions);
    }

    @Override
    protected ItemStack run(ItemStack stack, @NotNull LootContext context) {
        if(stack.is(ACTagRegistry.GUMMY_ITEMS) && context.hasParam(LootContextParams.THIS_ENTITY) && context.getParam(LootContextParams.THIS_ENTITY) instanceof HasGummyColors hasGummyColors){
            GummyColors color = hasGummyColors.getGummyColor();
            Item replaceItem = stack.getItem();
            if(stack.is(ACItemRegistry.SWEETISH_FISH_RED_BUCKET.get()) || stack.is(ACItemRegistry.SWEETISH_FISH_GREEN_BUCKET.get()) || stack.is(ACItemRegistry.SWEETISH_FISH_YELLOW_BUCKET.get()) || stack.is(ACItemRegistry.SWEETISH_FISH_BLUE_BUCKET.get()) || stack.is(ACItemRegistry.SWEETISH_FISH_PINK_BUCKET.get())){
                switch (color){
                    case RED:
                        replaceItem = ACItemRegistry.SWEETISH_FISH_RED_BUCKET.get();
                        break;
                    case GREEN:
                        replaceItem = ACItemRegistry.SWEETISH_FISH_GREEN_BUCKET.get();
                        break;
                    case YELLOW:
                        replaceItem = ACItemRegistry.SWEETISH_FISH_YELLOW_BUCKET.get();
                        break;
                    case BLUE:
                        replaceItem = ACItemRegistry.SWEETISH_FISH_BLUE_BUCKET.get();
                        break;
                    case PINK:
                        replaceItem = ACItemRegistry.SWEETISH_FISH_PINK_BUCKET.get();
                        break;
                }
            }else if(stack.is(ACItemRegistry.SWEETISH_FISH_RED.get()) || stack.is(ACItemRegistry.SWEETISH_FISH_GREEN.get()) || stack.is(ACItemRegistry.SWEETISH_FISH_YELLOW.get()) || stack.is(ACItemRegistry.SWEETISH_FISH_BLUE.get()) || stack.is(ACItemRegistry.SWEETISH_FISH_PINK.get())){
                switch (color){
                    case RED:
                        replaceItem = ACItemRegistry.SWEETISH_FISH_RED.get();
                        break;
                    case GREEN:
                        replaceItem = ACItemRegistry.SWEETISH_FISH_GREEN.get();
                        break;
                    case YELLOW:
                        replaceItem = ACItemRegistry.SWEETISH_FISH_YELLOW.get();
                        break;
                    case BLUE:
                        replaceItem = ACItemRegistry.SWEETISH_FISH_BLUE.get();
                        break;
                    case PINK:
                        replaceItem = ACItemRegistry.SWEETISH_FISH_PINK.get();
                        break;
                }
            }else if(stack.is(ACItemRegistry.GELATIN_RED.get()) || stack.is(ACItemRegistry.GELATIN_GREEN.get()) || stack.is(ACItemRegistry.GELATIN_YELLOW.get()) || stack.is(ACItemRegistry.GELATIN_BLUE.get()) || stack.is(ACItemRegistry.GELATIN_PINK.get())){
                switch (color){
                    case RED:
                        replaceItem = ACItemRegistry.GELATIN_RED.get();
                        break;
                    case GREEN:
                        replaceItem = ACItemRegistry.GELATIN_GREEN.get();
                        break;
                    case YELLOW:
                        replaceItem = ACItemRegistry.GELATIN_YELLOW.get();
                        break;
                    case BLUE:
                        replaceItem = ACItemRegistry.GELATIN_BLUE.get();
                        break;
                    case PINK:
                        replaceItem = ACItemRegistry.GELATIN_PINK.get();
                        break;
                }
            }else if(stack.is(ACBlockRegistry.GUMMY_RING_RED.get().asItem()) || stack.is(ACBlockRegistry.GUMMY_RING_GREEN.get().asItem()) || stack.is(ACBlockRegistry.GUMMY_RING_YELLOW.get().asItem()) || stack.is(ACBlockRegistry.GUMMY_RING_BLUE.get().asItem()) || stack.is(ACBlockRegistry.GUMMY_RING_PINK.get().asItem())){
                switch (color){
                    case RED:
                        replaceItem = ACBlockRegistry.GUMMY_RING_RED.get().asItem();
                        break;
                    case GREEN:
                        replaceItem = ACBlockRegistry.GUMMY_RING_GREEN.get().asItem();
                        break;
                    case YELLOW:
                        replaceItem = ACBlockRegistry.GUMMY_RING_YELLOW.get().asItem();
                        break;
                    case BLUE:
                        replaceItem = ACBlockRegistry.GUMMY_RING_BLUE.get().asItem();
                        break;
                    case PINK:
                        replaceItem = ACBlockRegistry.GUMMY_RING_PINK.get().asItem();
                        break;
                }
            }
            return new ItemStack(replaceItem, stack.getCount());
        }
        return stack;
    }


    @Override
    public @NotNull LootItemFunctionType getType() {
        return ACLootTableRegistry.GUMMY_COLORS_LOOT_FUNCTION.get();
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<GummyColorLootFunction> {
        public Serializer() {
            super();
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull GummyColorLootFunction functionClazz, @NotNull JsonSerializationContext serializationContext) {
        }

        @Override
        public @NotNull GummyColorLootFunction deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext deserializationContext, LootItemCondition @NotNull [] conditionsIn) {
            return new GummyColorLootFunction(conditionsIn);
        }
    }
}
