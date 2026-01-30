package CtCoreSystem.ui.dialogs;

import arc.Core;
import arc.scene.ui.CheckBox;
import arc.util.Log;
import mindustry.Vars;
import mindustry.ui.dialogs.BaseDialog;

import java.io.IOException;

public class ggg {
    public static boolean 蓝图模组, 显示模组;
    public static BaseDialog 功能图标UI;

    // 在类开始处定义配置
    private static final SettingConfig[] SETTINGS_CONFIGS = {
            new SettingConfig("蓝图模组", "蓝图模组", false),
           // new SettingConfig("显示模组", "显示模组", false)
    };

    // 配置类
    static class SettingConfig {
        String text, key;
        boolean defaultValue;

        SettingConfig(String text, String key, boolean defaultValue) {
            this.text = text;
            this.key = key;
            this.defaultValue = defaultValue;
        }
    }

    // 创建设置复选框的公共方法
    private static CheckBox createSettingCheckBox(String text, boolean defaultValue, String settingKey) {
        boolean currentValue = Core.settings.getBool(settingKey, defaultValue);
        CheckBox checkBox = new CheckBox(text);
        checkBox.setChecked(currentValue);
        checkBox.changed(() -> Core.settings.put(settingKey, checkBox.isChecked()));
        return checkBox;
    }

    // 更新变量的方法
    private static void updateSettingVariable(String key, boolean value) {
        switch(key) {
            case "蓝图模组":
                蓝图模组 = value;
                break;
       /*     case "显示模组":
                显示模组 = value;
                break;*/
        }
    }

    public static void inits() {
        // 初始化时从设置读取值
        蓝图模组 = Core.settings.getBool("蓝图模组", false);
       // 显示模组 = Core.settings.getBool("显示模组", false);

        // 自动导入auto_saver模组
        if (蓝图模组) {
            if (Vars.mods.getMod("schematic-browser") == null) {
                try {
                    Vars.mods.importMod(Vars.mods.locateMod("ctcoresystem").root.child("mod").child("schematic-browser.zip"));
                    Log.info("已自动导入模组");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        功能图标UI = new BaseDialog("模组选择"){{
            cont.table(table -> {
                table.left();
                for (SettingConfig config : SETTINGS_CONFIGS) {
                    CheckBox box = createSettingCheckBox(config.text, config.defaultValue, config.key);
                    box.changed(() -> updateSettingVariable(config.key, box.isChecked()));
                    table.add(box).left().row();
                }
            }).row();
            addCloseButton();
            show();
        }};
    }
}
