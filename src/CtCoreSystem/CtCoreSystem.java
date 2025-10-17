package CtCoreSystem;


import CtCoreSystem.CoreSystem.DsShaders;
import CtCoreSystem.CoreSystem.miner.minerRenderer;
import CtCoreSystem.CoreSystem.type.CTResearchDialog;
import CtCoreSystem.CoreSystem.type.Ovulam5480.xuetiao.BossBarFragment;
import CtCoreSystem.CoreSystem.type.VXV.SpawnDraw;
import CtCoreSystem.content.Effect.CT3FxEffect;
import CtCoreSystem.content.Effect.NewFx;
import CtCoreSystem.content.ItemX;
import CtCoreSystem.content.SourceCodeModification_Sandbox;
import CtCoreSystem.content.yuanban;
import CtCoreSystem.mfxiao.ActivateProgram;
import CtCoreSystem.ui.CTCampaignRulesDialog;
import CtCoreSystem.ui.CTGalaxyAcknowledgments;
import CtCoreSystem.ui.CTPlacementFragment;
import CtCoreSystem.ui.Ovulam5480.资源顶部显示;
import CtCoreSystem.ui.UnemFragment;
import CtCoreSystem.ui.dialogs.CT3InfoDialog;
import CtCoreSystem.ui.dialogs.CT3PlanetDialog;
import CtCoreSystem.ui.dialogs.CT3function;
import CtCoreSystem.ui.dialogs.游戏功能按钮;
import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.input.KeyCode;
import arc.scene.ui.Dialog;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Collapser;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Log;
import arc.util.Reflect;
import arc.util.Time;
import mindustry.Vars;
import mindustry.editor.MapInfoDialog;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Layer;
import mindustry.graphics.Shaders;
import mindustry.mod.Mod;
import mindustry.mod.Mods;
import mindustry.type.Planet;
import mindustry.type.UnitType;
import mindustry.ui.CoreItemsDisplay;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.CustomRulesDialog;
import mindustry.ui.dialogs.PlanetDialog;
import mindustry.ui.dialogs.ResearchDialog;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.DirectionalUnloader;
import mindustry.world.blocks.distribution.Sorter;
import mindustry.world.blocks.sandbox.ItemSource;
import mindustry.world.blocks.sandbox.LiquidSource;
import mindustry.world.blocks.storage.Unloader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Consumer;

import static CtCoreSystem.CoreSystem.type.VXV.TDpowerShowBlock.TDloadPowerShow;
import static CtCoreSystem.CoreSystem.type.VXV.powerShowBlock.loadPowerShow;
import static CtCoreSystem.mfxiao.ActivateProgram.startActivationProcess;
import static arc.Core.camera;
import static arc.Core.settings;
import static mindustry.Vars.*;

public class CtCoreSystem extends Mod {
    public final static Seq<Runnable> BlackListRun = new Seq<>();
    public static BossBarFragment bossBar;
    public static boolean cthind = settings.getBool("辅助模式");
    public static boolean 主动关闭激活 = settings.getBool("主动关闭激活");
    public static boolean VIP = false;//是否是赞助者
    public Seq<String> BaiMingDan = new Seq<>();
    public static String toText(String str) {
        return Core.bundle.format(str);
    }
    // 获取屏幕宽度
    public static int screenWidth = Core.graphics.getWidth();
    // 获取屏幕高度
    public static int screenHeight = Core.graphics.getHeight();
    // 检查设备是否处于竖屏模式
    public static boolean 竖屏 = Core.graphics.isPortrait();
     public static boolean 手机端 = !mobile;
    // 获取屏幕宽高的比例值（可能用于UI适配）
    public static float screenRatio = (float)Core.graphics.getWidth() / Core.graphics.getHeight();



