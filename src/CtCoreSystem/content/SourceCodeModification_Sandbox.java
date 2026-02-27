package CtCoreSystem.content;

import CtCoreSystem.CoreSystem.type.*;
import CtCoreSystem.CoreSystem.type.LYBF.SuperForceProjectorLYBF;
import CtCoreSystem.CoreSystem.type.Ovulam5480.BulletType.RoundBulletType;
import CtCoreSystem.CoreSystem.type.Ovulam5480.BulletType.percentBulletType;
import CtCoreSystem.CoreSystem.type.Ovulam5480.ReclamationBlock;
import CtCoreSystem.CoreSystem.type.Ovulam5480.液体分发器;
import CtCoreSystem.CoreSystem.type.Ovulam5480.物资分发器;
import CtCoreSystem.CoreSystem.type.Ovulam5480.物资提取器;
import CtCoreSystem.CoreSystem.type.TDTyep.TDBuffChange;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.ImageButton;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Table;
import arc.util.Eachable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.ai.types.BuilderAI;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.Weapon;
import mindustry.type.ammo.ItemAmmoType;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.distribution.BufferedItemBridge;
import mindustry.world.blocks.payloads.PayloadSource;
import mindustry.world.blocks.payloads.PayloadVoid;
import mindustry.world.blocks.sandbox.*;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static CtCoreSystem.CoreSystem.type.CTColor.C;
import static CtCoreSystem.content.ItemX.物品;
import static mindustry.Vars.state;
import static mindustry.type.Category.crafting;
import static mindustry.type.Category.turret;
import static mindustry.type.ItemStack.with;
import static mindustry.world.meta.BlockGroup.liquids;
import static mindustry.world.meta.BlockGroup.transportation;

public class SourceCodeModification_Sandbox {
    public static Block 隐形核心, 结束波次更改, Buff加盾, Buff加血, Buff速度, Buff射击速度,
            Buff伤害, 游戏速度, 游戏缩放,
            游戏环境光开关, 核心禁造圈, 资源清空开关, 敌人出生点范围, 拆除返还资源倍率, 地图炸档器, 无限火力开关,
            沙盒全能物品源, 无敌核心, 沙盒无敌墙, 无限电, 物品原, 液体源,
            物资分发器, 物资提取器, 液体分发器, 世界仓库, 电力力场网, 无敌虚无, 无限电力的电池, 填海器;
    private static Color THE_COLOR = Color.purple;

