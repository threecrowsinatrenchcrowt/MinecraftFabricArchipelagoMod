package net.deadlydiamond98.archipelago.mixin.compat.create;

import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementKind;
import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementUtil;
import net.deadlydiamond98.archipelago.util.APItemAccessUtil;
import net.deadlydiamond98.archipelago.util.APMissingItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(BlockBreakingKineticBlockEntity.class)
public class BlockBreakingKineticBlockEntityMixin {
    @Inject(method = "canBreak", at = @At("HEAD"), cancellable = true)
    void isAPBreakable(BlockState stateToBreak, float blockHardness, CallbackInfoReturnable<Boolean> cir) {
        Optional<APMissingItem> potentialMissingItem = APItemAccessUtil.getMissingItemToBreak(stateToBreak);
        CreateAPRequirementUtil.changeSingleNullable(
                (BlockEntity) (Object) this,
                potentialMissingItem.orElse(null),
                CreateAPRequirementKind.BREAKING
        );
        if (potentialMissingItem.isPresent()) {
            cir.setReturnValue(false);
        }
    }
}
