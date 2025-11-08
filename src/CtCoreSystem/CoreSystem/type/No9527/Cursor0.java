package CtCoreSystem.CoreSystem.type.No9527;

import CtCoreSystem.CtCoreSystem;
import arc.Core;
import arc.Graphics;
import arc.files.Fi;
import arc.graphics.Pixmap;
import arc.graphics.Pixmaps;
import arc.math.Mathf;
import arc.util.ArcRuntimeException;
import arc.util.Log;
import arc.util.OS;
import mindustry.Vars;
import mindustry.ui.Fonts;

import static CtCoreSystem.CtCoreSystem.CT;
import static mindustry.Vars.ui;

public class Cursor0 {
    private static Graphics.Cursor newCursor(String filename){
        Pixmap p = new Pixmap(CT.root.child("cursor").child(filename));
        return Core.graphics.newCursor(p, p.width /2, p.height /2);
    }
    private static Graphics.Cursor newCursor(String filename, int scale){
        if(scale == 1 || OS.isAndroid || OS.isIos) return newCursor(filename);
        Pixmap base = new Pixmap(CT.root.child("cursor").child(filename));
        Pixmap result = Pixmaps.scale(base, base.width * scale, base.height * scale);
        base.dispose();
        return Core.graphics.newCursor(result, result.width /2, result.height /2);
    }
    public static void CToverrideUI(){
        CT = Vars.mods.getMod(CtCoreSystem.class);
        Graphics.Cursor.SystemCursor.arrow.set(newCursor("cursor.png", Fonts.cursorScale()));
        Graphics.Cursor.SystemCursor.hand.set(newCursor("hand.png", Fonts.cursorScale()));
        Graphics.Cursor.SystemCursor.ibeam.set(newCursor("ibeam.png", Fonts.cursorScale()));
        ui.drillCursor = newCursor("drill.png", Fonts.cursorScale());
        ui.unloadCursor = newCursor("unload.png", Fonts.cursorScale());
        ui.targetCursor = newCursor("target.png", Fonts.cursorScale());
    }
}
