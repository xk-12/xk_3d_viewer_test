package com.lastcentury.Xk_HUD_3DViewer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Cube {

	private static final int[][] faces = {
			{0, 1, 2, 3}, // 前面
			{4, 5, 6, 7}, // 后面
			{0, 3, 7, 4}, // 左面
			{1, 2, 6, 5}, // 右面
			{0, 1, 5, 4}, // 上面
			{3, 2, 6, 7}  // 下面
	};

	private float[][] vertices;
	private float angleX = 0;
	private float angleY = 0;

	public Cube(float[][] vertices) {
		this.vertices = vertices;
	}

	public float[][] getVertices() {
		return vertices;
	}
	private float[] calculateCenter() {
		float centerX = 0, centerY = 0, centerZ = 0;
		for (float[] vertex : vertices) {
			centerX += vertex[0];
			centerY += vertex[1];
			centerZ += vertex[2];
		}
		centerX /= vertices.length;
		centerY /= vertices.length;
		centerZ /= vertices.length;
		return new float[]{centerX, centerY, centerZ};
	}

	public void rotate(float angleX, float angleY) {
		this.angleX += angleX;
		this.angleY += angleY;

		float[][] rotationMatrix = Matrix.getRotationMatrix(angleX, angleY);
		float[] center = calculateCenter();

		for (int i = 0; i < vertices.length; i++) {
			float[] vertex = vertices[i];

			// 移动到原点进行旋转
			vertex[0] -= center[0];
			vertex[1] -= center[1];
			vertex[2] -= center[2];

			// 转换为齐次坐标
			float[] homogeneousVertex = {vertex[0], vertex[1], vertex[2], 1};

			// 旋转
			float[] rotatedVertex = Matrix.multiplyMatrixVector(rotationMatrix, homogeneousVertex);

			// 平移回原位置
			rotatedVertex[0] += center[0];
			rotatedVertex[1] += center[1];
			rotatedVertex[2] += center[2];

			vertices[i] = new float[]{rotatedVertex[0], rotatedVertex[1], rotatedVertex[2]};
		}
	}

	public void rotateUseCenter(float angleX, float angleY,float[] center) {
		this.angleX += angleX;
		this.angleY += angleY;

		float[][] rotationMatrix = Matrix.getRotationMatrix(angleX, angleY);

		for (int i = 0; i < vertices.length; i++) {
			float[] vertex = vertices[i];

			// 移动到原点进行旋转
			vertex[0] -= center[0];
			vertex[1] -= center[1];
			vertex[2] -= center[2];

			// 转换为齐次坐标
			float[] homogeneousVertex = {vertex[0], vertex[1], vertex[2], 1};

			// 旋转
			float[] rotatedVertex = Matrix.multiplyMatrixVector(rotationMatrix, homogeneousVertex);

			// 平移回原位置
			rotatedVertex[0] += center[0];
			rotatedVertex[1] += center[1];
			rotatedVertex[2] += center[2];

			vertices[i] = new float[]{rotatedVertex[0], rotatedVertex[1], rotatedVertex[2]};
		}
	}

	public void translate(float tx, float ty, float tz) {
		for (int i = 0; i < vertices.length; i++) {
			vertices[i][0] += tx;
			vertices[i][1] += ty;
			vertices[i][2] += tz;
		}
	}

	public void scale(float sx, float sy, float sz) {
		float[] center = calculateCenter();

		for (int i = 0; i < vertices.length; i++) {
			float[] vertex = vertices[i];

			// 移动到原点进行缩放
			vertex[0] -= center[0];
			vertex[1] -= center[1];
			vertex[2] -= center[2];

			// 缩放
			vertex[0] *= sx;
			vertex[1] *= sy;
			vertex[2] *= sz;

			// 平移回原位置
			vertex[0] += center[0];
			vertex[1] += center[1];
			vertex[2] += center[2];

			vertices[i] = vertex;
		}
	}

	public void scaleUseCenter(float sx, float sy, float sz,float[] center) {
		for (int i = 0; i < vertices.length; i++) {
			float[] vertex = vertices[i];

			// 移动到原点进行缩放
			vertex[0] -= center[0];
			vertex[1] -= center[1];
			vertex[2] -= center[2];

			// 缩放
			vertex[0] *= sx;
			vertex[1] *= sy;
			vertex[2] *= sz;

			// 平移回原位置
			vertex[0] += center[0];
			vertex[1] += center[1];
			vertex[2] += center[2];

			vertices[i] = vertex;
		}
	}

	public void render(RenderMode renderMode,
	                   DrawContext drawContext,
	                   float tickDelta,
	                   BufferBuilder buffer,
	                   Matrix4f positionMatrix,
	                   Tessellator tessellator,
	                   Scene scene,
	                   float fov,
	                   float zNear,
	                   float zFar,
	                   float aspect,
	                   int screenWidth,
	                   int screenHeight,
	                   Matrix4f viewMatrix,
	                   Matrix4f projectionMatrix,
	                   int color
	                   ) {

		int[][] AllPoints = new int[8][2];
		for (int x= 0;x < faces.length;x++) {
			int[] face = faces[x];
			int[][] screenPoints = new int[face.length][2];
			for (int i = 0; i < face.length; i++) {
				int vertexIndex = face[i];
				float[] vertex = vertices[vertexIndex];
				// 使用视图矩阵和投影矩阵转换坐标
				Vector4f worldPos = new Vector4f(vertex[0], vertex[1], vertex[2], 1.0f);
				Vector4f viewPos = viewMatrix.transform(worldPos);
				Vector4f projPos = projectionMatrix.transform(viewPos);
				// 透视除法
				projPos.div(projPos.w);

				// 映射到屏幕坐标
				int xScreen = (int) ((projPos.x + 1) / 2 * screenWidth);
				int yScreen = (int) ((projPos.y + 1) / 2 * screenHeight);
				screenPoints[i] = new int[]{xScreen, yScreen};
				AllPoints[vertexIndex] = new int[]{xScreen, yScreen};
			}
			if (renderMode == RenderMode.Line_DEBUG) {
				// 绘制线段
				for (int i = 0; i < screenPoints.length; i++) {
					int x1 = screenPoints[i][0];
					int y1 = screenPoints[i][1];
					int x2 = screenPoints[(i + 1) % screenPoints.length][0];
					int y2 = screenPoints[(i + 1) % screenPoints.length][1];
					//
					RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
					buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
					buffer.vertex(positionMatrix, x1, y1, 1).color(color).next();
					buffer.vertex(positionMatrix, x2, y2, 1).color(color).next();
					RenderSystem.setShader(GameRenderer::getPositionTexProgram);
					RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
					tessellator.draw();
				}
			}
			else if (renderMode == RenderMode.Line_DEBUG_POINT) {
				for (int i = 0; i < screenPoints.length; i++) {
					int x1 = screenPoints[i][0];
					int y1 = screenPoints[i][1];
					int x2 = screenPoints[(i + 1) % screenPoints.length][0];
					int y2 = screenPoints[(i + 1) % screenPoints.length][1];
					// 渲染线
					RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
					buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
					buffer.vertex(positionMatrix, x1, y1, 1).color(color).next();
					buffer.vertex(positionMatrix, x2, y2, 1).color(color).next();
					RenderSystem.setShader(GameRenderer::getPositionTexProgram);
					RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
					tessellator.draw();
				}
			}
			else if (renderMode == RenderMode.Face_NORMAL) {
				// 渲染面
				buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
				buffer.vertex(positionMatrix, screenPoints[0][0], screenPoints[0][1], 1).texture(0f, 0f).next();
				buffer.vertex(positionMatrix, screenPoints[1][0], screenPoints[1][1], 1).texture(0f, 1f).next();
				buffer.vertex(positionMatrix, screenPoints[2][0], screenPoints[2][1], 1).texture(1f, 1f).next();
				buffer.vertex(positionMatrix, screenPoints[3][0], screenPoints[3][1], 1).texture(1f, 0f).next();
				RenderSystem.setShader(GameRenderer::getPositionTexProgram);
				RenderSystem.setShaderTexture(0, new Identifier("lastcentury", "icon.png"));
				RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
				tessellator.draw();
			}
		}
		// 测试信息渲染
		if (renderMode == RenderMode.Line_DEBUG_POINT) {
			// 渲染点
			for (int i = 0; i < AllPoints.length; i++) {
				int x1 = AllPoints[i][0];
				int y1 = AllPoints[i][1];
				buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
				buffer.vertex(positionMatrix, x1-1, y1-1, 1).color(0xFF0000FF).next();
				buffer.vertex(positionMatrix, x1-1, y1+1, 1).color(0xFF0000FF).next();
				buffer.vertex(positionMatrix, x1+1, y1+1, 1).color(0xFF0000FF).next();
				buffer.vertex(positionMatrix, x1+1, y1-1, 1).color(0xFF0000FF).next();
				RenderSystem.setShader(GameRenderer::getPositionColorProgram);
				RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
				tessellator.draw();
			}
			// 渲染点的检索值
			for (int i = 0; i < AllPoints.length; i++) {
				int x1 = AllPoints[i][0];
				int y1 = AllPoints[i][1];
				drawContext.drawText(MinecraftClient.getInstance().textRenderer, Integer.toString(i),x1,y1,0xFFDF901F,false);
			}
		}
	}

	public void orthographicRender(RenderMode renderMode,
	                               DrawContext drawContext,
	                               BufferBuilder buffer,
	                               Matrix4f positionMatrix,
	                               Tessellator tessellator,
	                               Matrix4f viewMatrix,
	                               float screenWidth,
	                               float screenHeight,
	                               float left,
	                               float right,
	                               float bottom,
	                               float top,
	                               float zNear,
	                               float zFar,
	                               int color) {


		// 创建正交投影矩阵
		Matrix4f projectionMatrix = new Matrix4f().ortho(left, right, bottom, top, zNear, zFar);

		// 存储所有屏幕坐标点
		int[][] AllPoints = new int[8][2];

		for (int x = 0; x < faces.length; x++) {
			int[] face = faces[x];
			int[][] screenPoints = new int[face.length][2];

			for (int i = 0; i < face.length; i++) {
				int vertexIndex = face[i];
				float[] vertex = vertices[vertexIndex];

				// 使用视图矩阵和正交投影矩阵转换坐标
				Vector4f worldPos = new Vector4f(vertex[0], vertex[1], vertex[2], 1.0f);
				Vector4f viewPos = viewMatrix.transform(worldPos);
				Vector4f projPos = projectionMatrix.transform(viewPos);

				// 映射到屏幕坐标
				int xScreen = (int) (projPos.x + screenWidth / 2); // 中心化
				int yScreen = (int) (screenHeight / 2 - projPos.y); // 翻转Y轴
				screenPoints[i] = new int[]{xScreen, yScreen};
				AllPoints[vertexIndex] = new int[]{xScreen, yScreen};
			}

			if (renderMode == RenderMode.Line_DEBUG) {
				// 绘制线段
				for (int i = 0; i < screenPoints.length; i++) {
					int x1 = screenPoints[i][0];
					int y1 = screenPoints[i][1];
					int x2 = screenPoints[(i + 1) % screenPoints.length][0];
					int y2 = screenPoints[(i + 1) % screenPoints.length][1];
					//
					RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
					buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
					buffer.vertex(positionMatrix, x1, y1, 1).color(color).next();
					buffer.vertex(positionMatrix, x2, y2, 1).color(color).next();
					RenderSystem.setShader(GameRenderer::getPositionTexProgram);
					RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
					tessellator.draw();
				}
			}
			else if (renderMode == RenderMode.Line_DEBUG_POINT) {
				for (int i = 0; i < screenPoints.length; i++) {
					int x1 = screenPoints[i][0];
					int y1 = screenPoints[i][1];
					int x2 = screenPoints[(i + 1) % screenPoints.length][0];
					int y2 = screenPoints[(i + 1) % screenPoints.length][1];
					// 渲染线
					RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
					buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
					buffer.vertex(positionMatrix, x1, y1, 1).color(color).next();
					buffer.vertex(positionMatrix, x2, y2, 1).color(color).next();
					RenderSystem.setShader(GameRenderer::getPositionTexProgram);
					RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
					tessellator.draw();
				}
			}
			else if (renderMode == RenderMode.Face_NORMAL) {
				// 渲染面
				buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
				buffer.vertex(positionMatrix, screenPoints[0][0], screenPoints[0][1], 1).texture(0f, 0f).next();
				buffer.vertex(positionMatrix, screenPoints[1][0], screenPoints[1][1], 1).texture(0f, 1f).next();
				buffer.vertex(positionMatrix, screenPoints[2][0], screenPoints[2][1], 1).texture(1f, 1f).next();
				buffer.vertex(positionMatrix, screenPoints[3][0], screenPoints[3][1], 1).texture(1f, 0f).next();
				RenderSystem.setShader(GameRenderer::getPositionTexProgram);
				RenderSystem.setShaderTexture(0, new Identifier("lastcentury", "icon.png"));
				RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
				tessellator.draw();
			}
		}
		// 测试信息渲染
		if (renderMode == RenderMode.Line_DEBUG_POINT) {
			// 渲染点
			for (int i = 0; i < AllPoints.length; i++) {
				int x1 = AllPoints[i][0];
				int y1 = AllPoints[i][1];
				buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
				buffer.vertex(positionMatrix, x1-1, y1-1, 1).color(0xFF287BDE).next();
				buffer.vertex(positionMatrix, x1-1, y1+1, 1).color(0xFF287BDE).next();
				buffer.vertex(positionMatrix, x1+1, y1+1, 1).color(0xFF287BDE).next();
				buffer.vertex(positionMatrix, x1+1, y1-1, 1).color(0xFF287BDE).next();
				RenderSystem.setShader(GameRenderer::getPositionColorProgram);
				RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
				tessellator.draw();
			}
			// 渲染点的检索值
			for (int i = 0; i < AllPoints.length; i++) {
				int x1 = AllPoints[i][0];
				int y1 = AllPoints[i][1];
				drawContext.drawText(MinecraftClient.getInstance().textRenderer, Integer.toString(i),x1,y1,0xFFDF901F,false);
			}
		}
	}

	// 正交投影矩阵
	public Matrix4f getOrthographicProjection(float left, float right, float bottom, float top, float near, float far) {
		Matrix4f projectionMatrix = new Matrix4f();
		return projectionMatrix.ortho(left, right, bottom, top, near, far);
	}
}
