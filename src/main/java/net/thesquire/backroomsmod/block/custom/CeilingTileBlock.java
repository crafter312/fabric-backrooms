package net.thesquire.backroomsmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;

public class CeilingTileBlock extends Block {

    public static final BooleanProperty HAS_LIGHT = BooleanProperty.of("has_light");

    public CeilingTileBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(HAS_LIGHT, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HAS_LIGHT);
    }
}
