package net.deadlydiamond98.archipelago.compat.create.requirements;

import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementKind;
import net.deadlydiamond98.archipelago.util.APMissingItem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.annotation.MethodsReturnNonnullByDefault;

@MethodsReturnNonnullByDefault
public class CreateCraftingAPRequirement extends CreateSingleAPRequirement {
    public CreateCraftingAPRequirement(BlockEntity blockEntity) {
        super(blockEntity);
    }

    public CreateCraftingAPRequirement(BlockEntity blockEntity, APMissingItem[] missingAPItems) {
        super(blockEntity, missingAPItems);
    }

    @Override
    public CreateAPRequirementKind getKind() {
        return CreateAPRequirementKind.CRAFTING;
    }

    @Override
    protected String getTranslationSegment() {
        return "crafting";
    }
}
