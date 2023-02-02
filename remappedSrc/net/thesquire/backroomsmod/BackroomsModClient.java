package net.thesquire.backroomsmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.thesquire.backroomsmod.block.entity.ModBlockEntities;
import reborncore.client.multiblock.MultiblockRenderer;

public class BackroomsModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ModBlockEntities.MAGNETIC_DISTORTION_SYSTEM_CONTROL_COMPUTER, MultiblockRenderer::new);
    }

}
