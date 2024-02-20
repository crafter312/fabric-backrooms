package net.thesquire.backroomsmod.client.render.entity.model;

import com.google.common.collect.Sets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.thesquire.backroomsmod.BackroomsMod;

import java.util.Set;

@Environment(value= EnvType.CLIENT)

public class ModEntityModelLayers {

    private static final String MAIN = "main";
    private static final Set<EntityModelLayer> LAYERS = Sets.newHashSet();

    public static EntityModelLayer ELEVATOR_DOOR_BOTTOM_LEFT;
    public static EntityModelLayer ELEVATOR_DOOR_BOTTOM_RIGHT;
    public static EntityModelLayer ELEVATOR_DOOR_TOP_LEFT;
    public static EntityModelLayer ELEVATOR_DOOR_TOP_RIGHT;

    public static void registerModEntityModelLayers() {
        BackroomsMod.LOGGER.info("Registering entity model layers for " + BackroomsMod.MOD_ID);

        ELEVATOR_DOOR_BOTTOM_LEFT = ModEntityModelLayers.registerMain("elevator_door_bottom_left");
        ELEVATOR_DOOR_BOTTOM_RIGHT = ModEntityModelLayers.registerMain("elevator_door_bottom_right");
        ELEVATOR_DOOR_TOP_LEFT = ModEntityModelLayers.registerMain("elevator_door_top_left");
        ELEVATOR_DOOR_TOP_RIGHT = ModEntityModelLayers.registerMain("elevator_door_top_right");
    }

    private static EntityModelLayer registerMain(String id) {
        return ModEntityModelLayers.register(id, MAIN);
    }

    private static EntityModelLayer register(String id, String layer) {
        EntityModelLayer entityModelLayer = new EntityModelLayer(BackroomsMod.makeId(id), layer);
        if (!LAYERS.add(entityModelLayer)) {
            throw new IllegalStateException("Duplicate registration for " + entityModelLayer);
        }
        return entityModelLayer;
    }

}
