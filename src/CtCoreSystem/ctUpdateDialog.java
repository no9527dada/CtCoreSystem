package CtCoreSystem;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.func.Floatc;
import arc.graphics.Color;
import arc.scene.ui.TextButton;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.Streams;
import arc.util.serialization.Jval;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.mod.Mods;
import mindustry.ui.dialogs.BaseDialog;

import java.util.Objects;

import static CtCoreSystem.CtURL.QQ群2;
import static CtCoreSystem.CtURL.网盘;
import static arc.Core.settings;
import static mindustry.Vars.*;

public class ctUpdateDialog {

    private static BaseDialog ct3info2;
    private static BaseDialog updateDialog;
    private static BaseDialog contentDialog;


    public static String toText(String str) {
        return Core.bundle.format(str);
    }

    //mod的name
    private static final String[] modNames = {
            "ctcoresystem"
            ,"ct"
            , "cttd"
            , "ct_fantasy_project"
            ,"creators"
    };

    //设置github地址
    private static final ObjectMap<String, String> modUrlName = ObjectMap.of(


            modNames[0],"no9527dada/CtCoreSystem"//系统
            ,modNames[1],   "no9527dada/creators3"//主篇
            , modNames[2],  "no9527dada/CreatorTD"//塔防
            ,modNames[3],"no9527dada/CT3FantasyProject"//幻想工程
             ,modNames[4], "no9527dada/CT-origin"//创2
    );

    //检查更新的级别
    //0 需要检查更新
    //1 检查完有更新
    //2 检查完无需更新
    private static int onUpdate = 0;

    //临时存储每一个检查项目的级别
    //-1 ???
    //0 需要更新
    //1 无需更新
    //2 检查错误
    //3 检查错误
    private static final ObjectMap<String, Integer> modUpdateType = new ObjectMap<>();

    //临时存储每一个检查项目的版本
    private static final ObjectMap<String, String> updateTag = new ObjectMap<>();
    //临时存储每一个检查项目的版本上传时间
    private static final ObjectMap<String, String> updateTime = new ObjectMap<>();
    //临时存储每一个检查项目的版本简介
    private static final ObjectMap<String, String> updateBody = new ObjectMap<>();

    //是否在检查更新中
    private static boolean InUpdate = false;

    //临时存储检查项目的计划更新状态 t为计划中 f为无计划
    private static final ObjectMap<String, Boolean> updateIng = new ObjectMap<>();
    //临时存储检查项目有没有 “启动？”
    private static final Seq<String> modNo = new Seq<>();

    private static boolean ModUpdateBool = false;
    private static boolean ModUpdateBoolTemp = false;

    //运行更新系统
    static {
        Events.run(EventType.Trigger.update, () -> {
            if (!InUpdate) return;
            inspect();
        });

        Events.run(EventType.Trigger.update, () -> {
            if (ModUpdateBool) return;

            if (Vars.state.isMenu()) {
                if (!Core.scene.hasDialog()) {
                    ModUpdateBool = true;

                    InUpdate = true;
                    for (var url : modUrlName) {
                        updateIng.put(url.key, true);
                        inspect = 0;

                        modUpdateType.put(url.key, -1);

                        updateTag.put(url.key, "未读取");
                        updateTime.put(url.key, "未读取");
                        updateBody.put(url.key, "未读取");
                    }
                }
            }
        });

        Events.run(EventType.Trigger.update, () -> {
            if (!ModUpdateBoolTemp) return;
            if (InUpdate()) return;

            if (onUpdate == 1) {
                ModUpdateBoolTemp = false;
                updateDialog.show();
            }
        });
    }

    //显示更新页面
    public static void show() {
        ct3info2.show();
    }

