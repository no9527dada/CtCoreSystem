package CtCoreSystem.content;

import arc.files.Fi;
import arc.graphics.gl.Shader;
import mindustry.Vars;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Shaders;
import mindustry.world.blocks.environment.Floor;

import static mindustry.graphics.Shaders.getShaderFi;

public class CTFragShader {
    public static Fi shaders;
    public static CacheLayer.ShaderLayer m;
    public static CacheLayer.ShaderLayer 紫色冷却液效果;
    public static CacheLayer.ShaderLayer 灰色冷却液大效果;
    public static CacheLayer.ShaderLayer 绿色冷却液大效果;
    public static CacheLayer.ShaderLayer 黄色冷却液大效果;
    public static CacheLayer.ShaderLayer 粉色冷却液效果;
    public static CacheLayer.ShaderLayer 蓝色冷却液效果;
    public static CacheLayer.ShaderLayer 玫瑰金小点效果;
    public static CacheLayer.ShaderLayer weizhi;
    public static CacheLayer.ShaderLayer 血条水效果;
    public static CacheLayer.ShaderLayer FI8;
   //er FI9;

    public CTFragShader() {
    }

    public static void load() {
    }

    static {
        shaders = Vars.mods.locateMod("ctcoresystem").root.child("CTshaders");
        紫色冷却液效果 = new CacheLayer.ShaderLayer(new Shaders.SurfaceShader(Shaders.getShaderFi("screenspace.vert").readString(), shaders.child("Floor1.frag").readString()));
        灰色冷却液大效果 = new CacheLayer.ShaderLayer(new Shaders.SurfaceShader(Shaders.getShaderFi("screenspace.vert").readString(), shaders.child("Floor2.frag").readString()));
        玫瑰金小点效果 = new CacheLayer.ShaderLayer(new Shaders.SurfaceShader(Shaders.getShaderFi("screenspace.vert").readString(), shaders.child("Floor3.frag").readString()));
        粉色冷却液效果 = new CacheLayer.ShaderLayer(new Shaders.SurfaceShader(Shaders.getShaderFi("screenspace.vert").readString(), shaders.child("Floor4.frag").readString()));
        绿色冷却液大效果 = new CacheLayer.ShaderLayer(new Shaders.SurfaceShader(Shaders.getShaderFi("screenspace.vert").readString(), shaders.child("Floor5.frag").readString()));
        黄色冷却液大效果 = new CacheLayer.ShaderLayer(new Shaders.SurfaceShader(Shaders.getShaderFi("screenspace.vert").readString(), shaders.child("Floor6.frag").readString()));
        蓝色冷却液效果 = new CacheLayer.ShaderLayer(new Shaders.SurfaceShader(Shaders.getShaderFi("screenspace.vert").readString(), shaders.child("Floor7.frag").readString()));
        血条水效果 = new CacheLayer.ShaderLayer(new Shaders.SurfaceShader(Shaders.getShaderFi("screenspace.vert").readString(), shaders.child("water2.frag").readString()));
        weizhi = new CacheLayer.ShaderLayer(new Shaders.SurfaceShader(Shaders.getShaderFi("screenspace.vert").readString(), shaders.child("cryofluid1.frag").readString()));
        m = new CacheLayer.ShaderLayer(new Shaders.SurfaceShader(Shaders.getShaderFi("screenspace.vert").readString(), shaders.child("cryofluid2.frag").readString()));
        FI8 = new CacheLayer.ShaderLayer(new Shaders.SurfaceShader(Shaders.getShaderFi("screenspace.vert").readString(), shaders.child("Fi8.frag").readString()));
        //FI9 = new CacheLayer.ShaderLayer(new Shaders.SurfaceShader(Shaders.getShaderFi("screenspace.vert").readString(), shaders.child("Fi9.frag").readString()));


        CacheLayer.add(new CacheLayer[]{
                FI8, m, 紫色冷却液效果, weizhi,灰色冷却液大效果,玫瑰金小点效果,
                绿色冷却液大效果,黄色冷却液大效果,粉色冷却液效果,蓝色冷却液效果,血条水效果
        });
    }
}
