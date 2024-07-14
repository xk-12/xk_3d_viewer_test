package com.lastcentury;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static com.lastcentury.LastCenturyClient.newScene;
import static org.lwjgl.glfw.GLFW.*;

public class LastCentury implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static boolean isClicked = false;
	public static double[] StartCursorPostition;
	public static double[] CursorPostition;
	public static boolean ifCLickedLeftAlt = false;
	public static boolean ifCLickedRightAlt = false;
	public static boolean ifCLickedLeftShift = false;
	public static boolean ifCLickedRightShift = false;
	public static boolean ifCLickedLeftCtrl = false;
	public static int ViewMode = 0;
	// 0 - 透视投影
	// 1 - 正交投影
	public static int ProjectionMode = 0;
    public static final Logger LOGGER = LoggerFactory.getLogger("last_century");


	@Override
	public void onInitialize() {
		// left click event
		ClientPreAttackCallback.EVENT.register((client, $_world, $_int) -> {
			client.mouse.unlockCursor();
			if(client.player != null){
				if (client.mouse.getX() < 0 ||
						client.mouse.getY() < 0 ||
						client.mouse.getX() > client.getWindow().getWidth() ||
						client.mouse.getY() > client.getWindow().getHeight()){
					// 出界1 x<0
					glfwSetCursorPos(client.getWindow().getHandle(),
							(double) client.getWindow().getWidth()/2,
							(double) client.getWindow().getHeight()/2);

					// System.out.println(Arrays.toString(new int[] {(int) client.mouse.getX(), (int) client.mouse.getY()}));
					// System.out.println("出界警告");
					isClicked =  false;
					return true;
				}
				if (! isClicked) {
					// 如果是false
					StartCursorPostition = new double[] { client.mouse.getX(),client.mouse.getY()};
					CursorPostition = new double[] { client.mouse.getX(),client.mouse.getY()};
					// LOGGER.info("按下鼠标 , Reset CursorPostition");
				}
				isClicked =  true;
				return true; // 返回成功结果
			}
			else {
				return false;
			}
		});

		// tick events
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			// 检测玩家是否按住并且鼠标已解锁 [旋转物体]
			if (isClicked){
				if (client.mouse.wasLeftButtonClicked()) {
					// 如果按下左右ALT则触发平移视图
					if (ifCLickedLeftAlt || ifCLickedRightAlt) {
						if (!Arrays.equals(new double[]{client.mouse.getX(), client.mouse.getY()}, CursorPostition)){
							// 计算偏移值
							// 如果现在的坐标点跟上一次记录的不一样
							// 计算现在的偏移值，并且调用update
							double dx = client.mouse.getX() - CursorPostition[0];
							double dy = client.mouse.getY() - CursorPostition[1];
							// [平移]
							newScene.updateCameraTranslate(dx,dy);
							CursorPostition = new double[] { client.mouse.getX(),client.mouse.getY()};
							return;
						}
						return;
					}
					// 如果按下的是左右SHIFT触发缩放视图
					else if(ifCLickedLeftShift || ifCLickedRightShift) {
						if (!Arrays.equals(new double[]{client.mouse.getX(), client.mouse.getY()}, CursorPostition)){
							// 计算偏移值
							double dy = client.mouse.getY() - CursorPostition[1];
							// [缩放]
							newScene.updateCameraScala(dy);
							CursorPostition = new double[] { client.mouse.getX(),client.mouse.getY()};
						}
						return;
					}
					// 如果既不是ALT，也不是SHIFT，则为普通旋转视图
					else {
						if (!Arrays.equals(new double[]{client.mouse.getX(), client.mouse.getY()}, CursorPostition)){
							// 计算偏移值
							double dx = client.mouse.getX() - CursorPostition[0];
							double dy = client.mouse.getY() - CursorPostition[1];
							// [旋转]
							newScene.updateCameraRotate(dx,dy);
							CursorPostition = new double[] { client.mouse.getX(),client.mouse.getY()};
						}
						return;
					}
				} else {
					client.mouse.unlockCursor();
					isClicked = false;
				}
			}
			// 监听1 2 3 4 5按键
			if (ifCLickedLeftCtrl) {
				if (InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW_KEY_1)) {
					ViewMode = 0;
				}else if (InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW_KEY_2)) {
					ViewMode = 1;
				}else if (InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW_KEY_3)) {
					ViewMode = 2;
				}else if (InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW_KEY_4)) {
					ViewMode = 3;
				}else if (InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW_KEY_5)) {
					ViewMode = 4;
				}else if (InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW_KEY_GRAVE_ACCENT)) {
					// 按键被按下,重置相机视点
					newScene.resetViewPort();
				}else if (InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW_KEY_PAGE_UP)) {
					// 透视投影
					ProjectionMode = 0;
				}else if (InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW_KEY_PAGE_DOWN)) {
					// 透视投影
					ProjectionMode = 1;
				}
			}
		});
	}
}