package net.thesquire.backroomsmod.portal;

import net.kyrptonaught.customportalapi.api.CustomPortalBuilder;
import net.minecraft.util.Identifier;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.block.ModBlocks;
import net.thesquire.backroomsmod.dimension.ModDimensionKeys;

public class ModPortals {

    public static final Identifier KV31_PORTAL_IGNITION_SOURCE = new Identifier(BackroomsMod.MOD_ID, "kv31_portal_ignition_source");

    public static void registerModPortals() {

        CustomPortalBuilder.beginPortal()
                .frameBlock(ModBlocks.TFMC_MAGNET)
                .customIgnitionSource(KV31_PORTAL_IGNITION_SOURCE)
                .destDimID(ModDimensionKeys.LEVEL_0.getValue())
                .tintColor(150, 142, 87)
                .onlyLightInOverworld()
                .registerPortal();
    }

}
