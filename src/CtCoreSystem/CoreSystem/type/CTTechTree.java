package CtCoreSystem.CoreSystem.type;

import arc.struct.Seq;
import arc.util.Log;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.TechTree;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Objectives;
import mindustry.type.ItemStack;
import mindustry.type.SectorPreset;
import mindustry.world.Block;

import java.lang.reflect.Method;

import static CtCoreSystem.content.ItemX.物品;


public class CTTechTree {


//科技树子父写法Type,水人提供代码
    /*
     * @author abomb4 2022-12-27 08:29:39
     *
     * @author NO:9527  2025-11-02 18:24:15  完善科技树代码
     */

    // 记录父节点是否找到的变量
    private static boolean parentFound = false;

    /**
     * 加入现有科技树
     *
     * @param content      内容
     * @param parent       父节点
     * @param requirements 物品需求
     * @param objectives   目标
     * @param ItemMultiplier 物品倍率
     */
    public static void addToTree(UnlockableContent content, UnlockableContent parent, ItemStack[] requirements,Seq<Objectives.Objective> objectives, float ItemMultiplier) {
        // 检查content是否为null
        if (content == null) {
            Log.err("尝试将空内容添加到科技树，父节点: " + (parent != null ? parent.name : "父节点也为空！你可真TM牛逼！！"));
            return;
        }

        // 检查父节点是否为null
        if (parent == null) {
            Log.err("尝试将内容:" + content.name + " 添加到科技树，但父节点为空值！" );
            return;
        }

       /* // 检查被研究的内容是否为null
        if (objectives == null) {
            Log.err("尝试将子内容:" + content.name + " 添加到科技树，但研究或占领需求项目为空值，无法完成目标");
            return ;
        }*/
        // 移除已存在的相同内容节点
        TechTree.all.each(t -> t.content == content, TechTree.TechNode::remove);

        // 重置父节点找到标记
        parentFound = false;

        // 查找父节点
        TechTree.all.each(t -> t.content == parent, parentNode -> {
            parentFound = true;

            ItemStack[] finalRequirements;

            if (requirements != null) {
                // 使用自定义需求
                finalRequirements = requirements;
            } else {
             /*  // 尝试从建筑获取建造资源并计算研究需求
                finalRequirements = ItemStack.empty;

                */
                //1.建筑科技树需求
                if (content instanceof Block) {
                    Block block = (Block) content;
                    // 获取建筑的建造资源
                    ItemStack[] buildCost = block.requirements;

                    // 检查buildCost是否为空或长度为0
                    if (buildCost == null || buildCost.length == 0) {
                        // 如果建造资源为空，使用默认需求
                        finalRequirements = ItemStack.with(
                                物品, 100
                        );
                    } else {
                        // 创建研究需求数组
                        finalRequirements = new ItemStack[buildCost.length];
                        for (int i = 0; i < buildCost.length; i++) {
                            // 计算研究需求数量：确保结果为整数且至少为1
                            int researchAmount = Math.max(1, Math.round(buildCost[i].amount / 4f * 210 * ItemMultiplier));
                            finalRequirements[i] = new ItemStack(buildCost[i].item, researchAmount);
                        }
                    }
                }else {
                    //2.地图科技树需求
                    finalRequirements = content.researchRequirements();
                }
            }

            final TechTree.TechNode node = new TechTree.TechNode(null, content, finalRequirements);

            if (objectives != null) {
                node.objectives.addAll(objectives);
            }

            if (node.parent != null) {
                node.parent.children.remove(node);
            }

            if (!parentNode.children.contains(node)) {
                parentNode.children.add(node);
            }
            node.parent = parentNode;
        });

        // 如果父节点未找到，输出错误信息而不是闪退
        if (!parentFound) {
            Log.err("在科技树中找不到父节点: " + parent.name + ", 无法添加内容: " + content.name);
            // 选择一个默认父节点作为后备选项
            addToDefaultTree(content, parent);
        }
    }

    /**
     * 添加到默认父节点的后备方法
     */
    private static void addToDefaultTree(UnlockableContent content, UnlockableContent parent) {
        // 这里实现一个后备机制，将节点添加到默认父节点
        Log.info("尝试将内容:" + content.name + " 添加到默认父节点: " + Blocks.coreShard.name);
        addToTree(content, Blocks.coreShard, null, null,1);
    }


    /**
     * 加入现有科技树
     *
     * @param content      内容
     * @param parent       爹
     * @param requirements 物品需求
     * @param objectives 研究目标或占领地图
     */
    public static void addToTree(UnlockableContent content, UnlockableContent parent, ItemStack[] requirements,Seq<Objectives.Objective> objectives) {
        addToTree(content, parent, requirements, objectives,1);
    }
    /**
     * 加入现有科技树
     *
     * @param content      内容
     * @param parent       爹
     * @param requirements 物品需求
     */
    public static void addToTree(UnlockableContent content, UnlockableContent parent, ItemStack[] requirements) {
        addToTree(content, parent, requirements, null,1);
    }

    /**
     * 加入现有科技树
     *
     * @param content 内容
     * @param parent  爹
     */
    public static void addToTree(UnlockableContent content, UnlockableContent parent) {
        addToTree(content, parent, null, null,1);
    }








/*    public static void load() {
        // clone serpulo tree
        final TechTree.TechNode tmp = Planets.serpulo.techTree;
        SerpuloTechTree.load();
        final TechTree.TechNode newTree = TechTree.nodeRoot("wrek", dimensionTechnologyCore5, () -> {});

        newTree.planet = DsPlanets.wrek;
        newTree.planet.techTree = newTree;
        Events.on(EventType.ClientLoadEvent.class, ev ->
                newTree.icon = new TextureRegionDrawable(dimensionTechnologyCore5.uiIcon));

        // -=-=-=-=-=-=-=-=-=-=-=- No core needed -=-=-=-=-=-=-=-=-=-=-=-
        addToTree(phaseSpaceBridge, Blocks.phaseConveyor);

        // -=-=-=-=-=-=-=-=-=-=-=- After core -=-=-=-=-=-=-=-=-=-=-=-
        // factory line
        addToTree(DsBlocks.shardReceiver, Blocks.phaseWeaver, ItemStack.with(
                Items.silicon, 200 * 30,
                Items.thorium, 320 * 30,
                Items.phaseFabric, 330 * 30,
                Items.surgeAlloy, 100 * 30
        ));
        addToTree(spaceCrystallizer, dimensionTechnologyCore5, null,
                Seq.with(new Objectives.SectorComplete(dimensionFall)));

        // distribution line
        addToTree(hardPhaseSpaceBridge, phaseSpaceBridge);

        // unit line
        addToTree(beat, dimensionT4Reconstructor);
        addToTree(rhapsody, beat, null, Seq.with(
                new Objectives.Research(dimensionT5Reconstructor),
                new Objectives.SectorComplete(darkGuard),
                new Objectives.SectorComplete(thunderLightning)
        ));

        // zones
        addToTree(dimensionFall, SectorPresets.planetaryTerminal);
        addToTree(hardStuff, dimensionFall, null, Seq.with(new Objectives.SectorComplete(dimensionFall)));
    }*/


}
