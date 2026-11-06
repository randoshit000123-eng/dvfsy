package de.tabcanvas;

import net.fabricmc.api.ModInitializer;

public class TabCanvasMod implements ModInitializer {
	public static final String MOD_ID = "tabcanvas";

	@Override
	public void onInitialize() {
		ModBlocks.register();
	}
}
