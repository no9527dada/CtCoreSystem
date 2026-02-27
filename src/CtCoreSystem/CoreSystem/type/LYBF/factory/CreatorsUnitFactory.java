package CtCoreSystem.CoreSystem.type.LYBF.factory;

import CtCoreSystem.CoreSystem.type.V8.ItemDisplay;
import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.struct.IntSeq;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.io.TypeIO;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.ui.Bar;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.blocks.units.UnitBlock;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.meta.Stat;


import static mindustry.Vars.state;

/*
 *modified by LYBF
 *@Date  :2024/3/11
 * 单位工厂
 */
public class CreatorsUnitFactory extends UnitBlock {

    public int unitLimit = 5;
    public int[] capacities = {};

    public Seq<UnitPlan> plans = new Seq<>(4);

    public CreatorsUnitFactory(String name) {
        this(name, 8);//default
    }

    public CreatorsUnitFactory(String name, int unitLimit) {
        super(name);
        //Log.info("config");
        this.unitLimit = unitLimit;
        update = true;
        hasPower = true;
        hasItems = true;
        solid = true;
        configurable = true;
        clearOnDoubleTap = true;
        outputsPayload = true;
        rotate = true;
        regionRotated1 = 1;
        commandable = true;
        ambientSound = Sounds.loopUnitBuilding;

        config(Integer.class, (CreatorsUnitFactoryBuild tile, Integer i) -> {
            if (!configurable) return;
            if (tile.currentPlan == i) return;
            tile.currentPlan = i < 0 || i >= plans.size ? -1 : i;
            tile.progress = 0;
        });

        config(UnitType.class, (CreatorsUnitFactoryBuild tile, UnitType val) -> {
            if (!configurable) return;

            int next = plans.indexOf(p -> p.unit == val);
            if (tile.currentPlan == next) return;
            tile.currentPlan = next;
            tile.progress = 0;
        });

        consume(new ConsumeItemDynamic((CreatorsUnitFactoryBuild e) -> e.currentPlan != -1 ? plans.get(Math.min(e.currentPlan, plans.size - 1)).requirements : ItemStack.empty));


    }


    @Override
    public void init() {
        capacities = new int[Vars.content.items().size];
        for (UnitPlan plan : plans) {
            for (ItemStack stack : plan.requirements) {
                capacities[stack.item.id] = Math.max(capacities[stack.item.id], stack.amount * 2);
                itemCapacity = Math.max(itemCapacity, stack.amount * 2);
            }
        }

        consumeBuilder.each(c -> c.multiplier = b -> state.rules.unitCost(b.team));

        super.init();
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("progress", (CreatorsUnitFactoryBuild e) -> new Bar("bar.progress", Pal.ammo, e::fraction));

        addBar("units", (CreatorsUnitFactoryBuild e) ->
                new Bar(
                        () -> e.unit() == null ? "[lightgray]" + Iconc.cancel :
                                Core.bundle.format("bar.unitcap",
                                        Fonts.getUnicodeStr(e.unit().name),
                                        e.team.data().countType(e.unit()),
                                        Units.getStringCap(e.team)
                                ),
                        () -> Pal.power,
                        () -> e.unit() == null ? 0f : (float) e.team.data().countType(e.unit()) / Units.getCap(e.team)
                ));
    }