    public static void load() {
        //敌人的隐形核心，作用于刷怪，不可被攻击和摧毁，只能用逻辑清除该方块，达到胜利的目的
        隐形核心 = new CoreBlock("hideCore") {
            {
                requirements(Category.effect, BuildVisibility.editorOnly, with());
                alwaysUnlocked = true;
                incinerateNonBuildable = true;//不可建造，会在建造UI面板隐藏
                isFirstTier = false;//核心地板限制
                //unitType = 核心机0号;
                health = 1000;
                itemCapacity = 10000;
                size = 1;
                solid = false;//固体
                targetable = false;//被单位攻击？
                canOverdrive = false;//超速
                unitCapModifier = 999999;
                hasShadow = false;//方块黑影
                drawTeamOverlay = false;//方块绘制队伍斜杠
                buildType = Build::new;
            }

            class Build extends CoreBlock.CoreBuild {
                @Override
                public void damage(float damage) {

                }

                public void drawLight() {
                }//不发光
            }
        };
 /*      Blocks.powerSource.envDisabled = Evn2.标志1 | Env.terrestrial;
        Blocks. powerVoid.envDisabled = Evn2.标志1 | Env.terrestrial;
        Blocks. itemSource.envDisabled = Evn2.标志1 | Env.terrestrial;
        Blocks.  itemVoid.envDisabled = Evn2.标志1 | Env.terrestrial;
        Blocks.  liquidSource.envDisabled = Evn2.标志1 | Env.terrestrial;
        Blocks.    liquidVoid.envDisabled = Evn2.标志1 | Env.terrestrial;*/

        结束波次更改 = new waveRule("waveRule");
        Buff加盾 = new TDBuffChange.Buff加盾("Shield");
        Buff加血 = new TDBuffChange.BuffHealth("Health");
        Buff速度 = new TDBuffChange.BuffSpee("Speed");
        Buff射击速度 = new TDBuffChange.BuffReload("Reload");
        Buff伤害 = new TDBuffChange.BuffDmage("Damage");
        游戏速度 = new TDBuffChange.游戏速度("游戏速度");
        游戏缩放 = new TDBuffChange.游戏缩放("游戏缩放");
        游戏环境光开关 = new TDBuffChange.游戏环境光开关("游戏环境光开关");
        核心禁造圈 = new TDBuffChange.核心禁造圈("核心禁造半径");
        资源清空开关 = new TDBuffChange.资源清空开关("资源清空开关");
        敌人出生点范围 = new TDBuffChange.敌人出生点范围("敌人出生点范围");
        拆除返还资源倍率 = new TDBuffChange.拆除返还资源倍率("拆除返还资源倍率");
        无限火力开关 = new TDBuffChange.无限火力开关("无限火力开关");

        //地图炸档器
        //没效果  待修
        地图炸档器 = new BufferedItemBridge("breakdown") {
            {
                requirements(Category.effect, BuildVisibility.editorOnly, with(物品, 1));
                buildType = Build::new;

            }

            @Override
            public void drawBridge(BuildPlan req, float ox, float oy, float flip) {
            }

            @Override
            public void drawPlace(int x, int y, int rotation, boolean valid) {
            }

            @Override
            public void drawPlanConfigTop(BuildPlan plan, Eachable<BuildPlan> list) {
            }

            class Build extends BufferedItemBridge.BufferedItemBridgeBuild {
                @Override
                public void damage(float damage) {

                }
            }
        };
        //沙盒全能物品源
        沙盒全能物品源 = new XVXSource("Automatic-adaptation-source") {
            {
                this.requirements(Category.distribution, BuildVisibility.sandboxOnly, ItemStack.with());
                this.alwaysUnlocked = true;
                this.localizedName = "沙盒源";
                envEnabled = Env.any;

            }
        };
        无敌核心 = new CoreBlock("invincibleCore") {
            float powerProduction;

            {
                // localizedName = Core.bundle.get("block.core0");
                // = Core.bundle.getOrNull("block.description.core0");
                requirements(Category.effect, BuildVisibility.sandboxOnly, with(物品, 1));
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
                        itemCapacity = 5201314;
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
                        weapons.add(new Weapon("small-mount-weapon") {
                            {
                                reload = 5f;
                                bullet = new BasicBulletType(20f, 1) {
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

                                    {
                                        despawnEffect = Fx.hitBulletSmall;
                                        width = 7f;
                                        height = 9f;
                                        lifetime = 40f;
                                        ammoMultiplier = 1;//装弹倍率
                                        // homingPower = 1;
                                        scaleLife = true;//开启指哪打哪
                                        trailLength = 8;
                                        trailWidth = 2;
                                        trailColor = C("baee33");
                                        absorbable = false;//子弹不被护盾仪吸收
                                    }
                                };
                            }
                        });
                    }
                };
                health = 100;
                itemCapacity = 90000000;
                size = 3;
                unitCapModifier = 10000;
                powerProduction = 1000000000;
                buildType = Build::new;
                configurable = true;
                hasPower = true;
                consumesPower = false;
                outputsPower = true;

            }


