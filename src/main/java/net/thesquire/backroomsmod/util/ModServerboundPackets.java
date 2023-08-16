package net.thesquire.backroomsmod.util;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.block.entity.MagneticDistortionSystemControlComputerBlockEntity;
import reborncore.common.network.IdentifiedPacket;
import reborncore.common.network.NetworkManager;

public class ModServerboundPackets {

    public static final Identifier MAGNETIC_DISTORTION_SYSTEM_SIDE = BackroomsMod.makeId("magnetic_distortion_system_side");
    public static final Identifier MAGNETIC_DISTORTION_SYSTEM_ACTIVE = BackroomsMod.makeId("magnetic_distortion_system_active");

    public static void registerServerboundPackets() {
        BackroomsMod.LOGGER.info("Registering serverbound packets for " + BackroomsMod.MOD_ID);

        NetworkManager.registerServerBoundHandler(MAGNETIC_DISTORTION_SYSTEM_SIDE, (server, player, handler, buf, responseSender) -> {
            boolean side = buf.readBoolean();
            BlockPos pos = buf.readBlockPos();

            server.execute(() -> {
                BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
                if (blockEntity instanceof MagneticDistortionSystemControlComputerBlockEntity) {
                    ((MagneticDistortionSystemControlComputerBlockEntity) blockEntity).setSide(side);
                }
            });
        });

        NetworkManager.registerServerBoundHandler(MAGNETIC_DISTORTION_SYSTEM_ACTIVE, ((server, player, handler, buf, responseSender) -> {
            boolean active = buf.readBoolean();
            BlockPos pos = buf.readBlockPos();

            server.execute(() -> {
                BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
                if (blockEntity instanceof  MagneticDistortionSystemControlComputerBlockEntity) {
                    ((MagneticDistortionSystemControlComputerBlockEntity) blockEntity).setActive(active);
                }
            });
        }));
    }

    public static IdentifiedPacket createPacketMagneticDistortionSystemSide(boolean side, BlockPos pos) {
        return NetworkManager.createServerBoundPacket(MAGNETIC_DISTORTION_SYSTEM_SIDE, buf -> {
            buf.writeBoolean(side);
            buf.writeBlockPos(pos);
        });
    }

    public static IdentifiedPacket createPacketMagneticDistortionSystemActive(boolean active, BlockPos pos) {
        return NetworkManager.createServerBoundPacket(MAGNETIC_DISTORTION_SYSTEM_ACTIVE, buf -> {
           buf.writeBoolean(active);
           buf.writeBlockPos(pos);
        });
    }

}
