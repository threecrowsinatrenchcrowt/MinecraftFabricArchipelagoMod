package net.deadlydiamond98.archipelago.mixin.compat.create;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.deadlydiamond98.archipelago.compat.create.requirements.CreateCraftingAPRequirement;
import net.deadlydiamond98.archipelago.compat.create.requirements.CreateDisplayableAPRequirement;
import net.deadlydiamond98.archipelago.util.mixinterfaces.IDisplayableRequirementAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(KineticBlockEntity.class)
public abstract class KineticBlockEntityMixin extends SmartBlockEntity implements IDisplayableRequirementAccessor {
    @Unique
    @NotNull
    private CreateDisplayableAPRequirement archipelago$displayableRequirement = new CreateCraftingAPRequirement(this);

    public KineticBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public @NotNull CreateDisplayableAPRequirement archipelago$getDisplayableRequirement() {
        return archipelago$displayableRequirement;
    }

    @Override
    public void archipelago$setDisplayableRequirement(@NotNull CreateDisplayableAPRequirement requirement) {
        archipelago$displayableRequirement = requirement;
    }

    @WrapMethod(method = "addToTooltip", remap = false)
    boolean addMissingAPItemTooltip(List<Text> tooltip, boolean isPlayerSneaking, Operation<Boolean> original) {
        // Important: This needs to not short circuit
        return original.call(tooltip, isPlayerSneaking)
                | archipelago$displayableRequirement.addToTooltip(tooltip, isPlayerSneaking);
    }
}
