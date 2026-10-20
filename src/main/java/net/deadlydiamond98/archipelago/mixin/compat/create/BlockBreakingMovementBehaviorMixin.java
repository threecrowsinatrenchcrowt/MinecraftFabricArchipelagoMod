package net.deadlydiamond98.archipelago.mixin.compat.create;

import com.simibubi.create.content.kinetics.base.BlockBreakingMovementBehaviour;
import net.deadlydiamond98.archipelago.util.APItemAccessUtil;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBreakingMovementBehaviour.class)
public class BlockBreakingMovementBehaviorMixin {
    @Inject(method = "canBreak", at = @At("HEAD"), cancellable = true)
    void isAPBreakable(World world, BlockPos breakingPos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (APItemAccessUtil.disallowBreakingBlock(null, state)) {
            cir.setReturnValue(false);
        }
    }
}
