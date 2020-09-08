package com.huawei.sharedrive.uam.cmb.control.manager;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.common.util.DBAccessKeyParser;
import com.huawei.sharedrive.uam.util.PropertiesUtils;
import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;

public class Constants
{
    // 是否启用招行定制
    public static final boolean isCMB = StringUtils.isBlank(PropertiesUtils.getProperty("is.cmb")) ? false
        : Boolean.parseBoolean(PropertiesUtils.getProperty("is.cmb"));
    
    // 同步web service地址
    public static final String URL = PropertiesUtils.getProperty("cmb.webservice.url");
    
    // 一事通权限用户
    public static final String APPCODE = PropertiesUtils.getProperty("cmb.webservice.appcode");
    
    // 一事通权限密码
    public static final String PTOKE = StringUtils.isBlank(PropertiesUtils.getProperty("cmb.webservice.ptoken")) ? ""
        : DBAccessKeyParser.decode(PropertiesUtils.getProperty("cmb.webservice.ptoken"));
    
    // 一事通邮箱后缀名
    public static final String EMAIL_SUFFIX = PropertiesUtils.getProperty("cmb.email.suffix");
    
    // 一事通同步开户状态
    public static final Byte USER_STATUS = UserAccount.INT_STATUS_ENABLE;
    
    // SSO鉴权，加密用户信息
    public static final String USERKEY = PropertiesUtils.getProperty("cmb.oams.userkey");
    
    // SSO健全,加密健全
    public static final String TOKENKEY = PropertiesUtils.getProperty("cmb.oams.tokenkey");
    
    public static final String SYSKEY = PropertiesUtils.getProperty("cmb.oams.syskey");
    
    // 下一步URL,
    public static final String NETACTIONKEY = PropertiesUtils.getProperty("cmb.oams.netactionkey");
    
    // SSO，cloudapp的 地址
    public static final String CLOUDAPP_LOGIN_URL = PropertiesUtils.getProperty("cmb.cloudapp.login.url");
    
    public static final String CLOUDAPP_URL = PropertiesUtils.getProperty("cmb.cloudapp.url");
    
    // SSO鉴权时候后，或session失效后，跳转地址
    public static final String CMB_URL = PropertiesUtils.getProperty("cmb.oams.url");
    
    // 鉴权返回数据关键字
    public static final String CERT_RESULT_SAPID = PropertiesUtils.getProperty("cmb.oams.result.sapid");
    
    // 鉴权返回数据时间关键字
    public static final String CERT_RESULT_TIME = PropertiesUtils.getProperty("cmb.oams.result.time");
    
    // 鉴权返回数据分隔符
    public static final String CERT_RESULT_SPLIT = PropertiesUtils.getProperty("cmb.oams.result.split");
    
    // sso登录一事通超时时间
    public static final int CERT_EXPIRED_TIME = StringUtils.isBlank(PropertiesUtils.getProperty("cmb.oams.sso.expiredtime")) ? 18000
        : Integer.parseInt(PropertiesUtils.getProperty("cmb.oams.sso.expiredtime"));
    
    public static final boolean IS_BUSINESS_BOX = StringUtils.isBlank(PropertiesUtils.getProperty("cmb.is.business")) ? false
        : Boolean.parseBoolean(PropertiesUtils.getProperty("cmb.is.business"));
    
    public static final String CMB_CERT_PATH = PropertiesUtils.getProperty("cmb.sert.path");
    
    /** 上面为招行定制配置 */
    // SSO 临时token失效时间
    public static final int TMP_TOKEN_EXPIRED = StringUtils.isBlank(PropertiesUtils.getProperty("cmb.tmp.token.expired")) ? 300000
        : Integer.parseInt(PropertiesUtils.getProperty("cmb.tmp.token.expired"));
    
    // SSO 临时token key
    public static final String TMP_TOKEN_KEY = PropertiesUtils.getProperty("cmb.tmp.token.key");
    
    // 用户检索最大数量
    public static final int MAX_USER = StringUtils.isBlank(PropertiesUtils.getProperty("cmb.oams.maxuser")) ? 10000
        : Integer.parseInt(PropertiesUtils.getProperty("cmb.oams.maxuser"));
    
    // 允许同步开户的机构
    public static final String CMB_ORG_USERCREATE = PropertiesUtils.getProperty("cmb.org.usercreate");
}
