package CtCoreSystem.mfxiao;

import arc.Core;
import arc.files.Fi;
import arc.scene.ui.Dialog;
import arc.scene.ui.Label;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Cell;
import arc.util.Align;
import arc.util.Log;
import arc.util.Threads;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.Tex;
import mindustry.ui.dialogs.BaseDialog;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

import static CtCoreSystem.CtCoreSystem.*;
import static CtCoreSystem.CtURL.爱发电;
import static CtCoreSystem.CtURL.赞助QQ群;

public class ActivateProgram {
    // 许可证文件路径
    public static final Fi LICENSE_FILE = Core.settings.getDataDirectory().child("mods/creatorsdlc/license.dat");
    // 激活状态标志
  public static boolean isActivated = false;
    //状态配置文件
    private static final Fi ACTIVATION_STATE_FILE = Core.settings.getDataDirectory().child("mods/creatorsdlc/activation_state.json");

    // 新增：初始化时加载保存的激活状态
    static {
        loadActivationState();
    }
    // 加载保存的激活状态
    private static void loadActivationState() {
        if (ACTIVATION_STATE_FILE.exists()) {
            try {
                String content = ACTIVATION_STATE_FILE.readString().trim();
                // 根据自定义字符串解析激活状态
                if (content.equals("pedt")) {
                    isActivated = true;
                    Log.info("已加载保存的激活状态：已激活");
                } else if (content.equals("oghf")) {
                    isActivated = false;
                    Log.info("已加载保存的激活状态：未激活");
                } else {
                    // 如果文件内容不是预期的自定义字符串，则视为未激活
                    isActivated = false;
                    Log.info("激活状态文件内容格式不正确，使用默认状态");
                }
            } catch (Exception e) {
                Log.err("加载激活状态失败：" + e.getMessage());
                isActivated = false;
            }
        } else {
            Log.info("未找到激活状态文件，使用默认状态");
        }
    }

    //保存激活状态到文件
    public static void saveActivationState() {
        try {
            // 确保目录存在
            ACTIVATION_STATE_FILE.parent().mkdirs();
            // 保存当前激活状态，使用自定义字符串代替直接的true/false
            String encodedState = isActivated ? "pedt" : "oghf";
            ACTIVATION_STATE_FILE.writeString(encodedState, false);
            String encodedState2 = isActivated ? "1" : "0";
            Log.info("激活状态已保存：" + encodedState2);
        } catch (Exception e) {
            Log.err("保存激活状态失败：" + e.getMessage());
        }
    }

    public static void startActivationProcess() {
        try {
            // 检查本地是否存在许可证文件

            // 移除内部对LICENSE_FILE存在性的检查，因为外部调用前已经检查过
            // 直接获取许可证密钥进行验证
            String licenseKey = LICENSE_FILE.readString().trim();
            if (!licenseKey.isEmpty()) {
                Log.info("找到本地许可证文件，正在同步验证...");
                String playerName = Vars.player.name();
                String playerUuid = Vars.player.uuid();

                boolean success = RSAEncryptionUtil.verifyLicense(playerName, playerUuid, licenseKey);

                if (success) {
                    Log.info("本地许可证验证成功！");
                    isActivated = true;
                    saveActivationState();
                } else {
                    Log.err("本地许可证验证失败.可能原因：激活码已失效或注册名称已被封禁");
                    isActivated = false;
                    主动关闭激活 = false;
                    saveActivationState();
                    PopUpWindow2("", cont -> {
                        cont.add(Core.bundle.format("activation.error.localfileinvalid")).row();
                    });
                }
            } else {
                Log.info("许可证文件存在但内容为空");
                isActivated = false;
                主动关闭激活 = false;
                saveActivationState();
            }
        } catch (Throwable t) {
            // 检查是否是Jackson Core相关的类加载错误
            if (t instanceof NoClassDefFoundError && t.getMessage() != null &&
                    (t.getMessage().contains("com/fasterxml/jackson/core") ||
                            t.getMessage().contains("com.fasterxml.jackson.core"))) {
                Log.err("检测到Jackson库缺失，跳过激活验证过程");
                isActivated = false;
                主动关闭激活 = false;
                saveActivationState();
                BaseDialog dialog = new BaseDialog("警告");
                dialog.cont.table(Tex.button, g -> {
                    g.defaults().size(280, 160).left();
                    g.add(Core.bundle.format("activation.error.info")).align(Align.center).row();
                    g.button("@wait", (dialog::hide)).size(100, 64).center();//关闭按钮
                    // 新增：添加一个重置按钮，点击后删除激活文件重置状态
                    g.button("@delete", () -> {
                        // 删除激活文件和许可证文件
                        ACTIVATION_STATE_FILE.delete();
                        LICENSE_FILE.delete();
                        dialog.hide();
                        PopUpWindow2("", cont -> {
                            cont.add(Core.bundle.format("activation.error.info2")).row();
                        },3);
                    }).size(100, 64).center();//重置按钮
                });
                dialog.show();
            } else {
                // 处理其他类型的错误
                Log.err("激活过程中发生错误: " + t.getMessage());
                t.printStackTrace();
                isActivated = false;
                主动关闭激活 = false;
                saveActivationState();
            }
        }
    }
    /**
     * 验证许可证 (用于用户手动激活)
     *
     * @param licenseKey 用户提供的许可证密钥
     */
    private static void verifyLicenseAsync(String licenseKey) {
        Dialog processing = new Dialog("");
        processing.cont.add("@loading").pad(20);
        processing.setModal(true);
        processing.show();

        // 在后台线程中执行网络请求和密码学验证，避免阻塞UI
        Threads.daemon("License-Verification", () -> {
            String playerName = Vars.player.name();
            String playerUuid = Vars.player.uuid();
            boolean success = RSAEncryptionUtil.verifyLicense(playerName, playerUuid, licenseKey);

            // 验证完成后，回到主线程更新UI
            Core.app.post(() -> {
                processing.hide(); // 隐藏“处理中”对话框
                if (success) {
                    Log.info("许可证验证成功！");
                    // 如果验证成功，将许可证密钥保存到本地文件
                    if (!LICENSE_FILE.exists() || !LICENSE_FILE.readString().trim().equals(licenseKey)) {
                        LICENSE_FILE.writeString(licenseKey, false);
                        Log.info("许可证已保存至本地。");
                    }
                    onActivationSuccess(); // 调用激活成功地回调
                   // isActivated = true;
                    saveActivationState(); // 新增：保存激活状态
                    // 显示激活成功的提示信息
                    showCustomDialog("", cont -> {
                        cont.margin(15);
                        cont.add("@activation.success").width(400f).wrap().get().setAlignment(Align.center, Align.center);
                    });

                } else {
                    Log.err("许可证验证失败。");
                    isActivated = false;
                    saveActivationState(); // 新增：保存激活状态
                    showActivationDialog("@activation.error.invalidkey");
                }
            });
        });
    }



