package CtCoreSystem.CoreSystem.type.Ovulam5480;

import arc.Core;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;

public class PreposeTurret {

    //需要前置升级覆盖的炮塔
    public static class PreposeItemTurret extends ItemTurret {
        //构造函数
        public PreposeItemTurret(String name) {
            super(name);
        }
        public Block 升级前置 = null;
        public boolean canReplace(Block other) {
            if (other.alwaysReplace) return true;
            return 升级前置 == null ? super.canReplace(other) : 升级前置 == other;
        }

        @Override
        public boolean canPlaceOn(Tile tile, Team team, int rotation) {
            if (tile == null) return false;
            if (Vars.state.isEditor() || 升级前置 == null || Vars.state.rules.infiniteResources) return true;

            tile.getLinkedTilesAs(this, tempTiles);
            return tempTiles.contains(o -> o.block() == 升级前置);
        }
        //炮塔自发光
        public class Build extends ItemTurret.ItemTurretBuild {
            public void drawLight() {
                Drawf.light(this.x, this.y, size*8, Pal.accent, 0.65F + Mathf.absin(20.0F, 0.1F));
            }
        }
        @Override
        public void drawPlace(int x, int y, int rotation, boolean valid) {
            if (!valid && 升级前置 != null)
                drawPlaceText(Core.bundle.format("ctsy.UpgradeFront") + 升级前置.localizedName, x, y, false);
            super.drawPlace(x, y, rotation, valid);



        }















    }
}
