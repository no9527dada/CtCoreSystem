package CtCoreSystem.ui.dialogs;

import CtCoreSystem.ui.CTui;
import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.scene.ui.ImageButton;
import arc.scene.ui.Label;
import arc.scene.ui.Slider;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import arc.util.Reflect;
import arc.util.Time;
import creators.Creators;
import mindustry.Vars;
import mindustry.core.UI;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.type.Sector;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import static CtCoreSystem.CtCoreSystem.加载CTTD;
import static CtCoreSystem.CtURL.QQ群2;
import static mindustry.gen.Call.sendChatMessage;
public class 游戏功能按钮 {
    // 添加单位贴图开关方法
    private int currentTeamMode = 0; // 0: none, 1: crux, 2: sharded, 3: all

    // 添加游戏变速方法
    private void setupSpeedTable(Table table) {
        // 定义速度颜色
        Color colorSpeedUp = Color.valueOf("ffd59e");
        Color colorSpeedDown = Color.valueOf("99ffff");

        // 创建滑块，范围从-3到2，步长为1
        Slider slider = new Slider(-3f, 2f, 1f, false);
        // 创建速度标签
        Label label = new Label("");

        // 设置滑块移动事件处理
        slider.changed(() -> {
            float value = slider.getValue();
            float speed = (float) Math.pow(2, value);

            // 设置游戏速度
            Time.setDeltaProvider(() -> Math.min(Core.graphics.getDeltaTime() * 60 * speed, 3 * speed));

            // 更新速度标签文本
            label.setText(getText(value));

            // 只在联机模式下，并且是主机时发送聊天消息 防止客机刷屏
            if (Vars.net.active() && Vars.net.server()) {
                // 向聊天框发送消息：房主已将游戏速度调至X倍
                sendChatMessage("[yellow]我已将游戏速度调至" + speed + "倍");
            }
        });

        // 初始化滑块值为0
        slider.setValue(0);
        label.setText(getText(0)); // 初始化标签文本

        // 将组件添加到表格
        table.add(label).width(52);

        // 添加重置按钮
        table.button(Icon.refresh, Styles.clearNonei, 30, () -> {
            slider.setValue(0);
        }).size(40).padLeft(6);

        // 添加滑块
        table.add(label).width(50);
        table.add(slider).growX();
    }

    // 辅助方法：根据速度值生成显示文本
    private String getText(float value) {
        float speed = (float) Math.pow(2, Math.abs(value));
        String text = "";

        if (value == 0) {
            text = "x";
        } else if (value > 0) {
            text = "[#F5F132]x"; // 加速状态 - 黄色
        } else {
            text = "[#5DDC74]x"; // 减速状态 - 绿色
        }

        return text + speed;
    }

    private void setupTeamSlider(Table table) {
        // 创建滑块，范围从0到3，步长为1
        Slider slider = new Slider(0f, 3f, 1f, false);
        // 创建队伍模式标签
        Label label = new Label("", Styles.outlineLabel);

        // 设置滑块移动事件处理
        slider.changed(() -> {
            currentTeamMode = (int) slider.getValue();

            // 清除所有单位渲染
            clearAllUnitsFromDrawGroup();

            // 根据选择的模式显示对应队伍的单位
           switch (currentTeamMode) {
                case 0:// all 显示全部
                    // 遍历Team.all数组中的所有队伍，而不是尝试直接获取Team.all
                    for (Team team : Team.all) {
                        if (team != null) { // 确保队伍不为null
                            Vars.state.teams.get(team).units.each(unit -> Groups.draw.add(unit));
                        }
                    }
                    break;
                case 1:  //显示 sharded (黄队)
                    Vars.state.teams.get(Team.sharded).units.each(unit -> Groups.draw.add(unit));
                    break;
                case 2://显示 crux (红队)
                        Vars.state.teams.get(Team.crux).units.each(unit -> Groups.draw.add(unit));
                    break;
                case 3: //全隐藏
                    break;
            }
            /*switch (currentTeamMode) {
                case 0:// all 显示全部
                    for (Team team : Team.all) {
                        if (team != null) { // 确保队伍不为null
                            Vars.state.teams.get(team).units.each(unit -> Groups.draw.add(unit));
                        }
                    }
                    break;
                case 1:  //显示 sharded (黄队)
                    Events.run(EventType.Trigger.update, () ->{Vars.state.teams.get(Team.crux).units.each(unit -> Groups.draw.remove(unit));});
                   Vars.state.teams.get(Team.sharded).units.each(unit -> Groups.draw.add(unit));
                    break;
                case 2://显示 crux (红队)
                    Events.run(EventType.Trigger.update, () ->{Vars.state.teams.get(Team.sharded).units.each(unit -> Groups.draw.remove(unit));});
                   Vars.state.teams.get(Team.crux).units.each(unit -> Groups.draw.add(unit));
                    break;
                case 3: //全隐藏
                    // 清除所有队伍的单位
                    for (Team team : Team.all) {
                        Events.run(EventType.Trigger.update, () ->{Vars.state.teams.get(team).units.each(unit -> Groups.draw.remove(unit));});
                    }
                    break;
            }*/
            // 更新标签文本
            label.setText(getTeamModeText(currentTeamMode));
        });

        // 初始化滑块值为0
        slider.setValue(currentTeamMode);
        label.setText(getTeamModeText(currentTeamMode));

        // 将组件添加到表格
        table.add(label).width(50);
        table.add(slider).growX();
    }

