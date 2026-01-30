package CtCoreSystem.CoreSystem.type.MultiCrafter;
/*多合成并行*/

import CtCoreSystem.CoreSystem.type.No9527.BlockTextRenderer;
import CtCoreSystem.CoreSystem.type.No9527.建筑贴图隐藏;
import CtCoreSystem.CoreSystem.type.V8.ItemDisplay;
import CtCoreSystem.CoreSystem.type.V8.LiquidDisplay;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.scene.ui.Button;
import arc.struct.ObjectSet;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.meta.Stat;

import static CtCoreSystem.CtCoreSystem.方块贴图;
import static arc.Core.bundle;
import static arc.Core.scene;
import static mindustry.Vars.headless;

public class CT3NoChooseMulti extends GenericCrafter {
    public final CreatorsRecipe[] recs;
    private int index = 0;
    public Color TableColor = Color.yellow;
    public final ObjectSet<Item> inputItemSet = new ObjectSet<>();
    public final ObjectSet<Liquid> inputLiquidSet = new ObjectSet<>(), liquidSet = new ObjectSet<>();

    private boolean powerBarI, powerBarO;
    private Button.ButtonStyle infoStyle;

    public Effect craftEffect = Fx.none;
    public Effect updateEffect = Fx.none;
    public float updateEffectChance = 0.04f;

    public CT3NoChooseMulti(String name, CreatorsRecipe[] recs) {
        super(name);
        this.recs = recs;
        //ambientSound = Sounds.conveyor;
        ambientSoundVolume = 0.005f;
        this.buildType = CreatorsNoChooseMultiBuild::new;

    }

    public CT3NoChooseMulti(String name, int recLen) {
        this(name, new CreatorsRecipe[recLen]);
    }

   @Override
public void setBars() {
    super.setBars();
    removeBar("power");

    if (consPower != null) {
        addBar("power", (CT3NoChooseMulti.CreatorsNoChooseMultiBuild entity) ->
                new Bar(() -> bundle.format("bar.powerA", Strings.fixed(entity.block.consPower.requestedPower(entity) * 60 * entity.timeScale(), 1)),
                        () -> Pal.powerBar,
                        () -> entity.power.status)
        );
    }

    removeBar("liquid");
    removeBar("items");
    if (!powerBarI && hasPower) removeBar("power");
    if (powerBarO) {
        addBar("poweroutput", (CT3NoChooseMulti.CreatorsNoChooseMultiBuild entity) ->
                new Bar(() -> bundle.format("bar.poweroutputA", Strings.fixed(entity.getPowerProduction() * 60 * entity.timeScale(), 1)),
                        () -> Pal.powerBar,
                        () -> entity.getPowerProduction() == 0 ? 0f : 1f)
        );
    }
    if (!liquidSet.isEmpty()) {
        liquidSet.each(k -> addBar(k.localizedName, entity -> new Bar(
            () -> k.localizedName + "[" + Math.round(entity.liquids.get(k)) + "]",
            k::barColor,
            () -> entity.liquids.get(k) / liquidCapacity
        )));
    }

    for (var i = 0; i < recs.length; i++) {
        int finalI = i;
        addBar("配方条" + i, (CT3NoChooseMulti.CreatorsNoChooseMultiBuild e) ->
                new Bar(
                        () -> bundle.format("bar.warmup", Math.floor(e.进度条(finalI) * 100.0f) + " %"),
                        () -> Pal.powerBar,
                        () -> e.进度条(finalI)
                )
        );
    }
}


    @Override
    public void setStats() {
        super.setStats();

        stats.remove(Stat.powerUse);
        stats.remove(Stat.productionTime);

        stats.add(Stat.input, table -> {
            table.row();
            table.table(infoStyle.up, part -> {
                for (CreatorsRecipe rec : recs) {
                    boolean jj = false;
                    for (ItemStack inputItem : rec.input.items) {
                        jj = true;
                        part.add(new ItemDisplay(inputItem.item, inputItem.amount, false)).scaling(Scaling.fill);
                        ;
                    }
                    for (LiquidStack inputLiquid : rec.input.liquids) {
                        jj = true;
                        part.add(new LiquidDisplay(inputLiquid.liquid, inputLiquid.amount, false)).scaling(Scaling.fill);
                        ;
                    }
                    if (rec.input.power != 0) {
                        part.add((jj ? "+ " : "") + rec.input.power * 60f).scaling(Scaling.fill);
                        ;
                        part.image(Icon.power).scaling(Scaling.fill);
                    }
                    part.image(Core.atlas.find("ctcoresystem-arrows")).scaling(Scaling.fill);

                    boolean jjj = false;
                    for (ItemStack outputItem : rec.output.items) {
                        jjj = true;
                        part.add(new ItemDisplay(outputItem.item, outputItem.amount, false)).scaling(Scaling.fill);
                        ;
                    }
                    for (LiquidStack outputLiquid : rec.output.liquids) {
                        jjj = true;
                        part.add(new LiquidDisplay(outputLiquid.liquid, outputLiquid.amount, false)).scaling(Scaling.fill);
                        ;
                    }
                    if (rec.output.power != 0) {
                        part.add((jjj ? "+ " : "") + rec.output.power * 60f).scaling(Scaling.fill);
                        ;
                        part.image(Icon.power).scaling(Scaling.fill);
                    }
                    part.add(" [" + display(rec.craftTime / 60.0f) + "s]").scaling(Scaling.fill);
                    ;
                    part.row();
                }
            }).color(TableColor);
        });
    }

