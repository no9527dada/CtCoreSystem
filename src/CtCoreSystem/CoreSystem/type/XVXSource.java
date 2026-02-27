package CtCoreSystem.CoreSystem.type;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.power.PowerNode;

import java.util.Iterator;

public class XVXSource extends PowerNode {
    public float heatOutput = Float.MAX_VALUE; // 无限热量输出
    public float warmupRate = 0.15f;

    public XVXSource(String name) {
        super(name);
        this.maxNodes = 0;
        this.outputsPower = true;
        this.consumesPower = false;
        this.envEnabled = -1;
        this.update = true;
        this.laserRange = 0.5f;
        this.buildType = () -> {
            return new XVXSourceBuild();
        };
    }

    public class XVXSourceBuild extends PowerNode.PowerNodeBuild implements HeatBlock {
        public float heat;

        public XVXSourceBuild() {
            super();
        }

        public void updateTile() {
            super.updateTile();

            // 更新热量
            this.heat = XVXSource.this.heatOutput;

            Iterator var1 = this.proximity.iterator();

            while (var1.hasNext()) {
                Building p = (Building) var1.next();
                Iterator var3 = Vars.content.items().iterator();

                while (var3.hasNext()) {
                    Item a = (Item) var3.next();
                    if (!p.block.hasItems || p.items == null) {
                        break;
                    }

                    if (p.acceptItem(this, a)) {
                        p.handleItem(p, a);
                    }
                }

                var3 = Vars.content.liquids().iterator();

                while (var3.hasNext()) {
                    Liquid ax = (Liquid) var3.next();
                    if (!p.block.hasLiquids || p.liquids == null) {
                        break;
                    }

                    if (p.acceptLiquid(this, ax)) {
                        p.handleLiquid(p, ax, 2.0F);
                    }
                }
            }
        }

        public float getPowerProduction() {
            return this.enabled ? 1000000.0F / 60 : 0.0F;
        }

        public void draw() {
            super.draw();
            Color RGB = new Color(1.0F, 1.0F, 1.0F, 1.0F);
            RGB.fromHsv(Time.globalTime * 3.0F % 360.0F, 1.0F, 1.0F);
            Draw.color(RGB);
            Draw.rect("center", this.x, this.y);
            Draw.color();
            XVXSource.this.laserColor1 = RGB;
            XVXSource.this.laserColor2 = RGB;
        }

        @Override
        public float heat() {
            return this.heat;
        }

        @Override
        public float heatFrac() {
            return this.heat / XVXSource.this.heatOutput;
        }
    }
}
