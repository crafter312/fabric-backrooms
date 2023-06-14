package net.thesquire.backroomsmod.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.thesquire.backroomsmod.BackroomsMod;

public class ModItemGroup {

    public static final ItemGroup BACKROOMS = FabricItemGroup.builder(BackroomsMod.makeId("backrooms"))
            .icon(() -> new ItemStack(ModItems.SUPERCONDUCTOR_MAGNET_COIL)).build();

}