    public String display(float value) {
        int precision = Math.abs((int) value - value) <= 0.001f ? 0 : Math.abs((int) (value * 10) - value * 10) <= 0.001f ? 1 : 2;

        return Strings.fixed(value, precision);
    }

    @Override
    public void init() {
        for (CreatorsRecipe rec : recs) {
            if (rec.input.power > 0f) {
                powerBarI = true;
            }
            if (rec.output.power > 0f) {
                powerBarO = true;
            }

            if (rec.input.items.length > 0) {
                hasItems = true;
                for (int i = 0; i < rec.input.items.length; i++) {
                    inputItemSet.add(rec.input.items[i].item);
                }
            }
            if (rec.input.liquids.length > 0) {
                hasLiquids = true;
                for (int i = 0; i < rec.input.liquids.length; i++) {
                    liquidSet.add(rec.input.liquids[i].liquid);
                    inputLiquidSet.add(rec.input.liquids[i].liquid);
                }
            }
            if (rec.output.items.length > 0) {
                hasItems = true;
            }
            if (rec.output.liquids.length > 0) {
                hasLiquids = true;
                for (int i = 0; i < rec.output.liquids.length; i++) {
                    liquidSet.add(rec.output.liquids[i].liquid);
                }
            }
        }

        hasPower = powerBarI || powerBarO;
        if (powerBarI) consPower = new MultiConsumePower();
        consumesPower = powerBarI;
        outputsPower = powerBarO;

        super.init();

        if (!headless) infoStyle = scene.getStyle(Button.ButtonStyle.class);
    }

    public void addRecipe(CreatorsRecipe.InputContents input, CreatorsRecipe.OutputContents output, float craftTime) {
        recs[index++] = new CreatorsRecipe(input, output, craftTime);
    }

    public class MultiConsumePower extends ConsumePower {
        public float requestedPower(Building entity) {
            float power = 0.0f;
            if (entity instanceof CreatorsNoChooseMultiBuild) {
                var build = (CreatorsNoChooseMultiBuild) entity;
                for (var i = 0; i < recs.length; i++) {
                    if (recs[i].input.power > 0) {
                        if (build.生产状态[i]) {
                            power += recs[i].input.power;
                        }
                    }
                }
            }
            return power;
        }
    }

    public class CreatorsNoChooseMultiBuild extends GenericCrafterBuild {

        public boolean[] 生产状态 = new boolean[recs.length];
        public float[] 加工时间 = new float[recs.length];

        public float 进度条(int i) {
            return 加工时间[i] / recs[i].craftTime;
        }

        public  Color getStatusColor() {

            // 检查是否有配方满足输入条件但无法输出（待机状态）
            for (var i = 0; i < recs.length; i++) {
                    // 检查输出是否会被阻塞（即输出槽满）
                    if (检测输出阻塞(recs[i].output)) {
                        return Color.orange; // 待机时黄色 - 输出被阻塞
                    }
            }
            // 检查是否有正在工作的配方
            for (boolean active : 生产状态) {
                if (active) return Color.green; // 工作时绿色
            }
            return Pal.remove; // 缺失时红色
        }

        public boolean 检测消耗(CreatorsRecipe.InputContents a) {
            if (a.items != null) {
                for (var A : a.items) {
                    if (items.get(A.item) < A.amount) {
                        return false;
                    }
                }
            }
            if (a.liquids != null) {
                for (var A : a.liquids) {
                    if (liquids.get(A.liquid) < A.amount) {
                        return false;
                    }
                }
            }

            return true;
        }

        public void 资源消耗(CreatorsRecipe.InputContents a) {
            if (a.items != null) {
                for (var A : a.items) {
                    items.remove(A.item, A.amount);
                }
            }
            if (a.liquids != null) {
                for (var A : a.liquids) {
                    liquids.remove(A.liquid, A.amount);
                }
            }
        }

        public float 副检测器(CreatorsRecipe.InputContents a, CreatorsRecipe.OutputContents b) {
            for (var i : b.items) {
                if (items.get(i.item) >= this.block.itemCapacity) {
                    return 0;
                }
            }
            for (var i : b.liquids) {
                if (liquids.get(i.liquid) >= this.block.liquidCapacity) {
                    return 0;
                }
            }

            if (a.power > 0) {
                return power.status * edelta();
            }

            return delta();
        }

