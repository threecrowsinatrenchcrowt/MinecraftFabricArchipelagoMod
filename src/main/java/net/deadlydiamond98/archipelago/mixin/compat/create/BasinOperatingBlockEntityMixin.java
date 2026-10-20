package net.deadlydiamond98.archipelago.mixin.compat.create;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
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
import java.util.List;
import java.util.Optional;

@Mixin(BasinOperatingBlockEntity.class)
public class BasinOperatingBlockEntityMixin extends KineticBlockEntity {
    public BasinOperatingBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @WrapMethod(method = "getMatchingRecipes", remap = false)
    List<Recipe<?>> filterMatchingRecipes(Operation<List<Recipe<?>>> original) {
        List<Recipe<?>> recipes = original.call();
        // The Mechanical Mixer overrides this method and thus filtering should be handled there
        if (recipes.isEmpty() || (Object) this instanceof MechanicalMixerBlockEntity || world == null)
            return recipes;
        DynamicRegistryManager manager = world.getRegistryManager();
        Recipe<?> recipe = recipes.get(0); // Basin operating block entities always take the first (best) recipe
        ItemStack itemStack = recipe.getOutput(manager);
        Optional<APMissingItem> potentialMissingItem = APItemAccessUtil.getMissingItemFor(itemStack.getItem());
        CreateAPRequirementUtil.changeSingleNullable(
                this,
                potentialMissingItem.orElse(null),
                CreateAPRequirementKind.CRAFTING
        );
        if (potentialMissingItem.isPresent()) {
            return new ArrayList<>();
        }
        return recipes;
    }
}
