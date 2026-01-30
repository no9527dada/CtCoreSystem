package CtCoreSystem.content;

import CtCoreSystem.CoreSystem.type.Fire.CTBeamExtractor;
import CtCoreSystem.CoreSystem.type.No9527.FanOverdriveProjector;
import CtCoreSystem.CoreSystem.type.No9527.建筑贴图隐藏.*;
import CtCoreSystem.content.Effect.NewFx;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.entities.Effect;
import mindustry.entities.Lightning;
import mindustry.entities.bullet.LightningBulletType;
import mindustry.entities.bullet.PointBulletType;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.BuildVisibility;

import static creators.CTItem.tanban;

public class 实验产物 {

    public static 实验产物_子弹 实验产物_子弹;

    public static void load() {
        FanOverdriveProjector 实验产物_fan = new FanOverdriveProjector("实验产物fan"){{
            group = BlockGroup.transportation;
            category = Category.crafting;
            buildVisibility = BuildVisibility.shown;
        }};

        CTBeamExtractor 实验产物_光束提取器 = new CTBeamExtractor("实验产物光束提取器");
        new newGenericCrafter("实验产物chang") {{
            health = 210;
            size = 4;
            hasPower = true;
            canOverdrive = true;
            hasLiquids = true;
            hasItems = true;
            localizedName="实验产物净化厂";
            updateEffect = Fx.lightningCharge;
            craftEffect = NewFx.正套圆(1,1,Color.valueOf("acff80"));
            requirements = ItemStack.with(

            );
            craftTime = 210;
            outputItem = new ItemStack(
                    Items.copper, 5
            );
            group = BlockGroup.transportation;
            category = Category.crafting;
            buildVisibility = BuildVisibility.shown;
        }};

        实验产物_子弹 = new 实验产物_子弹();

        new PowerTurret("实验产物") {
            {
                float brange = range = 140*8f;
                health = 1000000;
                requirements = ItemStack.with(
                        Items.copper, 2500
                );

                buildVisibility = BuildVisibility.shown;
                category = Category.turret;
                reload = 40f;
                //shootType = new 实验产物_子弹2();
                shootType = new PointBulletType() {{
                    shootEffect = Fx.instShoot;
                    hitEffect = Fx.none;
                    smokeEffect = Fx.smokeCloud;
                    trailEffect = Fx.instTrail;
                    despawnEffect = Fx.none;
                    trailSpacing = 20f;
                    damage = 0;
                    buildingDamageMultiplier = 0.2f;
                    speed = brange;
                    hitShake = 6f;
                    ammoMultiplier = 1f;
                    fragBullets = 1;
                    fragBullet =  new 实验产物_子弹();
                }};
            }
        };

    }

    public static class 实验产物_子弹 extends LightningBulletType {
        // 十字型闪电的四个方向
        private static final float[] directions = {0f, 90f, 180f, 270f};
        // 初始闪电长度
        private static final int initialLength = 5;
        // 最大闪电长度
        private static final int maxLength = 25;
        // 闪电颜色
        private static Color lightningColor = Color.valueOf("a9d8ff");

        public 实验产物_子弹() {
            damage = 100;
            speed = 0f;
            lifetime = 60f;
            //穿透能力
            pierce = true;
            absorbable = false;//子弹不被护盾仪吸收
            despawnEffect = Fx.none;
            hitEffect = Fx.none;
            keepVelocity = false;
            hittable = false;
            lightningLength = initialLength;
            lightningColor = 实验产物_子弹.lightningColor;
        }

        @Override
        public void init(mindustry.gen.Bullet b) {
            super.init(b);

            // 创建十字型闪电
            for (float direction : directions) {
                Lightning.create(b, lightningColor, damage, b.x, b.y, direction, initialLength);
            }
        }

        @Override
        public void update(mindustry.gen.Bullet b) {
            super.update(b);

            // 计算当前闪电长度（随时间增长）
            float progress = b.time / lifetime;
            int currentLength = (int) Mathf.lerp(initialLength, maxLength, progress);

            // 更新十字型闪电
            if (b.timer(0, 5)) {
                for (float direction : directions) {
                    Lightning.create(b, lightningColor, damage, b.x, b.y, direction, currentLength);
                }
            }

            // 绘制中心圆圈
            Fx.pointShockwave.at(b.x, b.y, 30f * progress, lightningColor);
        }

    }
}
