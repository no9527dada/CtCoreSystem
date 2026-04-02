package CtCoreSystem.CoreSystem.type.MinRi2;

import CtCoreSystem.CoreSystem.type.MinRi2.ui.*;
import arc.*;
import arc.scene.actions.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.*;
import mindustry.mod.*;
import mindustry.ui.*;

public class Main {
    private static AutoSaverDialog recoverDialog;


    public static void init(){
        SaverVars.init();
        recoverDialog = new AutoSaverDialog();
        addSaverButton();
    }

    private static void addSaverButton(){
        Table menu = Reflect.get(Vars.ui.settings, "menu");

        Runnable addCustomButton = () -> {
            if(!menu.getCells().peek().isEndRow()){
                menu.row();
            }

            menu.button(b -> {
                b.add("[accent][AS]").padLeft(6f);
                b.add(Core.bundle.get("auto-saver", "AutoSaver")).labelAlign(Align.center).growX();
            }, Styles.cleari, () -> {
                recoverDialog.show();
            }).name("recovery");

            menu.row();
        };

        Runnable updater = () -> {
            if(menu.find("recovery") == null){
                addCustomButton.run();
            }
        };

        menu.addAction(Actions.forever(Actions.run(updater)));
    }

}
