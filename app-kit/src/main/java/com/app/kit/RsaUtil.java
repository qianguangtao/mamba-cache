package com.app.kit;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RsaUtil {

	public static final String KEY_ALGORITHM = "RSA";
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

	private static final String PUBLIC_KEY = "RSAPublicKey";
	private static final String PRIVATE_KEY = "RSAPrivateKey";

	/**
	 * 用私钥对信息生成数字签名
	 * @param data       加密数据
	 * @param privateKey 私钥
	 */
	public static String sign(final byte[] data, final String privateKey) throws Exception {
		// 解密由base64编码的私钥
		final byte[] keyBytes = decryptBASE64(privateKey);
		// 构造PKCS8EncodedKeySpec对象
		final PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		// KEY_ALGORITHM 指定的加密算法
		final KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		// 取私钥匙对象
		final PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
		// 用私钥对信息生成数字签名
		final Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(priKey);
		signature.update(data);
		return encryptBASE64(signature.sign());
	}

	public static byte[] decryptBASE64(final String key) {
		return Base64.decode(key);
	}

	public static String encryptBASE64(final byte[] bytes) {
		return Base64.encode(bytes);
	}

	/**
	 * 校验数字签名
	 * @param data      加密数据
	 * @param publicKey 公钥
	 * @param sign      数字签名
	 * @return 校验成功返回true 失败返回false
	 */
	public static boolean verify(final byte[] data, final String publicKey, final String sign)
			throws Exception {
		// 解密由base64编码的公钥
		final byte[] keyBytes = decryptBASE64(publicKey);
		// 构造X509EncodedKeySpec对象
		final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		// KEY_ALGORITHM 指定的加密算法
		final KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		// 取公钥匙对象
		final PublicKey pubKey = keyFactory.generatePublic(keySpec);
		final Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(pubKey);
		signature.update(data);
		// 验证签名是否正常
		return signature.verify(decryptBASE64(sign));
	}

	/**
	 * 解密<br>
	 * 用公钥解密
	 */
	public static byte[] decryptByPublicKey(final byte[] data, final String key)
			throws Exception {
		// 对密钥解密
		final byte[] keyBytes = decryptBASE64(key);
		// 取得公钥
		final X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		final KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		final Key publicKey = keyFactory.generatePublic(x509KeySpec);
		// 对数据解密
		final Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, publicKey);
		return cipher.doFinal(data);
	}

	/**
	 * 加密<br>
	 * 用公钥加密
	 */
	public static byte[] encryptByPublicKey(final String data, final String key)
			throws Exception {
		// 对公钥解密
		final byte[] keyBytes = decryptBASE64(key);
		// 取得公钥
		final X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		final KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		final Key publicKey = keyFactory.generatePublic(x509KeySpec);
		// 对数据加密
		final Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(data.getBytes());
	}

	/**
	 * 加密<br>
	 * 用私钥加密
	 */
	public static byte[] encryptByPrivateKey(final byte[] data, final String key)
			throws Exception {
		// 对密钥解密
		final byte[] keyBytes = decryptBASE64(key);
		// 取得私钥
		final PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		final KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		final Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
		// 对数据加密
		final Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		return cipher.doFinal(data);
	}

	/**
	 * 取得私钥
	 */
	public static String getPrivateKey(final Map<String, Key> keyMap)
			throws Exception {
		final Key key = keyMap.get(PRIVATE_KEY);
		return encryptBASE64(key.getEncoded());
	}

	/**
	 * 取得公钥
	 */
	public static String getPublicKey(final Map<String, Key> keyMap)
			throws Exception {
		final Key key = keyMap.get(PUBLIC_KEY);
		return encryptBASE64(key.getEncoded());
	}

	/**
	 * 初始化密钥
	 */
	public static Map<String, Key> initKey() throws Exception {
		final KeyPairGenerator keyPairGen = KeyPairGenerator
				.getInstance(KEY_ALGORITHM);
		keyPairGen.initialize(2048);
		final KeyPair keyPair = keyPairGen.generateKeyPair();
		final Map<String, Key> keyMap = new HashMap(2);
		keyMap.put(PUBLIC_KEY, keyPair.getPublic());// 公钥
		keyMap.put(PRIVATE_KEY, keyPair.getPrivate());// 私钥
		return keyMap;
	}

	/**
	 * 通过前端传过来的经过md5和Rsa加密的密码和私钥解密
	 * @return md5加密的密码
	 */
	public static String rsaDecode(String password, final String privateKey) {
		try {
			final byte[] decryptByPrivateKey = RsaUtil.decryptByPrivateKey(password, privateKey);
			password = new String(decryptByPrivateKey);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return password;
	}

	/**
	 * 解密<br>
	 * 用私钥解密
	 */
	public static byte[] decryptByPrivateKey(final String data, final String key)
			throws Exception {
		return decryptByPrivateKey(decryptBASE64(data), key);
	}

	/**
	 * 解密<br>
	 * 用私钥解密
	 */
	public static byte[] decryptByPrivateKey(final byte[] data, final String key) throws Exception {
		// 对密钥解密
		final byte[] keyBytes = decryptBASE64(key);
		// 取得私钥
		final PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		final KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		final Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
		// 对数据解密
		final Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher.doFinal(data);
	}


	//    public static void main(String[] args) throws Exception {
	//        Map<String, Key> keyMap = initKey();
	//        String publicKey = getPublicKey(keyMap);
	//        String privateKey = getPrivateKey(keyMap);
	//
	//        System.out.println(keyMap);
	//        System.out.println("-----------------------------------");
	//        System.out.println(publicKey);
	//        System.out.println("-----------------------------------");
	//        System.out.println(privateKey);
	////        System.out.println("-----------------------------------");
	////        byte[] encryptByPrivateKey = encryptByPrivateKey("232323".getBytes(),privateKey);
	////        byte[] encryptByPublicKey = encryptByPublicKey("fwefw323",publicKey);
	////        System.out.println(new String(encryptByPrivateKey));
	////        System.out.println("-----------------------------------");
	////        System.out.println(new String(encryptByPublicKey));
	////        System.out.println("-----------------------------------");
	////        String sign = sign(encryptByPrivateKey,privateKey);
	////        System.out.println(sign);
	////        System.out.println("-----------------------------------");
	////        boolean verify = verify(encryptByPrivateKey,publicKey,sign);
	////        System.out.println(verify);
	////        System.out.println("-----------------------------------");
	////        byte[] decryptByPublicKey = decryptByPublicKey(encryptByPrivateKey,publicKey);
	////        byte[] decryptByPrivateKey = decryptByPrivateKey(encryptByPublicKey,privateKey);
	////        System.out.println(new String(decryptByPublicKey));
	////        System.out.println("-----------------------------------");
	////        System.out.println(new String(decryptByPrivateKey));
	//
	//        //Base64 加密
	//        byte[] encoded = decryptBASE64("123456");
	//        byte[] encryptByPublicKey = encryptByPublicKey(new String(encoded), publicKey);
	//        String string = new String(encryptByPublicKey);
	//        byte[] encoded2 = decryptBASE64(string);
	//        byte[] bytes = decryptByPrivateKey(new String(encoded2), privateKey);
	//        String string1 = new String(bytes);
	//        System.out.println(string1);
	//    }

	//     public static void main(String[] args) throws IOException, ClassNotFoundException {
	// //        System.out.println(Stream.of(Role.values()).map(role -> String.format("%s【%d】:%s", role.name(), role.ordinal(), role.comment)).collect(Collectors.joining(",")));
	// //        log.info("@Secured({\"" + String.join("\", \"", Util.toStringArray(Role.values())) + "\"})");
	//         // 判断密码是否匹配
	//         log.info("密码是否匹配：" + new BCryptPasswordEncoder().matches(
	//                 "12345678",
	//                 "$2a$10$9EsKmASE8gtlrYYuZ/lMHuljCIJZu2ZVn5eMurMcNNr41PItMdLm."));
	//         log.info("密码是否匹配2：" + new BCryptPasswordEncoder().matches(
	//                 "douyin@lynkco2021.com",
	//                 "$2a$10$TnBqWQSPxO0ugCOS497.mO3Z1Nc41PTVrPtU9ZsPhgXUSSWwsmY9q"));
	//         log.info("密码是否匹配3：" + BCrypt.checkpw("12345678", "$2a$10$r6B.foIdvwgJFU.vcbgzDOGxEPwdFrTHlIG65Sav4LBD7g.5dO0eO"));
	//         log.info(new BCryptPasswordEncoder().encode("superadmin"));
	//         log.info(new BCryptPasswordEncoder().encode("douyin@lynkco2021.com"));
	//         log.info(new BCryptPasswordEncoder().encode("!yxbb$@(123.#2021"));
	//         log.info(new BCryptPasswordEncoder().encode("!yxbb$@(123.#2021"));
	//         log.info(Rsa.encryptByPublicKey("douyin@lynkco2021.com", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0Xi8dPe1Po46Nl4jFPdly51A+CvxVj820Yh6EVIEUih4YNTHwgmSyh2ByBE005fECF60WxZGcqiWm1+NKk6RkibV7VHYDDd+AOsN7BGQApWs36kg/FIvfsDdF8ok1fxoOCSAF4a+ynn8bQhHaDUPgyjs6fQS2MKtybn9pn6vmPBTO3c6KnDAOzKmTVFwh2NVA0rgh1cBRqF1nLB8JY3pAUq/zB0vqsUCYdBKuCVm2r8rOdILjGSFrXsA9HruN2khIZ0k4rhRwso+Ff5hPB1Caeyz0kGukT/Sdo6Tl7ba0XI7s3aQwnxd293qe+Hs8IBWyj9/8XNKrBBnLkAh9w8lPQIDAQAB"));
	//     }
}

