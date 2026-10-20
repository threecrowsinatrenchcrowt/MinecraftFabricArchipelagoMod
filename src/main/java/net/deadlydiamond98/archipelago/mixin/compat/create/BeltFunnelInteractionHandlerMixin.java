package net.deadlydiamond98.archipelago.mixin.compat.create;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.belt.transport.BeltFunnelInteractionHandler;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import net.deadlydiamond98.archipelago.compat.create.CreateFunnelAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.InvocationTargetException;

@Mixin(BeltFunnelInteractionHandler.class)
public class BeltFunnelInteractionHandlerMixin {
    @Inject(method = "checkForFunnels", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/funnel/FunnelBlockEntity;getBehaviour(Lcom/simibubi/create/foundation/blockEntity/behaviour/BehaviourType;)Lcom/simibubi/create/foundation/blockEntity/behaviour/BlockEntityBehaviour;", ordinal = 0), cancellable = true)
    private static void checkPaused(BeltInventory beltInventory, TransportedItemStack currentItem, float nextOffset, CallbackInfoReturnable<Boolean> cir, @Local(name = "funnelBE") FunnelBlockEntity funnelBE) {
        try {
            assert CreateFunnelAccess.determineCurrentMode != null;
            if (CreateFunnelAccess.determineCurrentMode.invoke(funnelBE) == CreateFunnelAccess.PAUSED)
                cir.setReturnValue(true);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
