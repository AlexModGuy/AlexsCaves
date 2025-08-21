package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexthe666.citadel.client.rewards.CitadelCapes;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ACPlayerCapes {

    private static final ResourceLocation DEVELOPER_CAPE_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/cape/developer.png");
    private static final ResourceLocation CONTRIBUTOR_CAPE_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/cape/contributor.png");
    private static final List<UUID> DEVS = List.of(
            UUID.fromString("380df991-f603-344c-a090-369bad2a924a"), /*Dev*/
            UUID.fromString("4a463319-625c-4b86-a4e7-8b700f023a60"), /*Noonyeyz*/
            UUID.fromString("71363abe-fd03-49c9-940d-aae8b8209b7c") /*Alexthe666*/
    );

    private static final List<UUID> CONTRIBUTORS = List.of(
            UUID.fromString("2d173722-de6b-4bb8-b21b-b2843cfe395d"), /*_Ninni*/
            UUID.fromString("ce9dd341-b1c2-44d9-a014-71e11d163b01"), /*LudoCrypt*/
            UUID.fromString("0ca35240-695b-4f24-a37b-f48e7354b6fc"), /*Ron0*/
            UUID.fromString("24df449f-1f8f-4daf-b5d4-4afeb0491e49"), /*PrismaticPinky*/
            UUID.fromString("a8bf405c-4cf3-4f0b-a9dd-11708ef41b62"), /*Kotshi*/
            UUID.fromString("c4fd7b83-0e37-4fca-b920-19d5923999e1") /*Arby698*/
    );

    private static final List<UUID> COMMUNITY_ORGANIZERS = List.of(
            UUID.fromString("8c1af44c-d02a-42e8-8ae6-e3f2132acbbf"), /*Plummet_Studios*/
            UUID.fromString("7058b4a0-c527-4667-9162-d816e42ebf75"), /*HolidayTheRaptor*/
            UUID.fromString("3562ab33-f01b-4801-aab5-807f3750ded1"), /*AbysswalkerDeno*/
            UUID.fromString("befc934f-f684-4049-8f47-7b5a3727dbc6") /*PCAwesomeness*/
    );

    public static void setup() {
        CitadelCapes.addCapeFor(DEVS, "alexscaves_developer", DEVELOPER_CAPE_TEXTURE);
        List<UUID> contributorCapes = new ArrayList<>();
        contributorCapes.addAll(DEVS);
        contributorCapes.addAll(CONTRIBUTORS);
        CitadelCapes.addCapeFor(contributorCapes, "alexscaves_contributor", CONTRIBUTOR_CAPE_TEXTURE);
    }

    public static List<UUID> getAll(){
        List<UUID> list = new ArrayList<>();
        list.addAll(DEVS);
        list.addAll(CONTRIBUTORS);
        list.addAll(COMMUNITY_ORGANIZERS);
        return list;
    }
}
