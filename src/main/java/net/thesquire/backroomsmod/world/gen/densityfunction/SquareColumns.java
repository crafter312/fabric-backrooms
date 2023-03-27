package net.thesquire.backroomsmod.world.gen.densityfunction;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.world.gen.densityfunction.DensityFunction;

import java.util.function.Function;

public class SquareColumns implements DensityFunction.Base {

    private static final int MIN_TOTAL = 1;
    private static final int MAX_TOTAL = Long.SIZE;
    private static final Codec<Pair<Integer, Integer>> SIZE_CODEC =
            Codec.pair(Codec.INT.fieldOf("spacing").codec(), Codec.INT.fieldOf("column_size").codec());
    private static final Function<Pair<Integer, Integer>, DataResult<Pair<Integer, Integer>>> X_CHECKER = (value) -> {
        int total_size = value.getFirst() + value.getSecond();
        return total_size >= MIN_TOTAL && total_size <= MAX_TOTAL ? DataResult.success(value)
                : DataResult.error("Total x size " + total_size + " outside of range [" + MIN_TOTAL + ":" + MAX_TOTAL + "]", value);
    };
    private static final Function<Pair<Integer, Integer>, DataResult<Pair<Integer, Integer>>> Z_CHECKER = (value) -> {
        int total_size = value.getFirst() + value.getSecond();
        return total_size >= MIN_TOTAL && total_size <= MAX_TOTAL ? DataResult.success(value)
                : DataResult.error("Total z size " + total_size + " outside of range [" + MIN_TOTAL + ":" + MAX_TOTAL + "]", value);
    };
    private static final MapCodec<SquareColumns> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SIZE_CODEC.fieldOf("x_params").flatXmap(X_CHECKER, X_CHECKER)
                    .forGetter(provider -> new Pair<>(provider.getX_spacing(), provider.getX_column_size())),
            SIZE_CODEC.fieldOf("z_params").flatXmap(Z_CHECKER, Z_CHECKER)
                    .forGetter(provider -> new Pair<>(provider.getZ_spacing(), provider.getZ_column_size()))
    ).apply(instance, SquareColumns::new));

    public static final CodecHolder<SquareColumns> CODEC_HOLDER = CodecHolder.of(CODEC);

    ////////////////////////////////////////////////////////////////////////////////////

    private final int x_spacing;
    private final int z_spacing;
    private final int x_column_size;
    private final int z_column_size;

    private final long x_mask;
    private final long z_mask;

    public SquareColumns(Pair<Integer, Integer> x_params, Pair<Integer, Integer> z_params) {
        this.x_spacing = x_params.getFirst();
        this.z_spacing = z_params.getFirst();
        this.x_column_size = x_params.getSecond();
        this.z_column_size = z_params.getSecond();

        this.x_mask = ((1L << x_column_size) - 1) << (x_spacing / 2);
        this.z_mask = ((1L << z_column_size) - 1) << (z_spacing / 2);
    }

    @Override
    public double sample(NoisePos pos) {
        int x = Math.abs(pos.blockX());
        int z = Math.abs(pos.blockZ());

        boolean x_result = ((1L << (x % (x_column_size + x_spacing))) & x_mask) != 0;
        boolean z_result = ((1L << (z % (z_column_size + z_spacing))) & z_mask) != 0;

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
