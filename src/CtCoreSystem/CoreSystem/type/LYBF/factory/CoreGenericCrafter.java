package CtCoreSystem.CoreSystem.type.LYBF.factory;


import CtCoreSystem.CoreSystem.type.No9527.建筑贴图隐藏;
import mindustry.Vars;
import mindustry.type.Item;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.storage.CoreBlock;

/*
 *@Author:LYBF
 *@Date  :2024/3/8
 *@Desc  :
 * 核心工厂 直接产出物品至核心
 * 直接输出Items至核心,拒绝中间商赚差价
 */
public class CoreGenericCrafter extends 建筑贴图隐藏.newGenericCrafter {
    public CoreGenericCrafter(String name) {
        super(name);
    }

    public class CoreGenericCrafterBuilding extends GenericCrafterBuild {
        /*
         *dump items to core;
         */
        @Override
        public boolean dump(Item todump) {
            if (this.block.hasItems && this.items.total() != 0 && (todump == null || this.items.has(todump))) {
                CoreBlock.CoreBuild build = Vars.state.teams.get(team()).core();
                if (build == null) return false;
                if (build.acceptItem(this, todump)) {
                    build.handleItem(this, todump);
                    this.items.remove(todump, 1);
                    return true;
                }
            }
            return false;
        }

    }
}
