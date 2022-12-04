package net.thesquire.backroomsmod.screen;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.block.entity.IndustrialAlloySmelterBlockEntity;
import net.thesquire.backroomsmod.block.entity.MagneticDistortionSystemControlComputerBlockEntity;
import org.lwjgl.system.CallbackI;
import techreborn.blockentity.GuiType;

public class ModGuis {

    public static GuiType<IndustrialAlloySmelterBlockEntity> INDUSTRIAL_ALLOY_SMELTER;
    public static GuiType<MagneticDistortionSystemControlComputerBlockEntity> MAGNETIC_DISTORTION_SYSTEM_CONTROL_COMPUTER;

    public static void registerGuis() {
        BackroomsMod.LOGGER.info("Registring Mod Guis for " + BackroomsMod.MOD_ID);

        INDUSTRIAL_ALLOY_SMELTER = register("industrial_alloy_smelter");
        MAGNETIC_DISTORTION_SYSTEM_CONTROL_COMPUTER = register("magnetic_distortion_system_control_computer");
    }

    private static <T extends BlockEntity> GuiType<T> register(String name) {
        return GuiType.register(new Identifier(BackroomsMod.MOD_ID, name));
    }

}
