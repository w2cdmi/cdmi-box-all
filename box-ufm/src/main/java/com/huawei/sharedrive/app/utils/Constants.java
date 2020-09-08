package com.huawei.sharedrive.app.utils;


public class Constants
{
    
    /** HTTP ACCEPT */
    public static final String HTTP_ACCEPT = "Accept";
    
    /** HTTP X-REAL-IP */
    public static final String HTTP_X_REAL_IP = "X-Real-IP";
    
    /** 是否支持响应体字段名称兼容, 默认不支持 */
    public static final boolean IS_FILED_NAME_COMPATIBLE = Boolean
        .parseBoolean(PropertiesUtils.getProperty("field.name.compatible.supported", "false"));
        
    /** 是否支持文件摘要字段名称兼容。设置为true时, 如果是移动客户端的请求, 同时返回MD5及sha1两个字段。默认为false */
    public static final boolean IS_DIGEST_NAME_COMPATIBLE = Boolean
        .parseBoolean(PropertiesUtils.getProperty("digest.name.compatible.supported", "false"));
        
    /** JSON TYPE */
    public static final String JSON_TYPE = "application/json";
    
    /** 日志记录是参数为用户名长度过长则限制其显示长度 */
    public static final Integer MAX_NAME_LOG = 30;
    
    /** 文件内容MD5值取样长度, 单位Byte(较小文件) */
    public static final Integer SAMPLING_LENGTH_FOR_SMALLER_FILE = Integer
        .parseInt(PropertiesUtils.getProperty("sampling.length.for.smaller.file", "256"));
        
    /** 统计周期, 用于配额校验 */
    public static final Integer STATISTICS_PERIODS_FOR_CHECK_QUOTA = Integer
        .parseInt(PropertiesUtils.getProperty("statistics.periods.seconds.check.quota", "600"));
        
    /** 统计周期, 用于获取用户信息 */
    public static final Integer STATISTICS_PERIODS_FOR_GET_INFO = Integer
        .parseInt(PropertiesUtils.getProperty("statistics.periods.seconds.info", "20"));
        
    public final static String THUMBNAIL_PREFIX_BIG = "/thumbnail?minHeight=96&minWidth=96";
    
    public final static String THUMBNAIL_PREFIX_HUGE = "/thumbnail?minHeight=200&minWidth=200";
    
    public final static String THUMBNAIL_PREFIX_SMALL = "/thumbnail?minHeight=32&minWidth=32";
    
    /** UAM本身的配置APP ID */
    public static final String UFM_DEFAULT_APP_ID = "-1";
    
    /**
     * thrift超时配置
     */
    public static final int THRIFT_DSS_SOCKET_TIMEOUT = Integer
        .parseInt(PropertiesUtils.getProperty("thrift.dss.socket.timeout", "60000"));
     
    public static final String APPID_PPREVIEW = "PreviewPlugin";
    
    public static final String APPID_SECURITYSCAN = "SecurityScan";
    
}
