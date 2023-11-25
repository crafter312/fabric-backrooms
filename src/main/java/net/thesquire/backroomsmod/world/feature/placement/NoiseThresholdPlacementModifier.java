package net.thesquire.backroomsmod.world.feature.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;
import net.thesquire.backroomsmod.world.gen.densityfunction.GridWalls;

import java.util.function.Function;
import java.util.stream.Stream;

public class NoiseThresholdPlacementModifier extends PlacementModifier {

    private static final Function<DensityFunction, DataResult<DensityFunction>> DENSITY_FUNCTION_CHECKER =
            (value) -> value instanceof InterpolatedNoiseSampler ? DataResult.success(value)
                    : DataResult.error(() -> "DensityFunction parameter not of type 'old_blended_noise'");

    public static final Codec<NoiseThresholdPlacementModifier> MODIFIER_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DensityFunction.FUNCTION_CODEC.fieldOf("input").flatXmap(DENSITY_FUNCTION_CHECKER, DENSITY_FUNCTION_CHECKER)
                    .forGetter(NoiseThresholdPlacementModifier::getInput),
            GridWalls.CODEC_HOLDER.codec().fieldOf("grid_walls").forGetter(NoiseThresholdPlacementModifier::getGrid_walls)
    ).apply(instance, instance.stable(NoiseThresholdPlacementModifier::new)));

    /////////////////////////////////////////////////////////////////////

    private final DensityFunction input;
    private final GridWalls grid_walls;

    public NoiseThresholdPlacementModifier(DensityFunction input, GridWalls gridWalls) {
        this.input = input;
        this.grid_walls = gridWalls;
    }

    @Override
    public Stream<BlockPos> getPositions(FeaturePlacementContext context, Random random, BlockPos pos) {
        int x = pos.getX();
        int z = pos.getZ();
        int x_wall_coord = (x / this.grid_walls.getX_total()) + ((-1 + Integer.signum(x)) / 2);
        int z_wall_coord = (z / this.grid_walls.getZ_total()) + ((-1 + Integer.signum(z)) / 2);
        double sample = this.input.sample(new DensityFunction.UnblendedNoisePos(x_wall_coord, 0, z_wall_coord));

        return sample < 0 ? Stream.of(pos) : Stream.of(new BlockPos[0]);
    }

    @Override
    public PlacementModifierType<?> getType() {
        return ModPlacementModifierTypes.NOISE_THRESHOLD;
    }

    public DensityFunction getInput() { return this.input; }

    public GridWalls getGrid_walls() { return this.grid_walls; }

}
