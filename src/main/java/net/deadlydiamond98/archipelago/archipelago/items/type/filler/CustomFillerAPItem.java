package net.deadlydiamond98.archipelago.archipelago.items.type.filler;

import net.minecraft.util.Identifier;
import net.deadlydiamond98.archipelago.archipelago.Archipelago;
import net.deadlydiamond98.archipelago.archipelago.items.type.AbstractAPItem;
import net.deadlydiamond98.archipelago.init.APItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.registry.Registries;

public class CustomFillerAPItem extends AbstractAPItem {
    public CustomFillerAPItem() {
    }

    @Override
    public void applyReward(ServerPlayerEntity player) {
        String itemId = "archipelago:essence_of_archipelago"; //default when slot data cannot be read
        Archipelago.MCSlotData slot = Archipelago.getSlotData();
        if(slot != null)
            itemId = slot.custom_single_filler;
        Identifier ident = new Identifier(itemId);
        Item item = APItems.ESSENCE_OF_ARCHIPELAGO; //default when value contained in slot data is invalid
        if(Registries.ITEM.containsId(ident))
            item = Registries.ITEM.get(ident);
        ItemStack stack = new ItemStack(item, 1);
        giveItem(player, stack);
    }
}
