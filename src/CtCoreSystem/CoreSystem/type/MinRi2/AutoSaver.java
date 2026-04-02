package CtCoreSystem.CoreSystem.type.MinRi2;

import CtCoreSystem.CoreSystem.type.MinRi2.ui.*;
import arc.*;
import arc.files.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.io.*;

import java.text.*;
import java.util.*;

/**
 * @author minri2
 * Create by 2024/7/19
 */
public class AutoSaver{
    // 用于文件名
    public static final SimpleDateFormat fileNameDateFormat = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");
    public static final Fi[] EMPTY_FI_ARRAY = {};

    public boolean saving = false;

    private final Seq<SaveMeta> metas = new Seq<>();
  //  private Timekeeper timekeeper;


    private float lastSaveTime = 0f;
    private float saveInterval = 60f;
    public void init(){
        float minutes = SaverVars.setting.getSavePerMinute();
        // 确保至少 1 秒 (60 帧) 存档一次，防止卡死
        saveInterval = minutes * 60f * 60f;
        lastSaveTime = Time.time;

        loadSaveMetas();

/*
        Events.on(WorldLoadEndEvent.class, e -> {
            if(SaverVars.debug) Log.info("[accent]WorldLoading");

            timekeeper.reset();
        });
*/

        Events.run(Trigger.update, () -> {
            boolean autoSaveInGame = SaverVars.setting.getAutoSaveInGame() && Vars.state.isGame() && !Vars.state.isEditor()
                    && (!Vars.net.client() || SaverVars.setting.getSaveInServer());

            if(!autoSaveInGame){
                lastSaveTime = Time.time;
                return;
            }

            if(Time.time - lastSaveTime >= saveInterval){
                lastSaveTime = Time.time;
                SaverVars.saver.saveData();
            }
        });

        SaverVars.setting.<Float>watch("savePerMinute", savePerMinute -> {
            // 同样需要最小间隔保护
            saveInterval = savePerMinute * 60f * 60f;

            if(Vars.state.isGame()){
                lastSaveTime = Time.time;
            }
        });
    }

    public void saveData(){
        Date date = new Date();
        String fileName = fileNameDateFormat.format(date);
        String saveDate = SaverVars.dateFormat.format(date);

        // 保存时以日期为名
        // 后续日期存到saveMeta.json中
        Fi saveFi = SaverVars.saveDirectory.child(fileName + ".zip");

        Seq<Fi> files = new Seq<>();
        Fi[] customMaps = Vars.customMapDirectory.list();
        Fi[] saves = Vars.saveDirectory.list();
        Fi[] schematics = Vars.schematicDirectory.list();
        Fi[] mods = SaverVars.setting.getSaveMods() ? Vars.modDirectory.list() : EMPTY_FI_ARRAY; // empty

        files.add(Core.settings.getSettingsFile())
        .addAll(customMaps)
        .addAll(saves)
        .addAll(schematics)
        .addAll(mods);

        String base = Core.settings.getDataDirectory().path();

        saving = true;
        Runnable hideSaving = SaverUI.showSaving();

        Threads.thread(() -> {
            try{
                if(SaverVars.debug) Log.info("[accent]Saving");
                ZipUtils.writeAll(saveFi, files, base);
                if(SaverVars.debug) Log.info("[accent]Save done");

                checkMetas();

                // 超数删除旧存档
                if(metas.size >= SaverVars.setting.getSavesAmount()){
                    SaveMeta first = metas.first();
                    first.saveFi.delete();

                    metas.remove(first);
                }

                SaveMeta meta = new SaveMeta().set(saveFi, saveDate);

                meta.customMaps = customMaps.length;
                meta.saves = saves.length;
                meta.schematics = schematics.length;
                meta.mods = mods.length;

                metas.add(meta);
                metas.sort();

                Fi metaFi = Vars.tmpDirectory.child(SaverVars.metaFileName);
                JsonIO.json.toJson(meta, metaFi);
                ZipUtils.write(saveFi, metaFi, null, true);

                metaFi.delete();

                Core.app.post(() -> {
                    Events.fire(AutoSaveEvent.class, null);

                    saving = false;
                    hideSaving.run();
                });
            }catch(RuntimeException e){
                Log.err("Failed to save data ", e);
            }
        }).setPriority(Thread.MIN_PRIORITY);
    }

    public void readData(SaveMeta meta){
        Fi saveFi = meta.saveFi;
        Fi temp = Vars.tmpDirectory.child("tmp_" + saveFi.name());
        saveFi.copyTo(temp);

        Fi tempZip = new ZipFi(temp);

        Fi base = Core.settings.getDataDirectory();

        tempZip.walk(f -> {
            if(SaverVars.metaFileName.equals(f.name())) return;
            if(f.isDirectory() && f.list().length == 0) return;

            Fi old = base.child(f.path());
            old.delete();

            f.copyTo(old);
        });

        temp.delete();
        Core.settings.clear();

        Core.settings.load();
        Vars.schematics.load();
    }

    public void loadSaveMetas(){
        metas.clear();

        SaverVars.saveDirectory.walk(fi -> {
            if(!fi.extEquals("zip")) return;

            Fi metaFi = new ZipFi(fi).child(SaverVars.metaFileName);
            if(metaFi.exists()){
                SaveMeta meta = JsonIO.json.fromJson(SaveMeta.class, metaFi);
                meta.saveFi = fi;
                metas.add(meta);
            }
        });

        metas.sort();
    }

    private void checkMetas(){
        metas.removeAll(m -> !m.saveFi.exists());
    }

    public Seq<SaveMeta> getMetas(){
        checkMetas();
        return metas;
    }

    public void removeMeta(SaveMeta meta){
        checkMetas();

        meta.saveFi.delete();
        metas.remove(meta);
    }

    public static class SaveMeta implements Comparable<SaveMeta>{
        // Needn't save.
        public transient Fi saveFi;

        public String saveDate;
        public int customMaps, saves, schematics, mods;

        public Date getDate(){
            try{
                return SaverVars.dateFormat.parse(saveDate);
            }catch(ParseException e){
                throw new RuntimeException(e);
            }
        }

        public SaveMeta set(Fi saveFi, String saveDate){
            this.saveFi = saveFi;
            this.saveDate = saveDate;
            return this;
        }

        @Override
        public int compareTo(SaveMeta o){
            return getDate().compareTo(o.getDate());
        }

        @Override
        public String toString(){
            return "SaveMeta{" +
            "saveDate='" + saveDate + '\'' +
            ", customMaps=" + customMaps +
            ", saves=" + saves +
            ", schematics=" + schematics +
            '}';
        }
    }

    public static class AutoSaveEvent{
    }
}
