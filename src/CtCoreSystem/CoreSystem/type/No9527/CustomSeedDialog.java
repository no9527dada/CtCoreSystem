package CtCoreSystem.CoreSystem.type.No9527;

import arc.Core;
import arc.graphics.Color;
import arc.scene.Element;
import arc.scene.ui.Dialog;
import arc.scene.ui.TextButton;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import arc.util.Log;
import mindustry.ui.Fonts;
import mindustry.ui.dialogs.SettingsMenuDialog;

import java.util.Iterator;

import static CtCoreSystem.CoreSystem.type.CTColor.灰色;
import static CtCoreSystem.CoreSystem.type.CTColor.黄色;
import static CtCoreSystem.CtCoreSystem.*;

/**
 * 星球种子设置 2/2
 * 自定义种子设置对话框
 */
public class CustomSeedDialog extends Dialog {
    private TextField seedField;
    private TextField baseSeedField;
    private TextButton saveButton;
    private TextButton cancelButton;
    private arc.scene.ui.Label defaultSeedLabel;

    public CustomSeedDialog() {
        super(Core.bundle.get("customSeed.title", "自定义种子设置"));
        setupUI();
        loadCurrentSeeds();
        show();
    }
    private Element 设置确认() {
        //用于更新自定义种子确认的设置按钮
        SettingsMenuDialog.SettingsTable st= new SettingsMenuDialog.SettingsTable(){
            public void rebuild() {
                this.clearChildren();
                Iterator var1 = this.list.iterator();
                while(var1.hasNext()) {
                    Setting setting = (Setting)var1.next();
                    setting.add(this);
                }
            }
        };
       // st.add(Core.bundle.format("ct2-seedTXT")).left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left).row();
        st.checkPref("是否自定义种子", false, e -> {
            是否自定义种子 = !是否自定义种子;
            Core.settings.put("是否自定义种子", 是否自定义种子);
            showCustomDialog("", cont -> {
                cont.margin(15);
            });
        });
        st.row();

        return st;
    }
    private void setupUI() {
       // cont.add(设置确认()).row();
        cont.image().color(Color.valueOf("69dcee")).fillX().height(3).pad(3).row();
        cont.add().height(10f).row();// 空行

        Table table = new Table();
        table.defaults().pad(10f);
        // 标题
        table.add(Core.bundle.get("customSeed.header")).colspan(2).center().row();
        table.add().height(3f).row(); // 空行

        // Seed 输入框
        table.add(Core.bundle.get("customSeed.seed", "Seed 值:")).right().padRight(10f);
        seedField = new TextField("");
        seedField.setMessageText("请输入 Seed 值");
        seedField.setMaxLength(6);
        table.add(seedField).left().width(200f).row();

        // BaseSeed 输入框
        table.add(Core.bundle.get("customSeed.baseSeed", "BaseSeed 值:")).right().padRight(10f);
        baseSeedField = new TextField("");
        baseSeedField.setMessageText("请输入 BaseSeed 值");
        baseSeedField.setMaxLength(6);
        table.add(baseSeedField).left().width(200f).row();
        table.add().height(20f).row(); // 空行

        // 是否自定义种子切换按钮
        TextButton toggleButton = new TextButton("");
        updateToggleButton(toggleButton);
        toggleButton.clicked(() -> {
            是否自定义种子 = !是否自定义种子;
            Core.settings.put("是否自定义种子", 是否自定义种子);
            updateToggleButton(toggleButton);
        });
        table.add(toggleButton).colspan(2).center().width(200f).row();

        table.add().height(10f).row(); // 空行

        // 显示随机种子值标签
        Table infoTable = new Table();
        infoTable.defaults().center();
        if (!是否自定义种子) {
            infoTable.add(Core.bundle.get("customSeed.showDefault")).center().row();
        }else {
            infoTable.add(Core.bundle.get("customSeed.showCustom")).center().row();
        }
        defaultSeedLabel = new arc.scene.ui.Label("", new arc.scene.ui.Label.LabelStyle(Fonts.def, 黄色));
        defaultSeedLabel.setWrap(true);
        defaultSeedLabel.setAlignment(Align.center);
        infoTable.add(defaultSeedLabel).growX().pad(10f).width(400f);
        table.add(infoTable).colspan(2).center().row();

        // 按钮区域
        Table buttonTable = new Table();
         // 保存按钮
        saveButton = new TextButton(Core.bundle.get("customSeed.save"));
        // 先执行保存种子的逻辑
        saveButton.clicked(this::saveSeeds);
        buttonTable.add(saveButton).padRight(10f);
        
        // 取消按钮
        cancelButton = new TextButton(Core.bundle.get("customSeed.cancel"));
        cancelButton.clicked(this::hide);
        buttonTable.add(cancelButton);
        
        table.add(buttonTable).colspan(2).center();
        
        // 添加到对话框
        cont.add(table);
        
        // 设置对话框大小
        pack();
        setFillParent(false);
    }
    /**
     * 更新切换按钮文本
     */
    private void updateToggleButton(TextButton button) {
        if (是否自定义种子) {
            button.setText(Core.bundle.get("customSeed.customEnabled", "[green]已启用自定义种子[]"));
        } else {
            button.setText(Core.bundle.get("customSeed.customDisabled", "[scarlet]使用随机种子[]"));
        }
    }
    /**
     * 更新输入框状态
     */
    private void updateInputFieldsState() {
        boolean editable = 是否自定义种子;
        seedField.setDisabled(!editable);
        baseSeedField.setDisabled(!editable);

        // 设置视觉提示
        if (!editable) {
            seedField.setColor(灰色);
            baseSeedField.setColor(灰色);
        } else {
            seedField.setColor(arc.graphics.Color.white);
            baseSeedField.setColor(arc.graphics.Color.white);
        }
    }
    /**
     * 更新默认种子值标签显示
     */
    private void updateDefaultSeedLabel() {
        if (!是否自定义种子) {
            defaultSeedLabel.setText(Core.bundle.format("customSeed.defaultValues",
                    DEFAULT_SEED, DEFAULT_BASE_SEED));
           // defaultSeedLabel.setColor(黄色);
        } else {
            defaultSeedLabel.setText(Core.bundle.format("customSeed.usingCustom",
                    SeedManager.loadSeeds().seed,
                    SeedManager.loadSeeds().baseSeed
            ));
        }
    }
    /**
     * 加载当前保存的种子值到输入框
     */
    private void loadCurrentSeeds() {
        try {
            SeedManager.SeedData currentSeeds = SeedManager.loadSeeds();
            seedField.setText(String.valueOf(currentSeeds.seed));
            baseSeedField.setText(String.valueOf(currentSeeds.baseSeed));
           // updateInputFieldsState();
           updateDefaultSeedLabel();
        } catch (Exception e) {
            Log.err("加载种子值失败：" + e.getMessage());
            // 设置默认值
            seedField.setText("0");
            baseSeedField.setText("0");
           //updateInputFieldsState();
           //updateDefaultSeedLabel();
        }
    }

