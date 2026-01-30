package CtCoreSystem.CoreSystem.type.No9527;

import mindustry.world.blocks.distribution.ArmoredConveyor;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.distribution.StackConveyor;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.SolidPump;
import mindustry.world.blocks.storage.Unloader;

import static CtCoreSystem.CtCoreSystem.方块贴图;

public class 建筑贴图隐藏 {
    public static class newUnloader extends Unloader {
        public newUnloader(String name) {
            super(name);
            buildType = Build::new;
        }
        public class Build extends UnloaderBuild {
            public void draw() {
                if (方块贴图 == true) {
                    super.draw();
                } else {
                    // 使用工具类渲染方块文字
                    BlockTextRenderer.renderBlockText(x, y, localizedName, block.size);
                }
            }
        }
    }
    public static class newConveyor extends Conveyor {
        public newConveyor(String name) {
            super(name);
            buildType = Build::new;
        }

        public class Build extends Conveyor.ConveyorBuild {
            public void draw() {
                if (方块贴图 == true) {
                    super.draw();
                } else {
                    // 使用工具类渲染方块文字
                    BlockTextRenderer.renderBlockText(x, y, localizedName, block.size);
                }
            }
        }
    }

    public static class newArmoredConveyor extends ArmoredConveyor {
        public newArmoredConveyor(String name) {
            super(name);
            buildType = Build::new;
        }

        public class Build extends ArmoredConveyor.ArmoredConveyorBuild {
            public void draw() {
                if (方块贴图 == true) {
                    super.draw();
                } else {
                    // 使用工具类渲染方块文字
                    BlockTextRenderer.renderBlockText(x, y, localizedName, block.size);
                }
            }
        }
    }

    public static class newStackConveyor extends StackConveyor {
        public newStackConveyor(String name) {
            super(name);
            buildType = Build::new;
        }

        public class Build extends StackConveyorBuild {
            public void draw() {
                if (方块贴图 == true) {
                    super.draw();
                } else {
                    // 使用工具类渲染方块文字
                    BlockTextRenderer.renderBlockText(x, y, localizedName, block.size);
                }
            }
        }
    }

    public static class newSolidPump extends SolidPump {
        public newSolidPump(String name) {
            super(name);
            buildType = Build::new;
        }
        public class Build extends SolidPump.SolidPumpBuild {
            public void draw() {
                if (方块贴图 == true) {
                    super.draw();
                } else {
                    // 使用工具类渲染方块文字
                    BlockTextRenderer.renderBlockText(x, y, localizedName, block.size);
                }
            }
        }
    }

    public static class newDrill extends Drill {
        public newDrill(String name) {
            super(name);
            buildType = Build::new;
        }

        public class Build extends DrillBuild {
            public void draw() {
                if (方块贴图 == true) {
                    super.draw();
                } else {
                    // 使用工具类渲染方块文字
                    BlockTextRenderer.renderBlockText(x, y, localizedName, block.size);
                }
            }
        }
    }

    public static class newGenericCrafter extends GenericCrafter {
        public newGenericCrafter(String name) {
            super(name);
            buildType = newGenericCrafterBuild::new;
        }

        /*    @Override
            public boolean displayShadow(Tile tile) {
                return 方块贴图;
            }*/
        public class newGenericCrafterBuild extends GenericCrafterBuild {
            /*       private boolean lastShadowState = 方块贴图;

                   @Override
                   public void updateTile() {
                       super.updateTile();

                       // 检测阴影状态变化
                       if (lastShadowState != 方块贴图) {
                           lastShadowState = 方块贴图;
                           // 手动更新阴影
                           Vars.renderer.blocks.updateShadow(this);
                           displayShadow(tile);

                       }
                   }*/
            public void draw() {
                if (方块贴图 == true) {
                    drawer.draw(this);
                } else {
                    // 使用工具类渲染方块文字
                    BlockTextRenderer.renderBlockText(x, y, localizedName, block.size);
                }
                ;
            }

        }
    }
}