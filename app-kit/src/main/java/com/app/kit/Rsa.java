package com.app.kit;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * RSA 工具类
 * @author qiangt 2020-05-11
 */
@Slf4j
public final class Rsa {
	/**
	 * 参考文档: https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyPairGenerator
	 * <pre>
	 * KeyPairGenerator Algorithms
	 * (Except as noted, these classes create keys for which Key.getAlgorithm() returns the standard algorithm name.)
	 * The algorithm names in this section can be specified when generating an instance of KeyPairGenerator.
	 * <table border="5" cellpadding="5" frame="border" width="90%" summary="KeyPairGenerator Algorithms">
	 * <thead> <tr> <th>Algorithm Name</th> <th>Description</th> </tr> </thead>
	 * <tbody>
	 * <tr> <td>DiffieHellman</td> <td>Generates keypairs for the Diffie-Hellman KeyAgreement algorithm. <p>Note: <code>key.getAlgorithm()</code> will return "DH" instead of "DiffieHellman".</p> </td> </tr>
	 * <tr> <td>DSA</td> <td>Generates keypairs for the Digital Signature Algorithm.</td> </tr>
	 * <tr> <td>RSA</td> <td>Generates keypairs for the RSA algorithm (Signature/Cipher).</td> </tr>
	 * <tr> <td>EC</td> <td>Generates keypairs for the Elliptic Curve algorithm.</td> </tr>
	 * </tbody>
	 * </table>
	 */
	private static final String KEY_PAIR_GENERATOR_ALGORITHM = "RSA";
	/**
	 * 参考文档: https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyFactory
	 * <pre>
	 * KeyFactory Algorithms
	 * (Except as noted, these classes create keys for which Key.getAlgorithm() returns the standard algorithm name.)
	 * The algorithm names in this section can be specified when generating an instance of KeyFactory.
	 * <table border="5" cellpadding="5" frame="border" width="90%" summary="KeyFactory Algorithms">
	 * <thead> <tr> <th>Algorithm Name</th> <th>Description</th> </tr> </thead>
	 * <tbody>
	 * <tr> <td>DiffieHellman</td> <td>Keys for the Diffie-Hellman KeyAgreement algorithm. <p>Note: <code>key.getAlgorithm()</code> will return "DH" instead of "DiffieHellman".</p> </td> </tr>
	 * <tr> <td>DSA</td> <td>Keys for the Digital Signature Algorithm.</td> </tr>
	 * <tr> <td>RSA</td> <td>Keys for the RSA algorithm (Signature/Cipher).</td> </tr>
	 * <tr> <td>EC</td> <td>Keys for the Elliptic Curve algorithm.</td> </tr>
	 * </tbody>
	 * </table>
	 */
	private static final String KEY_FACTORY_ALGORITHM = "RSA";
	/**
	 * 参考文档: https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#Cipher
	 * <pre>
	 * Cipher (Encryption) Algorithms
	 * Cipher Algorithm Names
	 * The following names can be specified as the algorithm component in a transformation when requesting an instance of Cipher.
	 * <table border="5" cellpadding="5" frame="border" width="90%" summary="Cipher Algorithm Names">
	 * <thead> <tr> <th>Algorithm Name</th> <th>Description</th> </tr> </thead>
	 * <tbody>
	 * <tr> <td>AES</td> <td>Advanced Encryption Standard as specified by NIST in <a href="http://csrc.nist.gov/publications/fips/index.html">FIPS 197</a>. Also known as the Rijndael algorithm by Joan Daemen and Vincent Rijmen, AES is a 128-bit block cipher supporting keys of 128, 192, and 256 bits.</td> </tr>
	 * <tr> <td>AESWrap</td> <td>The AES key wrapping algorithm as described in <a href="http://www.ietf.org/rfc/rfc3394.txt">RFC 3394</a>.</td> </tr>
	 * <tr> <td>ARCFOUR</td> <td>A stream cipher believed to be fully interoperable with the RC4 cipher developed by Ron Rivest. For more information, see K. Kaukonen and R. Thayer, "A Stream Cipher Encryption Algorithm 'Arcfour'", Internet Draft (expired), <a href="http://www.mozilla.org/projects/security/pki/nss/draft-kaukonen-cipher-arcfour-03.txt"> draft-kaukonen-cipher-arcfour-03.txt</a>.</td> </tr>
	 * <tr> <td>Blowfish</td> <td>The <a href="http://www.schneier.com/blowfish.html">Blowfish block cipher</a> designed by Bruce Schneier.</td> </tr>
	 * <tr> <td>CCM</td> <td>Counter/CBC Mode, as defined in <a href="http://csrc.nist.gov/publications/nistpubs/800-38C/SP800-38C_updated-July20_2007.pdf">NIST Special Publication SP 800-38C</a>.</td> </tr>
	 * <tr> <td>DES</td> <td>The Digital Encryption Standard as described in <a href="http://csrc.nist.gov/publications/fips/index.html">FIPS PUB 46-3</a>.</td> </tr>
	 * <tr> <td>DESede</td> <td>Triple DES Encryption (also known as DES-EDE, 3DES, or Triple-DES). Data is encrypted using the DES algorithm three separate times. It is first encrypted using the first subkey, then decrypted with the second subkey, and encrypted with the third subkey.</td> </tr>
	 * <tr> <td>DESedeWrap</td> <td>The DESede key wrapping algorithm as described in <a href="http://www.ietf.org/rfc/rfc3217.txt">RFC 3217</a> .</td> </tr>
	 * <tr> <td>ECIES</td> <td>Elliptic Curve Integrated Encryption Scheme</td> </tr>
	 * <tr> <td>GCM</td> <td>Galois/Counter Mode, as defined in <a href="http://csrc.nist.gov/publications/nistpubs/800-38D/SP-800-38D.pdf">NIST Special Publication SP 800-38D</a>.</td> </tr>
	 * <tr> <td>PBEWith&lt;digest&gt;And&lt;encryption&gt; PBEWith&lt;prf&gt;And&lt;encryption&gt;</td> <td>The password-based encryption algorithm found in (PKCS5), using the specified message digest (&lt;digest&gt;) or pseudo-random function (&lt;prf&gt;) and encryption algorithm (&lt;encryption&gt;). Examples: <ul> <li><b>PBEWithMD5AndDES</b>: The password-based encryption algorithm as defined in <a href="http://www.rsa.com/rsalabs/node.asp?id=2127">RSA Laboratories, "PKCS #5: Password-Based Encryption Standard," version 1.5, Nov 1993</a>. Note that this algorithm implies <a href="#cbcMode"><i>CBC</i></a> as the cipher mode and <a href="#pkcs5Pad"><i>PKCS5Padding</i></a> as the padding scheme and cannot be used with any other cipher modes or padding schemes.</li> <li><b>PBEWithHmacSHA256AndAES_128</b>: The password-based encryption algorithm as defined in <a href="http://www.rsa.com/rsalabs/node.asp?id=2127">RSA Laboratories, "PKCS #5: Password-Based Cryptography Standard," version 2.0, March 1999</a>.</li> </ul> </td> </tr>
	 * <tr> <td>RC2</td> <td>Variable-key-size encryption algorithms developed by Ron Rivest for RSA Data Security, Inc.</td> </tr>
	 * <tr> <td>RC4</td> <td>Variable-key-size encryption algorithms developed by Ron Rivest for RSA Data Security, Inc. (See note prior for ARCFOUR.)</td> </tr>
	 * <tr> <td>RC5</td> <td>Variable-key-size encryption algorithms developed by Ron Rivest for RSA Data Security, Inc.</td> </tr>
	 * <tr> <td>RSA</td> <td>The RSA encryption algorithm as defined in <a href="http://www.rsa.com/rsalabs/node.asp?id=2125">PKCS #1</a></td> </tr>
	 * </tbody>
	 * </table>
	 */
	private static final String CIPHER_ALGORITHM = "RSA";
	/**
	 * 参考文档: https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#Cipher
	 * <pre>
	 * Signature Algorithms
	 * The algorithm names in this section can be specified when generating an instance of Signature.
	 * <table border="5" cellpadding="5" frame="border" width="90%" summary="Signature Algorithms">
	 * <thead> <tr> <th>Algorithm Name</th> <th>Description</th> </tr> </thead>
	 * <tbody>
	 * <tr> <td>NONEwithRSA</td> <td>The RSA signature algorithm, which does not use a digesting algorithm (for example, MD5/SHA1) before performing the RSA operation. For more information about the RSA Signature algorithms, see <a href="http://www.rsa.com/rsalabs/node.asp?id=2125">PKCS #1</a>.</td> </tr>
	 * <tr> <td>MD2withRSA<br> MD5withRSA<br></td> <td>The MD2/MD5 with RSA Encryption signature algorithm, which uses the MD2/MD5 digest algorithm and RSA to create and verify RSA digital signatures as defined in <a href="http://www.rsa.com/rsalabs/node.asp?id=2125">PKCS #1</a>.</td> </tr>
	 * <tr> <td>SHA1withRSA SHA256withRSA<br> SHA384withRSA<br> SHA512withRSA<br></td> <td>The signature algorithm with SHA-* and the RSA encryption algorithm as defined in the OSI Interoperability Workshop, using the padding conventions described in <a href="http://www.rsa.com/rsalabs/node.asp?id=2125">PKCS #1</a>.</td> </tr>
	 * <tr> <td>NONEwithDSA</td> <td>The Digital Signature Algorithm as defined in <a href="http://csrc.nist.gov/publications/fips/index.html">FIPS PUB 186-2</a>. The data must be exactly 20 bytes in length. This algorithm is also known as rawDSA.</td> </tr>
	 * <tr> <td>SHA1withDSA</td> <td>The DSA with SHA-1 signature algorithm, which uses the SHA-1 digest algorithm and DSA to create and verify DSA digital signatures as defined in <a href="http://csrc.nist.gov/publications/fips/index.html">FIPS PUB 186</a>.</td> </tr>
	 * <tr> <td>NONEwithECDSA<br> SHA1withECDSA<br> SHA256withECDSA<br> SHA384withECDSA<br> SHA512withECDSA<br> <i>(ECDSA)</i><br></td> <td valign="top">The ECDSA signature algorithms as defined in ANSI X9.62. <!-- as defined in <a href="http://csrc.nist.gov/publications/fips/index.html"> FIPS PUB 186</a>. --> <p><b>Note:</b>"ECDSA" is an ambiguous name for the "SHA1withECDSA" algorithm and should not be used. The formal name "SHA1withECDSA" should be used instead.</p> </td> </tr>
	 * <tr> <td>&lt;digest&gt;with&lt;encryption&gt;</td> <td>Use this to form a name for a signature algorithm with a particular message digest (such as MD2 or MD5) and algorithm (such as RSA or DSA), just as was done for the explicitly defined standard names in this section (MD2withRSA, and so on). <p>For the new signature schemes defined in <a href="http://www.rsa.com/rsalabs/node.asp?id=2125">PKCS #1 v 2.0</a>, for which the &lt;digest&gt;with&lt;encryption&gt; form is insufficient, <b>&lt;digest&gt;with&lt;encryption&gt;and&lt;mgf&gt;</b> can be used to form a name. Here, &lt;mgf&gt; should be replaced by a mask generation function such as MGF1. Example: <b>MD5withRSAandMGF1</b>.</p> </td> </tr>
	 * </tbody>
	 * </table>
	 */
	private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

