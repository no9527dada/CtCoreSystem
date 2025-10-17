package CtCoreSystem.CoreSystem.type.Ovulam5480;

import arc.math.geom.Rect;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.Separator;

import static mindustry.Vars.tilesize;

public class 炮塔子弹分发器 extends Block {
    public float range = 20f;

    public 炮塔子弹分发器(String name) {
        super(name);
        hasItems = true;
        acceptsItems = true;
        //光传不需要容量
        itemCapacity = 0;
        destructible = true;
        canOverdrive = false;
        update = true;

    }

    public Rect rangeRect(float x, float y, float range) {
        return Tmp.r1.set(x - range, y - range, range * 2, range * 2);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        Drawf.dashRect(Pal.accent, rangeRect(x * 8 + offset, y * 8 + offset, range * tilesize));
    }

    public class 炮塔子弹分发器Building extends Building {

        public boolean vailBuild(Building target, Item item) {
            //eff
            return target.block.hasItems
                    && (target.block instanceof ItemTurret)
                    && target.acceptItem(this, item)
                    && (!hasConsumers || efficiency == 1);
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return Vars.indexer.findTile(team, x, y, range * tilesize * 2, b -> vailBuild(b, item) && rangeRect(x, y, range * tilesize).grow(0.1f).contains(b.x, b.y)) != null;
        }

        @Override
        public void handleItem(Building source, Item item) {
            Vars.indexer.findTile(team, x, y, range * tilesize * 2, b -> vailBuild(b, item) && rangeRect(x, y, range * tilesize).grow(0.1f).contains(b.x, b.y)).handleItem(this, item);
        }

        @Override
        public void drawSelect() {
            Drawf.dashRect(Pal.accent, rangeRect(x, y, range * tilesize));
        }

    }
}
