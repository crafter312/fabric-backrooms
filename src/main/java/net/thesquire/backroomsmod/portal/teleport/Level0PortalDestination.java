package net.thesquire.backroomsmod.portal.teleport;

import net.kyrptonaught.customportalapi.CustomPortalApiRegistry;
import net.kyrptonaught.customportalapi.CustomPortalsMod;
import net.kyrptonaught.customportalapi.portal.frame.PortalFrameTester;
import net.kyrptonaught.customportalapi.util.PortalLink;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.thesquire.backroomsmod.block.ModBlocks;
import net.thesquire.backroomsmod.dimension.ModDimensionKeys;
import net.thesquire.backroomsmod.world.structure.ModStructureUtils;

import java.util.Optional;

public class Level0PortalDestination {

    public static final Block portalBase = ModBlocks.TFMC_MAGNET;

    public static void makePortalDestination(World world, BlockPos portalPos, Direction.Axis portalAxis) {
        if(world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            if (serverWorld.getRegistryKey() != World.OVERWORLD) return;

            ServerWorld destServerWorld = serverWorld.getServer().getWorld(ModDimensionKeys.LEVEL_0);
            assert destServerWorld != null;

            PortalFrameTester.PortalFrameTesterFactory portalFrameTesterFactory = CustomPortalApiRegistry.getPortalLinkFromBase(portalBase).getFrameTester();
            BlockLocating.Rectangle fromPortalRectangle = portalFrameTesterFactory.createInstanceOfPortalFrameTester().init(world, portalPos, portalAxis, portalBase).getRectangle();
            createDestinationPortal(serverWorld, destServerWorld, portalPos, portalAxis, fromPortalRectangle, portalBase.getDefaultState());
        }
    }

    public static void createDestinationPortal(ServerWorld initial, ServerWorld destination, BlockPos portalPos, Direction.Axis axis, BlockLocating.Rectangle portalFramePos, BlockState frameBlock) {
        WorldBorder worldBorder = destination.getWorldBorder();
        double xMin = Math.max(-2.9999872E7D, worldBorder.getBoundWest() + 16.0D);
        double zMin = Math.max(-2.9999872E7D, worldBorder.getBoundNorth() + 16.0D);
        double xMax = Math.min(2.9999872E7D, worldBorder.getBoundEast() - 16.0D);
        double zMax = Math.min(2.9999872E7D, worldBorder.getBoundSouth() - 16.0D);
        double scaleFactor = DimensionType.getCoordinateScaleFactor(initial.getDimension(), destination.getDimension());
        BlockPos blockPos3 = new BlockPos(MathHelper.clamp(portalPos.getX() * scaleFactor, xMin, xMax),
                20, MathHelper.clamp(portalPos.getZ() * scaleFactor, zMin, zMax));

        Optional<BlockLocating.Rectangle> portal = findDestinationPortal(destination, blockPos3, frameBlock);
        if(portal.isEmpty()) portal = makeDestinationPortal(destination, blockPos3, frameBlock, axis);

        portal.ifPresent(rectangle -> CustomPortalsMod.portalLinkingStorage.createLink(portalFramePos.lowerLeft,
                initial.getRegistryKey(), rectangle.lowerLeft, destination.getRegistryKey()));
    }

    private static Optional<BlockLocating.Rectangle> makeDestinationPortal(ServerWorld destWorld, BlockPos blockPos, BlockState frameBlock, Direction.Axis axis) {
        WorldBorder worldBorder = destWorld.getWorldBorder();
        PortalLink link = CustomPortalApiRegistry.getPortalLinkFromBase(frameBlock.getBlock());
        PortalFrameTester portalFrameTester = link.getFrameTester().createInstanceOfPortalFrameTester();

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
                return Optional.of(portalFrameTester.getRectangle());
            }
        }
        portalFrameTester.createPortal(destWorld, blockPos, frameBlock, axis);
        return Optional.of(portalFrameTester.getRectangle());
    }

    private static boolean canHoldPortal(BlockState state) {
        return state.getMaterial().isSolid();
    }

    public static Optional<BlockLocating.Rectangle> findDestinationPortal(ServerWorld world, BlockPos blockPos, BlockState frameBlock) {
        WorldBorder worldBorder = world.getWorldBorder();
        PortalLink link = CustomPortalApiRegistry.getPortalLinkFromBase(frameBlock.getBlock());

        BlockPos structurePos = ModStructureUtils.findStructure(world, blockPos).orElseThrow();
        Pair<BlockPos, Direction.Axis> portalInfo = findPortalInfo(world, structurePos, frameBlock).orElseThrow();
        if(!worldBorder.contains(portalInfo.getLeft())) return Optional.empty();

        Optional<PortalFrameTester> optional = link.getFrameTester().createInstanceOfPortalFrameTester().getNewPortal(world, portalInfo.getLeft(), portalInfo.getRight(), frameBlock.getBlock());
        return optional.map(PortalFrameTester::getRectangle);
    }

    private static Optional<Pair<BlockPos, Direction.Axis>> findPortalInfo(ServerWorld world, BlockPos structurePos, BlockState frameBlock) {

        // first two elements are z axis offsets for portal location
        // second two elements are x axis offsets for portal location
        BlockPos[] portalLocs = {
                structurePos.add(24, 20, 1),
                structurePos.add(-24, 20, -1),
                structurePos.add(1, 20, -24),
                structurePos.add(-1, 20, 24)
        };

        for(int i = 0; i < 4; i++) {
            if(world.getBlockState(portalLocs[i].add(0,-1,0)).isOf(frameBlock.getBlock())) {
                Direction.Axis portalAxis;
                if(i==0 || i==1) portalAxis = Direction.Axis.Z;
                else portalAxis = Direction.Axis.X;

                return Optional.of(new Pair<>(portalLocs[i], portalAxis));
            }
        }

        return Optional.empty();
    }

}
