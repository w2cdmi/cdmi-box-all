package pw.cdmi.box.uam.adminlog.domain;

import java.util.Locale;

import pw.cdmi.box.uam.adminlog.dao.impl.LogLanguageHelper;
import pw.cdmi.core.utils.BundleUtil;

public enum OperateType
{
    
    Uncertain(-1, "ALL_LOG"), Login(1, "ADMIN_LOGIN"), Logout(2, "ADMIN_LOGOUT"), ResetPassword(3,
        "ADMIN_RESET_PWD"), ChangeInitPassword(4, "ADMIN_CHANGE_DEFAULT_PWD"), ChangePassword(5,
        "ADMIN_CHANGE_PWD"), ChangeAdminEmailAddr(6, "ADMIN_CHANGE_EMAIL"), ChangeAdminInfo(7,
        "ADMIN_CHANGE_USERNAME"), CreateLocalAdmin(20, "ADMIN_ADD_ADMIN"), DeleteAdmin(21,
        "ADMIN_DELETE_ADMIN"), ChangeAdmin(22, "ADMIN_MODIFY_ADMIN"), ActiveUser(40, "ADMIN_ENABLE_USER"), DisableUser(
        41, "ADMIN_DISABLE_USER"), ChangeSyslog(60, "ADMIN_SYSLOG"), ChangeCustom(61,
        "ADMIN_SYSTEM_PERSONALIZED"), ChangeBasicConfig(62, "ADMIN_BASIC"), UploadClient(63, "ADMIN_UPLOAD"), ChangeEmailServer(
        64, "ADMIN_SET_EMAIL"), ChangeAuthServer(65, "ADMIN_SET_AUTHENTICATION"), ChangeAccessAddress(67,
        "ADMIN_SET_ACCESS"), ChangeSecurityConfig(68, "ADMIN_MODIFY_SECURITY"), ChangeAuthFeldConfig(69,
        "ADMIN_FIELD_MAPPING"), CreateAppAuth(80, "ADMIN_ADD_APP"), UpdateAppAuth(81, "ADMIN_MODIFY_APP"), AddAppAccessKey(
        43, "ADMIN_ADD_APP_KEY"), DeleteAppAccessKey(44, "ADMIN_DELETE_APP_KEY"), BindAdmin(83,
        "ADMIN_BIND_ADMIN"), CancelBindAdmin(84, "ADMIN_UNBIND_ADMIN"), ChangeSpaceMaxMembers(90,
        "ADMIN_CHANGE_MAX_TEANM"), ChangeSpaceQuota(91, "ADMIN_CHANGE_TEAM_QUOTA"), ChangeSpaceMaxVersions(
        92, "ADMIN_CHANGE_TEAM_VERSIONS"), CreateUser(100, "ADMIN_CREATE_USER"), DeleteUser(101,
        "ADMIN_DELETE_USER"), UpdateUser(102, "ADMIN_UPDATE_USER"), SyncUser(105, "ADMIN_SYNCHRONIZE_USER"), ImportUser(
        106, "ADMIN_IMPORT"), exportUser(107, "ADMIN_EXPORT"), ConfigNodeFilter(108,
        "ADMIN_CONFIG_NODE_FILTER"), ListTreeNode(109, "ADMIN_LIST_TREE_NODE"), FilterNode(110,
        "ADMIN_FILTER_NODE"), UserSearchRule(111, "ADMIN_USER_SEARCH_RULE"), CreateEnterprise(112,
        "ADMIN_ENTERPRISE_CREATE"), ChangeStatisticsAccesskey(120, "ADMIN_SET_STATISTICS_ACCESSKEY");
    
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
    
    private static final String ADMIN_LOG_FILE = "adminLog";
    
    public String getDetails(String[] params)
    {
        String details = BundleUtil.getText(ADMIN_LOG_FILE,
            LogLanguageHelper.getLanguage(),
            this.name,
            params);
        return details;
    }
    
    public String getDetails(Locale locale, String[] params)
    {
        String details = BundleUtil.getText(ADMIN_LOG_FILE, locale, this.name, params);
        return details;
    }
    
    static
    {
        BundleUtil.addBundle(ADMIN_LOG_FILE, new Locale[]{Locale.ENGLISH, Locale.CHINESE});
    }
}
