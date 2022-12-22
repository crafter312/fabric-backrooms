package net.thesquire.backroomsmod.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.thesquire.backroomsmod.block.entity.MagneticDistortionSystemControlComputerBlockEntity;
import net.thesquire.backroomsmod.util.ModServerboundPackets;
import reborncore.client.ClientNetworkManager;
import reborncore.client.gui.builder.GuiBase;
import reborncore.client.gui.builder.widget.GuiButtonExtended;
import reborncore.common.screen.BuiltScreenHandler;

import java.awt.*;

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
    protected void drawBackground(MatrixStack matrixStack, float lastFrameDuration, int mouseX, int mouseY) {
        super.drawBackground(matrixStack, lastFrameDuration, mouseX, mouseY);
        final GuiBase.Layer layer = Layer.BACKGROUND;

        drawSlot(matrixStack, 8, 72, layer);

        builder.drawJEIButton(matrixStack, this, 158, 5, layer);
        if (blockEntity.isMultiblockValid()) {
            builder.drawHologramButton(matrixStack, this, 6, 4, mouseX, mouseY, layer);

            if (!children().contains(ACTIVATE_BUTTON)) addDrawableChild(ACTIVATE_BUTTON);
            ACTIVATE_BUTTON.x = x + activeX;
            ACTIVATE_BUTTON.y = y + activeY;
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
        ClientNetworkManager.sendToServer(ModServerboundPackets.createPacketMagneticDistortionSystemSide(side, blockEntity.getPos()));
    }

    private void activate() {
        if (blockEntity.isMultiblockValid() && blockEntity.getEnergy() == blockEntity.getMaxStoredPower()) {
            ClientNetworkManager.sendToServer(ModServerboundPackets.createPacketMagneticDistortionSystemActive(true, blockEntity.getPos()));
        }
    }

}
