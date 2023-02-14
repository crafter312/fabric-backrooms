package net.thesquire.backroomsmod.block;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;

public class ModBlockProperties {

    public static final BooleanProperty HAS_LIGHT = BooleanProperty.of("has_light");
    public static final BooleanProperty FLICKERING = BooleanProperty.of("flickering");
    public static final BooleanProperty DRIPPING = BooleanProperty.of("dripping");
    public static final BooleanProperty ALT_SIDE = BooleanProperty.of("alt_side");
    //public static final BooleanProperty IS_BLACKOUT_SOURCE = BooleanProperty.of("is_blackout_source");
    public static final IntProperty LUMINANCE = IntProperty.of("luminance", 0, 15);

    // The following block properties seem to wreck all kinds of havoc when loading the game, eventually causing it
    // to crash. This means I'll have to find a different way of implementing the blackout feature, since that
    // hinged on using a BlockState to transmit the information concerning the length and range of the blackout
    // to each listener.
    //public static final IntProperty BLACKOUT_TIME = IntProperty.of("blackout_time", 0, 73200);
    //public static final IntProperty BLACKOUT_RANGE = IntProperty.of("blackout_range", ModConfig.minBlackoutRange, ModConfig.maxBlackoutRange);

}
