package net.deadlydiamond98.archipelago.compat.create.requirements;

import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementKind;
import net.deadlydiamond98.archipelago.util.APMissingItem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.annotation.MethodsReturnNonnullByDefault;

@MethodsReturnNonnullByDefault
public class CreateItemTransportAPRequirement extends CreateSingleAPRequirement {
    public CreateItemTransportAPRequirement(BlockEntity blockEntity) {
        super(blockEntity);
    }

    public CreateItemTransportAPRequirement(BlockEntity blockEntity, APMissingItem[] missingAPItems) {
        super(blockEntity, missingAPItems);
    }

    @Override
    public CreateAPRequirementKind getKind() {
        return CreateAPRequirementKind.ITEM_TRANSPORT;
    }

    @Override
    protected String getTranslationSegment() {
        return "item_transport";
    }
}
