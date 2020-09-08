package com.huawei.sharedrive.uam.log.domain;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.uam.adminlog.dao.impl.LogLanguageHelper;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.user.domain.User;
import com.huawei.sharedrive.uam.util.RequestUtils;
import com.huawei.sharedrive.uam.util.StringEncodeUtils;

import pw.cdmi.common.domain.Terminal;
import pw.cdmi.common.log.UserLog;
import pw.cdmi.core.utils.BundleUtil;

public enum UserLogType
{
     KEY_GET_TOKEN("token.get",1,true),

     KEY_GET_TOKEN_ERR("token.get.error",1001,true),

     KEY_REFRESH_TOKEN("token.refresh",2,true),

     KEY_REFRESH_TOKEN_ERR("token.refresh.error",1002,true),

     KEY_DELETE_TOKEN("token.delete",3,true),
     KEY_DELETE_TOKEN_ERR("token.delete.error",1003,true),

     KEY_CREATE_USER("user.create",4,true),
     KEY_CREATE_USER_ERR("user.create.error",1004,true),
     KEY_DELETE_USER("user.delete",5,true),
     KEY_DELETE_USER_ERR("user.delete.error",1005,true),

     KEY_CREATE_LDAP_USER("user.ldap.create",6,false),
     KEY_CREATE_LDAP_USER_ERR("user.ldap.create.error",1006,false),
     KEY_LIST_USER("user.list",7,false),
     KEY_LIST_USER_ERR("user.list.error",1007,false),
     KEY_GET_ME_TOKEN("token.get.me",8,false),
     KEY_GET_ME_TOKEN_ERR("token.get.me.error",1008,false),
     KEY_GET_ME_APP("app.get.me",9,false),
     KEY_GET_ME_APP_ERR("app.get.me.error",1009,false),

     KEY_SEARCH_TOKEN("token.search",10,true),

     KEY_SEARCH_TOKEN_ERROR("token.search.error",1010,true),

     KEY_UPDATE_APP_USER("user.update.app",11,true),
     KEY_UPDATE_APP_USER_ERR("user.update.app.error",1011,true),
     KEY_UPDATE_USER("user.update",12,true),
     KEY_UPDATE_USER_ERR("user.update.error",1012,true),

     KEY_GET_ME_INFO("me.info.get",13,false),
     KEY_GET_ME_INFO_ERR("me.info.get.error",1013,false),

     KEY_UPDATE_TOKEN_USER("user.update.token",14,true),
     KEY_UPDATE_TOKEN_USER_ERR("user.update.token.error",1014,true),

     KEY_LOCK_USER("user.lock",15,true),

     KEY_UNLOCK_USER("user.unLocked",16,true),

     KEY_GET_ACCOUNT_ATTR("accountattributes.get",17,false),
     KEY_GET_ACCOUNT_ATTR_ERR("accountattributes.get.error",1017,true),

     KEY_GET_ACCOUNT_LOGO("account.clientlogo.get",18,false),
     KEY_GET_ACCOUNT_LOGO_ERR("account.clientlogo.get.error",1018,true),

     KEY_GET_USER_IMAGE("user.get.image",19,false),
     KEY_GET_USER_IMAGE_ERR("user.get.image.error",1019,true),

     KEY_UPDATE_USER_IMAGE("user.update.image",20,true),
     KEY_UPDATE_USER_IMAGE_ERR("user.update.image.error",1020,true),

     KEY_PASSWD_LOCK("modifypswd.lock",21,true),
     KEY_PASSWD_UNLOCK("modifypswd.unLocked",22,true),

     // 重置密码
     KEY_RESET_PWD("user.pwd.reset",24,true),
     KEY_RESET_PWD_ERR("user.pwd.reset.error",1024,true),

