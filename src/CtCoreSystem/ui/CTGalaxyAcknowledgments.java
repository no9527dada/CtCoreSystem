package CtCoreSystem.ui;

import CtCoreSystem.mfxiao.ActivateProgram;
import CtCoreSystem.ui.NanDu.WorldDifficulty;
import CtCoreSystem.ui.NanDu.WorldDifficultyCT2;
import CtCoreSystem.ui.NanDu.SettingDifficultyDialog;
import arc.Core;
import mindustry.Vars;

import static CtCoreSystem.CtCoreSystem.主动关闭激活;
import static CtCoreSystem.CtCoreSystem.加载CT2;
import static mindustry.Vars.ui;

//添加标题页菜单
public class CTGalaxyAcknowledgments {
    public static void 标题页菜单() {
        if (加载CT2()) {
            new WorldDifficultyCT2().set();
        } else {
            new WorldDifficulty().init();
        }
        ui.settings.game.sliderPref("游戏难度", 3, 1,
                // 仅CT2模式在激活时上限至4级，未激活时为6级，普通模式始终为4级
                加载CT2() ? ((ActivateProgram.isActivated&&主动关闭激活) ? 4 : 6) : 4,
                1, i -> Core.bundle.get((加载CT2() ? "" : "CT3") + "Difficulty-" + i));


      //  ui.settings.game.sliderPref("游戏难度", 3, 1, 加载CT2() ? 6 : 4, 1, i -> Core.bundle.get((加载CT2() ? "" : "CT3") + "Difficulty-" + i));

       // Events.on(EventType.ClientLoadEvent.class, (event) -> {
        Vars.ui.menufrag.addButton(Core.bundle.get("difficulty.game"), () -> new SettingDifficultyDialog(加载CT2()).show());
       // });

    }
/*

    public static void LoadAcknowledgments() {
     *//*       Acknowledgments.cont.pane(cont -> {
        //255, 211, 127
            cont.add("[#FFD37F]DFAFS").pad(4).labelAlign(Align.center).row();
            cont.image(Tex.whiteui, Pal.accent).growX().height(3).pad(4).row();
            cont.image(Core.atlas.find("galaxymod-avatar-1")).height(280).width(280);
            cont.add(Core.bundle.get("galaxymod.acknowledgments.a1.content")).pad(4).labelAlign(Align.center).row();

            cont.add("[#FFD37F]d39052").pad(4).labelAlign(Align.center).row();
            cont.image(Tex.whiteui, Pal.accent).growX().height(3).pad(4).row();
            cont.image(Core.atlas.find("galaxymod-avatar-2")).height(280).width(280);
            cont.add(Core.bundle.get("galaxymod.acknowledgments.a2.content")).pad(4).labelAlign(Align.center).row();
        }).width(Core.scene.getWidth())*//*
        ;
        // Acknowledgments.buttons.button(Core.bundle.get("galaxymod.report.button.close"),Acknowledgments::hide).size(128,64);
    }*/


}
