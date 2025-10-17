package CtCoreSystem.CoreSystem.type;

import arc.Core;
import arc.Events;
import arc.func.Cons;
import arc.graphics.Color;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.world.modules.ItemModule;

import java.util.HashMap;
import java.util.Objects;

import static mindustry.logic.LAccess.config;

/*
 *@Author:LYBF
 *@Date  :2024/2/19
 * 用法示例：
 * UnitDeathReward reward = UnitDeathReward.getInstance();
 * reward.init();
 * reward.add(UnitTypes.mono,new ItemStack(Items.copper,100));
 *
 * 2.
 * UnitDeathReward.getInstance().init().add(UnitTypes.mono,new ItemStack(Items.copper,100))
 *
 * 3.
 *  UnitDeathReward.getInstance().init()
                .with(
                        UnitTypes.dagger, ItemStack.with(mindustry.content.Items.copper, 114, mindustry.content.Items.lead, 514)
                        ....
                );
 */
public class UnitDeathReward {

    public static UnitDeathReward instance;

    //奖励配置
    public static HashMap<String, UnitDeathRewardConfiguration> configurations = new HashMap<>();

    private static boolean initialized = false;//是否初始化过

    //配置类
    public static class UnitDeathRewardConfiguration {
        //单位
        public UnitType unitType;
        //单位死亡奖励
        public ItemStack[] itemStacks;

        public UnitDeathRewardConfiguration() {

        }

        public UnitDeathRewardConfiguration(UnitType unitType, ItemStack[] itemStacks) {
            this.unitType = unitType;
            this.itemStacks = itemStacks;
        }


    }

    public static UnitDeathReward getInstance() {
        if (instance == null) instance = new UnitDeathReward();
        return instance;
    }



    Cons<EventType.UnitDestroyEvent> cons = (EventType.UnitDestroyEvent e) -> {
        Team unitTeam = e.unit.team;
        Team playerTeam = Vars.player.team();
        //是同一个队伍时不触发
        // 判断条件：
        // 1. 死亡单位不是玩家团队的单位
        // 2. 玩家团队拥有核心建筑（基地）
        if (unitTeam == playerTeam || Vars.state.teams.get(Vars.player.team()).core() == null) return;
        UnitDeathRewardConfiguration configuration = configurations.get(e.unit.type.name);
        if (!Objects.isNull(configuration)) {
            ItemModule items = Vars.state.teams.get(Vars.player.team()).core().items;
            for (ItemStack itemStack : configuration.itemStacks) {
                items.add(itemStack.item, itemStack.amount);
            }
        }
    };

    //初始化
    public UnitDeathReward init() {
        if (!initialized) {
            Events.on(EventType.UnitDestroyEvent.class, cons);
            initialized = true;
        }
        return instance;
    }

    //移除应用，取消监听
    public UnitDeathReward removeEvent() {
        if (initialized) {
            Events.remove(EventType.UnitDestroyEvent.class, cons);
            initialized = false;
        }
        return instance;
    }


    /*
     *添加奖励规则
     */
    public UnitDeathReward add(UnitDeathRewardConfiguration configuration) {
        return add(configuration.unitType.name, configuration);
    }

    public UnitDeathReward add(UnitType unitType, ItemStack itemStacks) {
        return add(unitType, new ItemStack[]{itemStacks});
    }

    public UnitDeathReward add(UnitType unitType, ItemStack[] itemStacks) {
        UnitDeathRewardConfiguration configuration = new UnitDeathRewardConfiguration(unitType, itemStacks);
        return add(configuration);
    }

    public UnitDeathReward add(String name, UnitDeathRewardConfiguration configuration) {
        if (Objects.isNull(configuration) || Objects.isNull(name) || Objects.isNull(configuration.unitType) || Objects.isNull(configuration.itemStacks)) {
            return instance;
        }
        configuration.unitType.abilities.add(new ShowWhatItemsInUnit(configuration.itemStacks));
        configurations.put(name, configuration);
        return instance;
    }

    public UnitDeathReward add(UnitDeathRewardConfiguration[] array) {
        for (UnitDeathRewardConfiguration configuration : array) {
            add(configuration.unitType.name, configuration);
        }
        return instance;
    }


    /*
     *添加多个配置
     * 参数为
     * UnitType,ItemStack,UnitType,ItemStack
     */
    public UnitDeathReward with(Object... configs) {
        for (int i = 0; i < configs.length; i += 2) {
            add(new UnitDeathRewardConfiguration((UnitType) configs[i], (ItemStack[]) configs[i + 1]));
        }
        return instance;
    }

    /*
     *移除规则
     */
    public UnitDeathReward remove(String name) {
        configurations.remove(name);
        return instance;
    }

    public UnitDeathReward remove(UnitType unitType) {
        return remove(unitType.name);
    }

    public static UnitDeathRewardConfiguration getRewardConfig(UnitType unit) {
        return Objects.isNull(unit) ? null : configurations.get(unit.name);
    }
}