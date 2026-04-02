package CtCoreSystem.CoreSystem.type.VXV;



import arc.Core;
import arc.flabel.FLabel;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Interp;
import arc.math.Mathf;
import arc.scene.actions.Actions;
import arc.scene.style.Drawable;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import arc.util.Scaling;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;

/**
 * 通用统计标签动画组件
 * 提供多种带有平滑动画效果的文本标签显示
 *
 * 功能特性:
 * - 缩放淡入动画
 * - 颜色渐变效果
 * - 文字延迟显示
 * - 数字滚动动画
 * - 呼吸灯效果
 * - 基础用法:
 * table.add(new AnimatedStatLabels.StatLabel("欢迎信息", 1.0f));

 * // 自定义颜色
 * table.add(new AnimatedStatLabels.StatLabel("重要提示", 2.0f, Pal.remove));

 * // 数字滚动
 * table.add(new AnimatedStatLabels.MDTStatLabel("得分：", 100, 0.5f));

 * // 无边框版本
 * table.add(new AnimatedStatLabels.StatLabelNO("说明文字", 1.5f));
 */
public class AnimatedStatLabels {

    // ==================== 可配置参数 ====================

    private static float UI窗口按钮大小 = 400f; // 根据需要调整

    /** 默认动画持续时间 (秒) */
    public static float DEFAULT_ANIMATION_DURATION = 1.0f;

    /** 默认背景样式 */
    public static Drawable DEFAULT_BACKGROUND = Tex.inventory;

    /** 默认文字样式 */
    public static Label.LabelStyle DEFAULT_LABEL_STYLE = Styles.outlineLabel;

    /** 默认高亮颜色 */
    public static Color DEFAULT_HIGHLIGHT_COLOR = Pal.accent;

    /** 默认呼吸灯颜色 (银灰色) */
    public static Color DEFAULT_BREATH_COLOR = Color.valueOf("C0C0C0");

    /** 默认呼吸灯目标颜色 (白色) */
    public static Color DEFAULT_BREATH_TARGET = Color.white;

    /** 默认呼吸灯速度 (数值越大越慢) */
    public static float DEFAULT_BREATH_SPEED = 5.0f;

    /** 数字滚动速度 (60 帧 = 1 秒) */
    public static float NUMBER_ROLL_SPEED = 60.0f;

    // ==================== EMC 统计标签 ====================


    public static class StatLabelEMC extends Table {
        /**
         * 带物品图标的 统计标签
         * @param stat 显示的文本内容
         * @param delay 动画延迟时间 (秒)
         * @param content 物品内容 (用于显示图标)
         * @param bundleKey 本地化键名
         */
        public StatLabelEMC(String stat, float delay, UnlockableContent content,  String bundleKey) {
            this.setTransform(true);
            this.setClip(true);
            this.setBackground(Tex.inventory);

            FLabel statLabel = new FLabel(
                    content.localizedName + Core.bundle.format(bundleKey,  stat)
            );
            statLabel.setStyle(DEFAULT_LABEL_STYLE);
            statLabel.setWrap(true);
            statLabel.pause();

            this.image(content.uiIcon).size(32).scaling(Scaling.fit);

            this.add(statLabel)
                    .scaling(Scaling.fit)
                    .size(UI窗口按钮大小 / 1.5f, 50)
                    .update(ccc -> {
                        ccc.setColor(Tmp.c1.set(DEFAULT_BREATH_COLOR)
                                .lerp(DEFAULT_BREATH_TARGET, Mathf.absin(DEFAULT_BREATH_SPEED, 1f)));
                    });

            this.actions(
                    Actions.scaleTo(0.0F, 1.0F),
                    Actions.delay(delay),
                    Actions.parallel(
                            Actions.scaleTo(1.0F, 1.0F, DEFAULT_ANIMATION_DURATION, Interp.pow3Out),
                            Actions.color(DEFAULT_HIGHLIGHT_COLOR, 0.3F, Interp.pow3Out),
                            Actions.sequence(
                                    Actions.delay(0.5F),
                                    Actions.run(statLabel::resume)
                            )
                    )
            );
        }