    /**
     * 保存种子值
     */
    private void saveSeeds() {
        try {
            // 获取输入值
            String seedStr = seedField.getText().trim();
            String baseSeedStr = baseSeedField.getText().trim();

            // 验证输入
            if (seedStr.isEmpty() || baseSeedStr.isEmpty()) {
                showInfo(Core.bundle.get("customSeed.error.empty", "请输入完整的种子值"));
                return;
            }

            // 验证是否为纯数字
            if (!seedStr.matches("\\d+") || !baseSeedStr.matches("\\d+")) {
                showInfo(Core.bundle.get("customSeed.error.number", "请输入有效的数字"));
                return;
            }

            int seed = Integer.parseInt(seedStr);
            int baseSeed = Integer.parseInt(baseSeedStr);

            // 验证范围
            if (!SeedManager.isValidSeed(seed) || !SeedManager.isValidSeed(baseSeed)) {
                showInfo(Core.bundle.get("customSeed.error.invalid", "种子值必须在0-999999范围内"));
                return;
            }

            // 保存到文件
            SeedManager.saveSeeds(seed, baseSeed);

            // 显示成功消息
            Core.app.post(() -> {
                hide();
                // 然后显示自定义对话框
                showCustomDialog(Core.bundle.get("ct2-seedTXT"), cont -> {
                    cont.margin(15);
                });
               // showInfo( Core.bundle.get("customSeed.success", "种子设置已保存"));
            });
            
        } catch (NumberFormatException e) {
            showInfo(Core.bundle.get("customSeed.error.number", "请输入有效的数字"));
        } catch (Exception e) {
            Log.err("保存种子失败: " + e.getMessage());
            showInfo(Core.bundle.get("customSeed.error.save", "保存失败，请重试"));
        }
    }
    
    /**
     * 显示信息
     */
    private void showInfo(String message) {
        PopUpWindow("", cont -> {
            cont.add(message);
        });
    }
    
    /**
     * 显示对话框
     */
    @Override
    public Dialog show() {
        loadCurrentSeeds(); // 每次显示时重新加载当前值
        return super.show();
    }
}
