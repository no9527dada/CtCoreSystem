package CtCoreSystem.content;

import CtCoreSystem.CoreSystem.type.CTColor;
import CtCoreSystem.CoreSystem.type.No9527.TeamStatusFieldAbility;
import CtCoreSystem.CoreSystem.type.OverdriveAbility;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.FloatSeq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.entities.bullet.LaserBoltBulletType;
import mindustry.game.Team;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static mindustry.content.StatusEffects.burning;

public class 实验产物 {


    public static void load() {
        float 血量=24000;
        new UnitType("实验产物单位"){{
            constructor = UnitTypes.horizon.constructor;
            flying = true;
            hitSize=5*8;
            engineSize = 0f;
            health=血量;
            //  abilities.add(new drawAbility(Pal.heal));
            abilities.add(new OverdriveAbility(2f, 45*8f,0.2f,80*8f,25));
            abilities.add(new TeamStatusFieldAbility(burning, 60,60f,80*8f, Team.crux));
            weapons.add(
                    new Weapon(""){{
                        shootSound = Sounds.lasershoot;
                        reload = 24f;
                        x = 10f;
                        y = 0f;
                        rotate = true;

                        bullet = new LaserBoltBulletType(5.2f, 10){{
                            lifetime = 35f;
                            healPercent = 5.5f;
                            collidesTeam = true;
                            backColor = Pal.heal;
                            frontColor = Color.white;
                        }};
                    }},
                    new Weapon(""){{
                        shootSound = Sounds.lasershoot;
                        reload = 15f;
                        x = -10f;
                        y = 0f;
                        rotate = true;
                        bullet = new LaserBoltBulletType(5.2f, 8){{
                            lifetime = 35f;
                            healPercent = 3f;
                            collidesTeam = true;
                            backColor = Pal.heal;
                            frontColor = Color.white;
                        }};
                    }}
            );








        }

            public final float radiusTime = 120.0F;
            public final float showshowTime = 8*60.0F;
            public final Color color = CTColor.C("97fdff");
            public final float multi = 0.25F;
            public final float 半径=5*8f;
            public final float 层级=100;
            public void draw(Unit unit) {
                super.draw(unit);
                Color 统一颜色 = CTColor.C("76ff9a");



                Draw.z(层级);
                //从小变大的空心圆
                float progress = Time.time % this.radiusTime / this.radiusTime;
                float showProgress = Time.time % this.radiusTime / this.showshowTime;

                stroke(Math.min(4.0F, (1.0F - progress) * 16.0F) * this.multi);
                this.color.a(Mathf.clamp((1.0F - showProgress) * 4.0F, 0.0F, 1.0F));
                color(this.color);
                Draw.alpha(0.5f);//设置透明度
                stroke(3.0F);
                Lines.circle(unit.x, unit.y, progress * 半径);


   /*             Draw.z(层级);
                // 绘制一个填充的圆圈
                Draw.color(统一颜色);
                Lines.stroke((0.7F + Mathf.absin(3, 0.7f)), color);
                Fill.circle(unit.x, unit.y, 半径-1);
                Draw.reset();
                */

                //静止的空心圆
                color(统一颜色);
                Draw.alpha(1f);//设置透明度
                stroke(22.0F);
                Lines.circle(unit.x, unit.y,  半径);

                stroke(2F);
                Draw.alpha(1f);//设置透明度
                Lines.circle(unit.x, unit.y,  半径-1);
                Draw.reset();

                //静止的空心圆
                color(统一颜色);
                Draw.alpha(0.5f);//设置透明度
                stroke(15.0F);
                Lines.circle(unit.x, unit.y,  半径+17);

                stroke(1F);
                Draw.alpha(1f);//设置透明度
                Lines.circle(unit.x, unit.y,  半径+25);
                Draw.reset();

       /*         //实心圆 黑色
                Draw.z(层级+0.1f);
               Draw.alpha(0.2f);//设置透明度
                Draw.color(CTColor.C("141414"));
                Fill.circle(unit.x, unit.y,  半径-3);
                Draw.reset();
*/



                Draw.z(层级);
                float 正方形大小=4;
                int 正方形数量 = 8;
                float 角度间隔 = 360f / 正方形数量;
                float 自转速度 = -1f; // 控制自转速度

                // 使用循环创建8个正方形
                for(int i = 0; i < 正方形数量; i++){
                    float obitRotate3 = (Time.time - 0.5f + i * 角度间隔) % 360;
                    // 为每个正方形添加独立的自转角度，使用i索引来区分不同的自转速度
                    float rotation3 = (Time.time * 自转速度 + i ) % 360;

                    // 使用"半径"变量作为公转半径
                    var v3 = Tmp.v1.set(1, 1).setLength(半径+18).rotate(obitRotate3);
                    color(Pal.accent); // 设置正方形颜色，可以根据需要调整

                    // 绘制正方形，使用rotation3作为自转角度
                    Fill.square(unit.x + v3.x, unit.y + v3.y, 正方形大小, rotation3);
                }
                Draw.reset();

                //星贴图
                Draw.z(层级 + 1);
                color(Color.valueOf(("ffed76")));
                Draw.rect(Core.atlas.find("ctcoresystem-斜四星"), unit.x, unit.y, 90, 90, Time.time * -1);
                Draw.reset();


                // 新增绘制6个空心长方形 跟随变量“半径”值公转，公转和自转的值设置一样 实现潮汐锁定 始终一条边朝向中心
                Draw.z(层级+1);
// 设置空心长方形的颜色
                color(Pal.lancerLaser);
// 定义空心长方形的大小参数（可调节）
                float rectWidth = 21f; // 长方形宽度
                float rectHeight = 12f; // 长方形高度
                float rotationSpeed = -0.5f; // 旋转速度
                float orbitAngle = Time.time * rotationSpeed % 360; // 基础公转角度
                float lineWidth = 2.5f; // 线条宽度
                int rectCount = 6; // 长方形数量
                float angleStep = 360f / rectCount; // 每个长方形之间的角度间隔

// 设置线条宽度
                stroke(lineWidth);
                float orbitRadius = 半径 + 32; // 公转半径

// 循环绘制6个长方形
                for (int i = 0; i < rectCount; i++) {
                    // 计算每个长方形的公转角度（基础角度加上偏移量）
                    float currentOrbitAngle = (orbitAngle + i * angleStep) % 360;

                    // 计算公转位置
                    var rectOrbit = Tmp.v1.set(1, 0).setLength(orbitRadius).rotate(currentOrbitAngle);
                    float centerX = unit.x + rectOrbit.x;
                    float centerY = unit.y + rectOrbit.y;

                    // 预计算旋转角度的sin和cos值
                    float cos = Mathf.cosDeg(currentOrbitAngle);
                    float sin = Mathf.sinDeg(currentOrbitAngle);

                    // 定义长方形四个顶点的相对坐标
                    float halfHeight = rectWidth / 2;
                    float halfWidth = rectHeight / 2;

                    // 计算四个顶点在旋转后的实际坐标
                    // 左上顶点
                    float x1 = centerX + (-halfWidth * cos - (-halfHeight) * sin);
                    float y1 = centerY + (-halfWidth * sin + (-halfHeight) * cos);
                    // 右上顶点
                    float x2 = centerX + (halfWidth * cos - (-halfHeight) * sin);
                    float y2 = centerY + (halfWidth * sin + (-halfHeight) * cos);
                    // 右下顶点
                    float x3 = centerX + (halfWidth * cos - halfHeight * sin);
                    float y3 = centerY + (halfWidth * sin + halfHeight * cos);
                    // 左下顶点
                    float x4 = centerX + (-halfWidth * cos - halfHeight * sin);
                    float y4 = centerY + (-halfWidth * sin + halfHeight * cos);

                    // 使用Lines.line绘制空心长方形的四条边
                    Lines.line(x1, y1, x2, y2);
                    Lines.line(x2, y2, x3, y3);
                    Lines.line(x3, y3, x4, y4);
                    // 移除下边（朝向中心的边）： Lines.line(x4, y4, x1, y1);
                }
                // 重置绘图状态，避免影响后续绘制
                stroke(1f); // 重置线条宽度
                Draw.reset();

                //外部的星贴图
                Draw.z(层级+1);
                color(Pal.lancerLaser);

                // 生成6个星贴图并让它们绕半径公转
                int count = 6; // 星贴图数量
                float 贴图大小=18;
                float angleSpacing = 360f / count; // 每个星贴图之间的角度间隔
                float 星贴图orbitRadius = 半径 + 32; // 公转半径，基于现有"半径"变量并添加偏移
                float orbitSpeed = -0.5f; // 公转速度

                for(int i = 0; i < count; i++) {
                    // 计算当前星贴图的公转角度
                    float currentAngle = (i * angleSpacing + Time.time * orbitSpeed) % 360;

                    // 使用Mathf.cosDeg和Mathf.sinDeg计算公转位置
                    float x = unit.x + Mathf.cosDeg(currentAngle) * 星贴图orbitRadius;
                    float y = unit.y + Mathf.sinDeg(currentAngle) * 星贴图orbitRadius;

                    // 绘制星贴图，保持原有的大小和旋转效果
                    Draw.rect(Core.atlas.find("ctcoresystem-斜四星"), x, y, 贴图大小, 贴图大小, Time.time * 1.5f);
                }
                color(Color.valueOf("ffed76"));
                for(int i = 0; i < count; i++) {
                    // 计算当前星贴图的公转角度
                    float currentAngle = (i * angleSpacing+30 + Time.time * orbitSpeed) % 360;

                    // 使用Mathf.cosDeg和Mathf.sinDeg计算公转位置
                    float x = unit.x + Mathf.cosDeg(currentAngle) * 星贴图orbitRadius;
                    float y = unit.y + Mathf.sinDeg(currentAngle) * 星贴图orbitRadius;

                    // 绘制星贴图，保持原有的大小和旋转效果
                    Draw.rect(Core.atlas.find("ctcoresystem-斜四星"), x, y, 贴图大小, 贴图大小, Time.time * -1.5f);
                }

                Draw.reset();

                //新增实心菱形
                Draw.z(层级+1);
                color(Pal.accent);

                // 定义菱形的大小参数
                float diamondSize = 100f; // 菱形基本大小
                float aspectRatio = 0.1f; // 宽高比，值越小越细长

                // 绘制第一个菱形（正转 - 逆时针）
                float rotation1 = Time.time * -0.8f % 360; // 逆时针旋转
                float cos1 = Mathf.cosDeg(rotation1);
                float sin1 = Mathf.sinDeg(rotation1);
                float[] diamondPoints1 = new float[8];
                float[][] basePoints1 = {{1, 0}, {0, aspectRatio}, {-1, 0}, {0, -aspectRatio}};
                for(int i = 0; i < 4; i++){
                    float scaledX = basePoints1[i][0] * diamondSize;
                    float scaledY = basePoints1[i][1] * diamondSize;
                    float rotatedX = scaledX * cos1 - scaledY * sin1;
                    float rotatedY = scaledX * sin1 + scaledY * cos1;
                    diamondPoints1[i*2] = unit.x + rotatedX;
                    diamondPoints1[i*2+1] = unit.y + rotatedY;
                }
                Fill.poly(FloatSeq.with(diamondPoints1));

                // 绘制第二个菱形（反转 - 顺时针）
                float rotation2 = Time.time * 0.8f % 360; // 顺时针旋转
                float cos2 = Mathf.cosDeg(rotation2);
                float sin2 = Mathf.sinDeg(rotation2);
                float[] diamondPoints2 = new float[8];
                float[][] basePoints2 = {{1, 0}, {0, aspectRatio}, {-1, 0}, {0, -aspectRatio}};
                for(int i = 0; i < 4; i++){
                    float scaledX = basePoints2[i][0] * (diamondSize+30);
                    float scaledY = basePoints2[i][1] * (diamondSize+30);
                    float rotatedX = scaledX * cos2 - scaledY * sin2;
                    float rotatedY = scaledX * sin2 + scaledY * cos2;
                    diamondPoints2[i*2] = unit.x + rotatedX;
                    diamondPoints2[i*2+1] = unit.y + rotatedY;
                }
                Fill.poly(FloatSeq.with(diamondPoints2));

                Draw.reset(); // 重置绘图状态，避免影响后续绘制



                //血量视觉显示效果
                color(unit.team.color);
                float rad = 100f;

                // 跟随单位移动方向旋转 - 获取单位旋转角度
                float rotation = unit.rotation;

                // 预计算旋转角度的sin和cos值（性能优化）
                float cos = Mathf.cosDeg(rotation);
                float sin = Mathf.sinDeg(rotation);

                // 计算单位的血量百分比（只需计算一次，优化性能）
                float healthPercentage;
                // 防止除零错误（健壮性优化）
                if(unit.maxHealth <= 0f) {
                    healthPercentage = 1f; // 当最大血量无效时，默认为满血状态
                } else {
                    healthPercentage = unit.health / unit.maxHealth;
                    // 限制健康百分比范围，避免异常值（健壮性优化）
                    healthPercentage = Mathf.clamp(healthPercentage, 0f, 1f);
                }



                // 绘制外圈线条（不受旋转影响）
                // 优化闪烁效果：基于血量百分比和时间
                if(healthPercentage < 0.6f ) {// 只有非满血时才计算闪烁效果（性能优化）

                    float flickerValue = Mathf.absin(Time.time * 0.8f, 1f, 1f); // 范围：0-1-0

                    // 调整透明度范围：满血时完全不透明，血量越低透明度变化范围越大
                    float alpha = Mathf.lerp(0.1f, 1f, flickerValue); // 使用lerp函数简化计算
                    Draw.alpha(alpha); // 设置外圈线条透明度

                }
                else {
                    if( healthPercentage >= 0.6f&&healthPercentage <1f){
                        float flickerValue2 = Mathf.absin(Time.time * 0.1f, 1f, 1f); // 范围：0-1-0
                        // 调整透明度范围：满血时完全不透明，血量越低透明度变化范围越大
                        float alpha2 = Mathf.lerp(0.1f, 1f, flickerValue2); // 使用lerp函数简化计算
                        Draw.alpha(alpha2); // 设置外圈线条透明度
                    }else {
                        Draw.alpha(1f); // 满血时保持完全不透明
                    }
                }

                stroke(4f); // 固定线条宽度
                Lines.circle(unit.x, unit.y, rad);

                // 绘制三角形效果（受旋转影响）

                // 设置length初始值为50，血量越低，length越小（最低为1）
                float length = 1f + healthPercentage * 49f; // 范围：1-50
                // 优化颜色选择逻辑，使用三元运算符简化代码
                if(healthPercentage < 1f) {
                    color(unit.team == Vars.state.rules.defaultTeam ? Color.valueOf("8bffa9") : Color.valueOf("ffcc15"));
                } else {
                    color(unit.team.color);
                }

                // 提取常量到变量，提高可读性和可维护性
                final int POINTS_COUNT = 10;
                float offset = Mathf.randomSeed(unit.id, 360f);

                for(int i = 0; i < POINTS_COUNT; i++){
                    float angle = i * 360f / POINTS_COUNT + offset;
                    // 计算三角形相对于单位中心的位置（先应用单位旋转）
                    float localX = Angles.trnsx(angle, rad);
                    float localY = Angles.trnsy(angle, rad);

                    // 应用单位的旋转角度
                    float rotatedX = localX * cos - localY * sin;
                    float rotatedY = localX * sin + localY * cos;

                    // 计算最终世界坐标
                    float worldX = unit.x + rotatedX;
                    float worldY = unit.y + rotatedY;

                    // 绘制三角形，注意设置正确的旋转角度
                    Drawf.tri(worldX, worldY, 6f, length, angle + rotation);
                }

                // 绘制发光效果（不受旋转影响）
                Drawf.light(unit.x, unit.y, rad * 1.6f, Pal.heal, 1f); // 固定亮度

                Draw.reset(); // 重置绘图状态，避免影响后续绘制








                /*

                 //长方形出现不断变大消失
                Draw.z(层级);
                Draw.color(Pal.lancerLaser);
                // 定义空心长方形的大小参数（可调节）
                float rectWidth = 20f; // 长方形宽度
                float rectHeight = 15f; // 长方形高度
                float lineWidth = 1.5f; // 线条宽度
                int rectCount = 5; // 长方形数量
                float orbitSpeed = 0.3f; // 公转速度

                // 最小间隔距离 = 长方形宽度 + 10
                float minSeparation = rectWidth + 10f;
                // 计算最小角度间隔（弧度）
                float minAngleSeparation = Mathf.atan2(minSeparation, 半径 + 50) * Mathf.radiansToDegrees;

                // 设置线条宽度
                Lines.stroke(lineWidth);

                // 使用循环创建多个长方形，每个长方形的位置随机但保持最小间隔
                for(int i = 0; i < rectCount; i++){
                    // 为每个长方形生成基于索引的随机角度，但确保间隔足够大
                    // 使用i*100作为随机种子，确保每个长方形的位置相对稳定
                    float baseAngle = (Mathf.randomSeed(i * 100) * (360f - rectCount * minAngleSeparation)) % 360f;

                    // 添加累积的最小间隔，确保间隔距离
                    float randomAngle = (baseAngle + i * minAngleSeparation) % 360f;

                    // 添加随时间变化的公转角度
                    randomAngle = (randomAngle + Time.time * orbitSpeed) % 360f;

                    // 使用"半径"变量作为公转半径
                    var rectV = Tmp.v1.set(1, 1).setLength(半径 + 50).rotate(randomAngle);

                    // 绘制空心长方形，设置自转角度与公转角度相同，实现潮汐锁定效果
                    // 注意：这里的randomAngle就是潮汐锁定的自转角度
                    Lines.rect(unit.x + rectV.x, unit.y + rectV.y, rectWidth, rectHeight, randomAngle);
                }

                // 重置绘图状态，避免影响后续绘制
                Lines.stroke(1f); // 重置线条宽度
                Draw.reset();
                * */

            }
        };



    }
}
