package net.deadlydiamond98.archipelago.util;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

// I would have used a record, but I don't want to write getters for all the fields
/// An item that is required and (potentially) missing for a specific crafting output.
/// Used to display missing item requirements.
public final class APMissingItem {
    /// The name of the AP item required for this crafting output.
    public String name;
    /// The amount of this AP item that is required for this crafting output.
    /// `0` is a sentinel value indicating the item is not progressive.
    public int amount;
    /// The name of the attempted crafting output
    public Text attemptedCraft;

    public APMissingItem(String name, int amount, Text attemptedCraft) {
        this.name = name;
        this.amount = amount;
        this.attemptedCraft = attemptedCraft;
    }

    public void serialize(PacketByteBuf buf) {
        buf.writeString(name);
        buf.writeInt(amount);
        buf.writeText(attemptedCraft);
    }

    public static APMissingItem deserialize(PacketByteBuf buf) {
        String name = buf.readString();
        int amount = buf.readInt();
        Text attemptedCraft = buf.readText();
        return new APMissingItem(name, amount, attemptedCraft);
    }

    public boolean isStillMissing() {
        return !(amount == 0
                ? APItemAccessUtil.hasCheck(null, name)
                : APItemAccessUtil.hasProgressiveCheck(null, name, amount));
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof APMissingItem otherItem
                && this.name.equals(otherItem.name)
                && this.amount == otherItem.amount
                && this.attemptedCraft.equals(otherItem.attemptedCraft);
    }
}
