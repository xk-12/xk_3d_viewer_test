package com.lastcentury;

import com.lastcentury.Xk_HUD_3DViewer.Cube;
import com.lastcentury.Xk_HUD_3DViewer.RenderMode;
import com.lastcentury.Xk_HUD_3DViewer.Scene;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import static com.lastcentury.LastCentury.ViewMode;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL13C.GL_MULTISAMPLE;

public class LastCenturyClient implements ClientModInitializer {

	// Left Alt
	public static final KeyBinding KEY_LEFT_ALT = new KeyBinding(
			"key._3d_viewer.key_binding_left_alt",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_LEFT_ALT,
			"key.category._3d_viewer"
	){
		@Override
		public void setPressed(boolean pressed) {
			super.setPressed(pressed);
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			if (player != null) {
				LastCentury.ifCLickedLeftAlt = pressed;
			}
		}
	};

	// Right Alt
	public static final KeyBinding KEY_RIGHT_ALT = new KeyBinding(
			"key._3d_viewer.key_right_binding_alt",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_RIGHT_ALT,
			"key.category._3d_viewer"
	){
		@Override
		public void setPressed(boolean pressed) {
			super.setPressed(pressed);
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			if (player != null) {
				LastCentury.ifCLickedRightAlt = pressed;
			}
		}
	};

	// Left shift
	public static final KeyBinding KEY_LEFT_SHIFT = new KeyBinding(
			"key._3d_viewer.key_binding_left_shift",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_LEFT_SHIFT,
			"key.category._3d_viewer"
	){
		@Override
		public void setPressed(boolean pressed) {
			super.setPressed(pressed);
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			if (player != null) {
				LastCentury.ifCLickedLeftShift = pressed;
			}
		}
	};

	// Right shift
	public static final KeyBinding KEY_RIGHT_SHIFT = new KeyBinding(
			"key._3d_viewer.key_right_binding_shift",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_RIGHT_SHIFT,
			"key.category._3d_viewer"
	){
		@Override
		public void setPressed(boolean pressed) {
			super.setPressed(pressed);
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			if (player != null) {
				LastCentury.ifCLickedRightShift = pressed;
			}
		}
	};

	// Left ctrl
	public static final KeyBinding KEY_LEFT_CTRL = new KeyBinding(
			"key._3d_viewer.key_left_binding_ctrl",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_LEFT_CONTROL,
			"key.category._3d_viewer"
	){
		@Override
		public void setPressed(boolean pressed) {
			super.setPressed(pressed);
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			if (player != null) {
				LastCentury.ifCLickedLeftCtrl = pressed;
			}
		}
	};


	public float[][] vertices = {
			{1.0f, 1.0f, 1.0f},
			{1.0f, -1.0f, 1.0f},
			{-1.0f, -1.0f, 1.0f},
			{-1.0f, 1.0f, 1.0f},
			{1.0f, 1.0f, -1.0f},
			{1.0f, -1.0f, -1.0f},
			{-1.0f, -1.0f, -1.0f},
			{-1.0f, 1.0f, -1.0f}
	};
	public float[][] vertices_2 = {
			{1.0f, 1.0f + 1.0f, 1.0f},
			{1.0f, -1.0f + 1.0f, 1.0f},
			{-1.0f, -1.0f + 1.0f, 1.0f},
			{-1.0f, 1.0f + 1.0f, 1.0f},
			{1.0f, 1.0f + 1.0f, -1.0f},
			{1.0f, -1.0f + 1.0f, -1.0f},
			{-1.0f, -1.0f + 1.0f, -1.0f},
			{-1.0f, 1.0f + 1.0f, -1.0f}
	};

	public float fov = 55.0f;
	public float zNear = 0.1f;
	public float zFar = 1000.0f;
	public static Scene newScene;

	@Override
	public void onInitializeClient() {
		KeyBindingHelper.registerKeyBinding(KEY_LEFT_ALT);
		KeyBindingHelper.registerKeyBinding(KEY_RIGHT_ALT);
		KeyBindingHelper.registerKeyBinding(KEY_LEFT_SHIFT);
		KeyBindingHelper.registerKeyBinding(KEY_RIGHT_SHIFT);
		/* KEY_LEFT_CTRL :
		1 - Line_DEBUG
		2 - Line_DEBUG_POINT
		3 - Face_NORMAL
		4 - Face_DeepthTest
		5 - Face_DeepthTest_Shadow
		 */
		KeyBindingHelper.registerKeyBinding(KEY_LEFT_CTRL);

		newScene = new Scene(fov,zNear,zFar, new float[]{40,40,10,100,100,100});
		Cube newCube = new Cube(vertices);
		Cube newCube_2 = new Cube(vertices_2);
		newScene.addObject(newCube);
		newScene.addObject(newCube_2);
		HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
			// See below
			Matrix4f positionMatrix = drawContext.getMatrices().peek().getPositionMatrix();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();
			newScene.render(RenderMode.ModeList[ViewMode],drawContext,tickDelta,buffer,positionMatrix,tessellator);
		});
	}

}