    @Override
    public boolean outputsItems() {
        return false;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.itemCapacity);
        stats.add(Stat.output, table -> {
            table.row();
            for (var plan : plans) {
                table.table(Styles.grayPanel, t -> {
                    if (plan.unit.isBanned()) {
                        t.image(Icon.cancel).color(Pal.remove).size(40);
                        return;
                    }

                    if (plan.unit.unlockedNow()) {
                        t.image(plan.unit.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit);
                        t.table(info -> {
                            info.add(plan.unit.localizedName).left();
                            info.row();
                            info.add(Strings.autoFixed(plan.time / 60f, 1) + " " + Core.bundle.get("unit.seconds")).color(Color.lightGray);
                        }).left();
                        t.table(req -> {
                            req.right();
                            for (int i = 0; i < plan.requirements.length; i++) {
                                if (i % 6 == 0) {
                                    req.row();
                                }
                                ItemStack stack = plan.requirements[i];
                                req.add(new ItemDisplay(stack.item, stack.amount, false)).pad(5);
                            }
                        }).right().grow().pad(10f);
                    } else {
                        t.image(Icon.lock).color(Pal.darkerGray).size(40);
                    }
                }).growX().pad(5);
                table.row();
            }
        });
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{region, outRegion, topRegion};
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(region, plan.drawx(), plan.drawy());
        Draw.rect(outRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.rect(topRegion, plan.drawx(), plan.drawy());
    }

    public static class UnitPlan {
        public UnitType unit;
        public ItemStack[] requirements;
        public float time;

        public UnitPlan(UnitType unit, float time, ItemStack[] requirements) {
            this.unit = unit;
            this.time = time;
            this.requirements = requirements;
        }

        UnitPlan() {
        }
    }

    public class CreatorsUnitFactoryBuild extends UnitBuild {
        public int unitCount = unitLimit;

        public ObjectMap<UnitType, IntSeq> unitMap = new ObjectMap<>();

        public @Nullable Vec2 commandPos;
        public int currentPlan = -1;

        public CreatorsUnitFactoryBuild() {
            //Log.info("init factory");
        }

        public float fraction() {
            return currentPlan == -1 ? 0 : progress / plans.get(currentPlan).time;
        }

        @Override
        public Vec2 getCommandPosition() {
            return commandPos;
        }

        @Override
        public void onCommand(Vec2 target) {
            commandPos = target;
        }

        @Override
        public Object senseObject(LAccess sensor) {
            if (sensor == LAccess.config) return currentPlan == -1 ? null : plans.get(currentPlan).unit;
            return super.senseObject(sensor);
        }

   /*     @Override
        public boolean shouldActiveSound() {
            return shouldConsume();
        }*/

        @Override
        public double sense(LAccess sensor) {
            if (sensor == LAccess.progress) return Mathf.clamp(fraction());
            if (sensor == LAccess.itemCapacity) return Mathf.round(itemCapacity * state.rules.unitCost(team));
            return super.sense(sensor);
        }

        @Override
        public void buildConfiguration(Table table) {
            Seq<UnitType> units = Seq.with(plans).map(u -> u.unit).retainAll(u -> u.unlockedNow() && !u.isBanned());
            if (units.any()) {
                ItemSelection.buildTable(CreatorsUnitFactory.this, table, units, () -> currentPlan == -1 ? null : plans.get(currentPlan).unit, unit -> configure(plans.indexOf(u -> u.unit == unit)), selectionRows, selectionColumns);
            } else {
                table.table(Styles.black3, t -> t.add("@none").color(Color.lightGray));
            }
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload) {
            return false;
        }

        @Override
        public Object config() {
            return currentPlan;
        }

        @Override
        public void draw() {
            Draw.rect(region, x, y);
            Draw.rect(outRegion, x, y, rotdeg());

            if (currentPlan != -1) {
                UnitPlan plan = plans.get(currentPlan);
                Draw.draw(Layer.blockOver, () -> Drawf.construct(this, plan.unit, rotdeg() - 90f, progress / plan.time, speedScl, time));
            }

            Draw.z(Layer.blockOver);

            payRotation = rotdeg();
            drawPayload();

            Draw.z(Layer.blockOver + 0.1f);

            Draw.rect(topRegion, x, y);
        }

