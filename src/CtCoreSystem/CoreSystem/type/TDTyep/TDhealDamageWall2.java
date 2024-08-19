package CtCoreSystem.CoreSystem.type.TDTyep;

import mindustry.content.Fx;
import mindustry.graphics.Pal;
import mindustry.world.blocks.defense.Wall;

//回血和扣血 的墙
public class TDhealDamageWall2 {
    public static  class TDhealDamageWall extends Wall {
        //这个掉血也会延长回血时间, 所以不能在掉血的同时回血
        //伤害百分比, 负值时为回血
        public float damagePercent = 0f;
        //固定数值伤害, 负值时为回血
        public float damageAmount = 0f;
        //受伤或者回血间隔
        public float damageTime = 120f;
        //受伤不能回血时间
        public float pauseTime = 300f;

        public TDhealDamageWall(String name) {
            super(name);
            health = 4000;
            update = true;
            sync = true;
            canOverdrive = false;
        }

        public class TDhealDamageWallBuild extends WallBuild {
            public float damageTimer, pauseTimer;

            @Override
            public void updateTile() {
                boolean isDamage = damagePercent > 0 || damageAmount > 0;

                if(pauseTimer > 0 && !isDamage){
                    pauseTimer -= delta();
                }else {
                    damageTimer += delta();
                }

                if (damageTimer > damageTime) {
                    if(isDamage){
                        damage(damageAmount);
                        damage(damagePercent * maxHealth / 100);
                    }else {
                        heal(-damageAmount);
                        heal(-damagePercent * maxHealth / 100);
                    }

                    damageTimer = 0f;
                }
            }

            @Override
            public void damage(float damage) {
                super.damage(damage);
                pauseTimer = pauseTime;
            }
        }
    }

}
