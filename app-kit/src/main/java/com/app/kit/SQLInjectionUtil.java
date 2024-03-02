package com.app.kit;

import lombok.extern.slf4j.Slf4j;

/**
 * sql注入处理工具类
 */
@Slf4j
public class SQLInjectionUtil {

	final static String xssStr = "'|and |exec |insert |select |delete |update |drop |count |chr |mid |master |truncate |char |declare |;|or |+|,";

	/**
	 * sql注入过滤处理，遇到注入关键字抛异常
	 */
	public static String filterContent(String value) {
		if (value == null || "".equals(value)) {
			return value;
		}
		value = value.toLowerCase();// 统一转为小写
		final String[] xssArr = xssStr.split("\\|");
		for (final String s : xssArr) {
			if (value.contains(s)) {
				log.error("请注意，值可能存在SQL注入风险!---> {}", value);
				throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
			}
		}
		return value;
	}

	/**
	 * sql注入过滤处理，遇到注入关键字抛异常
	 */
	public static void filterContent(final String[] values) {
		final String[] xssArr = xssStr.split("\\|");
		for (String value : values) {
			if (value == null || "".equals(value)) {
				return;
			}
			value = value.toLowerCase();// 统一转为小写
			for (final String s : xssArr) {
				if (value.contains(s)) {
					log.error("请注意，值可能存在SQL注入风险!---> {}", value);
					throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
				}
			}
		}
	}

	/**
	 * 特殊方法(不通用) 仅用于字典条件SQL参数，注入过滤
	 */
	public static void specialFilterContent(String value) {
		final String specialXssStr = "exec |insert |select |delete |update |drop |count |chr |mid |master |truncate |char |declare |;|+|,";
		final String[] xssArr = specialXssStr.split("\\|");
		if (value == null || "".equals(value)) {
			return;
		}
		value = value.toLowerCase();// 统一转为小写
		for (final String s : xssArr) {
			if (value.contains(s)) {
				log.error("请注意，值可能存在SQL注入风险!---> {}", value);
				throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
			}
		}
	}

}
