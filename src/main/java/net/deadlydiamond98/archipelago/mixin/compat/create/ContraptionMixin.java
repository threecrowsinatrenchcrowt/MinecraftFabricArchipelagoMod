package net.deadlydiamond98.archipelago.mixin.compat.create;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.contraptions.Contraption;
import net.deadlydiamond98.archipelago.util.APItemAccessUtil;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Contraption.class)
public class ContraptionMixin {
    @WrapOperation(method = "addBlocksToWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getHardness(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F"))
    private float wrapHardnessCheck(BlockState instance, BlockView blockView, BlockPos blockPos, Operation<Float> original) {
        if (APItemAccessUtil.disallowBreakingBlock(null, instance)) {
            return -1.0F;
        } else {
            return original.call(instance, blockView, blockPos);
        }
    }
}
