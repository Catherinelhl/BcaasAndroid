package io.bcaas.tools.encryption;

/**
 * 
 * @since 2017-03-01
 * 
 * @author Costa
 * 
 * @version 1.0.0
 * 
 */

public class EncoDecodeTool {

	public enum EncodeType {
		EncodeType_3DES_ECB, EncodeType_3DES_CBC,

		EncodeType_AES_128_ECB, EncodeType_AES_128_CBC

	}

	public static String encode(EncodeType type, String content) {

		String result = "";
		switch (type) {

		case EncodeType_3DES_CBC:
			try {
				result = Des3Tool.encodeCBC(content);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case EncodeType_3DES_ECB:
			try {
				result = Des3Tool.encodeECB(content);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case EncodeType_AES_128_CBC:
			try {
				result = AESTool.encodeCBC_128(content);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case EncodeType_AES_128_ECB:
			try {
				result = AESTool.encodeECB_128(content);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		default:
			break;

		}

		return result;
	}

	public static String decode(EncodeType type, String hash) {

		String result = "";

		switch (type) {
		case EncodeType_3DES_CBC:
			try {
				result = Des3Tool.decodeCBC(hash);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case EncodeType_3DES_ECB:
			try {
				result = Des3Tool.decodeECB(hash);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case EncodeType_AES_128_CBC:
			try {
				result = AESTool.decodeCBC_128(hash);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case EncodeType_AES_128_ECB:
			try {
				result = AESTool.decodeECB_128(hash);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		default:
			break;

		}
		return result;
	}

}
