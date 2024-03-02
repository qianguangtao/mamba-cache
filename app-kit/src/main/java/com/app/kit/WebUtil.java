package com.app.kit;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Slf4j
public class WebUtil {

    public static String REQUEST_METHOD_GET = "GET";
    public static String REQUEST_UNKNOWN_IP = "unknown";

    public static boolean isGetRequest() {
        return REQUEST_METHOD_GET.equalsIgnoreCase(getRequestMethod());
    }

    public static String getRequestMethod() {
        return getRequest().getMethod();
    }

    public static HttpServletRequest getRequest() {
        if (ObjectUtil.isNull(RequestContextHolder.getRequestAttributes())) {
            return null;
        }
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static String getServerBasePath() {
        final HttpServletRequest request = getRequest();
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }

    /**
     * 获取用户IP地址
     */
    public static String getRemoteHost() {
        return getRemoteHost(getRequest());
    }

    /**
     * 获取用户IP地址
     */
    public static String getRemoteHost(final HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || REQUEST_UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || REQUEST_UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || REQUEST_UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    }

    /**
     * 获取指定Cookie的值
     * @param defaultValue 缺省值
     */
    public static final String getCookieValue(final HttpServletRequest request, final String cookieName, final String defaultValue) {
        final Cookie cookie = WebUtils.getCookie(request, cookieName);
        if (cookie == null) {
            return defaultValue;
        }
        return cookie.getValue();
    }

    public static void setSession(final HttpServletRequest request, final String key, final Object value) {
        final HttpSession session = request.getSession();
        if (null != session) {
            session.setAttribute(key, value);
        }
    }

    /**
     * 获得国际化信息
     */
    public static final String getApplicationResource(final String key, final HttpServletRequest request) {
        final ResourceBundle resourceBundle = ResourceBundle.getBundle("ApplicationResources", request.getLocale());
        return resourceBundle.getString(key);
    }

    /**
     * 获得参数Map
     */
    public static Map<String, Object> getParameterMap(final HttpServletRequest request) {
        return WebUtils.getParametersStartingWith(request, null);
    }


    /**
     * 获取客户端IP
     */
    public static String getHost(final HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isBlank(ip) || REQUEST_UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || REQUEST_UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || REQUEST_UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(ip) || REQUEST_UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(ip) || REQUEST_UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StringUtils.isBlank(ip) || REQUEST_UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.indexOf(",") > 0) {
            log.info(ip);
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            final String[] ips = ip.split(",");
            for (final String ip2 : ips) {
                final String strIp = ip2;
                if (!REQUEST_UNKNOWN_IP.equalsIgnoreCase(strIp)) {
                    ip = strIp;
                    break;
                }
            }
        }
        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            InetAddress inet = null;
            try { // 根据网卡取本机配置的IP
                inet = InetAddress.getLocalHost();
            } catch (final UnknownHostException e) {
                log.error("getCurrentIP", e);
            }
            if (inet != null) {
                ip = inet.getHostAddress();
            }
        }
        log.info("getRemoteAddr ip: " + ip);
        return ip;
    }

    /**
     * 判断是否是白名单
     */
    public static boolean isWhiteRequest(final String url, final int size, final List<String> whiteUrls) {
        if (url == null || "".equals(url) || size == 0) {
            return true;
        } else {
            final String refHost = url.toLowerCase();
            for (final String urlTemp : whiteUrls) {
                if (refHost.indexOf(urlTemp.toLowerCase()) > -1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 写出响应
     */
    public static boolean write(final HttpServletResponse response, final String code, final String description) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        final Map<String, Object> result = Maps.newHashMap();
        result.put("code", code);
        result.put("message", description);
        result.put("time", System.currentTimeMillis());
        log.info("RESPONSE === > {}", JSON.toJSON(result));
        response.getOutputStream().write(JSON.toJSONBytes(result, SerializerFeature.DisableCircularReferenceDetect));
        return false;
    }

    /**
     * 写出响应
     */
    public static void write(final HttpServletResponse response, final Map<String, Object> modelMap) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        log.info("RESPONSE === > {}", JSON.toJSON(modelMap));
        response.getOutputStream().write(JSON.toJSONBytes(modelMap, SerializerFeature.DisableCircularReferenceDetect));
    }

    public static void writeJson2Response(final HttpServletResponse response, final Object json) {
        PrintWriter out = null;

        try {
            final String result = JSONObject.toJSONString(json);

            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            out = response.getWriter();
            out.println(result);
        } catch (final IOException var7) {
            log.error("写入失败", var7);
        } finally {
            if (null != out) {
                out.flush();
                out.close();
            }

        }

    }

    /**
     * 为response设置header，实现跨域
     */
    public static void setAllowCorsHeader(final HttpServletRequest request, final HttpServletResponse response) {
        // 跨域的header设置
        response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods", request.getMethod());
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
        // 防止乱码，适用于传输JSON数据
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.setStatus(HttpStatus.OK.value());
    }

    public static String getHostIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
        }
        return "127.0.0.1";
    }

}