            //用于发电显示
            @Override
            public void setStats() {
                super.setStats();
                stats.remove(Stat.buildTime);
                this.stats.add(Stat.basePowerGeneration, powerProduction, StatUnit.powerSecond);//发电
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


            class Build extends CoreBuild {
                public boolean canDamage = false;

                //用于发电
                @Override
                public float getPowerProduction() {
                    return powerProduction / 60f;
                }

                @Override
                public void damage(float amount) {
                    if (canDamage) super.damage(amount);
                }

                @Override
                public void damage(Team source, float damage) {
                    if (canDamage) super.damage(source, damage);
                }

                @Override
                public void damage(float amount, boolean withEffect) {
                    if (canDamage) super.damage(amount, withEffect);
                }

                @Override
                public void damage(Bullet bullet, Team source, float damage) {
                    if (canDamage) super.damage(bullet, source, damage);
                }

                @Override
                public boolean damaged() {
                    if (canDamage) return super.damaged();
                    return false;
                }

                @Override
                public void damageContinuous(float amount) {
                    if (canDamage) super.damageContinuous(amount);
                }

                @Override
                public void damageContinuousPierce(float amount) {
                    if (canDamage) super.damageContinuousPierce(amount);
                }

                @Override
                public void damagePierce(float amount) {
                    if (canDamage) super.damagePierce(amount);
                }

                @Override
                public void damagePierce(float amount, boolean withEffect) {
                    if (canDamage) super.damagePierce(amount, withEffect);
                }

                @Override
                public float handleDamage(float amount) {
                    if (canDamage) super.handleDamage(amount);
                    return 0f;
                }

                @Override
                public byte version() {
                    return 2;
                }

                @Override
                public void read(Reads read, byte revision) {
                    super.read(read, revision);
                }

                @Override
                public void write(Writes write) {
                    super.write(write);
                }

                @Override
                public void buildConfiguration(Table table) {
                    super.buildConfiguration(table);
                    ButtonGroup<ImageButton> buttonGroup = new ButtonGroup<>();
                    Table cont = new Table().top();
                    cont.defaults().size(40);
                    int rows = 5;
                    for (Team team1 : Team.baseTeams) {
                        TextButton button = cont.button(team1.coloredName(), () -> {
                            configure(team1);
                        }).tooltip(Core.bundle.get("switchTo", "切换到") + team1.coloredName()).group(buttonGroup).pad(4).get();
                        button.setWidth(80);
                        button.setColor(team1.color);
                    }

                    ScrollPane scrollPane = new ScrollPane(cont, Styles.smallPane);
                    table.top().add(scrollPane).maxHeight(40 * rows);
                }

                @Override
                public void configure(Object value) {
                    super.configure(value);
                    if (value instanceof Team team) {
                        Vars.player.team(team);
                        this.team(team);
                    }
                }
            }
        };
        BasicBulletType 必死子弹 = new BasicBulletType(9f, 999999) {
            {
                width = 9f;
                height = 12f;
                reloadMultiplier = 1.3f;
                damage = 1;
                lifetime = 70f;
                absorbable = false;//子弹不被护盾仪吸收
                ammoMultiplier = 1;
                trailColor = THE_COLOR;
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
                hitEffect = new Effect(8, (e) -> {
                    Draw.color(Color.black, THE_COLOR, e.fin());
                    Lines.stroke(0.5f + e.fout());
                    Lines.circle(e.x, e.y, e.fin() * 10);
                });
                despawnEffect = new Effect(8, (e) -> {
                    Draw.color(Color.black, THE_COLOR, e.fin());
                    Lines.stroke(0.5f + e.fout());
                    Lines.circle(e.x, e.y, e.fin() * 5);
                });
             /*           splashDamageRadius = 2f * 8f;
                        splashDamage = Float.POSITIVE_INFINITY;*/
                fragBullets = 3; //分裂数量
                fragBullet = new BasicBulletType(9f, 999999) {
                    {
                        hitEffect = new Effect(8, (e) -> {
                            Draw.color(Color.black, THE_COLOR, e.fin());
                            Lines.stroke(0.5f + e.fout());
                            Lines.circle(e.x, e.y, e.fin() * 10);
                        });
                        despawnEffect = new Effect(8, (e) -> {
                            Draw.color(Color.black, THE_COLOR, e.fin());
                            Lines.stroke(0.5f + e.fout());
                            Lines.circle(e.x, e.y, e.fin() * 5);
                        });
                    }

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

                    {
                        absorbable = false;//子弹不被护盾仪吸收
                    }
                };

            }

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
        };
        //沙盒炮
        new PowerTurret("SandboxTurret") {
            {
                localizedName = Core.bundle.get("Turret.SandboxTurret");
                description = Core.bundle.getOrNull("Turret.description.SandboxTurret");
                requirements(turret, with(物品, 0));
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
        };

        //短距离必死炮塔
        new PowerTurret("SandboxTurret2") {
            {
                localizedName = Core.bundle.get("Turret.SandboxTurret2");
                description = Core.bundle.getOrNull("Turret.description.SandboxTurret");
                requirements(turret, with(物品, 0));
                targetable = false;//被单位攻击？
                canOverdrive = false;//超速
                alwaysUnlocked = true;
                envEnabled = Env.any;
                shootType = new BasicBulletType(7f, 999999) {
                    public void hitEntity(Bullet b, Hitboxc entity, float health) {
                        super.hitEntity(b, entity, health);
                        if (entity instanceof Unit unit) {
                            unit.remove();
                        }
                    }

                    public void draw(Bullet b) {
                        Draw.color(THE_COLOR);
                        Drawf.tri(b.x, b.y, 4, 8, b.rotation());
                        Drawf.tri(b.x, b.y, 4, 12, b.rotation() - 180);
                        Draw.reset();
                    }

                    {
                        absorbable = false;//子弹不被护盾仪吸收
                        width = 9f;
                        height = 12f;
                        reloadMultiplier = 1.3f;
                        lifetime = 15f;
                        ammoMultiplier = 1;
                        trailColor = THE_COLOR;
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
                        splashDamage = 999999;

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
        沙盒无敌墙 = new Wall("SandboxWall") {
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
        };
        无限电 = new PowerSource("power-source") {
            {
                requirements(Category.power, BuildVisibility.sandboxOnly, with(物品, 0));
                powerProduction = 9000000f / 60f;
                laserRange = 30;
                maxNodes = 100;
                alwaysUnlocked = true;
                envEnabled = Env.any;
            }
        };
        无限电力的电池 = new InfinitePowerSource("infinite-battery") {
            {
                requirements(Category.power, BuildVisibility.sandboxOnly, with(物品, 0));
                alwaysUnlocked = true;
                envEnabled = Env.any;
            }
        };
        new PowerVoid("power-void") {
            {
                requirements(Category.power, BuildVisibility.sandboxOnly, with(物品, 0));
                alwaysUnlocked = true;
                envEnabled = Env.any;
            }
        };
        物品原 = new ItemSource("item-source") {
            {
                requirements(Category.distribution, BuildVisibility.sandboxOnly, with(物品, 0));
                alwaysUnlocked = true;
                itemsPerSecond = 1000;
                envEnabled = Env.any;
            }
        };
        new ItemVoid("item-void") {
            {
                requirements(Category.distribution, BuildVisibility.sandboxOnly, with(物品, 0));
                alwaysUnlocked = true;
                envEnabled = Env.any;
            }
        };

        液体源 = new LiquidSource("liquid-source") {
            {
                requirements(Category.liquid, BuildVisibility.sandboxOnly, with(物品, 0));
                alwaysUnlocked = true;
                envEnabled = Env.any;
            }
        };
        new LiquidVoid("liquid-void") {
            {
                requirements(Category.liquid, BuildVisibility.sandboxOnly, with(物品, 0));
                alwaysUnlocked = true;
                envEnabled = Env.any;
            }
        };

        new PayloadSource("payload-source") {
            {
                requirements(Category.units, BuildVisibility.sandboxOnly, with(物品, 0));
                size = 5;
                alwaysUnlocked = true;
                envEnabled = Env.any;
            }
        };
        new PayloadVoid("payload-void") {
            {
                requirements(Category.units, BuildVisibility.sandboxOnly, with(物品, 0));
                size = 5;
                alwaysUnlocked = true;
                envEnabled = Env.any;
            }
        };
        物资分发器 = new 物资分发器("物资分发器") {{
            description = "";
            consumePower(1000 / 60f);
            size = 4;
            range = 30;
            group = transportation;
            requirements(crafting, BuildVisibility.sandboxOnly, with(
                    物品, 1

            ));
        }};
        物资提取器 = new 物资提取器("物资提取器") {{
            description = "";
            consumePower(1000 / 60f);
            size = 4;
            range = 40;
            group = transportation;
            requirements(crafting, BuildVisibility.sandboxOnly, with(
                    物品, 1

            ));
        }};
        液体分发器 = new 液体分发器("液体分发器") {{
            description = "";
            consumePower(1000 / 60f);
            size = 4;
            range = 30;
            group = liquids;
            requirements(crafting, BuildVisibility.sandboxOnly, with(
                    物品, 1

            ));
        }};
        new PowerTurret("百分比炮塔最大") {{
            description = "";
            consumePower(1000 / 60f);
            size = 4;
            group = transportation;
            requirements(turret, BuildVisibility.sandboxOnly, with(
                    物品, 1

            ));
            range = 50 * 8;
            shootType = new percentBulletType.maxHealthBulletType(10) {{
                speed = 10;
                damage = 0;
                lifetime = 60;
            }};
        }};
        new PowerTurret("百分比炮塔当前") {{
            description = "";
            consumePower(1000 / 60f);
            size = 4;
            group = transportation;
            requirements(turret, BuildVisibility.sandboxOnly, with(
                    物品, 1

            ));
            shootType = new percentBulletType.healthBulletType(10) {{
                speed = 10;
                damage = 1;
                lifetime = 60;
            }};
            range = 50 * 8;
        }};
        new PowerTurret("转圈炮塔") {{
            description = "";
            consumePower(1000 / 60f);
            size = 4;
            group = transportation;
            requirements(turret, BuildVisibility.sandboxOnly, with(
                    物品, 1

            ));
            shootType = new BulletType() {{
                speed = 2;
                damage = 0;
                lifetime = 130;
                fragBullets = 1;
                fragBullet = new RoundBulletType() {{
                    speed = 0;
                    damage = 50;
                    lifetime = 5 * 60;
                }};

            }};
            range = 50 * 8;

        }};
        世界仓库 = new StorageBlock("世界仓库") {
            {
                description = "";
                size = 2;
                health = 200;
                envEnabled = Env.any;
                itemCapacity = 20000000;
                requirements(Category.crafting, BuildVisibility.sandboxOnly, with(物品, 1));
                canOverdrive = false;
                targetable = false;
                forceDark = true;
                privileged = true;
            }

            @Override
            public boolean canBreak(Tile tile) {
                return Vars.state.rules.infiniteResources || !privileged || state.rules.editor || state.playtestingMap != null;
            }
        };
        电力力场网 = new PowerNetTower("电力力场网") {
            {
                requirements(Category.power, BuildVisibility.sandboxOnly, with(物品, 1));
                size = 3;
                range = 90;
                consumePowerBuffered(50000000);
                buildType = Build::new;
                envEnabled = Env.any;
            }

            public class Build extends PowerNetTowerBuild {
                private static final float INFINITE_POWER = Float.MAX_VALUE / 1000f;

                @Override
                public float getPowerProduction() {
                    // 返回一个极大的数值模拟无限供电
                    return INFINITE_POWER;
                }
            }
        };
        无敌虚无 = new SuperForceProjectorLYBF("superforceprojector") {
            {
                requirements(Category.crafting, BuildVisibility.sandboxOnly, with(物品, 1));
                health = 114514;
                size = 3;
                envEnabled = Env.any;
            }
        };
    }


}
