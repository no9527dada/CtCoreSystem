package CtCoreSystem.ui.dialogs;

import CtCoreSystem.mfxiao.激活进入;
import CtCoreSystem.ui.Award9527;
import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.ui.TextButton;
import arc.util.Align;
import arc.util.Time;
import CtCoreSystem.ctUpdateDialog;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.mod.Mods;
import mindustry.ui.dialogs.BaseDialog;

import static CtCoreSystem.CoreSystem.type.CTColor.C;
import static CtCoreSystem.CtURL.*;


//首页 开屏
public class CT3InfoDialog {
    public static BaseDialog ct3info;

    public static void show() {

        // String framer = Core.bundle.format("framer");
        Mods.LoadedMod mod = Vars.mods.getMod("ctcoresystem");
        String version = mod.meta.version;

       // String versionCT =  Vars.mods.getMod("ctcoresystem").meta.version;

       // String versionTD =  Vars.mods.getMod("ctcoresystem").meta.version;

        String MODname = Core.bundle.format("planet.ct3.ModName");


        ct3info = new BaseDialog("[yellow]Creators[#7bebf2] " + version + "[] Adapt 146+" + "\n策划:9527，贴图:皴皲，处理器逻辑指导:咕咕点心\nQQ群:909130592") {{
            //更新检查
            buttons.button(Core.bundle.format("发现更新"), (ctUpdateDialog::show)).size(150, 64);

            addCloseListener();//按esc关闭
            buttons.defaults().size(210, 64);
            buttons.button("@close", (this::hide)).size(100, 64);//关闭按钮
            cont.pane((table -> {
                table.add(MODname).left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);
                table.row();
                //  new SettingsMenuDialog().show();//原版设置界面 现在不需要
                table.image().color(Color.valueOf("69dcee")).fillX().height(3).pad(3);
                table.row();
                if(Vars.mods.locateMod("creators")==null){
                table.image(Core.atlas.find("ctcoresystem-CT-logo", Core.atlas.find("clear"))).height(290).width(587).pad(3).row();
                }else {
                    table.image(Core.atlas.find("ctcoresystem-CT2-logo", Core.atlas.find("clear"))).height(290).width(587).pad(3).row();
                  table.button("起源额外内容激活", () -> {
                        激活进入.show();
                    }).size(280, 64).left().row();
                }

                table.add("更新内容:").left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);
                table.row();

                table.add(Core.bundle.format("ct3-System")+version+":").left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);
                table.row();
                table.add(Core.bundle.format("ct3-SystemTXT")+"\n").left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);
                table.row();
                if(Vars.mods.locateMod("ct")!=null) {
                    table.add(Core.bundle.format("ct3-ct")+ Vars.mods.getMod("ct").meta.version+":").left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);
                    table.row();
                    table.add(Core.bundle.format("ct3-ctTXT")+"\n").left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);
                    table.row();
                }
                if(Vars.mods.locateMod("ct_fantasy_project")!=null) {
                    table.add(Core.bundle.format("ct3-fp") + Vars.mods.getMod("ct_fantasy_project").meta.version+":").left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);
                    table.row();
                    table.add(Core.bundle.format("ct3-fpTXT")+"\n").left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);
                    table.row();
                }
                if(Vars.mods.locateMod("cttd")!=null) {
                    table.add(Core.bundle.format("ct3-td")+  Vars.mods.getMod("cttd").meta.version+":").left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);
                    table.row();
                    table.add(Core.bundle.format("ct3-tdTXT")+"\n").left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);
                    table.row();
                }
                if(Vars.mods.locateMod("creators")!=null) {
                    table.add(Core.bundle.format("ct3-creators")+  Vars.mods.getMod("creators").meta.version+":").left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);
                    table.row();
                    table.add(Core.bundle.format("ct3-creatorsTXT")+"\n").left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);
                    table.row();
                }

                table.add(Core.bundle.format("ct3-notice")).left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);
                table.row();
            })).grow().center().maxWidth(770).row();
            String CT3framer = Core.bundle.format("CT3framer");
            buttons.button(CT3framer, (() -> {
                new BaseDialog("[yellow]Creators[#7bebf2] " + version + "\n" + CT3framer + "\nQQ群:909130592") {{
                    addCloseListener();//按esc关闭
                    buttons.defaults().size(210, 64);
                    buttons.button("@close", (this::hide)).size(100, 64);//关闭按钮
                    cont.pane((table -> {
                        table.add(Core.bundle.format("CT3framer_txt")).left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);
                        table.row();
                        table.button(Core.bundle.format("love9527"), (() -> {
                            new Award9527().show();
                        })).size(510, 64).update(i ->
                                i.getLabel().setColor(new Color().set(Color.white).lerp(C("fd5bff"),
                                        Mathf.absin(2f, 1f)))
                        ).row();
                    }));
                }}.show();
            })).size(150, 64);
            buttons.button("DLC", Icon.github, (() -> {
                new BaseDialog("[yellow]Creators[#7bebf2] " + version + "\n" + CT3framer + "\nQQ群:909130592") {{
                    addCloseListener();//按esc关闭
                    buttons.defaults().size(210, 64);
                    buttons.button("@close", (this::hide)).size(100, 64);//关闭按钮
                    cont.pane((table -> {

                        table.button("@CT3HX", (() -> {//幻想
                            new BaseDialog("[yellow]Creators[#7bebf2] " + version + "\n" + CT3framer + "\nQQ群:909130592") {{
                                addCloseListener();//按esc关闭
                                //defaults().size(210, 64);
                                buttons.button("@close", (this::hide)).size(100, 64);//关闭按钮
                                cont.pane((a -> {
                                    a.add(Core.bundle.format("CT3HX说明")).left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left).row();
                                    a.image().color(Color.valueOf("69dcee")).fillX().height(3).pad(3).row();
                                    a.button(Core.bundle.format("CT3_Git"), (() -> {
                                        if (!Core.app.openURI(FPGit)) {
                                            Vars.ui.showErrorMessage("@linkfail");
                                            Core.app.setClipboardText(FPGit);
                                        }
                                    })).size(250, 50).row();
                                    a.button("夸克网盘", (() -> {
                                        if (!Core.app.openURI(网盘)) {
                                            Vars.ui.showErrorMessage("@linkfail");
                                            Core.app.setClipboardText(网盘);
                                        }
                                    })).size(250, 50).row();
                                    a.button(Core.bundle.format("QQ群2"), (() -> {
                                        if (!Core.app.openURI(QQ群2)) {
                                            Vars.ui.showErrorMessage("@linkfail");
                                            Core.app.setClipboardText(QQ群2);
                                        }
                                    })).update(b -> b.color.fromHsv(Time.time % 360, 1, 1)).size(250.0f, 50).row();
                                }));
                            }}.show();
                        })).size(150, 50);
                        table.button("@CT3TD", (() -> {//塔防
                            new BaseDialog("[yellow]Creators[#7bebf2] " + version + "\n" + CT3framer + "\nQQ群:909130592") {{
                                addCloseListener();//按esc关闭
                                //defaults().size(210, 64);
                                buttons.button("@close", (this::hide)).size(100, 64);//关闭按钮
                                cont.pane((c -> {
                                    c.add(Core.bundle.format("CT3TD说明")).left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left).row();
                                    c.image().color(Color.valueOf("69dcee")).fillX().height(3).pad(3).row();
                                    c.button(Core.bundle.format("CT3_Git"), (() -> {
                                        if (!Core.app.openURI(TDGit)) {
                                            Vars.ui.showErrorMessage("@linkfail");
                                            Core.app.setClipboardText(TDGit);
                                        }
                                    })).size(250, 50).row();
                                    c.button("夸克网盘", (() -> {
                                        if (!Core.app.openURI(网盘)) {
                                            Vars.ui.showErrorMessage("@linkfail");
                                            Core.app.setClipboardText(网盘);
                                        }
                                    })).size(250, 50).row();
                                    c.button(Core.bundle.format("QQ群2"), (() -> {
                                        if (!Core.app.openURI(QQ群2)) {
                                            Vars.ui.showErrorMessage("@linkfail");
                                            Core.app.setClipboardText(QQ群2);
                                        }
                                    })).update(b -> b.color.fromHsv(Time.time % 360, 1, 1)).size(250.0f, 50).row();
                                }));
                            }}.show();
                        })).size(150, 50).row();
                        table.image().color(Color.valueOf("69dcee")).fillX().height(3).pad(3).padLeft(0).row();
                        table.button(Core.bundle.format("QQ群2"), (() -> {
                            if (!Core.app.openURI(QQ群2)) {
                                Vars.ui.showErrorMessage("@linkfail");
                                Core.app.setClipboardText(QQ群2);
                            }
                        })).update(b -> b.color.fromHsv(Time.time % 360, 1, 1)).size(250.0f, 50).padLeft(0).row();

                    /* table.button(Core.bundle.format("TD网盘"), (() -> {
                            if (!Core.app.openURI(TD网盘)) {
                                Vars.ui.showErrorMessage("@linkfail");
                                Core.app.setClipboardText(TD网盘);
                            }
                        })).size(510, 64).row();*/

                    }));
                }}.show();
            })).tooltip("更多可游玩内容").with(b -> {
                TextButton.TextButtonStyle s = new TextButton.TextButtonStyle(b.getStyle());
                s.fontColor = b.color;
                b.setStyle(s);
            }).size(150, 64).update(b -> b.color.fromHsv(Time.time % 360, 1, 1));
        }};
        ct3info.show();
    }
}
