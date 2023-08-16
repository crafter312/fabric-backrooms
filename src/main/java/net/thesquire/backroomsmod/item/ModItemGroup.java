package net.thesquire.backroomsmod.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.thesquire.backroomsmod.BackroomsMod;

public class ModItemGroup {

    public static RegistryKey<ItemGroup> BACKROOMS_ITEM_GROUP;

    public static ItemGroup BACKROOMS;

    public static void registerModItemGroups() {
        BackroomsMod.LOGGER.info("Registering mod item groups for " + BackroomsMod.MOD_ID);

        BACKROOMS_ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, BackroomsMod.makeId("backrooms"));

        BACKROOMS = Registry.register(Registries.ITEM_GROUP, BACKROOMS_ITEM_GROUP,
                FabricItemGroup.builder()
                        .displayName(Text.translatable("itemGroup.backroomsmod.backrooms"))
                        .icon(() -> new ItemStack(ModItems.SUPERCONDUCTOR_MAGNET_COIL))
                        .entries((context, entries) -> {})
                        .build());
    }

}
