package CtCoreSystem.CoreSystem.type;

import arc.graphics.Color;
import arc.util.Time;
import mindustry.game.Team;

import java.util.HashMap;
import java.util.Map;

//快捷颜色方法
public class CTColor {
    public static final Color 大红 = Color.valueOf("ff0000"),
            猩红 = Color.valueOf("930000"),
            暗红 = Color.valueOf("550606"),
            深金色 = Color.valueOf("ffb33c"),
            金黄色 = Color.valueOf("fff294"),
            黄色 = Color.valueOf("ffd03f"),
            浅黄色 = Color.valueOf("ffe28b"),
            米黄色 = 浅黄色, // 米黄色与浅黄色相同
            浅绿色 = Color.valueOf("bdffbb"),
            绿色 = Color.valueOf("3eff38"),
            深绿色 = Color.valueOf("05bb00"),
            暗绿色 = Color.valueOf("085f05"),
            浅橘色 = Color.valueOf("ffa97b"),
            橘色 = Color.valueOf("ff6d1f"),
            浅青色 = Color.valueOf("9afff5"),
            青色 = Color.valueOf("00ffe5"),
            浅蓝色 = Color.valueOf("9a9dff"),
            蓝色 = Color.valueOf("4348ff"),
            浅紫色 = Color.valueOf("c6a6ff"),
            紫色 = Color.valueOf("843eff"),
            深紫色 = Color.valueOf("6b16ff"),
            暗紫色 = Color.valueOf("4700c5"),
            浅粉色 = Color.valueOf("fa8bff"),
            粉色 = Color.valueOf("f63aff"),
            深粉色 = Color.valueOf("df00e9"),
            白色 = Color.valueOf("ffffff"),
            灰色 = Color.valueOf("808080"),
            黑色 = Color.valueOf("000000"),
            深灰色 = Color.valueOf("3b3b3b"),
            浅灰色 = Color.valueOf("c4c4c4"),
            红队色 = Team.crux.color,
            紫队色 = Team.malis.color,
            黄队色 = Team.sharded.color;


    public static Color 渐变色( float 渐变速度) {
        return Color.valueOf("ff0000").shiftHue((Time.time * 渐变速度) + (1 * (360 / 20f)));
    }
    public static Color 渐变色(Color 颜色, float 渐变速度) {
        return 颜色.shiftHue((Time.time * 渐变速度) + (1 * (360 / 20f)));
    }
    public static Color C(String string) {
        return Color.valueOf(string);
    }

    /*,
     * 获取所有预定义颜色的映射表
     * @return 包含颜色名称和对应Color对象的Map
     */
    public static Map<String, Color> getAllColors() {
        Map<String, Color> colors = new HashMap<>();
        // 红色系列
        colors.put("大红", 大红);
        colors.put("猩红", 猩红);
        colors.put("暗红", 暗红);

        // 黄色系列
        colors.put("黄色", 黄色);
        colors.put("浅黄色", 浅黄色);
        colors.put("米黄色", 米黄色);

        // 绿色系列
        colors.put("浅绿色", 浅绿色);
        colors.put("绿色", 绿色);
        colors.put("深绿色", 深绿色);
        colors.put("暗绿色", 暗绿色);

        // 其他颜色
        colors.put("浅橘色", 浅橘色);
        colors.put("橘色", 橘色);

        // 青色系列
        colors.put("浅青色", 浅青色);
        colors.put("青色", 青色);

        // 蓝色系列
        colors.put("浅蓝色", 浅蓝色);
        colors.put("蓝色", 蓝色);

        // 紫色系列
        colors.put("浅紫色", 浅紫色);
        colors.put("紫色", 紫色);
        colors.put("深紫色", 深紫色);
        colors.put("暗紫色", 暗紫色);

        // 粉色系列
        colors.put("浅粉色", 浅粉色);
        colors.put("粉色", 粉色);
        colors.put("深粉色", 深粉色);
        // 其他颜色
        colors.put("白色", 白色);
        colors.put("灰色", 灰色);
        colors.put("黑色", 黑色);
        colors.put("深灰色", 深灰色);
        colors.put("浅灰色", 浅灰色);
        colors.put("红队色", 红队色);
        colors.put("紫队色", 紫队色);
        colors.put("黄队色", 黄队色);
        colors.put("深金色", 深金色);
        colors.put("金黄色", 金黄色);


        return colors;
    }

    /**
     * 根据颜色名称获取对应的Color对象
     *
     * @param colorName 颜色名称
     * @return 对应的Color对象，如果找不到则返回null
     */
    public static Color getColorByName(String colorName) {
        return getAllColors().get(colorName);
    }
}
