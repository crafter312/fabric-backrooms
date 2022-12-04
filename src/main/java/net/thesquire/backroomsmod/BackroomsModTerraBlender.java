package net.thesquire.backroomsmod;

import net.minecraft.util.Identifier;
import net.thesquire.backroomsmod.world.biome.ModRegion;
import terrablender.api.Regions;
import terrablender.api.TerraBlenderApi;

public class BackroomsModTerraBlender implements TerraBlenderApi {

    @Override
    public void onTerraBlenderInitialized() {

        Regions.register(new ModRegion(new Identifier(BackroomsMod.MOD_ID, "backrooms"), 0));

    }

}
