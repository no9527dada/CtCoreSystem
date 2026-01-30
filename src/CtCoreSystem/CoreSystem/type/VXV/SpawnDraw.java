package CtCoreSystem.CoreSystem.type.VXV;

import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Liquids;
import mindustry.game.EventType;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Tile;

import static CtCoreSystem.CoreSystem.type.CTColor.C;
import static CtCoreSystem.CtCoreSystem.敌人行进路径;

/*
 *@Date  :2024/5/13
 */
public class SpawnDraw {
    //0 陆军
    //1 蜘蛛
    //2 海军
    // public static boolean[] enables = new boolean[]{true, true, true};


    public static boolean[] enables = new boolean[]{true, true, true};


    public static Color[] colors = new Color[]{Color.red, C("bd6cff"), C("41d0ff")};//Pal.reactorPurple

    public static void init() {
        Events.run(EventType.Trigger.draw, () -> {
            // 检查全局设置开关
            if (敌人行进路径) {
                Draw.draw(Layer.flyingUnit + 2.5f, draw);
            }
        });
    }
    public static boolean isEnable(int index) {
        return enables[index];
    }

    public static void setEnable(int index, boolean enable) {
        SpawnDraw.enables[index] = enable;
    }

    public static void setEnable2(boolean enable0, boolean enable1, boolean enable2) {
        SpawnDraw.enables[0] = enable0;
        SpawnDraw.enables[1] = enable1;
        SpawnDraw.enables[2] = enable2;

    }

    static Runnable draw = () -> {
        //0 陆军
        //1 蜘蛛
        //2 海军
        for (int i = 0; i <= 2; i++) {
            if (!enables[i]) continue;
            for (var tile : Vars.spawner.getSpawns()) {
                Draw.z(Layer.flyingUnit + 2.5f);//+ 2.5f);
                Lines.stroke(2, Vars.state.rules.waveTeam.color);
                Draw.color(colors[i], Mathf.absin(Time.time, 8.0F, 1.0F));
                while (true) {
                    Tile nextTile = Vars.pathfinder.getTargetTile(tile, Vars.pathfinder.getField(Vars.state.rules.waveTeam, i, 0));
                    if (nextTile == null || tile == nextTile) {
                        break;
                    }
                    Lines.dashLine(tile.worldx(), tile.worldy(), nextTile.worldx(), nextTile.worldy(),
                            (int) (Mathf.len(nextTile.worldx() - tile.worldx(), nextTile.worldy() - tile.worldy()) / 4f));
                    tile = nextTile;
                }
                Draw.reset();
            }

        }
    };

}

