package CtCoreSystem.ui.dialogs;
//区块名显示

import CtCoreSystem.ui.NanDu.CTCampaignRulesDialog;
import arc.Core;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.GlyphLayout;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.Scaling;
import arc.util.pooling.Pools;
import mindustry.content.TechTree;
import mindustry.core.UI;
import mindustry.ctype.UnlockableContent;
import mindustry.game.SectorInfo;
import mindustry.game.Team;
import mindustry.gen.Icon;
import mindustry.gen.Iconc;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.ui.Fonts;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.PlanetDialog;

import static mindustry.Vars.content;
import static mindustry.Vars.iconSmall;
import static mindustry.ui.dialogs.PlanetDialog.Mode.planetLaunch;
import static mindustry.ui.dialogs.PlanetDialog.Mode.select;

public class CT3PlanetDialog extends PlanetDialog {
    private CTCampaignRulesDialog campaignRules = new CTCampaignRulesDialog();

    public static float arcDrawText(String text, float scl, float dx, float dy, int halign) {
        Font font = Fonts.outline;
        GlyphLayout layout = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
        boolean ints = font.usesIntegerPositions();
        font.setUseIntegerPositions(false);
        font.getData().setScale(scl);
        layout.setText(font, text);

        float height = layout.height;

        font.draw(text, dx + layout.width / 2, dy + layout.height / 2, halign);

        font.setUseIntegerPositions(ints);
        font.setColor(Color.white);
        font.getData().setScale(1f);
        Draw.reset();
        Pools.free(layout);

        return height;
    }


    boolean canSelect(Sector sector) {
        if (mode == select) return sector.hasBase() && launchSector != null && sector.planet == launchSector.planet;
        //cannot launch to existing sector w/ accelerator TODO test
        if (mode == planetLaunch) return sector.id == sector.planet.startSector;
        if (sector.hasBase() || sector.id == sector.planet.startSector) return true;
        //preset sectors can only be selected once unlocked
        if (sector.preset != null) {
            TechTree.TechNode node = sector.preset.techNode;
            return node == null || node.parent == null || (node.parent.content.unlocked() && (!(node.parent.content instanceof SectorPreset preset) || preset.sector.hasBase()));
        }

        return sector.planet.generator != null ?
                //use planet impl when possible
                sector.planet.generator.allowLanding(sector) :
                sector.hasBase() || sector.near().contains(Sector::hasBase); //near an occupied sector
    }

    @Override
    public void renderProjections(Planet planet) {
        float iw = 48f / 4f;

        for (Sector sec : planet.sectors) {
            if (sec != hovered) {
                var preficon = sec.icon();
                var icon =
                        sec.isAttacked() ? Fonts.getLargeIcon("warning") :
                                !sec.hasBase() && sec.preset != null && sec.preset.unlocked() && preficon == null ?
                                        Fonts.getLargeIcon("terrain") :
                                        sec.preset != null && sec.preset.locked() && sec.preset.techNode != null && !sec.preset.techNode.parent.content.locked() ? Fonts.getLargeIcon("lock") :
                                                preficon;
                var color = sec.preset != null && !sec.hasBase() ? Team.derelict.color : Team.sharded.color;

                if (icon != null) {
                    planets.drawPlane(sec, () -> {
                        //use white for content icons
                        Draw.color(preficon == icon && sec.info.contentIcon != null ? Color.white : color, state.uiAlpha);
                        Draw.rect(icon, 0, 0, iw, iw * icon.height / icon.width);
                    });
                }
                planets.drawPlane(sec, () -> {
                    if ((canSelect(sec) || sec.hasBase()) && true)
                        arcDrawText((sec.preset != null ? "" : "[#ff8c8c]") + sec.name(), 0.5f, 0, 0, 0);
                });
            }
        }

        Draw.reset();

        if (hovered != null && state.uiAlpha > 0.01f) {
            planets.drawPlane(hovered, () -> {
                Draw.color(hovered.isAttacked() ? Pal.remove : Color.white, Pal.accent, Mathf.absin(5f, 1f));
                Draw.alpha(state.uiAlpha);

                var icon = hovered.locked() && !canSelect(hovered) ? Fonts.getLargeIcon("lock") : hovered.isAttacked() ? Fonts.getLargeIcon("warning") : hovered.icon();

                if (icon != null) {
                    Draw.rect(icon, 0, 0, iw, iw * icon.height / icon.width);
                }

                Draw.reset();
            });
        }

        Draw.reset();
    }


