package net.thesquire.backroomsmod.portal;

import net.kyrptonaught.customportalapi.CustomPortalApiRegistry;
import net.kyrptonaught.customportalapi.api.CustomPortalBuilder;
import net.minecraft.util.Identifier;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.block.ModBlocks;
import net.thesquire.backroomsmod.block.custom.BackroomsPortalBlock;
import net.thesquire.backroomsmod.dimension.ModDimensionKeys;
import net.thesquire.backroomsmod.portal.destination.Level0PortalDestination;
import net.thesquire.backroomsmod.portal.frame.Level0PortalAreaHelper;

public class ModPortals {

    public static final Identifier KV31_PORTAL_IGNITION_SOURCE = new Identifier(BackroomsMod.MOD_ID, "kv31_portal_ignition_source");
    public static final Identifier LEVEL_0_PORTAL_FRAME_TESTER = new Identifier(BackroomsMod.MOD_ID, "level_0_portal_frame_tester");

    public static void registerModPortals() {
        CustomPortalApiRegistry.registerPortalFrameTester(LEVEL_0_PORTAL_FRAME_TESTER, Level0PortalAreaHelper::new);

        CustomPortalBuilder.beginPortal()
                .frameBlock(Level0PortalDestination.portalBase)
                .customPortalBlock((BackroomsPortalBlock) ModBlocks.BACKROOMS_PORTAL_BLOCK)
                .customFrameTester(LEVEL_0_PORTAL_FRAME_TESTER)
                .customIgnitionSource(KV31_PORTAL_IGNITION_SOURCE)
                .destDimID(ModDimensionKeys.LEVEL_0.getValue())
                .tintColor(150, 142, 87)
                .onlyLightInOverworld()
                .registerPortal();
    }

}
