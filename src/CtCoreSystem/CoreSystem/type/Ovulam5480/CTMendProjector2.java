package CtCoreSystem.CoreSystem.type.Ovulam5480;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.world.blocks.defense.MendProjector;

import static CtCoreSystem.CtCoreSystem.修复超速仪范围显示开关;
import static mindustry.Vars.indexer;
import static mindustry.Vars.tilesize;

/**
 * 非百分比的数值修复器
 */
public class CTMendProjector2 extends MendProjector {
    public float healAmount;//修复的数值

    public CTMendProjector2(String name) {
        super(name);
    }
    public class CTMendBuild extends MendBuild {
        @Override
        public void draw(){
            super.draw();

            float f = 1f - (Time.time / 100f) % 1f;
            float realRange = range + phaseHeat * phaseRangeBoost;
            Draw.color(baseColor, phaseColor, phaseHeat);
            Draw.alpha(heat * Mathf.absin(Time.time, 50f / Mathf.PI2, 1f) * 0.5f);
            Draw.rect(topRegion, x, y);
            Draw.alpha(1f);
            Lines.stroke((2f * f + 0.2f) * heat);
            Lines.square(x, y, Math.min(1f + (1f - f) * size * tilesize / 2f, size * tilesize/2f));
            Draw.reset();
            Lines.stroke(1f, baseColor.a(!修复超速仪范围显示开关 ? 0.3f : 0f));
            Lines.dashCircle(x, y, realRange);

        }
    }
}