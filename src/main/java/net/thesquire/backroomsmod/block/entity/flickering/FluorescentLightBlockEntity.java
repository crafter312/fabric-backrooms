package net.thesquire.backroomsmod.block.entity.flickering;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.thesquire.backroomsmod.block.ModBlockEntities;
import net.thesquire.backroomsmod.block.entity.FlickeringBlockEntity;

public class FluorescentLightBlockEntity extends FlickeringBlockEntity {

    public static final int defaultLuminance = 15;

    public FluorescentLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUORESCENT_LIGHT, pos, state, defaultLuminance);
    }

}
