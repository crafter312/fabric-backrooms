package net.thesquire.backroomsmod.world.biome;

import net.minecraft.util.Identifier;
import terrablender.api.Region;
import terrablender.api.RegionType;

public class ModRegion extends Region {

    public ModRegion(Identifier name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

}
