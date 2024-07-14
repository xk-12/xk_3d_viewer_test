package com.lastcentury.Xk_HUD_3DViewer;

public enum RenderMode {
	Line_DEBUG, // debug线+点检索值显示
	Line_DEBUG_POINT, // debug线
	Face_NORMAL, // 普通面
	Face_DeepthTest, //深度测试面
	Face_DeepthTest_Shadow; //深度测试面+阴影渲染

	public static final RenderMode[] ModeList = new RenderMode[]{
			Line_DEBUG,
			Line_DEBUG_POINT,
			Face_NORMAL,
			Face_DeepthTest,
			Face_DeepthTest_Shadow
	};
}
