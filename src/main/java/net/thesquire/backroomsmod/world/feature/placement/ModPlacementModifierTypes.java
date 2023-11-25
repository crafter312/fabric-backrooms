package net.thesquire.backroomsmod.world.feature.placement;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;
import net.thesquire.backroomsmod.BackroomsMod;

public class ModPlacementModifierTypes {

    public static PlacementModifierType<WallAdjacentPlacementModifier> WALL_ADJACENT;
    public static PlacementModifierType<NoiseThresholdPlacementModifier> NOISE_THRESHOLD;

    public static void registerPlacementModifierTypes() {
        BackroomsMod.LOGGER.info("Registering placement modifier types for " + BackroomsMod.MOD_ID);

        WALL_ADJACENT = register("wall_adjacent", WallAdjacentPlacementModifier.MODIFIER_CODEC);
        NOISE_THRESHOLD= register("noise_threshold", NoiseThresholdPlacementModifier.MODIFIER_CODEC);
    }

    private static <P extends PlacementModifier> PlacementModifierType<P> register(String id, Codec<P> codec) {
        return Registry.register(Registries.PLACEMENT_MODIFIER_TYPE, id, () -> codec);
    }

}
