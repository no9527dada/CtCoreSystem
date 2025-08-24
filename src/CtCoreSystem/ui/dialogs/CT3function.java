package CtCoreSystem.ui.dialogs;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Reflect;
import arc.util.Time;
import mindustry.Vars;
import mindustry.core.UI;
import mindustry.graphics.Pal;
import mindustry.mod.Mods;
import mindustry.ui.Fonts;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.JoinDialog;
import mindustry.ui.dialogs.ModsDialog;

import static CtCoreSystem.CoreSystem.type.CTColor.C;
import static CtCoreSystem.CtURL.QQ群2;
import static CtCoreSystem.ui.dialogs.CT3InfoDialog.ct3info;
import static mindustry.Vars.turnDuration;
import static mindustry.Vars.universe;
import static mindustry.gen.Tex.windowEmpty;
import static mindustry.ui.Styles.black;

//首页功能图标UI
public class CT3function {
    public static BaseDialog 功能图标UI;

    public static void show() {
        Mods.LoadedMod mod = Vars.mods.getMod("ctcoresystem");
        String version = mod.meta.version;

        功能图标UI = new BaseDialog("[yellow]Creators[#7bebf2] " + version + "[] Adapt 146+" + "\n策划:9527，贴图:皴皲，处理器逻辑指导:咕咕点心\nQQ群:909130592") {{
            addCloseListener();//按esc关闭
            buttons.defaults().size(210, 64);
            buttons.button("@close", (this::hide)).size(100, 64);//关闭按钮
            setStyle(
                    new DialogStyle() {{
                        stageBackground = black;// Tmp.c1.set(Color.white).lerp(Pal.remove, Mathf.absin(2f, 1f)),Mathf.absin(5f, 1f))));
                        titleFont = Fonts.def;
                        background = windowEmpty;
                        titleFontColor = Pal.accent;

                    }}
            );
            cont.pane(t -> {
                int l = -180;
                //结算
                t.button("", () -> {
                }).size(300, 64).update(button -> {
                    float turnCounter = Reflect.get(universe, "turnCounter");
                    float ticks = turnDuration - turnCounter;
                    button.setText(Core.bundle.get("jiesuan") + "[#fff35b]" + UI.formatTime(ticks));
                });
                t.row();
                t.button("Online", (() -> {
                    new JoinDialog().show();//联机
                    功能图标UI.hide();
                })).size(120, 50).padLeft(l);

                t.button("@loadgame", (() -> {
                    Vars.ui.load.show();//加载游戏
                    功能图标UI.hide();
                })).size(150, 50).padLeft(l);
                t.row();
                t.button("@planets", (() -> {
                    Vars.ui.planet.show();//战役星球界面
                    功能图标UI.hide();
                })).size(120, 50).padLeft(l);

                t.button("@customgame", (() -> {
                    Vars.ui.custom.show();//自定义
                    功能图标UI.hide();
                })).size(120, 50).padLeft(l);
                t.row();

                t.button(Core.bundle.format("mods"), (() -> {
                    new ModsDialog().show();//模组
                })).size(120, 50).padLeft(l);

                t.button(Core.bundle.format("9527shouye"), (() -> {
                    ct3info.show();//首页
                })).size(120, 50).padLeft(l).update(i ->
                        i.getLabel().setColor(new Color().set(Color.white).lerp(C("fd5bff"),
                                Mathf.absin(5f, 1f)))
                ).row();

            }).grow().center().width(900).maxWidth(900).row();

            button(Core.bundle.format("QQ群2"), (() -> {
                if (!Core.app.openURI(QQ群2)) {
                    Vars.ui.showErrorMessage("@linkfail");
                    Core.app.setClipboardText(QQ群2);
                }
            }))
                    .update(b -> b.color.fromHsv(Time.time % 360, 1, 1)).size(250.0f, 50).row();
        }};
    }


}