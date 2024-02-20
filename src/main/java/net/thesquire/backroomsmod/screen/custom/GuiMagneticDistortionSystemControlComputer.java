package net.thesquire.backroomsmod.screen.custom;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.thesquire.backroomsmod.block.entity.MagneticDistortionSystemControlComputerBlockEntity;
import net.thesquire.backroomsmod.util.ModServerboundPackets;
import reborncore.client.ClientNetworkManager;
import reborncore.client.gui.GuiBase;
import reborncore.client.gui.widget.GuiButtonExtended;
import reborncore.common.screen.BuiltScreenHandler;

public class GuiMagneticDistortionSystemControlComputer extends GuiBase<BuiltScreenHandler> {

    private final MagneticDistortionSystemControlComputerBlockEntity blockEntity;
    private final GuiButtonExtended ACTIVATE_BUTTON; // 56, 40
    private final int activeX = 67;
    private final int activeY = 40;

    public GuiMagneticDistortionSystemControlComputer(int syncID, final PlayerEntity player, final MagneticDistortionSystemControlComputerBlockEntity blockEntity) {
        super(player, blockEntity, blockEntity.createScreenHandler(syncID, player));
        this.blockEntity = blockEntity;
        ACTIVATE_BUTTON  = new GuiButtonExtended(x + activeX, y + activeY, 64, 16, Text.literal("ACTIVATE").formatted(Formatting.GREEN),
                (ButtonWidget buttonWidget) -> activate());
    }

    @Override
    public void init() {
        super.init();
        addDrawableChild(new GuiButtonExtended(x + 54, y + 76, 40, 12, Text.literal("Left"), (ButtonWidget buttonWidget) -> sendSideChange(false)));
        addDrawableChild(new GuiButtonExtended(x + 54 + 48, y + 76, 40, 12, Text.literal("Right"), (ButtonWidget buttonWidget) -> sendSideChange(true)));
    }

    @Override
    protected void drawBackground(DrawContext drawContext, float lastFrameDuration, int mouseX, int mouseY) {
        super.drawBackground(drawContext, lastFrameDuration, mouseX, mouseY);
        final GuiBase.Layer layer = Layer.BACKGROUND;

        drawSlot(drawContext, 8, 72, layer);

        if (blockEntity.isMultiblockValid()) {
            builder.drawHologramButton(drawContext, this, 6, 4, mouseX, mouseY, layer);

            if (!children().contains(ACTIVATE_BUTTON)) addDrawableChild(ACTIVATE_BUTTON);
            ACTIVATE_BUTTON.setX(x + activeX);
            ACTIVATE_BUTTON.setY(y + activeY);
        }
        else if (children().contains(ACTIVATE_BUTTON)) remove(ACTIVATE_BUTTON);
    }

    @Override
    protected void drawForeground(DrawContext drawContext, int mouseX, int mouseY) {
        super.drawForeground(drawContext, mouseX, mouseY);
        final GuiBase.Layer layer = GuiBase.Layer.FOREGROUND;

        if(blockEntity.isMultiblockValid()) {
            addHologramButton(6, 4, 212, layer).clickHandler(this::hologramToggle);
        } else {
            builder.drawMultiblockMissingBar(drawContext, this, layer);
            addHologramButton(76, 56, 212, layer).clickHandler(this::hologramToggle);
            builder.drawHologramButton(drawContext, this, 76, 56, mouseX, mouseY, layer);
        }

        builder.drawMultiEnergyBar(drawContext, this, 9, 19, this.blockEntity.getEnergy(), this.blockEntity.getMaxStoredPower(), mouseX, mouseY, 0, layer);
    }

    public void hologramToggle(GuiButtonExtended button, double x, double y) {
        blockEntity.renderMultiblock ^= !hideGuiElements();
    }

    private void sendSideChange(boolean side) {
        ClientNetworkManager.sendToServer(ModServerboundPackets.createPacketMagneticDistortionSystemSide(side, blockEntity.getPos()));
    }

    private void activate() {
        if (blockEntity.isMultiblockValid() && blockEntity.getEnergy() == blockEntity.getMaxStoredPower()) {
            ClientNetworkManager.sendToServer(ModServerboundPackets.createPacketMagneticDistortionSystemActive(true, blockEntity.getPos()));
        }
    }

}
