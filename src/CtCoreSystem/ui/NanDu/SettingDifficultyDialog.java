package CtCoreSystem.ui.NanDu;
/*
 *@Author:LYBF
 *@Date  :2023/12/24
 */

import CtCoreSystem.mfxiao.ActivateProgram;
import arc.Core;

import arc.graphics.Color;
import arc.scene.event.Touchable;
import arc.scene.ui.Dialog;
import arc.scene.ui.Label;
import arc.scene.ui.Slider;
import arc.scene.ui.Tooltip;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import mindustry.type.Planet;
import mindustry.ui.Styles;

import static CtCoreSystem.CtCoreSystem.toText;
import static CtCoreSystem.CtCoreSystem.主动关闭激活;
import static arc.Core.bundle;
//难度 普通版
public class SettingDifficultyDialog extends Dialog {

    public static Table container;
    Planet planet;
    Table current;
    public SettingDifficultyDialog(boolean 加载CT2) {

        //标题名
        super(bundle.get("settings", "Settings"));
        container = new Table();
        container.row();
        addChangeDiffcutySlider(加载CT2);
        container.setWidth(Math.min(Core.graphics.getWidth() / 1.2f, 480f));
        container.setHeight(Math.min(Core.graphics.getHeight() / 1.2f, 900f));
        add(container);
        container.row();
        container.button("关闭", this::hide).width(100).padTop(20);
    }
     public void addChangeDiffcutySlider(boolean 加载CT2) {
        // CampaignRules rules = planet.campaignRules;
         Table table = new Table();
         Slider slider = (new Slider(1, 加载CT2 ? 6 : 4, 1, false));
         slider.setValue(Core.settings.getInt("游戏难度"));

         // 添加鼠标悬停提示
         slider.addListener(new Tooltip(t -> {
             t.background(Styles.black3); // 保持tooltip整体背景
             // 为文本添加底色
             Table textContainer = new Table();
             textContainer.background(Styles.black5); // 使用黑色半透明背景
             textContainer.add("非激活版每次启动使用正常难度").color(Color.white).pad(4); // 添加内边距
             t.add(textContainer);
         }));

         Label value = new Label("", Styles.outlineLabel);
         Table content = new Table();
         content.add("难度设置", Styles.outlineLabel).left().growX().wrap();
         content.add(value).padLeft(10f).right();
         content.margin(3f, 33f, 3f, 33f);
         content.touchable = Touchable.disabled;
         slider.changed(() -> {
             //滑动时触发
             int value1 = (int) slider.getValue();
             value.setText(bundle.get( (加载CT2 ? "" : "CT3") + "Difficulty-" + value1));
             //保存难度
             Core.settings.put("游戏难度", value1);
         });
         slider.change();
         table.stack(slider, content).width(Math.min(Core.graphics.getWidth() / 1.2f, 460f)).center().padTop(4f).get();
         table.row();
         if (加载CT2) {
             table.image(Core.atlas.find("ctcoresystem-nandu2")).height(185).width(450).pad(3);//难度图片公示
         } else {
             table.image(Core.atlas.find("ctcoresystem-nandu3")).height(185).width(445).pad(3);//难度图片公示
         }
         table.row();
         table.image().color(Color.valueOf("69dcee")).fillX().height(3).pad(9);
         table.row();
         table.add(  加载CT2 ?toText("Dc2.txt"):toText("Dc3.txt")).left().growX().wrap().width(400).maxWidth(400).pad(4).row();
         table.add(toText("TD难度调整说明")).center().growX().wrap().width(500).maxWidth(500).pad(4).labelAlign(Align.center).row();
         container.add(table);
     }

}
