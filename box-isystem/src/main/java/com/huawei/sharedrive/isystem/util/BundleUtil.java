/*
 * Copyright Huawei Technologies Co.,Ltd. 2013-2014. All rights reserved.
 * 
 * 
 */
package com.huawei.sharedrive.isystem.util;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 资源工具类
 * 
 * @author l90003768
 * 
 */
public final class BundleUtil {

    /** 集合容器初始大小 */
    public static final int INITIAL_CAPACITIES = 10;

    /**
     * 中文
     */
    public static final String CHINESE = "zh";

    /**
     * 英文
     */
    public static final String ENGLISH = "en";

    /**
     * 资源集
     */
    private final static Map<String, ResourceBundle> BUNDLEMAPS = new ConcurrentHashMap<String, ResourceBundle>(
        INITIAL_CAPACITIES);

    /**
     * 默认资源文件名称
     */
    private static String defaultBundleName = null;

    /**
     * 默认区域属性
     */
    private static Locale defaultLocale = new Locale("zh_CN");

    private static Logger logger = LoggerFactory.getLogger(BundleUtil.class);

    /**
     * 格式化工具集
     */
    private static final Map<MessageFormatKey, MessageFormat> MESSAGEMAPS = new ConcurrentHashMap<MessageFormatKey, MessageFormat>(
        INITIAL_CAPACITIES);

