package net.thesquire.backroomsmod.portal.teleport;

import net.minecraft.block.Block;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.border.WorldBorder;
import net.thesquire.backroomsmod.portal.util.PortalUtils;
import net.thesquire.backroomsmod.world.structure.ModStructureUtils;

import java.util.Optional;

public class Level0Teleporter {

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

}