        public void 存储资源输出(CreatorsRecipe.OutputContents a) {
            if (a.items != null) {
                for (var A : a.items) {
                    for (var i = 0; i < A.amount; i++) {
                        dump(A.item);
                    }
                }
            }
            if (a.liquids != null) {
                for (var A : a.liquids) {
                    for (var i = 0; i < A.amount; i++) {
                        dumpLiquid(A.liquid);
                    }
                }
            }
        }

        public void 资源输出(CreatorsRecipe.OutputContents a) {
            if (wasVisible) {
                craftEffect.at(x, y);
            }

            if (a.items != null) {
                for (var A : a.items) {
                    for (var i = 0; i < A.amount; i++) {
                        offload(A.item);
                    }
                }
            }

            if (a.liquids != null) {
                for (var A : a.liquids) {
                    liquids.add(A.liquid, A.amount);
                }
            }
        }
public boolean 检测输出阻塞(CreatorsRecipe.OutputContents output) {
    // 检查物品输出是否会被阻塞
    if (output.items != null) {
        for (var itemStack : output.items) {
            if (items.get(itemStack.item) >= this.block.itemCapacity) {
                return true; // 物品槽已满
            }
        }
    }

    // 检查液体输出是否会被阻塞
    if (output.liquids != null) {
        for (var liquidStack : output.liquids) {
            if (liquids.get(liquidStack.liquid) >= this.block.liquidCapacity) {
                return true; // 液体槽已满
            }
        }
    }

/*

    // 检查功率输出（如果有）//电力没有输出阻塞 注释掉
    if (output.power > 0 && this.block.outputsPower) {
        // 如果功率无法输出，可能也需要考虑
        if (this.power != null && this.power.graph.getLastPowerProduced() <= 0) {
            return true;
        }
    }
*/

    return false; // 没有输出阻塞
}

        @Override
        public void updateTile() {
            for (var i = 0; i < recs.length; i++) {
                if (生产状态[i]) {
                    加工时间[i] += 副检测器(recs[i].input, recs[i].output);
                    if (加工时间[i] >= recs[i].craftTime) {
                        资源输出(recs[i].output);
                        生产状态[i] = false;
                        加工时间[i] = 0;
                    }

                    if (wasVisible && (Mathf.chanceDelta(updateEffectChance * 副检测器(recs[i].input, recs[i].output)))) {
                        updateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4));
                    }
                } else {
                    if (cheating()) {
                        生产状态[i] = true;
                    } else {
                        if (检测消耗(recs[i].input)) {
                            资源消耗(recs[i].input);
                            生产状态[i] = true;
                        }
                    }
                }
            }

            if (timer(timerDump, dumpTime)) {
                for (CreatorsRecipe rec : recs) {
                    存储资源输出(rec.output);
                }
            }
        }

        @Override
        public float getPowerProduction() {
            float power = 0f;
            for (var i = 0; i < recs.length; i++) {
                if (recs[i].output.power > 0) {
                    if (生产状态[i]) {
                        power += recs[i].output.power;
                    }
                }
            }
            return power;
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            for (var i = 0; i < recs.length; i++) {
                write.bool(生产状态[i]);
                write.f(加工时间[i]);
            }
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            for (var i = 0; i < recs.length; i++) {
                生产状态[i] = read.bool();
                加工时间[i] = read.f();
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            if (items.get(item) < this.block.itemCapacity) {
                return inputItemSet.contains(item);
            } else {
                return false;
            }
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            if (liquids != null && liquids.get(liquid) < this.block.liquidCapacity) {
                return inputLiquidSet.contains(liquid);
            } else {
                return false;
            }
        }
        @Override
        public void draw() {
            if(方块贴图==true) {
                drawer.draw(this);
            }else {
                // 使用工具类渲染方块文字
                BlockTextRenderer.renderBlockText(x, y, localizedName, block.size);
            }
            // 绘制状态指示灯
            if (this.block.enableDrawStatus ) {
                float multiplier = this.block.size > 1 ? 1.0F : 0.64F;
                float brcx = this.x + (float)(this.block.size * 8) / 2.0F - 8.0F * multiplier / 2.0F;
                float brcy = this.y - (float)(this.block.size * 8) / 2.0F + 8.0F * multiplier / 2.0F;

                Draw.z(71.0F);
                Draw.color(Pal.gray);
                Fill.square(brcx, brcy, 2.5F * multiplier, 45.0F);
                Draw.color(getStatusColor());
                Fill.square(brcx, brcy, 1.5F * multiplier, 45.0F);
                Draw.color();
            }
        }
    }
}