     // 列举角色
     KEY_LIST_ROLES("user.roles.list",25,false),
     KEY_LIST_ROLES_ERR("user.roles.list.error",1025,true),
     // 获取消息
     KEY_MESSAGE_LIST("user.message.list",26,false),
     KEY_MESSAGE_LIST_ERR("user.message.list.error",1026,true),
     // 获取消息
     KEY_MESSAGE_LIST_ALL("user.message.list.all",27,false),
     KEY_MESSAGE_LIST_ALL_ERR("user.message.list.all.error",1027,true),

     // 获取隐私声明
     KEY_DECLARATION_GET("user.declaration.get",28,false),
     KEY_DECLARATION_GET_ERR("user.declaration.get.error",1028,true),
     // 发送邮件
     KEY_SEND_MAIL("user.mail.send",29,true),
     KEY_SEND_MAIL_ERR("user.mail.send.error",1029,true),
     // 更新信息状态
     KEY_UPDATE_MESSAGE("user.update.message",30,true),
     KEY_UPDATE_MESSAGE_ERR("user.update.message.error",1030,true),
     // 获取安全角色
     KEY_GET_SECURITY_ROLES("user.get.security.roles",31,false),
     KEY_GET_SECURITY_ROLES_ERR("user.get.security.roles.error",1031,true),
     // 获取文件级别
     KEY_GET_FILE_LEVEL("user.get.file.level",32,false),
     KEY_GET_FILE_LEVEL_ERR("user.get.file.level.error",1032,true),
     // 满足安全矩阵权限控制
     KEY_SECUTIRY_MATRIX("security.matrix.judge",33,false),
     KEY_SECUTIRY_MATRIX_ERR("security.matrix.judge.error",1033,true),
     // 获取系统配置
     KEY_SYSTEM_CONFIG("system.config.get",34,false),
     KEY_SYSTEM_CONFIG_ERR("system.config.get.error",1034,true),
     // 获取终端列表
     KEY_TERMINAL_LIST("terminal.list.get",35,false),
     KEY_TERMINAL_LIST_ERR("terminal.list.get.error",1035,true),
     // 更新终端信息
     KEY_TERMINAL_UPDATE_STATUS("terminal.update.status",36,true),
     KEY_TERMINAL_UPDATE_STATUS_ERR("terminal.update.status.error",1036,true),
     // 获取用户信息
     KEY_GET_UER_INFOMATION("user.infomation.get",37,false),
     KEY_GET_UER_INFOMATION_ERR("user.infomation.get.error",1037,false),
     // 获取ntlm挑战
     KEY_GET_NTLM_CHALLENGE("ntlm.challenge.get",38,false),
     KEY_GET_NTLM_CHALLENGE_ERR("ntlm.challenge.get.error",1038,true),
     KEY_USER_SIGN("user.sign",23,true),
     KEY_USER_SIGN_ERROR("user.sign.error",1023,true);

    private String modelName;
    
    private int value;
    
    private boolean enable;
    
    public boolean isEnable()
    {
        return enable;
    }
    
    UserLogType(String modelName, int value, boolean enable)
    {
        this.modelName = modelName;
        this.value = value;
        this.enable = enable;
    }
    
    private static final String USR_LOG_FILE = "userLog";
    
    public String getDetails(String[] params)
    {
        return BundleUtil.getText(USR_LOG_FILE, LogLanguageHelper.getLanguage(), this.modelName, params);
    }
    
    public String getDetails()
    {
        return BundleUtil.getText(USR_LOG_FILE, LogLanguageHelper.getLanguage(), this.modelName);
    }
    
    public byte getLevel()
    {
        if (this.value > 1000)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }
    
    static
    {
        BundleUtil.addBundle(USR_LOG_FILE, new Locale[]{Locale.ENGLISH, Locale.CHINESE});
        BundleUtil.setDefaultBundle(USR_LOG_FILE);
        BundleUtil.setDefaultLocale(Locale.CHINESE);
    }
    
    public int getTypeCode()
    {
        return this.value;
    }
    
