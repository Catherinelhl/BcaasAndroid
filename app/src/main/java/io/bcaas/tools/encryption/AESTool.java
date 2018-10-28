package io.bcaas.tools.encryption;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.bcaas.constants.MessageConstants;
import io.bcaas.tools.regex.RegexTool;

/**
 * @author Costa
 * @version 1.0.0
 * @since 2017-03-01
 */


public class AESTool {

    // 密鑰
    private final static String secretKey_128 = "jdcv@888@jdcv888";
    private final static String secretKey_256 = "jdcv@888@jdcv888jdcv@888@jdcv888";
    // 向量
    private final static String iv = "qwertyuioplkjhgg";

    // 加解密統一使用的編碼方式
    private final static String encoding = "utf-8";

    // 密碼八位固定
    private final static String secretKey_128_fixed = "jdcv@888";

    // 密碼8~16位
    private final static String secretKey_128_1 = "a";
    private final static String secretKey_128_2 = "b2";
    private final static String secretKey_128_3 = "cd3";
    private final static String secretKey_128_4 = "e@f4";
    private final static String secretKey_128_5 = "ghij5";
    private final static String secretKey_128_6 = "k#lmn6";
    private final static String secretKey_128_7 = "opqrst7";
    private final static String secretKey_128_8 = "uv@wxyz8";

    /**
     * 3DES加密
     *
     * @param plainText 普通文本
     * @return
     * @throws Exception
     */
    public static String encodeCBC_128(String plainText) throws Exception {
        Key deskey = null;
        byte[] raw = secretKey_128.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// "算法/模式/補碼方式"
        Cipher.getInstance("AES/CBC/PKCS5Padding");

        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ips);
        byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
        return Base64Tool.encode(encryptData);
    }

    /**
     * 3DES加密
     *
     * @param plainText    普通文本
     * @param secretKey128 加密金鑰
     * @return
     * @throws Exception
     */
    public static String encodeCBC_128(String plainText, String secretKey128) throws Exception {
        if (!RegexTool.isValidatePassword(secretKey128)) { // 正則表達式驗證密碼
            return MessageConstants.Empty;
        }
        secretKey128 = setSecretKey(secretKey128);
        byte[] raw = secretKey128.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"算法/模式/補碼方式"
        Cipher.getInstance("AES/CBC/PKCS5Padding");

        IvParameterSpec ips = new IvParameterSpec(secretKey128.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ips);
        byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
        return Base64Tool.encode(encryptData);
    }

    /**
     * 3DES解密
     *
     * @param encryptText 加密文本
     * @return
     * @throws Exception
     */
    public static String decodeCBC_128(String encryptText) throws Exception {
        Key deskey = null;
        byte[] raw = secretKey_128.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Cipher.getInstance("AES/CBC/PKCS5Padding");

        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ips);

        byte[] decryptData = cipher.doFinal(Base64Tool.decode(encryptText));

        return new String(decryptData, encoding);
    }

    /**
     * 3DES解密
     *
     * @param encryptText  加密文本
     * @param secretKey128 解密金鑰
     * @return
     * @throws Exception
     */
    public static String decodeCBC_128(String encryptText, String secretKey128) throws Exception {
        if (!RegexTool.isValidatePassword(secretKey128)) { // 正則表達式驗證密碼
            return MessageConstants.Empty;
        }
        secretKey128 = setSecretKey(secretKey128);
        byte[] raw = secretKey128.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Cipher.getInstance("AES/CBC/PKCS5Padding");

        IvParameterSpec ips = new IvParameterSpec(secretKey128.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ips);

        byte[] decryptData = cipher.doFinal(Base64Tool.decode(encryptText));

        return new String(decryptData, encoding);
    }

    /**
     * 3DES加密
     *
     * @param plainText 普通文本
     * @return
     * @throws Exception
     */
    public static String encodeECB_128(String plainText) throws Exception {
        Key deskey = null;
        byte[] raw = secretKey_128.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        Cipher.getInstance("AES/ECB/PKCS5Padding");

        IvParameterSpec ips = new IvParameterSpec("".getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ips);
        byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
        return Base64Tool.encode(encryptData);
    }

    /**
     * 3DES解密
     *
     * @param encryptText 加密文本
     * @return
     * @throws Exception
     */
    public static String decodeECB_128(String encryptText) throws Exception {
        Key deskey = null;
        byte[] raw = secretKey_128.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        Cipher.getInstance("AES/ECB/PKCS5Padding");

        IvParameterSpec ips = new IvParameterSpec("".getBytes());
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ips);

        byte[] decryptData = cipher.doFinal(Base64Tool.decode(encryptText));

        return new String(decryptData, encoding);
    }

    // 使用者輸入密碼8~16位, 密碼不足16位, 後綴補上設定好的密碼
    private static String setSecretKey(String secretKey128) {
        switch (secretKey128.length()) {
            case 8:
                secretKey128 += secretKey_128_8;
                break;
            case 9:
                secretKey128 += secretKey_128_7;
                break;
            case 10:
                secretKey128 += secretKey_128_6;
                break;
            case 11:
                secretKey128 += secretKey_128_5;
                break;
            case 12:
                secretKey128 += secretKey_128_4;
                break;
            case 13:
                secretKey128 += secretKey_128_3;
                break;
            case 14:
                secretKey128 += secretKey_128_2;
                break;
            case 15:
                secretKey128 += secretKey_128_1;
                break;
            case 16:
                // 16位密碼不需補
                break;
        }
        return secretKey128;
    }

}
