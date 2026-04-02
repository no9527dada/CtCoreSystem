package CtCoreSystem.CoreSystem.type.VXV;
import arc.graphics.Color;
import arc.util.Align;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

/**
 * 轻量级动画描述对话框工具类
 * 不依赖 StatGraphics，完全独立
 */
public class SimpleAnimatedDialog {

    private static final BaseDialog dialog = new BaseDialog("详情");
    public static void Description(String stat){
        dialog.cont.clear();
        dialog.buttons.clear();

       // dialog.setTitle(title);
        dialog.addCloseListener();
        dialog.setStyle(Styles.fullDialog);
        dialog.cont.pane(A -> {
            A.image().color(Color.white).fillX().height(3).pad(3);
            A.row();
            A.add(new AnimatedStatLabels.StatLabelNO(stat, 0.1f)).left().growX().wrap().width(200).pad(4).labelAlign(Align.left);
            A.row();
            A.image().color(Color.white).fillX().height(3).pad(3);
        });
        dialog.buttons.button("@close", dialog::hide).size(210, 64);
        dialog.show();
    }
}
