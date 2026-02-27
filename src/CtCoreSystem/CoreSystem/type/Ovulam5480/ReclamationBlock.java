package CtCoreSystem.CoreSystem.type.Ovulam5480;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

import static mindustry.Vars.world;

public class ReclamationBlock extends Block {
    public float 资源数量 ;
    public Seq<Floor> targets = Vars.content.blocks().select(f -> f instanceof Floor ff && ff.liquidDrop == Liquids.water).as();
    public Floor floor = Blocks.taintedWater.asFloor();
    public int targetSize = 1;

    public ReclamationBlock(String name, Item item) {
        super(name);
        rotate = true;
        update = true;
        solid = true;
        consumesPower = true;
        rebuildable = false;//是否可以被自动重建
        资源数量=120;//每帧会消耗1个，当消耗数量达到【资源数量】时完成填海操作
        consumePower(1f);//每秒消耗电力
        consumeItem(item, 1);//这里是每帧消耗多少个资源 一般不用改
        size = 2;
    }
    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
    Draw.rect(region, plan.drawx(), plan.drawy());
        Color color = Draw.getColor().cpy();
        // 设置绘制颜色
        Draw.color(Color.green, 0.5f);
        // 获取可被替换的地块
        Seq<Tile> replaceableTiles = tiles(world.tile(plan.x, plan.y), plan.rotation);
        if (!replaceableTiles.isEmpty()) {
            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;

            // 遍历可替换地块，找出最小和最大的 x、y 坐标
            for (Tile tile : replaceableTiles) {
                if (targets.contains(tile.floor())) {
                    minX = Math.min(minX, tile.x);
                    minY = Math.min(minY, tile.y);
                    maxX = Math.max(maxX, tile.x);
                    maxY = Math.max(maxY, tile.y);
                }
            }

            // 计算合并后方形的中心坐标和边长
            float centerX = (minX + maxX + 1) * Vars.tilesize / 2f;
            float centerY = (minY + maxY + 1) * Vars.tilesize / 2f;
            float width = (maxX - minX + 1) * Vars.tilesize / 2f;
            float height = (maxY - minY + 1) * Vars.tilesize / 2f;

            // 计算左下角坐标
            float leftBottomX = centerX - width;
            float leftBottomY = centerY - height;

            // 绘制合并后的方形，使用 Drawf.dashRect
            Drawf.dashRect(Color.green.cpy().a(0.5f),leftBottomX-4f, leftBottomY-4f, width * 2f, height * 2f );
        }

        // 恢复原来的颜色
        Draw.color(color);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        boolean[] has = {false};
        tiles(tile, rotation).each(target -> {
            if(target.block() == Blocks.air && targets.contains(target.floor())){
                has[0] = true;
            }
        });

        return super.canPlaceOn(tile, team, rotation) && has[0];
    }

 /*   Seq<Tile> tiles(Tile tile, int rotation){
        int radius = (size + 1) / 2;
        return world.tile(tile.x + Geometry.d4x(rotation) * radius, tile.y + Geometry.d4y(rotation) * radius).getLinkedTilesAs(Vars.content.blocks().find(b -> b.size == targetSize), tempTiles);
    }*/
    Seq<Tile> tiles(Tile tile, int rotation){
        int radius = targetSize / 2; // 计算目标范围的半径
        Seq<Tile> result = new Seq<>();
        int centerX = tile.x + Geometry.d4x(rotation) * (size + radius);
        int centerY = tile.y + Geometry.d4y(rotation) * (size + radius);

        // 遍历旋转方向前方的目标范围
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int newX = centerX + dx;
                int newY = centerY + dy;
                Tile targetTile = world.tile(newX, newY);
                if (targetTile != null) {
                    result.add(targetTile);
                }
            }
        }
        return result;
    }

    public class ReclamationBlockBuild extends Building {
        float timer;
        @Override
        public void updateTile() {
            // 检查电力和物品是否充足
            if (efficiency >= 1) {
                // 消耗电量和物品
                consume();
                timer += edelta();
            }
            if(timer > 资源数量){
                tiles(tile, rotation).each(t -> {
                    if(targets.contains(t.floor()))t.setFloor(floor);
                });
                damage(Float.MAX_VALUE);
            }
        }
        @Override
        public void draw() {
            super.draw();

            // 当区块工作时绘制特效
            if (efficiency >= 1) {
                float progress = Math.min(timer / 资源数量, 1f);

                // 绘制脉动光环
                Draw.z(Layer.effect);
                Draw.color(Color.blue, Color.cyan, progress);
                Draw.alpha(0.6f + Mathf.absin(Time.time, 1f, 0.4f));
                Lines.circle(x, y, (size * Vars.tilesize / 2f) + Mathf.absin(Time.time, 2f, 2f) + progress * 4f);

                // 绘制中心发光
                Draw.color(Color.cyan);
                Draw.alpha(0.8f);
               Fill.circle(x, y, (size * Vars.tilesize / 4f) + Mathf.absin(Time.time, 1.5f, 1f));

                // 绘制进度光环
                Draw.color(Color.green);
                Draw.alpha(0.5f);
                Lines.arc(x, y, (size * Vars.tilesize / 2f) + 8f, 0, progress * 360f);

                // 重置绘制状态
                Draw.color();
            }
        }

        @Override
        public void drawLight() {
            super.drawLight();

            // 工作时添加更强的光源
            if (efficiency >= 1) {
                float progress = Math.min(timer / 资源数量, 1f);
                float lightRadius = (size * Vars.tilesize) + progress * 32f;
                Drawf.light(x, y, lightRadius, Color.cyan, 0.6f);
            }
        }
    }
}