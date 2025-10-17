/*
package CtCoreSystem.CoreSystem.type.No9527;


import arc.graphics.Color;
import arc.graphics.g2d.Lines;
import arc.math.geom.Rect;
import arc.util.*;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.*;
import creators.CreatorsStatus;

public class SpeedSuppressionFieldAbility extends mindustry.entities.abilities.SuppressionFieldAbility {

    // 添加建筑减速相关参数
    public float buildingSlowMultiplier = 0.5f; // 建筑减速倍数，0.5表示速度减半
    public float buildingSlowDuration = 60f; // 建筑减速效果持续时间（帧）

    public float speedBoost;//超速倍率
    public float 建筑减速范围;//超速范围
    public float boostTime;//超速每次间隔和持续时间，默认1秒。一般不动

    public float charge = 0;
    // 构造函数，保留原有的参数
    public SpeedSuppressionFieldAbility() {
        super();
    }

    // 覆盖update方法，替换抑制修复的逻辑为抑制速度和建筑减速
    @Override
    public void update(Unit unit) {
        // 保留原有的粒子效果和视觉逻辑
        super.update(unit);

        // 添加敌方单位状态效果
        if (active && timer >= reload) {
            timer = 0;

            // 查找范围内的敌方单位
            Units.nearbyEnemies(unit.team, unit.x - range, unit.y - range, range * 2, range * 2, other -> {
                if (other.within(unit.x, unit.y, range)) {
                    // 应用速度抑制效果，这里使用CreatorsStatus中的speedDown效果
                    other.apply(CreatorsStatus.speedDown, 60f); // 持续60帧（1秒）
                }
            });

            //建筑减速抑制效果
            charge += Time.delta;
            if (charge > boostTime) {
                rect.at(unit.x, unit.y);
                Vars.indexer.eachBlock(unit.team, rect(unit.x, unit.y, 建筑减速范围), other -> other.block.canOverdrive,
                        other -> other.applyBoost(speedBoost, boostTime + 1));
                charge -= boostTime;
            }

         */
/*   // 查找范围内的敌方建筑并应用减速效果

            // 添加建筑减速效果
            // 创建范围矩形
            Rect effectRect = new Rect(unit.x - range, unit.y - range, range * 2, range * 2);

            Vars.indexer.eachBlock(unit.team.opposite(), effectRect, b -> true, building -> {
                if (building.within(unit.x, unit.y, range)) {
                    // 使用applyBoost方法应用减速效果，小于1的乘数表示减速
                    building.applyBoost(buildingSlowMultiplier, buildingSlowDuration);
                }
            });*//*

        }
    }
    public Effect rect = new Effect(60f, (e) -> {
        Lines.stroke(2f * e.fout(), Color.valueOf("feb380"));
        Lines.rect(rect(e.x, e.y, 建筑减速范围 * e.finpow()));
    });
    public Rect rect(float x, float y, float range) {
        return new Rect(x - range / 2, y - range / 2, range, range);
    }
    // 确保正确复制能力
    @Override
    public Ability copy() {
        SpeedSuppressionFieldAbility ability = new SpeedSuppressionFieldAbility();
        // 复制所有必要的属性
        ability.range = this.range;
        ability.reload = this.reload;
        ability.particles = this.particles;
        ability.particleColor = this.particleColor;
        ability.particleSize = this.particleSize;
        ability.orbRadius = this.orbRadius;
        ability.particleLife = this.particleLife;
        ability.particleLen = this.particleLen;
        ability.rotateScl = this.rotateScl;
        ability.x = this.x;
        ability.y = this.y;
        ability.layer = this.layer;
        ability.active = this.active;
        ability.display = this.display;
        // 复制新增的建筑减速参数
      //  ability.buildingSlowMultiplier = this.buildingSlowMultiplier;
      //  ability.buildingSlowDuration = this.buildingSlowDuration;
        return ability;
    }
}*/
