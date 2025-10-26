package CtCoreSystem.ui;

import CtCoreSystem.mfxiao.ActivateProgram;
import CtCoreSystem.ui.NanDu.*;
import arc.Core;
import arc.util.Log;
import mindustry.Vars;

import static CtCoreSystem.CtCoreSystem.主动关闭激活;
import static CtCoreSystem.CtCoreSystem.加载CT2;

//添加标题页菜单
public class CTGalaxyAcknowledgments {
    public static void 标题页菜单() {
        if (加载CT2()) {
            if (主动关闭激活 == false) {

                if (ActivateProgram.isActivated == true) {
                    new WorldDifficultyCT2Vip().set();
                } else {
                    new WorldDifficultyCT2().set();
                }
            }else {
                new WorldDifficultyCT2().set();
            }
        } else {
            new WorldDifficulty().init();
        }
      /*  ui.settings.game.sliderPref("游戏难度", 3, 1,
                // 仅CT2模式在激活时上限至4级，未激活时为6级，普通模式始终为4级
               // 加载CT2() ? ((ActivateProgram.isActivated&&主动关闭激活) ? 4 : 6) : 4,
                加载CT2() ? (ActivateProgram.isActivated || (!ActivateProgram.isActivated && 主动关闭激活)) ? 6 : 4 : 4,

                1, i -> Core.bundle.get((加载CT2() ? "" : "CT3") + "Difficulty-" + i));

*/
        //  ui.settings.game.sliderPref("游戏难度", 3, 1, 加载CT2() ? 6 : 4, 1, i -> Core.bundle.get((加载CT2() ? "" : "CT3") + "Difficulty-" + i));


        if (ActivateProgram.isActivated == true) {
            if (主动关闭激活 == true) {
                Vars.ui.menufrag.addButton(Core.bundle.get("difficulty.game"), () -> new SettingDifficultyDialog(加载CT2()).show());
                Log.info("经典难度");
            } else {
                Vars.ui.menufrag.addButton(Core.bundle.get("difficulty.game"), () -> new SettingDifficultyDialogVip(加载CT2()).show());
                Log.info("激活难度");
            }
        } else {
            Vars.ui.menufrag.addButton(Core.bundle.get("difficulty.game"), () -> new SettingDifficultyDialog(加载CT2()).show());
            Log.info("经典难度");
        }
    }


}
