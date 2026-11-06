package de.tabcanvas;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Duenne Wand-Platte. FACING = Richtung, in die die Vorderseite zeigt.
 * Die Platte sitzt immer auf der gegenueberliegenden Seite (an der Wand).
 */
public class TabCanvasBlock extends BaseEntityBlock {
	public static final MapCodec<TabCanvasBlock> CODEC = simpleCodec(TabCanvasBlock::new);
	public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

	private static final VoxelShape SHAPE_SOUTH = Block.box(0, 0, 0, 16, 16, 1);
	private static final VoxelShape SHAPE_NORTH = Block.box(0, 0, 15, 16, 16, 16);
	private static final VoxelShape SHAPE_EAST = Block.box(0, 0, 0, 1, 16, 16);
	private static final VoxelShape SHAPE_WEST = Block.box(15, 0, 0, 16, 16, 16);

	public TabCanvasBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction clicked = context.getClickedFace();
		Direction facing = clicked.getAxis().isHorizontal()
				? clicked // an einer Wand: Vorderseite zeigt von der Wand weg
				: context.getHorizontalDirection().getOpposite(); // sonst: zum Spieler
		return defaultBlockState().setValue(FACING, facing);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(FACING)) {
			case SOUTH -> SHAPE_SOUTH;
			case EAST -> SHAPE_EAST;
			case WEST -> SHAPE_WEST;
			default -> SHAPE_NORTH;
		};
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TabCanvasBlockEntity(pos, state);
	}
}
