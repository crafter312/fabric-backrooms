package net.thesquire.backroomsmod.world.feature.custom;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class ModWindowFeature extends Feature<ModWindowFeatureConfig> {

    public ModWindowFeature(Codec<ModWindowFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<ModWindowFeatureConfig> context) {
        BlockPos pos = context.getOrigin();
        ModWindowFeatureConfig config = context.getConfig();
        StructureWorldAccess world = context.getWorld();
        Random random = context.getRandom();

        BlockState blockState = config.blockState();

        int numWindows = config.numWindows().get(random);
        int spacing = config.spacing().get(random);
        boolean inWall = config.inWall();

        Direction facingDirection = null;

        // Determine initial facing direction
        // Check all horizontal directions for an air block, with solid blocks to its sides
        for (Direction dir : HorizontalFacingBlock.FACING.getValues()) {
            BlockPos airCheckPos = pos.offset(dir);
            if (world.isAir(airCheckPos)) {
                // Check if the blocks to the left and right of the air block are not air
                BlockPos leftCheckPos = pos.offset(dir.rotateYClockwise());
                BlockPos rightCheckPos = pos.offset(dir.rotateYCounterclockwise());
                if (!world.isAir(leftCheckPos) && !world.isAir(rightCheckPos)) {
                    facingDirection = dir;
                    break;
                }
            }
        }

        if (facingDirection == null) {
            return false; // No suitable facing direction found
        }

        // Set the facing property of the block state
        if (blockState.contains(Properties.HORIZONTAL_FACING)) {
            blockState = blockState.with(Properties.HORIZONTAL_FACING, facingDirection);
        } else if (blockState.contains(Properties.FACING)) {
            blockState = blockState.with(Properties.FACING, facingDirection);
        }

        int placedCount = 0;

        // Attempt to place the first block at the origin if the wall is solid there
        if (isWallSolid(world, pos, facingDirection)) {
            world.setBlockState(inWall ? pos : pos.offset(facingDirection), blockState, Block.NOTIFY_ALL);
            placedCount = 1;
        } else {
            return false; // Cannot place the first block, so return
        }

        // Try placing in one horizontal direction
        Direction firstPlacementDir = facingDirection.rotateYClockwise();
        placedCount += placeWindowsSegment(world, pos, facingDirection, firstPlacementDir, blockState, numWindows - placedCount, spacing, inWall);

        // If not all windows are placed, try the other horizontal direction
        if (placedCount < numWindows) {
            Direction secondPlacementDir = facingDirection.rotateYCounterclockwise();
            placedCount += placeWindowsSegment(world, pos, facingDirection, secondPlacementDir, blockState, numWindows - placedCount, spacing, inWall);
        }

        return placedCount > 0; // Return true if at least one window was placed
    }

    /**
     * Attempts to place a segment of windows starting from an offset of the startPos
     * and moving in the given horizontal placement direction.
     *
     * @param world The world access.
     * @param startPos The initial position from which to calculate offsets.
     * @param wallFacingDirection The direction the wall is facing (e.g., NORTH if the wall is to the NORTH of the window).
     * @param horizontalPlacementDirection The horizontal direction to extend the windows (e.g., EAST or WEST).
     * @param blockState The block state to place.
     * @param numWindowsToAttempt The maximum number of windows to attempt to place in this segment.
     * @param spacing The spacing between windows.
     * @return The number of windows successfully placed in this segment.
     */
    private int placeWindowsSegment(StructureWorldAccess world, BlockPos startPos, Direction wallFacingDirection,
                                    Direction horizontalPlacementDirection, BlockState blockState,
                                    int numWindowsToAttempt, int spacing, boolean inWall) {
        int placedInThisSegment = 0;

        for (int i = 1; i <= numWindowsToAttempt; i++) {
            // Calculate the candidate position for the current window
            // Offset by (spacing + 1) for each window
            BlockPos candidatePos = startPos.offset(horizontalPlacementDirection, i * (spacing + 1));

            if (isWallSolid(world, candidatePos, wallFacingDirection)) {
                world.setBlockState(inWall ? candidatePos : candidatePos.offset(wallFacingDirection), blockState, Block.NOTIFY_ALL);
                placedInThisSegment++;
            } else {
                break; // Wall is not solid, stop placing in this direction
            }
        }
        return placedInThisSegment;
    }

    /**
     * Checks if the 3x3 frame around the candidate block position in the vertical plane of the wall are all not air.
     *
     * @param world The world access.
     * @param candidatePos The position where the window block would be placed.
     * @param wallFacingDirection The direction the wall is facing (e.g., NORTH if the wall is to the NORTH of the window).
     * @return True if the 3x3 frame is solid (not air), false otherwise.
     */
    private boolean isWallSolid(StructureWorldAccess world, BlockPos candidatePos, Direction wallFacingDirection) {

        // Get the horizontal directions perpendicular to the wallFacingDirection
        Direction left = wallFacingDirection.rotateYClockwise();
        Direction right = wallFacingDirection.rotateYCounterclockwise();

        // Define the 8 positions in the 3x3 frame on the wall
        BlockPos[] positionsToCheck = {
                candidatePos.up(),                             // Center Up
                candidatePos.down(),                           // Center Down
                candidatePos.offset(left),                     // Left
                candidatePos.offset(right),                    // Right
                candidatePos.offset(left).up(),                // Left Up
                candidatePos.offset(left).down(),              // Left Down
                candidatePos.offset(right).up(),               // Right Up
                candidatePos.offset(right).down()              // Right Down
        };

        for (BlockPos pos : positionsToCheck) {
            if (world.isAir(pos)) return false; // Found an air block in the 3x3 frame, so the wall is not solid
        }
        return true; // All blocks in the 3x3 frame are solid
    }
}
