package net.thesquire.backroomsmod.portal;

import net.kyrptonaught.customportalapi.api.CustomPortalBuilder;
import net.minecraft.util.Identifier;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.dimension.ModDimensionKeys;
import net.thesquire.backroomsmod.portal.destination.Level0PortalDestination;

public class ModPortals {

    public static final Identifier KV31_PORTAL_IGNITION_SOURCE = new Identifier(BackroomsMod.MOD_ID, "kv31_portal_ignition_source");
    //public static final Identifier LEVEL_0_FRAME_TESTER = new Identifier(BackroomsMod.MOD_ID, "level_0_frame_tester");

    public static void registerModPortals() {
        //CustomPortalApiRegistry.registerPortalFrameTester(LEVEL_0_FRAME_TESTER, Level0PortalAreaHelper::new);

        CustomPortalBuilder.beginPortal()
                .frameBlock(Level0PortalDestination.portalBase)
                .customIgnitionSource(KV31_PORTAL_IGNITION_SOURCE)
                //.customFrameTester(LEVEL_0_FRAME_TESTER)
                .destDimID(ModDimensionKeys.LEVEL_0.getValue())
                .tintColor(150, 142, 87)
                .onlyLightInOverworld()
                .registerPortal();
    }

}