        @Override
        public void updateTile() {
            if (!configurable) {
                currentPlan = 0;
            }

            if (currentPlan < 0 || currentPlan >= plans.size) {
                currentPlan = -1;
            }

            if (efficiency > 0 && currentPlan != -1) {
                time += edelta() * speedScl * Vars.state.rules.unitBuildSpeed(team);
                progress += edelta() * Vars.state.rules.unitBuildSpeed(team);
                speedScl = Mathf.lerpDelta(speedScl, 1f, 0.05f);
            } else {
                speedScl = Mathf.lerpDelta(speedScl, 0f, 0.05f);
            }

            moveOutPayload();

            if (currentPlan != -1 && payload == null) {
                UnitPlan plan = plans.get(currentPlan);

                //make sure to reset plan when the unit got banned after placement
                if (plan.unit.isBanned()) {
                    currentPlan = -1;
                    return;
                }

                if (progress >= plan.time && this.getUnitIDs(plan.unit).size < unitCount) {
                    progress %= 1f;

                    Unit unit = plan.unit.create(team);
                    if (commandPos != null && unit.isCommandable()) {
                        unit.command().commandPosition(commandPos);
                    }
                    payload = new UnitPayload(unit);
                    payVector.setZero();
                    consume();
                    Events.fire(new EventType.UnitCreateEvent(payload.unit, this));
                }

                progress = Mathf.clamp(progress, 0, plan.time);
            } else {
                progress = 0f;
            }
            checkUnits();
        }

        @Override
        public void display(Table table) {
            super.display(table);
            TextureRegionDrawable reg = new TextureRegionDrawable();
            table.row();
            table.table(t -> {
                t.left();
                t.image().update(i -> {
                    i.setDrawable(currentPlan == -1 ? Icon.cancel : reg.set(plans.get(currentPlan).unit.uiIcon));
                    i.setScaling(Scaling.fit);
                    i.setColor(currentPlan == -1 ? Color.lightGray : Color.white);
                }).size(32).padBottom(-4).padRight(2);
                t.label(() -> currentPlan == -1 ? "@none" : plans.get(currentPlan).unit.localizedName).wrap().width(230f).color(Color.lightGray);
            }).left();
            if (unitMap.isEmpty()) return;
            table.marginBottom(0);
            table.row();
            table.table(null, main -> {
                main.defaults().growX();
                main.table(Tex.whiteui, title -> title.add("Units")).color(Pal.gray);
                main.row();
                main.table(Tex.pane2, bars -> {
                    bars.defaults().growX().height(18).pad(4);
                    unitMap.each((type, units) -> {
                        bars.add(new Bar(() -> Core.bundle.format("bar.unitcap", type.emoji(), units.size, unitCount),
                                () -> Pal.power, () -> units.size / unitCount));
                        bars.row();
                    });
                }).pad(8).growX();
            });

        }

        public void checkUnits() {
            if (unitMap.isEmpty()) return;
            unitMap.each((type, units) -> {
                units.each(id -> {
                    Unit u = Groups.unit.getByID(id);
                    int payloadUnitId = -1;
                    if (payload != null) {
                        payloadUnitId = payload.unit == null ? -1 : payload.unit.id;
                    }
                    if (u == null && payloadUnitId != id) {
                        units.removeValue(id);
                    }
                });
            });
        }

        @Override
        public void onRemoved() {
            removeLinkedUnits();
            super.onRemoved();
        }

        public void removeLinkedUnits() {
            if (unitMap.size == 0) return;
            unitMap.each((typeID, units) -> {
                units.each(unit -> {
                    Unit unit2 = Groups.unit.getByID(unit);
                    if (unit2 == null) {
                        units.removeValue(unit);
                        return;
                    }
                    if (unit2.isValid()) {
                        if (!unit2.dead && !Vars.net.client()) {
                            Call.unitDeath(unit);
                        }
                        unit2.killed();
                        unit2.remove();
                    }
                });
            });
        }

        @Override
        public boolean shouldConsume() {
            if (currentPlan == -1) return false;
            UnitType type = this.unit();
            return enabled && payload == null && type != null && this.getUnitIDs(type).size < unitCount;
        }

