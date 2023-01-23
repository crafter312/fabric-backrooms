package net.thesquire.backroomsmod.screen.custom;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.thesquire.backroomsmod.block.entity.IndustrialAlloySmelterBlockEntity;
import reborncore.client.gui.builder.GuiBase;
import reborncore.client.gui.guibuilder.GuiBuilder;
import reborncore.common.screen.BuiltScreenHandler;

public class GuiIndustrialAlloySmelter extends GuiBase<BuiltScreenHandler> {

    IndustrialAlloySmelterBlockEntity blockEntity;

    public GuiIndustrialAlloySmelter(int syncID, final PlayerEntity player, final IndustrialAlloySmelterBlockEntity blockEntity) {
        super(player, blockEntity, blockEntity.createScreenHandler(syncID, player));
        this.blockEntity = blockEntity;
    }

    @Override
    protected void drawBackground(MatrixStack matrixStack, final float f, final int mouseX, final int mouseY) {
        super.drawBackground(matrixStack, f, mouseX, mouseY);
        final GuiBase.Layer layer = GuiBase.Layer.BACKGROUND;

        //Battery slot
        drawSlot(matrixStack, 8, 72, layer);

        //Input slots
        drawSlot(matrixStack, 51, 72, layer);
        drawSlot(matrixStack, 71, 72, layer);
        drawSlot(matrixStack, 91, 72, layer);
        drawSlot(matrixStack, 111, 72, layer);

        //Output slots
        drawOutputSlot(matrixStack, 81, 24, layer);
        builder.drawJEIButton(matrixStack, this, 158, 5, layer);
    }

    @Override
    protected void drawForeground(MatrixStack matrixStack, final int mouseX, final int mouseY) {
        super.drawForeground(matrixStack, mouseX, mouseY);
        final GuiBase.Layer layer = GuiBase.Layer.FOREGROUND;

        builder.drawProgressBar(matrixStack, this, blockEntity.getProgressScaled(100), 100, 84, 50, mouseX, mouseY, GuiBuilder.ProgressDirection.UP, layer);
        builder.drawMultiEnergyBar(matrixStack, this, 9, 19, (int) blockEntity.getEnergy(), (int) blockEntity.getMaxStoredPower(), mouseX, mouseY, 0, layer);
    }

}
