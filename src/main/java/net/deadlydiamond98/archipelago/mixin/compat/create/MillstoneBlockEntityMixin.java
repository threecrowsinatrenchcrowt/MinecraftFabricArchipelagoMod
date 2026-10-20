package net.deadlydiamond98.archipelago.mixin.compat.create;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity;
import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementKind;
import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementUtil;
import net.deadlydiamond98.archipelago.util.APItemAccessUtil;
import net.deadlydiamond98.archipelago.util.APMissingItem;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Optional;

@Mixin(MillstoneBlockEntity.class)
public class MillstoneBlockEntityMixin {
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/AllRecipeTypes;find(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"))
    Optional<MillingRecipe> checkFoundRecipeInTick(AllRecipeTypes instance, Inventory inv, World world, Operation<Optional<MillingRecipe>> original) {
        return archipelago$checkFoundRecipe(instance, inv, world, original);
    }

    @WrapOperation(method = "process", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/AllRecipeTypes;find(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"))
    Optional<MillingRecipe> checkFoundRecipeInProcess(AllRecipeTypes instance, Inventory inv, World world, Operation<Optional<MillingRecipe>> original) {
        return archipelago$checkFoundRecipe(instance, inv, world, original);
    }

    @Inject(method = "lambda$process$0", at = @At("HEAD"), cancellable = true)
    void filterOutput(Transaction t, ItemStack stack, CallbackInfo ci) {
        if (!APItemAccessUtil.allowCraftOrUse(null, stack.getItem())) {
            ci.cancel();
        }
    }

    @Unique
    Optional<MillingRecipe> archipelago$checkFoundRecipe(AllRecipeTypes instance, Inventory inv, World world, Operation<Optional<MillingRecipe>> original) {
        Optional<MillingRecipe> foundRecipe = original.call(instance, inv, world);
        if (foundRecipe.isEmpty()) return foundRecipe;
        APMissingItem[] output = foundRecipe
                .get()
                .getRollableResultsAsItemStacks()
                .stream()
                .map(ItemStack::getItem)
                .distinct()
                .flatMap(i -> APItemAccessUtil.getRequirementFor(i).stream())
                .toArray(APMissingItem[]::new);
        BlockEntity blockEntity = (BlockEntity) (Object) this;
        if (output.length > 0 && Arrays.stream(output).anyMatch(APMissingItem::isStillMissing)) {
            CreateAPRequirementUtil.changeSingle(blockEntity, output, CreateAPRequirementKind.OUTPUT_FILTER);
        } else {
            CreateAPRequirementUtil.clearSingle(blockEntity);
        }
        return foundRecipe;
    }
}