       /* //使用空窗口背景的统计标签
        public StatLabelEMC(String stat, float delay, UnlockableContent content,  String bundleKey,int h) {
            this.setTransform(true);
            this.setClip(true);
            this.setBackground(Tex.inventory);

            FLabel statLabel = new FLabel(
                    content.localizedName + Core.bundle.format(bundleKey,  stat)
            );
            statLabel.setStyle(DEFAULT_LABEL_STYLE);
            statLabel.setWrap(true);
            statLabel.pause();

            this.image(content.uiIcon).size(32).scaling(Scaling.fit);

            this.add(statLabel)
                    .scaling(Scaling.fit)
                    .size(UI窗口按钮大小 / 1.5f, 50)
                    .update(ccc -> {
                        ccc.setColor(Tmp.c1.set(DEFAULT_BREATH_COLOR)
                                .lerp(DEFAULT_BREATH_TARGET, Mathf.absin(DEFAULT_BREATH_SPEED, 1f)));
                    });

            this.actions(
                    Actions.scaleTo(0.0F, 1.0F),
                    Actions.delay(delay),
                    Actions.parallel(
                            Actions.scaleTo(1.0F, 1.0F, DEFAULT_ANIMATION_DURATION, Interp.pow3Out),
                            Actions.color(DEFAULT_HIGHLIGHT_COLOR, 0.3F, Interp.pow3Out),
                            Actions.sequence(
                                    Actions.delay(0.5F),
                                    Actions.run(statLabel::resume)
                            )
                    )
            );
        }*/
    }

    // ==================== 普通统计标签 ====================


    public static class StatLabel extends Table {
        /**
         * 基础统计标签 (使用默认颜色)
         * @param stat 显示的文本内容
         * @param delay 动画延迟时间 (秒)
         */
        public StatLabel(String stat, float delay) {
            this.setTransform(true);
            this.setClip(true);
            this.setBackground(DEFAULT_BACKGROUND);

            FLabel statLabel = new FLabel(stat);
            statLabel.setStyle(DEFAULT_LABEL_STYLE);
            statLabel.setWrap(true);
            statLabel.pause();

            this.add(statLabel).left().growX().labelAlign(Align.left);

            this.actions(
                    Actions.scaleTo(0.0F, 1.0F),
                    Actions.delay(delay),
                    Actions.parallel(
                            Actions.scaleTo(1.0F, 1.0F, DEFAULT_ANIMATION_DURATION, Interp.pow3Out),
                            Actions.color(DEFAULT_HIGHLIGHT_COLOR, 0.3F, Interp.pow3Out),
                            Actions.sequence(
                                    Actions.delay(0.5F),
                                    Actions.run(statLabel::resume)
                            )
                    )
            );
        }
        public StatLabel(String stat, float delay, Color color ,float breathSpeed) {
            this.setTransform(true);
            this.setClip(true);
            this.setBackground(DEFAULT_BACKGROUND);

            FLabel statLabel = new FLabel(stat);
            statLabel.setStyle(DEFAULT_LABEL_STYLE);
            statLabel.setWrap(true);
            statLabel.pause();

            this.add(statLabel)
                    .left()
                    .growX()
                    .labelAlign(Align.left)
                    .update(ccc -> {
                        ccc.setColor(Tmp.c1.set(DEFAULT_BREATH_COLOR)
                                .lerp(DEFAULT_BREATH_TARGET, Mathf.absin(breathSpeed, 1f)));
                    });

            this.actions(
                    Actions.scaleTo(0.0F, 1.0F),
                    Actions.delay(delay),
                    Actions.parallel(
                            Actions.scaleTo(1.0F, 1.0F, DEFAULT_ANIMATION_DURATION, Interp.pow3Out),
                            Actions.color(color, 0.3F, Interp.pow3Out),
                            Actions.sequence(
                                    Actions.delay(0.5F),
                                    Actions.run(statLabel::resume)
                            )
                    )
            );
        }
        /**
         * 带灰色背景的统计标签
         * @param stat 标签文本
         * @param delay 动画延迟时间 (秒)
         * @param breathSpeed 呼吸灯速度
         */
        public StatLabel(String stat, float delay, float breathSpeed,boolean breath) {
            this.setTransform(true);
            this.setClip(true);
            this.setBackground(Tex.whiteui);
            this.setColor(Color.gray);
            this.margin(2.0F);

            FLabel statLabel = new FLabel(stat);
            statLabel.setStyle(DEFAULT_LABEL_STYLE);
            statLabel.setWrap(true);
            statLabel.pause();

            if(breath){
                this.add(statLabel)
                        .left()
                        .growX()
                        .padLeft(5.0F)
                        .update(ccc -> {
                            ccc.setColor(Tmp.c1.set(DEFAULT_BREATH_COLOR)
                                    .lerp(DEFAULT_BREATH_TARGET, Mathf.absin(breathSpeed, 1f)));
                        });
            }else {
                this.add(statLabel)
                        .left()
                        .growX()
                        .padLeft(5.0F);
            }

            this.actions(
                    Actions.scaleTo(0.0F, 1.0F),
                    Actions.delay(delay),
                    Actions.parallel(
                            Actions.scaleTo(1.0F, 1.0F, 0.3F, Interp.pow3Out),
                            Actions.color(Pal.darkestGray, 0.3F, Interp.pow3Out),
                            Actions.sequence(
                                    Actions.delay(0.3F),
                                    Actions.run(statLabel::resume)
                            )
                    )
            );
        }

           }


