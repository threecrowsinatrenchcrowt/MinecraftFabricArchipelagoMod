package net.deadlydiamond98.archipelago.mixin.compat.create;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementKind;
import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementUtil;
import net.deadlydiamond98.archipelago.util.APItemAccessUtil;
import net.deadlydiamond98.archipelago.util.APMissingItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(MechanicalMixerBlockEntity.class)
public class MechanicalMixerBlockEntityMixin extends KineticBlockEntity {
    public MechanicalMixerBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @WrapMethod(method = "getMatchingRecipes", remap = false)
    List<Recipe<?>> filterMatchingRecipes(Operation<List<Recipe<?>>> original) {
        List<Recipe<?>> recipes = original.call();
        if (recipes.isEmpty() || world == null) return recipes;
        DynamicRegistryManager manager = world.getRegistryManager();
        Recipe<?> recipe = recipes.get(0);
        ItemStack itemStack = recipe.getOutput(manager);
        Optional<APMissingItem> potentialMissingItem = APItemAccessUtil.getMissingItemFor(itemStack.getItem());
        // Special-case brewing with a mixer because I don't think fluids are handled properly
        // APItemDataLoader only uses the item registry
        if (potentialMissingItem.isEmpty() && recipe instanceof MixingRecipe mixingRecipe) {
            DefaultedList<FluidStack> fluidResults = mixingRecipe.getFluidResults();
            if (!fluidResults.isEmpty()) {
                FluidStack fluidStack = fluidResults.get(0);
                if (fluidStack.getFluid() instanceof PotionFluid) {
                    potentialMissingItem = Optional.of(
                            new APMissingItem("brewing", 0, fluidStack.getDisplayName())
                    );
                }
            }
        }
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
