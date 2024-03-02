package com.app.kit;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtils {

    // 默认占位符正则：{{xxx}}
    private static final Pattern DEFAULT_PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{([\\w]+)\\}\\}");

    /**
     * 验证手机号是否合法
     */
    public static boolean isCellphone(final String cellphone) {

        if (StringUtils.isEmpty(cellphone) || cellphone.length() != 11) {
            return false;
        }

        // 移动号段正则表达式
        String pattern = "^((13[4-9])|(147)|(15[0-2,7-9])|(178)|(18[2-4,7-8]))\\d{8}|(1705)\\d{7}$";
        if (Pattern.compile(pattern).matcher(cellphone).matches()) {
            return true;
        }

        // 联通号段正则表达式
        pattern = "^((13[0-2])|(145)|(15[5-6])|(176)|(18[5,6]))\\d{8}|(1709)\\d{7}$";
        if (Pattern.compile(pattern).matcher(cellphone).matches()) {
            return true;
        }

        // 电信号段正则表达式
        pattern = "^((133)|(153)|(177)|(18[0,1,9])|(149))\\d{8}$";
        if (Pattern.compile(pattern).matcher(cellphone).matches()) {
            return true;
        }

        // 虚拟运营商正则表达式
        pattern = "^((170))\\d{8}|(1718)|(1719)\\d{7}$";
        return Pattern.compile(pattern).matcher(cellphone).matches();
    }

    /**
     * 是否是合法的http连接
     */
    public static boolean isHttpUrl(final String url) {
        final Pattern pattern = Pattern.compile("(ht|f)tp(s?)\\:\\/\\/[0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*(:(0-9)*)*(\\/?)([a-zA-Z0-9\\-\\.\\?\\,\\'\\/\\\\\\+&amp=;%$#_]*)?");
        return pattern.matcher(url).matches();
    }

    /**
     * 模板占位符填充，"姓名：{{name}}，年龄：{{age}}"转"姓名：周杰伦，年龄：18"
     * 占位符默认{{xxx}}
     */
    public static String formatPlaceholder(final String templateStr, final Map<String, Object> param) {
        return formatPlaceholder(templateStr, DEFAULT_PLACEHOLDER_PATTERN, param);
    }

    /**
     * 模板按照指定占位符填充，"姓名：{{name}}，年龄：{{age}}"转"姓名：周杰伦，年龄：18"
     */
    public static String formatPlaceholder(final String templateStr, final Pattern pattern, final Map<String, Object> param) {
        if (StrUtil.isEmpty(templateStr) || MapUtil.isEmpty(param) || ObjectUtil.isNull(pattern)) {
            throw new IllegalArgumentException("Template can not be empty");
        }
        StringBuilder sb = new StringBuilder();
        Matcher matcher = pattern.matcher(templateStr);
        int lastEndIndex = 0;
        while (matcher.find()) {
            String placeholder = matcher.group(0);
            String key = matcher.group(1);
            Object value = param.get(key);
            sb.append(templateStr, lastEndIndex, matcher.start());
            sb.append(value);
            lastEndIndex = matcher.end();
        }
        sb.append(templateStr, lastEndIndex, templateStr.length());
        return sb.toString();
    }

    /**
     * @param templateStr
     * @param params
     * @return 使用params按照顺序填充Pattern后的结果String
     */
    public static String formatPlaceholder(final String templateStr, final Object... params) {
        return formatPlaceholder(templateStr, DEFAULT_PLACEHOLDER_PATTERN, params);
    }

    /**
     * @param templateStr
     * @param pattern
     * @param params
     * @return 使用params按照顺序填充Pattern后的结果String
     */
    public static String formatPlaceholder(final String templateStr, final Pattern pattern, final Object... params) {
        if (StrUtil.isEmpty(templateStr) || ObjectUtil.isEmpty(params) || ObjectUtil.isNull(pattern)) {
            throw new IllegalArgumentException("Template can not be empty");
        }
        StringBuilder sb = new StringBuilder();
        Matcher matcher = pattern.matcher(templateStr);
        int lastEndIndex = 0;
        int paramIndex = 0;
        while (matcher.find()) {
            String placeholder = matcher.group(0);
            String key = matcher.group(1);
            sb.append(templateStr, lastEndIndex, matcher.start());
            sb.append(params[paramIndex]);
            paramIndex++;
            lastEndIndex = matcher.end();
        }
        sb.append(templateStr, lastEndIndex, templateStr.length());
        return sb.toString();
    }

    // public static void main(String[] args) {
    //
    //    Map<String, Object> map = new HashMap<>();
    //    map.put("name", "小明");
    //    map.put("age", "18");
    //    String originalString = "这是{{name}}的{{age}}岁生日。";
    //
    //    String patternString = "\\{\\{([\\w]+)\\}\\}";
    //    Pattern pattern = Pattern.compile(patternString);
    //    System.out.println(PatternUtils.formatPlaceholder(originalString, map));
    //    System.out.println(PatternUtils.formatPlaceholder(originalString, pattern, "小明", 18));
    // }

}
