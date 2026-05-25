package net.thesquire.backroomsmod.world.feature.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

import java.util.Optional;
import java.util.stream.Stream;

public class WallSearchPlacementModifier extends PlacementModifier {

    public static final Codec<WallSearchPlacementModifier> MODIFIER_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(3, Integer.MAX_VALUE).fieldOf("width").forGetter(WallSearchPlacementModifier::getWidth),
            Codec.intRange(3, Integer.MAX_VALUE).fieldOf("height").forGetter(WallSearchPlacementModifier::getHeight),
            BlockPredicate.BASE_CODEC.fieldOf("test_condition").forGetter(WallSearchPlacementModifier::getTestCondition)
    ).apply(instance, instance.stable(WallSearchPlacementModifier::new)));

    /////////////////////////////////////////////////////////////////////

    private final int width;
    private final int height;
    private final BlockPredicate testCondition;

    // Other internal variables
    private final int searchDistance;
    private final int halfSearchDistance;
    private final long targetMask;
    private final int maxShift;
    private final int halfWidth;
    private final int halfHeight;

    public WallSearchPlacementModifier(int width, int height, BlockPredicate testCondition) {
        this.width = width;
        this.height = height;
        this.testCondition = testCondition;

        this.searchDistance = Integer.max(16, this.width);
        this.halfSearchDistance = this.searchDistance / 2;
        this.targetMask = (1L << this.width) - 1;
        this.maxShift = this.searchDistance - this.width;
        this.halfWidth = this.width / 2;
        this.halfHeight = this.height / 2;
    }

    @Override
    public Stream<BlockPos> getPositions(FeaturePlacementContext context, Random random, BlockPos pos) {
        StructureWorldAccess world = context.getWorld();

        int startX = pos.getX() - this.halfSearchDistance;
        int startZ = pos.getZ() - this.halfSearchDistance;
        int currentY = pos.getY();

        // Two 16-element primitive arrays acting as 64-bit coordinate grids
        // Note that arrays initialized like this have all their elements zero initialized
        long[] rowsX = new long[this.searchDistance];
        long[] columnsZ = new long[this.searchDistance];

        // --- Pass 1: Local Scanning via Single-Block Boundary Condition ---
        long testValue;
        int center;
        BlockPos candidateCenter;
        for (int dz = 0; dz < this.searchDistance; dz++) {
            for (int dx = 0; dx < this.searchDistance; dx++) {
                BlockPos currentPos = new BlockPos(startX + dx, currentY, startZ + dz);
                if (world.isOutOfHeightLimit(currentPos.getY())) continue;

                // Skip block if it doesn't satisfy the test condition, remains zero in array
                if (!this.testCondition.test(world, currentPos) || !hasHorizontalAirNeighbor(context, currentPos)) continue;

                // Set corresponding bits to 1 if block passes test
                rowsX[dz] |= (1L << dx);
                columnsZ[dx] |= (1L << dz);
            }

            // To minimize # loops, can scan for walls along x-axis immediately after it is filled
            testValue = rowsX[dz];
            for (int shiftX = 0; shiftX <= this.maxShift; shiftX++) {
                if (((testValue >> shiftX) & this.targetMask) != this.targetMask) continue;

                center = shiftX + this.halfWidth;
                candidateCenter = new BlockPos(startX + center, currentY, startZ + dz);

                // Run the comprehensive multi-block verification step
                Optional<BlockPos> verifiedPos = testWall(context, random, candidateCenter);
                if (verifiedPos.isPresent()) return Stream.of(verifiedPos.get());
            }
        }

        // Perform remaining scan for walls along z-axis now that all blocks have been scanned
        for (int dx = 0; dx < this.searchDistance; dx++) {
            testValue = columnsZ[dx];
            for (int shiftZ = 0; shiftZ <= this.maxShift; shiftZ++) {
                if (((testValue >> shiftZ) & targetMask) != targetMask) continue;

                center = shiftZ + this.halfWidth;
                candidateCenter = new BlockPos(startX + dx, currentY, startZ + center);

                // Run the comprehensive multi-block verification step
                Optional<BlockPos> verifiedPos = testWall(context, random, candidateCenter);
                if (verifiedPos.isPresent()) return Stream.of(verifiedPos.get());
            }
        }

        return Stream.empty();
    }

    private Optional<BlockPos> testWall(FeaturePlacementContext context, Random random, BlockPos pos) {
        StructureWorldAccess world = context.getWorld();

        // Single mutable instances used for all checks to prevent GC allocation overhead
        BlockPos.Mutable mutableCheckPos = new BlockPos.Mutable();
        BlockPos.Mutable mutableAirPos = new BlockPos.Mutable();

        // Symmetrical radius bounds calculation centered exactly around 'pos'
        int minW = -this.halfWidth;
        int maxW = this.width - this.halfWidth;
        int minH = -this.halfHeight;
        int maxH = this.height - this.halfHeight;

        // Iterate through all 4 horizontal orientations to look for a valid wall plane
        for (int d = 0; d < 4; d++) {
            Direction wallFacingDir = Direction.fromHorizontal(d); // From wall pointing INTO room air
            Direction wallRunningDir = wallFacingDir.rotateYClockwise(); // Sidelong horizontal propagation axis

            // Fast-path baseline check for the center coordinate before scanning the entire domain
            mutableCheckPos.set(pos);
            mutableAirPos.set(pos).move(wallFacingDir);

            // Center must be solid wall, front must be air, AND it must satisfy the extra custom predicate condition
            if (world.isAir(mutableAirPos) && this.testCondition.test(world, mutableCheckPos)) {

                boolean isWallValid = true;

                // Symmetrically scan the required dimensions surrounding the target center
                for (int w = minW; w < maxW; w++) {
                    for (int h = minH; h < maxH; h++) {

                        // Center-relative translations to map the full bounding canvas
                        mutableCheckPos.set(pos)
                                .move(wallRunningDir, w)
                                .move(Direction.UP, h);

                        mutableAirPos.set(mutableCheckPos)
                                .move(wallFacingDir);

                        // Height check guard to avoid evaluating out-of-bounds world coordinates
                        if (world.isOutOfHeightLimit(mutableCheckPos.getY())) {
                            isWallValid = false;
                            break;
                        }

                        // 1. Structural Checks: Wall must be solid, room space must be air
                        boolean isSolidWall = world.getBlockState(mutableCheckPos).isSideSolidFullSquare(world, mutableCheckPos, wallFacingDir);
                        boolean isAirSpace = world.isAir(mutableAirPos);

                        // 2. Extra Validation Check: Evaluate the block predicate on top of structural logic
                        boolean satisfiesPredicate = this.testCondition.test(world, mutableCheckPos);

                        // Early return condition: if any part of the structural OR predicate check fails, break early
                        if (!isSolidWall || !isAirSpace || !satisfiesPredicate) {
                            isWallValid = false;
                            break; // Short-circuit the vertical layer loop
                        }
                    }
                    if (!isWallValid) {
                        break; // Short-circuit the horizontal propagation loop
                    }
                }

                // If the entire calculated matrix passes all checks, return the original position
                if (isWallValid) {
                    return Optional.of(pos);
                }
            }
        }

        // No valid layout configuration matched the criteria
        return Optional.empty();
    }

    /**
     * Helper to perform a quick horizontal check for an air boundary.
     */
    private boolean hasHorizontalAirNeighbor(FeaturePlacementContext context, BlockPos pos) {
        StructureWorldAccess world = context.getWorld();
        return world.getBlockState(pos.north()).isAir()  ||
                world.getBlockState(pos.south()).isAir() ||
                world.getBlockState(pos.east()).isAir()  ||
                world.getBlockState(pos.west()).isAir();
    }

    @Override
    public PlacementModifierType<?> getType() {
        return ModPlacementModifierTypes.WALL_SEARCH;
    }

    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }
    public BlockPredicate getTestCondition() { return this.testCondition; }
}
