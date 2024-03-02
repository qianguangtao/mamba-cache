package com.app.kit;

import lombok.extern.slf4j.Slf4j;

/**
 * @author qiangt
 * @since 2023/6/29 16:27
 */
@Slf4j
public class BannerUtil {

	public static void info(final String message) {
		log.info("***************************************");
		log.info(message);
		log.info("***************************************");
	}

	public static void debug(final String message) {
		log.debug("***************************************");
		log.debug(message);
		log.debug("***************************************");
	}

}
