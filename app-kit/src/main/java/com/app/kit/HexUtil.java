package com.app.kit;

/**
 * 进制转化
 */
public class HexUtil {

	public static void main(final String[] args) {
		final String str1 = "abcedefghijklmnopqrstuvwxyz";
		// String str1 = "1";
		final String hexStr = HexUtil.byte2hex(str1.getBytes());
		System.out.println(hexStr);
		final String str2 = new String(HexUtil.hex2byte(hexStr));
		System.out.println(str2);
		System.out.println(str1.equals(str2));
	}

	/**
	 * 二进制byte数组转十六进制byte数组
	 * byte array to hex
	 * @param b byte array
	 * @return hex string
	 */
	public static String byte2hex(final byte[] b) {
		final StringBuilder hs = new StringBuilder();
		String stmp;
		for (final byte value : b) {
			stmp = Integer.toHexString(value & 0xFF).toUpperCase();
			if (stmp.length() == 1) {
				hs.append("0").append(stmp);
			} else {
				hs.append(stmp);
			}
		}
		return hs.toString();
	}

	/**
	 * 十六进制byte数组转二进制byte数组
	 * hex to byte array
	 * @param hex hex string
	 * @return byte array
	 */
	public static byte[] hex2byte(final String hex)
			throws IllegalArgumentException {
		if (hex.length() % 2 != 0) {
			throw new IllegalArgumentException("invalid hex string");
		}
		final char[] arr = hex.toCharArray();
		final byte[] b = new byte[hex.length() / 2];
		for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
			final String swap = String.valueOf(arr[i++]) + arr[i];
			final int byteint = Integer.parseInt(swap, 16) & 0xFF;
			b[j] = Integer.valueOf(byteint).byteValue();
		}
		return b;
	}

}
