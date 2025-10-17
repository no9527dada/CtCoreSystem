package CtCoreSystem.CoreSystem.type.Ovulam5480;


import arc.math.Mathf;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.type.*;
import mindustry.ui.Bar;
import mindustry.world.blocks.units.UnitBlock;
import mindustry.world.consumers.ConsumePayloads;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;
//本类不可用 问题： 单位无法进入工厂

/*
*它继承自UnitBlock，是一个处理单位的方块
*它接收特定类型的单位作为输入(payload)
*经过一定的制作时间后，产出物品或液体
*有生产进度条、液体存储等功能
*/


public class UnitProcessor extends UnitBlock {
    // 将单一单位类型改为单位类型数组
    public Seq<UnitType> unitTypes = new Seq<>();
    // 兼容旧代码的单一单位类型设置
    public @Nullable UnitType unitType;
    /** Written to outputItems as a single-element array if outputItems is null. */
    public @Nullable ItemStack outputItem;
    /** Overwrites outputItem if not null. */
    public @Nullable ItemStack[] outputItems;

    /** Written to outputLiquids as a single-element array if outputLiquids is null. */
    public @Nullable LiquidStack outputLiquid;
    /** Overwrites outputLiquid if not null. */
    public @Nullable LiquidStack[] outputLiquids;
    /** Liquid output directions, specified in the same order as outputLiquids. Use -1 to dump in every direction. Rotations are relative to block. */
    public int[] liquidOutputDirections = {-1};

    /** if true, crafters with multiple liquid outputs will dump excess when there's still space for at least one liquid type */
    public boolean dumpExtraLiquid = true;
    public boolean ignoreLiquidFullness = false;

    public float craftTime = 80;
    public Effect craftEffect = Fx.none;
    public Effect updateEffect = Fx.none;
    public float updateEffectChance = 0.04f;
    public float updateEffectSpread = 4f;
    public float warmupSpeed = 0.019f;

    public DrawBlock drawer = new DrawDefault();

    public UnitProcessor(String name) {
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        ambientSound = Sounds.machine;
        sync = true;
        ambientSoundVolume = 0.03f;
        flags = EnumSet.of(BlockFlag.factory);
        drawArrow = false;
    }

    @Override
    public void setStats(){
        stats.timePeriod = craftTime;
        super.setStats();
        if((hasItems && itemCapacity > 0) || outputItems != null){
            stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds);
        }

        if(outputItems != null){
            stats.add(Stat.output, StatValues.items(craftTime, outputItems));
        }

        if(outputLiquids != null){
            stats.add(Stat.output, StatValues.liquids(1f, outputLiquids));
        }
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("progress", (UnitProcessorBuild e) -> new Bar("bar.progress", Pal.ammo, e::fraction));

