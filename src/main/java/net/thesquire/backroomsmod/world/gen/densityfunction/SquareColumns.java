package net.thesquire.backroomsmod.world.gen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.world.gen.densityfunction.DensityFunction;

public class SquareColumns implements DensityFunction.Base {

    private static final Codec<Integer> SPACING_RANGE = Codec.intRange(1, 100);
    private static final Codec<Integer> COLUMN_SIZE = Codec.intRange(1, 100);
    private static final MapCodec<SquareColumns> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SPACING_RANGE.fieldOf("x_spacing").forGetter(SquareColumns::getX_spacing),
            SPACING_RANGE.fieldOf("z_spacing").forGetter(SquareColumns::getZ_spacing),
            COLUMN_SIZE.fieldOf("x_column_size").forGetter(SquareColumns::getX_column_size),
            COLUMN_SIZE.fieldOf("z_column_size").forGetter(SquareColumns::getZ_column_size)
    ).apply(instance, SquareColumns::new));

    public static final CodecHolder<SquareColumns> CODEC_HOLDER = CodecHolder.of(CODEC);

    ////////////////////////////////////////////////////////////////////////////////////

    private final int x_spacing;
    private final int z_spacing;
    private final int x_column_size;
    private final int z_column_size;

    private final int x_mask;
    private final int z_mask;

    public SquareColumns(int x_spacing, int z_spacing, int x_column_size, int z_column_size) {
        this.x_spacing = x_spacing;
        this.z_spacing = z_spacing;
        this.x_column_size = x_column_size;
        this.z_column_size = z_column_size;

        this.x_mask = ((1 << x_column_size) - 1) << (x_spacing / 2);
        this.z_mask = ((1 << z_column_size) - 1) << (z_spacing / 2);
    }

    @Override
    public double sample(NoisePos pos) {
        int x = Math.abs(pos.blockX());
        int z = Math.abs(pos.blockZ());

        boolean x_result = ((1 << (x % (x_column_size + x_spacing))) & x_mask) != 0;
        boolean z_result = ((1 << (z % (z_column_size + z_spacing))) & z_mask) != 0;

        return x_result && z_result ? 1 : -1;
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

    public int getX_spacing() { return this.x_spacing; }
    public int getZ_spacing() { return this.z_spacing; }
    public int getX_column_size() { return this.x_column_size; }
    public int getZ_column_size() { return this.z_column_size; }

}
