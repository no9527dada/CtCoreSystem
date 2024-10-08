package CtCoreSystem.ui;

import arc.Core;
import arc.graphics.Color;
import arc.util.Align;
import mindustry.ui.dialogs.BaseDialog;
//捐赠码

public class Award9527 extends BaseDialog {
    public Award9527() {
        super("@love9527");
        addCloseListener();//按esc关闭
        buttons.button("@close", (this::hide)).size(210, 64);
        cont.pane(t -> {
            // new Table();
            t.add(Core.bundle.format("juanzengxinxi")).left().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left).center().row();
            t.image().color(Color.valueOf("69dcee")).fillX().height(3).pad(9);//分割线
            t.row();
            // t.image(Core.atlas.find("creators-love")).height(955).width(477).pad(3);//捐赠
            t.button(Core.bundle.format("weixin"), (() -> {

                BaseDialog b = new BaseDialog(Core.bundle.format("weixin"));
                b.addCloseListener();//按esc关闭
                b.cont.pane(a -> {
                    a.add(Core.bundle.format("dashangxinxi")).left().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left).center().row();
                    a.image().color(Color.valueOf("69dcee")).fillX().height(3).pad(9).row();//分割线
                    a.image(Core.atlas.find("ctcoresystem-love2")).height(469 / 2f).width(477 / 2f).pad(3).row();//捐赠
                    a.button("@close", (b::hide)).size(100, 64);
                });
                b.show();
            })).size(210, 64).row();
            t.button(Core.bundle.format("zhifubao"), (() -> {

                BaseDialog b = new BaseDialog(Core.bundle.format("zhifubao"));
                b.addCloseListener();//按esc关闭
                b.cont.pane(a -> {
                    a.add(Core.bundle.format("dashangxinxi")).left().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left).center().row();
                    a.image().color(Color.valueOf("69dcee")).fillX().height(3).pad(9).row();//分割线
                    a.image(Core.atlas.find("ctcoresystem-love1")).height(486 / 2f).width(477 / 2f).pad(3).row();//捐赠
                    a.button("@close", (b::hide)).size(100, 64);
                });
                b.show();
            })).size(210, 64);
/*            t.button(Core.bundle.format("weixin"), (() -> {
                if (!Core.app.openURI("https://s1.ax1x.com/2023/08/19/pP8PtkF.png")) {
                    Vars.ui.showErrorMessage("@linkfail");
                    Core.app.setClipboardText("wxp://f2f1S-DZ4GM3wJ4v0GJkZ0FTBBBNtPdJK_NlwauP10acb2LGwLYoJgtovpvnDozPtTMX");
                }
            })).size(210, 64);*/

        }).center().maxWidth(770);

    }
}
