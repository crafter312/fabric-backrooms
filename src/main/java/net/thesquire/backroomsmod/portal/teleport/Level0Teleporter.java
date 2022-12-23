package net.thesquire.backroomsmod.portal.teleport;

import net.kyrptonaught.customportalapi.CustomPortalApiRegistry;
import net.kyrptonaught.customportalapi.CustomPortalsMod;
import net.kyrptonaught.customportalapi.interfaces.CustomTeleportingEntity;
import net.kyrptonaught.customportalapi.portal.frame.PortalFrameTester;
import net.kyrptonaught.customportalapi.portal.linking.DimensionalBlockPos;
import net.kyrptonaught.customportalapi.util.CustomPortalHelper;
import net.kyrptonaught.customportalapi.util.PortalLink;
import net.kyrptonaught.customportalapi.util.SHOULDTP;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.thesquire.backroomsmod.portal.frame.Level0PortalAreaHelper;
import net.thesquire.backroomsmod.world.structure.ModStructureUtils;

import java.util.Optional;

//TODO fix TP target direction in dest portal
//TODO fix TP target position in dest portal

public class Level0Teleporter {

    public static void TPToDim(World world, Entity entity, Block portalBase, BlockPos portalPos) {
        PortalLink link = CustomPortalApiRegistry.getPortalLinkFromBase(portalBase);
        if (link == null) return;
        if (link.getBeforeTPEvent().execute(entity) == SHOULDTP.CANCEL_TP)
            return;

        RegistryKey<World> destKey = wrapRegistryKey(link.dimID);
        if(world.getRegistryKey().getValue().equals(destKey.getValue()))//if already in destination
            destKey = wrapRegistryKey(link.returnDimID);

        ServerWorld destination = ((ServerWorld) world).getServer().getWorld(destKey);
        if (destination == null) return;
        if (!entity.canUsePortals()) return;

        destination.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(new BlockPos(portalPos.getX() / destination.getDimension().coordinateScale(), portalPos.getY() / destination.getDimension().coordinateScale(), portalPos.getZ() / destination.getDimension().coordinateScale())), 3, new BlockPos(portalPos.getX() / destination.getDimension().coordinateScale(), portalPos.getY() / destination.getDimension().coordinateScale(), portalPos.getZ() / destination.getDimension().coordinateScale()));
        Optional<TeleportTarget> optTarget = customTPTarget(destination, entity, portalPos, portalBase, link.getFrameTester());
        if(optTarget.isEmpty()) return;
        TeleportTarget target = optTarget.get();

