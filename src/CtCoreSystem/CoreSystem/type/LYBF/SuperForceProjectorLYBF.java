package CtCoreSystem.CoreSystem.type.LYBF;

import CtCoreSystem.CoreSystem.type.Ovulam5480.BulletType.percentBulletType;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.math.geom.Vec2;
import arc.scene.event.Touchable;
import arc.scene.ui.Image;
import arc.scene.ui.Label;
import arc.scene.ui.Slider;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Reflect;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.LightningBulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Icon;
import mindustry.gen.Unit;
import mindustry.ui.Styles;
import mindustry.world.blocks.defense.ForceProjector;
import mindustry.world.meta.Stat;

import java.lang.reflect.Method;

import static CtCoreSystem.CoreSystem.type.CTColor.C;
import static arc.Core.bundle;

/**
 * <p>infinite shield,deflect bullets,kill unit</p>
 *
 * @author LYBF
 * 超级无敌秒杀力场
 */
public class SuperForceProjectorLYBF extends ForceProjector {
    //产生的子弹
    public BulletType bullet ;
    //间隔时间
    public float 间隔Time = 30f;
    //劈里啪啦总时间, 不要超过间隔时间
    public float bilibiliTime = 20f;


    public SuperForceProjectorLYBF(String name) {
        super(name);
        configurable = true;
        shieldHealth = 9999999;
        cooldownNormal = 9999999;
        phaseUseTime=1;
       /* bullet =  new LightningBulletType() {{
            damage = 100;
            lightningLength = 6;
        }};*/
        bullet = new percentBulletType.healthBulletType(980) {{
            speed = 0;
            damage = 100;
            lifetime = 5;
        }};
    }


    protected static SuperForceProjectorLYBFBuild paramEntity;
    protected static Effect paramEffect; // 添加这一行
    protected static final Cons<Bullet> destroyAttacker = bullet -> {
        if (bullet.team != paramEntity.team && bullet.type.absorbable &&
                Intersector.isInRegularPolygon(((SuperForceProjectorLYBF) (paramEntity.block)).sides, paramEntity.x, paramEntity.y, paramEntity.realRadius(), ((SuperForceProjectorLYBF) (paramEntity.block)).shieldRotation, bullet.x, bullet.y)) {
            bullet.absorb();
            paramEffect.at(bullet);
            paramEntity.hit = 1f;
            //paramEntity.buildup += bullet.damage;
        }
    };

    public class SuperForceProjectorLYBFBuild extends ForceBuild {

        public float dynamicRadius = radius;
        public boolean killUnit = false;
        public boolean killSpecialUnit = false;
        public int killSpecialUnitTimer = timers++;

        public float 间隔Timer;
        public float bilibiliTimer, preTime;
        public Seq<Vec2> pos = new Seq<>();
        public int ampint;

        @Override
        public void updateTile() {
            super.updateTile();
            if (killUnit) {
                if (间隔Timer < 间隔Time) 间隔Timer += edelta();
                else {
                    pos.clear();
                    Groups.unit.intersect(x - realRadius(), y - realRadius(), realRadius() * 2f, realRadius() * 2f, unit -> {
                        if (unit.team != team && unit.type.targetable && Intersector.isInRegularPolygon(sides, x, y, realRadius(), shieldRotation, unit.x, unit.y)) {
                            间隔Timer = 0;
                            pos.add(new Vec2(unit.x, unit.y));
                        }
                    });
                    Vars.indexer.eachBlock(null, x, y, realRadius() * 2f, building -> building.team != team && building.block.targetable, building -> {
                        if (Intersector.isInRegularPolygon(sides, x, y, realRadius(), shieldRotation, building.x, building.y)) {
                            间隔Timer = 0;
                            pos.add(new Vec2(building.x, building.y));
                        }
                    });
                    ampint = pos.size;
                }
                if (!pos.isEmpty()) {
                    preTime = bilibiliTimer;
                    for (bilibiliTimer += edelta(); preTime < bilibiliTimer; preTime += bilibiliTime / ampint) {
                        if (pos.isEmpty()) break;
                        Vec2 vec2 = pos.first();
                        bullet.create(this, vec2.x, vec2.y, Mathf.random(360));
                        pos.remove(vec2);
                    }
                }
            }
            if (killSpecialUnit) {
                unitKiller();
                if (timer(killSpecialUnitTimer, 10)) {
                    killSpecialUnit();
                }
            }
        }
        private Class<?> empathyDamageClass;
        private Method resetMethod;
        private void initReflection() {
            try {
                empathyDamageClass = Class.forName("flame.unit.empathy.EmpathyDamage");
                resetMethod = empathyDamageClass.getMethod("reset");
            } catch (Exception ignored) {}
        }

        @Override
        public void created() {
            super.created();
            initReflection();  // 在创建时初始化反射
        }

        private void killSpecialUnit() {
            try {
                if (resetMethod != null) {
                    resetMethod.invoke(null);  // 调用静态方法
                }
            } catch (Exception ignored) {}
        }

