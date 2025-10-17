package CtCoreSystem.content.Effect;

import CtCoreSystem.content.NewColor;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.struct.FloatSeq;
import CtCoreSystem.CoreSystem.type.LYBF.LogicFxInit;
import mindustry.entities.Effect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.part.HaloPart;
import mindustry.entities.part.ShapePart;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.logic.LogicFx;
import mindustry.world.draw.DrawTurret;

import static CtCoreSystem.content.Effect.CT3FxEffect.*;
import static CtCoreSystem.content.NewColor.*;
import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.lineAngle;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.*;
import static mindustry.entities.part.DrawPart.PartProgress.reload;
import static mindustry.entities.part.DrawPart.PartProgress.warmup;

public class NewFx {

        /*
Lines.circle(float x, float y, float radius) - 绘制一个普通的空心圆圈
Fill.circle(float x, float y, float radius) - 绘制一个填充的圆圈
 Lines.stroke(1.5f); // 设置线条绘制时的宽度
 Draw.alpha(1f);//设置透明度
 Lines.lineAngle(x, y, angle, length); // 绘制一条从(x, y)开始，角度为angle，长度为length的线
 Lines.stroke(1.5f); // 设置线条绘制时的宽度
 Lines.line(x1, y1, x2, y2); // 绘制一条从(x1, y1)到(x2, y2)的线

Lines.rect(float x, float y, float width, float height, float rotation) - 绘制一个普通的空心矩形
Fill.rect(float x, float y, float width, float height, float rotation) - 绘制一个填充的矩形
 Draw.rect(x, y, width, height, rotation); // 绘制一个有贴图的效果
    */


    public static int 数量 = 6;
    public static float 特效粒子的范围 = 120f;
    public static float 特效粒子外径 = 120;
    //虽然规定内外径，但是并没有内径必须比外径小的必要
    public static float 特效粒子内径 = 12;
    //这里填X角星,五角星就填5，四角星就填4
    public static int X角星 = 16;

    public static FloatSeq 星星图形(float x, float y, float out, float in, int side, float rotation) {
        FloatSeq floatSeq = new FloatSeq(side * 4 + 4);
        floatSeq.add(x, y);

        for (int i = 0; i < side; i++) {
            float pointRotation = rotation + 360f / side * i;
            floatSeq.add((float) (x + out * Math.cos(Mathf.degreesToRadians * pointRotation)),
                    y + (float) (out * Math.sin(Mathf.degreesToRadians * pointRotation)));
            floatSeq.add((float) (x + in * Math.cos(Mathf.degreesToRadians * (pointRotation + 180f / side))),
                    y + (float) (in * Math.sin(Mathf.degreesToRadians * (pointRotation + 180f / side))));
        }

        floatSeq.add((float) (x + out * Math.cos(Mathf.degreesToRadians * rotation)),
                y + (float) (out * Math.sin(Mathf.degreesToRadians * rotation)));

        return floatSeq;
    }

    public static Effect 四角星(int 数量, float 特效粒子的范围, float 特效粒子外径, float 特效粒子内径, int X角星) {
        return new Effect(120, e -> {
            Draw.color(Color.white, Color.pink, e.fin());
            // color(Color.white, e.color, e.fin());
            randLenVectors(e.id, 数量, 特效粒子的范围, (x, y) -> {
                float angle = Mathf.angle(x, y);
                Fill.poly(星星图形(e.finpow() * x + e.x, e.finpow() * y + e.y,
                        特效粒子外径 * (1 - Mathf.sqr(1 - e.fslope())),
                        特效粒子内径 * (1 - Mathf.sqr(1 - e.fslope())), X角星, angle));
            });
        });
    }