	private Rsa() {
	}

	/**
	 * 创建公钥私钥
	 * @return {@link KeyStore}
	 */
	@SneakyThrows
	public static KeyStore createKeys() {
		final KeyPairGenerator keyPairGeno = KeyPairGenerator.getInstance(KEY_PAIR_GENERATOR_ALGORITHM);
		keyPairGeno.initialize(2048);
		final KeyPair keyPair = keyPairGeno.generateKeyPair();

		final RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		final RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

		final KeyStore keyStore = new KeyStore();
		keyStore.setPublicKey(Base64.encodeBase64String(publicKey.getEncoded()));
		keyStore.setPrivateKey(Base64.encodeBase64String(privateKey.getEncoded()));
		return keyStore;
	}

	/**
	 * 公钥加密
	 * @param data      {@link String} 明文
	 * @param publicKey {@link String} 公钥
	 * @return {@link String} 密文
	 */
	public static String encryptByPublicKey(final String data, final String publicKey) {
		return encryptByPublicKey(data, getPublicKey(publicKey));
	}

	/**
	 * 公钥加密
	 * @param data      {@link String} 明文
	 * @param publicKey {@link RSAPublicKey} 公钥
	 * @return {@link String} 密文
	 */
	@SneakyThrows
	public static String encryptByPublicKey(final String data, final RSAPublicKey publicKey) {
		final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		final byte[] bytes = cipher.doFinal(data.getBytes(UTF_8));
		return Base64.encodeBase64String(bytes);
	}

