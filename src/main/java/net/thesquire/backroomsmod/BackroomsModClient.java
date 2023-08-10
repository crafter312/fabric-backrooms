package net.thesquire.backroomsmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.thesquire.backroomsmod.block.ModBlockEntities;
import net.thesquire.backroomsmod.block.ModBlocks;
import net.thesquire.backroomsmod.item.ModItemColorProviders;
import net.thesquire.backroomsmod.screen.ModClientGuis;
import net.thesquire.backroomsmod.util.mixin.MixinCallbacks;
import reborncore.client.multiblock.MultiblockRenderer;

public class BackroomsModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(ModBlockEntities.MAGNETIC_DISTORTION_SYSTEM_CONTROL_COMPUTER, MultiblockRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                ModBlocks.MOUNTABLE_FLUORESCENT_LIGHT,
                ModBlocks.IRON_DOOR,
                ModBlocks.PIPE_BLOCK,
                ModBlocks.ELEVATOR_BUTTON);

        MixinCallbacks.registerClientCallbacks();
        ModClientGuis.registerClientGuis();
        ModItemColorProviders.registerModItemColorProviders();

        BackroomsMod.LOGGER.info("Client initialization finished for " + BackroomsMod.MOD_ID);
    }

}
