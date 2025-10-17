package CtCoreSystem.CoreSystem.type.No9527;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

//死亡收割者特效能力
public class drawAbility extends Ability {
    public drawAbility() {
        super();
    }

    public void drawAbility( Color color) {

    }

    @Override
    public void draw(Unit unit) {
        super.draw(unit);
        Color color = Pal.heal;
        var rx = Tmp.v1.x;
        var ry = Tmp.v1.y;
        float effectRadius = 10;
        float blinkScl = 20;
        int sectors = 5;
        float rotateSpeed = 0.5f;
        float sectorRad = 0.14f;
        float curStroke = 0;
        float range = 30 * 8;


        Draw.z(Layer.bullet - 0.001f);
        Draw.color(color);
        Tmp.v1.trns(unit.rotation - 90, 0, 0).add(unit.x, unit.y);


        var orbRadius = effectRadius * (1 + Mathf.absin(blinkScl, 0.1f));

        Fill.circle(rx, ry, orbRadius);
        Draw.color();
        Fill.circle(rx, ry, orbRadius / 2);

        Lines.stroke((0.7F + Mathf.absin(blinkScl, 0.7f)), color);

        for (var i = 0; i < sectors; i++) {
            var rot = unit.rotation + i * 360 / sectors - Time.time * rotateSpeed;
            Lines.arc(rx, ry, orbRadius + 3, sectorRad, rot);
        }

        Lines.stroke(Lines.getStroke() * curStroke);

        if (curStroke > 0) {
            for (var i = 0; i < sectors; i++) {
                var rot = unit.rotation + i * 360 / sectors + Time.time * rotateSpeed;
                Lines.arc(rx, ry, range, sectorRad, rot);
            }
        }
        Drawf.light(rx, ry, range * 1.5f, color, 0.8f);
        Draw.reset();
    }

}
