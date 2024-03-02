package com.app.kit;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 系统工具类
 * @author qiangt 2016-11-23 .
 */
@Slf4j
@SuppressWarnings({"unchecked"})
public final class CommonKit {
    public static final String BASE_62_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final int BASE = BASE_62_CHAR.length();
    private static final Random RANDOM = new Random();
    /**
     * 占位符匹配正则
     */
    private static final Pattern PLACEHOLDER = Pattern.compile("(?<=\\{)(\\w+)?");

    /**
     * 获取UUID；中间的 - 剔除
     * @return String
     * @deprecated 方法名有歧义，已重命名为：{@link CommonKit#uuid32()}
     */
    @Deprecated
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取指定长度的随机数字字符串，长度不够补零
     * @param length int 长度
     * @return String
     */
    public static String random(final int length) {
        return RandomStringUtils.randomNumeric(length);
    }

    /**
     * 获取随机数，指定随机数最大值
     * @param max int
     * @return int
     */
    public static int randomMax(final int max) {
        return RANDOM.nextInt(max);
    }

    /**
     * 转换为boolean类型，返回值不能为null
     * @param obj 转换对象
     * @return boolean
     */
    public static boolean booleanValue(final Object obj) {
        final Boolean value = toBoolean(obj);
        return Objects.nonNull(value) && value;
    }

    /**
     * 转换为Boolean类型，返回值可以为null
     * @param obj 转换对象
     * @return Boolean
     */
    public static Boolean toBoolean(final Object obj) {
        try {
            return Objects.isNull(obj) ? null : Boolean.valueOf(obj.toString());
        } catch (final Exception e) {
            return null;
        }
    }

    //    /**
    //     * 验证数字是否大于0
    //     *
    //     * @param value Number对象
    //     * @return true大于0，false小于等于0
    //     */
    //    public static boolean checkNumber(final Number value) {
    //        return Objects.nonNull(value) && value.doubleValue() > 0;
    //    }

    /**
     * 将Object 转换为String，""转换为null
     * @param obj 转换对象
     * @return String
     * @deprecated 请使用 Objects.toString(value)
     */
    @Deprecated
    public static String tostring(final Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        return "".equals(obj.toString().trim()) ? null : obj.toString().trim();
    }

    /**
     * 将Object 转换为String，null转换为""，且去掉左右空格
     * @param obj 转换对象
     * @return String
     * @deprecated 请使用 Objects.toString(value, "")
     */
    @Deprecated
    public static String toStringEmpty(final Object obj) {
        return (Objects.isNull(obj)) ? "" : obj.toString().trim();
    }

    /**
     * 判断Boolean值是否为false，为null时表示false
     * @param value Boolean
     * @return boolean true：为空或者值为false，false为空且值为true
     */
    public static boolean isFalse(final Boolean value) {
        return !isTrue(value);
    }

    /**
     * 判断Boolean值是否为true，为null时表示false
     * @param value Boolean
     * @return boolean true：非空且值为true，false为空或值为false
     */
    public static boolean isTrue(final Boolean value) {
        return Objects.nonNull(value) && value;
    }

