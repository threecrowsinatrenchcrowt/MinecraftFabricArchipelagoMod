package net.deadlydiamond98.archipelago.mixin.compat.create;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementKind;
import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementUtil;
import net.deadlydiamond98.archipelago.util.APItemAccessUtil;
import net.deadlydiamond98.archipelago.util.APMissingItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(DeployerBlockEntity.class)
public class DeployerBlockEntityMixin extends KineticBlockEntity {
    public DeployerBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @WrapMethod(method = "getRecipe")
    Recipe<? extends Inventory> filterRecipe(ItemStack stack, Operation<Recipe<? extends Inventory>> original) {
        Recipe<? extends Inventory> recipe = original.call(stack);
        if (recipe == null || world == null) return null;
        DynamicRegistryManager registryAccess = world.getRegistryManager();
        ItemStack output = recipe.getOutput(registryAccess);
        Optional<APMissingItem> potentialMissingItem = APItemAccessUtil.getMissingItemFor(output.getItem());
        CreateAPRequirementUtil.changeSingleNullable(
                this,
                potentialMissingItem.orElse(null),
                CreateAPRequirementKind.CRAFTING
        );
        if (potentialMissingItem.isPresent()) {
            return null;
        }
        return recipe;
    }
}
