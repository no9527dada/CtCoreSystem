package ccdd;

import arc.Core;
import arc.Events;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.mod.Mod;

//import static CtCoreSystem.CtCoreSystem.disableModIfExists;
//import static CtCoreSystem.CtCoreSystem.加载CT2;

public class ccdd extends Mod {

    public void loadContent(){
      //  disableModIfExists("creators");
    }
    @Override
    public void init() {

        Events.on(EventType.ClientLoadEvent.class, (e) -> {
            Vars.ui.menufrag.addButton(Core.bundle.get("difficulty.game"), Icon.map, () -> {
             //   new SettingDifficultyDialog(加载CT2()).show();
            });
        });

    }
}
