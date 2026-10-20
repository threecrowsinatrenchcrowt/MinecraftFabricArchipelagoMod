package net.deadlydiamond98.archipelago.mixin.compat.create;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementKind;
import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementUtil;
import net.deadlydiamond98.archipelago.util.APItemAccessUtil;
import net.deadlydiamond98.archipelago.util.APMissingItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(MechanicalPressBlockEntity.class)
public abstract class MechanicalPressBlockEntityMixin extends BasinOperatingBlockEntity {
    public MechanicalPressBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    // Filtering the recipe like this will let locked recipes pass underneath the press on a belt, instead of waiting.
    // Because the recipe isn't checked again, this wouldn't get the item unstuck if the recipe is unlocked later.
    // At least, that's my justification for having this naïve implementation.
    @WrapMethod(method = "getRecipe")
    Optional<PressingRecipe> filterRecipe(ItemStack item, Operation<Optional<PressingRecipe>> original) {
        Optional<PressingRecipe> potentialRecipe = original.call(item);
        if (potentialRecipe.isEmpty() || world == null) return potentialRecipe;
        PressingRecipe recipe = potentialRecipe.get();
        DynamicRegistryManager registryAccess = world.getRegistryManager();
        ItemStack output = recipe.getOutput(registryAccess);
        Optional<APMissingItem> potentialMissingItem = APItemAccessUtil.getMissingItemFor(output.getItem());
        CreateAPRequirementUtil.changeSingleNullable(
                this,
                potentialMissingItem.orElse(null),
                CreateAPRequirementKind.CRAFTING
        );
        if (potentialMissingItem.isPresent()) {
            return Optional.empty();
        }
        return potentialRecipe;
    }
}
