package pw.cdmi.box.uam.log.domain;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;

import pw.cdmi.box.uam.adminlog.dao.impl.LogLanguageHelper;
import pw.cdmi.box.uam.user.domain.Admin;
import pw.cdmi.common.log.SystemLog;
import pw.cdmi.core.utils.BundleUtil;
import pw.cdmi.core.utils.EnvironmentUtils;
import pw.cdmi.core.utils.IpUtils;

public enum OperateType
{
    
    Uncertain(-1, "ALL_LOG"), Login(1, "ADMIN_LOGIN"), Logout(2, "ADMIN_LOGOUT"), ResetPassword(3,
        "ADMIN_RESET_PWD"), ChangeInitPassword(4, "ADMIN_CHANGE_DEFAULT_PWD"), ChangePassword(5,
        "ADMIN_CHANGE_PWD"), ChangeAdminEmailAddr(6, "ADMIN_CHANGE_EMAIL"), ChangeAdminInfo(7,
        "ADMIN_CHANGE_USERNAME"), CreateLocalAdmin(20, "ADMIN_ADD_ADMIN"), DeleteAdmin(21,
        "ADMIN_DELETE_ADMIN"), ChangeAdmin(22, "ADMIN_MODIFY_ADMIN"), ChangeSyslog(60, "ADMIN_SYSLOG"), ChangeCustom(
        61, "ADMIN_SYSTEM_PERSONALIZED"), ChangeBasicConfig(62, "ADMIN_BASIC"), UploadClient(63,
        "ADMIN_UPLOAD"), ChangeEmailServer(64, "ADMIN_SET_EMAIL"), ChangeAuthServer(65,
        "ADMIN_SET_AUTHENTICATION"), ChangeAccessAddress(67, "ADMIN_SET_ACCESS"), ChangeSecurityConfig(68,
        "ADMIN_MODIFY_SECURITY"), ChangeAuthFeldConfig(69, "ADMIN_FIELD_MAPPING"), CreateAppAuth(80,
        "ADMIN_ADD_APP"), UpdateAppAuth(81, "ADMIN_MODIFY_APP"), ChangeSpaceMaxMembers(90,
        "ADMIN_CHANGE_MAX_TEANM"), ChangeSpaceQuota(91, "ADMIN_CHANGE_TEAM_QUOTA"), ChangeSpaceMaxVersions(
        92, "ADMIN_CHANGE_TEAM_VERSIONS"), SetSysLoginTimes(96, "SYS_ADMIN_SET_SYSTEM_LOGINTIMES"), SetUserLoginTimes(
        97, "SYS_ADMIN_SET_USER_LOGINTIMES"), UpdateUser(102, "ADMIN_UPDATE_USER"), SyncUser(105,
        "ADMIN_SYNCHRONIZE_USER"), ImportUser(106, "ADMIN_IMPORT"), exportUser(107, "ADMIN_EXPORT"), ConfigNodeFilter(
        108, "ADMIN_CONFIG_NODE_FILTER"), FilterNode(110, "ADMIN_FILTER_NODE"), UserSearchRule(111,
        "ADMIN_USER_SEARCH_RULE"), CreateEnterprise(112, "ADMIN_ENTERPRISE_CREATE"), AdminEnable(113,
        "ADMIN_ENABLE"), AdminDisable(114, "ADMIN_DISABLE"), AdminLogSet(115, "ADMIN_LOG_SET"), BindApp(117,
        "BIND_APP"), ChangeStatisticsAccesskey(118, "ADMIN_SET_STATISTICS_ACCESSKEY"), SystemAnnouncement(
        119, "SYSTEM_ANNOUNCEMENT"), AsyncTask(120, "ASYNC_TASK"), EnterpriseInfo(121, "ENTERPRISE_INFO"), AuthserverType(
        122, "AUTHSERVER_TYPE"), EnterpriseEmployees(123, "ENTERPRISE_EMPLOYEES"), UserStatusUpdate(124,
        "USER_STATUS_UPDATE"), SecurityAccessControlRole(125, "SECURITY_ACCESS_CONTROL_ROLE"), WaterMark(126,
        "WATER_MARK"), SystemRole(127, "SYSTEM_ROLE"), SecurityLevel(300, "SECURITY_LEVEL"), ResourceStrategy(
        302, "RESOURCE_STRATEGY"), NetworkRegion(303, "NETWORK_REGION"), NetworkRegionIp(304,
        "NETWORK_REGION_CONFIG_IP"), AccessConfig(305, "ACCESSCONFIG"), AccessSpaceConfig(306,
        "ACCESSCONFIG_SPACE"), AccessFileCopyConfig(307, "ACCESSCONFIG_FILECOPY"), Statistics(308,
        "STATISTICS"), LockModifyPassword(320, "LOCK_MODIFY_PWD"), UnlockModifyPassword(321,
        "UNLOCK_MODIFY_PWD"), ModifyAccountCofiguration(2002, "ADMIN_MODIFY_ACCOUNT_CONFIGURATION"), AccountStatus(
        2004, "UPDATE_ENTERPRISE_ACCOUNT_STATUS"), TestSyslog(2007, "TEST_SYSLOG"), Lock(8, "USER_LOCK"), Unlock(
        9, "USER_UNLOCK"), TestEmailServer(2005, "ADMIN_TEST_EMAIL"), SignDeclaration(2006,
        "SIGN_DECLARATION"), UpdateLoginConfig(2008, "UPDATE_LOGIN_CONFIG");
    
