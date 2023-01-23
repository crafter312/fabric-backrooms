package net.thesquire.backroomsmod.block;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;

public class ModBlockProperties {

    public static final BooleanProperty HAS_LIGHT = BooleanProperty.of("has_light");
    public static final BooleanProperty FLICKERING = BooleanProperty.of("flickering");
    public static final BooleanProperty DRIPPING = BooleanProperty.of("dripping");
    public static final IntProperty LUMINANCE = IntProperty.of("luminance", 0, 15);
    public static final IntProperty ALT_SIDES = IntProperty.of("alt_sides", 0, 2);

}
