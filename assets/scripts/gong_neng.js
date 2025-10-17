Events.on(EventType.ClientLoadEvent, cons(e => {

    /*    var dialog = new BaseDialog("[yellow]Creators[#7bebf2] " + "[] Adapt 140+" );
        dialog.buttons.defaults().size(210, 64);
        dialog.cont.pane(cons(t => {
        }));
    
           //首页主功能按钮
     const imagebutton = CTRebirth.CreatorsIcon("function", Styles.defaulti, dialog)
     Vars.ui.menuGroup.fill(cons(t => {
         if (Vars.mobile) {
             t.add(imagebutton).size(80.0);
             t.bottom();
         } else {
             t.add(imagebutton).size(80.0);
             t.left().bottom()
         };
     }));*/




    const kaite = new ImageButton.ImageButtonStyle();
    kaite.down = Tex.buttonDown;
    kaite.up = Styles.black3;
    kaite.over = Tex.buttonOver;
    kaite.imageDisabledColor = Color.gray;
    kaite.imageUpColor = Color.white;
    kaite.disabled = Tex.buttonDisabled;

    Vars.ui.hudGroup.fill(cons(table => {

        var locloseAnAccounve = new BaseDialog("");
        locloseAnAccounve.buttons.button("@close", run(() => {
            locloseAnAccounve.hide();
        })).size(210, 64);
        locloseAnAccounve.addCloseListener();//按esc关闭

        locloseAnAccounve.cont.pane((() => {
            var table = new Table();
            table.add(Core.bundle.format("locloseAnAccounve")).left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);
            table.row();
            table.image().color(Color.valueOf("69dcee")).fillX().height(3).pad(9);
            table.row();
            table.button("", () => { }).size(300, 64).update(buttonn => {
                let turnCounter = Reflect.get(Vars.universe, "turnCounter");
                let ticks = Vars.turnDuration - turnCounter;
                buttonn.setText(Core.bundle.get("jiesuan") + UI.formatTime(ticks));
            })
            table.row();
            table.button("@tongji2", Icon.info, run(() => {
                let sector = Vars.state.getSector();
                if (sector != null && sector.save != null) {
                    Reflect.invoke(CTui.CTplanet, "showStats", [Vars.state.getSector()], Sector);
                    // CTui.CTplanet, "showStats", [Vars.state.getSector()], Sector
                }
            })).size(250, 64).padLeft(-400).padTop(20);
            table.button(Core.bundle.format("gonglue"), run(() => {
                if (!Core.app.openURI(https4)) {
                    Vars.ui.showErrorMessage("@linkfail");
                    Core.app.setClipboardText(https4);
                }
            })).size(250, 64).padLeft(-400).padTop(20);
            /*         table.button(Icon.info, kaite, () => {
                        let sector = Vars.state.getSector();
                        if (sector != null&&sector.save != null) {
                            Reflect.invoke(CTui.CTplanet, "showStats", [Vars.state.getSector()], Sector);
                           // CTui.CTplanet, "showStats", [Vars.state.getSector()], Sector
                        }
                    }, null, "tongji"); //统计  */
            return table;
        })()).grow().center().maxWidth(770);
        //-------------------------------------------
        let shown = false;
        table.button(Icon.downSmall, Styles.defaulti, () => {
            shown = !shown;
        }).checked(b => shown).size(40).left().row();
        table.collapser(t => {
            t.top().left();
            let buttons = t.table().left().get();
            function addButton(icon, style, runnable, checked, tooltipName) {
                let cell = buttons.button(icon, style, 36, runnable).size(46).tooltip(Core.bundle.get(tooltipName));
                if (checked != null) {
                    cell.checked(checked);
                }
            }
            addButton(Icon.home, kaite, () => {
                CT3function.功能图标UI.show();
            }, null, "9527shouye"); // 首页

            addButton(Icon.refresh, kaite, () => {
                Call.sendChatMessage("/sync")
            }, null, "refresh"); // 刷新

            if (Vars.mods.locateMod("creators") != null) {
                addButton(Icon.book, Styles.clearTogglei, () => {
                    Creators.CTBlockBool = !Creators.CTBlockBool;
                }, b => Creators.CTBlockBool, "9527lantu"); // 蓝图
            }
            addButton(Icon.eye, Styles.clearTogglei, () => {
                let c = Core.settings.get("effects", true);
                Core.settings.put("effects", !c);
            }, b => Core.settings.get("effects", true), "NOFF"); // 全局特效开关
            addButton(Icon.save, kaite/* Styles.defaulti */, () => {
                locloseAnAccounve.show()
            }, null, "jiesuan"); //后台结算
            addButton(Icon.info, kaite, () => {
                let sector = Vars.state.getSector();
                if (sector != null && sector.save != null) {
                    Reflect.invoke(Vars.ui.planet, "showStats", [sector], Sector);
                }
            }, null, "tongji"); //统计 
            t.row();
            //加速条
            t.table(Styles.black6, speedTable => {
                setupSpeedTable(speedTable);
            }).growX();


        }, false, () => shown).left();
        table.top().left().marginTop(110);

    }));
    Vars.ui.hudGroup.fill(cons(cundang => {//存档 
        if (
            //在加载了自动存档模组后执行自动存档
            Vars.mods.locateMod("auto_saver") == null
        ) {
            //原版的存档方式
            function exportData(fi) {
                Vars.ui.settings.exportData(fi)
            }
            cundang.button(Icon.upload, Styles.defaulti, run(() => {
                if (Vars.ios) {
                    let file = Core.files.local("mindustry-data-export.zip");
                    try {
                        exportData(file);
                    } catch (e) {
                        Vars.showException(e)
                    }
                    Vars.platform.shareFile(file)
                } else {
                    Vars.platform.showFileChooser(false, "zip", file => {
                        try {
                            exportData(file);
                            Vars.showInfo("@data.exported");
                        } catch (e) {
                            // e.printStackTrace()
                            // Vars.showException(e)
                        }
                    });
                }
            })).width(40).height(40).name("ores").tooltip("@data.export");

        } else {

            //自动存档方式
            let mod = Vars.mods.locateMod("auto_saver");
            if (mod.main == null) return;
            let dialog = Reflect.get(mod.main, "recoverDialog");
            cundang.button(Icon.upload, Styles.defaulti, run(() => {
                dialog.show();
            })).width(40).height(40).name("ores").tooltip("@data.export");
            //  Reflect.invoke(?????)
        }
        cundang.top().left().marginTop(110).marginLeft(40);
    }));
}));
function setupSpeedTable(table) {
    function getText(value) {
        let speed = Math.pow(2, Math.abs(value));

        let text = "";
        let color = null;

        if (value == 0) {
            text = "x";
        } else if (value > 0) {
            text = "{shake}[#F5F132]x";
        } else {
            text = "{wave}[#5DDC74]x";
        }

        return text + speed;
    }
    let slider = new Slider(-3, 2, 1, false);
    let label = new FLabel("");

    let effects = Reflect.get(label, "activeEffects");

    let colorSpeedUp = Color.valueOf("ffd59e");
    let colorSpeedDown = Color.valueOf("99ffff");

    slider.moved(value => {
        let speed = Math.pow(2, value);
        Time.setDeltaProvider(() => Math.min(Core.graphics.getDeltaTime() * 60 * speed, 3 * speed));

        label.restart(getText(value));
    });

    slider.setValue(0);

    table.add(label).width(52);

    table.button(Icon.refresh, Styles.clearNonei, 30, () => {
        slider.setValue(0);
    }).size(40).padLeft(6);

    table.add(slider).growX();
}