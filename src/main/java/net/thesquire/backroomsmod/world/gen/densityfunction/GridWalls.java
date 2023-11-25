package net.thesquire.backroomsmod.world.gen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.thesquire.backroomsmod.util.ModUtils;

import java.util.function.Function;

public class GridWalls implements DensityFunction.Base {

    private static final int MIN_DOOR_WIDTH = 1;
    public record GridWallsData(int x_spacing, int x_wall_thickness, int z_spacing, int z_wall_thickness, boolean has_doors, int door_width) {
        public static final MapCodec<GridWallsData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.fieldOf("x_spacing").forGetter(GridWallsData::x_spacing),
                Codec.INT.fieldOf("x_wall_thickness").forGetter(GridWallsData::x_wall_thickness),
                Codec.INT.fieldOf("z_spacing").forGetter(GridWallsData::z_spacing),
                Codec.INT.fieldOf("z_wall_thickness").forGetter(GridWallsData::z_wall_thickness),
                Codec.BOOL.optionalFieldOf("has_doors", false).forGetter(GridWallsData::has_doors),
                Codec.INT.optionalFieldOf("door_width", MIN_DOOR_WIDTH).forGetter(GridWallsData::door_width)
        ).apply(instance, GridWallsData::new));

        ////////////////////////////////////////////////////////////////////////////////////

        public GridWallsData(int x_spacing, int x_wall_thickness, int z_spacing, int z_wall_thickness, boolean has_doors) {
            this(x_spacing, x_wall_thickness, z_spacing, z_wall_thickness, has_doors, MIN_DOOR_WIDTH);
        }
    }
    private static final int MIN_TOTAL = 1;
    private static final int MAX_TOTAL = Long.SIZE;
    private static final Function<GridWallsData, DataResult<GridWallsData>> X_CHECKER = (value) -> {
        int total_size = value.x_spacing() + value.x_wall_thickness();
        return total_size >= MIN_TOTAL && total_size <= MAX_TOTAL ? DataResult.success(value)
                : DataResult.error(() -> "Total x size " + total_size + " outside of range [" + MIN_TOTAL + ":" + MAX_TOTAL + "]", value);
    };
    private static final Function<GridWallsData, DataResult<GridWallsData>> Z_CHECKER = (value) -> {
        int total_size = value.z_spacing() + value.z_wall_thickness();
        return total_size >= MIN_TOTAL && total_size <= MAX_TOTAL ? DataResult.success(value)
                : DataResult.error(() -> "Total z size " + total_size + " outside of range [" + MIN_TOTAL + ":" + MAX_TOTAL + "]", value);
    };
    private static final Function<GridWallsData, DataResult<GridWallsData>> DOOR_CHECKER = (value) -> {
        if (!value.has_doors()) return DataResult.success(value);
        int door_width = value.door_width();
        int max_x_width = value.x_spacing() / 2;
        int max_z_width = value.z_spacing() / 2;
        return door_width >= MIN_DOOR_WIDTH && door_width <= max_x_width && door_width <= max_z_width ? DataResult.success(value)
                : DataResult.error(() -> "Door width " + door_width + " outside of range [" + MIN_DOOR_WIDTH + ":" + Math.min(max_x_width, max_z_width));
    };
    private static final Function<RegistryEntry<DensityFunction>, DataResult<RegistryEntry<DensityFunction>>> DENSITY_FUNCTION_CHECKER =
            (value) -> value.value() instanceof InterpolatedNoiseSampler ? DataResult.success(value)
                    : DataResult.error(() -> "DensityFunction parameter not of type 'old_blended_noise'");

    private static final MapCodec<GridWalls> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DensityFunction.REGISTRY_ENTRY_CODEC.fieldOf("noise").flatXmap(DENSITY_FUNCTION_CHECKER, DENSITY_FUNCTION_CHECKER)
                    .forGetter(GridWalls::getNoise),
            GridWallsData.CODEC.fieldOf("size_params").flatXmap(X_CHECKER, X_CHECKER).flatXmap(Z_CHECKER, Z_CHECKER)
                    .flatXmap(DOOR_CHECKER, DOOR_CHECKER).forGetter((provider) -> new GridWallsData(
                            provider.getX_spacing(), provider.getX_wall_thickness(),
                            provider.getZ_spacing(), provider.getZ_wall_thickness(),
                            provider.getHas_doors(), provider.getDoor_width()))
    ).apply(instance, GridWalls::new));

    public static final CodecHolder<GridWalls> CODEC_HOLDER = CodecHolder.of(CODEC);

    ////////////////////////////////////////////////////////////////////////////////////

    private final RegistryEntry<DensityFunction> noise;
    private final int x_spacing;
    private final int z_spacing;
    private final int x_wall_thickness;
    private final int z_wall_thickness;
    private final boolean has_doors;
    private final int door_width;

    private final long x_mask;
    private final long z_mask;
    private final int x_total;
    private final int z_total;

    public GridWalls(RegistryEntry<DensityFunction> noise, GridWallsData size_params) {
        this.noise = noise;
        this.x_spacing = size_params.x_spacing();
        this.z_spacing = size_params.z_spacing();
        this.x_wall_thickness = size_params.x_wall_thickness();
        this.z_wall_thickness = size_params.z_wall_thickness();
        this.has_doors = size_params.has_doors();
        this.door_width = size_params.door_width();

        this.x_mask = ((1L << this.x_wall_thickness) - 1) << this.x_spacing;
        this.z_mask = ((1L << this.x_wall_thickness) - 1) << this.z_spacing;
        this.x_total = this.x_spacing + this.x_wall_thickness;
        this.z_total = this.z_spacing + this.z_wall_thickness;
    }

    @Override
    public double sample(NoisePos pos) {
        int x = pos.blockX();
        int z = pos.blockZ();
        int x_is_neg = Integer.signum(x) == -1 ? 1 : 0;
        int z_is_neg = Integer.signum(z) == -1 ? 1 : 0;
        x += x_is_neg;
        z += z_is_neg;
        int x_mod = Math.abs(x) % this.x_total;
        x_mod = x_is_neg == 1 ? this.x_total - 1 - x_mod : x_mod;
        int z_mod = Math.abs(z) % this.z_total;
        z_mod = z_is_neg == 1 ? this.z_total - 1 - z_mod : z_mod;

        boolean x_result = ((1L << x_mod) & this.x_mask) != 0;
        boolean z_result = ((1L << z_mod) & this.z_mask) != 0;
        if(!(x_result || z_result)) return -1;

        int x_wall_coord = (x / this.x_total) - x_is_neg;
        int z_wall_coord = (z / this.z_total) - z_is_neg;

        double sample = this.noise.value().sample(new UnblendedNoisePos(x_wall_coord, 0, z_wall_coord));
        double is_wall = Math.signum(sample);
        if(!this.has_doors || (x_result && z_result) || is_wall < 0) return is_wall;

        return z_result
                ? ModUtils.boolToInt(((1L << x_mod) & makeDoorMask(sample, this.x_spacing, this.x_wall_thickness, this.x_mask)) == 0, 1, -1)
                : ModUtils.boolToInt(((1L << z_mod) & makeDoorMask(sample, this.z_spacing, this.z_wall_thickness, this.z_mask)) == 0, 1, -1);
    }

    private long makeDoorMask(double sample, int spacing, int wall_thickness, long mask) {
        long door_mask_temp = ((1L << this.door_width) - 1) << (int) (Math.abs(sample) * (spacing - (2 * (this.door_width - 1))));
        return door_mask_temp << (ModUtils.boolToInt((door_mask_temp & mask) > 0, 1, 0)
                * (this.door_width + wall_thickness - 1));
    }

    @Override
    public double minValue() {
        return -1;
    }

    @Override
    public double maxValue() {
        return 1;
    }

    @Override
    public CodecHolder<? extends DensityFunction> getCodecHolder() {
        return CODEC_HOLDER;
    }

    public RegistryEntry<DensityFunction> getNoise() { return this.noise; }
    public int getX_spacing() { return this.x_spacing; }
    public int getZ_spacing() { return this.z_spacing; }
    public int getX_wall_thickness() { return this.x_wall_thickness; }
    public int getZ_wall_thickness() { return this.z_wall_thickness; }
    public boolean getHas_doors() { return this.has_doors; }
    public int getDoor_width() { return this.door_width; }

    public int getX_total() { return this.x_total; }

    public int getZ_total() { return this.z_total; }

}
