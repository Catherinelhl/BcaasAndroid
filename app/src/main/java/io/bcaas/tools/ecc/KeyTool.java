package io.bcaas.tools.ecc;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.ECKey.ECDSASignature;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.KeyCrypterException;
import org.bitcoinj.params.MainNetParams;
import org.spongycastle.asn1.sec.SECNamedCurves;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.signers.ECDSASigner;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.util.encoders.Base64;

import io.bcaas.bean.WalletBean;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.encryption.Base58Tool;

public class KeyTool {

	public static void main(String[] args) throws Exception {

		WalletBean walletBean =Wallet.createWallet();

		// 比特幣錢包地址
		String bitcoinAddressStr = walletBean.getAddress();
		// 比特幣私鑰WIF格式
		String bitcoinPrivateKeyWIFStr = walletBean.getPrivateKey();
		// String bitcoinPrivateKeyWIFStr =
		// "5KEKVAm9JbNjd9iVRz6xonNhGafrKmzQLRwGx5G33gXLeUHCfWm";
		// 比特幣公鑰((130 characters [0-9A-F]))
		String bitcoinPublicKeyStr = walletBean.getPublicKey();
		// String bitcoinPublicKeyStr ="046604f1c0ce8029352e4bc2515c07c254ad4ad6116d44cda22d805f7f7d4dd5cdab812c6f9ec1d3d38c6f740e9af609125a416c9c17838d564650ad168c28bd1d";

		System.out.println("bitcoinPrivateKeyWIFStr = " + bitcoinPrivateKeyWIFStr);
		System.out.println("bitcoinPublicKeyStr = " + bitcoinPublicKeyStr);
		System.out.println("bitcoinAddressStr = " + bitcoinAddressStr);
		
		//根據私鑰產生Wallet
		WalletBean usePrivateKeyWIFStrCreateWallet = Wallet.createWallet(bitcoinPrivateKeyWIFStr);
		System.out.println("[usePrivateKeyWIFStrCreateWallet] bitcoinPrivateKeyWIFStr = " + usePrivateKeyWIFStrCreateWallet.getPrivateKey());
		System.out.println("[usePrivateKeyWIFStrCreateWallet] bitcoinPublicKeyStr = " + usePrivateKeyWIFStrCreateWallet.getPublicKey());
		System.out.println("[usePrivateKeyWIFStrCreateWallet] bitcoinAddressStr = " + usePrivateKeyWIFStrCreateWallet.getAddress());

		// 私鑰加簽
		String tcMessage = "{\"previous\":\"bb8ee265133794ba6a74e705fb8839539d41beb22ea7e075529b7b6dcee7506a\",\"blockService\":\"BCC\",\"blockType\":\"Open\",\"blockTxType\":\"Matrix\",\"sourceTxhash\":\"0f4e38eb39ac15befc5455782774a155f5f8a3fa36e49e7bb75968105ae43c70\",\"amount\":\"100\",\"representative\":\"1EykGQ6mNsoVNsdy9hM9frPNTWPg8jRCWG\",\"wallet\":\"1EykGQ6mNsoVNsdy9hM9frPNTWPg8jRCWG\",\"work\":\"0\",\"date\":\"1534940225033\"}";
		String signatureMessage = sign("5KhVkSGqV6jAyRbGuyg2DSDULZwGv9ueFdUzySxXxUZRzskBwmi", tcMessage);
		System.out.println("Signature Message = " + signatureMessage);

		// 公鑰解簽
		boolean verifyResult2 = verify("04c5f3dfd89ebb741e06e1f098f717ca21ee92e841f9754d177c847cefe23c5c2c593456968a223ff0b867ea8af0cb9ee0d4f238d4ec331a167b26d78fd9689bf6", signatureMessage, tcMessage);
		System.out.println("---------------------Verify RESULT Java = " + verifyResult2);
		boolean verifyResult = verify("04c5f3dfd89ebb741e06e1f098f717ca21ee92e841f9754d177c847cefe23c5c2c593456968a223ff0b867ea8af0cb9ee0d4f238d4ec331a167b26d78fd9689bf6", "Gy9FaEPdrUKraV3K5iN+EezQ2ad8MYOgkoLgYsuljfngAhUWKz8sRaCid3ixnOahRuei6QSgf4E9wsuPomDU8DU=", tcMessage);
		System.out.println("---------------------Verify RESULT iOS = " + verifyResult);

		// 驗證地址是否符合比特幣規範
		boolean validateBitcoinAddress = validateBitcoinAddress(bitcoinAddressStr);
		System.out.println("validateBitcoinAddress = " + validateBitcoinAddress);

		// 驗證公鑰
		boolean validateBitcoinPublicKeyStr = validateBitcoinPublicKeyStr(bitcoinPublicKeyStr);
		System.out.println("validateBitcoinPublicKeyStr = " + validateBitcoinPublicKeyStr);
		// 驗證私鑰
		
		boolean validateBitcoinPrivateKeyWIFStr = validateBitcoinPrivateKeyWIFStr(bitcoinPrivateKeyWIFStr);
		System.out.println("validateBitcoinPrivateKeyWIFStr = " + validateBitcoinPrivateKeyWIFStr);
		
	}

