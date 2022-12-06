package net.thesquire.backroomsmod.client.compat.rei.machine;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import net.minecraft.text.Text;
import reborncore.client.gui.guibuilder.GuiBuilder;
import reborncore.common.crafting.RebornRecipe;
import reborncore.common.crafting.RebornRecipeType;
import techreborn.client.compat.rei.MachineRecipeDisplay;
import techreborn.client.compat.rei.ReiPlugin;
import techreborn.client.compat.rei.machine.AbstractEnergyConsumingMachineCategory;

import java.text.DecimalFormat;
import java.util.List;

public class FourInputsCenterOutputCategory<R extends RebornRecipe> extends AbstractEnergyConsumingMachineCategory<R> {

    public FourInputsCenterOutputCategory(RebornRecipeType<R> rebornRecipeType) {
        super(rebornRecipeType);
    }

    @Override
    public List<Widget> setupDisplay(MachineRecipeDisplay<R> recipeDisplay, Rectangle bounds) {
        List<Widget> widgets = super.setupDisplay(recipeDisplay, bounds);

        // input slots
        widgets.add(Widgets.createSlot(new Point(bounds.x + 29 + 17, bounds.y + 51)).entries(getInput(recipeDisplay, 0)).markInput());
        widgets.add(Widgets.createSlot(new Point(bounds.x + 29 + 37, bounds.y + 51)).entries(getInput(recipeDisplay, 1)).markInput());
        widgets.add(Widgets.createSlot(new Point(bounds.x + 29 + 57, bounds.y + 51)).entries(getInput(recipeDisplay, 2)).markInput());
        widgets.add(Widgets.createSlot(new Point(bounds.x + 29 + 77, bounds.y + 51)).entries(getInput(recipeDisplay, 3)).markInput());

        // output slot
        widgets.add(Widgets.createResultSlotBackground(new Point(bounds.x + 29 + 47, bounds.y + 10)));
        widgets.add(Widgets.createSlot(new Point(bounds.x + 29 + 47, bounds.y + 10)).entries(getOutput(recipeDisplay, 0)).disableBackground().markOutput());

        // progress bar
        widgets.add(ReiPlugin.createProgressBar(bounds.x + 29 + 50, bounds.y + 32, recipeDisplay.getTime() * 50, GuiBuilder.ProgressDirection.UP));

        // recipe time
        widgets.add(Widgets.createLabel(new Point(bounds.getMaxX() - 5, bounds.y + 5), Text.translatable("techreborn.jei.recipe.processing.time.3", new DecimalFormat("###.##").format(recipeDisplay.getTime() / 20.0)))
                .shadow(false)
                .rightAligned()
                .color(0xFF404040, 0xFFBBBBBB)
        );

        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 74;
    }
}
