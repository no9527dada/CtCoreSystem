package CtCoreSystem.CoreSystem.type.MinRi2;

import arc.files.*;
import mindustry.*;

import java.text.*;

/**
 * @author minri2
 * Create by 2024/7/19
 */
public class SaverVars{
    public static boolean debug = false;

    public static final String metaFileName = "saveMeta.json";
    public static final Fi saveDirectory = Vars.dataDirectory.child("autoSaved");

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
    public static final String modPrefix = "AutoSaver-";
    public static final String settingPrefix = modPrefix + "setting.";

    public static AutoSaver saver;
    public static AutoSaverSetting setting;

    public static void init(){
        setting = new AutoSaverSetting();
        saver = new AutoSaver();

        saver.init();
    }

}
