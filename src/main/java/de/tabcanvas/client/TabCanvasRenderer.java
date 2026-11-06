package de.tabcanvas.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import de.tabcanvas.TabCanvasBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.PlayerInfo;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import de.tabcanvas.TabCanvasBlockEntity;

/**
 * Jede Kachel zeigt genau einen Spieler aus der Tab-Liste (alphabetisch sortiert).
 * Welcher, haengt von der Position in der 3x3-Gruppe ab:
 * Index = Reihe * 3 + Spalte (oben links = 0, unten rechts = 8).
 */
public class TabCanvasRenderer implements BlockEntityRenderer<TabCanvasBlockEntity, TabCanvasRenderState> {
	/** z der Vorderseite relativ zur Blockmitte (Platte ist 1/16 dick, sitzt hinten). */
	private static final float FRONT_Z = -0.4375f + 0.002f;

	public TabCanvasRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public TabCanvasRenderState createRenderState() {
		return new TabCanvasRenderState();
	}

	@Override
	public void extractRenderState(TabCanvasBlockEntity be, TabCanvasRenderState state, float partialTick,
			Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
		BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPos, crumbling);

		state.name = null;
		state.skinType = null;

		Level level = be.getLevel();
		BlockState bs = be.getBlockState();
		if (level == null || !bs.hasProperty(TabCanvasBlock.FACING)) {
			return;
		}
		Direction facing = bs.getValue(TabCanvasBlock.FACING);
		state.facing = facing;

		ClientPacketListener connection = Minecraft.getInstance().getConnection();
		if (connection == null) {
			return;
		}

		int index = tileIndex(level, be.getBlockPos(), facing);

		List<PlayerInfo> players = new ArrayList<>(connection.getOnlinePlayers());
		players.sort(Comparator.comparing(p -> p.getProfile().name(), String.CASE_INSENSITIVE_ORDER));
		if (index >= players.size()) {
			return;
		}

		PlayerInfo info = players.get(index);
		state.name = info.getProfile().name();
		state.nameWidth = Minecraft.getInstance().font.width(state.name);
		state.skinType = PlayerSkinRenderCache.playerSkinRenderType(info.getSkin());
	}

	/** Position in der zusammenhaengenden Wand aus Canvas-Bloecken, modulo 3 -> 3x3-Raster. */
	private static int tileIndex(Level level, BlockPos pos, Direction facing) {
		Direction left = facing.getClockWise(); // von vorne betrachtet links
		int col = countSame(level, pos, facing, left) % 3;
		int row = countSame(level, pos, facing, Direction.UP) % 3;
		return row * 3 + col;
	}

	private static int countSame(Level level, BlockPos pos, Direction facing, Direction dir) {
		int n = 0;
		BlockPos.MutableBlockPos p = pos.mutable();
		while (n < 64) {
			p.move(dir);
			BlockState s = level.getBlockState(p);
			if (s.getBlock() instanceof TabCanvasBlock && s.getValue(TabCanvasBlock.FACING) == facing) {
				n++;
			} else {
				break;
			}
		}
		return n;
	}

	@Override
	public void submit(TabCanvasRenderState state, PoseStack poseStack, SubmitNodeCollector collector,
			CameraRenderState cameraState) {
		if (state.name == null || state.skinType == null) {
			return;
		}

		// Kopf (Gesicht + Hut-Ebene)
		poseStack.pushPose();
		poseStack.translate(0.5, 0.5, 0.5);
		poseStack.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
		poseStack.translate(0.0, 0.0, FRONT_Z);
		collector.submitCustomGeometry(poseStack, state.skinType, (pose, vc) -> {
			// Gesicht: Skin-Pixel (8,8)-(16,16), Hut: (40,8)-(48,16)
			quad(pose, vc, -0.23f, -0.02f, 0.23f, 0.44f, 0f, 8f, 8f, 16f, 16f);
			quad(pose, vc, -0.25f, -0.04f, 0.25f, 0.46f, 0.002f, 40f, 8f, 48f, 16f);
		});
		poseStack.popPose();

		// Name
		poseStack.pushPose();
		poseStack.translate(0.5, 0.5, 0.5);
		poseStack.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
		poseStack.translate(0.0, -0.17, FRONT_Z + 0.004);
		float s = Math.min(0.012f, 0.9f / Math.max(1, state.nameWidth));
		poseStack.scale(s, -s, s);
		collector.submitText(poseStack, -state.nameWidth / 2f, 0f,
				Component.literal(state.name).getVisualOrderText(), false, Font.DisplayMode.NORMAL,
				LightTexture.FULL_BRIGHT, 0xFFFFFFFF, 0, 0);
		poseStack.popPose();
	}

	/** Zeichnet ein Rechteck in der XY-Ebene (Vorderseite zeigt nach +z). Pixel-UVs auf 64x64-Skin. */
	private static void quad(PoseStack.Pose pose, VertexConsumer vc, float x0, float y0, float x1, float y1, float z,
			float pu0, float pv0, float pu1, float pv1) {
		float u0 = pu0 / 64f, v0 = pv0 / 64f, u1 = pu1 / 64f, v1 = pv1 / 64f;
		vertex(pose, vc, x0, y0, z, u0, v1);
		vertex(pose, vc, x1, y0, z, u1, v1);
		vertex(pose, vc, x1, y1, z, u1, v0);
		vertex(pose, vc, x0, y1, z, u0, v0);
	}

	private static void vertex(PoseStack.Pose pose, VertexConsumer vc, float x, float y, float z, float u, float v) {
		vc.addVertex(pose, x, y, z)
				.setColor(-1)
				.setUv(u, v)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setLight(LightTexture.FULL_BRIGHT)
				.setNormal(pose, 0f, 0f, 1f);
	}
}
