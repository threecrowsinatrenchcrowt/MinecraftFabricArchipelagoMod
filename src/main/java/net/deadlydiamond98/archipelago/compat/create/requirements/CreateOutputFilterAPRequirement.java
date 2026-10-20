package net.deadlydiamond98.archipelago.compat.create.requirements;

import net.deadlydiamond98.archipelago.archipelago.items.SavedArchipelagoItems;
import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementKind;
import net.deadlydiamond98.archipelago.util.APMissingItem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.annotation.MethodsReturnNonnullByDefault;

import java.util.Arrays;
import java.util.List;

/// Displays a list of outputs which will be filtered out if their AP item requirements aren't met
@MethodsReturnNonnullByDefault
public class CreateOutputFilterAPRequirement extends CreateDisplayableAPRequirement {
    public CreateOutputFilterAPRequirement(BlockEntity blockEntity) {
        super(blockEntity);
    }

    public CreateOutputFilterAPRequirement(BlockEntity blockEntity, APMissingItem[] missingAPItems) {
        super(blockEntity, missingAPItems);
    }

    @Override
    public CreateAPRequirementKind getKind() {
        return CreateAPRequirementKind.OUTPUT_FILTER;
    }

    @Override
    protected boolean renderTooltip(List<Text> tooltip) {
        long unlockedItems = Arrays.stream(missingAPItems).filter(i -> !i.isStillMissing()).count();
        if (unlockedItems == missingAPItems.length)
            return false; // State has changed in the meantime and the popup is no longer necessary, or we have 0 items

        langBuilder()
                .translate("compat.create.crafting.some_missing.heading")
                .style(Formatting.GOLD)
                .forGoggles(tooltip);

        MutableText description = Text.translatable(
                "archipelago.compat.create.crafting.some_missing.description",
                unlockedItems,
                missingAPItems.length
        );
        cutAndAddTextComponent(description, tooltip);

        for (APMissingItem missingItem : missingAPItems) {
            String translationKey = String.format(
                    "compat.create.crafting.some_missing.item.%s",
                    missingItem.isStillMissing()
                            ? "locked"
                            : "unlocked"
            );
            String reqTranslationKey = String.format(
                    "compat.create.crafting.some_missing.requirement%s",
                    missingItem.amount == 0 ? "" : ".progressive"
            );
            String itemName = SavedArchipelagoItems.ID_TO_NAME_MAP.get(missingItem.name);
            Formatting formatting = missingItem.isStillMissing() ? Formatting.RED : Formatting.GREEN;

            tooltip.add(Text.empty());
            langBuilder()
                    .translate(translationKey, missingItem.attemptedCraft)
                    .style(formatting)
                    .forGoggles(tooltip, 2);
            langBuilder()
                    .translate(reqTranslationKey, itemName, missingItem.amount)
                    .style(formatting)
                    .forGoggles(tooltip, 4);
        }
        return true;
    }
}
