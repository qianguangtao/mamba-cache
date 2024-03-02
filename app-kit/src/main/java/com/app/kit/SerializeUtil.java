package com.app.kit;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * 序列化工具类
 */
@Slf4j
public final class SerializeUtil {

	/**
	 * 序列化
	 */
	public static byte[] serialize(final Object object) {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			return baos.toByteArray();
		} catch (final IOException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
			} catch (final Exception e) {
				log.error("", e);
			}
			try {
				if (baos != null) {
					baos.close();
				}
			} catch (final Exception e) {
				log.error("", e);
			}
		}
	}

	/**
	 * 反序列化
	 */
	public static Object deserialize(final byte[] bytes) {
		return deserialize(bytes, Object.class);
	}

	/**
	 * 反序列化
	 */
	@SuppressWarnings("unchecked")
	public static <K> K deserialize(final byte[] bytes, final Class<K> cls) {
		final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(bais);
			return (K) ois.readObject();
		} catch (final IOException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		} catch (final ClassNotFoundException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
			} catch (final Exception e) {
				log.error("", e);
			}
			try {
				if (bais != null) {
					bais.close();
				}
			} catch (final Exception e) {
				log.error("", e);
			}
		}
	}

}
