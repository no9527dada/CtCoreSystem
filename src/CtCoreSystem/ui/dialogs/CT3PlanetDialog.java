package CtCoreSystem.ui.dialogs;

import arc.Core;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.GlyphLayout;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.Align;
import arc.util.Reflect;
import arc.util.Scaling;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.content.TechTree;
import mindustry.core.UI;
import mindustry.ctype.UnlockableContent;
import mindustry.game.SectorInfo;
import mindustry.game.Team;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.ui.Fonts;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.PlanetDialog;

import java.util.Iterator;
import java.util.Objects;

import static arc.Core.settings;
import static mindustry.Vars.iconSmall;
import static mindustry.ui.dialogs.PlanetDialog.Mode.planetLaunch;
import static mindustry.ui.dialogs.PlanetDialog.Mode.select;

public class CT3PlanetDialog extends PlanetDialog {

    /**
     * 绘制文字的辅助方法，用于确保区块名完整显示
     */
    private static float arcDrawText(String text, float scl, float dx, float dy, int halign) {
        Font font = Fonts.outline;
        GlyphLayout layout = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
        boolean ints = font.usesIntegerPositions();
        font.setUseIntegerPositions(false);
        font.getData().setScale(scl);
        layout.setText(font, text);

        font.draw(text, dx + layout.width / 2, dy + layout.height / 2, halign);

        font.setUseIntegerPositions(ints);
        font.setColor(Color.white);
        font.getData().setScale(1f);
        Draw.reset();
        Pools.free(layout);

        return layout.height;
    }

