package CtCoreSystem.ui;

import CtCoreSystem.ui.NanDu.SettingDifficultyDialog;
import arc.Core;
import arc.func.Boolc;
import arc.func.Boolp;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import mindustry.Vars;
import mindustry.content.Planets;
import mindustry.game.CampaignRules;
import mindustry.gen.Call;
import mindustry.gen.Tex;
import mindustry.type.Planet;
import mindustry.ui.dialogs.CampaignRulesDialog;

import static CtCoreSystem.CtCoreSystem.加载CT2;

public class CTCampaignRulesDialog extends CampaignRulesDialog {
    Planet planet;
    Table current;
    public static Table container;
    public CTCampaignRulesDialog(){
        //addCloseButton();

        hidden(() -> {
            if(planet != null){
                planet.saveRules();

                if(Vars.state.isGame() && Vars.state.isCampaign() && Vars.state.getPlanet() == planet){
                    planet.campaignRules.apply(planet, Vars.state.rules);
                    Call.setRules(Vars.state.rules);
                }
            }
        });
        onResize(() -> {
            rebuild();
        });
    }

    void rebuild(){
        CampaignRules rules = planet.campaignRules;
        cont.clear();

        cont.top().pane(inner -> {
            //原版难度修改删除

            inner.top().left().defaults().fillX().left().pad(5);
            current = inner;
            current.add("原版难度已禁止修改").left().wrap().labelAlign(Align.left).center().row();
            //add("原版难度已禁止修改").left().wrap().labelAlign(Align.left).center().row();
            current.table(Tex.button, t -> {
                t.margin(10f);
                t.defaults().size(180f, 50f);
                t.button("创世神难度设置", () -> new SettingDifficultyDialog(加载CT2()).show());
            }).left().fill(false).expand(false, false).row();


            check("@rules.fog", b -> rules.fog = b, () -> rules.fog);
            check("@rules.showspawns", b -> rules.showSpawns = b, () -> rules.showSpawns);
            check("@rules.randomwaveai", b -> rules.randomWaveAI = b, () -> rules.randomWaveAI);

            if(planet.allowSectorInvasion){
                check("@rules.invasions", b -> rules.sectorInvasion = b, () -> rules.sectorInvasion);
            }
            if(planet.showRtsAIRule){
                check("@rules.rtsai.campaign", b -> rules.rtsAI = b, () -> rules.rtsAI);
            }

            //TODO: this is intentionally hidden until the new mechanics have been well-tested. I don't want people immediately switching to the old mechanics
           //发射台
            if(planet.allowLegacyLaunchPads){
                check("@rules.legacylaunchpads", b -> rules.legacyLaunchPads = b, () -> rules.legacyLaunchPads);
            }

            if(planet == Vars.content.planets().find(p ->
                    p.minfo.mod == Vars.mods.locateMod("cttd")
                    || p.minfo.mod == Vars.mods.locateMod("creators")
                    || p == Planets.erekir)){
               // SettingDifficultyDialog.addChangeDiffcutySlider(cont, 加载CT2());
            }
        }).growY();
    }

    public void show(Planet planet){
        this.planet = planet;

        rebuild();
        show();
    }

    void check(String text, Boolc cons, Boolp prov){
        check(text, cons, prov, () -> true);
    }

    void check(String text, Boolc cons, Boolp prov, Boolp condition){
        String infoText = text.substring(1) + ".info";
        arc.scene.ui.layout.Cell<arc.scene.ui.CheckBox> cell = current.check(text, cons).checked(prov.get()).update(a -> a.setDisabled(!condition.get()));
        if(Core.bundle.has(infoText)){
            cell.tooltip(text + ".info");
        }
        cell.get().left();
        current.row();
    }

}
