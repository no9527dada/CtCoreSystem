package CtCoreSystem.CoreSystem.type.VXV;

import arc.Core;
import arc.scene.event.Touchable;
import arc.scene.ui.Label;
import arc.struct.Seq;
import mindustry.core.UI;
import mindustry.ui.Styles;
import mindustry.world.blocks.logic.MessageBlock;
import mindustry.world.meta.BlockGroup;

public class powerShowBlock extends MessageBlock {
    //寄存电力显示器建筑
    public static Seq<powerShowBuild> powerShowBuild = new Seq<>();

    //加载显示到scene
    public static void loadPowerShow() {
        Core.scene.find("minimap/position").parent.fill(t -> {
            Label label = new Label("");
            label.update(() -> {
                label.setText(() -> {
                    StringBuilder text = new StringBuilder();

                    for (var build : powerShowBuild) {
                        text.append("<").append(build.message.toString()).append("> ").append(Core.bundle.get("category.power") + ": ");
                        text.append(build.power.graph.getPowerBalance() > 0 ? "+" : "").append(UI.formatAmount((long) (build.power.graph.getPowerBalance() * 60.0F)));
                        text.append(Core.bundle.format(
                                "bar.powerstored",
                                UI.formatAmount((long) build.power.graph.getLastPowerStored()),
                                UI.formatAmount((long) build.power.graph.getLastCapacity())
                        ));
                        /*
                        if (build.power.graph.getLastCapacity() == 0) {
                            text.append("0");
                        } else {
                            text.append(UI.formatAmount((long) build.power.graph.getLastPowerStored()));
                            text.append("/");
                            text.append(UI.formatAmount((long) build.power.graph.getLastCapacity()));
                        }*/
                        text.append("\n");
                    }

                    return text;
                });
            });
            t.row();
            t.add(label).touchable(Touchable.disabled).style(Styles.outlineLabel);
            t.top().right();//显示的位置
/*

            t.top().left();//左上角
            t.center();//居中
            t.bottom().right();//右下角
            t.top().right().pad(10);//右上角带10像素边距
*/

        });
    }

    public powerShowBlock(String name) {
        super(name);

        update = true;
        solid = true;
        hasPower = true;
        group = BlockGroup.power;
        consumesPower = false;
        outputsPower = false;

        buildType = powerShowBuild::new;
    }

    public class powerShowBuild extends MessageBuild {
        @Override
        public void add() {
            super.add();

            if (message.length() == 0) {
                message.append("未定义");
            }
            powerShowBuild.add(this);
        }

        @Override
        public void remove() {
            super.remove();
            powerShowBuild.remove(this);
        }
    }
}
