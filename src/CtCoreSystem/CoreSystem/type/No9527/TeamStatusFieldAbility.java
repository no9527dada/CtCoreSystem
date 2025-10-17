package CtCoreSystem.CoreSystem.type.No9527;


import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.Ability;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;

import static arc.graphics.g2d.Draw.reset;
import static mindustry.Vars.*;
// 状态场能力 可选队伍
public class TeamStatusFieldAbility extends Ability {
    public StatusEffect effect;
    public float duration = 60, reload = 100, range; //
    public boolean onShoot = false;
    public Effect applyEffect = Fx.none;
    public Effect activeEffect = Fx.overdriveWave;
    public float effectX, effectY;
    public boolean parentizeEffects, effectSizeParam = true;
    public Color color = Pal.accent;
    // 添加目标队伍字段，默认值为 Team.crux（即该状态仅对红方有效，如果单位自身是敌方队伍则不生效。下面有写），本类本身是减益状态，如果改黄方后  单位对任何队伍都不会生效
    public Team targetTeam =  Team.crux;
    protected float timer;

    TeamStatusFieldAbility(){}

    public TeamStatusFieldAbility(StatusEffect effect, float duration, float reload, float range){
        this.duration = duration;
        this.reload = reload;
        this.range = range;
        this.effect = effect;
    }
    // 添加新构造函数，支持指定目标队伍
    public TeamStatusFieldAbility(StatusEffect effect, float duration, float reload, float range, Team targetTeam){
        this.duration = duration;
        this.reload = reload;
        this.range = range;
        this.effect = effect;
        this.targetTeam = targetTeam;
    }

    @Override
    public void addStats(Table t){
       // super.addStats(t);
      //  t.add(Core.bundle.format("bullet.range", Strings.autoFixed(range / tilesize, 2)));
   //     t.row();
        t.add(Core.bundle.format("ability.teamstatusfield.description", Strings.autoFixed(range / tilesize, 2),targetTeam.name,effect.hasEmoji() ? effect.emoji() : "") + "[stat]" + effect.localizedName);
        // 添加目标队伍信息到统计面板
      //  if(targetTeam != null){
      //      t.row();
      //      t.add("目标队伍: " + targetTeam.name);
      //  }
    }
    @Override
    public void update(Unit unit){
        if(unit.team == targetTeam) return; //本类状态力场能力主要用于敌方队伍（即红方），如果单位自身是敌方队伍则不生效
        timer += Time.delta;
        if(timer >= reload && (!onShoot || unit.isShooting)){
            Team teamToUse = (targetTeam != null) ? targetTeam : unit.team;

            Units.nearby(teamToUse, unit.x, unit.y, range, other -> {
                other.apply(effect, duration);
                applyEffect.at(other, parentizeEffects);
            });
            timer = 0f;
        }
    }
//使用Draw绘制圆形范围
@Override
public void draw(Unit unit){
    if(unit.team == targetTeam) return; //本类状态力场能力主要用于敌方队伍，如果单位自身是敌方队伍则不生效
    if(timer < reload){
        // 使用Mathf.absin函数创建0-1之间的平滑值来控制颜色在黄色和白色之间切换
        float alpha = Mathf.absin(Time.time, 2f, 1f);
        float alpha2 = Mathf.absin(Time.time*0.2f, 2f, 1f);
        // 手动计算黄色和白色之间的插值颜色
        float r = Mathf.lerp(1f, 1f, alpha);  // 红色分量 (黄色和白色的红色都是1)
        float g = Mathf.lerp(1f, 1f, alpha);  // 绿色分量 (黄色和白色的绿色都是1)
        float b = Mathf.lerp(0f, 1f, alpha);  // 蓝色分量 (黄色是0，白色是1)
        Draw.z(Layer.effect);
        Draw.color(r, g, b, alpha2);
        Lines.stroke( 1.5f);
        Lines.circle(unit.x, unit.y, range);
        Draw.reset();
    }
}
/*    @Override
    public void draw(Unit unit){
        if(unit.team == targetTeam) return; //本类状态力场能力主要用于敌方队伍，如果单位自身是敌方队伍则不生效
        if(timer < reload){
            Draw.color(unit.team.color);
            //添加一个闪烁效果
            // 计算闪烁效果的透明度
            float alpha = Mathf.absin(Time.time/60, 10f, 0.5f);
            Draw.alpha(alpha);
            Lines.stroke( 1.5f);
            Lines.circle(unit.x, unit.y, range);
            Draw.reset();
        }
    }*/
}