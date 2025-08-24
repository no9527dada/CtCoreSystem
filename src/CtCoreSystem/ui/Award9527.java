package CtCoreSystem.ui;

import arc.Core;
import arc.graphics.Color;
import arc.util.Align;
import arc.util.Time;
import mindustry.Vars;
import mindustry.ui.dialogs.BaseDialog;

import static CtCoreSystem.CtURL.QQ群2;
import static CtCoreSystem.CtURL.爱发电;
//爱发电

public class Award9527 extends BaseDialog {

    public Award9527() {
        super("@love9527");
        addCloseListener();//按esc关闭
        buttons.button("@close", (this::hide)).size(210, 64);
        buttons.button(Core.bundle.format("QQ群2"), (() -> {
            if (!Core.app.openURI(QQ群2)) {
                Vars.ui.showErrorMessage("@linkfail");
                Core.app.setClipboardText(QQ群2);
            }
        })).update(b -> b.color.fromHsv(Time.time % 360, 1, 1)).size(250.0f, 50).row();
        cont.pane(t -> {
            // new Table();
            t.add(Core.bundle.format("juanzengxinxi")).left().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left).center().row();
            t.image().color(Color.valueOf("69dcee")).fillX().height(3).pad(9);//分割线
            t.row();
            t.button(Core.bundle.format("weixin"), (() -> {
                if (!Core.app.openURI(爱发电)) {
                    Vars.ui.showErrorMessage("@linkfail");
                    Core.app.setClipboardText(爱发电);
                }
            })).size(510, 64).row();
            t.add(Core.bundle.format("creators_KHHF")).left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left).row();//捐赠说明
            t.image(Core.atlas.find("ctcoresystem-love2", Core.atlas.find("clear"))).height(240).width(240).pad(3).row();
        }).center().maxWidth(900);
        cont.pane(e -> {
            e.add(Core.bundle.format("creators_DDF")).left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left).row();//捐赠名单标题
            e.image().color(Color.valueOf("69dcee")).fillX().height(3).pad(9).row();//分割线
            e.add(Core.bundle.format("creators_FG")).left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left).row();//捐赠名单


        }).center().maxWidth(900);

    }
}
