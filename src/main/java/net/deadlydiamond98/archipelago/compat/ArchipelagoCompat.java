package net.deadlydiamond98.archipelago.compat;

import java.util.Set;

import net.deadlydiamond98.archipelago.archipelago.Archipelago;
import net.deadlydiamond98.archipelago.compat.create.CreateCompat;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.world.ServerWorld;

public class ArchipelagoCompat {
    private static final boolean NATURES_COMPASS_LOADED =
        FabricLoader.getInstance().isModLoaded("naturescompass");
    private static final boolean EXPLORERS_COMPASS_LOADED =
        FabricLoader.getInstance().isModLoaded("explorerscompass");
    private static final boolean CREATE_LOADED = FabricLoader.getInstance().isModLoaded("create");

    public static void initialize() {
        registerMovementAllowedCheck();
    }

    public static void applyBiomeRestriction(ServerWorld world, Set<String> unlockedBiomes) {
        if (NATURES_COMPASS_LOADED & Archipelago.hasLogicSetting("Natures Compass Logic")) {
            NaturesCompassCompat.restrictTo(world, unlockedBiomes);
        } else if (NATURES_COMPASS_LOADED & (!Archipelago.slotData() | !Archipelago.hasLogicSetting("Natures Compass Logic"))) {
            NaturesCompassCompat.unrestrict();
        }
    }

    public static void applyStructureRestriction(ServerWorld world, Set<String> unlockedStructures) {
        if (EXPLORERS_COMPASS_LOADED & Archipelago.hasLogicSetting("Explorers Compass Logic")) {
            ExplorersCompassCompat.restrictTo(world, unlockedStructures);
        } else if (EXPLORERS_COMPASS_LOADED & (!Archipelago.slotData() | !Archipelago.hasLogicSetting("Explorers Compass Logic"))) {
            ExplorersCompassCompat.unrestrict();
        }
    }

    public static void registerMovementAllowedCheck() {
        if (CREATE_LOADED) {
            CreateCompat.registerMovementAllowedCheck();
        }
    }
}