    // ==================== 无边框统计标签 ====================

    public static class StatLabelNO extends Table {
        /**
         * 使用空窗口背景的统计标签
         * @param stat 显示的文本内容
         * @param delay 动画延迟时间 (秒)
         */
        public StatLabelNO(String stat, float delay) {
            this.setTransform(true);
            this.setClip(true);
            this.setBackground(Tex.windowEmpty);

            FLabel statLabel = new FLabel(stat);
            statLabel.setStyle(DEFAULT_LABEL_STYLE);
            statLabel.setWrap(true);
            statLabel.pause();

            this.add(statLabel).left().growX().labelAlign(Align.left);

            this.actions(
                    Actions.scaleTo(0.0F, 1.0F),
                    Actions.delay(delay),
                    Actions.parallel(
                            Actions.scaleTo(1.0F, 1.0F, DEFAULT_ANIMATION_DURATION, Interp.pow3Out),
                            Actions.color(DEFAULT_HIGHLIGHT_COLOR, 0.3F, Interp.pow3Out),
                            Actions.sequence(
                                    Actions.delay(0.5F),
                                    Actions.run(statLabel::resume)
                            )
                    )
            );
        }
        


        /**
         * 带物品图标的空窗口背景统计标签
         * @param stat 显示的文本内容
         * @param delay 动画延迟时间 (秒)
         * @param content 物品内容 (用于显示图标)
         * @param suffixText 后缀文本（直接显示的文本，不是 bundleKey）
         */
        public StatLabelNO(String stat, float delay, UnlockableContent content, String suffixText) {
            this.setTransform(true);
            this.setClip(true);
            this.setBackground(Tex.windowEmpty);

            FLabel statLabel = new FLabel(
                    content.localizedName + suffixText
            );
            statLabel.setStyle(DEFAULT_LABEL_STYLE);
            statLabel.setWrap(true);
            statLabel.pause();

            this.image(content.uiIcon).size(32).scaling(Scaling.fit);

            this.add(statLabel)
                    .scaling(Scaling.fit)
                    .size(UI窗口按钮大小 / 1.5f, 50)
                    .labelAlign(Align.left);

            this.actions(
                    Actions.scaleTo(0.0F, 1.0F),
                    Actions.delay(delay),
                    Actions.parallel(
                            Actions.scaleTo(1.0F, 1.0F, DEFAULT_ANIMATION_DURATION, Interp.pow3Out),
                            Actions.color(DEFAULT_HIGHLIGHT_COLOR, 0.3F, Interp.pow3Out),
                            Actions.sequence(
                                    Actions.delay(0.5F),
                                    Actions.run(statLabel::resume)
                            )
                    )
            );
        }
        /**
         * 带物品图标的空窗口背景统计标签
         * @param stat 显示的文本内容
         * @param delay 动画延迟时间 (秒)
         * @param content 物品内容 (用于显示图标)
         * @param suffixText 后缀文本（直接显示的文本，不是 bundleKey）
          * @param breathSpeed 呼吸灯速度
         */
        public StatLabelNO(String stat, float delay, UnlockableContent content, String suffixText,float breathSpeed) {
            this.setTransform(true);
            this.setClip(true);
            this.setBackground(Tex.windowEmpty);

            FLabel statLabel = new FLabel(
                    content.localizedName + suffixText
            );
            statLabel.setStyle(DEFAULT_LABEL_STYLE);
            statLabel.setWrap(true);
            statLabel.pause();

            this.image(content.uiIcon).size(32).scaling(Scaling.fit);

            this.add(statLabel)
                    .scaling(Scaling.fit)
                    .size(UI窗口按钮大小 / 1.5f, 50)
                    .update(ccc -> {
                        ccc.setColor(Tmp.c1.set(DEFAULT_BREATH_COLOR)
                                .lerp(DEFAULT_BREATH_TARGET, Mathf.absin(breathSpeed, 1f)));
                    });
            this.actions(
                    Actions.scaleTo(0.0F, 1.0F),
                    Actions.delay(delay),
                    Actions.parallel(
                            Actions.scaleTo(1.0F, 1.0F, DEFAULT_ANIMATION_DURATION, Interp.pow3Out),
                            Actions.color(DEFAULT_HIGHLIGHT_COLOR, 0.3F, Interp.pow3Out),
                            Actions.sequence(
                                    Actions.delay(0.5F),
                                    Actions.run(statLabel::resume)
                            )
                    )
            );
        }
    }

