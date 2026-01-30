package CtCoreSystem.CoreSystem.type.No9527;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

import static CtCoreSystem.content.ItemX.物品;
import static mindustry.Vars.tilesize;
import static mindustry.type.ItemStack.with;

//反超速 高级超速器 凌驾于普通超速器之上
public class FanOverdriveProjector extends Block {
    public TextureRegion topRegion;
    // 对应OverdriveProjector的属性
    public float reload = 60f;
    public float range = 80f;
    public float 减速倍率=1;
    private final float speedBoost = 1/减速倍率;

  //  private float 值 = ;



    public float useTime = 400f;
    public float phaseRangeBoost = 20f;
    public boolean hasBoost = true;
    public Color baseColor = Color.valueOf("feb380");
    public Color phaseColor = Color.valueOf("ff9ed5");
    float MAX = 1000 * 100;
    float MIN = 1;
    // 使用数组下标获取；存储的是乘以 100 的数字
    float[] commandMap = {1, 10, 100, 1000};
    float lastNumber = 200;
    float INIT_MASK = 1000000;
    float speedTo = 300;
    float heat = 0;
    float phaseHeat = 0;

    public FanOverdriveProjector(String name) {
        super(name);
        减速倍率=1;
        update = true;
        solid = true;
        configurable = true;
        buildType = FanOverdriveProjectorBuild::new;
        // 设置为projector组
        group = BlockGroup.projectors;
        // 设置灯光
        emitLight = true;
        lightRadius = reload - 8;
        hasPower = true;
        hasItems = true;
        canOverdrive = false;
        consumePower(1);
        requirements(Category.effect, with(
                物品, 1
        ));
        //consumeItems(with(物品, 1)).optional = true;
    }

    public void setStats() {
        this.stats.timePeriod = this.useTime;
        super.setStats();
       this.stats.add(Stat.speedIncrease, "+0%");
        this.stats.add(Stat.range, this.range / 8.0F, StatUnit.blocks);
        this.stats.add(Stat.productionTime, this.useTime / 60.0F, StatUnit.seconds);
        if (this.hasBoost) {
            Consume var2 = this.findConsumer((f) -> f instanceof ConsumeItems);
            if (var2 instanceof ConsumeItems) {
                ConsumeItems items = (ConsumeItems) var2;
                this.stats.remove(Stat.booster);
                this.stats.add(Stat.booster, StatValues.itemBoosters("+{0}%", this.stats.timePeriod, 0F, this.phaseRangeBoost, items.items));   }
        }

    }

    public float getHeat() {
        return heat;
    }

    public float getPhaseHeat() {
        return phaseHeat;
    }

    public float getSpeedTo() {
        return speedTo;
    }

    public float setSpeedTo(float v) {
        return speedTo = v;
    }

    @Override
    public void load() {
        super.load();
        // 从纹理图集加载纹理，格式为"blockname-top"
        topRegion = Core.atlas.find(name + "-top", Core.atlas.find("error"));
    }



    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        // 绘制基础范围圈
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, baseColor);

        // 如果有相位增强效果，绘制扩展范围圈
        if (hasBoost) {
            Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range + phaseRangeBoost, phaseColor);
        }

        // 高亮显示受影响的可超速方块
        Vars.indexer.eachBlock(null, x * tilesize + offset, y * tilesize + offset, range,
                other -> other.block.canOverdrive,
                other -> Drawf.selected(other, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f))));
    }

    public class FanOverdriveProjectorBuild extends Building {
        public float heat, phaseHeat, smoothEfficiency, useProgress;

        public void playerPlaced(Object config) {
            // 算出最少需要多少次可以达到 lastNumber ，并发送指定次数个 configure
            Core.app.post(() -> this.configure(lastNumber + INIT_MASK));
        }

/*        public void configured(Unit player, Object value) {
            // 小于 100 视为减小命令，大于 1000000（七位数）视为初始化
            if ((float) value > INIT_MASK) {
                setSpeedTo((float) value - INIT_MASK);
            } else if ((float) value >= 100) {
                float commandVal = commandMap[(int) ((float) value - 100)];
                float result = Math.max(MIN, getSpeedTo() - commandVal);
                setSpeedTo(result);
                lastNumber = getSpeedTo();
            } else {
                float commandVal = commandMap[(int) (float) value];
                float result = Math.min(MAX, getSpeedTo() + commandVal);
                setSpeedTo(result);
                lastNumber = getSpeedTo();
            }
        }*/

        @Override
        public void drawLight() {
            Drawf.light(x, y, lightRadius * smoothEfficiency, baseColor, 0.7f * smoothEfficiency);
        }

        public void drawSelect() {
            float realRange = range + phaseHeat * phaseRangeBoost;
            Vars.indexer.eachBlock(this, realRange, (other) -> other.block.canOverdrive, (other) -> {
                var tmp = Tmp.c1.set(baseColor);
                tmp.a = Mathf.absin(4, 1);
                Drawf.selected(other, tmp);
            });
            Drawf.dashCircle(this.x, this.y, realRange, baseColor);
        }

        public void draw() {
            super.draw();

            if (topRegion != null) { // 添加空值检查
                float f = 1f - (Time.time / 100f) % 1f;

                Draw.color(baseColor, phaseColor, phaseHeat);
                Draw.alpha(heat * Mathf.absin(Time.time, 50f / Mathf.PI2, 1f) * 0.5f);
                Draw.rect(topRegion, x, y);
                Draw.alpha(1f);
                Lines.stroke((2f * f + 0.1f) * heat);

                float r = Math.max(0f, Mathf.clamp(2f - f * 2f) * size * tilesize / 2f - f - 0.2f),
                        w = Mathf.clamp(0.5f - f) * size * tilesize;
                Lines.beginLine();
                for (int i = 0; i < 4; i++) {
                    Point2 d4 = Geometry.d4(i);
                    Lines.linePoint(x + d4.x * r + d4.y * w, y + d4.y * r - d4.x * w);
                    if (f < 0.5f)
                        Lines.linePoint(x + d4.x * r - d4.y * w, y + d4.y * r + d4.x * w);
                }
                Lines.endLine(true);

                Draw.reset();
            }
        }

        public void updateTile() {
            if(efficiency <= 0) return; // 添加电力检查
            smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, this.efficiency, 0.08F);
            heat = Mathf.lerpDelta(heat, efficiency > 0 ? 1f : 0f, 0.08f);

            if (hasBoost) {
                phaseHeat = Mathf.lerpDelta(phaseHeat, optionalEfficiency, 0.1f);
            }

            float realRange = range + phaseHeat * phaseRangeBoost;

            float duration = reload + 1f;

             float speedBoost_2 = 1f;
            Vars.indexer.eachBlock(this, realRange, (other) -> other.block.canOverdrive, (other) -> {
                other.applySlowdown(speedBoost, duration);
            });

            if (efficiency > 0) {
                useProgress += delta();
            }

            if (useProgress >= useTime) {
                consume();
                useProgress %= useTime;
            }
        }

        public void write(Writes write) {
            super.write(write);
            write.f(heat);
            write.f(phaseHeat);
            write.f(speedTo);
            write.f(useProgress);
        }

        public void read(Reads reader, byte revision) {
            super.read(reader, revision);
            heat = reader.f();
            phaseHeat = reader.f();
            speedTo = reader.f();
            useProgress = reader.f();
        }

    }
}
