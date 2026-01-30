package CtCoreSystem.CoreSystem.type.No9527;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import mindustry.Vars;
import mindustry.graphics.Layer;
import mindustry.ui.Fonts;

public class BlockTextRenderer {

    // 默认距离阈值：30个瓦片单位
    private static final float DEFAULT_DISTANCE_THRESHOLD = 20 * Vars.tilesize;

    // 默认基础字体缩放值
    private static final float DEFAULT_BASE_FONT_SCALE = 0.8f;

    // 字体缩放范围
    private static final float MIN_FONT_SCALE = 0.11f;
    private static final float MAX_FONT_SCALE = 0.3f;

    /**
     * 在方块上渲染文字，包含距离检测和动态字体缩放
     * @param x 方块X坐标
     * @param y 方块Y坐标
     * @param name 方块名称
     * @param blockSize 方块大小（瓦片单位）
     */
    public static void renderBlockText(float x, float y, String name, int blockSize) {
        renderBlockText(x, y, name, blockSize, DEFAULT_DISTANCE_THRESHOLD);
    }

    /**
     * 在方块上渲染文字，包含距离检测和动态字体缩放（可自定义距离阈值）
     * @param x 方块X坐标
     * @param y 方块Y坐标
     * @param name 方块名称
     * @param blockSize 方块大小（瓦片单位）
     * @param distanceThreshold 距离阈值（像素单位）
     */
    public static void renderBlockText(float x, float y, String name, int blockSize, float distanceThreshold) {
        // 距离检测：只有当方块距离相机在阈值内时才渲染文字
        float dx = x - arc.Core.camera.position.x;
        float dy = y - arc.Core.camera.position.y;
        float distanceSquared = dx * dx + dy * dy;

        if (distanceSquared <= distanceThreshold * distanceThreshold) {
            // 计算建筑的宽度
            float buildingWidth = blockSize * Vars.tilesize;
            // 计算建筑最左面的X坐标
            float leftX = x - buildingWidth / 2f;
            // 检测文字的字节数（使用UTF-8编码）
            int byteLength = 0;
            try {
                byteLength = name.getBytes("UTF-8").length;
            } catch (Exception e) {
                byteLength = name.length(); // 如果获取字节数失败，使用字符数作为备用
            }

            Draw.z(Layer.block + 1); // 设置绘制层级在方块之上
            Draw.color(Color.white); // 设置文本颜色为白色

            // 保存当前字体缩放
            float originalScale = Fonts.def.getData().scaleX;
            // 设置字体缩放因子，根据游戏缩放动态调整
            float gameScale = Vars.renderer.getDisplayScale();
            // 根据游戏缩放值计算合适的字体缩放，确保在不同缩放级别下都能保持合适的大小
            float fontScale = DEFAULT_BASE_FONT_SCALE / gameScale;
            // 添加最大值和最小值限制，确保字体缩放在合理范围内
            fontScale = Math.max(MIN_FONT_SCALE, Math.min(MAX_FONT_SCALE, fontScale));

            Fonts.def.getData().setScale(fontScale);

            // 使用默认字体绘制文本
            if (byteLength > 18) {
                Fonts.def.draw(name, leftX, y + 5, buildingWidth, 0, true);
            } else {
                // 如果字节数小于等于18，不换行
                Fonts.def.draw(name, leftX, y + 5);
            }

            // 恢复原始字体缩放
            Fonts.def.getData().setScale(originalScale);
            Draw.color(); // 重置颜色
        }
    }
}