package CtCoreSystem.ui.NanDu;
/*
 *@Author:LYBF
 *@Date  :2023/12/24
 */
//难度
import CtCoreSystem.mfxiao.ActivateProgram;
import arc.Core;

import arc.graphics.Color;
import arc.scene.event.Touchable;
import arc.scene.ui.Dialog;
import arc.scene.ui.Label;
import arc.scene.ui.Slider;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import mindustry.type.Planet;
import mindustry.ui.Styles;

import static CtCoreSystem.CtCoreSystem.主动关闭激活;
import static arc.Core.bundle;

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

        /* 未激活状态：难度范围1-4级（CT2模式为1-6级）
         *已激活状态：（CT2模式为1-4级）
         */
         //主动关闭激活 == true
       //  Slider slider = !ActivateProgram.isActivated ? new Slider(1, 加载CT2 ? 6 : 4, 1, false) : new Slider(1, 加载CT2 ? 4 : 4, 1, false);


         Slider slider = (!ActivateProgram.isActivated || 主动关闭激活)
                 ? new Slider(1, 加载CT2 ? 6 : 4, 1, false)
                 : new Slider(1, 加载CT2 ? 4 : 4, 1, false);

         slider.setValue(Core.settings.getInt("游戏难度"));

         Label value = new Label("", Styles.outlineLabel);
         Table content = new Table();
         content.add("难度设置", Styles.outlineLabel).left().growX().wrap();
         content.add(value).padLeft(10f).right();
         content.margin(3f, 33f, 3f, 33f);
         content.touchable = Touchable.disabled;
         slider.changed(() -> {
             //滑动时触发
             int value1 = (int) slider.getValue();

             value.setText(bundle.get(
                     (!ActivateProgram.isActivated || 主动关闭激活) ? ((加载CT2 ? "" : "CT3") + "Difficulty-" + value1) : ((加载CT2 ? "isActivated" : "CT3") + "Difficulty-" + value1)

             ));


             //保存难度
             Core.settings.put("游戏难度", value1);
         });
         slider.change();
         table.stack(slider, content).width(Math.min(Core.graphics.getWidth() / 1.2f, 460f)).center().padTop(4f).get();
         table.row();
         if (!ActivateProgram.isActivated) {
             table.image(Core.atlas.find(加载CT2 ? "ctcoresystem-nandu2" : "ctcoresystem-nandu3")).height(185).width(445).pad(3);//难度图片公示
         }else {
             if (主动关闭激活)
             {

                 if (加载CT2) {
                     table.image(Core.atlas.find("ctcoresystem-nandu2")).height(185).width(450).pad(3);//难度图片公示
                 } else {
                     table.image(Core.atlas.find("ctcoresystem-nandu3")).height(185).width(445).pad(3);//难度图片公示
                 }

             }else {
                 if (加载CT2) {
                     table.image(Core.atlas.find("ctcoresystem-nandu2+")).height(125).width(450).pad(3);//难度图片公示
                 } else {
                     table.image(Core.atlas.find("ctcoresystem-nandu3")).height(185).width(445).pad(3);//难度图片公示
                 }
             }


         }
         table.row();
         table.image().color(Color.valueOf("69dcee")).fillX().height(3).pad(9);
         table.row();
         table.add( /*ActivateProgram.isActivated  ? "[yellow]当前为CT2难度([#ff0000]激活模式[]）" :*/ 加载CT2 ?"[yellow]当前为CT2难度":"[yellow]当前为CT3难度").left().growX().wrap().width(200).maxWidth(200).pad(4).row();
         table.add(Core.bundle.get("TD难度调整说明")).center().growX().wrap().width(500).maxWidth(500).pad(4).labelAlign(Align.center).row();
/*         cont.pane((e -> {
                     e.check("@rules.fog", b -> rules.fog = b);
                     e.check("@rules.showspawns", b -> rules.showSpawns = b);
                     e.check("@rules.randomwaveai", b -> rules.randomWaveAI = b);
                     if (planet.allowSectorInvasion) {
                         e.check("@rules.invasions", b -> rules.sectorInvasion = b);
                     }
                     if (planet.showRtsAIRule) {
                         e.check("@rules.rtsai.campaign", b -> rules.rtsAI = b);
                     }
                 }));*/


         container.add(table);
     }

}