    private int code;
    
    private String name;
    
    private OperateType(int code, String name)
    {
        this.code = code;
        this.name = name;
    }
    
    public int getCode()
    {
        return this.code;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public static OperateType parseType(int code)
    {
        for (OperateType s : OperateType.values())
        {
            if (s.getCode() == code)
            {
                return s;
            }
        }
        return null;
    }
    
    private static final String SYSTEM_LOG_FILE = "systemLog";
    
    public String getDetails(String[] params)
    {
        String details = BundleUtil.getText(SYSTEM_LOG_FILE,
            LogLanguageHelper.getLanguage(),
            this.name,
            params);
        return details;
    }
    
    public String getDetails(Locale locale, String[] params)
    {
        String details = BundleUtil.getText(SYSTEM_LOG_FILE, locale, this.name, params);
        return details;
    }
    
    static
    {
        BundleUtil.addBundle(SYSTEM_LOG_FILE, new Locale[]{Locale.ENGLISH, Locale.CHINESE});
    }
    
    public static SystemLog getSystemLog(HttpServletRequest request)
    {
        SystemLog systemLog = new SystemLog();
        try
        {
            Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
            systemLog.setClientAddress(IpUtils.getClientAddress(request));
            systemLog.setClientDeviceName(EnvironmentUtils.getHostName());
            systemLog.setLoginName(admin.getLoginName());
            systemLog.setShowName(admin.getLoginName() + "(" + admin.getName() + ")");
        }
        catch (RuntimeException e)
        {
            if (null == systemLog.getLoginName())
            {
                systemLog.setLoginName("-");
                systemLog.setShowName("-");
            }
            return systemLog;
        }
        return systemLog;
    }
    
    public static SystemLog getSystemLog(HttpServletRequest request, String loginName)
    {
        SystemLog systemLog = new SystemLog();
        systemLog.setClientAddress(IpUtils.getClientAddress(request));
        systemLog.setClientDeviceName(EnvironmentUtils.getHostName());
        systemLog.setLoginName(loginName);
        systemLog.setShowName(loginName);
        return systemLog;
    }
    
    public static int setLogLevel(int code)
    {
        int high = 2;
        int medium = 1;
        int low = 0;
        Map<Integer, Integer> level = new HashMap<Integer, Integer>(200);
        level.put(OperateType.Login.getCode(), low);
        level.put(OperateType.Logout.getCode(), low);
        level.put(OperateType.ResetPassword.getCode(), high);
        level.put(OperateType.ChangeInitPassword.getCode(), high);
        level.put(OperateType.ChangePassword.getCode(), high);
        level.put(OperateType.ChangeAdminEmailAddr.getCode(), medium);
        level.put(OperateType.ChangeAdminInfo.getCode(), high);
        level.put(OperateType.CreateLocalAdmin.getCode(), medium);
        level.put(OperateType.DeleteAdmin.getCode(), high);
        level.put(OperateType.ChangeAdmin.getCode(), medium);
        level.put(OperateType.ChangeSyslog.getCode(), medium);
        level.put(OperateType.ChangeCustom.getCode(), low);
        level.put(OperateType.ChangeBasicConfig.getCode(), low);
        level.put(OperateType.UploadClient.getCode(), medium);
        level.put(OperateType.ChangeEmailServer.getCode(), high);
        level.put(OperateType.ChangeAuthServer.getCode(), high);
        level.put(OperateType.ChangeAccessAddress.getCode(), high);
        level.put(OperateType.ChangeSecurityConfig.getCode(), high);
        level.put(OperateType.ChangeAuthFeldConfig.getCode(), low);
        level.put(OperateType.CreateAppAuth.getCode(), low);
        level.put(OperateType.UpdateAppAuth.getCode(), high);
        level.put(OperateType.ChangeSpaceMaxMembers.getCode(), low);
        level.put(OperateType.ChangeSpaceQuota.getCode(), low);
        level.put(OperateType.ChangeSpaceMaxVersions.getCode(), low);
        level.put(OperateType.SetSysLoginTimes.getCode(), low);
        level.put(OperateType.UpdateUser.getCode(), low);
        level.put(OperateType.SyncUser.getCode(), low);
        level.put(OperateType.ImportUser.getCode(), low);
        level.put(OperateType.exportUser.getCode(), high);
        level.put(OperateType.ConfigNodeFilter.getCode(), high);
        level.put(OperateType.FilterNode.getCode(), low);
        level.put(OperateType.UserSearchRule.getCode(), low);
        level.put(OperateType.CreateEnterprise.getCode(), low);
        level.put(OperateType.AdminEnable.getCode(), high);
        level.put(OperateType.AdminDisable.getCode(), high);
        level.put(OperateType.AdminLogSet.getCode(), medium);
        level.put(OperateType.BindApp.getCode(), medium);
        level.put(OperateType.ChangeStatisticsAccesskey.getCode(), low);
        level.put(OperateType.SystemAnnouncement.getCode(), low);
        level.put(OperateType.AsyncTask.getCode(), low);
        level.put(OperateType.EnterpriseInfo.getCode(), low);
        level.put(OperateType.AuthserverType.getCode(), high);
        level.put(OperateType.EnterpriseEmployees.getCode(), high);
        level.put(OperateType.UserStatusUpdate.getCode(), high);
        level.put(OperateType.SecurityAccessControlRole.getCode(), low);
        level.put(OperateType.WaterMark.getCode(), low);
        level.put(OperateType.SystemRole.getCode(), high);
        level.put(OperateType.SecurityLevel.getCode(), high);
        level.put(OperateType.ResourceStrategy.getCode(), high);
        level.put(OperateType.NetworkRegion.getCode(), high);
        level.put(OperateType.NetworkRegionIp.getCode(), low);
        level.put(OperateType.AccessConfig.getCode(), high);
        level.put(OperateType.AccessSpaceConfig.getCode(), high);
        level.put(OperateType.AccessFileCopyConfig.getCode(), low);
        level.put(OperateType.Statistics.getCode(), low);
        level.put(OperateType.LockModifyPassword.getCode(), low);
        level.put(OperateType.UnlockModifyPassword.getCode(), high);
        level.put(OperateType.ModifyAccountCofiguration.getCode(), medium);
        level.put(OperateType.AccountStatus.getCode(), high);
        level.put(OperateType.TestEmailServer.getCode(), low);
        level.put(OperateType.TestSyslog.getCode(), low);
        level.put(OperateType.Lock.getCode(), high);
        level.put(OperateType.Unlock.getCode(), high);
        level.put(OperateType.SetUserLoginTimes.getCode(), low);
        level.put(OperateType.SignDeclaration.getCode(), low);
        level.put(OperateType.UpdateLoginConfig.getCode(), medium);
        
        return level.get(code);
    }
    
}
