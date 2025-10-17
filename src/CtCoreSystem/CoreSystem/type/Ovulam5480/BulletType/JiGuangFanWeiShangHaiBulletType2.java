package CtCoreSystem.CoreSystem.type.Ovulam5480.BulletType;

import arc.graphics.g2d.Draw;
import arc.util.Time;
import mindustry.entities.bullet.PointLaserBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import arc.math.Mathf;
import arc.math.Angles;

import static mindustry.Vars.headless;
// 激光范围伤害 新增：射程限制参数
public class JiGuangFanWeiShangHaiBulletType2 extends PointLaserBulletType {
    // 新增：射程限制参数
    public float maxRange = -1f; // -1表示无限制

    public JiGuangFanWeiShangHaiBulletType2() {
        this.splashDamage = 30.0F;
        this.splashDamageRadius = 30.0F;
    }

    @Override
    public void update(Bullet b) {
        // 检查射程限制
        if (maxRange > 0) {
            float distance = Mathf.dst(b.x, b.y, b.aimX, b.aimY);
            if (distance > maxRange) {
                // 如果超出射程，计算射程内的目标点
                float angle = b.angleTo(b.aimX, b.aimY);
                float limitedAimX = b.x + Angles.trnsx(angle, maxRange);
                float limitedAimY = b.y + Angles.trnsy(angle, maxRange);

                // 在射程内的点创建范围伤害
                if (b.timer.get(0, this.damageInterval)) {
                    this.createSplashDamage(b, limitedAimX, limitedAimY);
                    this.despawnEffect.at(b.x, b.y, b.rotation(), this.hitColor);
                }

                // 存储射程状态供draw方法使用
                b.fdata = maxRange;

                // 更新轨迹到射程内的点
                if (!headless && trailLength > 0 && b.trail != null) {
                    b.trail.update(limitedAimX, limitedAimY, b.fslope() * (1f - (trailSinMag > 0 ? Mathf.absin(Time.time, trailSinScl, trailSinMag) : 0f)));
                }

                // 调用父类update方法但不执行伤害逻辑（已在射程内处理）
                super.update(b);
                return;
            }
        }

        // 原有逻辑，在射程内时执行
        if (b.timer.get(0, this.damageInterval)) {
            this.createSplashDamage(b, b.aimX, b.aimY);
            this.despawnEffect.at(b.x, b.y, b.rotation(), this.hitColor);
        }

        super.update(b);
    }

    @Override
    public void draw(Bullet b) {
        super.draw(b);

        // 处理射程限制的绘制
        if (maxRange > 0 && b.fdata == maxRange) {
            // 如果已经应用了射程限制，重新绘制正确长度的激光
            Draw.color(color);
            float angle = b.angleTo(b.aimX, b.aimY);
            float limitedAimX = b.x + Angles.trnsx(angle, maxRange);
            float limitedAimY = b.y + Angles.trnsy(angle, maxRange);

            Drawf.laser(laser, laserEnd, b.x, b.y, limitedAimX, limitedAimY,
                    b.fslope() * (1f - oscMag + Mathf.absin(Time.time, oscScl, oscMag)));

            Draw.reset();
        }
    }

    // 重写continuousDamage方法，确保伤害计算考虑射程限制
    @Override
    public float continuousDamage() {
        // 基础伤害计算与父类相同，但添加了范围伤害的贡献
        float baseDamage = super.continuousDamage();
        // 计算范围伤害的每秒贡献
        float splashContribution = (splashDamage / damageInterval) * 60f;
        return baseDamage + splashContribution;
    }
}