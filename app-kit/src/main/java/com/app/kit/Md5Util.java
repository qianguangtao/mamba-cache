package com.app.kit;

import java.security.MessageDigest;

/**
 * md5加密工具类
 */
public class Md5Util {

	/**
	 * 加密常量
	 */
	private static final String[] HEXDIGITS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
			"a", "b", "c", "d", "e", "f"};

	/**
	 * md5加密  默认字节  32位
	 * @param origin 加密字符串
	 * @return 加密后的md5字符串
	 */
	public static String encodeMD5(final String origin) {
		return encodeMD5(origin, null, 32);
	}

	/**
	 * md5加密
	 * @param origin      加密字符串
	 * @param charsetname 字符编码格式
	 * @param wex         md5编码长度
	 * @return 加密后的md5字符串
	 */
	public static String encodeMD5(final String origin, final String charsetname, final Integer wex) {
		String resultString = null;
		try {
			resultString = origin;
			final MessageDigest md = MessageDigest.getInstance("MD5");
			if (charsetname == null || "".equals(charsetname)) {
				resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
			} else {
				resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
			}
		} catch (final Exception exception) {
		}

		if (wex == 16) {
			return resultString.substring(8, 24);
		}
		if (wex == 32) {
			return resultString;
		}
		return resultString;
	}

	/**
	 * 进行md5循环字节加密
	 * @param b 字节集合
	 * @return 加密后的md5字符串
	 */
	private static String byteArrayToHexString(final byte[] b) {
		final StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}

		return resultSb.toString();
	}

	/**
	 * 字节换算
	 * @param b 字节
	 * @return 换算后的字符
	 */
	private static String byteToHexString(final byte b) {
		int n = b;
		if (n < 0) {
			n += 256;
		}
		final int d1 = n / 16;
		final int d2 = n % 16;
		return HEXDIGITS[d1] + HEXDIGITS[d2];
	}

	/**
	 * md5加密 默认字节
	 * @param origin 加密字符串
	 * @param wex    md5编码长度
	 * @return 加密后的md5字符串
	 */
	public static String encodeMD5(final String origin, final Integer wex) {
		return encodeMD5(origin, null, wex);
	}

	/**
	 * md5加密   32位
	 * @param origin      加密字符串
	 * @param charsetname 字符编码格式
	 * @return 加密后的md5字符串
	 */
	public static String encodeMD5(final String origin, final String charsetname) {
		return encodeMD5(origin, charsetname, 32);
	}

	public static String md5(String str) {
		try {
			final MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			final byte[] b = md.digest();

			int i;

			final StringBuffer buf = new StringBuffer();
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			str = buf.toString();
		} catch (final Exception e) {
			e.printStackTrace();

		}
		return str;
	}

	//    public static void main(String[] args) {
	//        System.out.println(md5("123456"));
	//    }
}
