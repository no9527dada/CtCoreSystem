package CtCoreSystem.CoreSystem.type.MinRi2;

import arc.*;
import arc.func.*;
import arc.struct.*;

/**
 * @author minri2
 * Create by 2024/7/20
 */

// TODO: Auto generated.
public class AutoSaverSetting{
    private final ObjectMap<String, Seq<Cons<?>>> watcherMap;

    public AutoSaverSetting(){
        watcherMap = new ObjectMap<>();

        Core.settings.defaults(
        SaverVars.modPrefix + "autoSaveInGame", true,
        SaverVars.modPrefix + "saveMods", false,
        SaverVars.modPrefix + "saveInServer", false,
        SaverVars.modPrefix + "savesAmount", 20,
        SaverVars.modPrefix + "savePerMinute", 10f
        );
    }

    public void put(String name, Object value){
        Core.settings.put(SaverVars.modPrefix + name, value);
        fire(name, value);
    }

    public <T> T get(String name){
        return (T)get(name, Core.settings.getDefault(SaverVars.modPrefix + name));
    }

    public <T> T get(String name, T defValue){
        return (T)Core.settings.get(SaverVars.modPrefix + name, defValue);
    }

    public int getSavesAmount(){
        return get("savesAmount");
    }

    public float getSavePerMinute(){
        return get("savePerMinute");
    }

    public boolean getSaveMods(){
        return get("saveMods");
    }

    public boolean getAutoSaveInGame(){
        return get("autoSaveInGame");
    }

    public boolean getSaveInServer(){
        return get("saveInServer");
    }

    public <T> void watch(String name, Cons<T> cons){
        watcherMap.get(SaverVars.modPrefix + name, () -> new Seq<>(Cons.class)).add(cons);
    }

    private <T> void fire(String name, T value){
        Seq<Cons<?>> seq = watcherMap.get(SaverVars.modPrefix + name);

        if(seq == null || seq.isEmpty()) return;

        for(Cons<?> cons : seq){
            ((Cons<T>)cons).get(value);
        }
    }
}
