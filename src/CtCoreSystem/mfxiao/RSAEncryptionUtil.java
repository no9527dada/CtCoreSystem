package CtCoreSystem.mfxiao;

import arc.Core;
import arc.graphics.Color;
import arc.scene.ui.TextField;
import arc.util.Log;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mindustry.Vars;
import mindustry.ui.dialogs.BaseDialog;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;


public class RSAEncryptionUtil {

    private static final String VALIDATION_URL = "https://gitee.com/CT-no9527/ctblacklist/raw/master/key.json";

    /**
     * 从远程URL获取验证数据。
     */
    public static Map<String, String> fetchValidationData() throws Exception {
        URL url = new URL(VALIDATION_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.toString(), new TypeReference<Map<String, String>>() {});
        } finally {
            connection.disconnect();
        }
    }

    // 加密订单号的方法
    public static String encryptOrderId(String orderId) {
        if (orderId == null || orderId.isEmpty()) {
            return orderId;
        }

        StringBuilder encrypted = new StringBuilder();
        // 定义加密密钥（可以自定义）
        int key1 = 17;  // 加法密钥
        int key2 = 5;   // 乘法密钥
        int key3 = 3;   // 位移密钥

        try {
            // 处理订单号每一位
            for (int i = 0; i < orderId.length(); i++) {
                char c = orderId.charAt(i);
                if (Character.isDigit(c)) {
                    // 对数字进行加密
                    int digit = Character.getNumericValue(c);
                    // 应用数学运算: (digit + key1) * key2 - key3
                    int encryptedDigit = (digit + key1) * key2 - key3;
                    // 添加一些混淆字符（字母）
                    char 混淆字符 = (char) (65 + (encryptedDigit % 26)); // A-Z
                    encrypted.append(encryptedDigit).append(混淆字符);
                } else {
                    // 非数字字符保持不变
                    encrypted.append(c);
                }
            }
            // 添加校验和
            int checksum = calculateChecksum(orderId);
            encrypted.append("_")// 分隔符
                    .append(checksum);
        } catch (Exception e) {
            // 加密失败时返回原订单号
            return orderId;
        }

        return encrypted.toString();
    }

    // 解密订单号的方法
    public static String decryptOrderId(String encryptedOrderId) {
        if (encryptedOrderId == null || encryptedOrderId.isEmpty()) {
            Log.err("解密订单号时出错1：" + encryptedOrderId);
            return null;
        }

        try {
            // 提取校验和
            int underscoreIndex = encryptedOrderId.lastIndexOf('_');
            if (underscoreIndex == -1) {
                Log.err("解密订单号时出错2：" + encryptedOrderId);
                return null; // 格式错误
            }
            String encryptedPart = encryptedOrderId.substring(0, underscoreIndex);
            int checksum = Integer.parseInt(encryptedOrderId.substring(underscoreIndex + 1));

            StringBuilder decrypted = new StringBuilder();
            int key1 = 17;  // 加法密钥（与加密相同）
            int key2 = 5;   // 乘法密钥（与加密相同）
            int key3 = 3;   // 位移密钥（与加密相同）

            // 解密过程
            int i = 0;
            while (i < encryptedPart.length()) {
                // 找到下一个字母位置（混淆字符）
                int j = i;
                while (j < encryptedPart.length() && !Character.isLetter(encryptedPart.charAt(j))) {
                    j++;
                }

                if (j < encryptedPart.length()) {
                    // 提取加密的数字部分
                    String numStr = encryptedPart.substring(i, j);
                    try {
                        int encryptedDigit = Integer.parseInt(numStr);
                        // 应用逆向运算: (encryptedDigit + key3) / key2 - key1
                        int digit = (encryptedDigit + key3) / key2 - key1;
                        decrypted.append(digit);
                    } catch (NumberFormatException e) {
                        // 如果解析失败，保留原始字符
                        decrypted.append(encryptedPart.substring(i, j + 1));
                    }
                    i = j + 1;
                } else {
                    // 剩余的非字母字符
                    decrypted.append(encryptedPart.substring(i));
                    break;
                }
            }

            // 验证校验和
            String decryptedOrderId = decrypted.toString();
            if (calculateChecksum(decryptedOrderId) == checksum) {
                Log.info("激活码验证成功");
               // Log.info("激活码正确：" + encryptedOrderId);
                return decryptedOrderId;
            } else {
                Log.err("解密订单号时出错3：" + encryptedOrderId);
                return null; // 校验失败
            }
        } catch (Exception e) {
            // 解密失败时返回原加密字符串
            Log.err("解密订单号时出错4：" + encryptedOrderId);
            return null;
        }
    }

    // 计算简单校验和
    public  static int calculateChecksum(String orderId) {
        int sum = 0;
        for (char c : orderId.toCharArray()) {
            sum += (int) c;
        }
        return sum % 1000; // 取模限制范围
    }



    //显示创作者专用的秘钥生成对话框
    //创作者输入玩家订单号，系统通过加密算法生成可复制的秘钥

    public static void showCreatorKeyGenerator() {
        BaseDialog dialog = new BaseDialog("创作者秘钥生成器");
        dialog.addCloseButton();

        // 提示信息
        dialog.cont.add("请输入玩家提供的订单号，系统将生成加密后的秘钥供您复制使用").wrap().growX().pad(10).row();

        // 订单号输入框
        TextField orderIdField = new TextField();
        orderIdField.setMessageText("输入玩家的订单编号");
        dialog.cont.add("订单编号:").left().row();
        dialog.cont.add(orderIdField).growX().pad(5).row();

        // 显示生成的秘钥区域
        TextField keyDisplayField = new TextField();
        keyDisplayField.setMessageText("生成的加密秘钥将显示在这里");
        keyDisplayField.setDisabled(true); // 只读
        keyDisplayField.setColor(Color.gray);
        dialog.cont.add("加密秘钥:").left().row();
        dialog.cont.add(keyDisplayField).growX().pad(5).row();

        // 生成秘钥按钮
        dialog.cont.button("生成秘钥", () -> {
            String orderId = orderIdField.getText().trim();
            if (orderId.isEmpty()) {
                Vars.ui.showInfo("请输入有效的订单编号");
                return;
            }

            // 使用现有的加密算法生成秘钥
            String encryptedKey = encryptOrderId(orderId);
            keyDisplayField.setText(encryptedKey);

            // 自动选择并复制到剪贴板
            keyDisplayField.selectAll();
            Core.app.setClipboardText(encryptedKey);
            Vars.ui.showInfo("加密秘钥已生成并复制到剪贴板");
        }).width(220f).pad(4).growX().center().row();
        dialog.cont.button("复制加密秘钥", () -> {
            keyDisplayField.selectAll();
            Core.app.setClipboardText(keyDisplayField.getText());
            Vars.ui.showInfo("加密秘钥已复制到剪贴板");
        }).width(220f).pad(4).growX().center().row();
        // 为了方便测试，添加一个清空按钮
        dialog.cont.button("清空", () -> {
            orderIdField.clearText();
            keyDisplayField.clearText();
        }).width(120f).pad(4).growX().center().row();

        dialog.setModal(true);
        dialog.show();
    }

}
