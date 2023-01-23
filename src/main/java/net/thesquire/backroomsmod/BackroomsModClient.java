package net.thesquire.backroomsmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.thesquire.backroomsmod.block.ModBlocks;
import net.thesquire.backroomsmod.block.ModBlockEntities;
import reborncore.client.multiblock.MultiblockRenderer;

public class BackroomsModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ModBlockEntities.MAGNETIC_DISTORTION_SYSTEM_CONTROL_COMPUTER, MultiblockRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MOUNTABLE_FLUORESCENT_LIGHT, RenderLayer.getCutout());
    }

}
