package net.deadlydiamond98.archipelago.mixin.compat.create;

import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementKind;
import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementUtil;
import net.deadlydiamond98.archipelago.util.APItemAccessUtil;
import net.deadlydiamond98.archipelago.util.APMissingItem;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.stream.Stream;

@Mixin(ChuteBlockEntity.class)
public abstract class ChuteBlockEntityMixin extends SmartBlockEntity {
    @Unique
    private boolean archipelago$noInteractUp = false;
    @Unique
    private boolean archipelago$noInteractDown = false;

    public ChuteBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "tick", at = @At(value = "HEAD"), remap = false)
    void checkAllowedInteractionDirections(CallbackInfo ci) {
        if (world == null) return;
        // I tried to insert an optimization here that only updates these when they would be used,
        // but I couldn't get it to work reasonably well.
        // This gets checked nearly every tick anyway so it's hopefully fine
        Optional<APMissingItem> missingItemUp = APItemAccessUtil.getMissingItemToInteract(world.getBlockState(pos.up()));
        Optional<APMissingItem> missingItemDown = APItemAccessUtil.getMissingItemToInteract(world.getBlockState(pos.down()));
        archipelago$noInteractUp = missingItemUp.isPresent();
        archipelago$noInteractDown = missingItemDown.isPresent();
        APMissingItem[] missingItems = Stream.concat(missingItemUp.stream(), missingItemDown.stream()).toArray(APMissingItem[]::new);
        CreateAPRequirementUtil.changeSingle(this, missingItems, CreateAPRequirementKind.ITEM_TRANSPORT);
    }

    @Inject(method = "grabCapability", at = @At("HEAD"), cancellable = true)
    void filterBlockEntity(Direction side, CallbackInfoReturnable<Storage<ItemVariant>> cir) {
        if ((side == Direction.UP && archipelago$noInteractUp)
                || (side == Direction.DOWN && archipelago$noInteractDown))
            cir.setReturnValue(null);
    }

    @Inject(method = "handleDownwardOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;hasTopRim(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"), cancellable = true)
    void checkDownwardLockedBlockObstruction(boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        checkLockedBlockObstruction(this.pos.down(), cir);
    }

    @Inject(method = "handleUpwardOutput", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/BlockHelper;hasBlockSolidSide(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z"), cancellable = true)
    void checkUpwardLockedBlockObstruction(boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        checkLockedBlockObstruction(this.pos.up(), cir);
    }

    @Unique
    void checkLockedBlockObstruction(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (world == null) return;
        BlockState state = world.getBlockState(pos);
        Optional<APMissingItem> potentialMissingItem = APItemAccessUtil.getMissingItemToInteract(state);
        if (potentialMissingItem.isPresent())
            cir.setReturnValue(false);
    }
}
