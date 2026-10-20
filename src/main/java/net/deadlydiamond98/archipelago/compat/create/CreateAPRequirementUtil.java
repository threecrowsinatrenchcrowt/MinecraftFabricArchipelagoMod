package net.deadlydiamond98.archipelago.compat.create;

import net.deadlydiamond98.archipelago.APMod;
import net.deadlydiamond98.archipelago.compat.create.requirements.CreateDisplayableAPRequirement;
import net.deadlydiamond98.archipelago.networking.s2c.MissingAPItemForCreateS2CPacket;
import net.deadlydiamond98.archipelago.util.APMissingItem;
import net.deadlydiamond98.archipelago.util.mixinterfaces.IDisplayableRequirementAccessor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.annotation.MethodsReturnNonnullByDefault;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@MethodsReturnNonnullByDefault
public class CreateAPRequirementUtil {
    public static void changeSingleNullable(
            BlockEntity blockEntity,
            @Nullable APMissingItem missingItem,
            CreateAPRequirementKind kind
    ) {
        APMissingItem[] array = arrayFromNullable(missingItem);
        changeSingle(blockEntity, array, kind);
    }

    public static void changeSingle(
            BlockEntity blockEntity,
            APMissingItem[] missingItems,
            CreateAPRequirementKind kind
    ) {
        if (!setMissingItems(blockEntity, missingItems, kind)) return;
        if (!(blockEntity.getWorld() instanceof ServerWorld serverWorld)) return;
        MissingAPItemForCreateS2CPacket.send(serverWorld, new BlockPos[] { blockEntity.getPos() }, missingItems, kind);
    }

    public static void clearSingle(BlockEntity blockEntity) {
        IDisplayableRequirementAccessor accessor = getAccessor(blockEntity);
        if (accessor == null) return;
        CreateDisplayableAPRequirement requirement = accessor.archipelago$getDisplayableRequirement();
        if (requirement.missingAPItems.length == 0) return;
        requirement.missingAPItems = new APMissingItem[0];
        if (!(blockEntity.getWorld() instanceof ServerWorld serverWorld)) return;
        MissingAPItemForCreateS2CPacket.send(
                serverWorld,
                new BlockPos[] { blockEntity.getPos() },
                requirement.missingAPItems,
                requirement.getKind()
        );
    }

    public static void changeMultipleNullable(
            BlockEntity[] blockEntities,
            @Nullable APMissingItem missingItem,
            CreateAPRequirementKind kind
    ) {
        APMissingItem[] array = arrayFromNullable(missingItem);
        changeMultiple(blockEntities, array, kind);
    }

    public static void changeMultiple(
            BlockEntity[] blockEntities,
            APMissingItem[] missingItems,
            CreateAPRequirementKind kind
    ) {
        if (blockEntities.length == 0) return;
        boolean needsNotify = false;
        for (BlockEntity blockEntity : blockEntities) {
            needsNotify |= setMissingItems(blockEntity, missingItems, kind);
        }
        if (!needsNotify || !(blockEntities[0].getWorld() instanceof ServerWorld serverWorld)) return;
        BlockPos[] positions = Arrays.stream(blockEntities).map(BlockEntity::getPos).toArray(BlockPos[]::new);
        MissingAPItemForCreateS2CPacket.send(serverWorld, positions, missingItems, kind);
    }

    /// Tries to set the displayable missing items popup's contents. Returns whether anything actually changed.
    /// If nothing changed, this action does not need to be synchronized to the client.
    private static boolean setMissingItems(
            BlockEntity blockEntity,
            APMissingItem[] missingItems,
            CreateAPRequirementKind kind
    ) {
        IDisplayableRequirementAccessor accessor = getAccessor(blockEntity);
        if (accessor == null) return false;
        CreateDisplayableAPRequirement requirement = accessor.archipelago$getDisplayableRequirement();
        if (requirement.getKind() == kind) {
            if (Arrays.equals(requirement.missingAPItems, missingItems)) return false;
            requirement.missingAPItems = missingItems;
        } else {
            accessor.archipelago$setDisplayableRequirement(kind.instantiate(blockEntity, missingItems));
        }
        return true;
    }

    @Nullable
    private static IDisplayableRequirementAccessor getAccessor(BlockEntity blockEntity) {
        if (blockEntity instanceof IDisplayableRequirementAccessor accessor) {
            return accessor;
        } else {
            APMod.LOGGER.error(
                    "Just tried accessing the Displayable Requirement of {} " +
                            "which doesn't implement IDisplayableRequirementAccessor",
                    blockEntity.getClass()
            );
            return null;
        }
    }

    private static APMissingItem[] arrayFromNullable(@Nullable APMissingItem missingItem) {
        if (missingItem == null)
            return new APMissingItem[0];
        else
            return new APMissingItem[] { missingItem };
    }
}
