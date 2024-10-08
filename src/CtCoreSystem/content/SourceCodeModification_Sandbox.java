package CtCoreSystem.content;

import CtCoreSystem.CoreSystem.type.CT3UnitType;
import CtCoreSystem.CoreSystem.type.TDTyep.TDBuffChange;
import CtCoreSystem.CoreSystem.type.XVXSource;
import CtCoreSystem.CoreSystem.type.waveRule;
import arc.Core;
import arc.graphics.Color;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.ai.types.BuilderAI;
import mindustry.content.Fx;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.Weapon;
import mindustry.type.ammo.ItemAmmoType;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.logic.LogicBlock;
import mindustry.world.blocks.payloads.PayloadSource;
import mindustry.world.blocks.payloads.PayloadVoid;
import mindustry.world.blocks.sandbox.*;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static CtCoreSystem.CoreSystem.type.CTColor.C;
import static CtCoreSystem.content.ItemX.物品;
import static mindustry.type.ItemStack.with;

public class SourceCodeModification_Sandbox {
    public static void load() {

 /*      Blocks.powerSource.envDisabled = Evn2.标志1 | Env.terrestrial;
        Blocks. powerVoid.envDisabled = Evn2.标志1 | Env.terrestrial;
        Blocks. itemSource.envDisabled = Evn2.标志1 | Env.terrestrial;
        Blocks.  itemVoid.envDisabled = Evn2.标志1 | Env.terrestrial;
        Blocks.  liquidSource.envDisabled = Evn2.标志1 | Env.terrestrial;
        Blocks.    liquidVoid.envDisabled = Evn2.标志1 | Env.terrestrial;*/

        new waveRule("waveRule");
        new TDBuffChange.Buff加盾("Shield");
        new TDBuffChange.BuffHealth("Health");
        new TDBuffChange.BuffSpee("Speed");
        new TDBuffChange.BuffDmage("Damage");
        //沙盒全能物品源
        new XVXSource("Automatic-adaptation-source") {
            {
                this.requirements(Category.distribution, BuildVisibility.sandboxOnly, ItemStack.with());
                this.alwaysUnlocked = true;
                this.localizedName = "沙盒源";
                envEnabled = Env.any;

            }
        };
        new CoreBlock("invincibleCore") {
            {
                // localizedName = Core.bundle.get("block.core0");
                // = Core.bundle.getOrNull("block.description.core0");
                requirements(Category.effect, BuildVisibility.sandboxOnly, with(物品, 0));
                alwaysUnlocked = true;//默认解锁
                isFirstTier = true;
                envEnabled = Env.any;
                targetable = false;//被单位攻击
                unitType = new CT3UnitType.CTR1UnitType("invincibleCoreUnit", "gamma") {
                    {
                        aiController = BuilderAI::new;
                        isEnemy = false;//是否加入波次计数器中 用作敌人
                        lowAltitude = false;// 子弹/特效在单位上面显示
                        envEnabled = Env.any;
                        flying = true;
                        drag = 0.1f;
                        buildSpeed = 20f;
                        speed = 8f;
                        rotateSpeed = 15f;
                        accel = 0.1f;
                        itemCapacity = 1000;
                        health = 100f;
                        hitSize = 16f;
                        alwaysUnlocked = true;
                        mineRange = 40 * 8f;//采矿范围
                        mineTier = 100;//挖矿等级
                        mineSpeed = 100f;//挖矿速度
                        mineHardnessScaling = false;//高等级矿物挖掘变慢
                        hittable = false;//被子弹击中
                        killable = false;//被杀死
                        targetable = false;//被敌人瞄准
                        physics = false;//单位碰撞
                        buildRange = 80 * 8.0F;//建造范围
                        lightColor = Color.valueOf("ffcd35");
                        ammoType = new ItemAmmoType(物品);
                        lightOpacity = 1;
                        coreUnitDock = true;
                        envDisabled = 0;
                        engineOffset = 7.5f;
                        engineSize = 3.4f;
      /*          setEnginesMirror(
                        new UnitEngine(35 / 4f, -13 / 4f, 2.7f, 315f),
                        new UnitEngine(28 / 4f, -35 / 4f, 2.7f, 315f)
                );*/
                        weapons.add(new Weapon("small-mount-weapon") {
                            {
                                reload = 20f;
                                bullet = new BasicBulletType(4f, 1) {
                                    @Override
                                    public void hitEntity(Bullet b, Hitboxc entity, float health) {
                                        super.hitEntity(b, entity, health);

                                        if (entity instanceof Unit unit) {
                                            unit.kill();
                                            if (!unit.dead && !Vars.net.client()) {
                                                unit.health = 0;
                                                unit.dead = true;
                                                Call.unitDeath(unit.id);
                                            }
                                        }
                                    }

                                    @Override
                                    public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) {
                                        super.hitTile(b, build, x, y, initialHealth, direct);

                                        Tile.buildDestroyed(build);
                                    }

                                    /*               public void update(Bullet b){
                                     *//*                   updateTrail(b);
                                    updateHoming(b);
                                    updateWeaving(b);
                                    updateTrailEffects(b);
                                    updateBulletInterval(b);*//*
                                    b.remove();
                                }*/
                                   /* public void hitEntity(Bullet b, Hitboxc entity, float health) {
                                        super.hitEntity(b, entity, health);


                                        if (entity instanceof Unit unit) {
                                            unit.remove();
                                        }
                                           if (entity instanceof Unit unit) {
                                            unit.kill();
                                        }
                                        if (entity instanceof Unit unit) {
                                            unit.killed();
                                        }
                                        if (entity instanceof Building build) {
                                            build.kill();
                                        }*/
                            /*    public void hitEntity(Bullet b, Hitboxc entity, float health){
                                  //  boolean wasDead = entity instanceof Unit u && u.dead;

                                    if(entity instanceof Unit unit){
                                        unit.remove();
                                    }
                                    if(entity instanceof Healthc h){
                                        if(pierceArmor){
                                            h.damagePierce(b.damage);
                                        }else{
                                            h.damage(b.damage);
                                        }
                                    }
                                    handlePierce(b, health, entity.x(), entity.y());

                                }*/ {
                                        width = 7f;
                                        height = 9f;
                                        lifetime = 90f;
                                        ammoMultiplier = 1;//装弹倍率
                                        // homingPower = 1;
                                        scaleLife = true;//开启指哪打哪
                                        trailLength = 8;
                                        trailWidth = 2;
                                        trailColor = C("baee33");
                                        absorbable = false;//子弹不被护盾仪吸收
                                    }
                                }

                                ;

                            }
                        });
                    }
                };
                outputsPower = true;
                health = 100;
                itemCapacity = 900000;
                size = 3;
                unitCapModifier = 50;
                buildType = Build::new;
            }

            @Override
            public boolean canPlaceOn(Tile tile, Team team, int rotation) {
                return true;
            }//解除其他限制

            @Override
            public boolean canReplace(Block other) {
                return true;
            }//解除方块限制

            @Override
            public boolean canBreak(Tile tile) {
                return Vars.state.teams.cores(tile.team()).size > 1;
            }//核心数量小于1不可拆

            @Override
            public void setStats() {
                super.setStats();
                stats.remove(Stat.buildTime);
                this.stats.add(Stat.basePowerGeneration, 1000000000, StatUnit.powerSecond);//发电
            }

            class Build extends CoreBuild {
                @Override
                public void damage(@Nullable Team source, float damage) {
                }
            }
        }

