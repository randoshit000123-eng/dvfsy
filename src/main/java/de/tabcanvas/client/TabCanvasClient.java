package de.tabcanvas.client;

import de.tabcanvas.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class TabCanvasClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRenderers.register(ModBlocks.TAB_CANVAS_BE, TabCanvasRenderer::new);
	}
}
