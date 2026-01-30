package CtCoreSystem.CoreSystem.type.guiY;

import CtCoreSystem.CoreSystem.draw.CreatorsStyles;
import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.type.Category;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.blocks.production.Incinerator;
import mindustry.world.meta.BuildVisibility;
import java.util.HashMap;
import java.util.Map;

public class CTlajixiang extends Incinerator {
    private static Map<String, TextureRegion> loadRegionCache = new HashMap<>();
    // 加载纹理的方法
    public static TextureRegion loadRegion(String name) {
        // 如果是无头模式，返回null
        if (Vars.headless) {
            return null;
        }

        // 检查缓存中是否已有该纹理
        TextureRegion region = loadRegionCache.get(name);
        if (region != null) {
            return region;
        }

        // 从atlas中查找纹理，如果找不到则使用错误纹理
        String atlasName = "ctcoresystem-" + name;
        region = Core.atlas.find(atlasName, Core.atlas.find("error"));

        // 打印日志
        System.out.println("find " + atlasName + " result: " + region);

        // 缓存纹理
        loadRegionCache.put(name, region);
        return region;
    }

    public CTlajixiang(String name) {
        super(name);
        buildType = Build::new;
        sync = true;
        health = 110;
        buildVisibility = BuildVisibility.shown;
        category = Category.crafting;
        configurable = true;
        consumePower(0.5f);
        // 配置功能
        config(Integer.class, (tile, value) -> {
            ((Build) tile).setRecord(value);
        });
    }

    // 自定义Build类
    class Build extends Incinerator.IncineratorBuild {
        private boolean cI = true;
        private boolean cL = true;
        private int record = 1;
        public void setRecord(int v) {
            record = v;
        }

        @Override
        public void configured(Unit player, Object value){
            super.configured(player, value);

            if (value instanceof Integer) {
                int val = (Integer) value;
                switch (val) {
                    case 1:
                        cI = cL = true;
                        break;
                    case 2:
                        cI = cL = false;
                        break;
                    case 3:
                        cI = true;
                        cL = false;
                        break;
                    case 4:
                        cI = false;
                        cL = true;
                        break;
                }
            }
        }

        @Override
        public void draw() {
            super.draw();
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return heat > 0.5f && cI;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return heat > 0.5f && cL;
        }

        private void switchItem() {
            cI = !cI;
            updateRecord();
            configure(record);
        }

        private void switchLiquid() {
            cL = !cL;
            updateRecord();
            configure(record);
        }

        private void updateRecord() {
            if (cI == cL) {
                record = cI ? 1 : 2;
            } else if (cI && !cL) {
                record = 3;
            } else if (!cI && cL) {
                record = 4;
            }
        }


        @Override
        public void buildConfiguration(Table table) {
            // 创建第一个按钮（物品开关）
            ImageButton itemButton = new ImageButton(new TextureRegionDrawable(cI ? loadRegion("c1t") : loadRegion("c1f")), CreatorsStyles.clearTransi);
            itemButton.resizeImage(36f); // 设置图标大小
            itemButton.clicked(this::switchItem); // 添加点击事件
           // itemButton.setTooltip("switch mode"); // 设置提示
            // 添加update方法，每次更新时检查状态并更新纹理
            itemButton.update(() -> {
                itemButton.getStyle().imageUp = new TextureRegionDrawable(cI ? loadRegion("c1t") : loadRegion("c1f"));
            });
            table.add(itemButton).size(40); // 添加到表格并设置大小

            // 创建第二个按钮（液体开关）
            ImageButton liquidButton = new ImageButton(new TextureRegionDrawable(cL ? loadRegion("c2t") : loadRegion("c2f")), CreatorsStyles.clearTransi);
            liquidButton.resizeImage(36f); // 设置图标大小
            liquidButton.clicked(this::switchLiquid); // 添加点击事件
           // liquidButton.setTooltip("switch mode"); // 设置提示
            // 添加update方法，每次更新时检查状态并更新纹理
            liquidButton.update(() -> {
                liquidButton.getStyle().imageUp = new TextureRegionDrawable(cL ? loadRegion("c2t") : loadRegion("c2f"));
            });
            table.add(liquidButton).size(40); // 添加到表格并设置大小
        }
        @Override
        public Object config() {
            updateRecord();
            return record;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.bool(cI);
            write.bool(cL);
            write.i(record);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            cI = read.bool();
            cL = read.bool();
            record = read.i();
        }
    }
}
