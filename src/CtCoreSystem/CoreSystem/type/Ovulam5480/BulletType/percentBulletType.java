package CtCoreSystem.CoreSystem.type.Ovulam5480.BulletType;


import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Healthc;
import mindustry.gen.Hitboxc;
public class percentBulletType {

    public static class maxHealthBulletType extends BasicBulletType {
        //百分比, 填写40表示造成40%的血量上限的伤害
        float percent;

        public maxHealthBulletType(float percent) {
            this.percent = percent;
        }

        @Override
        public void hitEntity(Bullet b, Hitboxc entity, float health) {
            if (entity instanceof Healthc h) {
                h.damagePierce(h.maxHealth() * percent / 100);
            }
            super.hitEntity(b, entity, health);
            absorbable = false;//子弹不被护盾仪吸收
            damage = 0;//伤害为0
        }
    }
    public static class healthBulletType extends BasicBulletType {
        //百分比, 填写40表示造成40%的当前血量的伤害
        float percent;

        public healthBulletType(float percent) {
            this.percent = percent;
        }

        @Override
        public void hitEntity(Bullet b, Hitboxc entity, float health) {
            if (entity instanceof Healthc h) {
                h.damagePierce(h.health() * percent / 100);
            }
            super.hitEntity(b, entity, health);
        }
    }
}








