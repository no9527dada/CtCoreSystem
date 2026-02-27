package CtCoreSystem.CoreSystem.type.Ovulam5480.BlockLimit;

import CtCoreSystem.CoreSystem.type.No9527.建筑贴图隐藏.*;
import arc.Core;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.storage.CoreBlock;

//同个方块的数量限制
public class GenericCrafterLimit {

    //普通工厂
    public static class 工厂数量限制 extends newGenericCrafter {
        //  数字代表允许建筑的数量，最多是数字的值+1
        public int 数量 = 1;

        public 工厂数量限制(String name) {
            super(name);
            数量 = 1;
        }

        //瞬间替换和扣除物品
        @Override
        public void placeBegan(Tile tile, Block previous) {
            if (!Vars.state.rules.infiniteResources) {
                CoreBlock.CoreBuild core = Vars.player.team().core();
                core.items.remove(requirements);
            }
            tile.setBlock(this, tile.team());
            tile.block().placeEffect.at(tile, tile.block().size);
        }

        //允许放置
        @Override
        public boolean canPlaceOn(Tile tile, Team team, int rotation) {
            CoreBlock.CoreBuild core = team.core();
            //size后面数字代表允许建筑的数量，最多是数字的值+1
            if (core == null || Vars.state.teams.get(team).getBuildings(this).size > 数量 - 1
                    || (!Vars.state.rules.infiniteResources && !core.items.has(requirements, Vars.state.rules.buildCostMultiplier)
            )) return false;
            return super.canPlaceOn(tile, team, rotation);
        }

        //显示红字
        @Override
        public void drawPlace(int x, int y, int rotation, boolean valid) {
            CoreBlock.CoreBuild core = Vars.player.team().core();
            if (!Vars.state.rules.infiniteResources && !core.items.has(requirements, Vars.state.rules.buildCostMultiplier)) {
                drawPlaceText(Core.bundle.format("bar.noresources"), x, y, valid);
            } else if (Vars.state.teams.get(Vars.player.team()).getBuildings(this).size > 数量 - 1)
                drawPlaceText(Core.bundle.get("QuantityLimit.限制最大数量") + 数量, x, y, valid);
            super.drawPlace(x, y, rotation, valid);
        }
    }

    public static class 工厂前置限制 extends newGenericCrafter {
        // 前置方块存在场上的数量，低于数字的值则不允许建造
        public int 数量 = 1;

        public 工厂前置限制(String name) {
            super(name);
            //前置方块 = Blocks.router;
            数量 = 1;
        }

        //前置方块targetBlock
        public Block 前置方块;

        //允许放置
        @Override
        public boolean canPlaceOn(Tile tile, Team team, int rotation) {
            //size后面数字代表场上目标建筑的数量，低于数字的值则不允许建造
            if (Vars.state.teams.get(team).getBuildings(前置方块).size < 数量) return false;
            if (前置方块 == null) return false;
            return super.canPlaceOn(tile, team, rotation);
        }

        //显示红字
        @Override
        public void drawPlace(int x, int y, int rotation, boolean valid) {
            if (Vars.state.teams.get(Vars.player.team()).getBuildings(前置方块).size < 数量)
                drawPlaceText(Core.bundle.get("QuantityLimit.缺少前置方块") + 前置方块.localizedName + " X" + 数量, x, y, valid);
            super.drawPlace(x, y, rotation, valid);
        }
    }
}
