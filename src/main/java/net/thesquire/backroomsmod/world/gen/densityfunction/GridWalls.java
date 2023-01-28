package net.thesquire.backroomsmod.world.gen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.densityfunction.DensityFunction;

public class GridWalls implements DensityFunction.Base {

    private static final Codec<Integer> SPACING_RANGE = Codec.intRange(1, 100);
    private static final Codec<Integer> WALL_THICKNESS_RANGE = Codec.intRange(1, 4);
    private static final Codec<Float> SCALE_RANGE = Codec.floatRange(0, 100);
    private static final MapCodec<GridWalls> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SCALE_RANGE.fieldOf("xz_scale").forGetter(GridWalls::getXz_scale),
            SPACING_RANGE.fieldOf("x_spacing").forGetter(GridWalls::getX_spacing),
            SPACING_RANGE.fieldOf("z_spacing").forGetter(GridWalls::getZ_spacing),
            WALL_THICKNESS_RANGE.fieldOf("x_wall_thickness").forGetter(GridWalls::getX_wall_thickness),
            WALL_THICKNESS_RANGE.fieldOf("z_wall_thickness").forGetter(GridWalls::getZ_wall_thickness)
    ).apply(instance, GridWalls::new));

    public static final CodecHolder<GridWalls> CODEC_HOLDER = CodecHolder.of(CODEC);

    ////////////////////////////////////////////////////////////////////////////////////

    private final float xz_scale;
    private final int x_spacing;
    private final int z_spacing;
    private final int x_wall_thickness;
    private final int z_wall_thickness;

    private final int x_mask;
    private final int z_mask;
    private final int x_total;
    private final int z_total;

    private final Random random = Random.create();
    private final SimplexNoiseSampler noiseSampler = new SimplexNoiseSampler(random);

    public GridWalls(float xz_scale, int x_spacing, int z_spacing, int x_wall_thickness, int z_wall_thickness) {
        this.xz_scale = xz_scale;
        this.x_spacing = x_spacing;
        this.z_spacing = z_spacing;
        this.x_wall_thickness = x_wall_thickness;
        this.z_wall_thickness = z_wall_thickness;

        this.x_mask = ((1 << x_wall_thickness) - 1) << (x_spacing / 2);
        this.z_mask = ((1 << x_wall_thickness) - 1) << (z_spacing / 2);
        this.x_total = x_spacing + x_wall_thickness;
        this.z_total = z_spacing + z_wall_thickness;
    }

    @Override
    public double sample(NoisePos pos) {
        int x = pos.blockX();
        int z = pos.blockZ();

        boolean x_result = ((1 << (Math.abs(x) % x_total)) & x_mask) != 0;
        boolean z_result = ((1 << (Math.abs(z) % z_total)) & z_mask) != 0;
        if(!(x_result || z_result)) return -1;

        int x_wall_coord = (x / x_total) + ((-1 + Integer.signum(x)) / 2);
        int z_wall_coord = (z / z_total) + ((-1 + Integer.signum(z)) / 2);
        return MathHelper.clamp(noiseSampler.sample(x_wall_coord * xz_scale, z_wall_coord * xz_scale), -1, 1);
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

}
