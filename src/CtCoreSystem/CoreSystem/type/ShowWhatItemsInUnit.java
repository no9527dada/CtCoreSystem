package CtCoreSystem.CoreSystem.type;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.Element;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.Fonts;

/*
 * @Author:Ovulam5480 & LYBF
 * @Desc  :单位死亡掉落显示
 *
 */
public class ShowWhatItemsInUnit extends Ability {
    ItemStack[] itemStacks;

    public ShowWhatItemsInUnit(ItemStack[] itemStacks) {
        this.itemStacks = itemStacks;
    }

    @Override
    public void displayBars(Unit unit, Table bars) {
        super.displayBars(unit, bars);
    }

    @Override
    public String localized() {
        return Core.bundle.get("ability.ct-unitdeathreward");
    }

    @Override
    public void addStats(Table t) {
        super.addStats(t);
        t.row();
        for (ItemStack itemStack : itemStacks) {
            t.add(itemStack.amount + "x");
            Image image = new Image(itemStack.item.fullIcon);
            t.add(itemStack.item.localizedName+" " );
            t.add(image);
            t.row();
        }
    }

    @Override
    public void death(Unit unit) {

        for (int i = 0; i < itemStacks.length; i++) {
            ItemStack itemStack = itemStacks[i];
            whatItemsInUnit.at(unit.x, unit.y + 8 * (i + 2), itemStack.amount, itemStack.item);
        }
    }

    public Effect whatItemsInUnit = new Effect(120, e -> {
        if (!(e.data instanceof Item item)) return;
        int amount = (int) e.rotation;
        Draw.z(Layer.flyingUnit + 1);

        float alpha;

        if (e.fin() < 0.1f) alpha = e.fin() * 10;
        else alpha = e.fout() * 10 / 9;

        Draw.color(Color.black);
        Draw.alpha(0.5f * alpha);
        Fill.rect(e.x, e.y, (Mathf.digits(amount) + 1) * 8f, 8);
        Draw.reset();

        Draw.alpha(alpha);
        Draw.rect(item.fullIcon, -Mathf.digits(amount) * 4 + e.x, e.y);

        Font font = Fonts.outline;
        font.getData().setScale(1f / 2.5f / Scl.scl(1f));
        font.setColor(1, 1, 1, alpha);
        font.draw(String.valueOf(amount), 4 + e.x, e.y + 3, Align.center);

        Draw.reset();
        font.setColor(Color.white);
        font.getData().setScale(1f);

    });
}