    {
       if(手机端) {
           // 设置手机端UI初始缩放比例为75%（即0.75倍）
           Events.run(EventType.Trigger.update, () -> {
               Core.settings.put("uiscale", 75);
               Scl.setProduct(Math.max(Core.settings.getInt("uiscale", 100), 25) / 100f);
           });
        }
        //  Vars.state.rules.alloweditworldprocessors=false; 禁止世处编辑
        Vars.state.rules.hideBannedBlocks = false;
        //缩放
        Vars.renderer.minZoom = 0.2F;
        Vars.renderer.maxZoom = 35;
        //蓝图大小
        Vars.maxSchematicSize = 128;
    }

    //带桥物品显示
    public CtCoreSystem() {
        Events.on(EventType.ClientLoadEvent.class, (e) -> {
            minerRenderer.init();
        });
        Events.run(EventType.Trigger.draw, minerRenderer::draw);
    }

    //首页主功能按钮的系统
    public static ImageButton CreatorsIcon(String IconName, ImageButton.ImageButtonStyle imageButtonStyle, BaseDialog dialog) {
        TextureRegion A = Core.atlas.find("ctcoresystem-" + IconName);

        ImageButton buttonA = new ImageButton(A, imageButtonStyle);
        buttonA.clicked(dialog::show);
        return buttonA;
    }

    // 定义一个方法用于禁用指定名称的模组
    public static void disableModIfExists(String modName) {
        if (Vars.mods.locateMod(modName) != null) {
            Vars.mods.getMod(modName).state = Mods.ModState.disabled;
        }
    }

    public static boolean 加载CTTD() {
        return Vars.mods.locateMod("cttd") == null;
    }

    public static boolean 加载CT2() {
        return Vars.mods.locateMod("creators") != null;
    }

    public static void overrideVersion() {
        for (int i = 0; i < Vars.mods.list().size; i++) {
            Mods.LoadedMod mod = Vars.mods.list().get(i);
            if (mod != null) {
                mod.meta.description = Core.bundle.get("mod.CT.version") + mod.meta.version + "\n\n" + mod.meta.description;
            }
        }
    }

    public static void setPlanet(Planet planet, String[] names) {
        planet.ruleSetter = r -> {
            // planet.hiddenItems.addAll(Items.serpuloItems);
            ObjectSet<Block> B = new ObjectSet<>();
            for (Block b : content.blocks()) {
                if (b.minfo.mod == null) {
                    B.add(b);
                    continue;
                }
                boolean yes = true;
                for (String name : names) {
                    if (Objects.equals(b.minfo.mod.meta.name, name) || Objects.equals(b.minfo.mod.name, name)) {
                        yes = false;
                        break;
                    }
                }
                if (yes) {
                    B.add(b);
                }
            }
            r.bannedBlocks.addAll(B);
            ObjectSet<UnitType> U = new ObjectSet<>();
            for (UnitType u : content.units()) {
                if (u.minfo.mod == null) {
                    U.add(u);
                    continue;
                }
                boolean yes = true;
                for (String name : names) {
                    if (Objects.equals(u.minfo.mod.meta.name, name) || Objects.equals(u.minfo.mod.name, name)) {
                        yes = false;
                        break;
                    }
                }
                if (yes) {
                    U.add(u);
                }
            }
            r.bannedUnits.addAll(U);
            r.showSpawns = true;//显示单位刷出点
        };
    }

    // 退出游戏弹窗
    public static void showCustomDialog(String title, Consumer<Table> contBuilder) {
        new Dialog(title) {{
            // 让调用者自定义cont的内容
            contBuilder.accept(cont);
            row();
            // 添加默认的确定按钮
            // buttons.button("@ok", this::hide).size(110, 50).pad(4);
            button("@restart", () -> Core.app.exit()).center().pad(16).width(200f).fill().row();
            add(Core.bundle.format("CTreloadrequired")).pad(4).center().row();
            // keyDown(KeyCode.enter, this::hide);
            // closeOnBack();
        }}.show();
    }

    //自定义弹窗
    public static void PopUpWindow(String title, Consumer<Table> contBuilder) {
        new Dialog(title) {{
            // 让调用者自定义cont的内容
            contBuilder.accept(cont);
            // 添加默认的确定按钮
            button("@ok", this::hide).size(110, 50).pad(4).center();
            //回车键 和 Esc键 关闭弹窗
            keyDown(KeyCode.enter, this::hide);
            closeOnBack();
        }}.show();
    }