    public static UserLog getUserLog(UserToken userToken)
    {
        UserLog userLog = new UserLog();
        if (userToken != null)
        {
            userLog.setAppId(userToken.getAppId());
            userLog.setClientAddress(userToken.getDeviceAddress());
            userLog.setClientDeviceName(userToken.getDeviceName());
            userLog.setClientDeviceSN(userToken.getDeviceSN());
            userLog.setClientType(userToken.getType());
            userLog.setKeyword(userToken.getLoginName());
            userLog.setLoginName(userToken.getLoginName());
            userLog.setUserId(userToken.getId());
            return userLog;
        }
        return null;
    }
    
    /**
     * 
     * @param request
     * @param appId
     * @param loginName
     * @param userId
     * @return
     */
    public static UserLog getUserLog(HttpServletRequest request, String appId, String loginName,
        boolean isNtlm)
    {
        UserLog userLog = new UserLog();
        String deviceTypeStr = request.getHeader("x-device-type");
        String deviceSN = request.getHeader("x-device-sn");
        String deviceOS = request.getHeader("x-device-os");
        String deviceName = request.getHeader("x-device-name");
        String deviceAgent = request.getHeader("x-client-version");
        String requestIp = request.getHeader("x-request-ip");
        if (StringUtils.isBlank(requestIp))
        {
            requestIp = RequestUtils.getRealIP(request);
        }
        if (!isNtlm)
        {
            deviceTypeStr = StringEncodeUtils.decodeUft8ValueNoException(deviceTypeStr);
            deviceSN = StringEncodeUtils.decodeUft8ValueNoException(deviceSN);
            deviceOS = StringEncodeUtils.decodeUft8ValueNoException(deviceOS);
            deviceName = StringEncodeUtils.decodeUft8ValueNoException(deviceName);
            deviceAgent = StringEncodeUtils.decodeUft8ValueNoException(deviceAgent);
            requestIp = StringEncodeUtils.decodeUft8ValueNoException(requestIp);
        }
        userLog.setAppId(appId);
        userLog.setClientDeviceName(deviceName);
        userLog.setClientDeviceSN(deviceSN);
        userLog.setClientOS(deviceOS);
        byte clientType = (byte) getDeviceType(deviceTypeStr);
        userLog.setClientType(clientType);
        userLog.setClientVersion(deviceAgent);
        if (StringUtils.isNotBlank(requestIp))
        {
            userLog.setClientAddress(requestIp);
        }
        userLog.setLoginName(loginName);
        return userLog;
    }
    
    public static UserLogType build(int typeCode)
    {
        UserLogType[] allType = UserLogType.values();
        for (UserLogType tmpType : allType)
        {
            if (tmpType.getTypeCode() == typeCode)
            {
                return tmpType;
            }
        }
        return null;
    }
    
    public static UserLog getUserLog(User user)
    {
        UserLog userLog = new UserLog();
        if (user != null)
        {
            userLog.setAppId(user.getAppId());
            userLog.setUserId(user.getId());
            userLog.setLoginName(user.getLoginName());
            userLog.setKeyword(user.getLoginName());
            return userLog;
        }
        return null;
    }
    
    /**
     * 
     * @param deviceTypeStr
     * @return
     */
    private static int getDeviceType(String deviceTypeStr)
    {
        if (Terminal.CLIENT_TYPE_ANDROID_STR.equals(deviceTypeStr))
        {
            return Terminal.CLIENT_TYPE_ANDROID;
        }
        if (Terminal.CLIENT_TYPE_IOS_STR.equals(deviceTypeStr))
        {
            return Terminal.CLIENT_TYPE_IOS;
        }
        if (Terminal.CLIENT_TYPE_PC_STR.equals(deviceTypeStr))
        {
            return Terminal.CLIENT_TYPE_PC;
        }
        if (Terminal.CLIENT_TYPE_WEB_STR.equals(deviceTypeStr))
        {
            return Terminal.CLIENT_TYPE_WEB;
        }
        return Terminal.CLIENT_TYPE_WEB;
    }
}
