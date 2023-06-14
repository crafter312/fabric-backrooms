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
import net.thesquire.backroomsmod.util.ModUtils;

import java.util.stream.Stream;

public class WallAdjacentPlacementModifier extends PlacementModifier {

    public static final Codec<WallAdjacentPlacementModifier> MODIFIER_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ModUtils.HORIZONTAL_CODEC.fieldOf("initial_direction").forGetter(WallAdjacentPlacementModifier::getInitialDirection),
            BlockPredicate.BASE_CODEC.fieldOf("target_condition").forGetter(WallAdjacentPlacementModifier::getTargetPredicate),
            BlockPredicate.BASE_CODEC.optionalFieldOf("allowed_search_condition", BlockPredicate.alwaysTrue()).forGetter(WallAdjacentPlacementModifier::getAllowedSearchPredicate),
            Codec.intRange(1, 16).fieldOf("max_steps").forGetter(WallAdjacentPlacementModifier::getMaxSteps)
    ).apply(instance, instance.stable(WallAdjacentPlacementModifier::new)));

    public static WallAdjacentPlacementModifier of(Direction initialDirection, BlockPredicate targetPredicate, BlockPredicate allowedSearchPredicate, int maxSteps) {
        return new WallAdjacentPlacementModifier(initialDirection, targetPredicate, allowedSearchPredicate, maxSteps);
    }

    public static WallAdjacentPlacementModifier of(BlockPredicate targetPredicate, int maxSteps) {
        return of(Direction.NORTH, targetPredicate, BlockPredicate.alwaysTrue(), maxSteps);
    }

    /////////////////////////////////////////////////////////////////////

    private final Direction initialDirection;
    private final BlockPredicate targetPredicate;
    private final BlockPredicate allowedSearchPredicate;
    private final int maxSteps;

    public WallAdjacentPlacementModifier(Direction initialDirection, BlockPredicate targetPredicate, BlockPredicate allowedSearchPredicate, int maxSteps) {
        this.initialDirection = initialDirection;
        this.targetPredicate = targetPredicate;
        this.allowedSearchPredicate = allowedSearchPredicate;
        this.maxSteps = maxSteps;
    }

    @Override
    public Stream<BlockPos> getPositions(FeaturePlacementContext context, Random random, BlockPos pos) {
        BlockPos.Mutable mutable = pos.mutableCopy();
        Direction direction = this.initialDirection;
        StructureWorldAccess structureWorldAccess = context.getWorld();
        if (!this.allowedSearchPredicate.test(structureWorldAccess, mutable)) {
            return Stream.of(new BlockPos[0]);
        }
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < this.maxSteps; ++i) {
                if (this.targetPredicate.test(structureWorldAccess, mutable)) {
                    return Stream.of(mutable);
                }
                mutable.move(direction);
                if (structureWorldAccess.isOutOfHeightLimit(mutable.getY())) {
                    return Stream.of(new BlockPos[0]);
                }
                if (!this.allowedSearchPredicate.test(structureWorldAccess, mutable)) break;
            }
            if (j < 3) {
                mutable = pos.mutableCopy();
                direction = direction.rotateYClockwise();
            }
        }
        if (this.targetPredicate.test(structureWorldAccess, mutable)) {
            return Stream.of(mutable);
        }
        return Stream.of(new BlockPos[0]);
    }

    @Override
    public PlacementModifierType<?> getType() {
        return ModPlacementModifierTypes.WALL_ADJACENT;
    }

    public Direction getInitialDirection(){ return this.initialDirection; }
    public BlockPredicate getTargetPredicate() { return this.targetPredicate; }
    public BlockPredicate getAllowedSearchPredicate() { return this.allowedSearchPredicate; }
    public int getMaxSteps() { return this.maxSteps; }

}
