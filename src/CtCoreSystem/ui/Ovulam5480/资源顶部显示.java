package CtCoreSystem.ui.Ovulam5480;

import arc.graphics.Color;
import arc.util.Interval;
import arc.util.Log;
import mindustry.Vars;
import mindustry.core.UI;
import mindustry.type.Item;
import mindustry.ui.CoreItemsDisplay;
import mindustry.ui.Styles;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.modules.ItemModule;

import static mindustry.Vars.content;
import static mindustry.Vars.iconSmall;

public class 资源顶部显示 extends CoreItemsDisplay {
    private CoreBlock.CoreBuild core;
    private final float timeDelta = 30f;
    public ItemModule lastItems = new ItemModule();
    public Interval interval = new Interval();
    // 每行多少个
    float columns = 10;

    public 资源顶部显示(){
        rebuild();
        Log.info("创世神资源顶部显示已加载");
    }

    @Override
    public void resetUsed(){
        lastItems.clear();
        background(null);
    }

    void rebuild(){
        clear();
        if(lastItems.total() > 0){
            background(Styles.black6);
            margin(4);
        }

        update(() -> {
            core = Vars.player.team().core();

            if(content.items().contains(item -> core != null && core.items.get(item) > 0 && lastItems.get(item) != 0)){
                rebuild();
            }

            if(core != null && interval.get(timeDelta)){
                lastItems.set(core.items);
            }
        });

        int i = 0;

        for(Item item : content.items()){
            if(lastItems.get(item) != 0){
                image(item.uiIcon).size(iconSmall).padRight(3).tooltip(t -> t.background(Styles.black6).margin(4f).add(item.localizedName).style(Styles.outlineLabel));
                label(() -> {
                    int ci = core != null ? core.items.get(item) : 0;

                    String color = "[#";
                    if(ci > lastItems.get(item))color += Color.green.toString();
                    else if(ci == lastItems.get(item))color += Color.white.toString();
                    else color += Color.red.toString();
                    color += "]";

                    if(ci == 0)color = "";

                    return color + (core == null ? "0" : UI.formatAmount(ci));
                }).padRight(3).minWidth(52f).left().tooltip(t -> t.background(Styles.black6).margin(4f).label(() -> core == null ? "0" : core.items.get(item) + "").style(Styles.outlineLabel));

                if(++i % columns == 0){
                    row();
                }
            }
        }

    }

}
