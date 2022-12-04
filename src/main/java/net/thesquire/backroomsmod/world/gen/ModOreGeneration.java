package net.thesquire.backroomsmod.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.tag.BiomeTags;
import net.minecraft.world.gen.GenerationStep;
import net.thesquire.backroomsmod.world.feature.ModPlacedFeatures;

public class ModOreGeneration {

    public static void generateOres() {
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_MOUNTAIN),
                GenerationStep.Feature.UNDERGROUND_ORES, ModPlacedFeatures.BISMUTHINITE_ORE_PLACED.getKey().get());
    }

}
