package net.thesquire.backroomsmod.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.block.custom.CeilingTileBlock;
import net.thesquire.backroomsmod.block.custom.FluorescentLightBlock;
import net.thesquire.backroomsmod.block.custom.TFMCMagnetBlock;
import net.thesquire.backroomsmod.block.entity.IndustrialAlloySmelterBlockEntity;
import net.thesquire.backroomsmod.block.entity.MagneticDistortionSystemControlComputerBlockEntity;
import net.thesquire.backroomsmod.item.ModItemGroup;
import net.thesquire.backroomsmod.screen.ModGuis;
import org.jetbrains.annotations.Nullable;
import techreborn.blocks.GenericMachineBlock;

import java.util.List;

public class ModBlocks {

    public static final Block BISMUTHINITE_ORE = registerBlock("bismuthinite_ore",
            new Block(FabricBlockSettings.of(Material.STONE).strength(4.5f).requiresTool()));

    public static final Block CEILING_TILE = registerBlock("ceiling_tile",
            new CeilingTileBlock(FabricBlockSettings.of(Material.STONE).strength(4f).requiresTool()));

    public static final Block YELLOW_WALLPAPER = registerBlock("yellow_wallpaper",
            new Block(FabricBlockSettings.of(Material.STONE).strength(4f).requiresTool()));

    public static final Block TFMC_MAGNET = registerBlock("tfmc_magnet", new TFMCMagnetBlock(),
            "block.backroomsmod.tfmc_magnet.tooltip_1", "block.backroomsmod.tfmc_magnet.tooltip_2");

    // Blocks with a GUI or BlockEntity have to be registered in the method below to ensure proper register order!
    public static Block INDUSTRIAL_ALLOY_SMELTER;
    public static Block MAGNETIC_DISTORTION_SYSTEM_CONTROL_COMPUTER;
    public static Block FLUORESCENT_LIGHT;

    /**************************************************************************************************/

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registry.BLOCK, new Identifier(BackroomsMod.MOD_ID, name), block);
    }

    private static Block registerBlock(String name, Block block, String... tooltipKeys) {
        registerBlockItem(name, block, tooltipKeys);
        return Registry.register(Registry.BLOCK, new Identifier(BackroomsMod.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registry.ITEM, new Identifier(BackroomsMod.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings().group(ModItemGroup.BACKROOMS)));
    }

    private static void registerBlockItem(String name, Block block, String... tooltipKeys) {
        Registry.register(Registry.ITEM, new Identifier(BackroomsMod.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings().group(ModItemGroup.BACKROOMS)) {
                    @Override
                    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
                        if(Screen.hasShiftDown()) {
                            for(String tooltipKey : tooltipKeys) {
                                tooltip.add(Text.translatable(tooltipKey));
                            }
                        } else {
                            tooltip.add(Text.translatable("tooltip.backroomsmod.generic"));
                        }
                    }
                });
    }

    private static Block registerBlockWithoutBlockItem(String name, Block block) {
        return Registry.register(Registry.BLOCK, new Identifier(BackroomsMod.MOD_ID, name), block);
    }

    public static void registerModBlocks() {
        BackroomsMod.LOGGER.info("Registering mod blocks for " + BackroomsMod.MOD_ID);

        INDUSTRIAL_ALLOY_SMELTER = registerBlock("industrial_alloy_smelter",
                new GenericMachineBlock(ModGuis.INDUSTRIAL_ALLOY_SMELTER, IndustrialAlloySmelterBlockEntity::new));

        MAGNETIC_DISTORTION_SYSTEM_CONTROL_COMPUTER = registerBlock("magnetic_distortion_system_control_computer",
                new GenericMachineBlock(ModGuis.MAGNETIC_DISTORTION_SYSTEM_CONTROL_COMPUTER, MagneticDistortionSystemControlComputerBlockEntity::new));

        FLUORESCENT_LIGHT = registerBlock("fluorescent_light",
                new FluorescentLightBlock(FabricBlockSettings.of(Material.GLASS)
                        .strength(0.3f)
                        .luminance(FluorescentLightBlock::getLuminance)));
    }

}
