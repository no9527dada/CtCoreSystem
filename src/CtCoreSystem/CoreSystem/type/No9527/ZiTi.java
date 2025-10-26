package CtCoreSystem.CoreSystem.type.No9527;

import arc.Core;
import arc.files.Fi;
import arc.util.Log;
import arc.util.Reflect;
import mindustry.Vars;

public class ZiTi {
    private static String modName = "ctcoresystem";
    private static Fi ROOT;

    public static void registerFonts() {
        try {
            // 获取mod根目录
            ROOT = Vars.mods.locateMod(modName).root;

            // 使用反射方式注册字体
            registerFont("fonts/font.woff", ROOT.child("cute.woff"));
            registerFont("fonts/tech.ttf", ROOT.child("cuteTech.ttf"));
        } catch (Exception e) {
            Log.err("字体注册错误: " + e.getMessage(), e);
        }
    }
            private static void registerFont(String raw, Fi file) {
        try {
            if (!file.exists()) {
                Log.err(raw+"-找不到字体文件: " + file.path());
                return;
            }
            // 使用反射调用AssetManager的addAsset方法
            Reflect.invoke(Core.assets.getClass(), Core.assets, "addAsset",
                    new Object[]{raw + ".gen", Class.forName("arc.freetype.FreeTypeFontGenerator"),
                            Class.forName("arc.freetype.FreeTypeFontGenerator").getConstructor(Fi.class).newInstance(file)},
                    String.class, Class.class, Object.class);

            Log.info("字体已成功注册: " + raw + " (" + file.path() + ")");
        } catch (Exception e) {
            Log.err("注册字体失败: " + raw + " - " + e.getMessage(), e);
        }
    }

}