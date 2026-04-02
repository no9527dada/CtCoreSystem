#define HIGHP

uniform sampler2D u_texture;

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;

varying vec2 v_texCoords;

const float mscl = 40.0;
const float mth = 7.0;

void main(){
    vec2 c = v_texCoords;

    // 移除与分辨率相关的计算，直接使用纹理坐标进行波纹计算
    float stime = u_time / 5.0;

    // 使用固定比例的偏移量，不再依赖分辨率
    vec4 sampled = texture2D(u_texture, c + vec2(sin(stime/3.0 + c.y * 10.0) * 0.002, 0.0));
    vec3 color = sampled.rgb * vec3(0.9, 0.9, 1);

    // 使用纹理坐标代替屏幕空间坐标计算波纹效果
    float tester = mod((c.x * 20.0 + c.y * 22.0 + sin(stime / 8.0 + c.x * 4.0 - c.y * 0.2) * 2.0) +
                           sin(stime / 20.0 + c.y * 6.0) * 1.0 +
                           sin(stime / 10.0 - c.y * 4.0) * 2.0 +
                           sin(stime / 7.0 + c.y * 2.0) * 0.5 +
                           sin(c.x * 0.67 + c.y) +
                           sin(stime / 20.0 + c.x * 0.5) * 1.0, mscl);

    if(tester < mth){
        color *= 1.2;
    }

    gl_FragColor = vec4(color.rgb, min(sampled.a * 100.0, 1.0));
}