    void displayItems(Table c, float scl, ObjectMap<Item, SectorInfo.ExportStat> stats, String name) {
        displayItems(c, scl, stats, name, t -> {
        });
    }

    void displayItems(Table c, float scl, ObjectMap<Item, SectorInfo.ExportStat> stats, String name, Cons<Table> builder) {
        Table t = new Table().left();

        int i = 0;
        for (var item : content.items()) {
            var stat = stats.get(item);
            if (stat == null) continue;
            int total = (int) (stat.mean * 60 * scl);
            if (total > 1) {
                t.image(item.uiIcon).padRight(3);
                t.add(UI.formatAmount(total) + " " + Core.bundle.get("unit.perminute")).color(Color.lightGray).padRight(3);
                if (++i % 3 == 0) {
                    t.row();
                }
            }
        }

        if (t.getChildren().any()) {
            c.defaults().left();
            c.add(name).row();
            builder.get(c);
            c.add(t).padLeft(10f).row();
        }
    }


    /*
    *显示区块物品 by Anuken
    *为什么屑猫要写成private方法呢（
    */
    public void showStats(Sector sector) {
        BaseDialog dialog = new BaseDialog(sector.name());

        dialog.cont.pane(c -> {
            c.defaults().padBottom(5);

            if (sector.preset != null && sector.preset.description != null) {
                c.add(sector.preset.displayDescription()).width(420f).wrap().left().row();
            }

            c.add(Core.bundle.get("sectors.time") + " [accent]" + sector.save.getPlayTime()).left().row();

            if (sector.info.waves && sector.hasBase()) {
                c.add(Core.bundle.get("sectors.wave") + " [accent]" + (sector.info.wave + sector.info.wavesPassed)).left().row();
            }

            if (sector.isAttacked() || !sector.hasBase()) {
                c.add(Core.bundle.get("sectors.threat") + " [accent]" + sector.displayThreat()).left().row();
            }

            if (sector.save != null && sector.info.resources.any()) {
                c.add("@sectors.resources").left().row();
                c.table(t -> {
                    for (UnlockableContent uc : sector.info.resources) {
                        if (uc == null) continue;
                        t.image(uc.uiIcon).scaling(Scaling.fit).padRight(3).size(iconSmall);
                    }
                }).padLeft(10f).left().row();
            }

            //production
            displayItems(c, sector.getProductionScale(), sector.info.production, "@sectors.production");

            //export
            displayItems(c, sector.getProductionScale(), sector.info.export, "@sectors.export", t -> {
                if (sector.info.destination != null && sector.info.destination.hasBase()) {
                    String ic = sector.info.destination.iconChar();
                    t.add(Iconc.rightOpen + " " + (ic == null || ic.isEmpty() ? "" : ic + " ") + sector.info.destination.name()).padLeft(10f).row();
                }
            });

            //import
            if (sector.hasBase()) {
                displayItems(c, 1f, sector.info.importStats(sector.planet), "@sectors.import", t -> {
                    sector.info.eachImport(sector.planet, other -> {
                        String ic = other.iconChar();
                        t.add(Iconc.rightOpen + " " + (ic == null || ic.isEmpty() ? "" : ic + " ") + other.name()).padLeft(10f).row();
                    });
                });
            }

            ItemSeq items = sector.items();

            //stored resources
            if (sector.hasBase() && items.total > 0) {

                c.add("@sectors.stored").left().row();
                c.table(t -> {
                    t.left();

                    t.table(res -> {

                        int i = 0;
                        for (ItemStack stack : items) {
                            res.image(stack.item.uiIcon).padRight(3);
                            res.add(UI.formatAmount(Math.max(stack.amount, 0))).color(Color.lightGray);
                            if (++i % 4 == 0) {
                                res.row();
                            }
                        }
                    }).padLeft(10f);
                }).left().row();
            }
        });

        dialog.addCloseButton();

        if (sector.hasBase()) {
            dialog.buttons.button("@sector.abandon", Icon.cancel, () -> abandonSectorConfirm(sector, dialog::hide));
        }

        dialog.show();
    }

}