    //自定义弹窗2 倒计时间关闭弹窗
    private static float leave = 5f * 60;//倒计时间
    private static boolean canClose = false;
    public static void PopUpWindow2(String title, Consumer<Table> contBuilder) {
        new Dialog(title) {{
            update(() -> {
                leave -= Time.delta;
                if (leave < 0 && !canClose) {
                    canClose = true;
                }
            });
            // 让调用者自定义cont的内容
            contBuilder.accept(cont);
            // 添加默认的倒计时确定按钮
            row(); // 创建新行
           button("", this::hide).update(b -> {{
                b.setDisabled(!canClose);
                b.setText(canClose ? toText("close") : toText("close") + "[accent]" + Math.floor(leave / 60) + "[]s");
            }}).size(140f, 50f).pad(4).center(); // 使用center()方法居中对齐
            //回车键 和 Esc键 关闭弹窗
            //keyDown(KeyCode.enter, this::hide);
            //closeOnBack();
        }}.show();
    }

    public void loadContent() {
        //实验产物.load();

/*

        try {
            ZiTi.registerFonts();
        } catch (Exception e) {
            Log.err("注册字体失败", e);
        }
*/
        if (!cthind) {
            Vars.mods.getMod("ctcoresystem").meta.hidden = false;

        } else {
            Vars.mods.getMod("ctcoresystem").meta.hidden = true;
        }
        //禁用自动存档模组;
        disableModIfExists("auto_saver");

        DsShaders.load();//电力节点力场的动画效果
        //  if(!settings.getBool("cthind")) {
        new CT3FxEffect();
        NewFx.load();
        NewFx.init();
        ItemX.load();
        yuanban.load();
        SourceCodeModification_Sandbox.load();
        // }
        CreatorsModJS.DawnMods();//JS加载器
    /*
   //分类栏ui
   new CT3ClassificationUi();
        Scripts scripts = mods.getScripts();
        Scriptable scope = scripts.scope;
        try {
            Object obj = Context.javaToJS(new CT3ClassificationUi(), scope);
            ScriptableObject.putProperty(scope, "CT3ClassificationUi", obj);
        } catch (Exception var5) {
            ui.showException(var5);
        }
        */
        Log.info("主动关闭激活 " + 主动关闭激活);
    }

    public void init() {
        new 游戏功能按钮().addToHud();
    Events.on(EventType.ClientLoadEvent.class, e -> {
            if (主动关闭激活 == false) {
                Log.info("本地许可证验证中....");
                if (ActivateProgram.LICENSE_FILE.exists()) {
                    String licenseKey = ActivateProgram.LICENSE_FILE.readString().trim();
                    Log.info("本地许可证文件存在");
                    if (!licenseKey.isEmpty()) {
                        //  onActivationSuccess(); // 设置激活状态
                        Log.info("联网验证激活状态中...");
                        startActivationProcess();//联网验证激活状态
                        Log.info("赞助激活状态确认 " + ActivateProgram.isActivated);
                    } else {
                        Log.info("Creators DLC 内容未激活，跳过加载。");
                        PopUpWindow("", cont -> {
                            cont.add(Core.bundle.format("activation.error.localfileinvalid"));
                        });
                    }
                }else {
                    Log.info("本地许可证文件不存在");
                    ActivateProgram.isActivated = false;
                    ActivateProgram.saveActivationState(); // 新增：保存激活状态
                }
            }
        });

        boolean[] 辅助开关 = {false};
        ui.settings.addCategory("[accent][创世神][]辅助模式", Icon.chartBar, st -> {
            st.checkPref("辅助模式", false, e -> {
                辅助开关[0] = !辅助开关[0];
            });
            st.row();
            st.add(Core.bundle.format("ct3-hind")).visible(() -> 辅助开关[0]).row();
            st.image().color(Color.valueOf("69dcee")).fillX().height(3).pad(3).row();
            st.add(Core.bundle.format("ct3-hindTXT") + "\n").left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);
        });


