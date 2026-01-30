package CtCoreSystem.CoreSystem.type.No9527;

import arc.graphics.Color;
import arc.math.Interp;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.ContinuousFlameBulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.gen.Sounds;
import mindustry.type.Weapon;

public class 单位尾焰特效 extends Weapon {
    public  单位尾焰特效(float X,float Y, Boolean 镜像,float 角度,float 偏移Y,Color 主体色,Color 淡主体色,float 尾焰伤害,float 尾焰宽度,float 尾焰长度) {
        name= "engine2";
        x= X;
        y= Y;
        mirror= 镜像;
        alternate= false;
        top= true;//显示在贴图上层
        display= false;
        rotate= false;
        baseRotation= 角度;
        shootY= 偏移Y;
        shootSound= Sounds.none;
        alwaysShooting= true;
        alwaysContinuous= true;
        continuous= true;
        parentizeEffects= true;
        bullet=new ContinuousFlameBulletType() {{
            colors = new Color[]{
                    主体色.a(0.5f),
                    主体色,
                    淡主体色,
                    Color.white
            };
            damage= 尾焰伤害;
            layer= 110;
            intervalBullets= 2;
            intervalRandomSpread= 1;
            bulletInterval= 2.7f;
            drawFlare= false;
            collides= false;
            width= 尾焰宽度;
            length= 尾焰长度;
            divisions= 20;
            intervalBullet= new BulletType() {{
                despawnHit= true;
                despawnEffect= Fx.none;
                shootEffect= Fx.none;
                hitEffect=new MultiEffect(){{
                    followParent= true;
                    rotWithParent= true;
                    lifetime= 0;
                    instantDisappear= true;
                    effects= new Effect[]{
                            new ParticleEffect(){{
                                followParent= true;
                                rotWithParent= true;
                                line= true;
                                layer= 108;
                                particles= 1;
                                lifetime= 31;
                                interp= Interp.circleOut;
                                length= 45;
                                baseLength= 8;
                                cone= 25;
                                strokeFrom= 2;
                                lenFrom= 6;
                                lenTo= 0;
                                colorFrom=主体色;
                                colorTo=主体色;
                            }}
                    };
                }};
            }};
        }};
    }

 /*   public void 尾焰特效(float X,float Y,float 角度,float 偏移Y) {
        尾焰特效(X,Y, true,角度,偏移Y, Color.valueOf("ff5959"), Color.valueOf("ff9191"),50,5.3f,20);
    }
    public void 尾焰特效(float X,float Y,Boolean 镜像,float 角度,float 偏移Y) {
        尾焰特效(X,Y, 镜像,角度,偏移Y, Color.valueOf("ff5959"), Color.valueOf("ff9191"),50,5.3f,20);
    }
    public void 尾焰特效() {
        尾焰特效(0,-50, false,180,10, Color.valueOf("ff5959"), Color.valueOf("ff9191"),50,1f,5);
    }*/
}
