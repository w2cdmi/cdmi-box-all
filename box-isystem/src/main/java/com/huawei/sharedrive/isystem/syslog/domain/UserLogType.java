package com.huawei.sharedrive.isystem.syslog.domain;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.isystem.system.LogListener;

import pw.cdmi.core.utils.BundleUtil;

/**
 * 节点操作类型
 * 
 * @author l90003768
 * 
 */
public enum UserLogType
{
    
    Uncertain("", "", -1, false, "", ""),
    /*** APP */
    APP_CREATE("Create.application", "Create.application.de", 1, true, "log.middle", "log.middle"),
    
    APP_MODIFY("Modify.application", "Update.application", 2, true, "log.high", "log.high"),
    
    APP_DELETE_KEY("app.delete.acceesskey", "Delete.access.key.of.application", 4, true, "log.high",
        "log.high"),
    
    APP_CREATE_KEY("Add.application.key", "Create.access.application", 5, true, "log.middle", "log.middle"),
    
    /*** ADMIN */
    DISABLE_ADMIN("Disable.administrator", "Disable.administrator.de", 6, true, "log.high", "log.high"),
    
    ENABLE_ADMIN("Enable.administrator", "Enable.administrator.de", 7, true, "log.middle", "log.middle"),
    
    DELETE_ADMIN("Delete.administrator", "Delete.administrator.params", 8, true, "log.high", "log.high"),
    
    CHANGE_PWD_ADMIN("Reset.administrator.password", "Reset.administrator.password.de", 9, true, "log.high",
        "log.high"),
    
    CREATE_ADMIN("Add.an.administrator", "Create.administrator.designated.role", 10, true, "log.middle",
        "log.middle"),
    
    SEND_MIAL_ADMIN("addmin.sendEmail", "addmin.sendEmail.de", 11, true, "log.middle", "log.middle"),
    
    MODIFY_ADMIN("Change.administrator.role", "Modify.administrator.designated.role", 12, true, "log.high",
        "log.high"),
    
    ADMIN_NAME("Change.username", "Change.username.de", 13, true, "log.low", "log.low"),
    
    /*** mail */
    MAIL_SAVE("mail.server.save", "mail.server.save.de", 16, true, "log.middle", "log.middle"),
    
    MAIL_TEST("mail.server.test", "mail.server.test.de", 17, true, "log.middle", "log.middle"),
    
    /*** cluster */
    STORAGE_ADD("Add.storage", "Add.storage.to.data.center", 18, true, "log.high", "log.high"),
    
    STORAGE_MODIFY("Modify.storage", "Modify.storage.data.center", 19, true, "log.high", "log.high"),
    
    STORAGE_ENABLE("Enable.storage", "Enable.DC", 20, true, "log.middle", "log.middle"),
    
    STORAGE_DISABLE("Stop.storage", "Stop.DC", 21, true, "log.high", "log.high"),
    
    STORAGE_DELETE("Delete.storage", "Delete.DC", 22, true, "log.high", "log.high"),
    
    DATACENTER_CONFIG_NAME("Configure.datacenter.domainname", "Configure.the.domain name.of.data.center", 23,
        true, "log.high", "log.high"),
    
    DC_CONFIG_LOG("Configure.DC.log.storage", "Configure.DC.log.storage.de", 24, true, "log.high", "log.high"),
    
    DC_ADD("Add.data.center", "Add.data.center.param", 25, true, "log.middle", "log.middle"),
    
    DC_ENABLE("Enable.data.center", "Enable.data.center.param", 26, true, "log.high", "log.high"),
    
    DC_DELETE("Delete.data.center", "Delete.data.center.param", 27, true, "log.high", "log.high"),
    
    REGION_CREATE("Add.a.storage.domain", "Create.storage.domain", 28, true, "log.middle", "log.middle"),
    
    REGION_MODIFY("Modify.domain", "Modify.domain.de", 29, true, "log.high", "log.high"),
    
    REGION_DELETE("Delete.domain", "Delete.domain.de", 73, true, "log.high", "log.high"),
    
    REGION_DEFAULT("Configure.default.domain", "Configure.default.domain.de", 30, true, "log.high",
        "log.high"),
    
    /*** DNS */
    DOMAIN_DELETE("dns.domain.delete", "dns.domain.delete.de", 31, true, "log.high", "log.high"),
    
    DOMAIN_ADD("dns.domain.add", "dns.domain.add.de", 32, true, "log.high", "log.high"),
    
    // DNS_DELETE("dns.server.delete", "dns.server.delete.de", 33, true),
    
    DNS_ADD("dns.server.add", "dns.server.add.de", 34, true, "log.high", "log.high"),
    
    INTRANET_DELETE("dns.intranet.delete", "dns.intranet.delete.de", 35, true, "log.high", "log.high"),
    
    INTRANET_ADD("dns.intranet.add", "dns.intranet.add.de", 36, true, "log.high", "log.high"),
    
