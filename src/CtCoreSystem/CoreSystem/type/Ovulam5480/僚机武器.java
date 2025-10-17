package CtCoreSystem.CoreSystem.type.Ovulam5480;


import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.Trail;
import mindustry.type.Weapon;

public class 僚机武器 extends Weapon {
    //设为0, 效果为上下晃动
    public float X移动倍率 = 1f;
    //X轴的初始相位, 设为90则是左上--右下摆动, 设为-90则是右上--左下摆动
    public float X初相;
    //设为0, 效果为左右晃动
    public float Y移动倍率 = 1f;
    //Y轴的初始相位
    public float Y初相;
    public float 移动速度倍率 = 2f;
    public float 移动半径 = 1;
    public int 尾焰长度 = 16;
    public float 尾焰宽度 = 2.5f;
    public @Nullable Color 尾焰颜色 = Color.pink;


    public ObjectMap<Unit, Trail> trailmap = new ObjectMap<>();

    public 僚机武器(String name) {
        super(name);
        mirror = false;
    }

    public 僚机武器() {
        this("");
    }


    public void update(Unit unit, WeaponMount mount) {
        super.update(unit, mount);
        //x = Mathf.cosDeg(Time.time * 移动速度倍率) * 移动半径 * X移动倍率;
        x = Mathf.cos((Time.time * 移动速度倍率 + X初相) * Mathf.degreesToRadians) * 移动半径 * X移动倍率;
        y = Mathf.sin((Time.time * 移动速度倍率 + Y初相) * Mathf.degreesToRadians) * 移动半径 * Y移动倍率;

        updateTrail(unit, mount);
    }

    public void draw(Unit unit, WeaponMount mount) {
        super.draw(unit, mount);
        drawTrail(unit);
    }

    public Trail getTrail(Unit unit) {
        return trailmap.get(unit);
    }

    public void drawTrail(Unit unit) {
        if (!trailmap.containsKey(unit) || getTrail(unit) == null) {
            Trail trail = new Trail(尾焰长度);
            trailmap.put(unit, trail);
        } else {
            Draw.z(Layer.bullet + 1f);
            getTrail(unit).draw(尾焰颜色 == null ? unit.team.color : 尾焰颜色, 尾焰宽度 + Mathf.absin(Time.time, 2f, 尾焰宽度 / 4f));
        }
        Draw.z(Layer.flyingUnit);
    }

    public void updateTrail(Unit unit, WeaponMount mount) {
        if (getTrail(unit) != null) {
            getTrail(unit).length = 尾焰长度;

            float rotation = unit.rotation - 90,
                    realRecoil = Mathf.pow(mount.recoil, recoilPow) * recoil,
                    weaponRotation = rotation + (rotate ? mount.rotation : baseRotation),
                    wx = unit.x + Angles.trnsx(rotation, x, y) + Angles.trnsx(weaponRotation, 0, -realRecoil),
                    wy = unit.y + Angles.trnsy(rotation, x, y) + Angles.trnsy(weaponRotation, 0, -realRecoil);

            getTrail(unit).update(wx, wy);
        }
    }
}
