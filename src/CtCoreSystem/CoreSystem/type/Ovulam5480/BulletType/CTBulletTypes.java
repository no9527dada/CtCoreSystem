package CtCoreSystem.CoreSystem.type.Ovulam5480.BulletType;

import arc.graphics.g2d.Lines;
import CtCoreSystem.CoreSystem.type.CTContinuousLaserBulletType;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;
import arc.graphics.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Weapon;
import mindustry.type.unit.MissileUnitType;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;

//没啥用
public class CTBulletTypes {
    public static BulletType
            小旋风子弹1, 导弹1, 遗落, 遗落间隔子弹, laser01, 记挂, 艺璇, 艺璇2,


    子弹类型;

    public static void load() {
/*    buildingDamageMultiplier = 0.1;//对建筑的伤害 10%
        lightning = 50;//根数
        lightningLength = 20; //闪电长度
        lightningDamage = 20; //闪电伤害
splashDamageRadius = 64; //分裂范围
splashDamage = 150; //分裂的伤害
backColor = F.c("FFFFAA"); //背景颜色
frontColor = F.c("00E3E3"); //前面颜色
reloadMultiplier = 1; //装弹速度
knockback = 8; //击退;
pierce = false; //穿透
spin = 18;//子弹旋转
hitEffect=  ; //子弹命中效果
despawnEffect = Fx.hitBulletSmall;//子弹落地效果
shootEffect = Fx.shootSmall;//子弹发射时的效果
smokeEffect = Fx.shootSmallSmoke;//子弹发射时产生的烟雾效果
chargeEffect = Fx.none;//蓄能开始时产生的效果;只能用于带有firstShotDelay / shotDelay的单发武器。* /
hitSound = Sounds.none;//当击中某物或被移除时发出的声音
despawnSound = Sounds.none;//当击中某物或被移除时发出的声音
hitSoundPitch = 1;子弹声音音高，
hitSoundVolume = 1;子弹声音音量
inaccuracy = 3;//精准
shootCone = 30;//射击瞄准角度 最大360
        */
        /*基本子弹 = new BasicBulletType(2.5f, 9){{
            width = 7f;
            height = 9f;
            lifetime = 60f;
            ammoMultiplier = 2;
        }};*/
        小旋风子弹1 = new BasicBulletType(2.5f, 5, "ct-yueya") {{
            width = 5f;
            height = 5f;
            lifetime = 60f;
            ammoMultiplier = 1;//子弹装填倍率
            spin = 20;
            shrinkY = 0;
            shrinkX = 0;
            pierce = true; //穿透
            backColor = Color.valueOf("ffffff"); //背景颜色
            frontColor = Color.valueOf("cba1ff"); //前面颜色
            fragBullet = new BasicBulletType(2.5f, 5, "ct-yueya")//分裂的子弹
            {{
                width = 5f;
                height = 5f;
                lifetime = 45f;
                spin = 20;
                shrinkY = 0;
                shrinkX = 0;
                backColor = Color.valueOf("ffffff"); //背景颜色
                frontColor = Color.valueOf("cba1ff"); //前面颜色
                pierce = true;
                despawnEffect = Fx.none;
            }};
            fragBullets = 1;//分裂的数量
            fragRandomSpread = 0f;//分裂的角度
            fragAngle = 180f;//分裂的角度偏移，一般角度(上一个接口)越小越容易看出来
            fragVelocityMin = 1f;//随机范围的破片速度最小0。
            fragVelocityMax = 1f;//随机范围的破片速度最大1。
            fragLifeMin = 1f;//随机范围的碎片寿命作为乘数最小0
            fragLifeMax = 1f;//随机范围的碎片寿命作为乘数最大1。
            despawnEffect = Fx.none;
            hitEffect = Fx.none;
        }};
        导弹1 = new BulletType() {{
            shootEffect = Fx.sparkShoot;
            smokeEffect = Fx.shootSmokeTitan;
            hitColor = Pal.suppress;
            speed = 0f;
            keepVelocity = false;
            shootEffect = new MultiEffect(Fx.shootBigColor, new Effect(9, e -> {
                color(Color.white, e.color, e.fin());
                stroke(0.7f + e.fout());
                Lines.square(e.x, e.y, e.fin() * 5f, e.rotation + 45f);

                Drawf.light(e.x, e.y, 23f, e.color, e.fout() * 0.7f);
            }), new WaveEffect() {{
                colorFrom = colorTo = Pal.techBlue;
                sizeTo = 15f;
                lifetime = 12f;
                strokeFrom = 3f;
            }});

            spawnUnit = new MissileUnitType("disrupt-missile") {{
                damage = 1500;
                speed = 4.6f;
                maxRange = 5f;
                outlineColor = Pal.darkOutline;
                health = 70;
                homingDelay = 10f;
                lowAltitude = true;
                engineSize = 3f;
                engineColor = trailColor = Pal.sapBulletBack;
                engineLayer = Layer.effect;
                loopSoundVolume = 0.1f;
                deathExplosionEffect = Fx.dynamicExplosion;
                weapons.add(new Weapon() {{
                    shootCone = 360f;
                    mirror = false;
                    reload = 1f;
                    shootOnDeath = true;
                    bullet = new ExplosionBulletType(140f, 25f) {{
                        //suppressionRange = 140f;
                        shootEffect = new ExplosionEffect() {{
                            lifetime = 50f;
                            waveStroke = 5f;
                            waveLife = 8f;
                            waveColor = Color.white;
                            sparkColor = smokeColor = Pal.suppress;
                            waveRad = 40f;
                            smokeSize = 4f;
                            smokes = 7;
                            smokeSizeBase = 0f;
                            sparks = 10;
                            sparkRad = 40f;
                            sparkLen = 6f;
                            sparkStroke = 2f;
                        }};
                    }};
                }});
            }};

        }};

        记挂 = new CTContinuousLaserBulletType(4/*1:12伤害*/) {{
            length = 200f;
            hitEffect = Fx.hitMeltdown;
            hitColor = Pal.meltdownHit;
            status = StatusEffects.melting;
            drawSize = 420f;
            lifetime = 1200f;
            incendChance = 0.4f;
            incendSpread = 5f;
            incendAmount = 1;
            ammoMultiplier = 1f;

        }};
        laser01 = new CTContinuousLaserBulletType(120 / 12/*1:12伤害*/) {{
            colors = new Color[]{Color.valueOf("ec555555"), Color.valueOf("ec8888aa"), Color.valueOf("ff6c6a"), Color.white};
            width = 5;//宽
            length = 5;//长
            largeHit = true;
            hitEffect = Fx.hitMeltdown;
            hitColor = Pal.meltdownHit;
            drawSize = 40;
            incendAmount = 1;
            incendSpread = 5;
            incendChance = 0.4f;
            lightColor = Color.orange;

            lifetime = 600f;
            speed = 0;

            trailParam = 4;
            trailLength = 8;
            trailWidth = 2;
            trailInterval = 5;
            trailRotation = true;
            trailEffect = Fx.none;
            trailColor = Color.valueOf("8dfed4ff");
        }};

        遗落 = new BasicBulletType(4f, 120) {{
            width = 0f;
            height = 0f;
            lifetime = 120f;
            pierce = true; //穿透
            backColor = Color.valueOf("ffffff"); //背景颜色
            frontColor = Color.valueOf("cba1ff"); //前面颜色

            bulletInterval = 20;//子弹刷出的时间间隔 1s:60
            intervalBullet = laser01;
            intervalRandomSpread = -1;
            intervalBullets = 1;//每个间隔产生的子弹数
            intervalAngle = 0;//间隔弹的角度偏移。
            intervalSpread = 0;//各个间隔弹之间的夹角。

            trailWidth = 3f;
            trailLength = 8;
        }};

        艺璇 = new BasicBulletType(2.5f, 30) {{
            sprite = "mine-bullet";
            width = height = 8f;
            layer = Layer.scorch;
            shootEffect = smokeEffect = Fx.none;

            //   maxRange = 50f;//子弹射程确定覆盖范围。
            // ignoreRotation = true;//放在单位武器里的  射击时是否忽略射击者的旋转。
            // healPercent = 4f;//修复方块

            backColor = Pal.heal;
            frontColor = Color.white;
            mixColorTo = Color.white;
            hitSound = Sounds.none;
            hitSize = 22f;
            collidesAir = true;//这种子弹类型是否与空中/地面单位碰撞。
            lifetime = 120f;
            hitEffect = new MultiEffect(Fx.blastExplosion, Fx.greenCloud);
            shrinkX = shrinkY = 0f;
            reloadMultiplier = 2.5f; //装弹速度
            inaccuracy = 2f;
            weaveMag = 5f;
            weaveScale = 4f;

            //collideFloor = true;
            trailColor = Pal.heal;
            trailWidth = 3f;
            trailLength = 8;

            fragBullet = new BasicBulletType(0f, 5, "ct-yueya")//分裂的子弹
            {{
                width = 15f;
                height = 15f;
                lifetime = 240f;
                spin = 50;//旋转
                shrinkY = 0;
                shrinkX = 0;
                backColor = Color.valueOf("ffffff"); //背景颜色
                frontColor = Color.valueOf("cba1ff"); //前面颜色
                pierce = true;
                despawnEffect = Fx.none;

                bulletInterval = 5;//子弹刷出的时间间隔 1s:60
                intervalBullet = new BasicBulletType(4, 26) {{
                    width = 5f;
                    height = 5f;
                    lifetime = 20f;
                    pierce = true; //穿透
                    backColor = Color.valueOf("ffffff"); //背景颜色
                    frontColor = Color.valueOf("cba1ff"); //前面颜色
                    lightning = 2;//根数
                    lightningLength = 5; //闪电长度
                    lightningDamage = 12; //闪电伤害
                    trailWidth = 3f;//拖尾
                    trailLength = 8;//拖尾

                }};
                ;
                intervalRandomSpread = 360;//随机散布间隔子弹的角度
                intervalBullets = 1;//每个间隔产生的子弹数
                intervalAngle = 0;//间隔弹的角度偏移。
                intervalSpread = 0;//各个间隔弹之间的夹角。
            }};
            fragBullets = 1;//分裂的数量
            fragRandomSpread = 0f;//分裂的角度
            fragAngle = 0f;//分裂的角度偏移，一般角度(上一个接口)越小越容易看出来
            fragVelocityMin = 0.5f;//随机范围的破片速度最小0。
            fragVelocityMax = 1f;//随机范围的破片速度最大1。
            fragLifeMin = 0.5f;//随机范围的碎片寿命作为乘数最小0
            fragLifeMax = 1f;//随机范围的碎片寿命作为乘数最大1。
        }};

        艺璇2 = new BasicBulletType(4.5f, 9) {{
            width = 7f;
            height = 9f;
            lifetime = 60f;
            ammoMultiplier = 2;
            reloadMultiplier = 2.5f; //装弹速度
            fragBullet = new LaserBulletType(80) {{
                colors = new Color[]{Pal.lancerLaser.cpy().a(0.4f), Pal.lancerLaser, Color.white};

                chargeEffect = new MultiEffect(Fx.lancerLaserCharge, Fx.lancerLaserChargeBegin);

                buildingDamageMultiplier = 0.25f;//对建筑的伤害衰减
                hitEffect = Fx.hitLancer;
                hitSize = 4;
                lifetime = 16f;
                drawSize = 400f;
                // collidesAir = false;
                length = 90f;
                ammoMultiplier = 1f;
                pierceCap = 4;
            }};
            fragBullets = 5;//分裂的数量
            fragRandomSpread = 360f;//分裂的角度
            fragAngle = 0f;//分裂的角度偏移，一般角度(上一个接口)越小越容易看出来
            fragVelocityMin = 0.5f;//随机范围的破片速度最小0。
            fragVelocityMax = 1f;//随机范围的破片速度最大1。
            fragLifeMin = 0.5f;//随机范围的碎片寿命作为乘数最小0
            fragLifeMax = 1f;//随机范围的碎片寿命作为乘数最大1。
        }};



/*

           laser01 =new BasicBulletType() {
            @Override
            public void init(Bullet b){
              super.init(b);
                if(!continuous){
                    applyDamage(b);
                }return;

                //if (!b) { return; }
                if (!b.data) { b.data = {}; }
                b.data.bullet1 = laser01.create(b, b.x, b.y, 0);
                b.data.bullet2 = laser01.create(b, b.x, b.y, 120);
                b.data.bullet3 = laser01.create(b, b.x, b.y, 240);
                b.data.bullet4 = laser02.create(b, b.x, b.y, 0);
                b.data.bullet5 = laser02.create(b, b.x, b.y, 120);
                b.data.bullet6 = laser02.create(b, b.x, b.y, 240);
                b.data.bulletRot = 0;
                b.data.direction = b.id % 2 == 0;
            }

            @Override
            public void update(Bullet b){
                super.update(b);
                b.data.bulletRot += 4;
                b.data.bullet1.time = 0;
                b.data.bullet2.time = 0;
                b.data.bullet3.time = 0;
                b.data.bullet4.time = 0;
                b.data.bullet5.time = 0;
                b.data.bullet6.time = 0;
                b.data.bullet1.set(b.x, b.y);
                b.data.bullet2.set(b.x, b.y);
                b.data.bullet3.set(b.x, b.y);
                b.data.bullet4.set(b.x, b.y);
                b.data.bullet5.set(b.x, b.y);
                b.data.bullet6.set(b.x, b.y);
                if (b.data.direction) {
                    b.data.bullet1.rotation(b.rotation() + b.data.bulletRot);
                    b.data.bullet2.rotation(b.rotation() + b.data.bulletRot + 120);
                    b.data.bullet3.rotation(b.rotation() + b.data.bulletRot + 240);
                    b.data.bullet4.rotation(b.rotation() + b.data.bulletRot + 60);
                    b.data.bullet5.rotation(b.rotation() + b.data.bulletRot + 180);
                    b.data.bullet6.rotation(b.rotation() + b.data.bulletRot + 300);
                } else {
                    b.data.bullet1.rotation(b.rotation() - b.data.bulletRot);
                    b.data.bullet2.rotation(b.rotation() - b.data.bulletRot + 120);
                    b.data.bullet3.rotation(b.rotation() - b.data.bulletRot + 240);
                    b.data.bullet4.rotation(b.rotation() - b.data.bulletRot + 60);
                    b.data.bullet5.rotation(b.rotation() - b.data.bulletRot + 180);
                    b.data.bullet6.rotation(b.rotation() - b.data.bulletRot + 300);
                }


                if(!continuous) return;

                //damage every 5 ticks
                if(b.timer(1, damageInterval)){
                    applyDamage(b);
                }

                if(shake > 0){
                    Effect.shake(shake, shake, b);
                }


            }

*/

    }
}

