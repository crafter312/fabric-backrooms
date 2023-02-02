package net.thesquire.backroomsmod.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.thesquire.backroomsmod.block.ModBlocks;
import net.thesquire.backroomsmod.config.ModConfig;
import net.thesquire.backroomsmod.recipe.ModRecipes;
import reborncore.client.screen.builder.ScreenHandlerBuilder;
import reborncore.common.recipes.RecipeCrafter;
import reborncore.common.screen.BuiltScreenHandler;
import reborncore.common.screen.BuiltScreenHandlerProvider;
import reborncore.common.util.RebornInventory;
import techreborn.blockentity.machine.GenericMachineBlockEntity;

public class IndustrialAlloySmelterBlockEntity extends GenericMachineBlockEntity implements BuiltScreenHandlerProvider {

    public IndustrialAlloySmelterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INDUSTRIAL_ALLOY_SMELTER, pos, state, "IndustrialAlloySmelter", ModConfig.industrialAlloySmelterMaxInput,
                ModConfig.industrialAlloySmelterMaxEnergy, ModBlocks.INDUSTRIAL_ALLOY_SMELTER, 5);
        final int[] inputs = new int[]{0, 1, 2, 3};
        final int[] outputs = new int[]{4};
        this.inventory = new RebornInventory<>(6, "IndustrialAlloySmelterBlockEntity", 64, this);
        this.crafter = new RecipeCrafter(ModRecipes.INDUSTRIAL_ALLOY_SMELTER, this, 4, 1, this.inventory, inputs, outputs);
    }

    @Override
    public BuiltScreenHandler createScreenHandler(int syncID, PlayerEntity player) {
        return new ScreenHandlerBuilder("industrialalloysmelter").player(player.getInventory()).inventory().hotbar()
                .addInventory().blockEntity(this)
                .slot(0, 51, 72).slot(1, 71, 72).slot(2, 91, 72).slot(3, 111, 72)
                .outputSlot(4, 81, 24).energySlot(5, 8, 72).syncEnergyValue().syncCrafterValue().addInventory()
                .create(this, syncID);
    }

}