        if (!cthind) {
            if (!加载CTTD()) {
            } else {
                CTGalaxyAcknowledgments.标题页菜单();//添加标题页菜单
                if (加载CT2()) {
                    if (ActivateProgram.isActivated) {
                        if(!主动关闭激活) {
                            settings.put("游戏难度", 2);
                        }else {
                            settings.put("游戏难度", 4);
                        }
                    } else {
                        settings.put("游戏难度", 4);
                    }
                } else {
                    settings.put("游戏难度", 3);
                }//每次重启游戏恢复到默认难度
            }
            loadPowerShow();//电力显示方块
            TDloadPowerShow();//塔防电力显示方块
            //血条
            bossBar = new BossBarFragment(ui.hudGroup);
            //给状态上血条
            bossBar.putBarMap(u -> u.hasEffect(ItemX.超级Boss), Color.valueOf("ff4671"), f -> {
                //if (f < 0.5f)return Shaders.buildBeam;  //血量一半后显示另一种特效
                return Shaders.buildBeam;

            });
        }

        if (cthind) {
            //写一个提示信息，表示当前已启用创世神辅助模式
            BaseDialog dialog = new BaseDialog("警告");
            dialog.cont.table(Tex.button, t -> {
                t.defaults().size(280, 160).left();
                t.add("当前已启用[accent]创世神辅助模式[]\n所有添加内容被隐藏仅保留辅助功能\n创世神系列mod可能会出现未知错误\n关闭请到设置里设置").row();
                t.button("@close", (dialog::hide)).size(100, 64).center();//关闭按钮
            });
            dialog.show();
        }

        //显示怪物路径
        SpawnDraw.init();
        //显示版本号
        if (Vars.mods.locateMod("extra-utilities") == null) {
            overrideVersion();
        }

        //给单位上血条
 /*
    bossBar.putBarMap(u->u.type== UnitTypes.dagger, Color.valueOf("e35050"), f -> {
            if (f < 0.5f)return Shaders.buildBeam;
            return Shaders.water;

        });
        */

