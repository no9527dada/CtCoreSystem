package CtCoreSystem.mfxiao;

import arc.Core;
import arc.util.Log;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
// [MODIFIED] 导入用于解密的 Cipher 类和私钥相关的类
import javax.crypto.Cipher;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.util.Base64;
import java.util.Map;


import static CtCoreSystem.CtCoreSystem.PopUpWindow;
import static CtCoreSystem.CtCoreSystem.PopUpWindow2;
import static CtCoreSystem.mfxiao.ActivateProgram.showActivationDialog;

public class RSAEncryptionUtil {


    private static final String RSA_ALGORITHM = "RSA";
    private static final String VALIDATION_URL = "https://gitee.com/CT-no9527/ctblacklist/raw/master/key.json";
    private static final String YOUR_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCfQd1/CAbqLq+bAXFDfeM+sSC1lpJMm5cJbyzoxNv4pOOstpNApPmfFm7m5X2iU6rAoKUpxDTmMYAP2DOawRi/ZcOdnWAatJhir8syjKD1/3ltgInFWL2b2nv9mBL9BEke4RDGaKsYkyMHLimdWChFJ2RRYOXAVIgI1HG2/n0ADIdOll0fJdY40s/fBrzHx4ikiQYf6spWWMGLBvBuUk3FYAbKyTSYl+aSnaOXjKWvW292U1V7mU6ReNOJmiUOolkERyUItzSCUYsGzXw6fo+lD3UtLO2z1rudHVza3Tq2IOeOyEeEHFlMPKQZtFT3XGQTGMiyyimNSfPXZjb3CT4vAgMBAAECggEAATE0M6vbxIEkaUkmM2ozAlwUw3AhSpQURMOlIZaDrtPGghturYwnggKnQnCvs1zj+xH1Ph/bLGlQV5w+yN/ZhNsQAOFyOK1nF02XtTbHS0AmE0ic/2rt7VXQUs7X6Yv7RwoRSZS7Hyp5eyerJYc6stXU0uw+uIGXt1U0sdWBGinQJfGPTljIytj8/KS5xtunrMej0+L7MuhDVGoun9GLLvPdsbSX/2ZupqCOfQnZzX4iUfpZVI1KBSAOTs797oTTMh36hk8KphR6hvG19GpKHdbHyvAdAHeCikUGcPq50RyPq1eQWLDRYDy7p2b+0fg5MLDflHHrv53f7hzHaxIfvQKBgQC68hqymtNNG6pniz+l9VwPrv2inPlDP1usAHDeNlAecAKJvbKo7d0bUastBoYl91XEAxR2VCTuiusEO3FBgL9Iy/YlHdNQok8BtTfWmlAHC3jqmxlZuunoM4SLfIBf5DN4gqjJGvruTqR50FbbzHQGz9j9hRZmqQXPzde+3Wv+ZQKBgQDaFX83p3BK3TUEXPjgImgQSSD5BZRma2yCY3H1sYdjKteDPxakfAlMR+IVBxCNzVU5wXS++GbFVABZteYtH0bYxAJlJDIJfzyhy+i5Pk3amaRM2wDajBhVmafAwXI1ugve0hTuNBx765funXERz9TFfjy5eWQ7JYN8X3UHrEaHAwKBgDqxiHBK68xpwzZ/IZyZTC4gYsGLRzc1zTJTcRxTMRRdVm6ogE9JXwIUwgmA4t1IqkTAMsxz3qSGOQM7JnPKWEQixUUh+BZ43xVKMB3QlQ+hKnV9/JePd6DPi+2/SQ7SNta/Lv702ZO4XSthneOkbUk2OIpgHohcyNZVakcifaWBAoGBAK+mWtXHjaEus9H01BsqeyiKzfzkKpendDk8wX/DYilUS0qRmLoYkSCYURbgnsunkzo2f8KErbnHo5Sd9mtnYpean9c4pPiTO5GRe7sf3HLyBqxERFbCOaNQZBXTeRqe44qvKF6HKF4C7lcpBfXzsAr1VxGIHVuqJlIZTFhGwHNjAoGBAKzfbzS/ChHRU0lCd6U0arG2efLInR3xfzvxqrns8fKQctWSShDvZbbil264YiqxlCatl4Fmq2dw7x1CYqNRp4zZxFQa85hV8d1BweOV0o4VgT7UpuWspzytsrXDBjzqgymxeNDEja+uexq7rw397y5gI9Ka0b6ghwlSDLwFaq1W";
    /**
     * [MODIFIED] 新增的方法，从Base64字符串恢复私钥。
     * @param keyString Base64编码的PKCS#8私钥字符串
     * @return PrivateKey 私钥对象
     */
    private static PrivateKey stringToPrivateKey(String keyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(keyString);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return keyFactory.generatePrivate(spec);
    }

