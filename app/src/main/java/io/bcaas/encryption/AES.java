package io.bcaas.encryption;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 
 * @since 2017-03-01
 * 
 * @author Costa
 * 
 * @version 1.0.0
 * 
 */

public class AES {

	// 密鑰
	private final static String secretKey_128 = "jdcv@888@jdcv888";
	private final static String secretKey_256 = "jdcv@888@jdcv888jdcv@888@jdcv888";
	// 向量
	private final static String iv = "qwertyuioplkjhgg";

	// 加解密統一使用的編碼方式
	private final static String encoding = "utf-8";

	// 密碼八位固定
	private final static String secretKey_128_fixed = "jdcv@888";

	/**
	 * 3DES加密
	 *
	 * @param plainText
	 *            普通文本
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
		return Base64.encode(encryptData);
	}

	/**
	 * 3DES加密
	 *
	 * @param plainText 普通文本
	 * @param secretKey128 加密金鑰
	 * @return
	 * @throws Exception
	 */
	public static String encodeCBC_128(String plainText, String secretKey128) throws Exception {
		secretKey128 = secretKey128 + secretKey_128_fixed;
		byte[] raw = secretKey128.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"算法/模式/補碼方式"
		Cipher.getInstance("AES/CBC/PKCS5Padding");

		IvParameterSpec ips = new IvParameterSpec(secretKey128.getBytes());
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ips);
		byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
		return Base64.encode(encryptData);
	}

	/**
	 * 3DES解密
	 *
	 * @param encryptText
	 *            加密文本
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

		byte[] decryptData = cipher.doFinal(Base64.decode(encryptText));

		return new String(decryptData, encoding);
	}

	/**
	 * 3DES解密
	 *
	 * @param encryptText 加密文本
	 * @param secretKey128 解密金鑰
	 * @return
	 * @throws Exception
	 */
	public static String decodeCBC_128(String encryptText, String secretKey128) throws Exception {
		secretKey128 = secretKey128 + secretKey_128_fixed;
		byte[] raw = secretKey128.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Cipher.getInstance("AES/CBC/PKCS5Padding");

		IvParameterSpec ips = new IvParameterSpec(secretKey128.getBytes());
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, ips);

		byte[] decryptData = cipher.doFinal(Base64.decode(encryptText));

		return new String(decryptData, encoding);
	}

	/**
	 * 3DES加密
	 *
	 * @param plainText
	 *            普通文本
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
		return Base64.encode(encryptData);
	}

	/**
	 * 3DES解密
	 *
	 * @param encryptText
	 *            加密文本
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

		byte[] decryptData = cipher.doFinal(Base64.decode(encryptText));

		return new String(decryptData, encoding);
	}

}