    // 辅助方法：根据队伍模式生成显示文本
    private String getTeamModeText(int mode) {
        switch (mode) {
            case 0:
                return "[#66ff66]显示";
            case 1:
                return "[#ff6666]红队";
            case 2:
                return "[#ffff66]黄队";
            case 3:
                return "[#ff4444]全部";
            default:
                return "未知";
        }
    }

    // 辅助方法：清除所有单位从绘制组
    private void clearAllUnitsFromDrawGroup() {
        // 清除所有队伍的单位
        for (Team team : Team.all) {
            Vars.state.teams.get(team).units.each(unit -> Groups.draw.remove(unit));
        }
    }


    public void addToHud() {
        ImageButton.ImageButtonStyle kaite = new ImageButton.ImageButtonStyle();
        kaite.down = Tex.buttonDown;
        kaite.up = Styles.black3;
        kaite.over = Tex.buttonOver;
        kaite.imageDisabledColor = Color.gray;
        kaite.imageUpColor = Color.white;
        kaite.disabled = Tex.buttonDisabled;

        Vars.ui.hudGroup.fill(table -> {
            BaseDialog locloseAnAccounve = new BaseDialog("");
            locloseAnAccounve.buttons.button("@close", () -> {
                locloseAnAccounve.hide();
            }).size(210, 64);
            locloseAnAccounve.addCloseListener();//按esc关闭
            locloseAnAccounve.cont.pane((e -> {


                e.add(Core.bundle.format("locloseAnAccounve")).left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);
                e.row();
                e.image().color(Color.valueOf("69dcee")).fillX().height(3).pad(9);
                e.row();
                e.button("", () -> {
                }).size(300, 64).update(buttonn -> {

                    Object turnCounter = Reflect.get(Vars.universe, "turnCounter");

                    float ticks = Vars.turnDuration - ((Number) turnCounter).floatValue();
                    buttonn.setText(Core.bundle.get("jiesuan") + UI.formatTime(ticks));
                });
                e.row();
                e.button("@tongji2", Icon.info, () -> {
                    Sector sector = Vars.state.getSector();
                    if (sector != null && sector.save != null) {
                        Reflect.invoke(CTui.CTplanet, "showStats", new Object[]{Vars.state.getSector()}, Sector.class);
                        // CTui.CTplanet, "showStats", [Vars.state.getSector()], Sector
                    }
                }).size(250, 64).padLeft(-400).padTop(20);
                e.button(Core.bundle.format("gonglue"), () -> {
                    if (!Core.app.openURI(QQ群2)) {
                        Vars.ui.showErrorMessage("@linkfail");
                        Core.app.setClipboardText(QQ群2);
                    }
                }).size(250, 64).padLeft(-400).padTop(20);
            })).grow().center().maxWidth(770);
//--------------------------
            boolean[] shown = {false};
            table.button(Icon.downSmall, Styles.defaulti, () -> {
                shown[0] = !shown[0];
            }).checked(b -> shown[0]).size(40).left().row();

            table.collapser(t -> {
                t.top().left();


                Table buttons = t.table().left().get();

                // 添加按钮的方法
                Runnable addButton = () -> {

                    // 首页按钮
                    buttons.button(Icon.home, kaite, () -> {
                        CT3function.功能图标UI.show();
                    }).size(46).tooltip(Core.bundle.get("9527shouye")).padRight(4);

                    // 刷新按钮
                    buttons.button(Icon.refresh, kaite, () -> {
                        sendChatMessage("/sync");
                        //  Vars.netServer.sendChatMessage("/sync");
                    }).size(46).tooltip(Core.bundle.get("refresh")).padRight(4);

                    // 蓝图按钮
                    if (Vars.mods.getMod("creators") != null) {
                        buttons.button(Icon.book, Styles.clearTogglei, () -> {
                            Creators.CTBlockBool = !Creators.CTBlockBool;
                        }).size(46).tooltip(Core.bundle.get("9527lantu")).padRight(4);
                    }

                    // 全局特效开关
                    buttons.button(Icon.eye, Styles.clearTogglei, () -> {
                        boolean currentValue = Core.settings.getBool("effects", true);
                        Core.settings.put("effects", !currentValue);
                    }).size(46).tooltip(Core.bundle.get("NOFF")).padRight(4);

                    // 后台结算按钮
                    buttons.button(Icon.save, kaite, () -> {
                        locloseAnAccounve.show();
                    }).size(46).tooltip(Core.bundle.get("jiesuan")).padRight(4);

                    // 统计按钮
                    buttons.button(Icon.info, kaite, () -> {
                        Sector sector = Vars.state.getSector();
                        if (sector != null && sector.save != null) {
                            Reflect.invoke(Vars.ui.planet, "showStats", new Object[]{sector}, Sector.class);
                        }
                    }).size(46).tooltip(Core.bundle.get("tongji")).padRight(4);
                };
                // 执行添加按钮
                addButton.run();
                t.row();
                // 添加游戏变速滑块
                t.table(Styles.black6, speedTable -> {
                    setupSpeedTable(speedTable);
                }).growX();
                t.row();
                if (加载CTTD()) {//加载塔防不显示队伍切换滑块
                    // 添加队伍切换滑块
                    t.table(Styles.black6, teamTable -> {
                        teamTable.add("单位隐藏: ").color(Color.lightGray);
                        setupTeamSlider(teamTable);
                    }).growX();
                }
            }, false, () -> shown[0]).left();
            table.top().left().marginTop(110);
            Vars.ui.hudGroup.fill(cundang -> {
                // 存档功能实现
                if (Vars.mods.locateMod("auto_saver") == null) {
                    // 原版的存档方式
                    cundang.button(Icon.upload, Styles.defaulti, () -> {
                        if (Vars.ios) {
                            try {
                                Core.files.local("mindustry-data-export.zip");
                                Vars.ui.settings.exportData(Core.files.local("mindustry-data-export.zip"));
                                Vars.platform.shareFile(Core.files.local("mindustry-data-export.zip"));
                            } catch (Exception e) {
                                Vars.ui.showException(e);
                            }
                        } else {
                            Vars.platform.showFileChooser(false, "zip", file -> {
                                try {
                                    Vars.ui.settings.exportData(file);
                                    Vars.ui.showInfo("@data.exported");
                                } catch (Exception e) {
                                    // 异常处理保持原有逻辑
                                }
                            });
                        }
                    }).width(40).height(40).name("ores").tooltip("@data.export");
                } else {
                    // 自动存档方式
                    try {
                        Object mod = Vars.mods.locateMod("auto_saver");
                        if (mod == null || Reflect.get(mod, "main") == null) return;
                        Object dialog = Reflect.get(Reflect.get(mod, "main"), "recoverDialog");
                        cundang.button(Icon.upload, Styles.defaulti, () -> {
                            try {
                                Reflect.invoke(dialog, "show");
                            } catch (Exception e) {
                                Vars.ui.showException(e);
                            }
                        }).width(40).height(40).name("ores").tooltip("@data.export");
                    } catch (Exception e) {
                        Vars.ui.showException(e);
                    }
                }
                cundang.top().left().marginTop(110).marginLeft(40);
            });


        });
    }
}
