package net.thesquire.backroomsmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.thesquire.backroomsmod.block.ModBlockEntities;
import net.thesquire.backroomsmod.block.ModBlocks;
import net.thesquire.backroomsmod.client.render.block.entity.ElevatorDoorBlockEntityRenderer;
import net.thesquire.backroomsmod.client.render.entity.model.ModEntityModelLayers;
import net.thesquire.backroomsmod.item.ModItemColorProviders;
import net.thesquire.backroomsmod.screen.ModClientGuis;
import net.thesquire.backroomsmod.util.mixin.MixinCallbacks;
import reborncore.client.multiblock.MultiblockRenderer;

public class BackroomsModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MixinCallbacks.registerClientCallbacks();
        ModClientGuis.registerClientGuis();
        ModItemColorProviders.registerModItemColorProviders();
        ModEntityModelLayers.registerModEntityModelLayers();

        EntityModelLayerRegistry.registerModelLayer(ModEntityModelLayers.ELEVATOR_DOOR_BOTTOM_LEFT, ElevatorDoorBlockEntityRenderer::getBottomLeftTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModEntityModelLayers.ELEVATOR_DOOR_BOTTOM_RIGHT, ElevatorDoorBlockEntityRenderer::getBottomRightTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModEntityModelLayers.ELEVATOR_DOOR_TOP_LEFT, ElevatorDoorBlockEntityRenderer::getTopLeftTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModEntityModelLayers.ELEVATOR_DOOR_TOP_RIGHT, ElevatorDoorBlockEntityRenderer::getTopRightTexturedModelData);

        BlockEntityRendererFactories.register(ModBlockEntities.MAGNETIC_DISTORTION_SYSTEM_CONTROL_COMPUTER, MultiblockRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.ELEVATOR_DOOR, ElevatorDoorBlockEntityRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                ModBlocks.MOUNTABLE_FLUORESCENT_LIGHT,
                ModBlocks.CUSTOM_DOOR,
                ModBlocks.PIPE_BLOCK,
                ModBlocks.ELEVATOR_BUTTON,
                ModBlocks.ELEVATOR_DOOR);

        BackroomsMod.LOGGER.info("Client initialization finished for " + BackroomsMod.MOD_ID);
    }

}
