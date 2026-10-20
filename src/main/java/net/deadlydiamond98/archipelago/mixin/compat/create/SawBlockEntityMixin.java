package net.deadlydiamond98.archipelago.mixin.compat.create;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementKind;
import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementUtil;
import net.deadlydiamond98.archipelago.util.APItemAccessUtil;
import net.deadlydiamond98.archipelago.util.APMissingItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Mixin(SawBlockEntity.class)
public class SawBlockEntityMixin extends KineticBlockEntity {
    public SawBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @WrapMethod(method = "getRecipes", remap = false)
    List<? extends Recipe<?>> filterRecipes(Operation<List<? extends Recipe<?>>> original) {
        List<? extends Recipe<?>> recipes = original.call();
        if (recipes.isEmpty() || world == null) return recipes;
        DynamicRegistryManager registryAccess = world.getRegistryManager();
        Iterator<? extends Recipe<?>> recipeIterator = recipes.iterator();
        ArrayList<APMissingItem> missingItems = new ArrayList<>();
        while (recipeIterator.hasNext()) {
            Recipe<?> recipe = recipeIterator.next();
            ItemStack output = recipe.getOutput(registryAccess);
            Optional<APMissingItem> potentialMissingItem = APItemAccessUtil.getMissingItemFor(output.getItem());
            if (potentialMissingItem.isPresent()) {
                recipeIterator.remove();
                missingItems.add(potentialMissingItem.get());
            }
        }
        APMissingItem[] missingItemsArray = missingItems.toArray(new APMissingItem[0]);
        CreateAPRequirementUtil.changeSingle(this, missingItemsArray, CreateAPRequirementKind.OUTPUT_FILTER);
        return recipes;
    }
}