    UAS_NET("dns.uas.update", "dns.uas.update.de", 37, true, "log.high", "log.high"),
    
    /*** JOB */
    JOB_START("job.start", "job.start.de", 38, true, "log.middle", "log.middle"),
    
    JOB_STOP("job.stop", "job.stop.de", 39, true, "log.middle", "log.middle"),
    
    /*** license */
    LICENSE_EXPORT("license.export", "license.export", 40, true, "log.middle", "log.middle"),
    
    LICENSE_UPLOAD("license.upload", "license.upload", 41, true, "log.high", "log.high"),
    
    LICENSE_CONFIM("license.confim", "license.confim", 42, true, "log.middle", "log.middle"),
    
    /** LogFile */
    LOGAGENT_SAVE("Configure.AC.log.storage", "Configure.AC.log.storage.de", 43, true, "log.middle",
        "log.middle"),
    
    LOGAGENT_DOWN("logfile.download", "logfile.download.de", 44, true, "log.middle", "log.middle"),
    
    /** plugin */
    PLUGIN_ACCESSKEY_DELETE("plugin.accesskey.delete", "plugin.accesskey.delete.de", 45, true, "log.middle",
        "log.middle"),
    
    PLUGIN_ACCESSKEY_ADD("plugin.accesskey.add", "plugin.accesskey.add.de", 46, true, "log.middle",
        "log.middle"),
    
    PLUGIN_SERVICE_ADD("plugin.server.add", "plugin.server.add.de", 47, true, "log.middle", "log.middle"),
    
    PLUGIN_SERVICE_MODEFY("plugin.server.modify", "plugin.server.modify.de", 48, true, "log.middle",
        "log.middle"),
    
    PLUGIN_SERVICE_DEL("plugin.server.del", "plugin.server.del.de", 49, true, "log.middle", "log.middle"),
    
    PLUGIN_SERVICE_CONFIG("plugin.server.config", "plugin.server.config.de", 50, true, "log.middle",
        "log.middle"),
    
    PLUGIN_SERVICE_SCAN("plugin.server.scan", "plugin.server.scan.de", 51, true, "log.middle", "log.middle"),
    
    /** statistics */
    STATISTIC_ACCESSKEY_ADD("statistic.accesskey.add", "statistic.accesskey.add.de", 52, true, "log.middle",
        "log.middle"),
    
    STATISTIC_ACCESSKEY_DEL("statistic.accesskey.del", "statistic.accesskey.del.de", 53, true, "log.middle",
        "log.middle"),
    
    /** login */
    LOGOUT("Logout", "Logout", 54, true, "log.low", "log.low"),
    
    LOGIN("admin.login", "admin.login", 55, true, "log.low", "log.low"),
    
    LOGIN_EMPTY_ROLE("admin.login", "Undesignated.role", 56, true, "log.low", "log.low"),
    
    /**** sysconfig */
    
    DIRECT_CONFIG("Configure.direct.link", "Configure.direct.link.de", 58, true, "log.high", "log.high"),
    
    LOGO_CONFIG("logo.save", "logo.save", 59, true, "", ""),
    
    SYSLOG_CONFIG("syslog.save", "syslog.save.de", 60, true, "log.high", "log.high"),
    
    SYSLOG_CONFIG_TEST("syslog.test", "syslog.test.de", 61, true, "log.middle", "log.middle"),
    
    SYSLOG_LANG("syslog.langauge", "syslog.langauge.de", 62, true, "log.low", "log.low"),
    
    /*** User */
    USER_PASSWORD("Change.password", "Change.password", 63, true, "log.high", "log.high"),
    
    USER_MAIL("Change.email.address", "Change.email.address", 64, true, "log.low", "log.low"),
    
    //DC_NAT("dc.nat.update", "dc.nat.update.de", 65, true, "log.high", "log.high"),
    
    COPYPLICY_CREATE("copyPolicy.create", "copyPolicy.create.de", 66, true, "log.middle", "log.middle"),
    
    COPYPLICY_UPDATE("copyPolicy.update", "copyPolicy.update.de", 67, true, "log.middle", "log.middle"),
    
    COPYPLICY_UPDATE_STATS("copyPolicy.update.state", "copyPolicy.update.state.de", 68, true, "log.middle",
        "log.middle"),
    
    COPYPLICY_DEL("copyPolicy.del", "copyPolicy.del.de", 69, true, "log.middle", "log.middle"),
    
    
    
    GLOBAL_ENABLE("globalEnable.update", "globalEnable.update.de", 70, true, "log.high", "log.high"),
    
    TASK_STATE_ENABLE("task.state.enable", "task.state.enable.de", 71, true, "log.low", "log.low"),
    
    ADMIN_LOGIN_LOCKED("admin.locked", "admin.login.locded", 72, true, "log.middle", "log.middle"),
    
    ADMIN_LOGIN_UNLOCKED("admin.unlocked", "admin.login.unlocded", 81, true, "log.middle", "log.middle"),
    
