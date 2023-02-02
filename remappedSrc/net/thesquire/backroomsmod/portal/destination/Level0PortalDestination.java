package net.thesquire.backroomsmod.portal.teleport;

import net.kyrptonaught.customportalapi.CustomPortalApiRegistry;
import net.kyrptonaught.customportalapi.CustomPortalsMod;
import net.kyrptonaught.customportalapi.portal.frame.PortalFrameTester;
import net.kyrptonaught.customportalapi.util.PortalLink;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;

import java.util.Optional;

public class Level0PortalDestination {

    public static void createDestinationPortal(ServerWorld initial, ServerWorld destination, BlockPos portalPos, Direction.Axis axis, BlockLocating.Rectangle portalFramePos, BlockState frameBlock) {
        WorldBorder worldBorder = destination.getWorldBorder();
        double xMin = Math.max(-2.9999872E7D, worldBorder.getBoundWest() + 16.0D);
        double zMin = Math.max(-2.9999872E7D, worldBorder.getBoundNorth() + 16.0D);
        double xMax = Math.min(2.9999872E7D, worldBorder.getBoundEast() - 16.0D);
        double zMax = Math.min(2.9999872E7D, worldBorder.getBoundSouth() - 16.0D);
        double scaleFactor = DimensionType.getCoordinateScaleFactor(initial.getDimension(), destination.getDimension());
        BlockPos blockPos3 = new BlockPos(MathHelper.clamp(portalPos.getX() * scaleFactor, xMin, xMax),
                20, MathHelper.clamp(portalPos.getZ() * scaleFactor, zMin, zMax));

        Optional<BlockLocating.Rectangle> portal = makeDestinationPortal(destination, blockPos3, frameBlock, axis);

        portal.ifPresent(rectangle -> CustomPortalsMod.portalLinkingStorage.createLink(portalFramePos.lowerLeft,
                initial.getRegistryKey(), rectangle.lowerLeft, destination.getRegistryKey()));
    }

    public static Optional<BlockLocating.Rectangle> makeDestinationPortal(World world, BlockPos blockPos, BlockState frameBlock, Direction.Axis axis) {
        WorldBorder worldBorder = world.getWorldBorder();
        PortalLink link = CustomPortalApiRegistry.getPortalLinkFromBase(frameBlock.getBlock());
        PortalFrameTester portalFrameTester = link.getFrameTester().createInstanceOfPortalFrameTester();
        for (BlockPos.Mutable mutable : BlockPos.iterateInSquare(blockPos, 16, Direction.WEST, Direction.SOUTH)) {
            BlockPos testingPos = mutable.toImmutable();
            if (!worldBorder.contains(testingPos)) continue;

            BlockPos pos = null;
            if (canHoldPortal(world.getBlockState(testingPos.add(0, -1, 0)))) {
                BlockPos testRect = portalFrameTester.doesPortalFitAt(world, testingPos, axis);
                if (testRect != null) pos = testRect;
            }

            if (pos != null) {
                portalFrameTester.createPortal(world, pos, frameBlock, axis);
                return Optional.of(portalFrameTester.getRectangle());
            }
        }
        portalFrameTester.createPortal(world, blockPos, frameBlock, axis);
        return Optional.of(portalFrameTester.getRectangle());
    }

    private static boolean canHoldPortal(BlockState state) {
        return state.getMaterial().isSolid();
    }

}
