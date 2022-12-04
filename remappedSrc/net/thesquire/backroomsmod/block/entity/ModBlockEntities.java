package net.thesquire.backroomsmod.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.block.ModBlocks;

import java.util.function.BiFunction;

public class ModBlockEntities {

    public static BlockEntityType<IndustrialAlloySmelterBlockEntity> INDUSTRIAL_ALLOY_SMELTER;
    public static BlockEntityType<MagneticDistortionSystemControlComputerBlockEntity> MAGNETIC_DISTORTION_SYSTEM_CONTROL_COMPUTER;
    public static BlockEntityType<FluorescentLightBlockEntity> FLUORESCENT_LIGHT;

    public static void registerBlockEntities() {
        INDUSTRIAL_ALLOY_SMELTER = register(
                new Identifier(BackroomsMod.MOD_ID, "industrial_alloy_smelter"), IndustrialAlloySmelterBlockEntity::new,
                ModBlocks.INDUSTRIAL_ALLOY_SMELTER);

        MAGNETIC_DISTORTION_SYSTEM_CONTROL_COMPUTER = register(
                new Identifier(BackroomsMod.MOD_ID, "magnetic_distortion_system_control_computer"), MagneticDistortionSystemControlComputerBlockEntity::new,
                ModBlocks.MAGNETIC_DISTORTION_SYSTEM_CONTROL_COMPUTER);

        FLUORESCENT_LIGHT = register(
                new Identifier(BackroomsMod.MOD_ID, "fluorescent_light"), FluorescentLightBlockEntity::new,
                ModBlocks.FLUORESCENT_LIGHT);
    }

    public static <T extends BlockEntity> BlockEntityType<T> register(Identifier id, BiFunction<BlockPos, BlockState, T> supplier, Block... blocks) {
        BlockEntityType<T> blockEntityType = FabricBlockEntityTypeBuilder.create(supplier::apply, blocks).build(null);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id, blockEntityType);
        return blockEntityType;
    }

}