    /**
     * MD5加密
     * @param source 加密字符串
     * @return String 密文字符串
     * @deprecated 请使用 org.apache.commons.codec.digest.DigestUtils.md5Hex
     */
    @Deprecated
    public static String md5(final String source) {
        try {
            final MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(source.getBytes(UTF_8));
            final byte[] bytes = md5.digest();
            final StringBuilder sb = new StringBuilder();
            for (final byte b : bytes) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100), 1, 3);
            }
            //			for (int i = 0; i < bytes.length; ++i) {
            //				sb.append(Integer.toHexString((bytes[i] & 0xFF) | 0x100).substring(1, 3));
            //			}
            return sb.toString();
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 判断列表是否非空
     * @param collections 集合
     * @return boolean true非空 false空
     * @deprecated 请使用 org.apache.commons.collections4.CollectionUtils.isNotEmpty()
     */
    @Deprecated
    public static boolean isNotEmpty(final Collection<?> collections) {
        return !isEmpty(collections);
    }

    /**
     * 判断列表是否为空
     * @param collections 集合
     * @return boolean true空 false非空
     * @deprecated 请使用 org.apache.commons.collections4.CollectionUtils.isEmpty()
     */
    @Deprecated
    public static boolean isEmpty(final Collection<?> collections) {
        return Objects.isNull(collections) || collections.isEmpty();
    }

    /**
     * 判断Map列表是否非空
     * @param map 集合
     * @return boolean true非空 false空
     * @deprecated 请使用 org.apache.commons.collections4.MapUtils.isNotEmpty()
     */
    @Deprecated
    public static boolean isNotEmpty(final Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 判断Map列表是否为空
     * @param map 集合
     * @return boolean true空 false非空
     * @deprecated 请使用 org.apache.commons.collections4.MapUtils.isEmpty()
     */
    @Deprecated
    public static boolean isEmpty(final Map<?, ?> map) {
        return Objects.isNull(map) || map.isEmpty();
    }

    /**
     * 判断数组是否非空
     * @param array T[] 数组
     * @return boolean true非空 false空
     */
    public static <T> boolean isNotEmpty(final T[] array) {
        return !isEmpty(array);
    }

    /**
     * 判断数组是否为空
     * @param array T[] 数组
     * @return boolean true空 false非空
     */
    public static <T> boolean isEmpty(final T[] array) {
        return Objects.isNull(array) || array.length == 0;
    }

    /**
     * 判断对象是否为空
     * @param obj Object 对象
     * @return boolean true空 false非空
     * @deprecated 请使用 Objects.isNull()
     */
    @Deprecated
    public static boolean isEmpty(final Object obj) {
        return Objects.isNull(obj);
    }

    /**
     * 判断字符串是否为非空
     * @param obj String 对象
     * @return boolean true非空 false空
     * @deprecated 请使用 org.apache.commons.lang3.StringUtils.isNotEmpty()
     */
    @Deprecated
    public static boolean isNotEmpty(final String obj) {
        return !CommonKit.isEmpty(obj);
    }

    /**
     * 判断字符串是否为空
     * @param obj String 对象
     * @return boolean true空 false非空
     * @deprecated 请使用 org.apache.commons.lang3.StringUtils.isEmpty()
     */
    @Deprecated
    public static boolean isEmpty(final String obj) {
        return Objects.isNull(obj) || "".equals(obj.trim());
    }

    /**
     * 将Object数组转换为String数组
     * @param array Object[]
     * @return String[]
     */
    public static String[] toStringArray(final Object[] array) {
        return isEmpty(array)
                ? new String[]{}
                : Arrays.stream(array).filter(Objects::nonNull).map(Object::toString).toArray(String[]::new);
    }

    /**
     * 判断args是否不包含value
     * @param value Object
     * @param args  Object...
     * @return boolean true：args不包含value， false：args中包含value
     */
    public static boolean notIn(final Object value, final Object... args) {
        return !in(value, args);
    }

    /**
     * 判断args是否包含value
     * @param value Object
     * @param args  Object...
     * @return boolean true：args中包含value， false：args不包含value
     */
    public static boolean in(final Object value, final Object... args) {
        return Objects.nonNull(value) && Objects.nonNull(args) && Sets.newHashSet(args).contains(value);
    }

    /**
     * 判断args是否包含value
     * @param value      {@link Object}
     * @param collection {@link Collection}
     * @return boolean true：args不包含value， false：args中包含value
     */
    public static boolean notIn(final Object value, final Collection<?> collection) {
        return !in(value, collection);
    }

    /**
     * 判断args是否包含value
     * @param value      Object
     * @param collection Collection
     * @return boolean true：args中包含value， false：args不包含value
     */
    public static boolean in(final Object value, final Collection<?> collection) {
        return Objects.nonNull(value) && Objects.nonNull(collection) && collection.contains(value);
    }

    /**
     * 对数组进行排序，不改变原数组序列
     * @param args String[]
     * @return String[]
     */
    public static String[] sort(final String... args) {
        return sort(true, args);
    }

    /**
     * 对数组进行排序，不改变原数组序列
     * @param asc  boolean true:正序排列，false：倒序排列
     * @param args String[]
     * @return String[]
     */
    public static String[] sort(final boolean asc, final String... args) {
        if (isEmpty(args)) {
            return null;
        }
        final String[] arrays = args.clone();
        if (asc) {
            Arrays.sort(arrays);
        } else {
            Arrays.sort(arrays, Collections.reverseOrder());
        }
        return arrays;
    }

    /**
     * 合并多个数组
     * @param args String[]
     * @return String[]
     */
    public static String[] concat(final String[]... args) {
        return Arrays.stream(args).flatMap(Arrays::stream).toArray(String[]::new);
    }

    /**
     * 合并多个数组
     * @param args Object[]
     * @return Object[]
     */
    public static Object[] concat(final Object[]... args) {
        return Arrays.stream(args).flatMap(Arrays::stream).toArray();
    }

    /**
     * 合并map
     * @param dest    {@link Map}{@link Map<String, Object>}
     * @param sources {@link Map}{@link Map<String, Object>}
     */
    public static void assign(final Map<String, Object> dest, final Map<String, Object>... sources) {
        for (final Map<String, Object> map : sources) {
            dest.putAll(map);
        }
    }

    /**
     * 合并对象（sources）到目标对象（dest）
     * @param dest    JSONObject 目标对象
     * @param sources JSONObject[]
     */
    public static void assign(final JSONObject dest, final JSONObject... sources) {
        for (final JSONObject obj : sources) {
            dest.putAll(obj);
        }
    }

    /**
     * 将首字母变成小写
     * @param text String 处理字符串
     */
    public static String firstLower(final String text) {
        return text.replaceFirst("^[A-Z]", (String.valueOf(text.charAt(0))).toLowerCase());
    }

    /**
     * 将首字母变成大写
     * @param text String 处理字符串
     */
    public static String firstUpper(final String text) {
        return text.replaceFirst("^[a-z]", (String.valueOf(text.charAt(0))).toUpperCase());
    }

    /**
     * 转换枚举
     * @param elementType Class<E> 枚举类
     * @param name        String 枚举名
     * @return Optional<E>
     */
    public static <E extends Enum<E>> Optional<E> enumOf(final Class<E> elementType, final String name) {
        try {
            return Optional.of(Enum.valueOf(elementType, name));
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * 切割list
     * @param list List 需要切割的集合
     * @param size int 每个集合的大小
     * @return {@link List}{@link List<List<T>>}
     * @deprecated 请使用 com.google.common.collect.Lists.partition()
     */
    @Deprecated
    public static <T> List<List<T>> partition(final List<T> list, final int size) {
        final int max = list.size();
        return Stream.iterate(0, n -> n + 1)
                .limit(max / size + Math.min(1, max % size))
                .map(n -> list.subList(n * size, Math.min(max, (n + 1) * size)))
                //				.map(n -> list.stream().skip(n * size).limit(size).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    /**
     * <pre>
     * 将字符串中使用 {key:字段名} 占位符的字段，替换为 map 集合中的值；
     * 具体替换规则参考 {@link CommonKit#format(String, Object...)} 方法注释
     * 参考代码：
     *   Util.format(
     *     String.join(",\n", "\n单引号需要转义:''{string}''", "string:{string}", "short:{short}", "int:{int}", "float:{float}", "double:{double}", "long:{long}", "percent:{percent,number,percent}", "bigdecimal:{bigdecimal}", "bigdecimal:{bigdecimal,number,currency}", "date:{date}", "date:{date,date,short}", "date:{date,date,medium}", "date:{date,date,long}", "date:{date,date,full}", "time:{date,time,short}", "time:{date,time,medium}", "time:{date,time,long}", "time:{date,time,full}", "boolean:{boolean}", "null:{null}", "empty:{empty}"),
     *     Maps.ofSO(2).put("string", "Conor").put("short", 10).put("int", 20).put("float", 25.1F).put("double", 30.13D).put("long", 40L).put("percent", 0.2).put("bigdecimal", BigDecimal.valueOf(Long.MAX_VALUE)).put("date", new Date()).put("boolean", true).put("null", null).put("empty", "").build()
     *   )
     * @param text {@link String} 将要替换的字符串，当字符串中有单引号的，需要使用双单引号转义
     * @param stringObjectMap {@link Map}{@link Map<String:字段名, Object:字段值>}
     * @return {@link String} 替换后的字符串
     */
    public static String format(String text, final Map<String, Object> stringObjectMap) {
        if (Objects.nonNull(stringObjectMap) && !stringObjectMap.isEmpty()) {
            final Matcher m = PLACEHOLDER.matcher(text);
            final List<String> keys = new ArrayList<>(stringObjectMap.size());
            while (m.find()) {
                text = text.replaceAll("(?<=\\{)".concat(m.group()), String.valueOf(keys.size()));
                keys.add(m.group());
                //                final Object v = map.get(m.group().replaceAll("^\\{(.*)}$", "$1"));
                //                if (Objects.isNull(v)) {
                //                    return value.replace(m.group(), "null");
                //                } else if (v instanceof BigDecimal) {
                //                    value = value.replaceFirst("\\{(\\w+)?}", ((BigDecimal) v).toPlainString());
                //                } else if (v instanceof Double) {
                //                    value = value.replaceFirst(m.group(), BigDecimal.valueOf((Double) v).toPlainString());
                //                } else if (v instanceof Float) {
                //                    value = value.replaceFirst(m.group(), BigDecimal.valueOf((Float) v).toPlainString());
                //                } else {
                //                    value = value.replace(m.group(), Objects.toString(v));
                //                }
            }
            return MessageFormat.format(text, keys.stream().map(stringObjectMap::get).toArray());
        }
        return text;
    }

    /**
     * 替换字符串， 同 {@link CommonKit#format(String, Map)} 类似，只是参数需要转换为字符串
     * @param text            {@link String} 将要替换的字符串，当字符串中有单引号的，需要使用双单引号转义
     * @param stringStringMap {@link Map}{@link Map<String:字段名, String:字段值>}
     * @return {@link String} 替换后的字符串
     */
    public static String formatString(String text, final Map<String, String> stringStringMap) {
        if (Objects.nonNull(stringStringMap) && !stringStringMap.isEmpty()) {
            final Matcher m = PLACEHOLDER.matcher(text);
            final List<String> keys = new ArrayList<>(stringStringMap.size());
            while (m.find()) {
                text = text.replaceAll("(?<=\\{)".concat(m.group()), String.valueOf(keys.size()));
                keys.add(m.group());
            }
            return MessageFormat.format(text, keys.stream().map(stringStringMap::get).toArray());
        }
        return text;
    }

    /**
     * 替换字符串， 同 {@link CommonKit#format(String, Object...)} 类似，只是参数需要转换为字符串
     * @param text {@link String} 将要替换的字符串，当字符串中有单引号的，需要使用双单引号转义
     * @param args {@link String}[]
     * @return {@link String} 替换后的字符串
     */
    public static String formatString(String text, final String... args) {
        if (Objects.nonNull(args) && args.length > 0) {
            final Matcher m = PLACEHOLDER.matcher(text);
            final AtomicInteger counter = new AtomicInteger(0);
            while (m.find()) {
                text = text.replace(String.format("{%s}", m.group()), args[counter.getAndIncrement()]);
            }
        }
        return text;
    }

    /**
     * <pre>
     * 通过 {@link Objects}{@link Objects#equals(Object, Object)} 方法比对 a,b ，对比对结果取反
     * Util.notEquals(1,1) => false
     * Util.notEquals(1,2) => true
     * @param a {@link Object}
     * @param b {@link Object}
     * @return {@link Boolean}
     */
    public static boolean notEquals(final Object a, final Object b) {
        return !Objects.equals(a, b);
    }

    /**
     * 62 进制 转 10进制
     * @param value {@link String} 62进制字符串
     * @return {@link Long} 10进制 数字
     */
    public static long decodeBase62(final String value) {
        // 从右边开始
        final char[] chars = new StringBuilder(value).reverse().toString().toCharArray();
        long n = 0;
        int pow = 0;
        for (final char item : chars) {
            n += BASE_62_CHAR.indexOf(item) * (long) Math.pow(BASE, pow);
            pow++;
        }
        return n;
    }

    /**
     * 获取UUID；中间的 - 剔除
     * @return String
     */
    public static String uuid32() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * <pre>
     * 将字符串中使用 {key:字段名} 占位符的字段，替换为 values 数组中的值；
     * 将会使用 {@link MessageFormat#format(String, Object...)} 替换规则，不同的地方在于 {@link CommonKit}.{@link CommonKit#format(String, Object...)}可以使用 {key:任意字符占位}，然后将 {key} 顺序替换为位置索引
     * 警告：{@link MessageFormat#format(String, Object...)} 传入数字时，默认使用【{0..n,number,#,###}】规则会加千位符，解决方案有两种
     *      1：将数字转换成字符串 => String.valueOf(value)
     *      2：按实际情况指定数字替换格式，改变默认规则 => {0..n,number,#}
     * {@link MessageFormat#format(String, Object...)} 与 {@link CommonKit}.{@link CommonKit#format(String, Object...)} 之间的差异在于
     * 前者使用 [0-9] 占位 values 的索引位置，且索引位置可以重复使用，如果参数多了不利于阅读代码
     * 后者使用 [0-9A-Za-z] 任意字符串占位，将占位符再替换为索引位置，也兼容原生的索引占位方式，但是索引占位符顺序不能颠倒（且不支持重复占位），否则会出现替换错误的问题；
     *         Util.format("错误示例：{1},{0},{2},{0}, 期望输出 B,A,C,B","A","B","C")     将会输出 "B,B,C,B"，该案例使用 MessageFormat.format() 方法是可以的，但这里不支持
     *         Util.format("正确示例：{0},{1},{2},{0}, 期望输出 B,A,C,B","B","A","C","B") 将会输出 "B,A,C,B"
     *         Util.format("正确示例：{b},{a},{c},{b}, 期望输出 B,A,C,B",Maps.ofSO().put("a","A").put("b","B").put("c","C").build()) 将会输出 "B,A,C,B"
     * 建议优先使用 {@link MessageFormat#format(String, Object...)} 直接操作，避免不必要的逻辑判断
     * 参考代码：
     *   Util.format(
     *     String.join(",\n", "\n单引号需要转义:''{string}''", "string:{string}", "short:{short}", "int:{int}", "float:{float}", "double:{double}", "long:{long}", "percent:{percent,number,percent}", "bigdecimal:{bigdecimal}", "bigdecimal:{bigdecimal,number,currency}", "date:{date}", "date:{date,date,short}", "date:{date,date,medium}", "date:{date,date,long}", "date:{date,date,full}", "time:{date,time,short}", "time:{date,time,medium}", "time:{date,time,long}", "time:{date,time,full}", "boolean:{boolean}", "null:{null}", "empty:{empty}"),
     *     Maps.ofSO(2).put("string", "Conor").put("short", 10).put("int", 20).put("float", 25.1F).put("double", 30.13D).put("long", 40L).put("percent", 0.2).put("bigdecimal", BigDecimal.valueOf(Long.MAX_VALUE)).put("date", new Date()).put("boolean", true).put("null", null).put("empty", "").build()
     *   )
     * @param text {@link String} 将要替换的字符串，当字符串中有单引号的，需要使用双单引号转义
     * @param args {@link Object}[]
     * @return {@link String} 替换后的字符串
     */
    public static String format(String text, final Object... args) {
        if (Objects.nonNull(args) && args.length > 0) {
            final Matcher m = PLACEHOLDER.matcher(text);
            final AtomicInteger counter = new AtomicInteger(0);
            while (m.find()) {
                text = text.replaceAll("(?<=\\{)".concat(m.group()), String.valueOf(counter.getAndIncrement()));
            }
            return MessageFormat.format(text, args);
        }
        return text;
    }

    /**
     * unicode 字符串编码
     * @param value {@link String} 需要编码的字符串
     * @return {@link String} 编码后的 unicode 字符串
     */
    public static String encodeUnicode(final String value) {
        Objects.requireNonNull(value, "参数【value】不能为null");
        final StringBuilder unicode = new StringBuilder();
        for (final char c : value.toCharArray()) {
            unicode.append("\\u").append(Integer.toHexString(c));
        }
        return unicode.toString();
        //        return StringEscapeUtils.escapeJava(value);
    }

    /**
     * unicode 字符串解码
     * @param value {@link String} unicode 字符串
     * @return {@link String} 解码后的字符串
     */
    public static String decodeUnicode(final String value) {
        Objects.requireNonNull(value, "参数【value】不能为null");
        return StringEscapeUtils.unescapeJava(value.replaceAll("\\\\u(\\w{2})(?!\\w)", "\\\\u00$1"));
    }

    /**
     * 严格模式：unicode 字符串编码，不足4位的前面补 0
     * @param value {@link String} 需要编码的字符串
     * @return {@link String} 编码后的 unicode 字符串
     */
    public static String encodeUnicodeStrict(final String value) {
        Objects.requireNonNull(value, "参数【value】不能为null");
        final StringBuilder unicode = new StringBuilder();
        for (final char c : value.toCharArray()) {
            unicode.append("\\u").append("00".concat(Integer.toHexString(c)).replaceAll("0+(\\w{4})$", "$1"));
        }
        return unicode.toString();
    }

    /**
     * 合并list
     * @param lists List<T>[] 需要切割的集合
     * @return {@link List}{@link List<T>}
     * @deprecated 请使用 org.apache.commons.collections4.CollectionUtils.union()
     */
    @Deprecated
    @SafeVarargs
    public static <T> List<T> merge(final List<T>... lists) {
        if (lists.length == 1) {
            return lists[0];
        }
        if (lists.length > 1) {
            final List<T> result = new ArrayList<>();
            for (final List<T> list : lists) {
                result.addAll(list);
            }
            return result;
        }
        return Collections.emptyList();
    }

    /**
     * 从十进制 转 62 进制
     * @param value {@link Long} 十进制数字
     * @return {@link String} 62进制字符串
     */
    public static String encodeBase62(long value) {
        if (value == 0) {
            return "a";
        }
        final StringBuilder sb = new StringBuilder();
        int rem;
        while (value > 0) {
            rem = (int) (value % BASE);
            sb.append(BASE_62_CHAR.charAt(rem));
            value = value / BASE;
        }
        return sb.reverse().toString();
    }
}
