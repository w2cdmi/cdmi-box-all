package com.huawei.sharedrive.isystem.adminlog.domain;

import java.util.Locale;

import com.huawei.sharedrive.isystem.system.LogListener;

import pw.cdmi.core.utils.BundleUtil;

/**
 * 
 * @author s90006125
 * 
 */
public enum OperateType
{
    /*
     * 登录登出、重置密码、初次设置密码、修改密码、AD域配置、邮箱服务器配置、域控配置、
     * 新增AD管理员、新增本地管理员、删除管理员、修改管理员角色、修改邮箱、新增修改删除区域、新增DC、 启用停用删除DC、新增启用停用删除存储、
     * 启用禁用用户、QOS设置、SYSLOG配置、个性化配置
     * 
     * 
     * 忘记密码、
     */
    
    /** 不确定的操作 */
    Uncertain(-1, ""), Login(1, "admin.login"), Logout(2, "Logout"), CreateRegion(3, "Add.a.storage.domain"), ChangeRegion(
        4, "Modify.domain"), DeleteRegion(5, "Delete.domain"), SetDefaultRegion(6, "Configure.default.domain"), CreateDC(
        11, "Add.data.center"), ActiveDC(12, "Enable.data.center"), DeleteDC(13, "Delete.data.center"), ChangeDCDomainName(
        37, "Configure.datacenter.domainname"), AddStorage(14, "Add.storage"), ChangeStorage(51,
        "Modify.storage"), EnableStorage(15, "Enable.storage"), DisableStorage(16, "Stop.storage"), DeleteStorage(
        17, "Delete.storage"), ResetPassword(18, "Reset.password"), ChangePassword(20, "Change.password"), ChangeEmailServer(
        22, "Modify.email.server.settings"), CreateLocalAdmin(25, "Add.an.administrator"), DeleteAdmin(26,
        "Delete.administrator"), ChangeAdmin(27, "Change.administrator.role"), ChangeAdminEmailAddr(28,
        "Change.email.address"), ChangeAdminInfo(30, "Change.username"), DisableAdmin(31,
        "Disable.administrator"), EnableAdmin(32, "Enable.administrator"), ChangeSyslog(34,
        "Modify.Syslog.settings"), CreateAppAuth(40, "Create.application"), RefreshAppAuthKey(41,
        "Refresh.application.key"), UpdateAppAuth(42, "Modify.application"), AddAppAccessKey(43,
        "Add.application.key"), DeleteAppAccessKey(44, "Delete.application.access.key"), ResetAdminPwd(50,
        "Reset.administrator.password"), DirectLinkConfig(110, "Configure.direct.link"), ACLogArchiveConfig(
        111, "Configure.AC.log.storage"), DCLogArchiveConfig(112, "Configure.DC.log.storage"), ChangeLogConfig(
        35, "modifiy.log.config");
    
    /**
     * 云盘版本不要该功能 ChangeSecurityConfig(36, "修改系统安全设置"), CreateADAdmin(24, "新增AD域管理员"),
     * ChangeADomain(21, "修改AD域配置"), DisableAuthApp(43, "禁用应用"),EnableAuthApp(44, "启用应用"),
     * DeleteAuthApp(45, "删除应用")
     */
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
    
    private static final String ADMIN_LOG_FILE = "isysAdminLog";
    
    public String getDetails(String[] params)
    {
        return BundleUtil.getText(ADMIN_LOG_FILE, LogListener.getLanguage(), this.name, params);
    }
    
    static
    {
        BundleUtil.addBundle(ADMIN_LOG_FILE, new Locale[]{Locale.ENGLISH, Locale.CHINESE});
    }
}
