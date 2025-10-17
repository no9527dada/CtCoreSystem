package CtCoreSystem.content.Effect;

import arc.graphics.Color;
import arc.math.Interp;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.effect.WaveEffect;

public class NewEffect {



    public static class BulletParticleEffect extends ParticleEffect {
        public BulletParticleEffect() {
            region = "circle";
            colorFrom = Color.white.cpy();
            colorTo = Color.white.cpy();
            /*粒子数量*/
            particles = 6;
            //角度
            cone = 180f;
            //长度
            length = 20f;
            //什么玩意的长度
            baseLength = 0f;
            //粒径/长度/半径插值？？？？
            interp = Interp.linear;
            //旋转偏移？
            offset = 0;
            //不知道
            offsetX = 0;
            //不知道
            offsetY = 0;
            //光的什么东西不知道
            lightScl = 2f;
            //光的不透明度
            lightOpacity = 0.6f;
            //光的颜色
            lightColor = null;
            //旋转速度
            spin = 0f;

            //控制初始和最终 形状的 大小。开始
            sizeFrom = 2f;
            //控制初始和最终 形状的 大小。终止
            sizeTo = 0f;
            //横面/宽度 的起始值
            strokeFrom = 2f;
            //横面/宽度 的结束值
            strokeTo = 0f;
            //纵向/长度 的起始值
            lenFrom = 4f;
            //纵向/长度 的结束值
            lenTo = 2f;

            //持续时间
            lifetime = 50;
            //不知道
            line = false;
            cap = true;
            //旋转是否与父项一起
            useRotation = true;
            //不知道
            randLength = true;
            //提供特效翻转兼容性，如大小写效果。不太懂
            casingFlip = true;
            //不知道
            sizeInterp = null;
        }
    }

    public static class BulletWaveEffect extends WaveEffect {
        public BulletWaveEffect() {
            colorFrom = Color.white.cpy();
            colorTo = Color.white.cpy();
            lightColor = null;
            lightInterp = Interp.reverse;
            //持续时间
            lifetime = 50;
            lightScl = 3f;
            lightOpacity = 0.8f;
            sizeFrom = 0f;
            sizeTo = 100f;
            sides = -1;
            rotation = 0f;
            strokeFrom = 2f;
            strokeTo = 0f;
            interp = Interp.linear;
            offsetX = 0;
            offsetY = 0;


        }
    }
}
