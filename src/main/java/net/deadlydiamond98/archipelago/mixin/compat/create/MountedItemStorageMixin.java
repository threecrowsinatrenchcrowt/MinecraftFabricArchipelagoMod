package net.deadlydiamond98.archipelago.mixin.compat.create;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.content.contraptions.Contraption;
import net.deadlydiamond98.archipelago.util.APItemAccessUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.structure.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MountedItemStorage.class)
public class MountedItemStorageMixin {
    @Inject(method = "handleInteraction", at = @At("HEAD"), cancellable = true)
    void interceptInteraction(ServerPlayerEntity player, Contraption contraption, StructureTemplate.StructureBlockInfo info, CallbackInfoReturnable<Boolean> cir) {
        if (APItemAccessUtil.disallowUsingBlock(player, info.state())) {
            cir.setReturnValue(false);
        }
    }
}
