package net.deadlydiamond98.archipelago.util;

import net.deadlydiamond98.archipelago.archipelago.items.SavedArchipelagoItems;
import net.deadlydiamond98.archipelago.common.world.APPersistentState;
import net.deadlydiamond98.archipelago.init.APTags;
import net.deadlydiamond98.koalalib.init.KoalaLibSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class APItemAccessUtil {
    public static final Map<String, Set<Item>> BOOLEAN_ITEMS = new HashMap<>();
    public static final Map<String, HashMap<Item, Integer>> PROGRESSIVE_ITEMS = new HashMap<>();

    public static final Set<String> PROGRESSIVE_ITEM_IDS = new HashSet<>();
    public static final Set<String> BOOLEAN_ITEM_IDS = new HashSet<>();

    public static boolean disallowUsingBlock(@Nullable PlayerEntity player, BlockState state) {
        return state.isIn(APTags.ARCHIPELAGO_LOCK_INTERACTION) && !APItemAccessUtil.allowCraftOrUse(player, state.getBlock().asItem());
    }

    public static boolean disallowBreakingBlock(@Nullable PlayerEntity player, BlockState state) {
        return state.isIn(APTags.ARCHIPELAGO_LOCK_BREAKING) && !APItemAccessUtil.allowCraftOrUse(player, state.getBlock().asItem());
    }

    public static boolean disallowBreakingBlock(@Nullable PlayerEntity player, Block block) {
        return block.getDefaultState().isIn(APTags.ARCHIPELAGO_LOCK_BREAKING) && !APItemAccessUtil.allowCraftOrUse(player, block.asItem());
    }

    public static Optional<APMissingItem> getMissingItemToBreak(BlockState state) {
        if (!state.isIn(APTags.ARCHIPELAGO_LOCK_BREAKING)) return Optional.empty();
        return getMissingItemFor(state.getBlock().asItem());
    }

    public static Optional<APMissingItem> getMissingItemToInteract(BlockState state) {
        if (!state.isIn(APTags.ARCHIPELAGO_LOCK_INTERACTION)) return Optional.empty();
        return getMissingItemFor(state.getBlock().asItem());
    }

    /**
     * Checks if an item is unlocked
     * @param player the player
     * @param item the item to check
     */
    public static boolean allowCraftOrUse(@Nullable PlayerEntity player, ItemConvertible item) {
        Optional<APMissingItem> potentialMissingItem = getMissingItemFor(item);
        potentialMissingItem.ifPresent(missingItem -> sendRequiresText(player, missingItem.name, missingItem.amount));
        return potentialMissingItem.isEmpty();
    }

    public static Optional<APMissingItem> getMissingItemFor(ItemConvertible item) {
        AtomicReference<APMissingItem> missingItem = new AtomicReference<>(null);
        Item itemItem = item.asItem();
        Text itemName = itemItem.getName();
        PROGRESSIVE_ITEMS.forEach((key, items) -> {
            if (checkIfProgressiveRecipeUnlocked(itemItem, APPersistentState.get().getIntCheckValue(key), items)) {
                missingItem.compareAndSet(null, new APMissingItem(key, getTierForProgressive(key, itemItem), itemName));
            }
        });
        BOOLEAN_ITEMS.forEach((key, items) -> {
            if (items.contains(itemItem) && !APPersistentState.get().getBooleanCheckValue(key)) {
                missingItem.compareAndSet(null, new APMissingItem(key, 0, itemName));
            }
        });

        return Optional.ofNullable(missingItem.get());
    }

    /// Similar to {@link getMissingItemFor} except it also returns an item if its requirements were satisfied
    /// with {@link APMissingItem::isMissing} set to `false`
    public static Optional<APMissingItem> getRequirementFor(ItemConvertible item) {
        // Prioritize missing requirements first, check unlocked ones after.
        // Currently, I don't think items are supposed to have multiple requirements,
        // but it's probably better to handle this case than to falsely display
        // that an item is unlocked when it has another requirement, causing confusion.
        Optional<APMissingItem> potentialMissingItem = getMissingItemFor(item);
        if (potentialMissingItem.isPresent()) return potentialMissingItem;

        // Find the first available check (which will have been found at this point)
        AtomicReference<APMissingItem> obtainedItem = new AtomicReference<>(null);
        Item itemItem = item.asItem();
        Text itemName = itemItem.getName();
        PROGRESSIVE_ITEMS.forEach((key, items) -> {
            Integer tier = items.get(itemItem);
            if (tier != null) {
                obtainedItem.compareAndSet(null, new APMissingItem(key, tier, itemName));
            }
        });
        BOOLEAN_ITEMS.forEach((key, items) -> {
            if (items.contains(itemItem)) {
                obtainedItem.compareAndSet(null, new APMissingItem(key, 0, itemName));
            }
        });

        return Optional.ofNullable(obtainedItem.get());
    }

    public static int getTierForProgressive(String check, Item item) {
        HashMap<Item, Integer> map = PROGRESSIVE_ITEMS.get(check);
        if (map != null) {
            return map.getOrDefault(item, 0);
        }
        return 0;
    }

    /**
     * Checks if the player has a check, and sends a message if they don't
     * @param player the player
     * @param check the name of the check
     * @param amount the amount of the check the player needs
     */
    public static boolean hasProgressiveCheck(@Nullable PlayerEntity player, String check, int amount) {
        if (APPersistentState.get().getIntCheckValue(check) >= amount) {
            return true;
        }
        sendRequiresText(player, check, amount);
        return false;
    }

    /**
     * Checks if the player has a check, and sends a message if they don't
     * @param player the player
     * @param check the name of the check
     * @param item the item that's associated with the check
     */
    public static boolean hasProgressiveCheck(@Nullable PlayerEntity player, String check, ItemConvertible item) {
        return hasProgressiveCheck(player, check, getTierForProgressive(check, item.asItem()));
    }

    /**
     * Checks if the player has a check, and sends a message if they don't
     * @param player the player
     * @param check the name of the check
     */
    public static boolean hasCheck(@Nullable PlayerEntity player, String check) {
        if (APPersistentState.get().getBooleanCheckValue(check)) {
            return true;
        }
        sendRequiresText(player, check, 1);
        return false;
    }

    /**
     * Sends Message to the player to inform them that they're missing the required Check
     * @param player the player
     * @param check the name of the check
     */
    public static void sendRequiresText(@Nullable PlayerEntity player, String check, int amount) {
        if (player != null) {
            if (!player.getWorld().isClient()) {
                String checkName = SavedArchipelagoItems.ID_TO_NAME_MAP.get(check);
                String extraInfo = amount > 1 ? " (x" + amount + ")" : "";

                player.sendMessage(
                        Text.translatable("archipelago.check.requires_check", checkName + extraInfo)
                                .setStyle(Style.EMPTY.withColor(0xFF0000)),
                        true
                );
                player.playSound(KoalaLibSounds.CONSOLE_CRAFT_FAIL, SoundCategory.PLAYERS, 1, 1);
            }
        }
    }

    private static boolean checkIfProgressiveRecipeUnlocked(ItemConvertible item, int lvl, Map<Item, Integer> map) {
        Integer tier = map.get(item.asItem());
        return !(tier == null || lvl >= tier);
    }
}
