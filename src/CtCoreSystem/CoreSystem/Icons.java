package CtCoreSystem.CoreSystem;

import CtCoreSystem.CtCoreSystem;
import arc.Core;
import arc.graphics.g2d.TextureRegion;

/**
 * @author LYBF
 */
public class Icons {
    public static TextureRegion check_selected;
    public static TextureRegion check_unselected;

    public static void init(){
        check_selected = load("check-1");
        check_unselected = load("check-2");
    }

    private static TextureRegion load(String name){
        return Core.atlas.find(CtCoreSystem.name(name));
    }
}
