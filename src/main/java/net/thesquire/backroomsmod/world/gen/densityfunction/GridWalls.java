package net.thesquire.backroomsmod.world.gen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.thesquire.backroomsmod.util.ModUtils;

public class GridWalls implements DensityFunction.Base {

    private static final int MAX_SPACING = 100;
    private static final int MAX_WALL_THICKNESS = 4;
    private static final int MIN_DOOR_WIDTH = 1;
    private static final Codec<Integer> SPACING_RANGE = Codec.intRange(1, MAX_SPACING);
    private static final Codec<Integer> WALL_THICKNESS_RANGE = Codec.intRange(1, MAX_WALL_THICKNESS);
    private static final Codec<Float> SCALE_RANGE = Codec.floatRange(0, 100);
    private static final Codec<Integer> DOOR_WIDTH_RANGE = Codec.intRange(MIN_DOOR_WIDTH, (MAX_SPACING - MAX_WALL_THICKNESS) / 2);

    private static final MapCodec<GridWalls> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SCALE_RANGE.fieldOf("xz_scale").forGetter(GridWalls::getXz_scale),
            SPACING_RANGE.fieldOf("x_spacing").forGetter(GridWalls::getX_spacing),
            SPACING_RANGE.fieldOf("z_spacing").forGetter(GridWalls::getZ_spacing),
            WALL_THICKNESS_RANGE.fieldOf("x_wall_thickness").forGetter(GridWalls::getX_wall_thickness),
            WALL_THICKNESS_RANGE.fieldOf("z_wall_thickness").forGetter(GridWalls::getZ_wall_thickness),
            Codec.BOOL.optionalFieldOf("has_doors", false).forGetter(GridWalls::getHas_doors),
            DOOR_WIDTH_RANGE.optionalFieldOf("door_width", MIN_DOOR_WIDTH).forGetter(GridWalls::getDoor_width)
    ).apply(instance, GridWalls::new));

    public static final CodecHolder<GridWalls> CODEC_HOLDER = CodecHolder.of(CODEC);

    ////////////////////////////////////////////////////////////////////////////////////

    private final float xz_scale;
    private final int x_spacing;
    private final int z_spacing;
    private final int x_wall_thickness;
    private final int z_wall_thickness;
    private final boolean has_doors;
    private final int door_width;

    private final int x_mask;
    private final int z_mask;
    private final int x_total;
    private final int z_total;

    private final Random random = Random.create();
    private final SimplexNoiseSampler noiseSampler = new SimplexNoiseSampler(random);

    public GridWalls(float xz_scale, int x_spacing, int z_spacing, int x_wall_thickness, int z_wall_thickness, boolean has_doors, int door_width) {
        this.xz_scale = xz_scale;
        this.x_spacing = x_spacing;
        this.z_spacing = z_spacing;
        this.x_wall_thickness = x_wall_thickness;
        this.z_wall_thickness = z_wall_thickness;
        this.has_doors = has_doors;
        this.door_width = door_width;

        this.x_mask = ((1 << x_wall_thickness) - 1) << (x_spacing / 2);
        this.z_mask = ((1 << x_wall_thickness) - 1) << (z_spacing / 2);
        this.x_total = x_spacing + x_wall_thickness;
        this.z_total = z_spacing + z_wall_thickness;
    }

    @Override
    public double sample(NoisePos pos) {
        int x = pos.blockX();
        int z = pos.blockZ();
        int x_mod = Math.abs(x) % this.x_total;
        int z_mod = Math.abs(z) % this.z_total;

        boolean x_result = ((1 << x_mod) & this.x_mask) != 0;
        boolean z_result = ((1 << z_mod) & this.z_mask) != 0;
        if(!(x_result || z_result)) return -1;

        int x_wall_coord = (x / this.x_total) + ((-1 + Integer.signum(x)) / 2);
        int z_wall_coord = (z / this.z_total) + ((-1 + Integer.signum(z)) / 2);

        double sample = this.noiseSampler.sample(x_wall_coord * this.xz_scale, z_wall_coord * this.xz_scale);
        double is_wall = Math.signum(sample);
        if(!this.has_doors || (x_result && z_result) || is_wall < 0) return is_wall;

        return z_result
                ? ModUtils.boolToInt(((1 << x_mod) & makeDoorMask(sample, this.x_spacing, this.x_wall_thickness, this.x_mask)) == 0, 1, -1)
                : ModUtils.boolToInt(((1 << z_mod) & makeDoorMask(sample, this.z_spacing, this.z_wall_thickness, this.z_mask)) == 0, 1, -1);
    }

    private int makeDoorMask(double sample, int spacing, int wall_thickness, int mask) {
        int door_mask_x_temp = ((1 << this.door_width) - 1) << (int) (Math.abs(sample) * (spacing - (2 * (this.door_width - 1))));
        return door_mask_x_temp << (ModUtils.boolToInt((door_mask_x_temp & mask) > 0, 1, 0)
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

    public float getXz_scale() { return this.xz_scale; }
    public int getX_spacing() { return this.x_spacing; }
    public int getZ_spacing() { return this.z_spacing; }
    public int getX_wall_thickness() { return this.x_wall_thickness; }
    public int getZ_wall_thickness() { return this.z_wall_thickness; }
    public boolean getHas_doors() { return this.has_doors; }
    public int getDoor_width() { return this.door_width; }

}
