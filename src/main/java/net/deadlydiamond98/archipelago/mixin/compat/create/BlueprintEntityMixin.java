package net.deadlydiamond98.archipelago.mixin.compat.create;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.equipment.blueprint.BlueprintEntity;
import net.deadlydiamond98.archipelago.util.APItemAccessUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("UnstableApiUsage") // We are required to use unstable APIs, because Create does
@Mixin(BlueprintEntity.class)
public class BlueprintEntityMixin {
    @Inject(method = "interactAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 1), cancellable = true)
    void checkAllowCraft(PlayerEntity player, Vec3d vec, Hand hand, CallbackInfoReturnable<ActionResult> cir, @Local(name = "result") ItemStack result, @Local(name = "t") Transaction t) {
        if (!APItemAccessUtil.allowCraftOrUse(player, result.getItem())) {
            t.abort();
            cir.setReturnValue(ActionResult.FAIL);
        }
    }
}
