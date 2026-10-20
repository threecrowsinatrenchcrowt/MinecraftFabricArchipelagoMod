package net.deadlydiamond98.archipelago.mixin.compat.create;

import com.simibubi.create.content.logistics.funnel.AbstractFunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementKind;
import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementUtil;
import net.deadlydiamond98.archipelago.compat.create.CreateFunnelAccess;
import net.deadlydiamond98.archipelago.util.APItemAccessUtil;
import net.deadlydiamond98.archipelago.util.APMissingItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(FunnelBlockEntity.class)
public abstract class FunnelBlockEntityMixin extends SmartBlockEntity {
    public FunnelBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Coerce
    @Inject(method = "determineCurrentMode", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getOrEmpty(Lnet/minecraft/state/property/Property;)Ljava/util/Optional;"), cancellable = true)
    void determineMode(CallbackInfoReturnable<Object> cir) {
        if (world == null) return;
        BlockState blockState = this.getCachedState();
        Direction facing = AbstractFunnelBlock.getFunnelFacing(blockState);
        if (facing == null) return;
        Direction attachedDirection = facing.getOpposite();
        BlockPos attachedPos = pos.offset(attachedDirection);
        BlockState attachedState = world.getBlockState(attachedPos);
        Optional<APMissingItem> missingItem = APItemAccessUtil.getMissingItemToInteract(attachedState);
        CreateAPRequirementUtil.changeSingleNullable(
                this,
                missingItem.orElse(null),
                CreateAPRequirementKind.ITEM_TRANSPORT
        );
        if (missingItem.isPresent()) cir.setReturnValue(CreateFunnelAccess.PAUSED);
    }
}
