package net.thesquire.backroomsmod.block.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.sound.BlockSoundGroup;
import net.thesquire.backroomsmod.sound.ModSounds;

// The only purpose of this class is to play my custom warehouse concrete sound groups
public class WarehouseConcreteStairsBlock extends StairsBlock {
    /**
     * Access widened by fabric-transitive-access-wideners-v1 to accessible
     * Access widened by architectury to accessible
     *
     * @param baseBlockState block of which this is a stairs variant
     * @param settings block settings
     */
    public WarehouseConcreteStairsBlock(BlockState baseBlockState, Settings settings) {
        super(baseBlockState, settings);
    }

    @Override
    public BlockSoundGroup getSoundGroup(BlockState state) {
        if(state.get(WATERLOGGED)) return ModSounds.WAREHOUSE_CONCRETE_WATERLOGGED;
        return ModSounds.WAREHOUSE_CONCRETE;
    }

}
