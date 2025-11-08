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

public class Cursor {

    // 将文件夹变量声明为类成员变量
    private static String 文件夹;

    private static Graphics.Cursor newCursor(String filename){
        // 确保CT已经初始化
        if(CT == null){
            CT = Vars.mods.getMod(CtCoreSystem.class);
        }

        // 如果文件夹未初始化，则生成随机文件夹名
        if(文件夹 == null){
            int i = Mathf.random(0, 3);
            文件夹 = "cursor" + i; // 直接设置为cursor0, cursor1等
        }

        // 检查文件是否存在
        // 先在cursor文件夹下的子文件夹中查找
        Fi file = CT.root.child("cursor").child(文件夹).child(filename);
        if(!file.exists()){
            // 如果选定的文件夹不存在该文件，尝试使用默认的cursor文件夹
            file = CT.root.child("cursor").child(filename);
            Log.info("文件夹不存在该文件，尝试使用默认的cursor文件夹: " + file.path());
            if(!file.exists()){
                throw new ArcRuntimeException("无法找到光标文件: " + filename + " 在cursor文件夹下的" + 文件夹 + "子文件夹中");
            }
        }

        Pixmap p = new Pixmap(file);
        return Core.graphics.newCursor(p, p.width / 2, p.height / 2);
    }

    private static Graphics.Cursor newCursor(String filename, int scale){
        if(scale == 1 || OS.isAndroid || OS.isIos) return newCursor(filename);

        // 复用上面相同的文件夹变量（会在第一个方法中初始化）
        // 先在cursor文件夹下的子文件夹中查找
        Fi file = CT.root.child("cursor").child(文件夹).child(filename);
        if(!file.exists()){
            // 如果选定的文件夹不存在该文件，尝试使用默认的cursor文件夹
            file = CT.root.child("cursor").child(filename);
            Log.info("文件夹不存在该文件，尝试使用默认的cursor文件夹: " + file.path());
            if(!file.exists()){
                throw new ArcRuntimeException("无法找到光标文件: " + filename + " 在cursor文件夹下的" + 文件夹 + "子文件夹中");
            }
        }

        Pixmap base = new Pixmap(file);
        Pixmap result = Pixmaps.scale(base, base.width * scale, base.height * scale);
        base.dispose();
        return Core.graphics.newCursor(result, result.width / 2, result.height / 2);
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
