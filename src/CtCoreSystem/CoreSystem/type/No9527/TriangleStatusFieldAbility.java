package CtCoreSystem.CoreSystem.type.No9527;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.abilities.StatusFieldAbility;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;

import static mindustry.Vars.*;

/**
 * 正三角形范围的状态效果能力
 * 继承自StatusFieldAbility，但将圆形范围改为正三角形范围
 */
public class TriangleStatusFieldAbility extends StatusFieldAbility {
    // 三角形相关属性
    public float triangleRotation = 0f; // 三角形旋转角度（相对于单位朝向）
    public Color triangleColor = Pal.accent; // 三角形可视化颜色
    public float layer = Layer.bullet - 0.001f; // 绘制层级

    // 临时变量
    private static final Vec2[] trianglePoints = new Vec2[3];
    private static final Polygon trianglePolygon = new Polygon();

    static {
        // 初始化临时向量
        for (int i = 0; i < trianglePoints.length; i++) {
            trianglePoints[i] = new Vec2();
        }
    }

    // 构造函数
    public TriangleStatusFieldAbility(StatusEffect effect, float duration, float reload, float range) {
        super(effect, duration, reload, range);
    }

    /**
     * 计算正三角形的三个顶点
     * @param unit 拥有此能力的单位
     * @return 正三角形的三个顶点
     */
    private Vec2[] calculateTrianglePoints(Unit unit) {
        float baseAngle = unit.rotation + triangleRotation;

        // 计算正三角形的三个顶点
        for (int i = 0; i < 3; i++) {
            float angle = baseAngle + i * 120f;
            trianglePoints[i].trns(angle, range).add(unit.x + Angles.trnsx(unit.rotation, effectY, effectX),
                    unit.y + Angles.trnsy(unit.rotation, effectY, effectX));
        }

        return trianglePoints;
    }

    /**
     * 检查点是否在正三角形内
     */
    private boolean isPointInTriangle(float x, float y, Vec2[] points) {
        float[] vertices = new float[6];
        for (int i = 0; i < 3; i++) {
            vertices[i * 2] = points[i].x;
            vertices[i * 2 + 1] = points[i].y;
        }

        trianglePolygon.setVertices(vertices);
        return trianglePolygon.contains(x, y);
    }

    @Override
    public void update(Unit unit) {
        timer += Time.delta;

        if (timer >= reload && (!onShoot || unit.isShooting)) {
            // 计算三角形顶点
            Vec2[] points = calculateTrianglePoints(unit);

            // 查找三角形范围内的单位
            Units.nearby(unit.team, unit.x, unit.y, range * 1.5f, other -> {
                // 先进行粗略的圆形范围过滤，提高性能
                if (other.dst2(unit.x, unit.y) <= range * range * 2.25f &&
                        // 然后精确检查是否在三角形内
                        isPointInTriangle(other.x, other.y, points)) {
                    other.apply(effect, duration);
                    applyEffect.at(other, parentizeEffects);
                }
            });

            // 触发激活效果
            float x = unit.x + Angles.trnsx(unit.rotation, effectY, effectX);
            float y = unit.y + Angles.trnsy(unit.rotation, effectY, effectX);
            activeEffect.at(x, y, effectSizeParam ? range : unit.rotation, color, parentizeEffects ? unit : null);

            timer = 0f;
        }
    }

    @Override
    public void draw(Unit unit) {
        super.draw(unit);

        // 可视化正三角形范围

        Draw.z(Layer.shields);
        Draw.color(triangleColor);
        Draw.alpha(0.5f);

        Vec2[] points = calculateTrianglePoints(unit);

        // 修复：使用与计算顶点相同的中心点和角度来绘制填充三角形
        float centerX = unit.x + Angles.trnsx(unit.rotation, effectY, effectX);
        float centerY = unit.y + Angles.trnsy(unit.rotation, effectY, effectX);
        Fill.poly(centerX, centerY, 3, range, unit.rotation + triangleRotation);
        Draw.z(Layer.block);
        // 绘制三角形边框
        Draw.alpha(1f);
        Drawf.line(triangleColor,points[0].x, points[0].y, points[1].x, points[1].y);
        Drawf.line(triangleColor,points[1].x, points[1].y, points[2].x, points[2].y);
        Drawf.line(triangleColor,points[2].x, points[2].y, points[0].x, points[0].y);

        Draw.reset();
    }

    @Override
    public void addStats(Table t) {
        super.addStats(t);
        // 添加三角形范围的说明
        t.row();
        t.add(Core.bundle.format("ability.triangle-range", Strings.autoFixed(range / tilesize, 2)));
    }
}