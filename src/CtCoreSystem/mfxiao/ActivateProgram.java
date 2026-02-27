package CtCoreSystem.mfxiao;

import arc.Core;
import arc.files.Fi;
import arc.graphics.Color;
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
import mindustry.ui.dialogs.JoinDialog;

import java.nio.file.Files;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static CtCoreSystem.CtCoreSystem.*;
import static CtCoreSystem.CtCoreSystem.toText;
import static CtCoreSystem.CtURL.爱发电;
import static CtCoreSystem.CtURL.赞助QQ群;


public class ActivateProgram {
    // 许可证文件路径
    public static final Fi LICENSE_FILE = Core.settings.getDataDirectory().child("mods/creatorsdlc/license.dat");
    // 激活状态标志
    public static boolean isActivated = false;
    //状态配置文件
    private static final Fi ACTIVATION_STATE_FILE = Core.settings.getDataDirectory().child("mods/creatorsdlc/activation_state.json");
    // 保存初始激活状态
    private static boolean initialActivationState = false;
    // 新增：标记当前是否为移动设备
  //  private static boolean isMobileDevice = false;

    static {

        // 初始化时加载保存的激活状态
        loadActivationState();
        // 如果已保存了订单信息，尝试自动验证
      /*  if ( LICENSE_FILE.exists()) {
            try {
                String content = LICENSE_FILE.readString().trim();
                if (!content.isEmpty() && content.contains(":")) {
                    Log.info("检测到且有保存的订单信息，尝试自动验证");
                    String[] parts = content.split(":");
                    if (parts.length >= 2) {
                        String savedPlayerName = parts[0];
                        String savedOrderId = parts[1];
                        verifyMobileLicenseAsync(savedPlayerName, savedOrderId);
                    }
                }
            } catch (Exception e) {
                Log.err("读取保存的订单信息失败: " + e.getMessage());
            }
        }*/
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
        // 保存初始激活状态
        initialActivationState = isActivated;
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
    // 检查激活状态变化的方法
    private static void checkActivationStateChange() {
        // 只有当初始状态和当前状态不同时才显示重启提示
        if (initialActivationState != isActivated) {

            if(isActivated==true){
                Log.info("激活状态已改变，需要重启游戏以应用变更");
                Core.app.post(() -> showCustomDialog("", cont -> {
                    cont.add(Core.bundle.format("activation.error.StateChange1")).row();
                }));
            }else {
                Log.info("激活状态已改变，需要重启游戏以应用变更");
                Core.app.post(() -> showCoercivenessExitDialog("", cont -> {
                    cont.add(Core.bundle.format("activation.error.StateChange0")).row();
                }));
            }


        }
    }
    //有激活文件会联网进行验证 在CtCoreSystem类中使用
    public static void startActivationProcess() {
        try {
            if (LICENSE_FILE.exists()) {
                try {
                    String content = LICENSE_FILE.readString().trim();
                    if (content.contains(":")) {
                        String[] parts = content.split(":", 2);
                        // 加强格式验证：确保分割后有两个有效部分
                        if (parts.length == 2 && !parts[0].isEmpty() && !parts[1].isEmpty()) {
                            String savedPlayerName = parts[0];
                            String savedEncryptedOrderId = parts[1];

                            // 验证当前游戏名与本地存储名是否一致
                            String currentPlayerName = Vars.player.name();
                            if (!savedPlayerName.equals(currentPlayerName)) {
                              //  Log.err("玩家名不匹配：当前玩家名 '" + currentPlayerName + "' 与本地存储的玩家名 '" + savedPlayerName + "' 不符");
                                Log.err("玩家名未注册");
                                isActivated = false;
                                主动关闭激活 = false;
                                saveActivationState();
                                // 检查激活状态是否发生变化


                                PopUpWindow2("", cont -> {
                                    cont.add(Core.bundle.format("activation.error.playernamechanged")).row();
                                });
                                checkActivationStateChange();
                                // 删除本地存储的许可证文件
                                LICENSE_FILE.delete();
                                return;
                            }
                            try {
                                // 解密订单号
                                String savedOrderId = RSAEncryptionUtil.decryptOrderId(savedEncryptedOrderId);

                                // 使用解密后的订单号进行验证
                                verifyMobileLicenseAsync(savedPlayerName, savedOrderId);
                            } catch (Exception e) {
                                Log.err("读取加密订单号时出错: " + e.getMessage());
                                // 在解密失败时立即设置激活状态为false并保存
                                isActivated = false;
                                主动关闭激活 = false;
                                saveActivationState();
                                PopUpWindow2("", cont -> {
                                    cont.add(Core.bundle.format("activation.error.localfileinvalid")).row();
                                });
                            }
                        } else {
                            Log.err("激活文件无效1");
                            isActivated = false;
                            主动关闭激活 = false;
                            saveActivationState();
                            showActivationDialog("@activation.error.invalidkey");
                            PopUpWindow2("", cont -> {
                                cont.add(Core.bundle.format("activation.error.localfileinvalid")).row();
                            });
                        }
                    } else {
                        Log.err("激活文件无效2");
                        isActivated = false;
                        主动关闭激活 = false;
                        saveActivationState();
                        showActivationDialog("@activation.error.invalidkey");
                        PopUpWindow2("", cont -> {
                            cont.add(Core.bundle.format("activation.error.localfileinvalid")).row();
                        });
                    }
                } catch (Exception e) {
                    Log.err("读取加密订单号时出错: " + e.getMessage());
                    isActivated = false;
                    主动关闭激活 = false;
                    saveActivationState();
                    PopUpWindow2("", cont -> {
                        cont.add(Core.bundle.format("activation.error.localfileinvalid")).row();
                    });
                }
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
                    g.button("@delete1", () -> {
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
    // 这是有激活文件后调用的方法  会联网进行验证
    // 修改verifyMobileLicenseAsync方法以处理解密失败返回null的情况
    private static void verifyMobileLicenseAsync(String playerName, String orderIds) {
        Dialog processing = new Dialog("");
        processing.cont.add(toText("activation.error.loading")).pad(20);
        processing.setModal(true);
        processing.show();

        // 在后台线程中执行网络请求
        Threads.daemon("Mobile-License-Verification", () -> {
            boolean success = false;

            try {
                // 设置超时时间
                Time.runTask(300f, () -> { // 300 ticks = 5秒超时
                    if (processing.isShown()) {
                        Core.app.post(processing::hide);
                    }
                });

                // 直接使用传入的订单号，不再进行解密
                String orderId = orderIds;
                if (orderId == null || orderId.isEmpty()) {
                    // 订单号为空
                    arc.util.Log.err("许可证验证失败：订单号为空");
                    success = false;
                } else {
                    // 直接调用RSAEncryptionUtil中的fetchValidationData方法获取验证数据
                    Map<String, String> validationData = RSAEncryptionUtil.fetchValidationData();

                    // 遍历所有键值对查找匹配的订单号和玩家名
                    for (Map.Entry<String, String> entry : validationData.entrySet()) {
                        if (entry.getValue().equals(orderId)) {
                            String key = entry.getKey(); // 形如 玩家名:[LOCAL]
                            int idx = key.indexOf(':');
                            String registeredPlayerName = (idx > -1) ? key.substring(0, idx) : key;

                            if (registeredPlayerName.equals(playerName)) {
                                success = true;
                               // arc.util.Log.info("许可证验证成功！订单号: " + orderId + ", 玩家名: " + playerName);
                                arc.util.Log.info("玩家名: " + playerName + ", 已成功验证登录！");
                                break;
                            }
                        }
                    }
                    if (!success) {
                        arc.util.Log.warn("许可证检查失败：玩家名已注销。");
                    }
                }
            } catch (Exception e) {
                String errorMsg = e.getMessage();
                if (errorMsg != null && errorMsg.contains("gitee.com")) {
                    //无网络连接时 本地文件验证成功  也被认为是成功
                    success = true;
                    arc.util.Log.warn("许可证验证过程中发生网络异常: 无法连接到验证服务器");
                } else {
                    arc.util.Log.err("许可证验证过程中发生异常: " + errorMsg);
                }
                e.printStackTrace();
            }
            // 验证完成后，回到主线程更新UI
            final boolean finalSuccess = success;
            Core.app.post(() -> {
                // 确保对话框被隐藏
                processing.hide();

                if (finalSuccess) {
                    isActivated = true;
                    saveActivationState();
                    arc.util.Log.info("赞助激活状态确认 " + ActivateProgram.isActivated);
                    // 检查激活状态是否发生变化
                    checkActivationStateChange();
                } else {
                    arc.util.Log.err("许可证验证失败。");
                    isActivated = false;
                    主动关闭激活 = false;
                    saveActivationState();
                    showActivationDialog("@activation.error.invalidkey");
                    PopUpWindow2("", cont -> {
                        cont.add(Core.bundle.format("activation.error.localfileinvalid")).row();
                    });
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
        BaseDialog dialog = new BaseDialog("@activation.title");
        dialog.addCloseButton();

        // 提示信息
        dialog.cont.add(message).wrap().growX().pad(10).row();

        dialog.cont.button(toText("activation.infoio"), (() -> {
            BaseDialog dialog2 = new BaseDialog("");
            dialog2. buttons.defaults().size(210, 64);//为 dialog2 对话框的按钮表格中后续添加的所有按钮设置默认尺寸。
            dialog2.cont.add(Core.bundle.format("activation.io")).center().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.center).row();
            dialog2.addCloseButton();// 同时添加可见按钮和返回键关闭功能
          // dialog2. addCloseListener();// 仅添加返回键关闭功能 没有按钮
           // dialog2. buttons.button("@close", (dialog2::hide)).size(100, 64);//有关闭按钮 没有返回键
            dialog2.show();
        })).width(220f).pad(4).growX().left().center().row();

        // --- 步骤一：获取购买信息 ---
        dialog.cont.add(toText("activation.info1")).padTop(10).left().row();
        dialog.cont.button(toText("activation.info2"), (() -> {
            if (!Core.app.openURI(爱发电)) {
                Vars.ui.showErrorMessage("@linkfail");
                Core.app.setClipboardText(爱发电);
            }
        })).width(220f).pad(4).growX().left().row();


        // --- 步骤二：输入订单号 ---
        TextField playerNameField = new TextField();
        playerNameField.setText(Vars.player.name());
        boolean hasError;
        String playerNamee = Vars.player.name();
        if(playerNamee == null || playerNamee.isEmpty()){
            dialog.cont.add(toText("activation.info3")).left().row();
            hasError = true;
            dialog.cont.button(toText("activation.info4"), (() -> {
                new JoinDialog().show();
                dialog.hide();
            })).width(220f).pad(4).growX().left().row();
        } else if(playerNamee.contains(" ")){
            dialog.cont.add(toText("activation.info5")).left().row();
            hasError = true;
            dialog.cont.button(toText("activation.info6"), (() -> {
                new JoinDialog().show();
                dialog.hide();
            })).width(220f).pad(4).growX().left().row();
        } else {
            hasError = false;
            dialog.cont.add(toText("activation.info7") + playerNamee).left().row();
        }
       // dialog.cont.add(playerNameField).growX().pad(5).row();

        TextField orderIdField = new TextField();
        orderIdField.setMessageText(toText("activation.info8"));
        dialog.cont.add(toText("activation.info9")).left().row();
        dialog.cont.add(orderIdField).growX().pad(5).row();
        dialog.row();
        dialog.cont.add(toText("activation.info10")).padTop(10).left().row();
        dialog.cont.button(toText("activation.info11"), () -> {
            if(hasError) {
                Vars.ui.showInfo(toText("activation.info12"));
                return;
            }
                    String playerName = playerNameField.getText().trim();
                    String orderId = orderIdField.getText().trim();
                    if (playerName.isEmpty() || orderId.isEmpty()) {
                        Vars.ui.showInfo("@activation.error.noorderid");
                    }else {
                        Core.app.setClipboardText("\""+playerName + ":[LOCAL]\": \"" + orderId+"\","); //
                        Vars.ui.showInfo("@ctcopied");
                    }
        }).width(220f).pad(4).growX().left().row();
        dialog.row();

        // --- 步骤三：加入QQ群获取激活码 ---
        dialog.cont.add(toText("activation.info13")).padTop(10).left().row();
        dialog.cont.button(toText("activation.info14"), (() -> {
            if (!Core.app.openURI(赞助QQ群)) {
                Vars.ui.showErrorMessage("@linkfail");
                Core.app.setClipboardText(赞助QQ群);
            }
        })).update(b -> b.color.fromHsv(Time.time % 360, 1, 1)).size(250.0f, 50).padTop(10).left().row();

        // --- 步骤四：粘贴开发者给你的激活码并激活 ---
        dialog.cont.add(toText("activation.info15")).padTop(10).left().row();

        // 密匙输入框
        TextField keyField = new TextField();
        keyField.setMessageText(toText("activation.info16"));
        dialog.cont.add(toText("activation.info17")).left().row();
        dialog.cont.add(keyField).growX().pad(5).row();

        dialog.cont.button(toText("activation.info18"), () -> {
            String playerName = playerNameField.getText().trim();
            String key = keyField.getText().trim();
            if (playerName.isEmpty() || key.isEmpty()) {
                Vars.ui.showInfo("@activation.error.nokey");
                return;
            }
            dialog.hide();
            // 修改：调用新的验证方法，传递游戏名称和密匙
            verifyMobileKeyAsync(playerName, key);
        }).width(220f).pad(4).growX().center().row();

        dialog.setModal(true);
        dialog.show();
    }

    // 这是玩家在填写激活码时调用的方法  会联网进行验证  如果验证成功  会在本地保存一个文件  文件名是玩家名  文件内容是订单号
    private static void verifyMobileKeyAsync(String playerName, String key) {
        Dialog processing = new Dialog("");
        processing.cont.add(toText("activation.error.loading")).pad(20);
        processing.setModal(true);
        processing.show();

        // 在后台线程中执行网络请求
        Threads.daemon("Mobile-Key-Verification", () -> {
            boolean success = false;
            try {
                // 设置超时时间
                Time.runTask(1800f, () -> { // 1800 ticks = 30秒超时
                    if (processing.isShown()) {
                        Core.app.post(processing::hide);
                        Core.app.post(() -> Vars.ui.showInfo("@activation.error.timeout"));
                    }
                });

                // 1. 直接调用RSAEncryptionUtil中的fetchValidationData方法获取验证数据
                Map<String, String> validationData = RSAEncryptionUtil.fetchValidationData();

                // 2. 解密密匙获取原始订单号
                String orderId = RSAEncryptionUtil.decryptOrderId(key);
               // Log.info("解密后的订单号: " + orderId);

                // 3. 检查订单号是否存在且对应的玩家名是否匹配
                // 遍历所有键值对查找匹配的订单号和玩家名
                for (Map.Entry<String, String> entry : validationData.entrySet()) {
                    if (entry.getValue().equals(orderId)) {
                        String storedKey = entry.getKey(); // 形如 玩家名:[LOCAL]
                        int idx = storedKey.indexOf(':');
                        String registeredPlayerName = (idx > -1) ? storedKey.substring(0, idx) : storedKey;

                        if (registeredPlayerName.equals(playerName)) {
                            success = true;
                           // Log.info("密匙验证成功！订单号: " + orderId + ", 玩家名: " + playerName);
                            break;
                        }
                    }
                }

                if (!success) {
                    // 添加验证失败的日志记录
                    Log.err("密匙验证失败: 未找到匹配的订单号和玩家名");
            /*        PopUpWindow("", cont -> {
                        cont.add(Core.bundle.format("activation.error.noUserName"));
                    });*/
                }
            } catch (Exception e) {
                String errorMsg = e.getMessage();
                if (errorMsg != null && errorMsg.contains("gitee.com")) {
                    arc.util.Log.err("许可证验证过程中发生网络异常: 无法连接到验证服务器");
                    //网络无连接
                    PopUpWindow("", cont -> {
                        cont.add(Core.bundle.format("activation.error.NetworkError"));
                    });
                    processing.hide(); // 隐藏处理中对话框
                    return;
                } else {
                    arc.util.Log.err("许可证验证过程中发生异常: " + errorMsg);

                }

                // 确保在主线程中显示对话框并隐藏处理中对话框
                Core.app.post(() -> {
                    processing.hide(); // 重要：隐藏处理中对话框

                    // 弹出对话框提示用户复制异常日志
                    BaseDialog dialog = new BaseDialog("");
                    dialog.addCloseButton();
                    dialog.cont.add(toText("info19")).row();
                    dialog.cont.add(toText("info20")).row();
                    dialog.cont.add(toText("info21") + e.getMessage()).row();
                    dialog.cont.button(toText("info22"), () -> {
                        Core.app.setClipboardText(e.getMessage()); // 仅复制错误信息
                        dialog.hide();
                        Vars.ui.showInfo("@copied");
                    }).padTop(5).width(200f).height(50).row();
                    dialog.show();
                });
                e.printStackTrace(); // 仍然保留日志记录，但不显示给用户
            }

            // 验证完成后，回到主线程更新UI
            final boolean finalSuccess = success;
            Core.app.post(() -> {
                processing.hide(); // 隐藏"处理中"对话框
                if (finalSuccess) {
                    // 如果验证成功，保存密匙到本地文件
                    String licenseContent = playerName + ":" + key;
                    if (!LICENSE_FILE.exists() || !LICENSE_FILE.readString().trim().equals(licenseContent)) {
                        LICENSE_FILE.writeString(licenseContent, false);
                        Log.info("激活密匙已保存至本地。");
                    }
                    isActivated = true;
                    saveActivationState();
                    Log.info("赞助激活状态确认 " + ActivateProgram.isActivated);
                    // 显示激活成功的提示信息
                    showCustomDialog("", cont -> {
                        cont.margin(15);
                        cont.add("@activation.success").width(400f).wrap().get().setAlignment(Align.center, Align.center);
                    });
                } else {
                    Log.err("密匙验证失败。");
                    isActivated = false;
                    saveActivationState();
                    showActivationDialog("@activation.error.invalidkey");
                }
            });
        });
    }

}