    //加载更新系统
    public static void load() {
        boolean bool = true;//settings.getBool("自动检查更新");
        ModUpdateBool = !bool;
        ModUpdateBoolTemp = bool;


        tempKey = new Seq<>();
        tempValue = new Seq<>();

        for (var name : modNames) {
            updateIng.put(name, false);
        }

        for (var url : modUrlName) {
            tempKey.add(url.key);
            tempValue.add(url.value);
        }

        contentDialog = new BaseDialog("Renew Version") {{
            addCloseListener();
            buttons.defaults().size(210, 64);
        }};

        updateDialog = new BaseDialog("Renew Version") {{
            addCloseListener();//按esc关闭
            buttons.defaults().size(210, 64);
            buttons.button("@close", (this::hide)).size(100, 64);//关闭按钮

            cont.pane((table -> {
                table.add(Core.bundle.get("点击内容")).left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left).row();
                table.image().color(Color.valueOf("69dcee")).fillX().height(3).pad(3).row();

                for (var i = 0; i < tempKey.size; i++) {
                    var value = tempValue.get(i);
                    var key = tempKey.get(i);

                    var mod = Vars.mods.getMod(key);
                    String displayName;

                    if (mod != null) {
                        displayName = mod.meta.displayName;
                    } else {
                        displayName = key + Core.bundle.get("未安装");
                        modNo.add(key);
                    }

                    TextButton update = new TextButton(displayName);
                    update.update(() -> {
                        var type = modUpdateType.get(key);

                        switch (type) {
                            case -1 -> update.setText(displayName + "[gray](???)[]");
                            case 0 -> {
                                if (modNo.contains(name)) {
                                    if (settings.getBool("mod-" + name + "-enabled")) {
                                        update.setText(displayName + Core.bundle.get("未下载模组"));
                                    } else {
                                        update.setText(displayName + Core.bundle.get("模组未启用"));
                                    }
                                } else {
                                    update.setText(displayName + Core.bundle.get("需要更新"));
                                }
                            }
                            case 1 -> update.setText(displayName + Core.bundle.get("无需更新"));
                            case 2, 3 -> update.setText(displayName + Core.bundle.get("检查出错"));
                        }
                        update.setDisabled(type != 0);
                    });
                    update.changed(() -> {
                        getUpdate(mod, key, value, mod != null && mod.isJava());
                    });

                    table.add(update).size(510, 64).row();
                }
            }));
        }};


