package CtCoreSystem.CoreSystem.type.Ovulam5480.BlockLimit;


import CtCoreSystem.CoreSystem.type.LYBF.factory.CoreGenericCrafter;
import CtCoreSystem.CoreSystem.type.No9527.建筑贴图隐藏.*;
import arc.Core;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.world;
//任何方块均适用
/**
 * 仅要求资源不足+瞬间放置 查看塔防2的 TD22MendProjector 写法  里面也有扣血的写法
 */

public class 方块限制写法 {
    public static class 方块限制写法GenericCrafter extends CoreGenericCrafter {

        public 方块限制写法GenericCrafter(String name) {
            super(name);
            size = 3;
            health = 100;
            armor = 500;
            itemCapacity = 10;
            craftTime = 60 * 5;
            //  envDisabled = Evn2.TD2标记;
        }

        //是否立即放置, 立即放置则资源不足时禁止放置
        public boolean 立即放置 = true; //true是启用资源不足的条件限制

        //场上数量要求方块不足时禁止放置
        public Block 场上数量要求方块 ;
        public int 数量 = 1;

        //放置位置没有被覆盖方块时禁止放置
        public Block 被覆盖方块;

        public boolean 资源要求(Team team){
            CoreBlock.CoreBuild core = Vars.state.teams.get(team).core();
            return !立即放置 || core != null && core.items.has(requirements, Vars.state.rules.buildCostMultiplier);
        }

        public boolean 数量要求(Team team){
            return 场上数量要求方块 == null || Vars.state.teams.get(team).getBuildings(场上数量要求方块).size >= 数量;
        }

        public boolean 覆盖要求(Tile tile){
            tile.getLinkedTilesAs(this, tempTiles);
            return 被覆盖方块 == null || tempTiles.contains(o -> o.block() == 被覆盖方块);
        }

        public boolean 沙盒模式(){
            return Vars.state.isEditor() || Vars.state.rules.infiniteResources;
        }

        @Override
        public boolean canPlaceOn(Tile tile, Team team, int rotation) {
            if(!沙盒模式() && !(资源要求(team) && 数量要求(team) && 覆盖要求(tile)))return false;
            return super.canPlaceOn(tile, team, rotation);
        }

        //显示红字
        @Override
        public void drawPlace(int x, int y, int rotation, boolean valid) {
            Team team = Vars.player.team();

            if(沙盒模式()){}
            else if(!资源要求(team))drawPlaceText(Core.bundle.format("bar.noresources"), x, y, valid);
            else if (!数量要求(team))drawPlaceText(Core.bundle.get("QuantityLimit.缺少前置方块") + 场上数量要求方块.localizedName + " X" + 数量, x, y, false);
            else if (!覆盖要求(world.tile(x, y)))drawPlaceText(Core.bundle.format("cttd.UpgradeFront") + 被覆盖方块.localizedName, x, y, valid);;

            super.drawPlace(x, y, rotation, valid);
        }

        //瞬间替换和扣除物品
        @Override
        public void placeBegan(Tile tile, Block previous) {
            Team team = Vars.player.team();

            if(立即放置 && 资源要求(team) && !沙盒模式()){
                CoreBlock.CoreBuild core = Vars.player.team().core();
                core.items.remove(requirements);
                tile.setBlock(this, tile.team());
                tile.block().placeEffect.at(tile, tile.block().size);
            }
            else super.placeBegan(tile, previous);
        }

        @Override
        public boolean canReplace(Block other) {
            return super.canReplace(other) || other == 被覆盖方块;
        }
    }



    //普通工厂
    public static class 工厂数量限制GenericCrafter extends newGenericCrafter {
        //  数字代表允许建筑的数量，最多是数字的值+1


        public 工厂数量限制GenericCrafter(String name) {
            super(name);
            数量 = 1;
        }
        public int 数量 = 1;
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
            return true;
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

    //前置方块限制
    public static class 前置限制Block extends Block {
        // 前置方块存在场上的数量，低于数字的值则不允许建造


        public 前置限制Block(String name) {
            super(name);
            //前置方块 = Blocks.router;
            数量 = 1;
        }
        public int 数量 = 1;
        //前置方块targetBlock
        public Block 前置方块;

        //允许放置
        @Override
        public boolean canPlaceOn(Tile tile, Team team, int rotation) {
            //编辑模式 沙盒模式不限制
            if (!Vars.state.rules.editor||Vars.state.rules.infiniteResources)return true;


            //size后面数字代表场上目标建筑的数量，低于数字的值则不允许建造
            if (Vars.state.teams.get(team).getBuildings(前置方块).size < 数量) return false;
            if (前置方块 == null) return false;
            return true;
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