	/**
	 * 获取公钥对象，初始化公钥实例
	 * @param pubKey {@link String} 公钥字符串
	 * @return {@link RSAPublicKey}
	 */
	@SneakyThrows
	public static RSAPublicKey getPublicKey(final String pubKey) {
		final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(pubKey));
		final KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
		return (RSAPublicKey) keyFactory.generatePublic(keySpec);
	}

	/**
	 * 公钥解密
	 * @param data         {@link String} 密文
	 * @param rsaPublicKey {@link String} 公钥
	 * @return {@link String} 明文
	 */
	public static String decryptByPublicKey(final String data, final String rsaPublicKey) {
		return decryptByPublicKey(data, getPublicKey(rsaPublicKey));
	}

	/**
	 * 公钥解密
	 * @param data         {@link String} 密文
	 * @param rsaPublicKey {@link RSAPublicKey} 公钥
	 * @return {@link String} 明文
	 */
	@SneakyThrows
	public static String decryptByPublicKey(final String data, final RSAPublicKey rsaPublicKey) {
		final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, rsaPublicKey);
		final byte[] inputData = Base64.decodeBase64(data);
		final byte[] bytes = cipher.doFinal(inputData);
		return new String(bytes, UTF_8);
	}

	/**
	 * 私钥加密
	 * @param data       {@link String} 明文
	 * @param privateKey {@link String} 私钥
	 * @return {@link String} 密文
	 */
	public static String encryptByPrivateKey(final String data, final String privateKey) {
		return encryptByPrivateKey(data, getPrivateKey(privateKey));
	}

	/**
	 * 私钥加密
	 * @param data       {@link String} 明文
	 * @param privateKey {@link RSAPrivateKey} 私钥
	 * @return {@link String} 密文
	 */
	@SneakyThrows
	public static String encryptByPrivateKey(final String data, final RSAPrivateKey privateKey) {
		final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		final byte[] bytes = cipher.doFinal(data.getBytes(UTF_8));
		return Base64.encodeBase64String(bytes);
	}

	/**
	 * 通过私钥 byte[] 将公钥还原，适用于RSA算法
	 * @param priKey {@link String} 私钥字符串
	 * @return {@link RSAPrivateKey}
	 */
	@SneakyThrows
	public static RSAPrivateKey getPrivateKey(final String priKey) {
		final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(priKey));
		final KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
		return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
	}

	/**
	 * 私钥解密
	 * @param data       {@link String} 密文
	 * @param privateKey {@link String} 私钥
	 * @return {@link String} 明文
	 */
	public static String decryptByPrivateKey(final String data, final String privateKey) {
		return decryptByPrivateKey(data, getPrivateKey(privateKey));
	}

	/**
	 * 私钥解密
	 * @param data       {@link String} 密文
	 * @param privateKey {@link RSAPrivateKey} 私钥
	 * @return {@link String} 明文
	 */
	@SneakyThrows
	public static String decryptByPrivateKey(final String data, final RSAPrivateKey privateKey) {
		final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		final byte[] inputData = Base64.decodeBase64(data);
		final byte[] bytes = cipher.doFinal(inputData);
		return new String(bytes, UTF_8);
	}

	/**
	 * 用私钥对数据生成数字签名
	 * @param data       {@link String} 被签名数据
	 * @param privateKey {@link String} 私钥
	 * @return {@link String} 数字签名串
	 */
	@SneakyThrows
	public static String signatureByPrivateKey(final String data, final String privateKey) {
		return signatureByPrivateKey(data, getPrivateKey(privateKey));
	}

	/**
	 * 用私钥对数据生成数字签名
	 * @param data       {@link String} 被签名数据
	 * @param privateKey {@link String} 私钥
	 * @return {@link String} 数字签名串
	 */
	@SneakyThrows
	public static String signatureByPrivateKey(final String data, final RSAPrivateKey privateKey) {
		final Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(privateKey);
		signature.update(data.getBytes(UTF_8));
		return Base64.encodeBase64String(signature.sign());
	}

	/**
	 * 校验数字签名
	 * @param data      {@link String} 被签名数据
	 * @param publicKey {@link String} 公钥
	 * @param sign      {@link String} 数字签名串
	 * @return {@link Boolean} true：校验成功， false：校验失败
	 */
	@SneakyThrows
	public static boolean verifyByPublicKey(final String data, final String publicKey, final String sign) {
		return verifyByPublicKey(data, getPublicKey(publicKey), sign);
	}

	/**
	 * 校验数字签名
	 * @param data      {@link String} 被签名数据
	 * @param publicKey {@link String} 公钥
	 * @param sign      {@link String} 数字签名串
	 * @return {@link Boolean} true：校验成功， false：校验失败
	 */
	@SneakyThrows
	public static boolean verifyByPublicKey(final String data, final RSAPublicKey publicKey, final String sign) {
		final Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(publicKey);
		signature.update(data.getBytes(UTF_8));
		return signature.verify(Base64.decodeBase64(sign));
	}

	@Getter
	@Setter
	@ToString
	public static class KeyStore {
		/**
		 * 公钥
		 */
		private String publicKey;
		/**
		 * 私钥
		 */
		private String privateKey;
	}

	//    @SneakyThrows
	//    public static void main(String[] args) {
	//        final KeyStore keys = createKeys();
	//        final String privateKey = keys.getPrivateKey();
	//        final String publicKey = keys.getPublicKey();
	//        System.out.println("私钥：" + privateKey);
	//        System.out.println("公钥：" + publicKey);
	//        Files.write(Paths.get("logs/private.pkcs8"), privateKey.getBytes());
	//        Files.write(Paths.get("logs/public.pkcs8"), publicKey.getBytes());
	//        Files.write(
	//                Paths.get("logs/private.pem"),
	//                ("-----BEGIN RSA PRIVATE KEY-----\n"
	//                        + privateKey.replaceAll("(.{64})", "$1\n")
	//                        + "\n-----END RSA PRIVATE KEY-----"
	//                ).getBytes()
	//        );
	//        Files.write(
	//                Paths.get("logs/public.pem"),
	//                ("-----BEGIN RSA PUBLIC KEY-----\n"
	//                        + publicKey.replaceAll("(.{64})", "$1\n")
	//                        + "\n-----END RSA PUBLIC KEY-----"
	//                ).getBytes()
	//        );
	//        String privateContent = encryptByPrivateKey("zh_CN 中文", privateKey);
	//        System.out.println("私钥加密：" + privateContent);
	//        try {
	//            System.out.println("私钥加密 => 私钥解密：" + decryptByPrivateKey(privateContent, privateKey));
	//        } catch (Exception e) {
	//            System.out.println("私钥加密 => 私钥解密：失败");
	//        }
	//        try {
	//            System.out.println("私钥加密 => 公钥解密：" + decryptByPublicKey(privateContent, publicKey));
	//        } catch (Exception e) {
	//            System.out.println("私钥加密 => 公钥解密：失败");
	//        }
	//
	//        String publicContent = encryptByPublicKey("zh_CN 中文", publicKey);
	//        System.out.println("公钥加密：" + publicContent);
	//        try {
	//            System.out.println("公钥加密 => 公钥解密：" + decryptByPublicKey(publicContent, publicKey));
	//        } catch (Exception e) {
	//            System.out.println("公钥加密 => 公钥解密：失败");
	//        }
	//        try {
	//            System.out.println("公钥加密 => 私钥解密：" + decryptByPrivateKey(publicContent, privateKey));
	//        } catch (Exception e) {
	//            System.out.println("公钥加密 => 私钥解密：失败");
	//        }
	//
	//        final String signature = signatureByPrivateKey(privateContent, privateKey);
	//        System.out.println("私钥签名：" + signature);
	//        System.out.println("公钥验签：" + verifyByPublicKey(privateContent, publicKey, signature));
	//    }

	//    public static void main(String[] args) {
	//        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0Xi8dPe1Po46Nl4jFPdly51A+CvxVj820Yh6EVIEUih4YNTHwgmSyh2ByBE005fECF60WxZGcqiWm1+NKk6RkibV7VHYDDd+AOsN7BGQApWs36kg/FIvfsDdF8ok1fxoOCSAF4a+ynn8bQhHaDUPgyjs6fQS2MKtybn9pn6vmPBTO3c6KnDAOzKmTVFwh2NVA0rgh1cBRqF1nLB8JY3pAUq/zB0vqsUCYdBKuCVm2r8rOdILjGSFrXsA9HruN2khIZ0k4rhRwso+Ff5hPB1Caeyz0kGukT/Sdo6Tl7ba0XI7s3aQwnxd293qe+Hs8IBWyj9/8XNKrBBnLkAh9w8lPQIDAQAB";
	//        String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDReLx097U+jjo2XiMU92XLnUD4K/FWPzbRiHoRUgRSKHhg1MfCCZLKHYHIETTTl8QIXrRbFkZyqJabX40qTpGSJtXtUdgMN34A6w3sEZAClazfqSD8Ui9+wN0XyiTV/Gg4JIAXhr7KefxtCEdoNQ+DKOzp9BLYwq3Juf2mfq+Y8FM7dzoqcMA7MqZNUXCHY1UDSuCHVwFGoXWcsHwljekBSr/MHS+qxQJh0Eq4JWbavys50guMZIWtewD0eu43aSEhnSTiuFHCyj4V/mE8HUJp7LPSQa6RP9J2jpOXttrRcjuzdpDCfF3b3ep74ezwgFbKP3/xc0qsEGcuQCH3DyU9AgMBAAECggEADd8k+8n7oqzghgEt6ZGImDdNuiGO1dtR0gZrrwHzb01my2LL57mikufpVEpvaSO/w54h63ZKLAWmE5v8STTyxu7zc8fbutNh8MWgImX3V3R6AkW8/Xij1I0JFy43ykPw0ub35Jbzw4zYfo/xyqDY6iKnrcaQCPK7d4Rtm7z/++yvkbBtD6KpTny2cjf8NLV0l56rBe2mbT498LwqYyFh1evH6kUugpqA7lfg76UPS7iRM1vDnnfh3qEEwNlQZbLvkQ7nbFmhy9QB3a7nP07uDZObAL+Y6D4/i1ku0Q3mTJldr2jnPuhA0Mj3i0tMgsvJ4qJ1IVHbcOkQA6EXvraSMQKBgQD6zN6jJl8BWtG5VAkvYF0F04mfecBa+teAFuhNDAKj7hDRiYi/Sroc55C0ekP2SaLn2zU5YaZqWMtzqq7Rrvp4mh6u6OcxiokDc044VtqKXCVkzXoBKbGbzaTCsPtfGpFw1Jay3jow0LT8vAwHokS74eZ61gbv2ohj8lQhssbYAwKBgQDV0INtuNG/wKSWNYppdUKiaV55LDi5AD9FkXR5syhaHYMzgb+SIL/kuEELiMsWY9EtqsQMUEmHt5y2d+unkAWWmQtKmyQNQg7fdgyYXe+WDEPacOzb5SC/ad+K7gEgBTtCr0wCPQDV6zPDQ4+Yf//e8+OIipzqHA9j7jsUVV6pvwKBgA8uXX+kr/WGCNHeC1xJyfezUU5M8V7QdkDzogYf5v0DSRNV7ugdXtynC7RQ/lInLOw58aPSjF89lrsepsxceh7YvWi7AluWVirWTPLOFxZu83iBk5QEcRLMRN/gFKcyFYGt7J1RJFaWG45814GSyfRRfUEMOYlFQiJCpeMQTkb/AoGAHRn8SXrWUZsOy8MNy7zCDQj6atOyBLwb4IQjrkzMOTe7G8+VG3aJp7/MWp2LxshfOC/97w8ecvJyygMYd/4KRK0f9E+ZYJSVIaUXocVnTqzsr1afZm6RPnxSiL5MOOd9YV4qokNbUcJpkTIo4UDKurXHSlZTdSnMjnVmZSYZdFECgYEA8DwVl9x4G2R43lhFfnaU/hxJR/0Z8fV1kH++SProEn0khZJVAGc06Hr82h1UkAMx8c2O/SAJjI4UnjHmvo6cpX+fd9/myl46aB8prR4UNuBHs9/A7sR3zizVJL9ItKbTgsqr3b0aF/IkJMrv8sv6ejmCVn5jACo8bnfmOG3eN7g=";
	//        final String s = Rsa.encryptByPublicKey("18555305562", publicKey);
	//        System.out.println(s);
	//        final String s1 = Rsa.decryptByPrivateKey(s, privateKey);
	//        System.out.println(s1);
	//    }
}
