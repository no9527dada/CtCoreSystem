package CtCoreSystem.mfxiao;

import arc.Core;
import arc.files.Fi;
import arc.input.KeyCode;
import arc.scene.ui.Dialog;
import arc.scene.ui.Label;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import arc.util.Log;
import arc.util.Threads;
import arc.util.Time;
import mindustry.Vars;
import mindustry.ui.dialogs.BaseDialog;

// 新增的导入，用于Base64编码
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import static CtCoreSystem.CtURL.爱发电;

public class ActivateProgram {
    // 许可证文件路径
    public static final Fi LICENSE_FILE = Core.settings.getDataDirectory().child("mods/creatorsdlc/license.dat");
    // 激活状态标志
    public static boolean isActivated = false;


    public static void startActivationProcess() {
        // 检查本地是否存在许可证文件
        if (LICENSE_FILE.exists()) {
            String licenseKey = LICENSE_FILE.readString().trim();
            if (!licenseKey.isEmpty()) {
                Log.info("找到本地许可证文件，正在同步验证...");
                String playerName = Vars.player.name();
                String playerUuid = Vars.player.uuid();

                boolean success = RSAEncryptionUtil.verifyLicense(playerName, playerUuid, licenseKey);

                if (success) {
                    Log.info("本地许可证验证成功！");
                    onActivationSuccess(); // 设置激活状态
                    return; // 验证成功，直接返回，不显示对话框
                } else {
                    Log.err("本地许可证验证失败或已失效。");
                }
            }
        }
//开屏展示
       // Log.info("需要用户激活。正在显示激活对话框...");
       // showActivationDialog("@activation.title");
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
                    // 显示激活成功的提示信息
//                    Vars.ui.showInfo("@activation.success");
                    showCustomDialog("", cont -> {
                        cont.margin(15);
                        Cell<Label> add = cont.add("@activation.success");
                        add.row();
                        add.width(400f).wrap().get().setAlignment(Align.center, Align.center);
                        cont.button("@restart", () -> {
                            Core.app.exit();
                        }).center().pad(16).width(200f).fill().row();
                    });

                } else {
                    Log.err("许可证验证失败。");
                    showActivationDialog("@activation.error.invalidkey");
                }
            });
        });
    }

    public static void showCustomDialog(String title, Consumer<Table> contBuilder) {
        new Dialog(title) {{
            // 让调用者自定义cont的内容
            contBuilder.accept(cont);
            // 添加默认的确定按钮
            buttons.button("@ok", this::hide).size(110, 50).pad(4);
            keyDown(KeyCode.enter, this::hide);
            closeOnBack();
        }}.show();
    }

    /**
     * 显示激活对话框
     *
     * @param message 对话框顶部显示的提示信息
     */
    public static void showActivationDialog(String message) {
        String QQ群2 = "https://jq.qq.com/?_wv=1027&k=oygqLbJ5";
        BaseDialog dialog = new BaseDialog("@activation.title");
        dialog.addCloseButton();

        // 提示信息
        dialog.cont.add(message).wrap().growX().pad(10).row();

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

        dialog.cont.add("[orange]步骤 3: 加入QQ群2获取激活码").padTop(10).left().row();
        dialog. cont.button(Core.bundle.format("QQ群2"), (() -> {
            if (!Core.app.openURI(QQ群2)) {
                Vars.ui.showErrorMessage("@linkfail");
                Core.app.setClipboardText(QQ群2);
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
        Log.info("激活状态已设置。");
    }
}