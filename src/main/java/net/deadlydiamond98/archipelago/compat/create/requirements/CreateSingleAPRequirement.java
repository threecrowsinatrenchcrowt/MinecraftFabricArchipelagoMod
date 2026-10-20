package net.deadlydiamond98.archipelago.compat.create.requirements;

import net.deadlydiamond98.archipelago.archipelago.items.SavedArchipelagoItems;
import net.deadlydiamond98.archipelago.util.APMissingItem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.annotation.MethodsReturnNonnullByDefault;

import java.util.List;

/// Displays a single missing AP item requirement
@MethodsReturnNonnullByDefault
public abstract class CreateSingleAPRequirement extends CreateDisplayableAPRequirement {
    protected CreateSingleAPRequirement(BlockEntity blockEntity) {
        super(blockEntity);
    }

    protected CreateSingleAPRequirement(BlockEntity blockEntity, APMissingItem[] missingAPItems) {
        super(blockEntity, missingAPItems);
    }

    protected abstract String getTranslationSegment();

    @Override
    protected boolean renderTooltip(List<Text> tooltip) {
        if (this.missingAPItems.length == 0) return false;
        APMissingItem missingItem = this.missingAPItems[0];
        String itemName = SavedArchipelagoItems.ID_TO_NAME_MAP.get(missingItem.name);

        langBuilder()
                .translate("check.requires_check", itemName)
                .style(Formatting.GOLD)
                .forGoggles(tooltip);

        String translationKey = String.format(
                "archipelago.compat.create.%s.requires_check%s",
                this.getTranslationSegment(),
                missingItem.amount == 0 ? "" : ".progressive"
        );
        MutableText description = Text.translatable(translationKey, missingItem.attemptedCraft, itemName, missingItem.amount);
        cutAndAddTextComponent(description, tooltip);

        return true;
    }
}
