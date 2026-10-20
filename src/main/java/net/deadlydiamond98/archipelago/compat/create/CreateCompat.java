package net.deadlydiamond98.archipelago.compat.create;

import com.simibubi.create.api.contraption.BlockMovementChecks;
import net.deadlydiamond98.archipelago.util.APItemAccessUtil;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CreateCompat {
    public static void registerMovementAllowedCheck() {
        BlockMovementChecks.registerMovementAllowedCheck(
                (BlockState state, World world, BlockPos pos) ->
                        APItemAccessUtil.disallowBreakingBlock(null, state)
                                ? BlockMovementChecks.CheckResult.FAIL
                                : BlockMovementChecks.CheckResult.PASS
        );
    }
}
