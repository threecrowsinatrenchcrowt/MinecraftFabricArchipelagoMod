package net.deadlydiamond98.archipelago.mixin.compat.create;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlockEntity;
import com.simibubi.create.content.kinetics.crafter.RecipeGridHandler;
import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementKind;
import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementUtil;
import net.deadlydiamond98.archipelago.util.APItemAccessUtil;
import net.deadlydiamond98.archipelago.util.APMissingItem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(MechanicalCrafterBlockEntity.class)
public class MechanicalCrafterBlockEntityMixin {
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/crafter/RecipeGridHandler;tryToApplyRecipe(Lnet/minecraft/world/World;Lcom/simibubi/create/content/kinetics/crafter/RecipeGridHandler$GroupedItems;)Lnet/minecraft/item/ItemStack;"))
    ItemStack filterRecipeOutput(World world, RecipeGridHandler.GroupedItems items, Operation<ItemStack> original) {
        ItemStack output = original.call(world, items);
        if (output == null) return null;
        Optional<APMissingItem> potentialMissingItem = APItemAccessUtil.getMissingItemFor(output.getItem());
        BlockEntity[] blockEntities = RecipeGridHandler
                .getAllCraftersOfChain((MechanicalCrafterBlockEntity) (Object) this)
                .toArray(BlockEntity[]::new);
        CreateAPRequirementUtil.changeMultipleNullable(
                blockEntities,
                potentialMissingItem.orElse(null),
                CreateAPRequirementKind.CRAFTING
        );
        if (potentialMissingItem.isPresent()) {
            return null;
        }
        return output;
    }
}