        ct3info2 = new BaseDialog("Renew Version") {
            private float leave = 5f * 60;
            private boolean canClose = false;

            @Override
            public void hide() {
                super.hide();
                onUpdate = 0;
            }

            {
                update(() -> {
                    leave -= Time.delta;
                    if (leave < 0 && !canClose) {
                        canClose = true;
                    }
                });
                // addCloseListener();//按esc关闭
                buttons.defaults().size(210, 64);
                buttons.button("", this::hide).update(b -> {
                    b.setDisabled(!canClose);
                    b.setText(canClose ? toText("close") : toText("close") + "[accent]" + Math.floor(leave / 60) + "[]s");
                }).size(140f, 50f).center();

                cont.pane((table -> {
                    table.add(Core.bundle.get("发现更新")).left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left).row();
                    table.image().color(Color.valueOf("69dcee")).fillX().height(3).pad(3).row();
                    TextButton update = new TextButton("Check For Updates");
                    update.update(() -> {
                        update.setDisabled(onUpdate != 0);

                        if (onUpdate == 0) {
                            update.setText(Core.bundle.get("检查更新"));
                        } else {
                            update.setText(Core.bundle.get("检查完毕"));
                        }
                    });

                    update.changed(() -> {
                        InUpdate = true;

                        for (var url : modUrlName) {
                            updateIng.put(url.key, true);
                            inspect = 0;

                            modUpdateType.put(url.key, -1);

                            updateTag.put(url.key, "未读取");
                            updateTime.put(url.key, "未读取");
                            updateBody.put(url.key, "未读取");
                        }
                    });

                    TextButton  download = new TextButton("Renew Version");
                    download.update(() -> {
                        if (InUpdate()) {
                            download.setText(Core.bundle.get("检查更新中"));
                        } else {
                            switch (onUpdate) {
                                case 0 -> download.setText(Core.bundle.get("更新下载") + Core.bundle.get("请检查更新"));
                                case 1 ->
                                        download.setText(Core.bundle.get("更新下载") + Core.bundle.get("内容需要更新"));
                                case 2 -> download.setText(Core.bundle.get("更新下载") + Core.bundle.get("无需更新"));
                            }
                        }

                        if (inspectIng) {
                            download.setDisabled(true);
                        } else {
                            if (InUpdate()) {
                                download.setDisabled(true);
                            } else {
                                download.setDisabled(onUpdate != 1);
                            }
                        }
                    });
                    download.changed(updateDialog::show);

                    table.add(update).size(150, 64).padLeft(-400).padTop(20);
                    table.add(download).size(150, 64).padLeft(-600).padTop(20);

                })).grow().center().maxWidth(770).row();


            }
        };
    }

    //详细更新页面
    private static void getUpdate(Mods.LoadedMod mod, String name, String url, boolean isJava) {
        contentDialog.cont.clear();
        contentDialog.cont.pane((table -> {
             table.add(updateBody.get(name)).left().growX().wrap().width(350).maxWidth(350).pad(4).row();

            if ((Objects.equals(updateBody.get(name), "ctcoresystem"))) {
                table.add(Core.bundle.get("系统名字")).center().growX().wrap().width(350).maxWidth(350).pad(4).row();
            }
            if ((Objects.equals(updateBody.get(name),"creators" ))) {
                table.add(Core.bundle.get("起源名字")).center().growX().wrap().width(350).maxWidth(350).pad(4).row();
            }
            if ((Objects.equals(updateBody.get(name),"ct"))) {
                table.add(Core.bundle.get("仙明决名字")).center().growX().wrap().width(350).maxWidth(350).pad(4).row();
            }
            if ((Objects.equals(updateBody.get(name),"cttd"))) {
                table.add(Core.bundle.get("创世神塔防名字")).center().growX().wrap().width(350).maxWidth(350).pad(4).row();
            }
            if ((Objects.equals(updateBody.get(name),"ct_fantasy_project"))) {
                table.add(Core.bundle.get("幻想工程名字")).center().growX().wrap().width(350).maxWidth(350).pad(4).row();
            }
            if ((Objects.equals(updateBody.get(name), "mscience"))) {
                table.add(Core.bundle.get("M科技名字")).center().growX().wrap().width(350).maxWidth(350).pad(4).row();
            }

            table.add(Core.bundle.get("如果失败")).left().growX().wrap().width(350).maxWidth(350).pad(4).row();
            table.image().color(Color.valueOf("69dcee")).fillX().height(3).pad(3).row();

            table.add(Core.bundle.get("最新版本") + updateTag.get(name)).center().growX().wrap().width(200).maxWidth(200).pad(4).row();
            table.image().color(Color.valueOf("69dcee")).fillX().height(3).pad(3).row();
            table.add(Core.bundle.get("本地版本") + (modNo.contains(name) ? Core.bundle.get("未安装") : mod.meta.version)).center().growX().wrap().width(200).maxWidth(200).pad(4).row();
            table.image().color(Color.valueOf("69dcee")).fillX().height(3).pad(3).row();
            table.button(Core.bundle.get("现在更新"), (() -> {
                githubImportMod(url, isJava);
            })).size(300, 64).row();
            table.button("夸克网盘", (() -> {
                if (!Core.app.openURI(网盘)) {
                    Vars.ui.showErrorMessage("@linkfail");
                    Core.app.setClipboardText(网盘);
                }
            })).size(300, 50).row();
            table.button(Core.bundle.format("QQ群2"), (() -> {
                if (!Core.app.openURI(QQ群2)) {
                    Vars.ui.showErrorMessage("@linkfail");
                    Core.app.setClipboardText(QQ群2);
                }
            })).size(300, 64).row();
            table.button("@close", (contentDialog::hide)).size(100, 64).labelAlign(Align.center);//关闭按钮
        }));

        contentDialog.show();
    }

    //判断有没有项目正在更新
    private static boolean InUpdate() {
        boolean ing = false;

        for (var obj : updateIng) {
            if (obj.value) {
                ing = true;
                break;
            }
        }

        return ing;
    }

    //临时存储项目Name
    private static Seq<String> tempKey = new Seq<>();
    //临时存储项目Url
    private static Seq<String> tempValue = new Seq<>();

    //异步更新的步骤
    private static int inspect = 0;
    //异步更新的状态
    private static boolean inspectIng = false;

    //检查项目
    private static void inspect() {
        for (var i = 0; i < tempKey.size; i++) {
            if (i != inspect) continue;

            var value = tempValue.get(i);
            var key = tempKey.get(i);

            if (!inspectIng) {
                inspectIng = true;

                Http.get(Vars.ghApi + "/repos/" + value + "/releases/latest", res -> {
                    Jval json = Jval.read(res.getResultAsString());

                    String tag = json.get("tag_name").asString();
                    String time = json.get("published_at").asString();
                    String body = json.get("body").asString();

                    Mods.LoadedMod mod = Vars.mods.getMod(key);
                    String version;

                    if (mod != null) {
                        version = mod.meta.version;
                    } else {
                        version = "null";
                    }

                    if (tag != null && time != null && !tag.equals(settings.getString(key + "-NewVersion")) && !tag.equals(version)) {
                        modUpdateType.put(key, 0);

                        updateTag.put(key, tag);
                        updateTime.put(key, time);
                        updateBody.put(key, body);

                        onUpdate = 1;
                    } else {
                        if (!Objects.equals(tag, settings.getString(key + "-NewVersion"))) {
                            modUpdateType.put(key, 1);
                        } else {
                            modUpdateType.put(key, 2);
                            ui.showInfoOnHidden(key + " 检查出错!", () -> settings.put(key + "-NewVersion", "-1"));
                            onUpdate = 0;
                        }
                    }

                    updateIng.put(key, false);
                    inspect += 1;
                    inspectIng = false;

                }, ex -> {
                    updateIng.put(key, false);
                    inspect += 1;
                    inspectIng = false;

                    modUpdateType.put(key, 3);
                    onUpdate = 0;

                    ui.showInfoOnHidden(ex.toString(), () -> settings.put(key + "-NewVersion", "-1"));
                });
            }
        }
    }

    //以下为下载和安装方面的API

    private static float modImportProgress;

    private static void importFail(Throwable t) {
        Core.app.post(() -> modError(t));
    }

    private static void modError(Throwable error) {
        ui.loadfrag.hide();

        if (error instanceof NoSuchMethodError || Strings.getCauses(error).contains(t -> t.getMessage() != null && (t.getMessage().contains("trust anchor") || t.getMessage().contains("SSL") || t.getMessage().contains("protocol")))) {
            ui.showErrorMessage("@feature.unsupported");
        } else if (error instanceof Http.HttpStatusException) {
            var st = (Http.HttpStatusException) error;
            ui.showErrorMessage(Core.bundle.format("connectfail", Strings.capitalize(st.status.toString().toLowerCase())));
        } else {
            ui.showException(error);
        }
    }

    public static void githubImportMod(String repo, boolean isJava) {
        modImportProgress = 0f;
        ui.loadfrag.show("@downloading");
        ui.loadfrag.setProgress(() -> modImportProgress);

        if (isJava) {
            githubImportJavaMod(repo);
        } else {
            Http.get(ghApi + "/repos/" + repo, res -> {
                var json = Jval.read(res.getResultAsString());
                String mainBranch = json.getString("default_branch");
                String language = json.getString("language", "<none>");

                if (language.equals("Java") || language.equals("Kotlin")) {
                    githubImportJavaMod(repo);
                } else {
                    githubImportBranch(mainBranch, repo);
                }
            }, ctUpdateDialog::importFail);
        }
    }

    private static void handleMod(String repo, Http.HttpResponse result) {
        try {
            Fi file = tmpDirectory.child(repo.replace("/", "") + ".zip");
            long len = result.getContentLength();
            Floatc cons = len <= 0 ? f -> {
            } : p -> modImportProgress = p;

            Streams.copyProgress(result.getResultAsStream(), file.write(false), len, 4096, cons);

            var mod = mods.importMod(file);
            mod.setRepo(repo);
            file.delete();
            Core.app.post(() -> {
                try {
                    ui.loadfrag.hide();

                    BaseDialog 是否重启 = new BaseDialog("");
                    是否重启.add(Core.bundle.get("是否重启")).left().growX().wrap().width(420).maxWidth(420).pad(4).labelAlign(Align.left);
                    是否重启.buttons.defaults().size(140.0F, 54.0F).pad(2.0F);
                    是否重启.setFillParent(false);
                    是否重启.buttons.button("no", () -> {
                        是否重启.hide();
                    });
                    是否重启.buttons.button("yes", () -> {
                        Core.app.exit();
                    });
                    是否重启.show();

                } catch (Throwable e) {
                    ui.showException(e);
                }
            });
        } catch (Throwable e) {
            modError(e);
        }
    }

    //代理下载
    private static final String VpnHttps = "http://ghproxy.org/";
    private static final String VpnHttps2 = "http://gh.tinylake.tech/";//用這個會報錯zip END header not found
    private static final String VpnHttps3 = "";
    private static void githubImportJavaMod(String repo) {
        Http.get(ghApi + "/repos/" + repo + "/releases/latest", res -> {
            var json = Jval.read(res.getResultAsString());
            var assets = json.get("assets").asArray();

            var dexedAsset = assets.find(j -> j.getString("name").startsWith("dexed") && j.getString("name").endsWith(".jar"));
            var asset = dexedAsset == null ? assets.find(j -> j.getString("name").endsWith(".jar")) : dexedAsset;

            if (asset != null) {
                var url = asset.getString("browser_download_url");

                if (Objects.equals(settings.getString("locale"), "zh_CN") || Objects.equals(settings.getString("locale"), "zh_TW")) {
                    url = VpnHttps3 + url;
                }

                Http.get(url, result -> handleMod(repo, result), ctUpdateDialog::importFail);
            } else {
                throw new ArcRuntimeException("在发行版中找不到 JAR 文件. 确保你在 mod 的最新 Github 版本中有一个有效的 jar 文件.");
            }
        }, ctUpdateDialog::importFail);
    }

    private static void githubImportBranch(String branch, String repo) {
        Http.get(ghApi + "/repos/" + repo + "/zipball/" + branch, loc -> {
            if (loc.getHeader("Location") != null) {
                Http.get(loc.getHeader("Location"), result -> {
                    handleMod(repo, result);
                }, ctUpdateDialog::importFail);
            } else {
                handleMod(repo, loc);
            }
        }, ctUpdateDialog::importFail);
    }
}