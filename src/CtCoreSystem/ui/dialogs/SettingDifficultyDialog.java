package CtCoreSystem.ui.dialogs;
/*
 *@Author:LYBF
 *@Date  :2023/12/24
 */
//难度
import arc.Core;
import arc.func.Floatc;
import arc.scene.event.Touchable;
import arc.scene.ui.Dialog;
import arc.scene.ui.Label;
import arc.scene.ui.Slider;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import mindustry.ui.Styles;

import java.util.Objects;

import static arc.Core.bundle;

public class SettingDifficultyDialog extends Dialog {

    private Table container;

    public SettingDifficultyDialog() {
        //标题名
        super(bundle.get("settings", "Settings"));
        container = new Table();
        container.row();
        addChangeDiffcutySlider();
        container.setWidth(Math.min(Core.graphics.getWidth() / 1.2f, 480f));
        container.setHeight(Math.min(Core.graphics.getHeight() / 1.2f, 900f));
        add(container);
        container.row();
        container.button("关闭", this::hide).width(100).padTop(20);

    }


    private void addChangeDiffcutySlider() {
        Table table = new Table();
        Slider slider = new Slider(1, 4, 1, false);
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
            value.setText(bundle.get(("CT3Difficulty-" + value1)));
            if (!Objects.isNull(difficutyValueChangeListener)) {
                difficutyValueChangeListener.get(slider.getValue());
            }
            //保存难度
            Core.settings.put("游戏难度", value1);
        });
        slider.change();
        table.stack(slider, content).width(Math.min(Core.graphics.getWidth() / 1.2f, 460f)).left().padTop(4f).get();
        table.row();
        table.add(Core.bundle.get("TD难度调整说明")).left().growX().wrap().width(200).maxWidth(200).pad(4).labelAlign(Align.center).row();
        container.add(table);
    }

    private Floatc difficutyValueChangeListener;

    /*
     *监听难度修改事件
     */
    public SettingDifficultyDialog onDifficutyChange(Floatc listener) {
        this.difficutyValueChangeListener = listener;
        return this;
    }
}