    FORGET_PASS("forget.password", "forget.password.de", 74, true, "log.middle", "log.middle"),
    
    RESET_PASS("reset.password", "reset.password.de", 75, true, "log.middle", "log.middle"),
    
    CONFIG_MESSAGE("message.retention.days", "message.retention.days.de", 76, true, "log.middle",
        "log.middle"),
    
    MODIFY_PASSWD_LOCK("modify.passwd.lock", "modify.passwd.lock", 77, true, "log.middle", "log.middle"),
    
    MODIFY_PASSWD_UNLOCK("modify.passwd.unlock", "modify.passwd.unlock", 78, true, "log.middle", "log.middle"),
    
    LOCK_CONFIG("modify.lock.config", "modify.lock.config.de", 79, true,"log.high","log.high"),
    
    UPDATE_REGION_STAUTS("update.data.center.status", "update.data.center.status.de", 79, true,"log.high","log.high"),
    
    UPDATE_REGION_RW("update.data.center.rwstatus", "update.data.center.rwstatus.de", 79, true,"log.high","log.high"),
    
    TIMECONFIG_CREATE("timeConfig.create", "timeConfig.create.de", 80, true, "log.middle", "log.middle"),
    
    TIMECONFIG_DEL("timeConfig.del", "timeConfig.del.de", 81, true, "log.middle", "log.middle"),
        
    TIMECONFIG_ENABLE("timeConfigEnable.update","timeConfigEnable.update.de", 82, true, "log.middle", "log.middle"), 
    
    PRIORITY_SET("priority.set","priority.set.de", 83, true, "log.middle", "log.middle");
    
    private static final String ADMIN_LOG_FILE = "isystemLog";
    
    public static final int PARAM_MAX_LEN = 50;
    
    public static final String PARAM_ELLIPSIS = "...";
    
    public static final String PARAM_INVALID = "parameter.calibration";
    
    static
    {
        BundleUtil.addBundle(ADMIN_LOG_FILE, new Locale[]{Locale.ENGLISH, Locale.CHINESE});
        BundleUtil.setDefaultBundle(ADMIN_LOG_FILE);
        BundleUtil.setDefaultLocale(Locale.ENGLISH);
    }
    
    private String type;
    
    private boolean enable;
    
    private int value;
    
    private String detail;
    
    private String rightRank;
    
    private String wrongRank;
    
    public static UserLogType build(int typeCode)
    {
        UserLogType[] allType = UserLogType.values();
        for (UserLogType tmpType : allType)
        {
            if (tmpType.getValue() == typeCode)
            {
                return tmpType;
            }
        }
        return null;
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    private UserLogType(String type, String detail, int value, boolean enable, String rightRank,
        String wrongRank)
    {
        this.type = type;
        this.value = value;
        this.enable = enable;
        this.detail = detail;
        this.rightRank = rightRank;
        this.wrongRank = wrongRank;
    }
    
    public String getRightRank(String[] params)
    {
        params = replacelongString(params);
        if (StringUtils.isEmpty(this.rightRank))
        {
            return "";
        }
        return BundleUtil.getText(ADMIN_LOG_FILE, LogListener.getLanguage(), this.rightRank, params);
        
    }
    
    public String getWrongRank(String[] params)
    {
        params = replacelongString(params);
        if (StringUtils.isEmpty(this.wrongRank))
        {
            return "";
        }
        return BundleUtil.getText(ADMIN_LOG_FILE, LogListener.getLanguage(), this.wrongRank, params);
        
    }
    
    public String getType(String[] params)
    {
        params = replacelongString(params);
        if (StringUtils.isEmpty(this.type))
        {
            return "";
        }
        return BundleUtil.getText(ADMIN_LOG_FILE, LogListener.getLanguage(), this.type, params);
        
    }
    
    public String getCommonErrorParamDetails(String[] params)
    {
        params = replacelongString(params);
        return "[ERROR]" + BundleUtil.getText(ADMIN_LOG_FILE, LogListener.getLanguage(), PARAM_INVALID, params);
    }
    
    public String getDetails(String[] params)
    {
        params = replacelongString(params);
        return BundleUtil.getText(ADMIN_LOG_FILE, LogListener.getLanguage(), this.detail, params);
    }
    
    public String getErrorDetails(String[] params)
    {
        return "[ERROR]" + getDetails(params);
    }
    
    public boolean isEnable()
    {
        return enable;
    }
    
    public int getValue()
    {
        return value;
    }
    
    public String[] replacelongString(String[] params)
    {
        if (null == params)
        {
            return params;
            
        }
        String p = null;
        for (int i = 0; i < params.length; i++)
        {
            p = params[i];
            if (p != null && p.length() > PARAM_MAX_LEN)
            {
                p = p.substring(0, PARAM_MAX_LEN);
                p = p + PARAM_ELLIPSIS;
                params[i] = p;
            }
        }
        return params;
    }
}
