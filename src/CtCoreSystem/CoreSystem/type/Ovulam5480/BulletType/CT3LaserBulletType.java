package CtCoreSystem.CoreSystem.type.Ovulam5480.BulletType;


import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;

import static CtCoreSystem.CoreSystem.type.CTColor.C;
//熔毁吸敌人炮
public class CT3LaserBulletType extends ContinuousLaserBulletType {
    public float 圆环间隔 = 32f;//改大就间隔更小, 改小就间隔更大
    public float 圆环大小 = 0.8f;
    public float roundWidth = 16f, roundHeight = 24f;
    public Effect chain = Fx.chainLightning;
    public float 闪电范围 = 60;
    public Color 闪电颜色 = C("ec7458");
    public Color 圈颜色 = Color.valueOf("ffaa97");

    //牵引范围的半径
    public float qian = 128f;
    //拉扯力度 这个数字好像调非常大也效果不明显
    public float force = 1f;
    //////////////
    public int timer;
    Vec2 v = new Vec2();

    public CT3LaserBulletType(float damage) {
        colors = new Color[]{Color.valueOf("ec745855"), Color.valueOf("ec7458aa"), Color.valueOf("ff9c5a"), Color.white};
        this.damage = damage;
    }

    @Override
    public void update(Bullet b) {
        float realLength = Damage.findLaserLength(b, length);
        //float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f);
        float fout =1f;
        float baseLen = realLength * fout;

        Tmp.v1.trns(b.rotation(), baseLen);
        Vec2 link = new Vec2(b.x + Tmp.v1.x, b.y + Tmp.v1.y);

        /////////////////单位牵引///////////////
        Units.nearbyEnemies(b.team,
                Math.min(link.x, b.x), Math.min(link.y, b.y),
                Math.abs(link.x - b.x) * 1.5f, Math.abs(link.y - b.y) * 1.5f,
                unit -> {

                    if (near(unit, b.x, b.y, link.x, link.y)) {
                        Intersector.nearestSegmentPoint(b.x, b.y, link.x, link.y, unit.x, unit.y, v);
                        unit.moveAt(Tmp.v1.set(v).sub(unit).limit((force)));
                    }
                });

        super.update(b);

        ////////////////闪电特效///////////////////

        int t = (int) (Time.time / 300 * 60) % 6;
        if (t != timer) {
            timer = t;

            for (int i = 0; i < 4; i++) {
                float dog = Mathf.random(-0.75f, 0.75f);
                float offX = (float) (Math.sin(Mathf.degreesToRadians * -b.rotation()) * 闪电范围 * dog);
                float offY = (float) (Math.cos(Mathf.degreesToRadians * -b.rotation()) * 闪电范围 * dog);
                Vec2 vec2 = new Vec2(link.x + offX, link.y + offY);
                chain.at(b.x + offX, b.y + offY, b.rotation(), 闪电颜色, vec2);
            }
        }
    }

    public boolean near(Unit unit, float x, float y, float x2, float y2) {
        return Intersector.distanceLinePoint(x, y, x2, y2, unit.x, unit.y) < qian;
    }

    @Override
    public void draw(Bullet b) {
        //激光长度，建筑绝缘
        float realLength = Damage.findLaserLength(b, length);
        float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f);
        float baseLen = realLength * fout;
        float rot = b.rotation();

        /////////////激光////////////
        Draw.z(Layer.effect);

        for (int i = 0; i < colors.length; i++) {
            Draw.color(Tmp.c1.set(colors[i]).mul(1f + Mathf.absin(Time.time, 1f, 0.1f)));

            float colorFin = i / (float) (colors.length - 1);
            float baseStroke = Mathf.lerp(strokeFrom, strokeTo, colorFin);
            float stroke = (width + Mathf.absin(Time.time, oscScl, oscMag)) * baseStroke;

            Lines.stroke(stroke);
            Lines.lineAngle(b.x, b.y, rot, baseLen - frontLength, false);

            //back ellipse
            Drawf.flameFront(b.x, b.y, divisions, rot + 180f, backLength, stroke / 2f);

            //front ellipse
            Tmp.v1.trnsExact(rot, baseLen - frontLength);
            Drawf.flameFront(b.x + Tmp.v1.x, b.y + Tmp.v1.y, divisions, rot, frontLength, stroke / 2f);
        }

        Tmp.v1.trns(b.rotation(), baseLen * 1.1f);

        Drawf.light(b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, lightStroke, lightColor, 0.7f);
        Draw.reset();

        Lines.stroke(2.3f);//圈粗细？
        Draw.color(圈颜色
                // Pal.remove
        );
        float lfin = (Time.time % 20f) / 20f;
        int amount = (int) (baseLen / (圆环间隔)) + 2;
        float len = baseLen / amount;

        for (int i = 0; i < amount; i++) {
            float scl = 1;
            if (i == 0) scl = lfin;
            else if (i == amount - 1) scl = 1 - lfin;

            float fx = (float) (b.x + (i + lfin) * len * Math.cos(Mathf.degreesToRadians * rot));
            float fy = (float) (b.y + (i + lfin) * len * Math.sin(Mathf.degreesToRadians * rot));

            Lines.ellipse(fx, fy, scl * 圆环大小, roundWidth, roundHeight, b.rotation());
        }
        Draw.reset();
    }
}