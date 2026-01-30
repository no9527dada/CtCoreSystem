package CtCoreSystem.CoreSystem.type;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.geom.Rect;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.game.Team;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.ui.Styles;

//单位给方块超速力场能力
public class OverdriveAbility extends Ability {
    public float friendlySpeedBoost; // 己方超速倍率
    public float enemySpeedSlowdown;  // 敌方减速倍率
    public float friendlyRange;       // 己方超速范围
    public float enemyRange;          // 敌方减速范围
    public float boostTime;           // 效果每次间隔和持续时间，默认1秒
    public float effectDuration = 120f; // 特效持续时间，与boostTime分离，默认120帧（2秒）


    public float charge = 0;
// 己方效果的视觉效果
    public Effect friendlyEffect = new Effect(effectDuration, (e) -> {
        //  Color teamColor = e.data instanceof Color ? (Color)e.data : Team.derelict.color;
         Draw.z(Layer.effect);
        Lines.stroke(2f * e.fout(), Team.sharded.color);
        Lines.rect(rect(e.x, e.y, friendlyRange * e.finpow()));
    });

    // 默认构造函数，使用相同的值给己方和敌方
    public OverdriveAbility(float speedValue, float rangeValue) {
        this(speedValue, rangeValue, 60f);
    }

    // 构造函数，支持设置boostTime
    public OverdriveAbility(float speedValue, float rangeValue, float boostTime) {
        this.friendlySpeedBoost = speedValue;
        this.enemySpeedSlowdown = speedValue;
        this.friendlyRange = rangeValue;
        this.enemyRange = rangeValue;
        this.boostTime = boostTime;
    }

    // 完整构造函数，分别设置己方和敌方的参数
    public OverdriveAbility(float friendlySpeedBoost, float friendlyRange, float enemySpeedSlowdown, float enemyRange, float boostTime) {
        this.friendlySpeedBoost = friendlySpeedBoost;
        this.friendlyRange = friendlyRange;
        this.enemySpeedSlowdown = enemySpeedSlowdown;
        this.enemyRange = enemyRange;
        this.boostTime = boostTime;
        // 重新创建friendlyEffect以使用更新后的effectDuration值
        this.friendlyEffect = new Effect(effectDuration, (e) -> {
            Lines.stroke(2f * e.fout(), Team.sharded.color);
            Lines.rect(rect(e.x, e.y, friendlyRange * e.finpow()));
        });
    }

    // 构造函数，分别设置己方和敌方的参数
    public OverdriveAbility(float friendlySpeedBoost, float friendlyRange, float enemySpeedSlowdown, float enemyRange) {
        this.friendlySpeedBoost = friendlySpeedBoost;
        this.friendlyRange = friendlyRange;
        this.enemySpeedSlowdown = enemySpeedSlowdown;
        this.enemyRange = enemyRange;
    }

    public Rect rect(float x, float y, float range) {
        return new Rect(x - range / 2, y - range / 2, range, range);
    }

    @Override
    public void update(Unit unit) {
        charge += Time.delta;
        if (charge > boostTime) {
            // 单位所属队伍
            Team unitTeam = unit.team;
            // 判断单位是否属于玩家方
            // boolean isPlayerUnit = unitTeam == Vars.player.team();

            // 获取队伍颜色
            Color teamColor = unitTeam.color;

            // friendlyEffect.at(unit.x, unit.y, Team.sharded.color);
            // 对黄方应用减速效果
            if (unit.team != Team.sharded) {
                Vars.indexer.eachBlock(Team.sharded, rect(unit.x, unit.y, enemyRange),
                        other -> other.block.canOverdrive,
                        other -> other.applySlowdown(enemySpeedSlowdown, boostTime + 1));
            }else {
                // 对己方应用超速效果
                Vars.indexer.eachBlock(unitTeam, rect(unit.x, unit.y, friendlyRange),
                        other -> other.block.canOverdrive,
                        other -> other.applyBoost(friendlySpeedBoost, boostTime + 1)
                );
            }




        }
    }
    // 添加draw方法，实现特效显示
    @Override
    public void draw(Unit unit) {

            // 根据单位所属方显示相应的特效范围
            if (unit.team == Team.sharded) {
                Draw.z(Layer.effect);
                // 如果是玩家方单位，显示己方特效范围
                Lines.stroke(1f , Team.sharded.color);
                Lines.rect(rect(unit.x, unit.y, friendlyRange ));
            } else {
                Draw.z(Layer.effect);
                // 如果是敌方单位，显示敌方特效范围
                Lines.stroke(3f , unit.team.color);
                Lines.rect(rect(unit.x, unit.y, enemyRange));
            }

    }
    public void display(Table t) {
        t.table(Styles.grayPanel, a -> {
            a.add("[accent]" + localized()).padBottom(4).center().top().expandX();
            // 打印参数值用于调试
          //  Log.err("OverdriveAbility - friendlySpeedBoost: " + friendlySpeedBoost + ", friendlyRange: " + friendlyRange + ", enemySpeedSlowdown: " + enemySpeedSlowdown + ", enemyRange: " + enemyRange);
            a.add(Core.bundle.format("overdrive.ability",
                    friendlySpeedBoost,
                    friendlyRange/8,
                    1/enemySpeedSlowdown,
                    enemyRange/8
            ));
            a.row();
            a.left().top().defaults().left();
            addStats(a);
        }).pad(5).margin(10).growX().top().uniformX();
    }
}