        //==================
        //动态logo加载会导致难度设置选项消失
        //动态logo
        //X端和学术端不加载动态logo
        try {
            Class.forName("mindustry.arcModule.ARCVars");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("mindustryX.VarsX");
            } catch (ClassNotFoundException b) {
                资源顶部显示 资源显示 = new 资源顶部显示();
                if (Vars.mobile) ui.settings.graphics.checkPref("coreitems", true);
                Events.on(EventType.ClientLoadEvent.class, ei ->  {
                    //资源顶部显示
                    ui.hudfrag.coreItems = 资源显示;
                    ((Collapser) ((Table) ((Table) ui.hudGroup.find("coreinfo")).getChildren().get(1)).getChildren().get(0)).setTable(资源显示);

                    if(!竖屏){
                        ((Collapser) ((Table) ((Table) ui.hudGroup.find("coreinfo")).getChildren().get(1)).getChildren().get(0)).setCollapsed(() -> !(ui.hudfrag.shown && settings.getBool("coreitems", true)));
                    }
                });

                Events.on(EventType.ResetEvent.class, eq -> {
                    资源显示.resetUsed();
	            });

                //动态logo
                UnemFragment unemFragment = new UnemFragment();
                Vars.ui.menufrag = unemFragment;
                unemFragment.build(ui.menuGroup);
            }
        }


        //==================


        //敌人行进路线
        if (加载CTTD()) {
            SpawnDraw.setEnable2(true, true, true);
        } else {
            SpawnDraw.setEnable2(true, false, true);
            Events.on(EventType.ClientLoadEvent.class, e -> {//加载塔防时，显示一个信息提示
                BaseDialog dialog = new BaseDialog("");
                dialog.cont.table(Tex.button, t -> {
                    t.defaults().size(280, 160).left();
                    t.add(Core.bundle.format("planet.CT.xinxi")).row();
                    t.button("@close", (dialog::hide)).size(100, 64).center();//关闭按钮
                });
                dialog.addCloseListener();//按esc关闭
                dialog.show();
            });
        }

        //区块名显示
        Vars.ui.planet = new CT3PlanetDialog();
        CT3InfoDialog.show();//开屏显示
        CT3选择方块显示图标(); //选择方块显示图标
       // ctUpdateDialog.load();//更新检测 新版 在用


        // Timer.schedule(CTUpdater::checkUpdate, 4);//檢測更新 旧版 未用

        //首页主功能按钮
        CT3function.show();
        ImageButton imagebutton = CreatorsIcon("functionIcon", Styles.defaulti, CT3function.功能图标UI);
        Vars.ui.menuGroup.fill(t -> {
            if (mobile) {
                t.add(imagebutton).update(b -> b.color.fromHsv(Time.time % 360, 1, 1)).size(80);//手机
                t.bottom();
            } else {
                t.add(imagebutton).update(b -> b.color.fromHsv(Time.time % 360, 1, 1)).size(120.0f);//电脑
                t.left().bottom();
            }
        });
        //科技树全显
        CTResearchDialog dialog = new CTResearchDialog();
        ResearchDialog research = Vars.ui.research;
        research.shown(() -> {
            dialog.show();
            Objects.requireNonNull(research);
            Time.runTask(1.0F, research::hide);
        });



        //替换原版选择方块UI
        replaceUI();
    }

    /**
     * replace ui
     */
    private void replaceUI() {
        if (!竖屏 ) {//pc端替换PlacementFragmentUI
            //覆盖原版选择
            CTPlacementFragment placementFragment = new CTPlacementFragment();
            placementFragment.build(ui.hudGroup);
            try {
                ui.hudfrag.blockfrag = placementFragment;
            } catch (Exception ignore) {
            }


      }

        //覆盖原版难度选择
        try {
            Field field = PlanetDialog.class.getDeclaredField("campaignRules");
            field.setAccessible(true);
            field.set(ui.planet, new CTCampaignRulesDialog());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    //选择方块显示图标
    public void CT3选择方块显示图标() {
        Events.run(EventType.Trigger.draw, () -> {
            if (Vars.ui != null) {
                indexer.eachBlock(null, camera.position.x, camera.position.y, (30 * tilesize), b -> true, b -> {
                    if (b instanceof LiquidSource.LiquidSourceBuild) {
                        LiquidSource.LiquidSourceBuild source = (LiquidSource.LiquidSourceBuild) b;
                        if (source.config() != null) {
                            Draw.z(Layer.block + 1);
                            Draw.rect(source.config().fullIcon, b.x, b.y, 3, 3);
                        }
                    }
                    if (b instanceof ItemSource.ItemSourceBuild) {
                        ItemSource.ItemSourceBuild source = (ItemSource.ItemSourceBuild) b;
                        if (source.config() != null) {
                            Draw.z(Layer.block + 1);
                            Draw.rect(source.config().fullIcon, b.x, b.y, 3, 3);
                        }
                    }
                    if (b instanceof Sorter.SorterBuild) {
                        Sorter.SorterBuild sorter = (Sorter.SorterBuild) b;
                        if (sorter.config() != null) {
                            Draw.z(Layer.block + 1);
                            Draw.rect(sorter.config().fullIcon, b.x, b.y, 3, 3);
                        }
                    }
                    //装卸器
                    if (b instanceof Unloader.UnloaderBuild) {
                        Unloader.UnloaderBuild source = (Unloader.UnloaderBuild) b;
                        if (source.config() != null) {
                            Draw.z(Layer.block + 1);
                            Draw.rect(source.config().fullIcon, b.x, b.y, 3, 3);
                        }
                    }
                    //定向装卸器
                    if (b instanceof DirectionalUnloader.DirectionalUnloaderBuild) {
                        DirectionalUnloader.DirectionalUnloaderBuild source = (DirectionalUnloader.DirectionalUnloaderBuild) b;
                        if (source.config() != null) {
                            Draw.z(Layer.block + 1);
                            Draw.rect(source.config().fullIcon, b.x, b.y, 3, 3);
                        }
                    }
                });
            }
        });
    }

}