        ((CustomTeleportingEntity) entity).setCustomTeleportTarget(target);
        entity = entity.moveToWorld(destination);
        if (entity != null) {
            entity.setYaw(target.yaw);
            entity.setPitch(target.pitch);
            if (entity instanceof ServerPlayerEntity)
                entity.refreshPositionAfterTeleport(target.position);
            link.executePostTPEvent(entity);
        }
    }

    public static RegistryKey<World> wrapRegistryKey(Identifier dimID) {
        return RegistryKey.of(Registry.WORLD_KEY, dimID);
    }

    public static Optional<TeleportTarget> customTPTarget(ServerWorld destinationWorld, Entity entity, BlockPos enteredPortalPos, Block frameBlock, PortalFrameTester.PortalFrameTesterFactory portalFrameTesterFactory) {
        Direction.Axis portalAxis = CustomPortalHelper.getAxisFrom(entity.getEntityWorld().getBlockState(enteredPortalPos));
        BlockLocating.Rectangle fromPortalRectangle = portalFrameTesterFactory.createInstanceOfPortalFrameTester().init(entity.getEntityWorld(), enteredPortalPos, portalAxis, frameBlock).getRectangle();
        DimensionalBlockPos destinationPos = CustomPortalsMod.portalLinkingStorage.getDestination(fromPortalRectangle.lowerLeft, entity.getEntityWorld().getRegistryKey());

        if (destinationPos != null && destinationPos.dimensionType.equals(destinationWorld.getRegistryKey().getValue())) {
            Direction.Axis destAxis = CustomPortalHelper.getAxisFrom(destinationWorld.getBlockState(destinationPos.pos));
            Level0PortalAreaHelper portalFrameTester = (Level0PortalAreaHelper) portalFrameTesterFactory.createInstanceOfPortalFrameTester().init(destinationWorld, destinationPos.pos, destAxis, frameBlock);
            if (portalFrameTester.isValidFrame()) {
                if (!portalFrameTester.isAlreadyLitPortalFrame()) {
                    portalFrameTester.lightPortal(frameBlock);
                }
                return Optional.of(portalFrameTester.getTPTargetInPortal(portalFrameTester.getRectangle(), destAxis, portalAxis, portalFrameTester.getEntityOffsetInPortal(fromPortalRectangle, entity, portalAxis), entity));
            }
        }
        else if (destinationPos == null && entity.getWorld().getRegistryKey() != World.OVERWORLD) return Optional.empty();

        return Optional.of(createDestinationPortal(destinationWorld, entity, portalAxis, fromPortalRectangle, frameBlock.getDefaultState()));
    }

    public static TeleportTarget createDestinationPortal(ServerWorld destination, Entity entity, Direction.Axis axis, BlockLocating.Rectangle portalFramePos, BlockState frameBlock) {
        WorldBorder worldBorder = destination.getWorldBorder();
        double xMin = Math.max(-2.9999872E7D, worldBorder.getBoundWest() + 16.0D);
        double zMin = Math.max(-2.9999872E7D, worldBorder.getBoundNorth() + 16.0D);
        double xMax = Math.min(2.9999872E7D, worldBorder.getBoundEast() - 16.0D);
        double zMax = Math.min(2.9999872E7D, worldBorder.getBoundSouth() - 16.0D);
        double scaleFactor = DimensionType.getCoordinateScaleFactor(entity.world.getDimension(), destination.getDimension());
        BlockPos blockPos3 = new BlockPos(MathHelper.clamp(entity.getX() * scaleFactor, xMin, xMax), entity.getY(), MathHelper.clamp(entity.getZ() * scaleFactor, zMin, zMax));

        Pair<Optional<BlockLocating.Rectangle>, Direction.Axis> portal = findDestinationPortal(destination, blockPos3, frameBlock);
        if (portal.getLeft().isEmpty()) portal = makeDestinationPortal(destination, blockPos3, frameBlock, axis);
        Optional<BlockLocating.Rectangle> destFrame = portal.getLeft();
        Direction.Axis destAxis = portal.getRight();
        Direction.Axis destAxisPerpendicular = destAxis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;

        if (destFrame.isPresent()) {
            BlockPos destPos = destFrame.get().lowerLeft;
            Level0PortalAreaHelper portalFrameTester = (Level0PortalAreaHelper) CustomPortalApiRegistry.getPortalLinkFromBase(frameBlock.getBlock()).getFrameTester().createInstanceOfPortalFrameTester();
            PortalFrameTester destPortalFrameTester = CustomPortalApiRegistry.getPortalLinkFromBase(frameBlock.getBlock()).getFrameTester().createInstanceOfPortalFrameTester()
                    .init(destination, destPos, destAxisPerpendicular, frameBlock.getBlock());

            if (destPortalFrameTester.isValidFrame()) {
                if (!destPortalFrameTester.isAlreadyLitPortalFrame()) destPortalFrameTester.lightPortal(frameBlock.getBlock());

                CustomPortalsMod.portalLinkingStorage.createLink(portalFramePos.lowerLeft, entity.world.getRegistryKey(), destPos, destination.getRegistryKey());
                Vec3d inPortalOffset = portalFrameTester.getEntityOffsetInPortal(portalFramePos, entity, axis).rotateY(90 * (axis.equals(destAxis) ? 0 : 1));
                return portalFrameTester.getTPTargetInPortal(destFrame.get(), destAxisPerpendicular, axis, inPortalOffset, entity);
            }
        }
        return idkWhereToPutYou(destination, entity, blockPos3);
    }

    private static Pair<Optional<BlockLocating.Rectangle>, Direction.Axis> makeDestinationPortal(ServerWorld destWorld, BlockPos blockPos, BlockState frameBlock, Direction.Axis axis) {
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
                return new Pair<>(Optional.of(portalFrameTester.getRectangle()), axis);
            }
        }
        portalFrameTester.createPortal(destWorld, blockPos, frameBlock, axis);
        return new Pair<>(Optional.of(portalFrameTester.getRectangle()), axis);
    }

    private static boolean canHoldPortal(BlockState state) {
        return state.getMaterial().isSolid();
    }

    public static Pair<Optional<BlockLocating.Rectangle>, Direction.Axis> findDestinationPortal(ServerWorld world, BlockPos blockPos, BlockState frameBlock) {
        WorldBorder worldBorder = world.getWorldBorder();
        PortalLink link = CustomPortalApiRegistry.getPortalLinkFromBase(frameBlock.getBlock());

        BlockPos structurePos = ModStructureUtils.findStructure(world, blockPos).orElseThrow();
        Pair<BlockPos, Direction.Axis> portalInfo = findPortalInfo(world, structurePos, frameBlock).orElseThrow();
        if(!worldBorder.contains(portalInfo.getLeft())) return new Pair<>(Optional.empty(), portalInfo.getRight());

        Optional<PortalFrameTester> optional = link.getFrameTester().createInstanceOfPortalFrameTester().getNewPortal(world, portalInfo.getLeft(), portalInfo.getRight(), frameBlock.getBlock());
        return new Pair<>(optional.map(PortalFrameTester::getRectangle), portalInfo.getRight());
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

    protected static TeleportTarget idkWhereToPutYou(ServerWorld world, Entity entity, BlockPos pos) {
        CustomPortalsMod.logError("Unable to find tp location, forced to place on top of world");
        BlockPos destinationPos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, pos);
        return new TeleportTarget(new Vec3d(destinationPos.getX() + .5, destinationPos.getY(), destinationPos.getZ() + .5), entity.getVelocity(), entity.getYaw(), entity.getPitch());
    }

}