    /**
     * 重写父类方法，实现区块名全显示功能，文字大小随界面缩放变化
     */
    @Override
    public void renderProjections(Planet planet) {
        float iw = 48f / 4f;

        // 获取当前界面的缩放值，使用state.zoom或zoom均可
        // state.zoom是平滑过渡后的缩放值，zoom是原始缩放值
        float currentZoom = state.zoom;

      // // 基础文字缩放因子，可根据需要调整
       float baseTextScale = 0.45f;
      // // 计算实际文字缩放因子，与界面缩放成比例
      // float actualTextScale =   Mathf.clamp(baseTextScale * currentZoom, 0.3f, 1.5f);

        // 计算实际文字缩放因子，与界面缩放成反比
        float actualTextScale = Mathf.clamp(baseTextScale / currentZoom, 1f, 1f);

        // 绘制所有非选中区块的图标和名称
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

                // 绘制图标
                if (icon != null) {
                    planets.drawPlane(sec, () -> {
                        //use white for content icons
                        Draw.color(preficon == icon && sec.info.contentIcon != null ? Color.white : color, state.uiAlpha);
                        Draw.rect(icon, 0, 0, iw, iw * icon.height / icon.width);
                    });
                }

                // 绘制区块名，使用实际文字缩放因子
                planets.drawPlane(sec, () -> {
                    if ((canSelect(sec) || sec.hasBase()))
                        arcDrawText((sec.preset != null ? "" : "[#ff8c8c]") + sec.name(), baseTextScale, 0, 0, 0);
                });
            }
        }

        Draw.reset();

        // 绘制选中区块的高亮效果
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

    /**
     * 重写父类方法，添加public访问修饰符，使外部包可以访问
     */
    public boolean canSelect(Sector sector) {
        if (this.mode == select) {
            return sector.hasBase() && this.launchSector != null && sector.planet == this.launchSector.planet;
        } else if (this.mode == planetLaunch && sector.hasBase()) {
            return false;
        } else if (sector.planet.generator == null) {
            return false;
        } else if (!sector.hasBase() && sector.id != sector.planet.startSector) {
            if (sector.preset != null && sector.preset.requireUnlock) {
                TechTree.TechNode node = sector.preset.techNode;
                boolean var10000;
                if (!sector.preset.unlocked() && node != null && node.parent != null) {
                    label89: {
                        if (node.parent.content.unlocked()) {
                            UnlockableContent var4 = node.parent.content;
                            if (!(var4 instanceof SectorPreset)) {
                                break label89;
                            }

                            SectorPreset preset = (SectorPreset)var4;
                            if (preset.sector.hasBase()) {
                                break label89;
                            }
                        }

                        var10000 = false;
                        return var10000;
                    }
                }

                var10000 = true;
                return var10000;
            } else {
                return this.mode == planetLaunch ? sector.planet.generator.allowAcceleratorLanding(sector) : sector.planet.generator.allowLanding(sector);
            }
        } else {
            return true;
        }
    }
    void displayItems(Table c, ObjectMap<Item, SectorInfo.ExportStat> stats, String name) {
        this.displayItems(c, stats, name, (t) -> {
        });
    }

    void displayItems(Table c, ObjectMap<Item, SectorInfo.ExportStat> stats, String name, Cons<Table> builder) {
        Table t = (new Table()).left();
        int i = 0;
        Iterator var7 = Vars.content.items().iterator();

        while(var7.hasNext()) {
            Item item = (Item)var7.next();
            SectorInfo.ExportStat stat = (SectorInfo.ExportStat)stats.get(item);
            if (stat != null) {
                int total = (int)(stat.mean * 60.0F);
                if (total > 1) {
                    t.image(item.uiIcon).padRight(3.0F);
                    t.add(UI.formatAmount((long)total) + " " + Core.bundle.get("unit.perminute")).color(Color.lightGray).padRight(3.0F);
                    ++i;
                    if (i % 3 == 0) {
                        t.row();
                    }
                }
            }
        }

      if (t.getChildren().any()) {
            c.defaults().left();
            c.add(name).row();
            builder.get(c);
            c.add(t).padLeft(10.0F).row();
        }



    }
    public  void showStats(Sector sector) {
        BaseDialog dialog = new BaseDialog(sector.name());
        dialog.cont.pane((c) -> {
            c.defaults().padBottom(5.0F);
            if (sector.preset != null && sector.preset.description != null) {
                c.add(sector.preset.displayDescription()).width(420.0F).wrap().left().row();
            }

            c.add(Core.bundle.get("sectors.time") + " [accent]" + sector.save.getPlayTime()).left().row();
            if (sector.info.attempts > 0) {
                c.add(Core.bundle.get("sectors.attempts") + " [accent]" + sector.info.attempts).left().row();
            }

            if (sector.info.waves && sector.hasBase()) {
                c.add(Core.bundle.get("sectors.wave") + " [accent]" + sector.info.wave).left().row();
            }

            if (sector.isAttacked() || !sector.hasBase()) {
                c.add(Core.bundle.get("sectors.threat") + " [accent]" + sector.displayThreat()).left().row();
            }

            if (sector.save != null && sector.info.resources.any()) {
               c.add("@sectors.resources").left().row();
                c.table((t) -> {
                    Iterator var2 = sector.info.resources.iterator();

                    while(var2.hasNext()) {
                        UnlockableContent uc = (UnlockableContent)var2.next();
                        if (uc != null) {
                            t.image(uc.uiIcon).scaling(Scaling.fit).padRight(3.0F).size(24.0F);
                        }
                    }

                }).padLeft(10.0F).left().row();
            }

            boolean lockdown = sector.isAttacked() && !sector.isBeingPlayed();
            if (lockdown) {
                c.add(UI.formatIcons(Core.bundle.get("sector.lockdown"))).wrap().fillX().padBottom(10.0F).row();
            }

            this.displayItems(c, sector.info.production, "@sectors.production");
            this.displayItems(c, sector.info.export, "@sectors.export", (t) -> {
                if (sector.info.destination != null && sector.info.destination.hasBase()) {
                    String ic = sector.info.destination.iconChar();
                    t.add("\ue83a " + (ic != null && !ic.isEmpty() ? ic + " " : "") + sector.info.destination.name()).padLeft(10.0F).row();
                }

            });
            if (sector.hasBase()) {
                this.displayItems(c,  sector.info.importStats(sector.planet), "@sectors.import", (t) -> {
                    sector.info.eachImport(sector.planet, (other) -> {
                        String ic = other.iconChar();
                        t.add("\ue83a " + (ic != null && !ic.isEmpty() ? ic + " " : "") + other.name()).padLeft(10.0F).row();
                    });
                });
            }

            ItemSeq items = sector.items();
            if (sector.hasBase() && items.total > 0) {
                c.add("@sectors.stored").left().row();
                c.table((t) -> {
                    t.left();
                    t.table((res) -> {
                        int i = 0;
                        Iterator var3 = items.iterator();

                        while(var3.hasNext()) {
                            ItemStack stack = (ItemStack)var3.next();
                            res.image(stack.item.uiIcon).padRight(3.0F);
                            res.add(UI.formatAmount((long)Math.max(stack.amount, 0))).color(Color.lightGray);
                            ++i;
                            if (i % 4 == 0) {
                                res.row();
                            }
                        }

                    }).padLeft(10.0F);
                }).left().row();
            }

            //计算时间按钮  资源结算倒计时 资源接收倒计时 后台结算倒计时
            c.image().color(Color.valueOf("69dcee")).fillX().height(3).pad(9);
            c.row();
            c.add(Core.bundle.format("locloseAnAccounve")).left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);
            c.row();
            c.button("", () -> {
            }).size(300, 64).update(buttonn -> {
                Object turnCounter = Reflect.get(Vars.universe, "turnCounter");
                float ticks = Vars.turnDuration - ((Number) turnCounter).floatValue();
                buttonn.setText(Core.bundle.get("jiesuan") + UI.formatTime(ticks));
            }).center().row();
        });
        dialog.addCloseButton();
        if (sector.hasBase()) {
            dialog.buttons.button("@sector.abandon", Icon.cancel, () -> {
                Objects.requireNonNull(dialog);
                this.abandonSectorConfirm(sector, dialog::hide);
            });
        }

        dialog.show();
    }
}