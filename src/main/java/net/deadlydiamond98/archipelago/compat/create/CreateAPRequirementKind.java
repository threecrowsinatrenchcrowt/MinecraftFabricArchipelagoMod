package net.deadlydiamond98.archipelago.compat.create;

import net.deadlydiamond98.archipelago.compat.create.requirements.*;
import net.deadlydiamond98.archipelago.util.APMissingItem;
import net.minecraft.block.entity.BlockEntity;

public enum CreateAPRequirementKind {
    CRAFTING,
    BREAKING,
    OUTPUT_FILTER,
    ITEM_TRANSPORT;

    CreateDisplayableAPRequirement instantiate(BlockEntity blockEntity, APMissingItem[] missingAPItems) {
        return switch (this) {
            case CRAFTING -> new CreateCraftingAPRequirement(blockEntity, missingAPItems);
            case BREAKING -> new CreateBreakingAPRequirement(blockEntity, missingAPItems);
            case OUTPUT_FILTER -> new CreateOutputFilterAPRequirement(blockEntity, missingAPItems);
            case ITEM_TRANSPORT -> new CreateItemTransportAPRequirement(blockEntity, missingAPItems);
        };
    }
}
