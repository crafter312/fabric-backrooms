package net.thesquire.backroomsmod.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.BackroomsMod;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModItems {

    public static final Item BISMUTH_INGOT = registerSimpleItem("bismuth_ingot");
    public static final Item RAW_BISMUTHINITE = registerSimpleItem("raw_bismuthinite");
    public static final Item BISMUTHINITE_DUST = registerSimpleItem("bismuthinite_dust");
    public static final Item BISMUTH_DUST = registerSimpleItem("bismuth_dust");
    public static final Item CRUSHED_LEAD = registerSimpleItem("crushed_lead");
    public static final Item BISMUTH_SMALL_DUST = registerSimpleItem("bismuth_small_dust");
    public static final Item IRON_DUST = registerSimpleItem("iron_dust");
    public static final Item INDIUM_DUST = registerSimpleItem("indium_dust");
    public static final Item INDIUM_SMALL_DUST = registerSimpleItem("indium_small_dust");
    public static final Item INDIUM_INGOT = registerSimpleItem("indium_ingot");
    public static final Item SUPERCONDUCTOR_MAGNET_COIL = registerSimpleItem("superconductor_magnet_coil");
    public static final Item SUPERCONDUCTOR_MAGNET = registerSimpleItem("superconductor_magnet");

    public static final Item LOW_TEMP_SOLDER_INGOT = registerSimpleItemWithTooltip("low_temp_solder_ingot",
            "item.backroomsmod.low_temp_solder_ingot.tooltip_1", "item.backroomsmod.low_temp_solder_ingot.tooltip_2");
    public static final Item LN2_COOLANT_CELL = registerSimpleItemWithTooltip("ln2_coolant_cell",
            "item.backroomsmod.ln2_coolant_cell.tooltip");

    /*****************************************************************************************/

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(BackroomsMod.MOD_ID, name), item);
    }

    private static Item registerSimpleItem(String name){
        return registerItem(name, new Item(new FabricItemSettings().group(ModItemGroup.BACKROOMS)));
    }

    private static Item registerSimpleItemWithTooltip(String name, String... tooltipKeys) {
        return registerItem(name,
                new Item(new FabricItemSettings().group(ModItemGroup.BACKROOMS)) {
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

    public static void registerModItems() {
        BackroomsMod.LOGGER.info("Registering mod items for " + BackroomsMod.MOD_ID);
    }

}
