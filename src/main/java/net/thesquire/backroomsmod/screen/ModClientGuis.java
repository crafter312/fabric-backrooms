package net.thesquire.backroomsmod.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.block.entity.IndustrialAlloySmelterBlockEntity;
import net.thesquire.backroomsmod.block.entity.MagneticDistortionSystemControlComputerBlockEntity;
import net.thesquire.backroomsmod.screen.custom.GuiIndustrialAlloySmelter;
import net.thesquire.backroomsmod.screen.custom.GuiMagneticDistortionSystemControlComputer;
import techreborn.blockentity.GuiType;
import techreborn.client.ClientGuiType;
import techreborn.client.GuiFactory;

@Environment(EnvType.CLIENT)
public class ModClientGuis {

    public static ClientGuiType<IndustrialAlloySmelterBlockEntity> INDUSTRIAL_ALLOY_SMELTER;
    public static ClientGuiType<MagneticDistortionSystemControlComputerBlockEntity> MAGNETIC_DISTORTION_SYSTEM_CONTROL_COMPUTER;

    public static void registerClientGuis() {
        BackroomsMod.LOGGER.info("Registering ClientGuiTypes for " + BackroomsMod.MOD_ID);

        INDUSTRIAL_ALLOY_SMELTER = register(ModGuis.INDUSTRIAL_ALLOY_SMELTER, GuiIndustrialAlloySmelter::new);
        MAGNETIC_DISTORTION_SYSTEM_CONTROL_COMPUTER = register(ModGuis.MAGNETIC_DISTORTION_SYSTEM_CONTROL_COMPUTER, GuiMagneticDistortionSystemControlComputer::new);
    }

    private static <T extends BlockEntity> ClientGuiType<T> register(GuiType<T> type, GuiFactory<T> factory) {
        return new ClientGuiType<T>(type, factory);
    }

}
