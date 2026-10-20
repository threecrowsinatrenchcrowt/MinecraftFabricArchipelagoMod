package net.deadlydiamond98.archipelago.mixin.compat.create;

import com.simibubi.create.content.logistics.funnel.AbstractFunnelBlock;
import net.deadlydiamond98.archipelago.util.APItemAccessUtil;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFunnelBlock.class)
public class AbstractFunnelBlockMixin {
    @Inject(method = "tryInsert", at = @At("HEAD"), cancellable = true)
    private static void onInsert(World worldIn, BlockPos pos, ItemStack toInsert, boolean simulate, CallbackInfoReturnable<ItemStack> cir) {
        BlockState state = worldIn.getBlockState(pos);
        Direction facing = AbstractFunnelBlock.getFunnelFacing(state);
        if (facing == null) return;
        Direction attachedDirection = facing.getOpposite();
        BlockPos attachedPos = pos.offset(attachedDirection);
        BlockState attachedState = worldIn.getBlockState(attachedPos);
        if (APItemAccessUtil.disallowUsingBlock(null, attachedState)) cir.setReturnValue(toInsert);
    }
}
