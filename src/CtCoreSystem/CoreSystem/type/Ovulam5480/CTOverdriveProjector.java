package CtCoreSystem.CoreSystem.type.Ovulam5480;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.util.Time;
import mindustry.graphics.Layer;
import mindustry.world.blocks.defense.OverdriveProjector;
public class CTOverdriveProjector extends OverdriveProjector {
    public float 间隔 = 180f;
    public CTOverdriveProjector(String name) {
        super(name);
        chuxi=4f;
    }
    public float chuxi;
    public class CTOverdriveProjectorBuild extends OverdriveBuild{
        @Override
        public void draw() {
            super.draw();

            float realRange = range + phaseHeat * phaseRangeBoost;
            float progress = (Time.time % 间隔) / 间隔;

            Lines.stroke((1 - progress) * chuxi, baseColor.a(efficiency));
            Draw.z(Layer.effect);

            Lines.circle(x,y,realRange * progress);

            Draw.reset();
            Lines.stroke(1);
        }
    }
}
