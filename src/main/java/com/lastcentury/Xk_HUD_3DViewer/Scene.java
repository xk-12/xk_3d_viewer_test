package com.lastcentury.Xk_HUD_3DViewer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;

import static com.lastcentury.LastCentury.ProjectionMode;
import static java.lang.Math.round;

public class Scene {
	private final float fov;
	private final float zNear;
	private final float zFar;
	private ArrayList<Cube> objects;  // 场景中的物体
	private Color backgroundColor; // 背景色
	private float[] boundingBox;   // 边界框 [minX, minY, minZ, maxX, maxY, maxZ]
	public Camera camera;
	
	public Scene(float fov,float zNear,float zFar,float[] boundingBox) {
		this.fov = fov;
		this.zNear = zNear;
		this.zFar = zFar;
		this.objects = new ArrayList<>();
		this.backgroundColor = Color.WHITE; // 默认白色背景
		this.boundingBox = boundingBox;
		// 初始化相机
		this.camera = new Camera(new Vector3f(0,0,-10), new Vector3f(0,0,0));
	}

	private float[] calculateModelCenter() {
		// 获取总模型的几何中心
		float centerX = 0, centerY = 0, centerZ = 0;
		float count = 0;
		for (Cube cube : objects) {
			// 获取单个cube
			for (float[] vertex : cube.getVertices()) {
				centerX += vertex[0];
				centerY += vertex[1];
				centerZ += vertex[2];
				count += 1;
			}
		}
		centerX /= count;
		centerY /= count;
		centerZ /= count;
		return new float[]{centerX, centerY, centerZ};
	}

	// [视图旋转]
	public void updateViewRotate(double dx, double dy) {
		// 调整旋转角度的灵敏度,旋转场景里的物体
		float d = 0.0015f;
		double viewAngleX = dx * d;
		double viewAngleY = dy * d;
		// 限制视角的X轴旋转范围（俯仰角）
		viewAngleX = Math.max(-90.0f, Math.min(90.0f, viewAngleX));
		float[] centerPos = calculateModelCenter();
		// System.out.println(Arrays.toString(new double[]{bd_x.doubleValue(), bd_y.doubleValue()}));
		// 更新所有物体的旋转角度
		for (Cube cube : objects) {
			cube.rotateUseCenter( - (float) (viewAngleY), (float) (viewAngleX),centerPos);
		}
	}

	// [视图平移]
	public void updateViewTranslate(double dx, double dy) {
		// 平移场景里的物体
		float d = 0.0025f;
		for (Cube cube : objects) {
			cube.translate(- (float) dx*d, - (float) dy*d,0f);
		}
	}

	// [视图缩放]
	public void updateViewScala(double dy) {
		// 缩放场景里的物体
		float d = 0.0025f;
		float[] centerPos = calculateModelCenter();
		for (Cube cube : objects) {
			cube.scaleUseCenter(1-(float) dy*d, 1-(float) dy*d, (float) (1-dy*d),centerPos);
		}
	}

	// [相机旋转]
	public void updateCameraRotate(double dx, double dy) {
		float d = 0.0015f;
		double viewAngleX = dx * d;
		double viewAngleY = dy * d;
		// 限制视角的X轴旋转范围（俯仰角）
		viewAngleX = Math.max(-90.0f, Math.min(90.0f, viewAngleX));
		this.camera.rotate(-(float) viewAngleY, -(float) viewAngleX);
	}

	// [相机平移]
	public void updateCameraTranslate(double dx, double dy) {
		// 平移场景里的物体
		float d = 0.01f;
		this.camera.move((float) (dx*d), - (float) (dy*d));
	}

	// [相机缩放]
	public void updateCameraScala(double dy) {
		// 缩放场景里的物体
		float d = 0.025f;
		this.camera.zoom((float) (dy*d));
	}

	public void resetViewPort() {
		camera.viewpoint = new Vector3f(0, 0, 0);
	}

	// 添加物体到场景
	public void addObject(Cube cube) {
		objects.add(cube);
	}

	// 设置背景色
	public void setBackgroundColor(Color color) {
		this.backgroundColor = color;
	}

	// 设置边界框
	public void setBoundingBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		this.boundingBox = new float[]{minX, minY, minZ, maxX, maxY, maxZ};
	}

	// 检查点是否在边界框内
	public  boolean isPointInsideBoundingBox(float[] point) {
		return point[0] >= boundingBox[0] && point[0] <= boundingBox[3] &&
				point[1] >= boundingBox[1] && point[1] <= boundingBox[4] &&
				point[2] >= boundingBox[2] && point[2] <= boundingBox[5];
	}

	// 调整点到边界框
	public float[] adjustPointToBoundingBox(float[] point) {
		float[] adjustedPoint = new float[3];
		adjustedPoint[0] = Math.max(boundingBox[0], Math.min(point[0], boundingBox[3]));
		adjustedPoint[1] = Math.max(boundingBox[1], Math.min(point[1], boundingBox[4]));
		adjustedPoint[2] = Math.max(boundingBox[2], Math.min(point[2], boundingBox[5]));
		return adjustedPoint;
	}

	// 渲染场景
	public void render(RenderMode renderMode,
	                   DrawContext drawContext,
	                   float tickDelta,
	                   BufferBuilder buffer,
	                   Matrix4f positionMatrix,
	                   Tessellator tessellator) {
		// 绘制背景
		int screenWidth = drawContext.getScaledWindowWidth();
		int screenHeight = drawContext.getScaledWindowHeight();
		float aspect = (float) screenWidth / screenHeight;
		// 相机矩阵
		Matrix4f viewMatrix = camera.getViewMatrix();
		Matrix4f projectionMatrix = camera.getProjectionMatrix(this.fov, aspect, this.zNear, this.zFar);
		// 渲染场景中的所有物体
		for (Cube cube : objects) {
			if (ProjectionMode == 0){
				cube.render(renderMode,
						drawContext,
						tickDelta,
						buffer,
						positionMatrix,
						tessellator,
						this,
						this.fov,
						this.zNear,
						this.zFar,
						aspect,
						screenWidth,
						screenHeight,
						viewMatrix,
						projectionMatrix,
						0xffFFFFFF
				);
			}else if(ProjectionMode == 1){
				cube.orthographicRender(
						renderMode,
						drawContext,
						buffer,
						positionMatrix,
						tessellator,
						viewMatrix,
						screenWidth,
						screenHeight,
						0,
						screenWidth,
						screenHeight,
						0,
						this.zNear,
						this.zFar,
						0xffFFFFFF
				);
			}
		}
		// 可选：绘制边界框
		drawBoundingBox(drawContext);
	}

	// 绘制边界框
	private void drawBoundingBox(DrawContext drawContext) {
		// 实现具体的绘制逻辑，例如绘制边界框的线
	}
}