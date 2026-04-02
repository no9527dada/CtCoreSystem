            /*

            修饰符	同一类	同一包	不同包子类	不同包非子类
            private	√	×	×	×
            protected	√	√	√	×
            default(无修饰符)	√	√	×	×
            public	√	√	√	√

         此件记录一些常用的方法

            Math.floor()（向下取整）
            Math.ceil()（向上取整）
            Math.round()通常是最合理的选择，值进行四舍五入。
            e.fout() 表示获取当前进度的值 1~0
            e.fin() 表示获取当前进度 0~1

            特效部分：
            Draw.z(层级);
            Lines.circle(float x, float y, float radius) - 绘制一个普通的空心圆圈
            Fill.circle(float x, float y, float radius) - 绘制一个填充的圆圈
            Lines.stroke(1.5f); // 设置线条绘制时的宽度
            Draw.alpha(1f);//设置透明度
            Lines.lineAngle(x, y, angle, length); // 绘制一条从(x, y)开始，角度为angle，长度为length的线
            Lines.stroke(1.5f); // 设置线条绘制时的宽度
            Lines.line(x1, y1, x2, y2); // 绘制一条从(x1, y1)到(x2, y2)的线

            Lines.rect(float x, float y, float width, float height, float rotation) - 绘制一个普通的空心矩形
            Fill.rect(float x, float y, float width, float height, float rotation) - 绘制一个填充的矩形
            Draw.rect(Core.atlas.find("贴图文件名字"),x, y, width, height, rotation); // 绘制一个有贴图的效果






























    */