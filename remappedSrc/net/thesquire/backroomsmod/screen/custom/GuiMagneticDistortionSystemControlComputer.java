package net.thesquire.backroomsmod.screen.custom;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.util.Formatting;
import net.thesquire.backroomsmod.block.entity.MagneticDistortionSystemControlComputerBlockEntity;
import net.thesquire.backroomsmod.config.ModConfig;
import net.thesquire.backroomsmod.util.ModServerboundPackets;
import reborncore.client.gui.builder.GuiBase;
import reborncore.client.gui.builder.widget.GuiButtonExtended;
import reborncore.common.network.NetworkManager;
import reborncore.common.screen.BuiltScreenHandler;

public class GuiMagneticDistortionSystemControlComputer extends GuiBase<BuiltScreenHandler> {

    private final MagneticDistortionSystemControlComputerBlockEntity blockEntity;
    private GuiButtonExtended ACTIVATE_BUTTON; // 56, 40

    public GuiMagneticDistortionSystemControlComputer(int syncID, final PlayerEntity player, final MagneticDistortionSystemControlComputerBlockEntity blockEntity) {
        super(player, blockEntity, blockEntity.createScreenHandler(syncID, player));
        this.blockEntity = blockEntity;
        ACTIVATE_BUTTON  = new GuiButtonExtended(x + 209, y + 80, 64, 16, new LiteralTextContent("ACTIVATE").formatted(Formatting.GREEN),
                (ButtonWidget buttonWidget) -> activate());
    }

    @Override
    public void init() {
        super.init();
        addDrawableChild(new GuiButtonExtended(x + 44, y + 76, 40, 12, new LiteralTextContent("Left"), (ButtonWidget buttonWidget) -> sendSideChange(false)));
        addDrawableChild(new GuiButtonExtended(x + 44 + 48, y + 76, 40, 12, new LiteralTextContent("Right"), (ButtonWidget buttonWidget) -> sendSideChange(true)));
    }

    @Override
    protected void drawBackground(MatrixStack matrixStack, float lastFrameDuration, int mouseX, int mouseY) {
        super.drawBackground(matrixStack, lastFrameDuration, mouseX, mouseY);
        final GuiBase.Layer layer = Layer.BACKGROUND;

        drawSlot(matrixStack, 8, 72, layer);

        builder.drawJEIButton(matrixStack, this, 158, 5, layer);
        if (blockEntity.isMultiblockValid()) {
            builder.drawHologramButton(matrixStack, this, 6, 4, mouseX, mouseY, layer);
            if (!children().contains(ACTIVATE_BUTTON)) addDrawableChild(ACTIVATE_BUTTON);
        }
        else if (children().contains(ACTIVATE_BUTTON)) remove(ACTIVATE_BUTTON);
    }

    @Override
    protected void drawForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawForeground(matrixStack, mouseX, mouseY);
        final GuiBase.Layer layer = GuiBase.Layer.FOREGROUND;

        if(blockEntity.isMultiblockValid()) {
            addHologramButton(6, 4, 212, layer).clickHandler(this::hologramToggle);
        } else {
            builder.drawMultiblockMissingBar(matrixStack, this, layer);
            addHologramButton(76, 56, 212, layer).clickHandler(this::hologramToggle);
            builder.drawHologramButton(matrixStack, this, 76, 56, mouseX, mouseY, layer);
        }

        builder.drawMultiEnergyBar(matrixStack, this, 9, 19, this.blockEntity.getEnergy(), this.blockEntity.getMaxStoredPower(), mouseX, mouseY, 0, layer);
    }

    public void hologramToggle(GuiButtonExtended button, double x, double y) {
        blockEntity.renderMultiblock ^= !hideGuiElements();
    }

    private void sendSideChange(boolean side) {
        NetworkManager.sendToServer(ModServerboundPackets.createPacketMagneticDistortionSystemSide(side, blockEntity.getPos()));
    }

    private void activate() {
        if (blockEntity.isMultiblockValid() && blockEntity.getEnergy() == blockEntity.getMaxStoredPower()) {
            NetworkManager.sendToServer(ModServerboundPackets.createPacketMagneticDistortionSystemActive(true, blockEntity.getPos()));
        }
    }

}
