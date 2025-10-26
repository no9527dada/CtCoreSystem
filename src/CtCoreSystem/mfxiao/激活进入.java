package CtCoreSystem.mfxiao;

import arc.Core;
import arc.graphics.Color;
import arc.scene.Element;
import arc.scene.ui.Label;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Cell;
import arc.util.Align;
import arc.util.Log;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.SettingsMenuDialog;

import static CtCoreSystem.CtCoreSystem.showCustomDialog;
import static CtCoreSystem.CtCoreSystem.主动关闭激活;

public class 激活进入 {
    public static BaseDialog 激活进入dialog;


    public static void show() {
        激活进入dialog = new BaseDialog("") {{
            addCloseListener();//按esc关闭
            buttons.defaults().size(210, 64);
            buttons.button("@close", (this::hide)).size(100, 64);//关闭按钮
            if (!ActivateProgram.isActivated) {
                ActivateProgram.showActivationDialog("");
                主动关闭激活 = false;
            } else {
                SettingsMenuDialog.SettingsTable st= new SettingsMenuDialog.SettingsTable(){
                    final Cell empty = new Cell<>();
                    @Override
                    public <T extends Element> Cell add(T element) {
                        if(element.getClass() == TextButton.class){
                            return empty;
                        }
                        return super.add(element);
                    }
                };
                st.checkPref("主动关闭激活", false, e -> {
                    主动关闭激活 = !主动关闭激活;
                    Core.settings.put("主动关闭激活", 主动关闭激活);
                    showCustomDialog("", cont -> {
                        cont.margin(15);
                    });
                });
                st.row();
                st.add(Core.bundle.format("ct3-hindActivated")).visible(() -> 主动关闭激活).row();
                st.image().color(Color.valueOf("69dcee")).fillX().height(3).pad(3).row();
                st.add(Core.bundle.format("ct3-hindhindActivatedTXT")).left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);
                //移除 rebuild() 方法调用

                cont.add("您已经激活，无需重复激活").center().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.center).row();
                cont.add(st);
                Log.info("主动关闭激活 " + 主动关闭激活);

            }
        }};
        激活进入dialog.show();
    }
}