        //set up liquid bars for liquid outputs
        if(outputLiquids != null && outputLiquids.length > 0){
            //no need for dynamic liquid bar
            removeBar("liquid");

            //then display output buffer
            for(var stack : outputLiquids){
                addLiquidBar(stack.liquid);
            }
        }
    }

    @Override
    public void init(){
        if(outputItems == null && outputItem != null){
            outputItems = new ItemStack[]{outputItem};
        }
        if(outputLiquids == null && outputLiquid != null){
            outputLiquids = new LiquidStack[]{outputLiquid};
        }
        //write back to outputLiquid, as it helps with sensing
        if(outputLiquid == null && outputLiquids != null && outputLiquids.length > 0){
            outputLiquid = outputLiquids[0];
        }
        outputsLiquid = outputLiquids != null;

        if(outputItems != null) hasItems = true;
        if(outputLiquids != null) hasLiquids = true;

        // 初始化单位类型列表，如果设置了单个单位类型，则添加到列表中
        if(unitType != null && !unitTypes.contains(unitType)){
            unitTypes.add(unitType);
        }

        // 创建多种单位的消耗配置
        Seq<PayloadStack> stacks = new Seq<>();
        for(UnitType type : unitTypes){
            stacks.add(PayloadStack.with(type, 1));
        }
        consume(new ConsumePayloads(stacks));

        super.init();
    }

    public class UnitProcessorBuild extends UnitBuild{
        public float progress;
        public float totalProgress;
        public float warmup;
        PayloadSeq seq = new PayloadSeq();

        public float fraction(){
            return progress;
        }

        @Override
        public boolean acceptUnitPayload(Unit unit){
            // 检查是否为空，以及单位类型是否在允许的列表中
            return this.payload == null && unitTypes.contains(unit.type);
        }
        @Override
        public PayloadSeq getPayloads() {
            seq.clear();
            if(payload != null && payload.content() instanceof UnitType){
                seq.add((UnitType)payload.content());
            }
            return seq;
        }

        @Override
        public void updateTile(){
            if(efficiency > 0){

                progress += getProgressIncrease(craftTime);
                warmup = Mathf.approachDelta(warmup, warmupTarget(), warmupSpeed);

                //continuously output based on efficiency
                if(outputLiquids != null){
                    float inc = getProgressIncrease(1f);
                    for(var output : outputLiquids){
                        handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
                    }
                }

                if(wasVisible && Mathf.chanceDelta(updateEffectChance)){
                    updateEffect.at(x + Mathf.range(size * updateEffectSpread), y + Mathf.range(size * updateEffectSpread));
                }
            }else{
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
            }

            //TODO may look bad, revert to edelta() if so
            totalProgress += warmup * Time.delta;

            if(progress >= 1f){
                craft();
            }

            dumpOutputs();
        }

        @Override
        public float getProgressIncrease(float baseTime){
            if(ignoreLiquidFullness){
                return super.getProgressIncrease(baseTime);
            }

            //limit progress increase by maximum amount of liquid it can produce
            float scaling = 1f, max = 1f;
            if(outputLiquids != null){
                max = 0f;
                for(var s : outputLiquids){
                    float value = (liquidCapacity - liquids.get(s.liquid)) / (s.amount * edelta());
                    scaling = Math.min(scaling, value);
                    max = Math.max(max, value);
                }
            }

            //when dumping excess take the maximum value instead of the minimum.
            return super.getProgressIncrease(baseTime) * (dumpExtraLiquid ? Math.min(max, 1f) : scaling);
        }

        public float warmupTarget(){
            return 1f;
        }

        @Override
        public float warmup(){
            return warmup;
        }

        @Override
        public float totalProgress(){
            return totalProgress;
        }

        public void craft(){
            consume();
            payload = null;

            if(outputItems != null){
                for(var output : outputItems){
                    for(int i = 0; i < output.amount; i++){
                        offload(output.item);
                    }
                }
            }

            if(wasVisible){
                craftEffect.at(x, y);
            }
            progress %= 1f;
        }

        public void dumpOutputs(){
            if(outputItems != null && timer(timerDump, dumpTime / timeScale)){
                for(ItemStack output : outputItems){
                    dump(output.item);
                }
            }

            if(outputLiquids != null){
                for(int i = 0; i < outputLiquids.length; i++){
                    int dir = liquidOutputDirections.length > i ? liquidOutputDirections[i] : -1;

                    dumpLiquid(outputLiquids[i].liquid, 2f, dir);
                }
            }
        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.progress) return progress();
            //attempt to prevent wild total liquid fluctuation, at least for crafters
            if(sensor == LAccess.totalLiquids && outputLiquid != null) return liquids.get(outputLiquid.liquid);
            return super.sense(sensor);
        }

        @Override
        public float progress(){
            return Mathf.clamp(progress);
        }

        @Override
        public int getMaximumAccepted(Item item){
            return itemCapacity;
        }

        @Override
        public boolean shouldAmbientSound(){
            return efficiency > 0;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(progress);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            progress = read.f();
        }

        @Override
        public boolean shouldConsume(){
            if(outputItems != null){
                for(var output : outputItems){
                    if(items.get(output.item) + output.amount > itemCapacity){
                        return false;
                    }
                }
            }
            if(outputLiquids != null && !ignoreLiquidFullness){
                boolean allFull = true;
                for(var output : outputLiquids){
                    if(liquids.get(output.liquid) >= liquidCapacity - 0.001f){
                        if(!dumpExtraLiquid){
                            return false;
                        }
                    }else{
                        //if there's still space left, it's not full for all liquids
                        allFull = false;
                    }
                }

                //if there is no space left for any liquid, it can't reproduce
                if(allFull){
                    return false;
                }
            }

            return enabled;
        }
    }
}
