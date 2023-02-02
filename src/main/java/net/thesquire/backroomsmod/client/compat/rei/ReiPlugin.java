package net.thesquire.backroomsmod.client.compat.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.block.ModBlocks;
import net.thesquire.backroomsmod.client.compat.rei.machine.FourInputsCenterOutputCategory;
import net.thesquire.backroomsmod.recipe.ModRecipes;
import reborncore.api.blockentity.IUpgradeable;
import reborncore.client.gui.builder.GuiBase;
import reborncore.client.gui.builder.slot.GuiTab;
import reborncore.common.crafting.RebornRecipe;
import reborncore.common.crafting.RebornRecipeType;
import reborncore.common.crafting.RecipeManager;
import techreborn.client.compat.rei.MachineRecipeDisplay;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ReiPlugin implements REIClientPlugin {

    public static final Map<RebornRecipeType<?>, ItemConvertible> iconMap = new HashMap<>();

    public ReiPlugin() {
        iconMap.put(ModRecipes.INDUSTRIAL_ALLOY_SMELTER, ModBlocks.INDUSTRIAL_ALLOY_SMELTER);
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new FourInputsCenterOutputCategory<>(ModRecipes.INDUSTRIAL_ALLOY_SMELTER));

        addWorkstations(ModRecipes.INDUSTRIAL_ALLOY_SMELTER.name(), EntryStacks.of(ModBlocks.INDUSTRIAL_ALLOY_SMELTER));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        RecipeManager.getRecipeTypes(BackroomsMod.MOD_ID).forEach(rebornRecipeType -> registerMachineRecipe(registry, rebornRecipeType));
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        ExclusionZones exclusionZones = registry.exclusionZones();
        exclusionZones.register(GuiBase.class, guiBase -> {
            int height = 0;
            if (guiBase.tryAddUpgrades() && guiBase.be instanceof IUpgradeable upgradeable) {
                if (upgradeable.canBeUpgraded()) {
                    height = 80;
                }
            }
            for (GuiTab slot : (List<GuiTab>) guiBase.getTabs()) {
                if (slot.enabled()) {
                    height += 24;
                }
            }
            if (height > 0) {
                int width = 20;
                return Collections.singletonList(new Rectangle(guiBase.getGuiLeft() - width, guiBase.getGuiTop() + 8, width, height));
            }
            return Collections.emptyList();
        });
    }

    private void addWorkstations(Identifier identifier, EntryStack<?>... stacks) {
        CategoryRegistry.getInstance().addWorkstations(CategoryIdentifier.of(identifier), stacks);
    }

    private <R extends RebornRecipe> void registerMachineRecipe(DisplayRegistry registry, RebornRecipeType<R> recipeType) {
        Function<R, Display> recipeDisplay = r -> new MachineRecipeDisplay<>((RebornRecipe) r);
        registry.registerFiller(RebornRecipe.class, recipe -> {
            if (recipe != null) {
                return recipe.getRebornRecipeType() == recipeType;
            }
            return false;
        }, recipeDisplay);
    }

}
