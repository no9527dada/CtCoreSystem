package CtCoreSystem.CoreSystem.type.MinRi2.ui;

import arc.graphics.*;
import arc.math.*;
import arc.scene.actions.*;
import mindustry.gen.*;
import mindustry.ui.*;

/**
 * @author minri2
 * Create by 2024/7/20
 */
public class BorderColorImage extends BorderImage{

    public BorderColorImage(Color color){
        this(color, 4f);
    }

    public BorderColorImage(Color color, float thick){
        super(Tex.whiteui);

        thickness = thick;
        setColor(color);
    }

    public void setColorSmooth(Color newColor){
        setColorSmooth(newColor, 0.75f, Interp.smooth);
    }

    public void setColorSmooth(Color newColor, float duration, Interp interp){
        if(color.equals(newColor)){
            return;
        }

        addAction(Actions.color(newColor, duration, interp));
    }
}
