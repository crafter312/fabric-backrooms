package net.thesquire.backroomsmod.block;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;

public class ModBlockProperties {

    public static final BooleanProperty HAS_LIGHT = BooleanProperty.of("has_light");
    public static final BooleanProperty FLICKERING = BooleanProperty.of("flickering");
    public static final BooleanProperty DRIPPING = BooleanProperty.of("dripping");
    public static final IntProperty LUMINANCE = IntProperty.of("luminance", 0, 15);

    // Block properties for abandoned supplies loot block
    public static final BooleanProperty HAS_ALMOND_WATER = BooleanProperty.of("has_almond_water");
    public static final BooleanProperty HAS_SPRAY_PAINT = BooleanProperty.of("has_spray_paint");
    public static final BooleanProperty HAS_OAK_LOGS = BooleanProperty.of("has_oak_logs");
    public static final BooleanProperty HAS_STICKS = BooleanProperty.of("has_sticks");
    public static final BooleanProperty HAS_FLINT_AND_STEEL = BooleanProperty.of("has_flint_and_steel");
    public static final BooleanProperty HAS_COAL = BooleanProperty.of("has_coal");

}
