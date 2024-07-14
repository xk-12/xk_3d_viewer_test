package com.lastcentury.Xk_HUD_3DViewer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
	public Vector3f position; // 相机位置
	public Vector3f rotation; // 相机旋转（以弧度为单位）
	public Vector3f viewpoint; // 相机视点

	public Camera(Vector3f position, Vector3f rotation) {
		this.position = position;
		this.rotation = rotation;
		this.viewpoint = new Vector3f(0, 0, 0); // 初始视点为世界原点
		updatePosition(); // 初始化相机位置
	}

	// 更新相机位置以实现旋转
	private void updatePosition() {
		float distance = position.distance(viewpoint); // 计算与视点的距离
		position.x = viewpoint.x + (float) (distance * Math.sin(rotation.y) * Math.cos(rotation.x));
		position.y = viewpoint.y + (float) (distance * Math.sin(rotation.x));
		position.z = viewpoint.z + (float) (distance * Math.cos(rotation.y) * Math.cos(rotation.x));
	}

	// 旋转相机
	public void rotate(float dPitch, float dYaw) {
		rotation.x += dPitch;
		rotation.y += dYaw;

		// 限制俯仰角度
		if (rotation.x > Math.PI / 2) rotation.x = (float) (Math.PI / 2);
		if (rotation.x < -Math.PI / 2) rotation.x = (float) (-Math.PI / 2);

		updatePosition(); // 更新相机位置
	}

	// 缩放相机
	public void zoom(float amount) {
		Vector3f direction = new Vector3f(position).sub(viewpoint).normalize();
		position.add(direction.mul(amount)); // 根据方向移动相机
	}

	public Vector3f getForwardVector() {
		return new Vector3f(
				(float) (Math.sin(rotation.y) * Math.cos(rotation.x)),
				(float) Math.sin(rotation.x),
				(float) (Math.cos(rotation.y) * Math.cos(rotation.x))
		).normalize();
	}

	public void move(float dx, float dy) {
		// 计算相机的右向量和上向量
		Vector3f forward = getForwardVector().normalize();
		Vector3f right = new Vector3f(forward).cross(new Vector3f(0, 1, 0)).normalize(); // 右向量
		Vector3f up = new Vector3f(right).cross(forward).normalize(); // 上向量

		// 平移相机
		viewpoint.add(right.mul(dx)); // 平移视点
		viewpoint.add(up.mul(dy)); // 垂直平移
		updatePosition(); // 更新相机位置
	}

	// 获取视图矩阵
	public Matrix4f getViewMatrix() {
		Matrix4f viewMatrix = new Matrix4f();
		return viewMatrix.lookAt(position, viewpoint, new Vector3f(0, 1, 0)); // 向上向量
	}

	// 获取投影矩阵
	public Matrix4f getProjectionMatrix(float fov, float aspect, float zNear, float zFar) {
		Matrix4f projectionMatrix = new Matrix4f();
		return projectionMatrix.perspective((float) Math.toRadians(fov), aspect, zNear, zFar);
	}
}
