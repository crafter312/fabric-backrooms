package net.thesquire.backroomsmod.block;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;

public class ModBlockProperties {

    public static final BooleanProperty FLICKERING = BooleanProperty.of("flickering");
    public static final IntProperty LUMINANCE = IntProperty.of("luminance", 0, 15);

}
