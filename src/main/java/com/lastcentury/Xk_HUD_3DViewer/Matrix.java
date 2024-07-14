package com.lastcentury.Xk_HUD_3DViewer;

public class Matrix {

	// 透视投影矩阵
	private static float[][] getProjectionMatrix(float fov, float aspect, float zNear, float zFar) {
		float tanHalfFOV = (float) Math.tan(Math.toRadians(fov / 2));
		float[][] matrix = new float[4][4];

		matrix[0][0] = 1 / (aspect * tanHalfFOV);
		matrix[1][1] = 1 / tanHalfFOV;
		matrix[2][2] = -(zFar + zNear) / (zFar - zNear);
		matrix[2][3] = -1;
		matrix[3][2] = -(2 * zFar * zNear) / (zFar - zNear);

		return matrix;
	}

	// 旋转矩阵
	public static float[][] getRotationMatrix(float angleX, float angleY) {
		float[][] rotationX = {
				{1, 0, 0, 0},
				{0, (float) Math.cos(angleX), (float) -Math.sin(angleX), 0},
				{0, (float) Math.sin(angleX), (float) Math.cos(angleX), 0},
				{0, 0, 0, 1}
		};

		float[][] rotationY = {
				{(float) Math.cos(angleY), 0, (float) Math.sin(angleY), 0},
				{0, 1, 0, 0},
				{(float) -Math.sin(angleY), 0, (float) Math.cos(angleY), 0},
				{0, 0, 0, 1}
		};

		// 矩阵相乘，返回旋转矩阵
		return multiplyMatrices(rotationY, rotationX);
	}

	// 获取沿 X 轴旋转的旋转矩阵
	public static float[][] getRotationMatrixX(float angle) {
		return new float[][]{
				{1, 0, 0, 0},
				{0, (float) Math.cos(angle), (float) -Math.sin(angle), 0},
				{0, (float) Math.sin(angle), (float) Math.cos(angle), 0},
				{0, 0, 0, 1}
		};
	}

	// 获取沿 Y 轴旋转的旋转矩阵
	public static float[][] getRotationMatrixY(float angle) {
		return new float[][]{
				{(float) Math.cos(angle), 0, (float) Math.sin(angle), 0},
				{0, 1, 0, 0},
				{(float) -Math.sin(angle), 0, (float) Math.cos(angle), 0},
				{0, 0, 0, 1}
		};
	}

	// 矩阵与向量相乘
	public static float[] multiplyMatrixVector(float[][] matrix, float[] vector) {
		float[] result = new float[4];
		for (int i = 0; i < 4; i++) {
			result[i] = 0;
			for (int j = 0; j < 4; j++) {
				result[i] += matrix[i][j] * vector[j];
			}
		}
		return result;
	}
	// 矩阵相乘
	private static float[][] multiplyMatrices(float[][] a, float[][] b) {
		float[][] result = new float[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				result[i][j] = a[i][0] * b[0][j] + a[i][1] * b[1][j] + a[i][2] * b[2][j] + a[i][3] * b[3][j];
			}
		}
		return result;
	}

	// 将三维点转换为二维点
	public static int[] projectPoint(float x, float y, float z, float fov, float aspect, float zNear, float zFar, int screenWidth, int screenHeight) {
		float[] point3D = {x, y, z, 1};
		float[][] projectionMatrix = getProjectionMatrix(fov, aspect, zNear, zFar);
		float[] projectedPoint = multiplyMatrixVector(projectionMatrix, point3D);

		// 透视除法
		float xNDC = projectedPoint[0] / projectedPoint[3];
		float yNDC = projectedPoint[1] / projectedPoint[3];

		// 映射到屏幕坐标
		int xScreen = (int) ((xNDC + 1) / 2 * screenWidth);
		int yScreen = (int) ((yNDC + 1) / 2 * screenHeight);

		return new int[]{xScreen, yScreen};
	}
}
