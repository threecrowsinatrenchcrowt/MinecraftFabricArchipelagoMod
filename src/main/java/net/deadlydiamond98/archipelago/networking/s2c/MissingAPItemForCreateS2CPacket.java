package net.deadlydiamond98.archipelago.networking.s2c;

import net.deadlydiamond98.archipelago.APMod;
import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementKind;
import net.deadlydiamond98.archipelago.compat.create.CreateAPRequirementUtil;
import net.deadlydiamond98.archipelago.util.APMissingItem;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.Objects;

public class MissingAPItemForCreateS2CPacket {
    public static final Identifier ID = APMod.id("missing_ap_item_for_create");

    /// Send the packet.
    /// @param world The {@link ServerWorld} whose players to send this packet to
    /// @param positions The block positions these missing items apply to.
    ///                  For example, the entire wall of mechanical crafters.
    /// @param missingItems The list of missing items.
    ///                     A single recipe might be missing multiple unlocks for all of its outputs.
    public static void send(
            ServerWorld world,
            BlockPos[] positions,
            APMissingItem[] missingItems,
            CreateAPRequirementKind kind
    ) {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeInt(positions.length);
        for (BlockPos pos : positions) {
            buf.writeBlockPos(pos);
        }

        buf.writeInt(missingItems.length);
        for (APMissingItem missingItem : missingItems) {
            missingItem.serialize(buf);
        }

        buf.writeEnumConstant(kind);

        for (ServerPlayerEntity player : world.getPlayers()) {
            ServerPlayNetworking.send(player, ID, buf);
        }
    }

    public static class Handler {
        public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
            if (!FabricLoader.getInstance().isModLoaded("create")) return;

            int positionCount = buf.readInt();
            BlockPos[] positions = new BlockPos[positionCount];
            for (int i = 0; i < positionCount; i++) {
                positions[i] = buf.readBlockPos();
            }

            int missingItemCount = buf.readInt();
            APMissingItem[] missingItems = new APMissingItem[missingItemCount];
            for (int i = 0; i < missingItemCount; i++) {
                missingItems[i] = APMissingItem.deserialize(buf);
            }

            CreateAPRequirementKind kind = buf.readEnumConstant(CreateAPRequirementKind.class);

            client.execute(() -> {
                if (client.world != null) {
                    BlockEntity[] blockEntities = Arrays.stream(positions)
                            .map(position -> client.world.getBlockEntity(position))
                            .filter(Objects::nonNull)
                            .toArray(BlockEntity[]::new);
                    CreateAPRequirementUtil.changeMultiple(blockEntities, missingItems, kind);
                }
            });
        }
    }
}
