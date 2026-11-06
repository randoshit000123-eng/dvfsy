package de.tabcanvas.client;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;

public class TabCanvasRenderState extends BlockEntityRenderState {
	public Direction facing = Direction.NORTH;
	/** null = kein Spieler fuer diese Kachel */
	public String name = null;
	public int nameWidth = 0;
	public RenderType skinType = null;
}
