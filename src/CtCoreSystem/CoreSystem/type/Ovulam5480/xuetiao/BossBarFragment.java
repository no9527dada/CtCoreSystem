package CtCoreSystem.CoreSystem.type.Ovulam5480.xuetiao;

import arc.Core;
import arc.Events;
import arc.func.Boolf;
import arc.func.Func;
import arc.graphics.Color;
import arc.graphics.gl.Shader;
import arc.scene.Group;
import arc.struct.ObjectMap;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
//BOSS血条
public class BossBarFragment {
    private final int time = 60;
    private float timer;
    private @Nullable Unit currentUnit;
    private final ObjectMap<Boolf<Unit>, BarDrawer> barDrawers = new ObjectMap<>();

    private final BossBar bar = new BossBar();

    public BossBarFragment(Group parent){
        parent.fill(table -> {
            if(Core.graphics.getWidth() < Core.graphics.getHeight()){
                table.add(bar).size(Core.graphics.getWidth() / 3f, 64).visible(() -> !(currentUnit == null || currentUnit.dead));
                table.setPosition(0, Core.graphics.getHeight() / 3f);
            }else {
                table.add(bar).size(Core.graphics.getWidth() / 1.5f, 20).visible(() -> !(currentUnit == null || currentUnit.dead));
                table.setPosition(0, Core.graphics.getHeight()/2f-30 );
            }
        });

        Events.run(EventType.Trigger.update, () -> {
            if(currentUnit == null || currentUnit.dead){
                timer += Time.delta;
            }

            if(timer > time){
                boolean find = false;
                for (Unit unit : Groups.unit) {
                    if(unit.team != Vars.player.team()){
                        for (ObjectMap.Entry<Boolf<Unit>, BarDrawer> barDrawer : barDrawers) {
                            if(barDrawer.key.get(unit)){
                                bar.set(barDrawer.value.color, () -> unit.health / unit.maxHealth, barDrawer.value.shaders);
                                //bossBar.blink(Color.white);
                                currentUnit = unit;
                                find = true;
                                break;
                            }
                        }
                        if(find)break;
                    }
                }
                timer = 0;
            }
        });

        Events.on(EventType.WorldLoadEvent.class, e -> currentUnit = null);
//        Events.on(EventType.UnitDamageEvent.class, e -> {
//            if(e.unit == currentUnit){
//                bar.flash(Mathf.clamp((e.bullet.damage / e.unit.maxHealth) - 0.1f, 0, 1));
//            }
//        });
    }

    public void putBarMap(Boolf<Unit> boolf, BarDrawer barDrawer){
        barDrawers.put(boolf, barDrawer);
    }

    public void putBarMap(Boolf<Unit> boolf, Color color, Func<Float, Shader> shaders){
        barDrawers.put(boolf, new BarDrawer(color, shaders));
    }

    public class BarDrawer{
        public Color color;
        public Func<Float, Shader> shaders;

        public BarDrawer(Color color, Func<Float, Shader> shaders) {
            this.color = color;
            this.shaders = shaders;
        }

        public BarDrawer(Color color) {
            this.color = color;
        }
    }
}