        @Override
        public void consume() {
            //Log.info("UnitFactory consume");
            if (payload == null || payload.unit == null) return;
            Unit unit = this.payload.unit;
            IntSeq units = this.getUnitIDs(unit.type);
            units.add(unit.id);
            //Log.info("add unit id" + unit.id + "to seq");

            super.consume();
        }

        @Override
        public int getMaximumAccepted(Item item) {
            return Mathf.round(capacities[item.id] * state.rules.unitCost(team));
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return currentPlan != -1 && items.get(item) < getMaximumAccepted(item) &&
                    Structs.contains(plans.get(currentPlan).requirements, stack -> stack.item == item);
        }

        public @Nullable UnitType unit() {
            return currentPlan == -1 ? null : plans.get(currentPlan).unit;
        }

        @Override
        public byte version() {
            return 2;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(progress);
            write.s(currentPlan);
            TypeIO.writeVecNullable(write, commandPos);
            //if (unitMap.isEmpty()) return;
            write.i(unitMap.size);
            unitMap.each((type, seq) -> {
                write.i(type.id);
                write.i(seq.size);
                seq.each(write::i);
            });
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            progress = read.f();
            currentPlan = read.s();
            if (revision >= 2) {
                commandPos = TypeIO.readVecNullable(read);
            }
            unitMap.clear();
            int mapSize = read.i();
            for (int i = 0; i < mapSize; i++) {
                int typeID = read.i();
                UnitType type = Vars.content.unit(typeID);
                if (type == null) {
                    Log.err("Faild read unit's id: @", typeID);
                    continue;
                }
                IntSeq seq = this.getUnitIDs(type);
                int seqSize = read.i();
                for (int j = 0; j < seqSize; j++) {
                    int unitID = read.i();
                    seq.add(unitID);
                }
            }

        }


        public IntSeq getUnitIDs(UnitType type) {
            return unitMap.get(type, () -> new IntSeq());
        }

        @Override
        public void drawSelect() {
            super.drawSelect();
            if (unitMap.isEmpty()) return;
            unitMap.each((typeID, units) -> {
                units.each(id -> {
                    Unit u = Groups.unit.getByID(id);
                    int payloadUnitId = -1;
                    if (payload != null) {
                        payloadUnitId = payload.unit == null ? -1 : payload.unit.id;
                    }
                    if (u == null && payloadUnitId != id) {
                        units.removeValue(id);
                    }
                    if (u == null || !u.isValid()) return;
                    this.drawUnitLink(u);
                });
            });
        }

        public void drawUnitLink(Unit unit) {
            float unitX = unit.x, unitY = unit.y;
            float drawX, drawY;
            float angle = Angles.angleRad(x, y, unitX, unitY);
            double sin = Math.sin(angle), cos = Math.cos(angle);
            double tan = Math.abs(sin / cos);
            int size = block.size * Vars.tilesize;
            int halfSize = size / 2;
            int sx = Mathf.sign(unitX >= x), sy = Mathf.sign(unitY >= y);
            if (tan <= 1) {
                drawX = x + sx * halfSize;
                drawY = (float) (y + sy * halfSize * tan);
            } else {
                drawX = (float) (x + sx * halfSize / tan);
                drawY = y + sy * halfSize;
            }

            float circleRadius = 2f,
                    unitRadius = (float) (unit.hitSize * 1.2);

            float startX = (float) (drawX + circleRadius * cos),
                    startY = (float) (drawY + circleRadius * sin);
            float endX = (float) (unitX - unitRadius * cos),
                    endY = (float) (unitY - unitRadius * sin);
            float rot = angle * Mathf.radDeg + 45;
            Lines.stroke(3, Pal.gray);
            Lines.square(drawX, drawY, circleRadius, rot);
            Lines.line(startX, startY, endX, endY);
            Lines.square(unitX, unitY, unitRadius, rot);
            Lines.stroke(1, Pal.accent);
            Lines.square(drawX, drawY, circleRadius, rot);
            Lines.line(startX, startY, endX, endY);
            Lines.square(unitX, unitY, unitRadius, rot);
        }


    }

}