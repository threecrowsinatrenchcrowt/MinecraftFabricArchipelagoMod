package net.deadlydiamond98.archipelago.mixin.compat.create;

import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.deadlydiamond98.archipelago.compat.create.requirements.CreateDisplayableAPRequirement;
import net.deadlydiamond98.archipelago.compat.create.requirements.CreateItemTransportAPRequirement;
import net.deadlydiamond98.archipelago.util.mixinterfaces.IDisplayableRequirementAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin({
        CrushingWheelControllerBlockEntity.class,
        ChuteBlockEntity.class,
        FunnelBlockEntity.class,
        PackagerBlockEntity.class
})
public abstract class AddDisplayableRequirementMixin extends SmartBlockEntity implements IDisplayableRequirementAccessor, IHaveHoveringInformation {
    @Unique
    @NotNull
    private CreateDisplayableAPRequirement archipelago$displayableRequirement = new CreateItemTransportAPRequirement(this);

    public AddDisplayableRequirementMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
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

    @Override
    public boolean addToTooltip(List<Text> tooltip, boolean isPlayerSneaking) {
        return archipelago$displayableRequirement.addToTooltip(tooltip, isPlayerSneaking);
    }
}
