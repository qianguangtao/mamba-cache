package com.app.kit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

public final strictfp class MathUtil {
	// 默认运算精度
	private static final int DEF_SCALE = 10;

	private MathUtil() {
	}

	/**
	 * 提供(相对)精确的加法运算。
	 * @param num1 被加数
	 * @param num2 加数
	 * @return 两个参数的和
	 */
	public static Double add(final Object num1, final Object num2) {
		final BigDecimal result = bigDecimal(num1).add(bigDecimal(num2));
		return result.setScale(DEF_SCALE, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * 提供数据类型转换为BigDecimal
	 * @param object 原始数据
	 * @return BigDecimal
	 */
	public static BigDecimal bigDecimal(final Object object) {
		if (object == null) {
			throw new NullPointerException();
		}
		final BigDecimal result;
		try {
			result = new BigDecimal(String.valueOf(object).replaceAll(",", ""));
		} catch (final NumberFormatException e) {
			throw new NumberFormatException("Please give me a numeral.Not " + object);
		}
		return result;
	}

	/**
	 * 提供(相对)精确的减法运算。
	 * @param num1 被减数
	 * @param num2 减数
	 * @return 两个参数的差
	 */
	public static Double subtract(final Object num1, final Object num2) {
		final BigDecimal result = bigDecimal(num1).subtract(bigDecimal(num2));
		return result.setScale(DEF_SCALE, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * 提供(相对)精确的乘法运算。
	 * @param num1 被乘数
	 * @param num2 乘数
	 * @return 两个参数的积
	 */
	public static Double multiply(final Object num1, final Object num2) {
		final BigDecimal result = bigDecimal(num1).multiply(bigDecimal(num2));
		return result.setScale(DEF_SCALE, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * 提供(相对)精确的除法运算，当发生除不尽的情况时，精度为10位，以后的数字四舍五入。
	 * @param num1 被除数
	 * @param num2 除数
	 * @return 两个参数的商
	 */
	public static Double divide(final Object num1, final Object num2) {
		return divide(num1, num2, DEF_SCALE);
	}

	/**
	 * 提供(相对)精确的除法运算。 当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
	 * @param num1  被除数
	 * @param num2  除数
	 * @param scale 表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static Double divide(final Object num1, Object num2, Integer scale) {
		if (scale == null) {
			scale = DEF_SCALE;
		}
		num2 = num2 == null || Math.abs(new Double(num2.toString())) == 0 ? 1 : num2;
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		final BigDecimal result = bigDecimal(num1).divide(bigDecimal(num2), scale, RoundingMode.HALF_UP);
		return result.doubleValue();
	}

	/**
	 * 提供精确的小数位四舍五入处理。
	 * @param num   需要四舍五入的数字
	 * @param scale 小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static Double round(final Object num, final int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		final BigDecimal result = bigDecimal(num).divide(bigDecimal("1"), scale, RoundingMode.HALF_UP);
		return result.doubleValue();
	}

	/**
	 * 获取start到end区间的随机数,不包含start+end
	 */
	public static BigDecimal getRandom(final int start, final int end) {
		return new BigDecimal(start + Math.random() * end);
	}

	/**
	 * 格式化
	 */
	public static String format(final Object obj, String pattern) {
		if (obj == null) {
			return null;
		}
		if (pattern == null || "".equals(pattern)) {
			pattern = "#";
		}
		final DecimalFormat format = new DecimalFormat(pattern);
		return format.format(bigDecimal(obj));
	}

	/**
	 * 是否数字
	 */
	public static boolean isNumber(final Object object) {
		final Pattern pattern = Pattern.compile("\\d+(.\\d+)?$");
		return pattern.matcher(object.toString()).matches();
	}

	//	public static final void main(String[] args) {
	//		System.out.println(add(1.000001, 2.10));
	//	}
}
