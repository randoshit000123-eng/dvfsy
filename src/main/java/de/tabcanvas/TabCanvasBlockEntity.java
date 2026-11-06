package de.tabcanvas;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Haelt keine Daten - existiert nur, damit der Client-Renderer daran haengen kann. */
public class TabCanvasBlockEntity extends BlockEntity {
	public TabCanvasBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlocks.TAB_CANVAS_BE, pos, state);
	}
}
