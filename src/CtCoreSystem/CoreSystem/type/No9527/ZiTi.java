package CtCoreSystem.CoreSystem.type.No9527;

import arc.freetype.FreeTypeFontGenerator;
import arc.files.Fi;
import arc.func.Prov;
import arc.graphics.g2d.Font;
import arc.struct.ObjectMap;
import arc.util.Log;
import mindustry.Vars;
import mindustry.ui.Fonts;

public class ZiTi {
    private static String modName = "ctcoresystem";
    private static Fi ROOT;
    // 存储已注册的字体
    private static final ObjectMap<String, Font> fonts = new ObjectMap<>();

    public static void registerFonts() {
        try {
            // 获取mod根目录（修正：使用类变量而不是局部变量）
            ROOT = Vars.mods.locateMod(modName).root;

            // 注册字体
            // 参数：字体ID, 字体文件路径, 字体大小
            registerFont("cute", ROOT.child("fonts/font.woff"), 16);
            registerFont("cuteTech", ROOT.child("fonts/tech.ttf"), 16);
        } catch (Exception e) {
            Log.err("字体注册错误: " + e.getMessage(), e);
        }
    }

    private static void registerFont(String fontId, Fi file, int size) {
        if (!file.exists()) {
            Log.err("找不到字体文件: " + file.path());
            return;
        }

        try {
            // 创建字体生成器
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(file);
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

            // 设置字体参数
            parameter.size = size;
            parameter.incremental = true;
            parameter.hinting = FreeTypeFontGenerator.Hinting.none;

            // 生成字体
            Font font = generator.generateFont(parameter);

            // 存储字体供自定义使用
            fonts.put(fontId, font);

            Log.info("字体已成功注册: " + fontId + " (" + file.path() + ")");

            // 释放生成器资源
            generator.dispose();
        } catch (Exception e) {
            Log.err("注册字体失败: " + fontId + " - " + e.getMessage(), e);
        }
    }

    // 获取已注册的字体
    public static Font getFont(String fontId) {
        return fonts.get(fontId, Fonts.outline); // 默认返回outline字体
    }
}