package CtCoreSystem.CoreSystem.type.No9527;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import mindustry.entities.abilities.ForceFieldAbility;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;

import static CtCoreSystem.CtCoreSystem.toText;


// 自定义延迟恢复护盾能力类
public class DelayedForceFieldAbility extends ForceFieldAbility {
    // 上次受到伤害的时间
    private float lastDamageTime = 0f;
    // 恢复延迟时间（5秒 = 300帧）
    public float RECOVERY_DELAY = cooldown;

    // 构造函数，保持与父类一致的参数
    public DelayedForceFieldAbility(float radius, float regen, float max, float cooldown) {
        super(radius, regen, max, cooldown);
    }
    // UI 显示名称
    public String localizedName = toText("ability-delayed-force-field");

    /*    // 构造函数，允许自定义恢复延迟时间
        public DelayedForceFieldAbility(float radius, float regen, float max,  float delay) {
            super(radius, regen, max, delay);
            RECOVERY_DELAY = delay;
        }*/

    @Override
    public void update(Unit unit) {
        // 检查护盾是否被破坏（被攻击）
        if (unit.shield <= 0f && !wasBroken) {
            // 记录被破坏的时间
            lastDamageTime = Time.time;
            unit.shield -= cooldown * regen;
        }

        wasBroken = unit.shield <= 0f;

        // 计算距离上次受到伤害的时间
        float timeSinceLastDamage = Time.time - lastDamageTime;

        // 只有当护盾未满且距离上次受到伤害超过恢复延迟时间时，才恢复护盾
        if (unit.shield < max && timeSinceLastDamage > RECOVERY_DELAY) {
            unit.shield += Time.delta * regen;
        }

        // 调用父类的update方法来处理其他逻辑，但不包括护盾恢复部分
        // 我们需要确保父类不会覆盖我们的恢复逻辑
        // 注意：为了避免访问私有变量，这里的实现方式做了调整
        alpha = Math.max(alpha - Time.delta/10f, 0f);

        if(unit.shield > 0){
            radiusScale = Mathf.lerpDelta(radiusScale, 1f, 0.06f);
            // 不直接访问paramUnit和paramField，因为它们是私有的

            // 直接调用父类的checkRadius方法
            checkRadius(unit);

            // 计算真实半径（不直接访问realRad私有变量）
            float calculatedRealRad = radius * radiusScale;

            // 自己实现子弹碰撞检测，使用圆形碰撞检测代替矩形检测
            Groups.bullet.intersect(unit.x - calculatedRealRad, unit.y - calculatedRealRad,
                    calculatedRealRad * 2f, calculatedRealRad * 2f,
                    bullet -> {
                        // 使用圆形碰撞检测公式：(x1-x2)^2 + (y1-y2)^2 <= r^2
                        if(bullet.team != unit.team && bullet.type.absorbable) {
                            float dx = bullet.x - unit.x;
                            float dy = bullet.y - unit.y;
                            // 使用平方比较来提高性能，避免开根号
                            if(dx * dx + dy * dy <= calculatedRealRad * calculatedRealRad) {
                                // 消耗子弹能量，减少护盾值
                                float damage = bullet.damage;
                                unit.shield -= damage;
                                lastDamageTime = Time.time; // 受到攻击，更新时间

                                if(unit.shield < 0) {
                                    // 护盾被击破，处理剩余伤害
                                    float excessDamage = -unit.shield;
                                    unit.damage(excessDamage);
                                    unit.shield = 0f;
                                }

                                // 移除子弹
                                bullet.absorb();
                            }
                        }
                    });
        }else{
            radiusScale = 0f;
        }
    }

    //现在我需要在单位的上方显示一个长条矩形，用于动态显示护盾值的恢复量的进度
    // 重写draw方法，确保护盾绘制范围与实际碰撞范围一致
    @Override
    public void draw(Unit unit) {
        // 计算护盾当前值相对于最大值的比例 (类似血条效果)
        float shieldPercent = Mathf.clamp(unit.shield / max);

        // 绘制护盾值进度条 (始终显示，类似血条效果)
        // 设置进度条位置 (单位上方)
        float barWidth = 40f;
        float barHeight = 5f;
        float barX = unit.x - barWidth / 2;
        float barY = unit.y + unit.type.hitSize + 10f; // 在单位上方

        // 设置绘制层级
        Draw.z(mindustry.graphics.Layer.overlayUI);

        // 绘制进度条背景
        Draw.color(Color.gray);
        Fill.rect(barX, barY, barWidth, barHeight);

        // 绘制进度条填充 (从左到右，基于护盾当前值)
        if (shieldPercent > 0) {
            Draw.color(Color.valueOf("ffb32c")); // 使用与护盾相同的金色
            // 从左到右填充：从进度条最左侧开始
            Fill.rect(barX - barWidth / 2 + (barWidth * shieldPercent) / 2, barY, barWidth * shieldPercent, barHeight);
        }

        // 绘制进度条边框
        Draw.color(Color.white);
        Lines.stroke(1f);
        Lines.rect(barX - barWidth / 2, barY - barHeight / 2, barWidth, barHeight);

        // 重置绘制状态
        Draw.reset();

        // 原始护盾绘制逻辑
        if (unit.shield > 0) {
            // 使用与碰撞检测相同的方式计算护盾半径
            float calculatedRealRad = radius * radiusScale;

            // 设置绘制颜色和透明度
            Draw.z(mindustry.graphics.Layer.shields);
            Draw.color(unit.type.shieldColor(unit), Color.valueOf("ffb32c"), Mathf.clamp(alpha));

            // 绘制护盾圆环
            Draw.alpha(0.5f);
            Fill.circle(unit.x, unit.y, calculatedRealRad - 5);

            // 绘制护盾边缘
            Draw.alpha(1f);//设置透明度
            Lines.stroke(1.5f); // 设置线条绘制时的宽度
            Lines.circle(unit.x, unit.y, calculatedRealRad);

            // 重置绘制状态
            Draw.reset();
        }

    }
    }
