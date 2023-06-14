package net.thesquire.backroomsmod.recipe;

import net.thesquire.backroomsmod.BackroomsMod;
import reborncore.common.crafting.RebornRecipe;
import reborncore.common.crafting.RebornRecipeType;
import reborncore.common.crafting.RecipeManager;

public class ModRecipes {

    public static RebornRecipeType<RebornRecipe> INDUSTRIAL_ALLOY_SMELTER;

    public static void registerRecipes() {
        BackroomsMod.LOGGER.info("Registering recipe types for " + BackroomsMod.MOD_ID);

        INDUSTRIAL_ALLOY_SMELTER = RecipeManager.newRecipeType(BackroomsMod.makeId("industrial_alloy_smelter"));
    }

}
