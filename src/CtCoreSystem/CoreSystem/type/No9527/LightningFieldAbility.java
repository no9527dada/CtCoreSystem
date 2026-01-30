package CtCoreSystem.CoreSystem.type.No9527;

import arc.Core;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;

import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;

import mindustry.entities.Effect;
import mindustry.entities.Lightning;
import mindustry.entities.Units;
import mindustry.gen.Unit;

import mindustry.Vars;
import mindustry.entities.abilities.Ability;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;

//死星中心的光球特效
public class LightningFieldAbility extends Ability {
    private final float damage;
    private final float reload;
    private final float range;
    private final Color color;
    private final int maxFind;

    // 常量定义
    private static final int sectors = 5;
    private static final float sectorRad = 0.14f;
    private static final float blinkScl = 20f;
    private static final float rotateSpeed = 0.5f;
    private static final float effectRadius = 15f;
    private static final float chargeTime = 20f;

    // 实例变量
    private float x = 0;
    private final float y = 40;
    private float timer = 0;
    private float curStroke = 0;
    private boolean find = false;
    private final Seq<Unit> target = new Seq<>();

    // 构造函数
    public LightningFieldAbility(float damage, float reload, float range, Color color, int maxFind) {
        this.damage = damage;
        this.reload = reload;
        this.range = range;
        this.color = color;
        this.maxFind = maxFind;
    }

    @Override
    public String localized() {
        return Core.bundle.format("T6sixingg", damage, range / Vars.tilesize, maxFind);
    }

    @Override
    public void draw(Unit unit) {
        Draw.z(Layer.bullet - 0.001f);
        Draw.color(color);

        Tmp.v1.trns(unit.rotation - 90, x, y).add(unit.x, unit.y);
        float rx = Tmp.v1.x;
        float ry = Tmp.v1.y;
        float orbRadius = effectRadius * (1 + Mathf.absin(blinkScl, 0.1f));

        Fill.circle(rx, ry, orbRadius);
        Draw.color();
        Fill.circle(rx, ry, orbRadius / 2);

        Lines.stroke((0.7f + Mathf.absin(blinkScl, 0.7f)), color);

        for (int i = 0; i < sectors; i++) {
            float rot = unit.rotation + i * 360f / sectors - Time.time * rotateSpeed;
            Lines.arc(rx, ry, orbRadius + 3f, sectorRad, rot);
        }

        Lines.stroke(Lines.getStroke() * curStroke);

        if (curStroke > 0) {
            for (int i = 0; i < sectors; i++) {
                float rot = unit.rotation + i * 360f / sectors + Time.time * rotateSpeed;
                Lines.arc(rx, ry, range, sectorRad, rot);
            }
        }

        Drawf.light(rx, ry, range * 1.5f, color, 0.8f);
        Draw.reset();
    }

    @Override
    public void update(Unit unit) {
        timer = Math.min(timer + Time.delta, reload);
        curStroke = Mathf.lerpDelta(curStroke, find ? 1f : 0f, 0.09f);

        // 锁定多个目标（群体友方选择）
        if (timer >= reload) {
            find = false;
            target.clear();

            Units.nearby(null, unit.x, unit.y, range, other -> {
                if (other.team != unit.team) {
                    target.add(other);
                }
            });

            target.sort(u -> u.dst2(unit));

            int max = Math.min(maxFind, target.size);
            for (int a = 0; a < max; a++) {
                Unit other = target.get(a);
                find = true;

                new Effect(12, e -> {
                    Draw.color(color);
                    Lines.circle(e.x, e.y, e.fin() * range);
                    Draw.reset();
                }).at(unit);

                Fx.chainLightning.at(unit.x, unit.y + 40, 0, color, other);
                other.apply(StatusEffects.unmoving, 30);

                for (int i = 0; i < 4; i++) {
                    Lightning.create(unit.team, color, damage / 4, other.x, other.y + 40, Mathf.range(180f), 10);
                }

                timer = 0;
            }
        }

        if (Mathf.chance(0.05)) {
            float a = unit.rotation + Mathf.range(180f) + 0;
            Lightning.create(unit.team, color, damage,
                    unit.x + Angles.trnsx(unit.rotation, 40, 0),
                    unit.y + Angles.trnsy(unit.rotation, 40, 0),
                    a, 8);
        }
    }

    @Override
    public Ability copy() {
        return new LightningFieldAbility(damage, reload, range, color, maxFind);
    }
}