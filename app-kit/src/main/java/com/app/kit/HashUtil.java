package com.app.kit;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashUtil {
	private static final java.security.SecureRandom random = new java.security.SecureRandom();
	private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
	private static final char[] CHAR_ARRAY = "_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			.toCharArray();

	public static String md5(final String srcStr) {
		return hash("MD5", srcStr);
	}

	public static String hash(final String algorithm, final String srcStr) {
		try {
			final MessageDigest md = MessageDigest.getInstance(algorithm);
			final byte[] bytes = md.digest(srcStr.getBytes(StandardCharsets.UTF_8));
			return toHex(bytes);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String toHex(final byte[] bytes) {
		final StringBuilder ret = new StringBuilder(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
			ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
		}
		return ret.toString();
	}

	public static String sha1(final String srcStr) {
		return hash("SHA-1", srcStr);
	}

	public static String sha256(final String srcStr) {
		return hash("SHA-256", srcStr);
	}

	public static String sha384(final String srcStr) {
		return hash("SHA-384", srcStr);
	}

	public static String sha512(final String srcStr) {
		return hash("SHA-512", srcStr);
	}

	public static String generateSaltForSha256() {
		return generateSalt(32);
	}

	/**
	 * md5 128bit 16bytes sha1 160bit 20bytes sha256 256bit 32bytes sha384
	 * 384bit 48bytes sha512 512bit 64bytes
	 */
	public static String generateSalt(final int saltLength) {
		final StringBuilder salt = new StringBuilder();
		for (int i = 0; i < saltLength; i++) {
			salt.append(CHAR_ARRAY[random.nextInt(CHAR_ARRAY.length)]);
		}
		return salt.toString();
	}

	public static String generateSaltForSha512() {
		return generateSalt(64);
	}

	public static boolean slowEquals(final byte[] a, final byte[] b) {
		if (a == null || b == null) {
			return false;
		}

		int diff = a.length ^ b.length;
		for (int i = 0; i < a.length && i < b.length; i++) {
			diff |= a[i] ^ b[i];
		}
		return diff == 0;
	}
}