    /**
     * 显示激活对话框
     *
     * @param message 对话框顶部显示的提示信息
     */
    public static void showActivationDialog(String message) {
        // 添加调用追踪日志
        Log.info("showActivationDialog called with message: " + message);

        String QQ群2 = "https://jq.qq.com/?_wv=1027&k=oygqLbJ5";
        BaseDialog dialog = new BaseDialog("@activation.title");
        dialog.addCloseButton();

        // 提示信息
        dialog.cont.add(message).wrap().growX().pad(10).row();
        dialog.cont.add(Core.bundle.format("activation.io")).center().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.center).row();
        // --- 步骤一：获取购买信息 ---
        dialog.cont.add("[orange]步骤 1: 前往赞助获取订单编号").padTop(10).left().row();
        dialog.cont.button("前往赞助", (() -> {
            if (!Core.app.openURI(爱发电)) {
                Vars.ui.showErrorMessage("@linkfail");
                Core.app.setClipboardText(爱发电);
            }
        })).width(220f).pad(4).growX().left().row();

        dialog.cont.add("[orange]步骤 2: 获取并复制您的购买信息发送给开发者进行注册").padTop(10).left().row();
        dialog.cont.add(new Label("你的名称: " + Vars.player.name)).pad(5).left().row();
      //  dialog.cont.add(new Label("你的UUID: " + Vars.player.uuid())).pad(5).left().row();

        TextField orderIdField = new TextField();
        orderIdField.setMessageText("在此输入你的订单编号");
        dialog.cont.add(orderIdField).growX().pad(5).row();
        dialog.cont.button("复制购买信息", () -> {
            String orderId = orderIdField.getText().trim();
            if (orderId.isEmpty()) {
                Vars.ui.showInfo("@activation.error.noorderid");
                return;
            }

            // 获取玩家信息
            String playerName = Vars.player.name;
            String playerUuid = Vars.player.uuid();

            // 组合信息
            String requestData = playerName + ":" + playerUuid + ":" + orderId;

            // 使用 Java 内置的 Base64 编码器
            String base64Request = Base64.getEncoder().encodeToString(requestData.getBytes(StandardCharsets.UTF_8));

            // 复制到剪贴板
            Core.app.setClipboardText(base64Request);
            Vars.ui.showInfo("@copied");

        }).width(220f).pad(4).growX().left().row();

        dialog.cont.add("[orange]步骤 3: 加入QQ群获取激活码").padTop(10).left().row();
        dialog. cont.button(Core.bundle.format("QQ群2"), (() -> {
            if (!Core.app.openURI(赞助QQ群)) {
                Vars.ui.showErrorMessage("@linkfail");
                Core.app.setClipboardText(赞助QQ群);
            }
        })).update(b -> b.color.fromHsv(Time.time % 360, 1, 1)).size(250.0f, 50).padTop(10).left().row();

        dialog.cont.add("[orange]步骤 4: 粘贴开发者给你的激活码并激活").padTop(10).left().row();
        TextField keyField = new TextField();
        keyField.setMessageText("在此粘贴激活码");
        dialog.cont.add(keyField).growX().pad(5).row();

        dialog.cont.button("激活", () -> {
            String key = keyField.getText().trim();
            if (!key.isEmpty()) {
                dialog.hide();
                verifyLicenseAsync(key);
            } else {
                Vars.ui.showInfo("@activation.error.nokey");
            }
        }).width(220f).pad(4).growX().left().row();
        dialog.setModal(true);

        dialog.show();
    }
    /**
     * 激活成功后的回调函数
     */
    public static void onActivationSuccess() {
        if (isActivated) return; // 防止重复执行
        isActivated = true;
        saveActivationState(); // 新增：保存激活状态
        Log.info("激活状态已设置。");
    }
}