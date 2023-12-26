package net.thesquire.backroomsmod.world.feature.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;
import net.thesquire.backroomsmod.world.gen.densityfunction.GridWalls;

import java.util.stream.Stream;

public class NoiseThresholdPlacementModifier extends PlacementModifier {

    public static final Codec<NoiseThresholdPlacementModifier> MODIFIER_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DensityFunction.REGISTRY_ENTRY_CODEC.fieldOf("input").forGetter(NoiseThresholdPlacementModifier::getInput),
            DensityFunction.REGISTRY_ENTRY_CODEC.fieldOf("grid_walls").forGetter(NoiseThresholdPlacementModifier::getGrid_walls)
    ).apply(instance, instance.stable(NoiseThresholdPlacementModifier::new)));

    /////////////////////////////////////////////////////////////////////

    private final RegistryEntry<DensityFunction> input;
    private final RegistryEntry<DensityFunction> grid_walls;

    public NoiseThresholdPlacementModifier(RegistryEntry<DensityFunction> input, RegistryEntry<DensityFunction> gridWalls) {
        this.input = input;
        this.grid_walls = gridWalls;
    }

    @Override
    public Stream<BlockPos> getPositions(FeaturePlacementContext context, Random random, BlockPos pos) {
        // check types
        if (!(this.input.value() instanceof InterpolatedNoiseSampler) || !(this.grid_walls.value() instanceof GridWalls))
            return Stream.of(new BlockPos[0]);
        GridWalls gridWalls = (GridWalls) this.grid_walls.value();

        int x = pos.getX();
        int z = pos.getZ();
        int x_wall_coord = (x / gridWalls.getX_total()) + ((-1 + Integer.signum(x)) / 2);
        int z_wall_coord = (z / gridWalls.getZ_total()) + ((-1 + Integer.signum(z)) / 2);
        double sample = this.input.value().sample(new DensityFunction.UnblendedNoisePos(x_wall_coord, 0, z_wall_coord));

        return sample < 0 ? Stream.of(pos) : Stream.of(new BlockPos[0]);
    }

    @Override
    public PlacementModifierType<?> getType() {
        return ModPlacementModifierTypes.NOISE_THRESHOLD;
    }

    public RegistryEntry<DensityFunction> getInput() { return this.input; }

    public RegistryEntry<DensityFunction> getGrid_walls() { return this.grid_walls; }

}
