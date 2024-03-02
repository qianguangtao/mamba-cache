package com.app.kit;

import cn.hutool.core.util.RandomUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 验证码工具类
 */
@Slf4j
public abstract class Captcha {

	/**
	 * 验证码图片的宽度
	 */
	private static final int AUTH_CODE_IMG_WIDTH = 63;
	/**
	 * 验证码图片的高度
	 */
	private static final int AUTH_CDOE_IMG_HEIGHT = 37;
	/**
	 * 验证码缓存时长：5分钟
	 */
	private static final int AUTH_CODE_CACHE_EXPIRATION = 5 * 60;
	/**
	 * 验证码缓存key前缀
	 */
	private static final String AUTH_CODE_CACHE_KEY_PREFIX = "captcha:";
	/**
	 * 验证码缓存
	 */
	private static final Map<String, String> cache = new HashMap<>();

	public static JsonResult checkCaptcha(final HttpServletRequest request, final String key) {
		return checkCaptcha(request.getSession().getId(), key);
	}

	public static JsonResult checkCaptcha(final String sessionId, final String key) {
		final String captcha = getCaptchaFromCache(sessionId);
		removeCaptchaFromCache(sessionId);
		if (captcha == null) {
			return new JsonResult(JsonResult.FAILED_CODE, "验证码过期，请重试");
		}
		if (key.equals(captcha)) {
			return new JsonResult(JsonResult.SUCCESS_CODE, "校验成功");
		} else {
			return new JsonResult(JsonResult.FAILED_CODE, "验证码错误，请重试");
		}
	}

	private static String getCaptchaFromCache(final String key) {
		final String captchaCacheKey = AUTH_CODE_CACHE_KEY_PREFIX + key;
		return cache.get(captchaCacheKey);
	}

	private static void removeCaptchaFromCache(final String key) {
		final String captchaCacheKey = AUTH_CODE_CACHE_KEY_PREFIX + key;
		cache.remove(captchaCacheKey);
	}

	/**
	 * 绘制普通的验证码
	 */
	public static void drawCaptchaImg(final HttpServletRequest request, final HttpServletResponse response) {
		final int width = AUTH_CODE_IMG_WIDTH;
		final int height = AUTH_CDOE_IMG_HEIGHT;
		// 设置response头信息：禁止缓存
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		// 生成缓冲区image类
		final BufferedImage image = new BufferedImage(width, height, 1);
		// 产生image类的Graphics用于绘制操作
		final Graphics g = image.getGraphics();
		// Graphics类的样式
		g.setColor(getRandColor(200, 250));
		g.setFont(new Font("Times New Roman", 0, 28));
		g.fillRect(0, 0, width, height);
		// 绘制干扰线
		final Random random = new Random();
		final int lineCount = 40;
		for (int i = 0; i < lineCount; i++) {
			g.setColor(getRandColor(130, 200));
			final int x1 = random.nextInt(width);
			final int y1 = random.nextInt(height);
			final int x2 = x1 + random.nextInt(12);
			final int y2 = y1 + random.nextInt(12);
			g.drawLine(x1, y1, x2, y2);
		}
		// 绘制字符
		final String captcha = Captcha.generateCaptcha(request.getSession().getId());
		final char[] chars = captcha.toCharArray();
		for (int i = 0, length = chars.length; i < length; i++) {
			final char c = chars[i];
			g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
			g.drawString(String.valueOf(c), 13 * i + 6, 28);
		}
		g.dispose();
		try {
			ImageIO.write(image, "JPEG", response.getOutputStream());
			log.debug("draw auth code");
			response.getOutputStream().flush();
		} catch (final IOException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			throw new IllegalStateException(e.getMessage());
		}
	}

	/**
	 * 创建颜色
	 */
	private static Color getRandColor(int fc, int bc) {
		final Random random = new Random();
		final int limit = 255;
		if (fc > limit) {
			fc = limit;
		}
		if (bc > limit) {
			bc = limit;
		}
		final int r = fc + random.nextInt(bc - fc);
		final int g = fc + random.nextInt(bc - fc);
		final int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}

	public static String generateCaptcha(final String sessionId) {
		final String value = RandomUtil.randomString(4).toLowerCase();
		saveCaptchaToCache(sessionId, value);
		return value;
	}

	private static void saveCaptchaToCache(final String key, final String captcha) {
		final String captchaCacheKey = AUTH_CODE_CACHE_KEY_PREFIX + key;
		cache.put(captchaCacheKey, captcha);
	}

	/**
	 * 绘制背景透明的验证码
	 */
	@SuppressWarnings("unused")
	public static void drawTranslucentCaptchaImg(final HttpServletRequest request, final HttpServletResponse response)
			throws IOException {
		final int width = AUTH_CODE_IMG_WIDTH;
		final int height = AUTH_CDOE_IMG_HEIGHT;
		// 设置response头信息：禁止缓存
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		// 生成缓冲区image类
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// 产生image类的Graphics用于绘制操作
		Graphics2D g = image.createGraphics();
		image = g.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
		g.dispose();
		g = image.createGraphics();
		// Graphics类的样式
		g.setColor(getRandColor(200, 250));
		g.setFont(new Font("Times New Roman", 0, 28));
		// 绘制字符
		final String captcha = Captcha.generateCaptcha(request.getSession().getId());
		final char[] chars = captcha.toCharArray();
		for (int i = 0, length = chars.length; i < length; i++) {
			final char c = chars[i];
			g.setColor(new Color(255, 255, 255));
			g.drawString(String.valueOf(c), 13 * i + 6, 28);
		}
		g.dispose();
		ImageIO.write(image, "PNG", response.getOutputStream());
		response.getOutputStream().flush();
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class JsonResult {

		public static final Integer SUCCESS_CODE = 0;
		public static final Integer FAILED_CODE = 1;
		public static final String SUCCESS_MSG = "成功";
		public static final String FAILED_MSG = "失败";

		/**
		 * 0：成功，非0：失败
		 */
		private Integer code = SUCCESS_CODE;
		/**
		 * 结果描述信息
		 */
		private String msg = SUCCESS_MSG;
		/**
		 * 存放的数据
		 */
		private Object data;

		public JsonResult(final Integer code, final String msg) {
			this.code = code;
			this.msg = msg;
		}

		public boolean success() {
			return SUCCESS_CODE.equals(this.code);
		}

	}

}
