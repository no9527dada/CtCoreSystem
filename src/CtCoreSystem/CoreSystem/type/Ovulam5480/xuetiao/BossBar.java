package CtCoreSystem.CoreSystem.type.Ovulam5480.xuetiao;

import arc.Core;
import arc.func.Floatp;
import arc.func.Func;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.GlyphLayout;
import arc.graphics.g2d.ScissorStack;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.scene.Element;
import arc.scene.style.Drawable;
import arc.scene.ui.layout.Scl;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.pooling.Pools;
import mindustry.gen.Tex;
import mindustry.ui.Fonts;
//BOSS血条
public class BossBar extends Element {
    private static final Rect scissor = new Rect();

    private Floatp fraction;
    private final Color blinkColor = new Color();
    private final Color outlineColor = new Color();
    private CharSequence name = "";
    private float value, lastValue, blink, outlineRadius;
    private @Nullable Func<Float, Shader> shaders;
    private final FrameBuffer buffer = new FrameBuffer();

    public BossBar(){
        this.fraction = () -> 0;
        update(() -> {
            this.name = String.format("%.2f", fraction.get() * 100) + "%";
           // Log.info(name);
        });
    }

    public void set(Color color, Floatp fraction, Func<Float, Shader> shaders){
        this.fraction = fraction;
        this.lastValue = fraction.get();
        this.blinkColor.set(color);
        this.shaders = shaders;
        setColor(color);
    }

    public void reset(float value) {
        this.value = lastValue = blink = value;
    }

    public void snap() {
        lastValue = value = fraction.get();
    }

    public BossBar outline(Color color, float stroke) {
        outlineColor.set(color);
        outlineRadius = Scl.scl(stroke);
        return this;
    }

    public void flash() {
        blink = 1f;
    }

    public void flash(float f) {
        blink = f;
    }

    public BossBar blink(Color color) {
        blinkColor.set(color);
        return this;
    }

    @Override
    public void draw() {
        if (fraction == null) return;

        float computed = Mathf.clamp(fraction.get());


        if (lastValue > computed) {
            blink = 1f;
            lastValue = computed;
        }

        if (Float.isNaN(lastValue)) lastValue = 0;
        if (Float.isInfinite(lastValue)) lastValue = 1f;
        if (Float.isNaN(value)) value = 0;
        if (Float.isInfinite(value)) value = 1f;
        if (Float.isNaN(computed)) computed = 0;
        if (Float.isInfinite(computed)) computed = 1f;

        blink = Mathf.lerpDelta(blink, 0f, 0.2f);
        value = Mathf.lerpDelta(value, computed, 0.15f);

        Drawable bar = Tex.bar;

        if (outlineRadius > 0) {
            Draw.color(outlineColor);
            bar.draw(x - outlineRadius, y - outlineRadius, width + outlineRadius * 2, height + outlineRadius * 2);
        }

        Draw.colorl(0.1f);
        Draw.alpha(parentAlpha);
        bar.draw(x, y, width, height);
        Draw.color(color, blinkColor, blink);
        Draw.alpha(parentAlpha);

        Drawable top = Tex.barTop;
        float topWidth = width * value;

        boolean hasShaders = shaders != null && shaders.get(value) != null;
        if (hasShaders) {
            buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
            buffer.begin(Color.clear);
        }

        if (topWidth > Core.atlas.find("bar-top").width) {
            top.draw(x, y, topWidth, height);
        } else {
            if (ScissorStack.push(scissor.set(x, y, topWidth, height))) {
                top.draw(x, y, Core.atlas.find("bar-top").width, height);
                ScissorStack.pop();
            }
        }

        if (hasShaders) {
            buffer.end();
            buffer.blit(shaders.get(value));
        }

        Draw.color();

    /* // 血量百分比数字显示
      Font font = Fonts.tech;

        font.getData().setScale(2);
        GlyphLayout lay = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
        lay.setText(font, name);

        font.setColor(1f, 1f, 1f, 1f);
        font.getCache().clear();
        font.getCache().addText(name, x + width / 2f - lay.width / 2f, y + height / 2f + lay.height / 2f + 1);
        font.getData().setScale(1);

        font.getCache().draw(parentAlpha);

        Pools.free(lay);*/
    }
}
