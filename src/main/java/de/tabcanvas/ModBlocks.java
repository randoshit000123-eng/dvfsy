package de.tabcanvas;

import java.util.Set;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class ModBlocks {
	public static final Identifier ID = Identifier.fromNamespaceAndPath(TabCanvasMod.MOD_ID, "tab_canvas");

	public static Block TAB_CANVAS;
	public static BlockItem TAB_CANVAS_ITEM;
	public static BlockEntityType<TabCanvasBlockEntity> TAB_CANVAS_BE;

	private ModBlocks() {
	}

	public static void register() {
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, ID);
		TAB_CANVAS = Registry.register(BuiltInRegistries.BLOCK, blockKey,
				new TabCanvasBlock(BlockBehaviour.Properties.of()
						.setId(blockKey)
						.strength(1.0f)
						.sound(SoundType.METAL)
						.noOcclusion()));

		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, ID);
		TAB_CANVAS_ITEM = Registry.register(BuiltInRegistries.ITEM, itemKey,
				new BlockItem(TAB_CANVAS, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));

		TAB_CANVAS_BE = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ID,
				new BlockEntityType<>(TabCanvasBlockEntity::new, Set.of(TAB_CANVAS)));

		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
				.register(entries -> entries.accept(TAB_CANVAS_ITEM));
	}
}
