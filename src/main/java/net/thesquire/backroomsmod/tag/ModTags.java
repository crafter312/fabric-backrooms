package net.thesquire.backroomsmod.tag;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.event.GameEvent;
import net.thesquire.backroomsmod.BackroomsMod;

public class ModTags {

    public static final TagKey<GameEvent> BLACKOUT_TRIGGERS = of(RegistryKeys.GAME_EVENT, "blackout_triggers");
    public static final TagKey<DimensionType> BLACKOUT_DIMENSIONS = of(RegistryKeys.DIMENSION_TYPE, "blackout_dimensions");

    private static <T> TagKey<T> of(RegistryKey<? extends Registry<T>> registry, String name) {
        return TagKey.of(registry, BackroomsMod.makeId(name));
    }

}
