package CtCoreSystem.CoreSystem.type.Ovulam5480;

import arc.math.Mathf;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.game.Team;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.MendProjector;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.indexer;
import static mindustry.Vars.tilesize;

/**
 *
 * 非百分比的数值修复器
 */
public class CTMendProjector extends CTMendProjector2 {
    public float healAmount;//修复的数值

    public CTMendProjector(String name) {
        super(name);
        healPercent = 0;//这个接口无用，不要动他
        phaseBoost = 40;//是加入（相织布）提升的数值
        reload = 5 * 60;//5秒1次加血
        healAmount = 100f;//修复的数值
        phaseRangeBoost=3*8;//是加入（相织布）提升的范围

    }
    public class CTMendBuild extends MendBuild {
        @Override
        public void updateTile() {
            boolean canHeal = !checkSuppression();

            smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, efficiency, 0.08f);
            heat = Mathf.lerpDelta(heat, efficiency > 0 && canHeal ? 1f : 0f, 0.08f);
            charge += heat * delta();

            phaseHeat = Mathf.lerpDelta(phaseHeat, optionalEfficiency, 0.1f);

            if (optionalEfficiency > 0 && timer(timerUse, useTime) && canHeal) {
                consume();
            }

            if (charge >= reload && canHeal) {
                float realRange = range + phaseHeat * phaseRangeBoost;
                charge = 0f;

                indexer.eachBlock(this, realRange, b -> b.damaged() && !b.isHealSuppressed(), other -> {
                    other.heal((healAmount + phaseHeat * phaseBoost) * efficiency);
                    other.recentlyHealed();
                    Fx.healBlockFull.at(other.x, other.y, other.block.size, baseColor, other.block);
                });
            }
        }

    }
    //复制的原版
    @Override
    public void setStats(){
        stats.timePeriod = useTime;
        super.setStats();

        stats.add(Stat.repairTime, (int)(100f / healPercent * reload / 60f), StatUnit.seconds);
        stats.add(Stat.range, range / tilesize, StatUnit.blocks);

     /*   if(findConsumer(c -> c instanceof ConsumeItems) instanceof ConsumeItems cons){
            stats.remove(Stat.booster);
            stats.add(Stat.booster, StatValues.itemBoosters(
                    "{0}" + StatUnit.timesSpeed.localized(),
                    stats.timePeriod, (phaseBoost + healPercent) / healPercent, phaseRangeBoost,
                    cons.items, this::consumesItem)
            );
        }*/
    }
}