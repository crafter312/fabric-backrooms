package net.thesquire.backroomsmod.item;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.thesquire.backroomsmod.BackroomsMod;

public class ModItemGroup {

    public static final ItemGroup BACKROOMS = FabricItemGroupBuilder.build(new Identifier(BackroomsMod.MOD_ID, "backrooms"),
            () -> new ItemStack(ModItems.SUPERCONDUCTOR_MAGNET_COIL));

}
