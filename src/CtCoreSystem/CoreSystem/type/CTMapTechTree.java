package CtCoreSystem.CoreSystem.type;

import arc.struct.Seq;
import arc.util.Log;
import mindustry.content.TechTree;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Objectives;
import mindustry.type.ItemStack;


public class CTMapTechTree {


//科技树子父写法Type,水人提供代码
    //也是最初版  只增加了子父检查打印报告：32-41行  其他未动
    /*
     * @author abomb4 2022-12-27 08:29:39
     */


    /**
     * 加入现有科技树
     *
     * @param content      内容
     * @param parent       爹
     * @param requirements 物品需求
     * @param objectives   目标
     */
    public static void addMapToTree(UnlockableContent content, UnlockableContent parent, ItemStack[] requirements, Seq<Objectives.Objective> objectives) {

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


        TechTree.all.each(t -> t.content == content, TechTree.TechNode::remove);

        // find parent node
        TechTree.all.each(t -> t.content == parent, parentNode -> {
            final TechTree.TechNode node = new TechTree.TechNode(null, content,
                    requirements == null ? content.researchRequirements() : requirements);

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
    }

    /**
     * 加入现有科技树
     *
     * @param content      内容
     * @param parent       爹
     * @param requirements 物品需求
     */
    public static void addMapToTree(UnlockableContent content, UnlockableContent parent, ItemStack[] requirements) {
        addMapToTree(content, parent, requirements, null);
    }

    /**
     * 加入现有科技树
     *
     * @param content 内容
     * @param parent  爹
     */
    public static void addMapToTree(UnlockableContent content, UnlockableContent parent) {
        addMapToTree(content, parent, null, null);
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
        addMapToTree(phaseSpaceBridge, Blocks.phaseConveyor);

        // -=-=-=-=-=-=-=-=-=-=-=- After core -=-=-=-=-=-=-=-=-=-=-=-
        // factory line
        addMapToTree(DsBlocks.shardReceiver, Blocks.phaseWeaver, ItemStack.with(
                Items.silicon, 200 * 30,
                Items.thorium, 320 * 30,
                Items.phaseFabric, 330 * 30,
                Items.surgeAlloy, 100 * 30
        ));
        addMapToTree(spaceCrystallizer, dimensionTechnologyCore5, null,
                Seq.with(new Objectives.SectorComplete(dimensionFall)));

        // distribution line
        addMapToTree(hardPhaseSpaceBridge, phaseSpaceBridge);

        // unit line
        addMapToTree(beat, dimensionT4Reconstructor);
        addMapToTree(rhapsody, beat, null, Seq.with(
                new Objectives.Research(dimensionT5Reconstructor),
                new Objectives.SectorComplete(darkGuard),
                new Objectives.SectorComplete(thunderLightning)
        ));

        // zones
        addMapToTree(dimensionFall, SectorPresets.planetaryTerminal);
        addMapToTree(hardStuff, dimensionFall, null, Seq.with(new Objectives.SectorComplete(dimensionFall)));
    }*/


}