        @Override
        public void deflectBullets() {
            float realRadius = realRadius();
            if (realRadius > 0 && !broken) {
                paramEntity = this;
                paramEffect = absorbEffect;
                Groups.bullet.intersect(x - realRadius, y - realRadius, realRadius * 2f, realRadius * 2f, destroyAttacker);
            }
        }

        private final Cons<Unit> unitKiller = unit -> {
            if (unit.team != paramEntity.team &&
                    Intersector.isInRegularPolygon(((SuperForceProjectorLYBF) (paramEntity.block)).sides, paramEntity.x, paramEntity.y, paramEntity.realRadius(), ((SuperForceProjectorLYBF) (paramEntity.block)).shieldRotation, unit.x, unit.y)) {
                unit.remove();
            }
        };

        public void unitKiller() {
            float realRadius = realRadius();
            if (realRadius > 0 && !broken) {
                Groups.unit.intersect(x - realRadius, y - realRadius, realRadius * 2f, realRadius * 2f, unitKiller);
            }
        }

        public float realRadius() {
            return (dynamicRadius + phaseHeat * phaseRadiusBoost) * radscl;
        }


        @Override
        public void buildConfiguration(Table table) {
            if (Vars.player != null && Vars.player.team() != team) return;
            table.setWidth(300f);
//            table.background(Styles.black9);
            showControl(table);
            super.buildConfiguration(table);
        }

        public void showControl(Table table) {
            table.button(bundle.get("ctcoresystem.unit-killer"), Icon.eyeOffSmall, Styles.togglet, () -> killUnit = !killUnit)
                    .width(table.getWidth() / 2)
                    .checked(killUnit)
                    .update(b -> {
                        b.setChecked(killUnit);
                        ((Image) b.getChildren().get(1)).setDrawable(killUnit ? Icon.eyeSmall : Icon.eyeOffSmall);
                    }).left().row();
            table.button(bundle.get("ctcoresystem.kill-special-unit"), Icon.eyeOffSmall, Styles.togglet, () -> killSpecialUnit = !killSpecialUnit)
                    .width(table.getWidth() / 2)
                    .checked(killSpecialUnit)
                    .update(b -> {
                        b.setChecked(killSpecialUnit);
                        ((Image) b.getChildren().get(1)).setDrawable(killSpecialUnit ? Icon.eyeSmall : Icon.eyeOffSmall);
                    }).left().row();
            adjustRadius(table);

        }

        public void adjustRadius(Table table1) {
            Table table = new Table();
            int max_radius = Math.min(Vars.state.map.width, Vars.state.map.height)/2 ;
            if (max_radius <= 0) {
                max_radius = 500;
            }
            max_radius += (int) (max_radius * 1.2f);
            Slider slider = new Slider(1, max_radius, 1, false);

            slider.setWidth(table1.getWidth());
            slider.setValue(dynamicRadius / 8);

            Label value = new Label("", Styles.outlineLabel);
            Table content = new Table();
            content.setWidth(table1.getWidth());
            content.add(bundle.get("ctcoresystem.adjustRadius"), Styles.outlineLabel).left().growX().wrap();
            content.add(value).padLeft(10f).right();
//            content.margin(3f, 33f, 3f, 33f);
            content.touchable = Touchable.disabled;
            slider.changed(() -> {
                int value1 = (int) slider.getValue();
                dynamicRadius = value1 * 8;
                value.setText("" + value1);
            });
            table.stack(slider, content).width(table1.getWidth()).left().padTop(4f).get();
            table.row();
            table1.add(table);

        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            dynamicRadius = read.f();
            killUnit = read.bool();
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(dynamicRadius);
            write.bool(killUnit);
        }
        @Override
        public void drawShield() {
            if (!this.broken) {
                float radius = this.realRadius();
                if (radius > 0.001F) {
                    Draw.color(C("ad6eff"), Color.white, Mathf.clamp(this.hit));
                    if (Vars.renderer.animateShields) {
                        Draw.z(125.0F + 0.001F * this.hit);
                        Fill.poly(this.x, this.y, sides, radius, shieldRotation);
                    } else {
                        Draw.z(125.0F);
                        Lines.stroke(1.5F);
                        Draw.alpha(0.09F + Mathf.clamp(0.08F * this.hit));
                        Fill.poly(this.x, this.y, sides, radius, shieldRotation);
                        Draw.alpha(1.0F);
                        Lines.poly(this.x, this.y,sides, radius,shieldRotation);
                        Draw.reset();
                    }
                }
            }

            Draw.reset();
        }
        //免疫一切伤害
        public boolean canDamage = false;
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

    }
    public void setStats() {
        super.setStats();
        this.stats.remove(Stat.booster);
        stats.remove(Stat.booster);
        stats.remove( Stat.shieldHealth);
        stats.remove( Stat.cooldownTime);
        stats.remove( Stat.liquidCapacity);
        stats.remove( Stat.itemCapacity);
        // stats.remove(Stat.range);
    }
}