        ;
        new LogicBlock("world-logic") {
            {
                requirements(Category.logic, BuildVisibility.editorOnly, with());
                canOverdrive = false;
                targetable = false;
                instructionsPerTick = 8;
                forceDark = true;
                privileged = true;
                size = 1;
                maxInstructionsPerTick = 1000;
                range = Float.MAX_VALUE;
                envEnabled = Env.any;
            }
        };
        BasicBulletType 必死子弹 = new BasicBulletType(9f, 1) {
            @Override
            public void hitEntity(Bullet b, Hitboxc entity, float health) {
                super.hitEntity(b, entity, health);


                if (entity instanceof Unit unit) {
                    unit.remove();
                }
            }

            @Override
            public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) {
                super.hitTile(b, build, x, y, initialHealth, direct);

                Tile.buildDestroyed(build);
            }

            {
                width = 9f;
                height = 12f;
                reloadMultiplier = 1.3f;
                damage = 1;
                lifetime = 70f;
                absorbable = false;//子弹不被护盾仪吸收
                ammoMultiplier = 1;
                trailColor = Color.valueOf("f1cc68");
                trailParam = 5;
                trailLength = 8;
                trailWidth = 5;
                trailInterval = 10;
                trailChance = 1;
                trailRotation = true;
                trailEffect = Fx.none;
                homingRange = 3 * 8f;//追踪范围 跟踪
                homingPower = 0.3f; //追踪
                homingDelay = 0;
             /*           splashDamageRadius = 2f * 8f;
                        splashDamage = Float.POSITIVE_INFINITY;*/
                fragBullets = 3; //分裂数量
                fragBullet = new BasicBulletType(9f, 1) {
                    @Override
                    public void hitEntity(Bullet b, Hitboxc entity, float health) {
                        super.hitEntity(b, entity, health);

                        if (entity instanceof Unit unit) {
                            unit.kill();
                            if (!unit.dead && !Vars.net.client()) {
                                unit.health = 0;
                                unit.dead = true;
                                Call.unitDeath(unit.id);
                            }
                        }
                    }
                    @Override
                    public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) {
                        super.hitTile(b, build, x, y, initialHealth, direct);

                        Tile.buildDestroyed(build);
                    }

                    {    absorbable = false;//子弹不被护盾仪吸收
                        }};

            }
        };
        //沙盒炮
        new

                PowerTurret("SandboxTurret") {
                    {
                        localizedName = Core.bundle.get("Turret.SandboxTurret");
                        description = Core.bundle.getOrNull("Turret.description.SandboxTurret");
                        requirements(Category.turret, with(物品, 0));
                        targetable = false;//被单位攻击？
                        canOverdrive = false;//超速
                        alwaysUnlocked = true;
                        envEnabled = Env.any;
                        shootType = 必死子弹;
                        reload = 20f;
                        range = 70 * 8;
                        armor = 114154f;
                        shootCone = 365f;
                        ammoUseEffect = Fx.casing1;
                        health = 909130592;
                        inaccuracy = 1f; //精准
                        rotateSpeed = 30f;//炮塔旋转速度
                        buildVisibility = BuildVisibility.sandboxOnly;
                        buildType = Build::new;
                    }

                    class Build extends PowerTurretBuild {
                        @Override
                        public void damage(float damage) {

                        }
                    }
                }

        ;

        //短距离必死炮塔
        new

                PowerTurret("SandboxTurret2") {
                    {
                        localizedName = Core.bundle.get("Turret.SandboxTurret2");
                        description = Core.bundle.getOrNull("Turret.description.SandboxTurret");
                        requirements(Category.turret, with(物品, 0));
                        targetable = false;//被单位攻击？
                        canOverdrive = false;//超速
                        alwaysUnlocked = true;
                        envEnabled = Env.any;
                        shootType = new BasicBulletType(7f, 20) {
                            public void hitEntity(Bullet b, Hitboxc entity, float health) {
                                super.hitEntity(b, entity, health);


                                if (entity instanceof Unit unit) {
                                    unit.remove();
                                }
                            }

                            {
                                absorbable = false;//子弹不被护盾仪吸收
                                width = 9f;
                                height = 12f;
                                reloadMultiplier = 1.3f;
                                damage = 100;
                                lifetime = 15f;
                                ammoMultiplier = 1;
                                trailColor = Color.valueOf("f1cc68");
                                trailParam = 5;
                                trailLength = 8;
                                trailWidth = 5;
                                trailInterval = 10;
                                trailChance = 1;
                                trailRotation = true;
                                trailEffect = Fx.none;
                                homingRange = 3 * 8f;//追踪范围 跟踪
                                homingPower = 0.3f; //追踪
                                homingDelay = 0;
                                splashDamageRadius = 2f * 8f;
                                splashDamage = Float.POSITIVE_INFINITY;

                            }
                        };
                        reload = 2f;
                        range = 10 * 8;
                        armor = 114154f;
                        shootCone = 365f;
                        ammoUseEffect = Fx.casing1;
                        health = 909130592;
                        inaccuracy = 13f; //精准
                        rotateSpeed = 30f;//炮塔旋转速度
                        buildVisibility = BuildVisibility.sandboxOnly;
                        buildType = Build::new;
                    }

                    class Build extends PowerTurretBuild {
                        @Override
                        public void damage(float damage) {

                        }
                    }
                };

        //沙盒无敌墙
        new Wall("SandboxWall") {
            {
                localizedName = Core.bundle.get("Wall.SandboxWall");
                description = Core.bundle.getOrNull("Wall.description.SandboxWall");
                health = 100000;
                size = 2;
                envEnabled = Env.any;
                targetable = false;//被单位攻击？
                requirements(Category.defense, BuildVisibility.sandboxOnly, with(
                        物品, 0
                ));
                buildType = Build::new;
                alwaysUnlocked = true;
            }

            class Build extends WallBuild {
                @Override
                public void damage(float damage) {

                }
            }
        }

        ;


        new

                PowerSource("power-source") {
                    {
                        requirements(Category.power, BuildVisibility.sandboxOnly, with(物品, 0));
                        powerProduction = 9000000f / 60f;
                        laserRange = 30;
                        maxNodes = 100;
                        alwaysUnlocked = true;
                        envEnabled = Env.any;
                    }
                }

        ;

        new

                PowerVoid("power-void") {
                    {
                        requirements(Category.power, BuildVisibility.sandboxOnly, with(物品, 0));
                        alwaysUnlocked = true;
                        envEnabled = Env.any;
                    }
                }

        ;

        new

                ItemSource("item-source") {
                    {
                        requirements(Category.distribution, BuildVisibility.sandboxOnly, with(物品, 0));
                        alwaysUnlocked = true;
                        itemsPerSecond = 1000;
                        envEnabled = Env.any;
                    }
                }

        ;

        new

                ItemVoid("item-void") {
                    {
                        requirements(Category.distribution, BuildVisibility.sandboxOnly, with(物品, 0));
                        alwaysUnlocked = true;
                        envEnabled = Env.any;
                    }
                }

        ;

        new

                LiquidSource("liquid-source") {
                    {
                        requirements(Category.liquid, BuildVisibility.sandboxOnly, with(物品, 0));
                        alwaysUnlocked = true;
                        envEnabled = Env.any;
                    }
                }

        ;

        new

                LiquidVoid("liquid-void") {
                    {
                        requirements(Category.liquid, BuildVisibility.sandboxOnly, with(物品, 0));
                        alwaysUnlocked = true;
                        envEnabled = Env.any;
                    }
                }

        ;

        new

                PayloadSource("payload-source") {
                    {
                        requirements(Category.units, BuildVisibility.sandboxOnly, with(物品, 0));
                        size = 5;
                        alwaysUnlocked = true;
                        envEnabled = Env.any;
                    }
                }

        ;

        new

                PayloadVoid("payload-void") {
                    {
                        requirements(Category.units, BuildVisibility.sandboxOnly, with(物品, 0));
                        size = 5;
                        alwaysUnlocked = true;
                        envEnabled = Env.any;
                    }
                }

        ;
    }
}
