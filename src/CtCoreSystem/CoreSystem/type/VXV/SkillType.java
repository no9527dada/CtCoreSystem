package CtCoreSystem.CoreSystem.type.VXV;

import arc.Core;
import arc.struct.ObjectMap;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import mindustry.type.Weapon;

//TODO <技能>
public class SkillType {
    public static ObjectMap<String, SkillType> SkillAll = new ObjectMap<>();
    public static SkillType getSkill(String name){
        return SkillAll.get(name);
    }

    public String 名字;
    public float 时间;
    public String 图标;

    public Effect effect = null;
    public StatusEffect status = null;
    public float statusTime = -1;

    public StatusEffect[] unstatus = null;

    public Weapon weapon = null;

    public boolean AI = false;
    public float AITime = 1f;

    public SkillType(String 名字, String 图标, float 时间){
        this.名字 = 名字;
        this.图标 = 图标;
        this.时间 = 时间;

        显示名 = Core.bundle.get("Skill." + 名字 + ".name", 名字);
        简介 = Core.bundle.format("Skill." + 名字 + ".description");

        SkillAll.put(名字, this);
    }

    public String 显示名;
    public String 简介;

    public void freed(Unit unit){

    }

    public boolean freedAI(Unit unit){
        return false;
    }

    public void update(Unit unit, boolean freed){

    }

    public void draw(Unit unit, boolean freed){

    }

/*    public SkillUnit create() {
        return new SkillUnit(this);
    }*/
}