	// ========================================================================================================================
	/**
	 *
	 * Verification Signature
	 *
	 * @param publicKeyStr
	 * @param signatureBase64Str
	 * @param tcMessage
	 *
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean verify(String publicKeyStr, String signatureBase64Str, String tcMessage) throws Exception {

		boolean verifySueecss = false;

		// 使用secp256k1產生公鑰
		X9ECParameters params = SECNamedCurves.getByName("secp256k1");
		ECDomainParameters CURVE = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(),
				params.getH());

		// 公鑰X軸
		BigInteger axisX = new BigInteger(publicKeyStr.substring(2, 66), 16);
		// 公鑰Y軸
		BigInteger axisY = new BigInteger(publicKeyStr.substring(66, publicKeyStr.length()), 16);
		ECPoint ecPoint = CURVE.getCurve().createPoint(axisX, axisY);
		// 依照ECPoint轉為公鑰ECKey
		ECKey fromPublicOnly = ECKey.fromPublicOnly(ecPoint);

		try {
			
			tcMessage =  Sha256Tool.doubleSha256ToString(tcMessage);
			
			// 公鑰解簽
			fromPublicOnly.verifyMessage(tcMessage, signatureBase64Str);
			verifySueecss = true;
		} catch (Exception e) {
			LogTool.d("Verify Exception = " + e.getMessage());
		}

		return verifySueecss;

	}

	// ========================================================================================================================
	/**
	 * Create Signature
	 *
	 * @param privateKeyStr
	 * @param tcMessage
	 * @return signatureMessage
	 * 
	 * @throws Exception
	 */
	public static String sign(String privateKeyWIFStr, String tcMessage) throws Exception {

		// 私鑰WIF字串轉ECKey
		ECKey privateKey = DumpedPrivateKey.fromBase58(MainNetParams.get(), privateKeyWIFStr).getKey();
		
		tcMessage =  Sha256Tool.doubleSha256ToString(tcMessage);
		
		return signMessage(privateKey, tcMessage);

	}

	// ========================================================================================================================
	/**
	 * Checks if the given String is a valid Bitcoin address.
	 *
	 * @param address
	 *            The address to check
	 * @return True, if the String is a valid Bitcoin address, false otherwise
	 */
	public static boolean validateBitcoinAddress(String address) {

		if (address == null) {
			return false;
		}

		// Check the length
		if (address.length() < 26 || address.length() > 35) {
			return false;
		}

		byte[] addressBytes = Base58Tool.decode(address);

		// Check the version byte
		if (addressBytes[0] != 0) {
			return false;
		}

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("BitcoinUtils.validateBitcoinAddress: SHA-256 digest not found");
		}

		md.update(addressBytes, 0, 21);
		byte[] sha256Hash = md.digest();
		sha256Hash = md.digest(sha256Hash);

		byte[] addressChecksum = Arrays.copyOfRange(addressBytes, 21, addressBytes.length);
		byte[] calculatedChecksum = Arrays.copyOfRange(sha256Hash, 0, 4);
		return Arrays.equals(addressChecksum, calculatedChecksum);
	}

	// ========================================================================================================================
	/**
	 * Checks if the given String is a valid PublicKey String.
	 *
	 * @param publicKeyStr
	 *            The PublicKey String to check
	 * @return True, if the String is a valid Bitcoin PublicKey String, false
	 *         otherwise
	 */
	public static boolean validateBitcoinPublicKeyStr(String publicKeyStr) {

		if (publicKeyStr.matches("^04[a-f0-9]{128}")) {
			return true;
		}
		return false;

	}
	// ========================================================================================================================
		/**
		 * Checks if the given String is a valid PublicKey String.
		 *
		 * @param publicKeyStr
		 *            The PublicKey String to check
		 * @return True, if the String is a valid Bitcoin PublicKey String, false
		 *         otherwise
		 */
		public static boolean validateBitcoinPrivateKeyWIFStr(String privateKeyStr) {

			if (privateKeyStr.matches("^5[123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz]{50}")) {
				return true;
			}
			return false;

		}

	/**
	 * Signs a text message using the standard Bitcoin messaging signing format and
	 * returns the signature as a base64 encoded string.
	 *
	 * @throws IllegalStateException
	 *             if this ECKey does not have the private part.
	 * @throws KeyCrypterException
	 *             if this ECKey is encrypted and no AESKey is provided or it does
	 *             not decrypt the ECKey.
	 */
	public static String signMessage(ECKey ecKey, String message) throws KeyCrypterException {

		byte[] data = Utils.formatMessageForSigning(message);
		// Twice Sha256
		Sha256Hash hash = Sha256Hash.twiceOf(data);

		// 使用預設RandomDSAKCalculator
		ECDSASigner signer = new ECDSASigner();

		ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(ecKey.getPrivKey(), ECKey.CURVE);
		signer.init(true, privKey);
		BigInteger[] components = signer.generateSignature(hash.getBytes());
		ECDSASignature sig = new ECDSASignature(components[0], components[1]).toCanonicalised();

		String ecKeyPublicAsStr = ecKey.getPublicKeyAsHex();
		// Now we have to work backwards to figure out the recId needed to recover the
		int recId = -1;
		for (int i = 0; i < 4; i++) {
			ECKey key = ECKey.recoverFromSignature(i, sig, hash, ecKey.isCompressed());
			String recoverFromSignatureStr = key.getPublicKeyAsHex();
			// Check Public Key String
			if (key != null && ecKeyPublicAsStr.equals(recoverFromSignatureStr)) {
				recId = i;
				break;
			}
		}

		if (recId == -1) {
			throw new RuntimeException("Could not construct a recoverable key. This should never happen.");
		}

		int headerByte = recId + 27 + (ecKey.isCompressed() ? 4 : 0);
		// 1 header + 32 bytes for R + 32 bytes for S
		byte[] sigData = new byte[65];
		sigData[0] = (byte) headerByte;
		System.arraycopy(Utils.bigIntegerToBytes(sig.r, 32), 0, sigData, 1, 32);
		System.arraycopy(Utils.bigIntegerToBytes(sig.s, 32), 0, sigData, 33, 32);
		return new String(Base64.encode(sigData), Charset.forName("UTF-8"));
	}

}
