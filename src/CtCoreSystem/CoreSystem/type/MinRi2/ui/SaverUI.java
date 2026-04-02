package CtCoreSystem.CoreSystem.type.MinRi2.ui;

import CtCoreSystem.CoreSystem.type.MinRi2.*;
import arc.*;
import arc.flabel.*;
import arc.func.*;
import arc.math.*;
import arc.scene.actions.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.ui.*;

/**
 * @author minri2
 * Create by 2024/7/21
 */
public class SaverUI{
    public static Runnable showSaving(){
        Table table = new Table();

        table.touchable = Touchable.disabled;

        Image image = table.image(Tex.alphaaaa).get();

        table.row();
        table.add(Core.bundle.get(SaverVars.modPrefix + "saving", "Auto Saving")).style(Styles.outlineLabel);
        table.add(new FLabel("{WIND}..."));

        float[] lastRotate = {Time.time};
        float rotateInterval = 1.25f;

        table.update(() -> {
            table.toFront();

            if(Time.time - lastRotate[0] >= rotateInterval){
                lastRotate[0] = Time.time*60f;

                image.setOrigin(Align.center);
                image.actions(
                        Actions.parallel(
                                Actions.sequence(
                                        Actions.scaleTo(1.2f, 1.2f, 0.5f, Interp.smooth),
                                        Actions.scaleTo(1f, 1f, 0.5f, Interp.smooth)
                                ),
                                Actions.rotateBy(-360, 1f, Interp.smooth)
                        )
                );
            }
        });

        Core.scene.root.fill(t -> {
            t.bottom();

            t.add(table);
        });

        return () -> {
            table.actions(
            Actions.delay(5), // 看看alpha动画吧!
            Actions.scaleTo(0, 0),
            Actions.remove()
            );
        };
    }
}