    public static Effect 火焰(int lifetime, int clipsize, int 数量) {
        return new Effect(lifetime, clipsize, e -> {
            color(Pal.lightFlame, Pal.darkFlame, Color.gray, e.fin());
            randLenVectors(e.id, 数量, e.finpow() * 60f, e.rotation, 10, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 0.65f + e.fout() * 1.5f);
            });
        });
    }

    /*////////////////////////////////////*/
    public static Effect 制裁子弹消失, 灭亡子弹消失;
    public static Effect 拖尾, 拖尾a,拖尾b,拖尾圈;

    public static void load() {
        拖尾 = new Effect(50, e -> {
            Draw.color(Color.valueOf("ab83f6ff"), e.color, e.fin());
            Lines.stroke(e.fout() * 1 + 0.5f);
            Angles.randLenVectors(e.id, 10, 100 * e.fin(), e.rotation, 5, (x, y) -> {
                Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 5 + 0.5f);

            });
            Draw.color(Color.valueOf("6dd8fe"), Color.valueOf("ab83f6ff"), e.fin());
            Angles.randLenVectors(e.id, 5, 3.5f + e.fin() * 7, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 0.1f + e.fout() * 1.4f);
            });
        });
        拖尾a = new Effect(50, e -> {
            Draw.color(Color.valueOf("6dd8fe"), Color.valueOf("ab83f6ff"), e.fin());
            Angles.randLenVectors(e.id, 5, 3.5f + e.fin() * 7, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 0.1f + e.fout() * 1.4f);
            });
        });
        拖尾b = new Effect(50, e -> {
            Draw.color(Color.valueOf("ab83f6ff"), e.color, e.fin());
            Lines.stroke(e.fout() * 1 + 0.5f);
            Angles.randLenVectors(e.id, 10, 100 * e.fin(), e.rotation, 5, (x, y) -> {
                Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 5 + 0.5f);

            });

        });
        拖尾圈 = new Effect(90f, 30f, e -> {
            color(e.color);
            stroke(e.fout() * 2f);
            float circleRad = 4f + e.finpow() * 10f;
            Lines.circle(e.x, e.y, circleRad);

        });
        制裁子弹消失 = new Effect(14, e -> {
            color(Color.white, Pal.lightOrange, e.fin());

            e.scaled(7f, s -> {
                stroke(0.5f + s.fout());
                Lines.circle(e.x, e.y, s.fin() * 15f);
            });
            color(制裁Bullet颜色);
            randLenVectors(e.id, 5, 2f + 23f * e.finpow(), (x, y) -> {
                Fill.circle(e.x + x, e.y + y, e.fout() * 4f + 0.5f);
            });
            stroke(0.5f + e.fout() * 1.5f);

            Drawf.light(e.x, e.y, 40f, Pal.lightOrange, 0.6f * e.fout());
        });
        灭亡子弹消失 = new Effect(14, e -> {
            color(Color.white, Pal.lightOrange, e.fin());

            e.scaled(7f, s -> {
                stroke(0.5f + s.fout());
                Lines.circle(e.x, e.y, s.fin() * 15f);
            });
            color(灭亡Bullet颜色);
            randLenVectors(e.id, 5, 2f + 23f * e.finpow(), (x, y) -> {
                Fill.circle(e.x + x, e.y + y, e.fout() * 4f + 0.5f);
            });
            stroke(0.5f + e.fout() * 1.5f);

            Drawf.light(e.x, e.y, 40f, Pal.lightOrange, 0.6f * e.fout());
        });


    }

    public static void init() {
        //
        //世处注入特效
        // 在任意位置显示字母A效果
       ;
     //   LogicFxInit.injectEffect("D13", new LogicFx.EffectEntry(D13).color().size());
        LogicFxInit.injectEffect("D1", new LogicFx.EffectEntry(D1).color().size());
        LogicFxInit.injectEffect("D2", new LogicFx.EffectEntry(D2).color().size());
        LogicFxInit.injectEffect("D3", new LogicFx.EffectEntry(D3).color().size());
        LogicFxInit.injectEffect("D4", new LogicFx.EffectEntry(D4).color().size());
        LogicFxInit.injectEffect("D5", new LogicFx.EffectEntry(D5));
        LogicFxInit.injectEffect("D6", new LogicFx.EffectEntry(D6));
        LogicFxInit.injectEffect("D7", new LogicFx.EffectEntry(D7));
        LogicFxInit.injectEffect("D8", new LogicFx.EffectEntry(D8).color().size());
        LogicFxInit.injectEffect("D9", new LogicFx.EffectEntry(D9).color().size());
        LogicFxInit.injectEffect("D10", new LogicFx.EffectEntry(D10));
        LogicFxInit.injectEffect("D11", new LogicFx.EffectEntry(D11).color().size());
        LogicFxInit.injectEffect("D12", new LogicFx.EffectEntry(D12).color().size());
        LogicFxInit.injectEffect("D12-2", new LogicFx.EffectEntry(TriangleEffect));
       LogicFxInit.injectEffect("arcIndexer", new LogicFx.EffectEntry(arcIndexer).color().size());

        LogicFxInit.injectEffect("simpleRect", new LogicFx.EffectEntry(simpleRect).color().size());
        LogicFxInit.injectEffect("simpleCircle", new LogicFx.EffectEntry(simpleCircle).color().size());
        LogicFxInit.injectEffect("arcQuesMarker", new LogicFx.EffectEntry(arcQuesMarker).color().size());
        LogicFxInit.injectEffect("arcDefenseMarker", new LogicFx.EffectEntry(arcDefenseMarker).color().size());
        LogicFxInit.injectEffect("arcAttackMarker", new LogicFx.EffectEntry(arcAttackMarker).color().size());
        LogicFxInit.injectEffect("arcGatherMarker", new LogicFx.EffectEntry(arcGatherMarker).color().size());
        LogicFxInit.injectEffect("arcMarker", new LogicFx.EffectEntry(arcMarker).color().size());
        LogicFxInit.injectEffect("coreLandDust", new LogicFx.EffectEntry(coreLandDust).color().size());
        LogicFxInit.injectEffect("shieldBreak", new LogicFx.EffectEntry(shieldBreak).color().size());
        LogicFxInit.injectEffect("overdriveBlockFull", new LogicFx.EffectEntry(overdriveBlockFull).color().size());
        LogicFxInit.injectEffect("lightBlock", new LogicFx.EffectEntry(lightBlock).color().size());
        LogicFxInit.injectEffect("rotateBlock", new LogicFx.EffectEntry(rotateBlock).color().size());
        LogicFxInit.injectEffect("healBlock", new LogicFx.EffectEntry(healBlock).color().size());
        LogicFxInit.injectEffect("overdriveWave", new LogicFx.EffectEntry(overdriveWave).color().size());
        LogicFxInit.injectEffect("healWaveMend", new LogicFx.EffectEntry(healWaveMend).color().size());
        LogicFxInit.injectEffect("launchPod", new LogicFx.EffectEntry(launchPod).color().size());
        LogicFxInit.injectEffect("launch", new LogicFx.EffectEntry(launch).color().size());
        LogicFxInit.injectEffect("bubble", new LogicFx.EffectEntry(bubble).color().size());
        LogicFxInit.injectEffect("ripple", new LogicFx.EffectEntry(ripple).color().size());
        LogicFxInit.injectEffect("teleportOut", new LogicFx.EffectEntry(teleportOut).color().size());
        LogicFxInit.injectEffect("teleport", new LogicFx.EffectEntry(teleport).color().size());
        LogicFxInit.injectEffect("teleportActivate", new LogicFx.EffectEntry(teleportActivate).color().size());
        LogicFxInit.injectEffect("payloadReceive", new LogicFx.EffectEntry(payloadReceive).color().size());
        LogicFxInit.injectEffect("mineImpactWave", new LogicFx.EffectEntry(mineImpactWave).color().size());
        LogicFxInit.injectEffect("mineImpact", new LogicFx.EffectEntry(mineImpact).color().size());
        LogicFxInit.injectEffect("mineHuge", new LogicFx.EffectEntry(mineHuge).color().size());
        LogicFxInit.injectEffect("mineBig", new LogicFx.EffectEntry(mineBig).color().size());
        LogicFxInit.injectEffect("mine", new LogicFx.EffectEntry(mine).color().size());
        LogicFxInit.injectEffect("mineSmall", new LogicFx.EffectEntry(mineSmall).color().size());
        LogicFxInit.injectEffect("mineWallSmall", new LogicFx.EffectEntry(mineWallSmall).color().size());
        LogicFxInit.injectEffect("generate", new LogicFx.EffectEntry(generate).color().size());
        LogicFxInit.injectEffect("artilleryTrailSmoke", new LogicFx.EffectEntry(artilleryTrailSmoke).color().size());
        LogicFxInit.injectEffect("conveyorPoof", new LogicFx.EffectEntry(conveyorPoof).color().size());
        LogicFxInit.injectEffect("turbinegenerate", new LogicFx.EffectEntry(turbinegenerate).color().size());
        LogicFxInit.injectEffect("redgeneratespark", new LogicFx.EffectEntry(redgeneratespark).color().size());
        LogicFxInit.injectEffect("thoriumShoot", new LogicFx.EffectEntry(thoriumShoot).color().size());
        LogicFxInit.injectEffect("lightningShoot", new LogicFx.EffectEntry(lightningShoot).color().size());
        LogicFxInit.injectEffect("sparkShoot", new LogicFx.EffectEntry(sparkShoot).color().size());
        LogicFxInit.injectEffect("lightningCharge", new LogicFx.EffectEntry(lightningCharge).color().size());
        LogicFxInit.injectEffect("lancerLaserChargeBegin", new LogicFx.EffectEntry(lancerLaserChargeBegin).color().size());
        LogicFxInit.injectEffect("lancerLaserCharge", new LogicFx.EffectEntry(lancerLaserCharge).color().size());
        LogicFxInit.injectEffect("lancerLaserShootSmoke", new LogicFx.EffectEntry(lancerLaserShootSmoke).color().size());
        LogicFxInit.injectEffect("lancerLaserShoot", new LogicFx.EffectEntry(lancerLaserShoot).color().size());
        LogicFxInit.injectEffect("railHit", new LogicFx.EffectEntry(railHit).color().size());
        LogicFxInit.injectEffect("railTrail", new LogicFx.EffectEntry(railTrail).color().size());
        LogicFxInit.injectEffect("casing3Double", new LogicFx.EffectEntry(casing3Double).color().size());
        LogicFxInit.injectEffect("casing2Double", new LogicFx.EffectEntry(casing2Double).color().size());
        LogicFxInit.injectEffect("casing4", new LogicFx.EffectEntry(casing4).color().size());
        LogicFxInit.injectEffect("casing3", new LogicFx.EffectEntry(casing3).color().size());
        LogicFxInit.injectEffect("casing2", new LogicFx.EffectEntry(casing2).color().size());
        LogicFxInit.injectEffect("casing1", new LogicFx.EffectEntry(casing1).color().size());
        LogicFxInit.injectEffect("shootLiquid", new LogicFx.EffectEntry(shootLiquid).color().size());
        LogicFxInit.injectEffect("shootPyraFlame", new LogicFx.EffectEntry(shootPyraFlame).color().size());
        LogicFxInit.injectEffect("shootSmallFlame", new LogicFx.EffectEntry(shootSmallFlame).color().size());
        LogicFxInit.injectEffect("shootPayloadDriver", new LogicFx.EffectEntry(shootPayloadDriver).color().size());
        LogicFxInit.injectEffect("randLifeSpark", new LogicFx.EffectEntry(randLifeSpark).color().size());
        LogicFxInit.injectEffect("colorSparkBig", new LogicFx.EffectEntry(colorSparkBig).color().size());
        LogicFxInit.injectEffect("colorSpark", new LogicFx.EffectEntry(colorSpark).color().size());
        LogicFxInit.injectEffect("circleColorSpark", new LogicFx.EffectEntry(circleColorSpark).color().size());
        LogicFxInit.injectEffect("heatReactorSmoke", new LogicFx.EffectEntry(heatReactorSmoke).color().size());
        LogicFxInit.injectEffect("neoplasiaSmoke", new LogicFx.EffectEntry(neoplasiaSmoke).color().size());
        LogicFxInit.injectEffect("surgeCruciSmoke", new LogicFx.EffectEntry(surgeCruciSmoke).color().size());
        LogicFxInit.injectEffect("regenSuppressParticle", new LogicFx.EffectEntry(regenSuppressParticle).color().size());
        LogicFxInit.injectEffect("regenParticle", new LogicFx.EffectEntry(regenParticle).color().size());
        LogicFxInit.injectEffect("shootSmokeMissile", new LogicFx.EffectEntry(shootSmokeMissile).color().size());
        LogicFxInit.injectEffect("shootSmokeSquareBig", new LogicFx.EffectEntry(shootSmokeSquareBig).color().size());
        LogicFxInit.injectEffect("shootSmokeSquareSparse", new LogicFx.EffectEntry(shootSmokeSquareSparse).color().size());
        LogicFxInit.injectEffect("shootSmokeSquare", new LogicFx.EffectEntry(shootSmokeSquare).color().size());
        LogicFxInit.injectEffect("shootSmokeDisperse", new LogicFx.EffectEntry(shootSmokeDisperse).color().size());
        LogicFxInit.injectEffect("shootBigSmoke2", new LogicFx.EffectEntry(shootBigSmoke2).color().size());
        LogicFxInit.injectEffect("shootBigSmoke", new LogicFx.EffectEntry(shootBigSmoke).color().size());
        LogicFxInit.injectEffect("shootTitan", new LogicFx.EffectEntry(shootTitan).color().size());
        LogicFxInit.injectEffect("shootBigColor", new LogicFx.EffectEntry(shootBigColor).color().size());
        LogicFxInit.injectEffect("shootBig2", new LogicFx.EffectEntry(shootBig2).color().size());
        LogicFxInit.injectEffect("shootBig", new LogicFx.EffectEntry(shootBig).color().size());
        LogicFxInit.injectEffect("shootSmallSmoke", new LogicFx.EffectEntry(shootSmallSmoke).color().size());
        LogicFxInit.injectEffect("shootHealYellow", new LogicFx.EffectEntry(shootHealYellow).color().size());
        LogicFxInit.injectEffect("shootHeal", new LogicFx.EffectEntry(shootHeal).color().size());
        LogicFxInit.injectEffect("shootSmallColor", new LogicFx.EffectEntry(shootSmallColor).color().size());
        LogicFxInit.injectEffect("shootSmall", new LogicFx.EffectEntry(shootSmall).color().size());
        LogicFxInit.injectEffect("smokePuff", new LogicFx.EffectEntry(smokePuff).color().size());
        LogicFxInit.injectEffect("blockExplosionSmoke", new LogicFx.EffectEntry(blockExplosionSmoke).color().size());
        LogicFxInit.injectEffect("reactorExplosion", new LogicFx.EffectEntry(reactorExplosion).color().size());
        LogicFxInit.injectEffect("dynamicExplosion", new LogicFx.EffectEntry(dynamicExplosion).color().size());
        LogicFxInit.injectEffect("explosion", new LogicFx.EffectEntry(explosion).color().size());
        LogicFxInit.injectEffect("spawnShockwave", new LogicFx.EffectEntry(spawnShockwave).color().size());
        LogicFxInit.injectEffect("bigShockwave", new LogicFx.EffectEntry(bigShockwave).color().size());
        LogicFxInit.injectEffect("shockwave", new LogicFx.EffectEntry(shockwave).color().size());
        LogicFxInit.injectEffect("overclocked", new LogicFx.EffectEntry(overclocked).color().size());
        LogicFxInit.injectEffect("overdriven", new LogicFx.EffectEntry(overdriven).color().size());
        LogicFxInit.injectEffect("oily", new LogicFx.EffectEntry(oily).color().size());
        LogicFxInit.injectEffect("sporeSlowed", new LogicFx.EffectEntry(sporeSlowed).color().size());
        LogicFxInit.injectEffect("electrified", new LogicFx.EffectEntry(electrified).color().size());
        LogicFxInit.injectEffect("sapped", new LogicFx.EffectEntry(sapped).color().size());
        LogicFxInit.injectEffect("muddy", new LogicFx.EffectEntry(muddy).color().size());
        LogicFxInit.injectEffect("wet", new LogicFx.EffectEntry(wet).color().size());
        LogicFxInit.injectEffect("freezing", new LogicFx.EffectEntry(freezing).color().size());
        LogicFxInit.injectEffect("fireballsmoke", new LogicFx.EffectEntry(fireballsmoke).color().size());
        LogicFxInit.injectEffect("vaporSmall", new LogicFx.EffectEntry(vaporSmall).color().size());
        LogicFxInit.injectEffect("vapor", new LogicFx.EffectEntry(vapor).color().size());
        LogicFxInit.injectEffect("fluxVapor", new LogicFx.EffectEntry(fluxVapor).color().size());
        LogicFxInit.injectEffect("ventSteam", new LogicFx.EffectEntry(ventSteam).color().size());
        LogicFxInit.injectEffect("steam", new LogicFx.EffectEntry(steam).color().size());
        LogicFxInit.injectEffect("neoplasmHeal", new LogicFx.EffectEntry(neoplasmHeal).color().size());
        LogicFxInit.injectEffect("fireSmoke", new LogicFx.EffectEntry(fireSmoke).color().size());
        LogicFxInit.injectEffect("fireHit", new LogicFx.EffectEntry(fireHit).color().size());
        LogicFxInit.injectEffect("fire", new LogicFx.EffectEntry(fire).color().size());
        LogicFxInit.injectEffect("burning", new LogicFx.EffectEntry(burning).color().size());
        LogicFxInit.injectEffect("flakExplosionBig", new LogicFx.EffectEntry(flakExplosionBig).color().size());
        LogicFxInit.injectEffect("forceShrink", new LogicFx.EffectEntry(forceShrink).color().size());
        LogicFxInit.injectEffect("absorb", new LogicFx.EffectEntry(absorb).color().size());
        LogicFxInit.injectEffect("colorTrail", new LogicFx.EffectEntry(colorTrail).color().size());
        LogicFxInit.injectEffect("massiveExplosion", new LogicFx.EffectEntry(massiveExplosion).color().size());
        LogicFxInit.injectEffect("airBubble", new LogicFx.EffectEntry(airBubble));
        LogicFxInit.injectEffect("despawn", new LogicFx.EffectEntry(despawn).color().size());
        LogicFxInit.injectEffect("hitLaserColor", new LogicFx.EffectEntry(hitLaserColor).color().size());
        LogicFxInit.injectEffect("hitLaser", new LogicFx.EffectEntry(hitLaser).color().size());
        LogicFxInit.injectEffect("instHit", new LogicFx.EffectEntry(instHit).color().size());
        LogicFxInit.injectEffect("instShoot", new LogicFx.EffectEntry(instShoot).color().size());
        LogicFxInit.injectEffect("instBomb", new LogicFx.EffectEntry(instBomb).color().size());
        LogicFxInit.injectEffect("hitBeam", new LogicFx.EffectEntry(hitBeam).color().size());
        LogicFxInit.injectEffect("hitFlameBeam", new LogicFx.EffectEntry(hitFlameBeam).color().size());
        LogicFxInit.injectEffect("hitFlamePlasma", new LogicFx.EffectEntry(hitFlamePlasma).color().size());
        LogicFxInit.injectEffect("hitFlameSmall", new LogicFx.EffectEntry(hitFlameSmall).color().size());
        LogicFxInit.injectEffect("hitBulletBig", new LogicFx.EffectEntry(hitBulletBig).color().size());
        LogicFxInit.injectEffect("hitFuse", new LogicFx.EffectEntry(hitFuse).color().size());
        LogicFxInit.injectEffect("hitSquaresColor", new LogicFx.EffectEntry(hitSquaresColor).color().size());
        LogicFxInit.injectEffect("hitBulletColor", new LogicFx.EffectEntry(hitBulletColor).color().size());
        LogicFxInit.injectEffect("hitBulletSmall", new LogicFx.EffectEntry(hitBulletSmall).color().size());
        LogicFxInit.injectEffect("disperseTrail", new LogicFx.EffectEntry(disperseTrail).color().size());
        LogicFxInit.injectEffect("shieldApply", new LogicFx.EffectEntry(shieldApply).color().size());
        LogicFxInit.injectEffect("shieldWave", new LogicFx.EffectEntry(shieldWave).color().size());
        LogicFxInit.injectEffect("dynamicWave", new LogicFx.EffectEntry(dynamicWave).color().size());
        LogicFxInit.injectEffect("heal", new LogicFx.EffectEntry(heal).color().size());
        LogicFxInit.injectEffect("healWave", new LogicFx.EffectEntry(healWave).color().size());
        LogicFxInit.injectEffect("healWaveDynamic", new LogicFx.EffectEntry(healWaveDynamic).color().size());
        LogicFxInit.injectEffect("greenCloud", new LogicFx.EffectEntry(greenCloud).color().size());
        LogicFxInit.injectEffect("greenLaserChargeSmall", new LogicFx.EffectEntry(greenLaserChargeSmall).color().size());
        LogicFxInit.injectEffect("greenLaserCharge", new LogicFx.EffectEntry(greenLaserCharge).color().size());
        LogicFxInit.injectEffect("greenBomb", new LogicFx.EffectEntry(greenBomb).color().size());
        LogicFxInit.injectEffect("dynamicSpikes", new LogicFx.EffectEntry(dynamicSpikes).color().size());
        LogicFxInit.injectEffect("scatheSlash", new LogicFx.EffectEntry(scatheSlash).color().size());
        LogicFxInit.injectEffect("scatheLight", new LogicFx.EffectEntry(scatheLight).color().size());
        LogicFxInit.injectEffect("scatheExplosion", new LogicFx.EffectEntry(scatheExplosion).color().size());
        LogicFxInit.injectEffect("neoplasmSplat", new LogicFx.EffectEntry(neoplasmSplat).color().size());
        LogicFxInit.injectEffect("missileTrailSmoke", new LogicFx.EffectEntry(missileTrailSmoke).color().size());
        LogicFxInit.injectEffect("titanSmoke", new LogicFx.EffectEntry(titanSmoke).color().size());
        LogicFxInit.injectEffect("titanExplosion", new LogicFx.EffectEntry(titanExplosion).color().size());
        LogicFxInit.injectEffect("sparkExplosion", new LogicFx.EffectEntry(sparkExplosion).color().size());
        LogicFxInit.injectEffect("pickup", new LogicFx.EffectEntry(pickup).color().size());
        LogicFxInit.injectEffect("landShock", new LogicFx.EffectEntry(landShock).color().size());
        LogicFxInit.injectEffect("unitPickup", new LogicFx.EffectEntry(unitPickup).color().size());
        LogicFxInit.injectEffect("unitLandSmall", new LogicFx.EffectEntry(unitLandSmall).color().size());
        LogicFxInit.injectEffect("unitDust", new LogicFx.EffectEntry(unitDust).color().size());
        LogicFxInit.injectEffect("unitLand", new LogicFx.EffectEntry(unitLand).color().size());
        LogicFxInit.injectEffect("unitDrop", new LogicFx.EffectEntry(unitDrop).color().size());
        LogicFxInit.injectEffect("breakProp", new LogicFx.EffectEntry(breakProp).color().size());
        LogicFxInit.injectEffect("padlaunch", new LogicFx.EffectEntry(padlaunch).color().size());
        LogicFxInit.injectEffect("unitAssemble", new LogicFx.EffectEntry(unitAssemble).color().size());
        LogicFxInit.injectEffect("spawn2", new LogicFx.EffectEntry(spawn).color().size());
        LogicFxInit.injectEffect("magmasmoke", new LogicFx.EffectEntry(magmasmoke).color().size());
        LogicFxInit.injectEffect("rocketSmokeLarge", new LogicFx.EffectEntry(rocketSmokeLarge).color().size());
        LogicFxInit.injectEffect("rocketSmoke", new LogicFx.EffectEntry(rocketSmoke).color().size());
        LogicFxInit.injectEffect("smoke", new LogicFx.EffectEntry(smoke).color().size());
        LogicFxInit.injectEffect("select", new LogicFx.EffectEntry(select).color().size());
        LogicFxInit.injectEffect("breakBlock", new LogicFx.EffectEntry(breakBlock).color().size());
        LogicFxInit.injectEffect("tapBlock", new LogicFx.EffectEntry(tapBlock).color().size());
        LogicFxInit.injectEffect("coreLaunchConstruct", new LogicFx.EffectEntry(coreLaunchConstruct).color().size());
        LogicFxInit.injectEffect("placeBlock", new LogicFx.EffectEntry(placeBlock).color().size());
        LogicFxInit.injectEffect("upgradeCoreBloom", new LogicFx.EffectEntry(upgradeCoreBloom).color().size());
        LogicFxInit.injectEffect("commandSend", new LogicFx.EffectEntry(commandSend).color().size());
        LogicFxInit.injectEffect("attackCommand", new LogicFx.EffectEntry(attackCommand).color().size());
        LogicFxInit.injectEffect("moveCommand", new LogicFx.EffectEntry(moveCommand).color().size());
        LogicFxInit.injectEffect("pointShockwave", new LogicFx.EffectEntry(pointShockwave).color().size());
        LogicFxInit.injectEffect("pointHit", new LogicFx.EffectEntry(pointHit).color().size());










































    }


    //子弹特效  子弹效果
    public static DrawTurret 光棱塔蓄力射击效果() {
        return new DrawTurret() {{
            parts.addAll(
                    new HaloPart() {{
                        progress = warmup;
                        shapeRotation = 60;
                        y = 0;
                        sides = 3;
                        shapes = 2;
                        color = NewColor.光棱塔Bullet1;
                        colorTo = NewColor.光棱塔Bullet2;
                        hollow = true;
                        radius = 0;
                        radiusTo = 6;
                        triLength = 0;
                        triLengthTo = 30;
                        haloRadius = 0;
                        haloRotation = 0;
                        haloRotateSpeed = 5;
                        layer = 110;
                    }},
                    new HaloPart() {{
                        progress = warmup;
                        shapeRotation = 30;
                        y = 0;
                        sides = 3;
                        shapes = 2;
                        color = NewColor.光棱塔Bullet1;
                        colorTo = NewColor.光棱塔Bullet2;
                        hollow = true;
                        radius = 0;
                        radiusTo = 6;
                        triLength = 0;
                        triLengthTo = 30;
                        haloRadius = 0;
                        haloRotation = 0;
                        haloRotateSpeed = -5f;
                        layer = 110f;

                    }}
            );
        }};
    }

    public static DrawTurret 超级光棱塔蓄力射击效果() {
        return new DrawTurret() {{
            parts.addAll(

                    new HaloPart() {{
                        progress = warmup;
                        shapeRotation = 60;
                        y = 35;
                        sides = 3;
                        shapes = 2;
                        color = NewColor.光棱塔Bullet1;
                        colorTo = NewColor.光棱塔Bullet2;
                        tri = hollow = true;
                        radius = 0;
                        radiusTo = 6;
                        triLength = 30;
                        triLengthTo = 30;
                        haloRadius = 0;
                        haloRotation = 0;
                        haloRotateSpeed = -2;
                        layer = 110;
                    }},
                    new HaloPart() {{
                        progress = warmup;
                        shapeRotation = 30;
                        y = 35;
                        sides = 3;
                        shapes = 2;
                        color = NewColor.光棱塔Bullet1;
                        colorTo = NewColor.光棱塔Bullet2;
                        tri = hollow = true;
                        radius = 0;
                        radiusTo = 6;
                        triLength = 30;
                        triLengthTo = 30;
                        haloRadius = 0;
                        haloRotation = 120;
                        haloRotateSpeed = -2f;
                        layer = 110f;

                    }},
                    new HaloPart() {{
                        progress = warmup;
                        shapeRotation = 60;
                        y = 0;
                        sides = 3;
                        shapes = 2;
                        color = NewColor.光棱塔Bullet1;
                        colorTo = NewColor.光棱塔Bullet2;
                        hollow = true;
                        radius = 0;
                        radiusTo = 6;
                        triLength = 0;
                        triLengthTo = 30;
                        haloRadius = 0;
                        haloRotation = 0;
                        haloRotateSpeed = 5;
                        layer = 110;
                    }},
                    new HaloPart() {{
                        progress = warmup;
                        shapeRotation = 30;
                        y = 0;
                        sides = 3;
                        shapes = 2;
                        color = NewColor.光棱塔Bullet1;
                        colorTo = NewColor.光棱塔Bullet2;
                        hollow = true;
                        radius = 0;
                        radiusTo = 6;
                        triLength = 0;
                        triLengthTo = 30;
                        haloRadius = 0;
                        haloRotation = 0;
                        haloRotateSpeed = -5f;
                        layer = 110f;

                    }}
            );
        }};
    }

    public static Effect 起源蓄力效果() {
        return new MultiEffect() {{
            //lifetime = 80;
            //rotWithParent = true;
            //followParent = true;
            effects = new Effect[]{
                    new ParticleEffect() {{//实心圆，从小到大
                        particles = 1;
                        sizeFrom = 5;
                        sizeTo = 13;
                        interp = Interp.swing;
                        length = 1;
                        lifetime = 50;
                        colorFrom = 起源Bullet颜色1;
                        colorTo = 起源Bullet颜色2;
                    }},
                    new ParticleEffect() {{
                        line = true;
                        particles = 15;
                        lifetime = 60;
                        length = 45;
                        offset = 4;
                        baseLength = -45;
                        cone = -360;
                        lenFrom = 20;
                        lenTo = 0;
                        colorFrom = 起源Bullet颜色1;
                        colorTo = 起源Bullet颜色2;
                    }},
                    new WaveEffect() {{
                        lifetime = 70;
                        sizeFrom = 40;
                        sizeTo = 0;
                        strokeFrom = 0;
                        strokeTo = 7;
                        colorFrom = 起源Bullet颜色1;
                        colorTo = 起源Bullet颜色2;
                    }},
                    new WaveEffect() {{
                        lifetime = 70;
                        sizeFrom = 30;
                        sizeTo = 0;
                        strokeFrom = 0;
                        strokeTo = 7;
                        colorFrom = 起源Bullet颜色1;
                        colorTo = 起源Bullet颜色2;
                    }},
            };
        }};
    }

    public static Effect 尘埃蓄力效果() {
        return new MultiEffect() {{
            effects = new Effect[]{
                    new NewEffect.BulletParticleEffect() {{//外部线条向内聚集缩小，,适用蓄力效果
                        sizeFrom = 130;
                        sizeTo = 30;
                        line = true;
                        particles = 50;
                        lifetime = 200;
                        offset = 4;
                        cone = -360;
                        lenFrom = 200;
                        lenTo = 0;
                        colorFrom = 尘埃Bullet颜色1;
                        colorTo = 尘埃Bullet颜色2;
                    }},
                    new ParticleEffect() {{//实心圆，从小到大
                        particles = 1;
                        sizeFrom = 1;
                        sizeTo = 10.5f;
                        length = 0;
                        lifetime = 200;
                        cone = 360;
                        colorFrom = 尘埃Bullet颜色1;
                        colorTo = 尘埃Bullet颜色1;
                    }},
                    new ParticleEffect() {{//实心圆，从小到大
                        particles = 1;
                        sizeFrom = 1;
                        sizeTo = 18;
                        length = 0;
                        lifetime = 200;
                        layer = 109;
                        cone = 360;
                        colorFrom = 尘埃Bullet颜色1;
                        colorTo = 尘埃Bullet颜色1;
                    }},
                    new ParticleEffect() {{
                        particles = 1;
                        sizeFrom = 2;
                        sizeTo = 140;
                        length = 0;
                        spin = -10;
                        layer = 109;
                        lifetime = 200;
                        region = "ctcoresystem-菱形2";
                        colorFrom = 尘埃Bullet颜色1;
                        colorTo = 尘埃Bullet颜色2;
                    }},
                    new ParticleEffect() {{
                        particles = 1;
                        sizeFrom = 2;
                        sizeTo = 110;
                        length = 0;
                        spin = 10;
                        layer = 109;
                        lifetime = 200;
                        region = "ctcoresystem-菱形3";
                        colorFrom = 尘埃Bullet颜色1;
                        colorTo = 尘埃Bullet颜色2;
                    }},
                    new WaveEffect() {{
                        startDelay = 150;
                        lifetime = 50;
                        sizeFrom = 150;
                        sizeTo = 0;
                        strokeFrom = 6;
                        strokeTo = 0;
                        colorFrom = 尘埃Bullet颜色1;
                        colorTo = 尘埃Bullet颜色2;
                    }},
                    new WaveEffect() {{
                        startDelay = 150;
                        lifetime = 35;
                        sizeFrom = 150;
                        sizeTo = 0;
                        strokeFrom = 6;
                        strokeTo = 0;
                        colorFrom = 尘埃Bullet颜色1;
                        colorTo = 尘埃Bullet颜色2;
                    }},
                    new WaveEffect() {{
                        startDelay = 130;
                        lifetime = 50;
                        sizeFrom = 50;
                        sizeTo = 0;
                        strokeFrom = 6;
                        strokeTo = 0;
                        colorFrom = 尘埃Bullet颜色1;
                        colorTo = 尘埃Bullet颜色2;
                    }},
                    new WaveEffect() {{
                        startDelay = 100;
                        lifetime = 50;
                        sizeFrom = 70;
                        sizeTo = 0;
                        strokeFrom = 6;
                        strokeTo = 0;
                        colorFrom = 尘埃Bullet颜色1;
                        colorTo = 尘埃Bullet颜色2;
                    }},
                    new WaveEffect() {{
                        startDelay = 50;
                        lifetime = 50;
                        sizeFrom = 110;
                        sizeTo = 0;
                        strokeFrom = 6;
                        strokeTo = 0;
                        colorFrom = 尘埃Bullet颜色1;
                        colorTo = 尘埃Bullet颜色2;
                    }},
                    new WaveEffect() {{
                        lifetime = 200;
                        sizeFrom = 150;
                        sizeTo = 0;
                        strokeFrom = 6;
                        strokeTo = 0;
                        colorFrom = 尘埃Bullet颜色1;
                        colorTo = 尘埃Bullet颜色2;
                    }},
            };
        }};
    }

    public static Effect 尘埃子弹2次击中消失效果() {
        return new MultiEffect() {{
            lifetime = 80;
            effects = new Effect[]{

                    new WaveEffect() {{
                        lifetime = 30;
                        sizeFrom = 150;//开始范围直径
                        sizeTo = 0;//结束范围直径
                        strokeFrom = 8;//开始圈的线的粗度
                        strokeTo = 0;//结束圈的线的粗度
                        colorFrom = 尘埃Bullet颜色2;
                        colorTo = 尘埃Bullet颜色1;
                    }},

                    new WaveEffect() {{
                        startDelay = 25;//延迟出现时间
                        lifetime = 30;
                        sizeFrom = 50;
                        sizeTo = 0;
                        strokeFrom = 6;
                        strokeTo = 0;
                        colorFrom = 尘埃Bullet颜色2;
                        colorTo = 尘埃Bullet颜色1;
                    }},
                    new WaveEffect() {{
                        lifetime = 30;
                        sizeFrom = 100;
                        sizeTo = 0;
                        strokeFrom = 8;
                        strokeTo = 0;
                        colorFrom = 尘埃Bullet颜色2;
                        colorTo = 尘埃Bullet颜色1;
                    }},

                    new WaveEffect() {{
                        startDelay = 30;
                        lifetime = 30;
                        sizeFrom = 0;
                        sizeTo = 100;
                        strokeFrom = 6;
                        strokeTo = 0;
                        colorFrom = 尘埃Bullet颜色2;
                        colorTo = 尘埃Bullet颜色1;
                    }},
                    new WaveEffect() {{
                        startDelay = 50;
                        lifetime = 30;
                        sizeFrom = 0;
                        sizeTo = 100;
                        strokeFrom = 6;
                        strokeTo = 0;
                        colorFrom = 尘埃Bullet颜色2;
                        colorTo = 尘埃Bullet颜色1;
                    }},
                    new ParticleEffect() {{
                        lightOpacity = 0;
                        particles = 50;
                        length = 250;
                        baseLength = 5;
                        lifetime = 150;
                        layer = 106;
                        interp = Interp.circleOut;
                        sizeFrom = 24;
                        sizeTo = 10;
                        colorFrom = 尘埃Bullet颜色1;
                        colorTo = 尘埃Bullet颜色2;
                    }},
                    new ParticleEffect() {{
                        lightOpacity = 0;
                        particles = 50;
                        length = 250;
                        baseLength = 10;
                        lifetime = 180;
                        layer = 106;
                        interp = Interp.circleOut;
                        sizeFrom = 30;
                        sizeTo = 23;
                        colorFrom = 尘埃Bullet颜色1;
                        colorTo = 尘埃Bullet颜色2;
                        ;
                    }}
            };
        }};
    }

    public static Effect 尘埃子弹拖尾效果() {
        return new MultiEffect() {{
            lifetime = 20;
            layer = 105;
            effects = new Effect[]{
                    new ParticleEffect() {{
                        lightOpacity = 0;
                        length = 105;
                        cone = 0;
                        randLength = false;
                        sizeFrom = 45;
                        sizeTo = 45;
                        region = "ct-菱形2";
                        ;
                        particles = 1;
                        lifetime = 35;
                        colorFrom = 尘埃Bullet颜色2;
                        colorTo = 尘埃Bullet颜色1;
                        spin = 6.5f;
                    }},
                    new ParticleEffect() {{
                        lightOpacity = 0;
                        length = 105;
                        cone = 0;
                        randLength = false;
                        sizeFrom = 60;
                        sizeTo = 60;
                        region = "ct-菱形3";
                        ;
                        particles = 1;
                        lifetime = 35;
                        colorFrom = 尘埃Bullet颜色2;
                        colorTo = 尘埃Bullet颜色1;
                        spin = -6.5f;
                    }}
            };
        }};
    }

    public static DrawTurret 起源装弹效果() {//两个3角型旋转
        return new DrawTurret() {{
            parts.addAll(
                    new ShapePart() {{
                        progress = reload;
                        y = 0;
                        sides = 3;//边数
                        color = 起源Bullet颜色1;
                        stroke = 1;
                        strokeTo = 1;
                        radius = 17;
                        radiusTo = 0;
                        //circle= true;//圆
                        hollow = true;
                        layer = 110;
                        rotateSpeed = 0.5f;//旋转
                    }},
                    new ShapePart() {{
                        progress = reload;
                        y = 0;
                        sides = 3;//边数
                        color = 起源Bullet颜色1;
                        stroke = 1;//线条粗细初始值
                        strokeTo = 0;//线条粗细结束值
                        radius = 17;
                        radiusTo = 0;
                        //circle= true;//圆
                        hollow = true;
                        layer = 110;
                        rotateSpeed = -0.5f;//旋转
                    }}
            );
        }};
    }

    public static DrawTurret 皇后装弹效果() {//两个3角型旋转
        return new DrawTurret() {{
            parts.addAll(

                    new ShapePart() {{
                        progress = reload;
                        y = 17;
                        sides = 3;//边数
                        color = 皇后Bullet颜色3;
                        colorTo = 皇后Bullet颜色2;
                        stroke = 2;
                        strokeTo = 2;
                        radius = 5;
                        radiusTo = 0;
                        //circle= true;//圆
                        rotation = 0;
                        hollow = true;
                        layer = 110;
                        rotateSpeed = 0.5f;//旋转
                    }},
                    new ShapePart() {{
                        progress = reload;
                        y = 17;
                        sides = 3;//边数
                        color = 皇后Bullet颜色3;
                        colorTo = 皇后Bullet颜色2;
                        stroke = 2;//线条粗细初始值
                        strokeTo = 2;//线条粗细结束值
                        radius = 5;
                        radiusTo = 0;
                        //circle= true;//圆
                        rotation = 180;
                        hollow = true;
                        layer = 110;
                        rotateSpeed = 0.5f;//旋转
                    }}, new ShapePart() {{
                        progress = reload;
                        y = 17;
                        //sides = 3;//边数
                        color = 皇后Bullet颜色3;
                        stroke = 3;//线条粗细初始值
                        strokeTo = 3.5f;//线条粗细结束值
                        radius = 2f;
                        radiusTo = 0;
                        circle = true;//圆
                        rotation = 180;
                        hollow = true;
                        layer = 110;
                        rotateSpeed = 0f;//旋转
                    }}
            );
        }};
    }

    public static DrawTurret 帝王装弹效果() {//两个3角型旋转
        return new DrawTurret() {{
            parts.addAll(

                    new ShapePart() {{
                        progress = reload;
                        y = 17;
                        sides = 3;//边数
                        color = 帝王Bullet颜色1;
                        colorTo = 帝王Bullet颜色2;
                        stroke = 2;
                        strokeTo = 2;
                        radius = 5;
                        radiusTo = 0;
                        //circle= true;//圆
                        rotation = 0;
                        hollow = true;
                        layer = 110;
                        rotateSpeed = 0.5f;//旋转
                    }},
                    new ShapePart() {{
                        progress = reload;
                        y = 17;
                        sides = 3;//边数
                        color = 帝王Bullet颜色3;
                        colorTo = 帝王Bullet颜色2;
                        stroke = 2;//线条粗细初始值
                        strokeTo = 2;//线条粗细结束值
                        radius = 5;
                        radiusTo = 0;
                        //circle= true;//圆
                        rotation = 180;
                        hollow = true;
                        layer = 110;
                        rotateSpeed = 0.5f;//旋转
                    }}, new ShapePart() {{
                        progress = reload;
                        y = 17;
                        //sides = 3;//边数
                        color = 帝王Bullet颜色3;
                        stroke = 3;//线条粗细初始值
                        strokeTo = 3.5f;//线条粗细结束值
                        radius = 2f;
                        radiusTo = 0;
                        circle = true;//圆
                        rotation = 180;
                        hollow = true;
                        layer = 110;
                        rotateSpeed = 0f;//旋转
                    }}
            );
        }};
    }

    public static final Rand rand = new Rand();

    public static Effect 普通击中despawnEffect(Color 颜色) {
        return new Effect(14, 55f, e -> {
            {
                color(Color.white, 颜色, e.fin());
                e.scaled(7f, s -> {
                    stroke(0.5f + s.fout());
                    Lines.circle(e.x, e.y, s.fin() * 5f);
                });
                stroke(0.5f + e.fout());
                randLenVectors(e.id, 5, e.fin() * 15f, (x, y) -> {
                    float ang = Mathf.angle(x, y);
                    lineAngle(e.x + x, e.y + y, ang, e.fout() * 3 + 1f);
                });
                Drawf.light(e.x, e.y, 20f, 颜色, 0.6f * e.fout());
                Drawf.tri(e.x, e.y, 2, 10, e.rotation + 180);
                Drawf.tri(e.x, e.y, 10, 2, e.rotation + 180);
                Fill.circle(e.x, e.y, e.fout() + 3f);
            }
        });
    }


    public static Effect 毁灭despawnEffect() {
        return new Effect(30, 55f, b -> {
            float intensity = 6.8f;
            float baseLifetime = 25f + intensity * 11f;
            b.lifetime = 50f + intensity * 65f;
            color(Color.valueOf("c6a873"));
            alpha(0.7f);
            for (int i = 0; i < 4; i++) {
                rand.setSeed(b.id * 2 + i);
                float lenScl = rand.random(0.4f, 1f);
                int fi = i;
                b.scaled(60, e -> {
                    randLenVectors(e.id + fi - 1, e.fin(Interp.pow10Out), (int) (2.9f * intensity), 22f * intensity, (x, y, in, out) -> {
                        float fout = e.fout(Interp.pow5Out) * rand.random(0.5f, 1f);
                        float rad = fout * ((2f + intensity) * 2.35f);

                        Fill.circle(e.x + x, e.y + y, rad);
                        Drawf.light(e.x + x, e.y + y, rad * 2.5f, Color.valueOf("f99592"), 0.5f);
                    });
                });
            }
            b.scaled(baseLifetime, e -> {
                Draw.color();
                e.scaled(5 + intensity * 2f, i -> {
                    stroke((3.1f + intensity / 5f) * i.fout());
                    Lines.circle(e.x, e.y, (3f + i.fin() * 14f) * intensity);
                    Drawf.light(e.x, e.y, i.fin() * 14f * 2f * intensity, Color.white, 0.9f * e.fout());
                });
                color(Pal.lighterOrange, Pal.reactorPurple, e.fin());
                stroke((2f * e.fout()));
                Draw.z(Layer.effect + 0.001f);
                randLenVectors(e.id + 1, e.finpow() + 0.001f, (int) (8 * intensity), 28f * intensity, (x, y, in, out) -> {
                    lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + out * 4 * (4f + intensity));
                    Drawf.light(e.x + x, e.y + y, (out * 4 * (3f + intensity)) * 3.5f, Draw.getColor(), 0.8f);
                });
            });
        });
    }


    public static Effect 赤狐trailEffect(Color 颜色) {
        return new Effect(20, e -> {
            color(颜色);
            Fill.circle(e.x, e.y, e.rotation * e.fout());
            randLenVectors(e.id, 3, 2f + 15f * e.finpow(), (x, y) -> {
                Fill.circle(e.x + x, e.y + y, e.fout() * 3f + 0.5f);
            });
        });
    }

    public static Effect 子弹抛壳(float 抛壳大小, float 抛壳距离, Color Color1, Color Color2, Color Color3) {
        return new Effect(34f, e -> {
            color(Color1, Color2, Color3, e.fin());
            alpha(e.fout(0.3f));
            float rot = Math.abs(e.rotation) + 90f;
            int i = -Mathf.sign(e.rotation);
            float len = (2f + e.finpow() * 抛壳距离) * i;
            float lr = rot + e.fin() * 20f * i;
            rect(Core.atlas.find("casing"),
                    e.x + trnsx(lr, len) + Mathf.randomSeedRange(e.id + i + 7, 3f * e.fin()),
                    e.y + trnsy(lr, len) + Mathf.randomSeedRange(e.id + i + 8, 3f * e.fin()),
                    抛壳大小 + 0, 抛壳大小 + 1, rot + e.fin() * 50f * i
            );
        }).layer(Layer.bullet);
    }

    public static Effect 子弹抛壳2(float 抛壳大小, float 抛壳距离, Color Color1, Color Color2, Color Color3) {
        return new Effect(40f, e -> {
            color(Color1, Color2, Color3, e.fin());
            alpha(e.fout(0.5f));
            float rot = Math.abs(e.rotation) + 90f;
            int i = -Mathf.sign(e.rotation);
            float len = (4f + e.finpow() * 9f) * i;
            float lr = rot + Mathf.randomSeedRange(e.id + i + 6, 20f * e.fin()) * i;

            rect(Core.atlas.find("casing"),
                    e.x + trnsx(lr, len) + Mathf.randomSeedRange(e.id + i + 7, 3f * e.fin()),
                    e.y + trnsy(lr, len) + Mathf.randomSeedRange(e.id + i + 8, 3f * e.fin()),
                    抛壳大小 + 0, 抛壳大小 + 2,
                    rot + e.fin() * 50f * i
            );
        }).layer(Layer.bullet);

    }
}
