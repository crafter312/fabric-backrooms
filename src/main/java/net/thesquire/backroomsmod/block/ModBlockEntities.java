package net.thesquire.backroomsmod.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.BlockPos;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.block.entity.*;
import net.thesquire.backroomsmod.block.entity.flickering.FluorescentLightBlockEntity;
import net.thesquire.backroomsmod.block.entity.flickering.MountableFluorescentLightBlockEntity;

import java.util.function.BiFunction;

public class ModBlockEntities {

    public static BlockEntityType<IndustrialAlloySmelterBlockEntity> INDUSTRIAL_ALLOY_SMELTER;
    public static BlockEntityType<MagneticDistortionSystemControlComputerBlockEntity> MAGNETIC_DISTORTION_SYSTEM_CONTROL_COMPUTER;
    public static BlockEntityType<FluorescentLightBlockEntity> FLUORESCENT_LIGHT;
    public static BlockEntityType<MountableFluorescentLightBlockEntity> MOUNTABLE_FLUORESCENT_LIGHT;
    public static BlockEntityType<PortalPlacerBlockEntity> PORTAL_PLACER;
    public static BlockEntityType<ElevatorButtonBlockEntity> ELEVATOR_BUTTON;
    public static BlockEntityType<ElevatorDoorBlockEntity> ELEVATOR_DOOR;

    public static void registerBlockEntities() {
        BackroomsMod.LOGGER.info("Registering block entities for " + BackroomsMod.MOD_ID);

        INDUSTRIAL_ALLOY_SMELTER = register("industrial_alloy_smelter",
                IndustrialAlloySmelterBlockEntity::new, ModBlocks.INDUSTRIAL_ALLOY_SMELTER);
        MAGNETIC_DISTORTION_SYSTEM_CONTROL_COMPUTER = register("magnetic_distortion_system_control_computer",
                MagneticDistortionSystemControlComputerBlockEntity::new, ModBlocks.MAGNETIC_DISTORTION_SYSTEM_CONTROL_COMPUTER);
        FLUORESCENT_LIGHT = register("fluorescent_light",
                FluorescentLightBlockEntity::new, ModBlocks.FLUORESCENT_LIGHT);
        MOUNTABLE_FLUORESCENT_LIGHT = register("mountable_fluorescent_light",
                MountableFluorescentLightBlockEntity::new, ModBlocks.MOUNTABLE_FLUORESCENT_LIGHT);
        PORTAL_PLACER = register("portal_placer",
                PortalPlacerBlockEntity::new, ModBlocks.PORTAL_PLACER);
        ELEVATOR_BUTTON = register("elevator_button",
                ElevatorButtonBlockEntity::new, ModBlocks.ELEVATOR_BUTTON);
        ELEVATOR_DOOR = register("elevator_door",
                ElevatorDoorBlockEntity::new, ModBlocks.ELEVATOR_DOOR);
    }

    public static <T extends BlockEntity> BlockEntityType<T> register(String name, BiFunction<BlockPos, BlockState, T> supplier, Block... blocks) {
        BlockEntityType<T> blockEntityType = FabricBlockEntityTypeBuilder.create(supplier::apply, blocks).build(null);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, BackroomsMod.makeId(name), blockEntityType);
        return blockEntityType;
    }

}
