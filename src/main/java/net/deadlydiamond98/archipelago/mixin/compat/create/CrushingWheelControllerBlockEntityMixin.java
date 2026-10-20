package net.deadlydiamond98.archipelago.mixin.compat.create;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementKind;
import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementUtil;
import net.deadlydiamond98.archipelago.util.APItemAccessUtil;
import net.deadlydiamond98.archipelago.util.APMissingItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Mixin(CrushingWheelControllerBlockEntity.class)
public abstract class CrushingWheelControllerBlockEntityMixin extends SmartBlockEntity {
    public CrushingWheelControllerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @WrapMethod(method = "findRecipe", remap = false)
    Optional<ProcessingRecipe<Inventory>> checkRecipe(Operation<Optional<ProcessingRecipe<Inventory>>> original) {
        Optional<ProcessingRecipe<Inventory>> foundRecipe = original.call();
        if (foundRecipe.isEmpty()) return foundRecipe;
        APMissingItem[] output = foundRecipe
                .get()
                .getRollableResultsAsItemStacks()
                .stream()
                .map(ItemStack::getItem)
                .distinct()
                .flatMap(i -> APItemAccessUtil.getRequirementFor(i).stream())
                .toArray(APMissingItem[]::new);
        if (output.length > 0 && Arrays.stream(output).anyMatch(APMissingItem::isStillMissing)) {
            CreateAPRequirementUtil.changeSingle(this, output, CreateAPRequirementKind.OUTPUT_FILTER);
        } else {
            CreateAPRequirementUtil.clearSingle(this);
        }
        return foundRecipe;
    }

    @Inject(method = "applyRecipe", at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 2), remap = false)
    void filterOutput(CallbackInfo ci, @Local(name = "list") List<ItemStack> list) {
        list.removeIf(stack -> !APItemAccessUtil.allowCraftOrUse(null, stack.getItem()));
    }
}