    /**
     * [MODIFIED] 新增的方法，用于执行解密。
     * @param encryptedDataB64 Base64编码的加密数据 (即玩家的许可证密钥)
     * @return 解密后的原始字符串 (订单号)
     */
    private static String decrypt(String encryptedDataB64) throws Exception {
        PrivateKey privateKey = stringToPrivateKey(YOUR_PRIVATE_KEY);
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedDataB64);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * 从远程URL获取验证数据。
     */
    private static Map<String, String> fetchValidationData() throws Exception {
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

    /**
     * [MODIFIED] 方法重构，功能变为根据玩家信息从远程获取预期的订单号。
     * @param playerName 玩家名称
     * @param uuid 玩家UUID
     * @return 远程服务器上的订单号，如果找不到则返回 null
     */
    private static String getExpectedOrderId(String playerName, String uuid) throws Exception {
        Map<String, String> validationData = fetchValidationData();
        String key = playerName + ":" + uuid;
        return validationData.get(key);
    }

    /**
     * 通过解密许可证并与远程数据比对来校验。
     * @param playerName 待验证的玩家名称
     * @param uuid 待验证的玩家UUID
     * @param licenseKey 玩家提供的许可证密钥 (即加密后的订单号)
     * @return 如果许可证有效，返回 true
     */
    public static boolean verifyLicense(String playerName, String uuid, String licenseKey) {
        try {
            /* 步骤 1：先用私钥解密玩家提交的 licenseKey，拿到订单号 */
            String decryptedOrderId = decrypt(licenseKey);
            if (decryptedOrderId.isEmpty()) {
                Log.warn("许可证检查失败：无法从密钥解出订单号。");
                return false;
            }

            /* 步骤 2：根据订单号反查玩家名称 */
            String registeredPlayerName = getPlayerNameByOrderId(decryptedOrderId);
            if (registeredPlayerName == null) {
                Log.warn("许可证检查失败：订单号 @ 未在白名单中找到对应的玩家。", decryptedOrderId);
                return false;
            }

            /* 步骤 3：比对反查到的玩家名称 与 调用方传进来的 playerName */
            boolean isValid = registeredPlayerName.equals(playerName);
            if (!isValid) {
               // Log.warn("许可证检查失败：玩家名称不匹配。预期 @，实际 @", registeredPlayerName, playerName);
                Log.warn("许可证检查失败：注册名称不存在。");
                PopUpWindow("", cont -> {
                    cont.add(Core.bundle.format("activation.error.noUserName"));
                });
                ActivateProgram.isActivated = false;
            }
            return isValid ; // 只有全部通过才返回 true
        } catch (Exception e) {
            Log.err("许可证验证过程中发生异常", e);
            ActivateProgram.isActivated = false;
            showActivationDialog("@activation.error.invalidkey");
            PopUpWindow2("", cont -> {
                cont.add(Core.bundle.format("activation.error.localfileinvalid")).row();
            });

            return false;
        }
    }
    private static String getPlayerNameByOrderId(String orderId) throws Exception {
        Map<String, String> validationData = fetchValidationData();
        for (Map.Entry<String, String> entry : validationData.entrySet()) {
            if (entry.getValue().equals(orderId)) {
                String key = entry.getKey();              // 形如  玩家名:UUID
                int idx = key.indexOf(':');
                return (idx > -1) ? key.substring(0, idx) // 冒号前即玩家名称
                        : key;                  // 找不到冒号就整串返回
            }
        }
        return null; // 未找到
    }
}