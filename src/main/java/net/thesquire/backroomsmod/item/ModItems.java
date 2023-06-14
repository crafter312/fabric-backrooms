package net.thesquire.backroomsmod.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.item.custom.Drink;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModItems {

    public static Item BISMUTH_INGOT;
    public static Item RAW_BISMUTHINITE;
    public static Item BISMUTHINITE_DUST;
    public static Item BISMUTH_DUST;
    public static Item CRUSHED_LEAD;
    public static Item BISMUTH_SMALL_DUST;
    public static Item IRON_DUST;
    public static Item INDIUM_DUST;
    public static Item INDIUM_SMALL_DUST;
    public static Item INDIUM_INGOT;
    public static Item SUPERCONDUCTOR_MAGNET_COIL;
    public static Item SUPERCONDUCTOR_MAGNET;

    // food items
    public static Item ALMOND_WATER;

    // items with tooltips
    public static Item LOW_TEMP_SOLDER_INGOT;
    public static Item LN2_COOLANT_CELL;

    public static void registerModItems() {
        BackroomsMod.LOGGER.info("Registering mod items for " + BackroomsMod.MOD_ID);

        BISMUTH_INGOT = registerSimpleItem("bismuth_ingot");
        RAW_BISMUTHINITE = registerSimpleItem("raw_bismuthinite");
        BISMUTHINITE_DUST = registerSimpleItem("bismuthinite_dust");
        BISMUTH_DUST = registerSimpleItem("bismuth_dust");
        CRUSHED_LEAD = registerSimpleItem("crushed_lead");
        BISMUTH_SMALL_DUST = registerSimpleItem("bismuth_small_dust");
        IRON_DUST = registerSimpleItem("iron_dust");
        INDIUM_DUST = registerSimpleItem("indium_dust");
        INDIUM_SMALL_DUST = registerSimpleItem("indium_small_dust");
        INDIUM_INGOT = registerSimpleItem("indium_ingot");
        SUPERCONDUCTOR_MAGNET_COIL = registerSimpleItem("superconductor_magnet_coil");
        SUPERCONDUCTOR_MAGNET = registerSimpleItem("superconductor_magnet");

        // food items
        ALMOND_WATER = registerItem("almond_water", new Drink(new FabricItemSettings().food(ModFoodComponents.ALMOND_WATER)
                .recipeRemainder(Items.GLASS_BOTTLE)));

        // items with tooltips
        LOW_TEMP_SOLDER_INGOT = registerSimpleItemWithTooltip("low_temp_solder_ingot",
                "item.backroomsmod.low_temp_solder_ingot.tooltip_1", "item.backroomsmod.low_temp_solder_ingot.tooltip_2");
        LN2_COOLANT_CELL = registerSimpleItemWithTooltip("ln2_coolant_cell",
                "item.backroomsmod.ln2_coolant_cell.tooltip");
    }

    private static Item registerItem(String name, Item item) {
        ItemGroupEvents.modifyEntriesEvent(ModItemGroup.BACKROOMS).register(entries -> entries.add(item));
        return Registry.register(Registries.ITEM, BackroomsMod.makeId(name), item);
    }

    private static Item registerSimpleItem(String name){
        return registerItem(name, new Item(new FabricItemSettings()));
    }

    private static Item registerSimpleItemWithTooltip(String name, String... tooltipKeys) {
        return registerItem(name,
                new Item(new FabricItemSettings()) {
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

}
