package CtCoreSystem.CoreSystem.type.Ovulam5480.BulletType;

import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.Rand;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Pal;

import static arc.scene.actions.Actions.color;

//转圈子弹
public class RoundBulletType extends BulletType {
    public float radius = 48f;
    public float angle = 22f;

    public RoundBulletType() {
        speed = 0;
        lifetime = 120f;
        despawnEffect = Fx.none;
        collides = false;
        hittable = false;

        pierce = true;
        //pierceCap = 999;
    }

    public static final Rand rand = new Rand();
    public Effect e = new Effect(20f, e -> {
        color(Pal.accent);
        Lines.stroke(e.fout() * 2);
        float spread = 16f;

        float angle = e.rotation + e.finpow() * this.angle * 9;
        float rad = this.radius;

        float x = Mathf.cosDeg(angle) * rad;
        float y = Mathf.sinDeg(angle) * rad;

        float rfin = (1 + e.fin() * 2) / 3f;

        rand.setSeed(e.id);
        for (int i = 0; i < 20; i++) {
            float ang = angle + rand.range(14f) + 89f;
            Lines.lineAngle(e.x + x + rand.range(spread) * rfin, e.y + y + rand.range(spread) * rfin, ang, e.fout() * 10f * rand.random(1f) + 1f);
        }
    });

    @Override
    public void update(Bullet b) {
        super.update(b);

        float a = b.time * angle;

        e.at(b.x, b.y, a, radius);

        if (b.timer(1, 5)) {
            for (int i = 0; i < 12; i++) {
                float an = a + 30 * i;

                float x = Mathf.cosDeg(an) * radius + b.x;
                float y = Mathf.sinDeg(an) * radius + b.y;

                float x2 = Mathf.cosDeg(an + 30) * radius + b.x;
                float y2 = Mathf.sinDeg(an + 30) * radius + b.y;
                float dst = Mathf.dst(x, y, x2, y2);

                Damage.collideLine(b, b.team,/* Fx.none,*/ x, y, an + 90, dst, true, false, pierceCap);
            }
        }
    }
}
