package net.thesquire.backroomsmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.impl.client.rendering.ColorProviderRegistryImpl;
import net.kyrptonaught.customportalapi.CustomPortalApiRegistry;
import net.kyrptonaught.customportalapi.CustomPortalsMod;
import net.kyrptonaught.customportalapi.mixin.client.ChunkRendererRegionAccessor;
import net.kyrptonaught.customportalapi.util.CustomPortalHelper;
import net.kyrptonaught.customportalapi.util.PortalLink;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.thesquire.backroomsmod.block.ModBlocks;
import net.thesquire.backroomsmod.block.entity.ModBlockEntities;
import reborncore.client.multiblock.MultiblockRenderer;

public class BackroomsModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ModBlockEntities.MAGNETIC_DISTORTION_SYSTEM_CONTROL_COMPUTER, MultiblockRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BACKROOMS_PORTAL_BLOCK, RenderLayer.getTranslucent());
        ColorProviderRegistryImpl.BLOCK.register((state, world, pos, tintIndex) -> {
            if (pos != null && world instanceof ChunkRendererRegion) {
                Block block = CustomPortalHelper.getPortalBase(((ChunkRendererRegionAccessor) world).getWorld(), pos);
                PortalLink link = CustomPortalApiRegistry.getPortalLinkFromBase(block);
                if (link != null) return link.colorID;
            }
            return 1908001;
        }, ModBlocks.BACKROOMS_PORTAL_BLOCK);
    }

}
