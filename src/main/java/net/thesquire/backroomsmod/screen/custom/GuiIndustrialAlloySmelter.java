package net.thesquire.backroomsmod.screen.custom;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.thesquire.backroomsmod.block.entity.IndustrialAlloySmelterBlockEntity;
import reborncore.client.gui.GuiBase;
import reborncore.client.gui.GuiBuilder;
import reborncore.common.screen.BuiltScreenHandler;

public class GuiIndustrialAlloySmelter extends GuiBase<BuiltScreenHandler> {

    IndustrialAlloySmelterBlockEntity blockEntity;

    public GuiIndustrialAlloySmelter(int syncID, final PlayerEntity player, final IndustrialAlloySmelterBlockEntity blockEntity) {
        super(player, blockEntity, blockEntity.createScreenHandler(syncID, player));
        this.blockEntity = blockEntity;
    }

    @Override
    protected void drawBackground(DrawContext drawContext, final float f, final int mouseX, final int mouseY) {
        super.drawBackground(drawContext, f, mouseX, mouseY);
        final GuiBase.Layer layer = GuiBase.Layer.BACKGROUND;

        //Battery slot
        drawSlot(drawContext, 8, 72, layer);

        //Input slots
        drawSlot(drawContext, 51, 72, layer);
        drawSlot(drawContext, 71, 72, layer);
        drawSlot(drawContext, 91, 72, layer);
        drawSlot(drawContext, 111, 72, layer);

        //Output slots
        drawOutputSlot(drawContext, 81, 24, layer);
    }

    @Override
    protected void drawForeground(DrawContext drawContext, final int mouseX, final int mouseY) {
        super.drawForeground(drawContext, mouseX, mouseY);
        final GuiBase.Layer layer = GuiBase.Layer.FOREGROUND;

        builder.drawProgressBar(drawContext, this, blockEntity.getProgressScaled(100), 100, 84, 50, mouseX, mouseY, GuiBuilder.ProgressDirection.UP, layer);
        builder.drawMultiEnergyBar(drawContext, this, 9, 19, (int) blockEntity.getEnergy(), (int) blockEntity.getMaxStoredPower(), mouseX, mouseY, 0, layer);
    }

}
