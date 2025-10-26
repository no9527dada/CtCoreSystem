package CtCoreSystem.ui.NanDu;



import arc.Core;
import arc.Events;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

public class WorldDifficulty {
    // public DifficultyConfigure 配置项 = new DifficultyConfigure();
    public static StatusEffect cheat;

    public static void init() {
        if (cheat == null) {
            cheat = new StatusEffect("difficulty") {
                {show = true;
                    localizedName = "难度模式";
                    fullIcon = Core.atlas.find("ctcoresystem-difficulty");
                    uiIcon = Core.atlas.find("ctcoresystem-difficulty");
                }

                @Override
                public void loadIcon() {
                    fullIcon = Core.atlas.find("ctcoresystem-difficulty");
                    uiIcon = Core.atlas.find("ctcoresystem-difficulty");
                }
            };
       }

        Events.run(EventType.Trigger.update, () -> {
            if (!Vars.ui.editor.isShown() && !Vars.state.rules.editor && !Vars.state.isMenu()) {
                var MapName = Vars.state.map.name();
                saveDefaultDifficulty(MapName);
                setDifficulty(MapName);
                Groups.unit.each(WorldDifficulty::UnitDraw);
            }
        });
    }

    public static void saveDefaultDifficulty(String MapName) {
        var keyA = Vars.state.rules.waveTimer;
        if (keyA) {
            var key = Vars.state.rules.waveSpacing;
            setWorldMap(MapName, "波次", key);
        }

        var keyB = Vars.state.rules.waveTeam;

        var key1 = Vars.state.rules.teams.get(keyB).unitDamageMultiplier;
        //var key2 = Vars.state.rules.teams.get(keyB).blockHealthMultiplier;
        //var key3 = Vars.state.rules.teams.get(keyB).blockDamageMultiplier;

        setWorldMap(MapName, "地图敌人单位攻击", key1);
        //setWorldMap(MapName, "地图敌人建筑血量", key2);
        //setWorldMap(MapName, "地图敌人建筑攻击", key3);
    }

    public static void setDifficulty(String MapName) {
        var keyA = Vars.state.rules.waveTimer;
        if (keyA) {
            var key = getWorldMap(MapName, "波次");
            //地图世处中大量设置了随时改波次时间的逻辑，这里会导致失效，所以暂时不适用
            //Vars.state.rules.waveSpacing = key * 时间倍率(Core.settings.getInt("游戏难度"));
        }

        var keyB = Vars.state.rules.waveTeam;

        var key1 = getWorldMap(MapName, "地图敌人单位攻击");
        //var key2 = getWorldMap(MapName, "地图敌人建筑血量");
        //var key3 = getWorldMap(MapName, "地图敌人建筑攻击");

        //地图世处中大量设置了随时改敌人伤害的逻辑，这里会导致失效，所以暂时不适用
        // Vars.state.rules.teams.get(keyB).unitDamageMultiplier = key1 * 单位攻击倍率(Core.settings.getInt("游戏难度"));


        //Vars.state.rules.teams.get(keyB).blockHealthMultiplier = key2 * 配置项.建筑血量倍率(Core.settings.getInt("地图敌人建筑血量"));
        //Vars.state.rules.teams.get(keyB).blockDamageMultiplier = key3 * 配置项.建筑攻击倍率(Core.settings.getInt("地图敌人建筑攻击"));
    }

    public static void UnitDraw(Unit unit) {
        if (unit.team == Vars.state.rules.waveTeam) {
            cheat.healthMultiplier = unitHealthMulti(Core.settings.getInt("游戏难度"));//血量
            cheat.reloadMultiplier = reloadMulti(Core.settings.getInt("游戏难度"));//射速
            cheat.speedMultiplier = speedMulti(Core.settings.getInt("游戏难度"));//移速
            unit.apply(cheat, 100f);
        }

    }

    public static float setWorldMap(String MapName, String State, float key) {
        if (Vars.state.rules.tags.get(MapName + "-" + State + "-Difficulty") == null) {
            Vars.state.rules.tags.put(MapName + "-" + State + "-Difficulty", String.valueOf(key));
        }
        return key;
    }

    public static float getWorldMap(String MapName, String State) {
        if (Vars.state.rules.tags.get(MapName + "-" + State + "-Difficulty") == null) {
            setWorldMap(MapName, State, 0);
        }
        return Float.parseFloat(Vars.state.rules.tags.get(MapName + "-" + State + "-Difficulty"));
    }

/*    public float 时间倍率(int value){
        switch (value){
           // case 0 : return 1f;
            case 1 : return 2.5f;
            case 2 : return 1.5f;
            case 3 : return 1;
            case 4 : return 1.8f;
            //case 5 : return 1.5f;
        }
        return 1f;
    }
        public float 单位攻击倍率(int value){
        switch (value){
            //case 0 : return 0.0001f;
            case 1 : return 0.35f;
            case 2 : return 0.75f;
            case 3 : return 1;
            case 4 : return 1.15f;
           // case 5 : return 1.5f;
        }
        return 1f;
    }
    */

    public static float unitHealthMulti(int value) {
        switch (value) {
            //case 0 : return 0.01f;
            case 1:
                return 0.2f;
            case 2:
                return 0.5f;
            case 3:
                return 1;
            case 4:
                return 1.5f;
            // case 5 : return 3.3f;
        }
        return 1f;
    }

    public static float reloadMulti(int value) {
        switch (value) {
            //case 0 : return 0f;
            case 1:
                return 0.5f;
            case 2:
                return 0.6f;
            case 3:
                return 1;
            case 4:
                return 1.8f;
            // case 5 : return 2.2f;
        }
        return 1f;
    }

    public static float speedMulti(int value) {
        switch (value) {
            // case 0 : return 1f;
            case 1:
                return 0.5f;
            case 2:
                return 0.7f;
            case 3:
                return 1f;
            case 4:
                return 0.7f;
            // case 5 : return 0.8f;
        }
        return 1f;
    }
}
