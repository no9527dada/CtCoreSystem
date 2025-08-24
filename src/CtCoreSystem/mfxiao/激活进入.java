package CtCoreSystem.mfxiao;

import arc.util.Align;
import mindustry.ui.dialogs.BaseDialog;

public class 激活进入 {
    public static BaseDialog 激活进入dialog;
    public static void show() {
        激活进入dialog = new BaseDialog("") {{
            addCloseListener();//按esc关闭
            buttons.defaults().size(210, 64);
            buttons.button("@close", (this::hide)).size(100, 64);//关闭按钮
            if (!ActivateProgram.isActivated) {
                ActivateProgram.showActivationDialog("@activation.title");
            } else {
                cont.add("您已经激活，无需重复激活").center().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.center);
            }
        }};
        激活进入dialog.show();
    }
}