    /**
     * 添加bundle集
     * 
     * @param bundleName
     * @param locales
     */
    public static void addBundle(String bundleName, Locale[] locales) {
        for (Locale locale : locales) {
            if (!BUNDLEMAPS.containsKey(bundleName + '-' + locale.getLanguage())) {
                ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale,
                    Thread.currentThread().getContextClassLoader());
                if (null == bundle) {
                    return;
                }
                BUNDLEMAPS.put(bundleName + '-' + locale.getLanguage(), bundle);
            }
        }
    }

    /**
     * 获取浏览器默认的首选语言
     * 
     * @param request
     * @return
     */
    public static Locale getDefaultLanguage(HttpServletRequest request) {
        Enumeration<?> language = request.getHeaders("accept-language");
        String lang = null;
        boolean hasMore = language.hasMoreElements();
        while (hasMore) {
            lang = language.nextElement().toString();
            if (lang.startsWith(ENGLISH)) {
                return Locale.ENGLISH;
            } else if (lang.startsWith(CHINESE)) {
                return Locale.SIMPLIFIED_CHINESE;
            }
            hasMore = language.hasMoreElements();
        }
        return Locale.ENGLISH;
    }

    /**
     * 从默认资源文件中，获取资源文件keyName对应的值
     * 
     * @param locale 区域
     * @param keyName key名称
     * @return
     */
    public static String getText(Locale locale, String keyName) {
        return getText(defaultBundleName, locale, keyName);
    }

    /**
     * 获取默认资源文件中keyName对应的格式化值
     * 
     * @param locale 区域
     * @param keyName key名称
     * @param params 参数
     * @return
     */
    public static String getText(Locale locale, String keyName, Object[] params) {
        return getText(defaultBundleName, locale, keyName, params);
    }

    /**
     * 获取默认资源文件默认区域下keyName对应的值
     * 
     * @param bundleName 资源文件名称
     * @param locale 区域
     * @param keyName key名称
     * @return
     */
    public static String getText(String keyName) {
        return getText(defaultBundleName, defaultLocale, keyName);
    }

    /**
     * 获取资源文件keyName对应的值
     * 
     * @param bundleName 资源文件名称
     * @param locale 区域
     * @param keyName key名称
     * @return
     */
    public static String getText(String bundleName, Locale locale, String keyName) {
        ResourceBundle bundle = getCurrentBundle(bundleName, locale);
        if (null == bundle) {
            logger.info("cann't find the bundle " + bundleName);
            return keyName;
        }
        return bundle.getString(keyName);
    }

    /**
     * 获取文件中keyName对应的格式化值
     * 
     * @param bundleName 资源文件名称
     * @param locale 区域
     * @param keyName key名称
     * @param params 参数
     * @return
     */
    public static String getText(String bundleName, Locale locale, String keyName, Object[] params) {
        ResourceBundle bundle = getCurrentBundle(bundleName, locale);
        if (null == bundle) {
            logger.info("cann't find the bundle " + bundleName);
            return keyName;
        }
        String value = bundle.getString(keyName);
        MessageFormat mf = buildMessageFormat(value, locale);
        return mf.format(params);
    }

    /**
     * 获取默认文件中默认区域keyName对应的格式化值
     * 
     * @param keyName key名称
     * @param params 参数
     * @return
     */
    public static String getText(String keyName, Object[] params) {
        return getText(defaultBundleName, defaultLocale, keyName, params);
    }

    /**
     * 获取资源文件中，默认区域下keyName对应的值
     * 
     * @param bundleName 资源文件名称
     * @param keyName key名称
     * @return
     */
    public static String getText(String bundleName, String keyName) {
        return getText(bundleName, defaultLocale, keyName);
    }

    /**
     * 获取文件中默认区域keyName对应的格式化值
     * 
     * @param bundleName 资源文件名称
     * @param locale 区域
     * @param keyName key名称
     * @param params 参数
     * @return
     */
    public static String getText(String bundleName, String keyName, Object[] params) {
        return getText(bundleName, defaultLocale, keyName, params);
    }

    /**
     * 
     * @param request
     * @param keyName 资源文件对应Key
     * @return
     */
    public static String resourceInfo(HttpServletRequest request, String keyName) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages",
            (Locale) request.getSession(false).getAttribute("cur_language"));
        String message = resourceBundle.getString(keyName);
        return message;
    }

    /**
     * 设置默认资源文件名称
     * 
     * @param bundleName
     */
    public static void setDefaultBundle(String bundleName) {
        defaultBundleName = bundleName;
    }

    /**
     * 设置默认区域项
     * 
     * @param localeName
     */
    public static void setDefaultLocale(Locale locale) {
        defaultLocale = locale;
    }

    /**
     * 构造国际化资源格式化对象并进行缓存，返回当前构造的格式化对象实例
     * 
     * @param pattern 格式化模式
     * @param locale 语言
     * @return 格式化对象实例
     */
    private static MessageFormat buildMessageFormat(String pattern, Locale locale) {
        MessageFormatKey key = new MessageFormatKey(pattern, locale);
        MessageFormat format = null;

        format = MESSAGEMAPS.get(key);
        if (format == null) {
            format = new MessageFormat(pattern);
            format.setLocale(locale);
            format.applyPattern(pattern);
            MESSAGEMAPS.put(key, format);
        }
        return format;
    }

    /**
     * 获取当前BUNDLE
     * 
     * @param bundleName
     * @param locale
     * @return
     */
    private static ResourceBundle getCurrentBundle(String bundleName, Locale locale) {
        if (BUNDLEMAPS.containsKey(bundleName + '-' + locale.getLanguage())) {
            return BUNDLEMAPS.get(bundleName + '-' + locale.getLanguage());
        }
        return null;
    }

    /**
     * 构造方法私有化，防止实例化
     */
    private BundleUtil() {
    }

}

/**
 * 
 * @author s00108907
 * 
 */
class MessageFormatKey {

    private Locale locale;

    private String pattern;

    MessageFormatKey(String pattern, Locale locale) {
        this.pattern = pattern;
        this.locale = locale;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MessageFormatKey) {
            if (this == obj) {
                return true;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            MessageFormatKey other = (MessageFormatKey) obj;
            if (pattern == null) {
                if (other.pattern != null) {
                    return false;
                }
            } else if (!pattern.equals(other.pattern)) {
                return false;
            }

            if (locale == null) {
                if (other.locale != null) {
                    return false;
                }
            } else if (!locale.equals(other.locale)) {
                return false;
            }
            return true;
        }
        return false;

    }

    public Locale getLocale() {
        return locale;
    }

    public String getPattern() {
        return pattern;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((locale == null) ? 0 : locale.hashCode());
        result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
        return result;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
