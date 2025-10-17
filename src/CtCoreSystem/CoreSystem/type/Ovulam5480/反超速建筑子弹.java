package CtCoreSystem.CoreSystem.type.Ovulam5480;
import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.entities.bullet.EmpBulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;

public class 反超速建筑子弹 {
    // 自定义减速EMP子弹类型
    public static class SlowEmpBulletType extends EmpBulletType {
        // 减速倍数
        public float 值 = 2f;
        // 减速持续时间
        public float slowDuration = 60f;

        private float slowMultiplier = 1f / 值;

        public SlowEmpBulletType(float damage) {
            super();
            // 初始化子弹基本属性
            this.speed = 4f;
            this.lifetime = 60f;
            this.shootEffect = Fx.none;
            this.smokeEffect = Fx.none;
            this.hitColor = Color.valueOf("6666ff");
            this.lightning = 3;
            this.lightningLength = 6;
            this.lightningDamage = damage / 2;
        }

        @Override
        public void hitTile(Bullet b, Building other, float x, float y, float initialDamage, boolean direct) {
            // 检查是否为敌方建筑
            if(other != null && other.team != b.team) {
                // 获取当前时间缩放
                float currentTimeScale = other.timeScale();

                // 只有当当前时间缩放比减速倍数大时才应用减速（避免多重减速叠加）
                if(currentTimeScale > slowMultiplier) {
                    // 使用applyBoost方法实现减速，传入小于1的值
                    other.applyBoost(slowMultiplier, slowDuration);

                    // 播放效果
                    if(chainEffect != null) {
                        chainEffect.at(x, y, 0, hitColor, other);
                    }
                    if(applyEffect != null) {
                        applyEffect.at(other, other.block.size * 7f);
                    }
                }
            }

            // 调用父类方法处理其他击中逻辑
            super.hitTile(b, other, x, y, initialDamage, direct);
        }
    }
}

