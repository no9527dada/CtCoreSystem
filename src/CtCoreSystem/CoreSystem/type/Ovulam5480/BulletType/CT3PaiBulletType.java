package CtCoreSystem.CoreSystem.type.Ovulam5480.BulletType;


import arc.graphics.Color;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
//一排 很宽的子弹 幻想工程的排山倒海炮在用
public class CT3PaiBulletType extends BasicBulletType {
    public float 子弹宽度 = 200f;

    public boolean 显示伤害线 = false;

    public float shake = 0f;
    public float damageInterval = 0f;
    public boolean largeHit = false;
    public boolean continuous = true;

    public CT3PaiBulletType (){
        removeAfterPierce = false;
        pierceCap = -1;
        despawnEffect = Fx.none;
        shootEffect = Fx.none;
        impact = true;
        collides = false;
        pierce = true;
        hittable = false;
        absorbable = false;
        speed = 3f;
    }

    @Override
    public float continuousDamage(){
        if(!continuous) return -1f;
        return damage;
       // return damage / damageInterval * 60f;
    }

    @Override
    public float estimateDPS(){
        if(!continuous) return super.estimateDPS();
        //assume firing duration is about 100 by default, may not be accurate there's no way of knowing in this method
        //assume it pierces 3 blocks/units
        return damage * 100f / damageInterval * 3f;
    }

    @Override
    protected float calculateRange(){
        return Math.max(子弹宽度, maxRange);
    }

    @Override
    public void init(){
        super.init();

        drawSize = Math.max(drawSize, 子弹宽度*2f);
    }

    @Override
    public void init(Bullet b){
        super.init(b);

        if(!continuous){
            applyDamage(b);
        }
    }

    @Override
    public void draw(Bullet b){
        if(显示伤害线){
            float x1 = 子弹宽度 / 2f * Mathf.cosDeg(b.rotation() - 90) + b.x;
            float y1 = 子弹宽度 / 2f * Mathf.sinDeg(b.rotation() - 90) + b.y;
            float x2 = 子弹宽度 / 2f * Mathf.cosDeg(b.rotation() + 90) + b.x;
            float y2 = 子弹宽度 / 2f * Mathf.sinDeg(b.rotation() + 90) + b.y;
            Drawf.line(Color.pink, x1, y1, x2, y2);}

        super.draw(b);
    }

   @Override
    public void update(Bullet b){
        if(!continuous) return;

        //damage every 5 ticks
        if(b.timer(1, damageInterval)){
            applyDamage(b);
        }

        if(shake > 0){
            Effect.shake(shake, shake, b);
        }
    }

    public void applyDamage(Bullet b){
        float x1 = 子弹宽度 / 2f * Mathf.cosDeg(b.rotation() - 90) + b.x;
        float y1 = 子弹宽度 / 2f * Mathf.sinDeg(b.rotation() - 90) + b.y;

        Damage.collideLine(b, b.team,/* hitEffect,*/ x1, y1, b.rotation() + 90f, currentLength(b), largeHit, false, pierceCap);
    }

    public float currentLength(Bullet b){
        return 子弹宽度;
    }
}
