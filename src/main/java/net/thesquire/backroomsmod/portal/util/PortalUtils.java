package net.thesquire.backroomsmod.portal.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockLocating;
import net.thesquire.backroomsmod.block.ModBlocks;
import org.jetbrains.annotations.Nullable;
import qouteall.imm_ptl.core.McHelper;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.q_misc_util.Helper;

public class PortalUtils {

    public static final int maxPortalDim = 21;
    public static final int minPortalDim = 2;

    public static class FrameTester {

        private static final Block[] inFrameBlocks = {
                Blocks.AIR,
                ModBlocks.YELLOW_WALLPAPER
        };

        private final ServerWorld serverWorld;
        private final BlockLocating.Rectangle rectangle;
        private final Direction up;
        private final Direction facing;
        private final Direction horizontal;
        private final Block frameBlock;

        public FrameTester(ServerWorld world, BlockPos pos, Direction dirUp, Direction dirFacing, Block frame) {
            this.serverWorld = world;
            this.up = dirUp;
            this.facing = dirFacing;
            this.frameBlock = frame;

            Vec3i cross = dirFacing.getVector().crossProduct(dirUp.getVector());
            this.horizontal = Direction.fromVector(cross.getX(), cross.getY(), cross.getZ());
            this.rectangle = findRectangle(pos);
        }

        public ServerWorld getServerWorld() { return serverWorld; }
        public Direction getUp() { return up; }
        public Direction getFacing() { return facing; }
        public Direction getHorizontal() { return horizontal; }
        public Block getFrameBlock() { return frameBlock; }
        public BlockLocating.Rectangle getRectangle() { return rectangle; }

        private BlockLocating.Rectangle findRectangle(BlockPos initPos) {
            BlockPos lowerCorner = getLimitForAxis(getLimitForAxis(initPos, horizontal.getAxis()), up.getAxis());
            if(lowerCorner == null) return new BlockLocating.Rectangle(initPos, 1, 1);

            int width = getSize(horizontal.getAxis(), lowerCorner);
            int height = getSize(up.getAxis(), lowerCorner);
            return new BlockLocating.Rectangle(lowerCorner, width, height);
        }

        private BlockPos getLimitForAxis(BlockPos blockPos, Direction.Axis axis) {
            if (blockPos == null || axis == null) return null;
            int offset = 1;
            while (isValidBlockInFrame(serverWorld.getBlockState(blockPos.offset(axis, -offset)))) {
                offset++;
                if (offset > 20) return null;
                if ((axis.equals(Direction.Axis.Y) && blockPos.getY() - offset < serverWorld.getBottomY()) ||
                        (!axis.equals(Direction.Axis.Y) && !serverWorld.getWorldBorder().contains(blockPos.offset(axis, -offset))))
                    return null;
            }
            return blockPos.offset(axis, -(offset - 1));
        }

        protected int getSize(Direction.Axis axis, BlockPos lowerCorner) {
            for (int i = 1; i <= PortalUtils.maxPortalDim; i++) {
                BlockState blockState = this.serverWorld.getBlockState(lowerCorner.offset(axis, i));
                if (!isValidBlockInFrame(blockState)) {
                    if (blockState.isOf(frameBlock)) return i >= PortalUtils.minPortalDim ? i : 0;
                    break;
                }
            }
            return 0;
        }

        private boolean isValidBlockInFrame(BlockState state) {
            for(Block block : inFrameBlocks) {
                if(state.isOf(block)) return true;
            }
            return false;
        }

    }

    public static Vec3d getPortalOrigin(BlockLocating.Rectangle rect, Direction.Axis axis) {
        double x = rect.lowerLeft.getX();
        double y = rect.lowerLeft.getY() + (((double) rect.height) / 2);
        double z = rect.lowerLeft.getZ();

        if (axis == Direction.Axis.X) {
            x += 0.5D;
            z += ((double) rect.width) / 2;
        }
        else if (axis == Direction.Axis.Z) {
            x += ((double) rect.width) / 2;
            z += 0.5D;
        }

        // remaining axis is y-axis
        // assumes x used as primaryAxis in BlockLocating.getLargestRectangle() method
        else {
            x += ((double) rect.width) / 2;
            y = rect.lowerLeft.getY() + 0.5D;
            z += ((double) rect.height) / 2;
        }

        return new Vec3d(x, y, z);
    }

    // this function calculates how much you have to rotate clockwise to get from the first vector to the second vector
    // it is used to rotate an entity when teleporting between portals with non-aligned axes
    public static float getAngle(Direction first, Direction second) {
        if (first.getAxis().isVertical() || second.getAxis().isVertical())
            throw new IllegalStateException("Unable to get angle with vertical portal direction!");

        float degrees = 0;
        Direction temp = first;
        while(temp != second) {
            degrees += 90;
            temp = temp.rotateYClockwise();
        }

        return degrees;
    }

    @Nullable
    public static Portal findRotatedPortal(Portal portal, float angle) {
        return Helper.getFirstNullable(McHelper.findEntitiesRough(
                Portal.class,
                portal.getDestinationWorld(),
                portal.getDestPos(),
                0,
                p1 -> p1.getOriginPos().subtract(portal.getDestPos()).lengthSquared() < 0.01 &&
                        p1.getDestPos().subtract(portal.getOriginPos()).lengthSquared() < 0.01 &&
                        p1.getNormal().dotProduct(portal.getContentDirection()) - Math.cos(angle) < 0.01
        ));
    }

    public static Direction.Axis flipHorizontalAxis(Direction.Axis axis) {
        return axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
    }

}