    // ==================== 数字滚动标签 ====================


    public static class MDTStatLabel extends Table {
        private float progress = 0.0F;
        /**
         * 带动画效果的数字滚动标签
         * @param stat 标签文本
         * @param value 目标数值
         * @param delay 动画延迟时间 (秒)
         */
        public MDTStatLabel(String stat, int value, float delay) {
            this.setTransform(true);
            this.setClip(true);
            this.setBackground(Tex.whiteui);
            this.setColor(Pal.accent);
            this.margin(2.0F);

            FLabel statLabel = new FLabel(stat);
            statLabel.setStyle(DEFAULT_LABEL_STYLE);
            statLabel.setWrap(true);
            statLabel.pause();

            Label valueLabel = new Label("", DEFAULT_LABEL_STYLE);
            valueLabel.setAlignment(Align.center);

            this.add(statLabel).left().growX().padLeft(5.0F);
            this.add(valueLabel).right().growX().padRight(5.0F);

            this.actions(
                    Actions.scaleTo(0.0F, 1.0F),
                    Actions.delay(delay),
                    Actions.parallel(
                            Actions.scaleTo(1.0F, 1.0F, 0.3F, Interp.pow3Out),
                            Actions.color(Pal.darkestGray, 0.3F, Interp.pow3Out),
                            Actions.sequence(
                                    Actions.delay(0.3F),
                                    Actions.run(() -> {
                                        valueLabel.update(() -> {
                                            this.progress = Math.min(1.0F, this.progress + Time.delta / NUMBER_ROLL_SPEED);

                                            // 根据数值大小选择插值方式
                                            float interpolatedProgress = value < 10
                                                    ? this.progress
                                                    : Interp.slowFast.apply(this.progress);

                                            valueLabel.setText("" + (int) Mathf.lerp(0.0F, (float)value, interpolatedProgress));
                                        });
                                        statLabel.resume();
                                    })
                            )
                    )
            );
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 设置全局默认配置
     * @param animationDuration 动画持续时间
     * @param highlightColor 高亮颜色
     * @param breathSpeed 呼吸灯速度
     */
    public static void setDefaultConfig(float animationDuration, Color highlightColor, float breathSpeed) {
        DEFAULT_ANIMATION_DURATION = animationDuration;
        DEFAULT_HIGHLIGHT_COLOR = highlightColor;
        DEFAULT_BREATH_SPEED = breathSpeed;
    }

    /**
     * 重置为默认配置
     */
    public static void resetToDefaults() {
        DEFAULT_ANIMATION_DURATION = 1.0f;
        DEFAULT_HIGHLIGHT_COLOR = Pal.accent;
        DEFAULT_BREATH_SPEED = 5.0f;
    }

}
