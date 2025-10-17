package CtCoreSystem.CoreSystem.type.Ovulam5480;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
///import mindustry.annotations.Annotations.*;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.world.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;
import mindustry.world.blocks.defense.MendProjector;
import static mindustry.Vars.*;


// 单位治疗修复仪
public class UnitMendProjector extends MendProjector {
    public float unitHealAmount = 50f; // 基础单位治疗量
    public float unitPhaseBoost = 50f; // 强化时额外治疗量

    //public @Load("laser-white") TextureRegion laser;
    //public @Load("laser-white-end") TextureRegion laserEnd;
//
    public Color laserColor = Color.valueOf("98ffa9");

    public UnitMendProjector(String name) {
        super(name);
        solid = true;
        update = true;
        group = BlockGroup.projectors;
        hasPower = true;
        hasItems = true;
        emitLight = true;
        lightRadius = 50f;
        suppressable = true;
        envEnabled |= Env.space;
        flags = EnumSet.of(BlockFlag.repair); // 改为修复单位的标记
    }

    @Override
    public void setStats() {
        super.setStats();
        // 移除方块修复相关的统计信息
        stats.remove(Stat.repairTime);
        // 添加单位治疗相关的统计信息
        stats.add(Stat.repairSpeed, unitHealAmount + " 每" + reload/60f + "秒", StatUnit.none);

        // 更新强化效果描述
        if(findConsumer(c -> c instanceof ConsumeItems) instanceof ConsumeItems cons) {
            stats.remove(Stat.booster);
            stats.add(Stat.booster, StatValues.itemBoosters(
                    "{0}" + StatUnit.timesSpeed.localized(),
                    stats.timePeriod, (unitPhaseBoost + unitHealAmount) / unitHealAmount, phaseRangeBoost,
                    cons.items
            ));
        }
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        // 绘制治疗范围指示器
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, baseColor);
    }

    public class UnitMendBuild extends MendBuild {
        @Override
        public void updateTile() {
            boolean canHeal = !checkSuppression();

            smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, efficiency, 0.08f);
            heat = Mathf.lerpDelta(heat, efficiency > 0 && canHeal ? 1f : 0f, 0.08f);
            charge += heat * delta();

            phaseHeat = Mathf.lerpDelta(phaseHeat, optionalEfficiency, 0.1f);

            if(optionalEfficiency > 0 && timer(timerUse, useTime) && canHeal) {
                consume();
            }

            // 核心修改：将方块修复改为单位治疗
            if(charge >= reload && canHeal) {
                float realRange = range + phaseHeat * phaseRangeBoost;
                charge = 0f;
                float healAmount = (unitHealAmount + phaseHeat * unitPhaseBoost) * efficiency;

                // 使用Units.nearby遍历范围内所有受损单位
                Units.nearby(team, x, y, realRange, unit -> {
                    if(unit.damaged()) {
                        unit.heal(healAmount);

                        // 在每个被治疗的单位上显示自定义效果
                        // 创建5-8个绿色加号粒子，持续时间2秒(120帧)
                        int particleCount = Mathf.random(5, 8);
                        for(int i = 0; i < particleCount; i++){
                            // 计算粒子的随机位置，分布在单位体积周围
                            float offsetX = Mathf.random(-unit.hitSize()/2, unit.hitSize()/2);
                            float offsetY = Mathf.random(-unit.hitSize()/2, unit.hitSize()/2);
                            float posX = unit.x + offsetX;
                            float posY = unit.y + offsetY;

                            // 创建自定义的治疗加号效果
                            Effect effect = new Effect(120f, e -> {
                                // 设置绿色
                                Draw.color(Color.green);
                                // 计算透明度，随时间衰减
                                float alpha = 1f - e.fin();
                                Draw.alpha(alpha);
                                // 计算粒子向上移动的距离
                                float moveY = e.fin() * 15f; // 15像素的移动距离
                                float size = 3f + e.fout() * 2f; // 初始大小3，逐渐缩小

                                // 绘制加号
                                // 横线（水平方向）
                                Lines.line(e.x - size/2, e.y + moveY, e.x + size/2, e.y + moveY, false);
                                // 竖线（垂直方向）
                                Lines.line(e.x, e.y + moveY - size/2, e.x, e.y + moveY + size/2, false);
                            });

                            // 在计算好的位置触发效果
                            effect.at(posX, posY);
                        }
                    }
                });

                // 在投影仪位置显示脉冲效果
                Fx.healBlockFull.at(x, y, realRange, baseColor);
            }
        }

        public void drawSelect() {
            float realRange = range + phaseHeat * phaseRangeBoost;
            // 高亮显示范围内的所有友方单位
            Units.nearby(team, x, y, realRange, unit -> {
                // 使用八边形替代矩形，并添加旋转效果
                float size = unit.hitSize() * 1.2f; // 稍微扩大八边形大小以更好地显示 将八边形大小设置为单位碰撞尺寸的1.2倍
                float rotation =Time.time / 300 * 360f; // 根据时间旋转，周期5秒
               // Draw.color(Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f)));//闪烁效果
                Draw.color(Tmp.c1.set(baseColor).a(0.7f));
                //设置线条宽度为1像素
                Lines.stroke(1f);
                // 绘制八边形，使用Lines.poly方法，传入位置、大小、边数和旋转角度
                Lines.poly(unit.x, unit.y, 8, size, rotation);
                Draw.color();
            });
            Drawf.dashCircle(x, y, realRange, baseColor);
        }

        @Override
        public void draw() {
            super.draw();
            float realRange = range + phaseHeat * phaseRangeBoost;
            // 额外绘制单位治疗的视觉效果
            if(heat > 0.01f) {
                float f = 1f - (Time.time / 100f) % 1f;

                // 绘制脉冲波纹效果
                Draw.z(Layer.flyingUnit + 1);
                Draw.color(laserColor);
                Draw.alpha(0.3f * heat * Mathf.absin(Time.time, 2f, 1f));
              //  Draw.alpha(0.3f);
                Lines.stroke(1f);
                Lines.circle(x, y, realRange);

                Draw.color();
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
        }
    }
}