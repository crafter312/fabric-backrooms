package net.thesquire.backroomsmod.portal.teleport;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.border.WorldBorder;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.portal.util.PortalUtils;
import net.thesquire.backroomsmod.world.structure.ModStructureUtils;

import java.util.Optional;

public class Level0Teleporter {

    //TODO add default portal creation function for new portal setup
    /*
    private static Pair<Optional<BlockLocating.Rectangle>, Direction> makeDestinationPortal(ServerWorld destWorld, BlockPos blockPos, BlockState frameBlock, Direction dir) {
        WorldBorder worldBorder = destWorld.getWorldBorder();
        PortalLink link = CustomPortalApiRegistry.getPortalLinkFromBase(frameBlock.getBlock());
        PortalFrameTester portalFrameTester = link.getFrameTester().createInstanceOfPortalFrameTester();
        Direction.Axis axis = Level0PortalAreaHelper.flipHorizontalAxis(dir.getAxis());

        for (BlockPos.Mutable mutable : BlockPos.iterateInSquare(blockPos, 16, Direction.WEST, Direction.SOUTH)) {
            BlockPos testingPos = mutable.toImmutable();
            if (!worldBorder.contains(testingPos)) continue;

            BlockPos pos = null;
            if (canHoldPortal(destWorld.getBlockState(testingPos.add(0, -1, 0)))) {
                BlockPos testRect = portalFrameTester.doesPortalFitAt(destWorld, testingPos, axis);
                if (testRect != null) pos = testRect;
            }

            if (pos != null) {
                portalFrameTester.createPortal(destWorld, pos, frameBlock, axis);
                return new Pair<>(Optional.of(portalFrameTester.getRectangle()), dir);
            }
        }
        portalFrameTester.createPortal(destWorld, blockPos, frameBlock, axis);
        return new Pair<>(Optional.of(portalFrameTester.getRectangle()), dir);
    }*/

    private static boolean canHoldPortal(BlockState state) {
        return state.getMaterial().isSolid();
    }

    public static Pair<Optional<BlockLocating.Rectangle>, Direction> findDestinationPortal(ServerWorld world, BlockPos blockPos, Block frameBlock) {
        WorldBorder worldBorder = world.getWorldBorder();

        BlockPos structurePos = ModStructureUtils.findStructure(world, blockPos).orElseThrow();
        Pair<BlockPos, Direction> portalInfo = findPortalInfo(world, structurePos, frameBlock).orElseThrow();
        if (!worldBorder.contains(portalInfo.getLeft())) return new Pair<>(Optional.empty(), portalInfo.getRight());

        PortalUtils.FrameTester frameTester = new PortalUtils.FrameTester(world, portalInfo.getLeft(), Direction.UP, portalInfo.getRight(), frameBlock);

        return new Pair<>(Optional.of(frameTester.getRectangle()), portalInfo.getRight());
    }

    private static Optional<Pair<BlockPos, Direction>> findPortalInfo(ServerWorld world, BlockPos structurePos, Block frameBlock) {

        // first two elements are z-axis offsets for portal location
        // second two elements are x-axis offsets for portal location
        BlockPos[] portalLocs = {
                structurePos.add(-24, 20, -1), // -z north
                structurePos.add(24, 20, 1),   // +z south
                structurePos.add(-1, 20, 24),  // -x west
                structurePos.add(1, 20, -24)   // +x east
        };

        for(int i = 0; i < 4; i++) {
            if (world.getBlockState(portalLocs[i].add(0,-1,0)).isOf(frameBlock))
                return Optional.of(new Pair<>(portalLocs[i], Direction.byId(i + 2)));
        }

        return Optional.empty();
    }

    protected static TeleportTarget idkWhereToPutYou(ServerWorld world, Entity entity, BlockPos pos) {
        BackroomsMod.LOGGER.error("Unable to find tp location, forced to place on top of world");
        BlockPos destinationPos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, pos);
        return new TeleportTarget(new Vec3d(destinationPos.getX() + .5, destinationPos.getY(), destinationPos.getZ() + .5), entity.getVelocity(), entity.getYaw(), entity.getPitch());
    }

}
