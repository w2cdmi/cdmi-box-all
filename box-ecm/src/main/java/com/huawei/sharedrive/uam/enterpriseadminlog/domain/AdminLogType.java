package com.huawei.sharedrive.uam.enterpriseadminlog.domain;

import java.util.Locale;

import com.huawei.sharedrive.uam.adminlog.dao.impl.LogLanguageHelper;

import pw.cdmi.core.utils.BundleUtil;

public enum AdminLogType
{
    KEY_ADMIN_LOGIN("admin.login", 1, true, "low"),
    
    KEY_ADMIN_LOGIN_LOCK("admin.login.lock", 90, true, "low"),
    
    KEY_ADMIN_LOGIN_UNLOCK("admin.login.unlock", 91, true, "low"),
    
    KEY_ADMIN_LOGIN_ERROR("admin.login.error", 1001, true, "low"),
    
    KEY_ADMIN_LOGOUT("admin.logout", 2, true, "low"),
    
    KEY_ADMIN_LOUOUT_ERROR("admin.logout.error", 1002, true, "low"),
    
    KEY_UPDAYE_PSWD("update.pswd", 3, true, "high"),
    
    KEY_UPDATE_PSWD_ERROR("update.pswd.error", 1003, true, "high"),
    
    KEY_ADMIN_RESET_PWD("admin.reset.pwd", 4, true, "high"),
    
    KEY_ADMIN_RESET_PWD_ERROR("admin.reset.pwd.error", 1004, true, "high"),
    
    KEY_UPDATE_ENTERPRISE_INFO("update.enterprise.info", 5, true, "medium"),
    
    KEY_UPDATE_ENTERPRISE_INFO_ERROR("update.enterprise.info.error", 1005, true, "medium"),
    
    KEY_ADD_AUTH("add.auth", 6, true, "low"),
    
    KEY_ADD_AUTH_ERROR("add.auth.error", 1006, true, "low"),
    
    KEY_DELETE_AUTH("delete.auth", 7, true, "high"),
    
    KEY_DELETE_AUTH_ERROR("delete.auth.error", 1007, true, "high"),
    
    KEY_BIND_APP("bind.app", 8, true, "low"), // high \ medium \ low
    
    KEY_BIND_APP_ERROR("bind.app.error", 1008, true, "low"),
    
    KEY_DELETE_BIND_APP("delete.bind.app", 9, true, "high"),
    
    KEY_DELETE_BIND_APP_ERROR("delete.bind.app.error", 1009, true, "high"),
    
    KEY_STOP_AUTH("stop.auth", 10, true, "medium"),
    
    KEY_STOP_AUTH_ERROR("stop.auth.error", 1010, true, "medium"),
    
    KEY_SYNC_USER_START("enterprise.sync.user.start", 11, true, "low"),
    
    KEY_SYNC_USER_START_ERROR("enterprise.sync.user.start.err", 1011, true, "low"),
    
    KEY_SYNC_USER_END("enterprise.sync.user.end", 12, true, "low"),
    
    KEY_SYNC_USER_ERROR("enterprise.sync.user.error", 1013, true, "high"),
    
    KEY_BASIC_CONFIG("account.basic.config", 14, true, "low"),
    
    KEY_BASIC_CONFIG_ERROR("account.basic.config.error", 1014, true, "low"),
    
    KEY_START_AUTH("start.auth", 15, true, "low"),
    
    KEY_START_AUTH_ERROR("start.auth.error", 1015, true, "low"),
    
    KEY_UPDAYE_OPENACCT("update.openacct.way", 16, true, "medium"),
    
    KEY_UPDAYE_OPENACCT_ERROR("update.openacct.way.error", 1016, true, "medium"),
    
    KEY_EMPLOYEES_ADD("enterprise.employees.add", 17, true, "low"),
    
    KEY_EMPLOYEES_ADD_ERROR("enterprise.employees.add.error", 1017, true, "low"),
    
    KEY_OPEN_ACCOUNT("employees.open.account", 18, true, "low"),
    
    KEY_OPEN_ACCOUNT_ERROR("employees.open.account.error", 1018, true, "low"),
    
    KEY_DELETE_EMPLOYEE("enterprise.employees.delete", 19, true, "high"),
    
    KEY_DELETE_EMPLOYEE_ERROR("enterprise.employees.delete.error", 1019, true, "high"),
    
    KEY_EXPORT_EMPLOYEE("employees.export", 20, true, "high"),
    
    KEY_EXPORT_EMPLOYEE_ERROR("employees.export.error", 1020, true, "high"),
    
    KEY_ENABLE_EMPLOYEE("enable.user", 21, true, "low"),
    
    KEY_CHANGE_EMPLOYEE_STATUS("enterprise.employees.status.change", 1121, true, "high"),
    
    KEY_CHANGE_EMPLOYEE_STATUS_ERROR("enterprise.employees.status.change.error", 1122, true, "low"),
    
    KEY_MIGRATE_DATA("enterprise.migratedata", 1123, true, "low"),
    
    KEY_MIGRATE_DATA_ERROR("enterprise.migratedata.error", 1124, true, "high"),
    
    KEY_ENABLE_EMPLOYEE_ERROR("enable.user.error", 1021, true, "low"),
    
    KEY_DISABLE_EMPLOYEE("disable.user", 22, true, "low"),
    
    KEY_DISABLE_EMPLOYEE_ERROR("disable.user.error", 1022, true, "low"),
    
    KEY_SET_ROLE("app.user.set.role", 23, true, "medium"),
    
    KEY_SET_ROLE_ERROR("app.user.set.role.error", 1023, true, "medium"),
    
    KEY_SECURITY_CONTROL_ROLE_ADD("security.access.control.role.add", 24, true, "low"),
    
    KEY_SECURITY_CONTROL_ROLE_UPDATE("security.access.control.role.update", 25, true, "medium"),
    
    KEY_SECURITY_CONTROL_ROLE_DELETE("security.access.control.role.delete", 26, true, "high"),
    
    KEY_SECURITY_CONTROL_OPNE("security.access.control.opne", 27, true, "medium"),
    
    KEY_SECURITY_CONTROL_CLOSE("security.access.control.close", 28, true, "medium"),
    
    KEY_SECURITY_CONTROL_ROLE_ADD_ERROR("security.access.control.role.add.error", 1024, true, "low"),
    
    KEY_SECURITY_CONTROL_ROLE_UPDATE_ERROR("security.access.control.role.update.error", 1025, true, "medium"),
    
    KEY_SECURITY_CONTROL_ROLE_DELETE_ERROR("security.access.control.role.delete.error", 1026, true, "high"),
    
    KEY_SECURITY_CONTROL_OPNE_ERROR("security.access.control.opne.error", 1027, true, "medium"),
    
    KEY_SECURITY_CONTROL_CLOSE_ERROR("security.access.control.close.error", 1028, true, "medium"),
    
    KEY_NETWORK_REGION_DELETE("network.region.delete", 29, true, "high"),
    
    KEY_NETWORK_REGION_ADD("network.region.add", 30, true, "low"),
    
    KEY_NETWORK_REGION_UPDATE("network.region.update", 31, true, "medium"),
    
    KEY_NETWORK_REGION_IP_DELETE("network.region.ip.delete", 32, true, "high"),
    
    KEY_NETWORK_REGION_IP_ADD("network.region.ip.add", 33, true, "low"),
    
    KEY_NETWORK_REGION_IP_UPDATE("network.region.ip.update", 34, true, "medium"),
    
    KEY_NETWORK_REGION_DELETE_ERROR("network.region.delete.error", 1029, true, "high"),
    
    KEY_NETWORK_REGION_ADD_ERROR("network.region.add.error", 1030, true, "low"),
    
    KEY_NETWORK_REGION_UPDATE_ERROR("network.region.update.error", 1031, true, "medium"),
    
    KEY_NETWORK_REGION_IP_DELETE_ERROR("network.region.ip.delete.error", 1032, true, "high"),
    
    KEY_NETWORK_REGION_IP_ADD_ERROR("network.region.ip.add.error", 1033, true, "low"),
    
    KEY_NETWORK_REGION_IP_UPDATE_ERROR("network.region.ip.update.error", 1034, true, "medium"),
    
    KEY_SECURITY_LEVEL_DELETE("security.level.delete", 35, true, "high"),
    
    KEY_SECURITY_LEVEL_ADD("security.level.add", 36, true, "medium"),
    
    KEY_SECURITY_LEVEL_UPDATE("security.level.update", 37, true, "low"),
    
    KEY_SECURITY_LEVEL_DELETE_ERROR("security.level.delete.error", 1035, true, "high"),
    
    KEY_SECURITY_LEVEL_ADD_ERROR("security.level.add.error", 1036, true, "low"),
    
    KEY_SECURITY_LEVEL_UPDATE_ERROR("security.level.update.error", 1037, true, "medium"),
    
    KEY_RESOURCE_DELETE("resource.strategy.delete", 38, true, "high"),
    
    KEY_RESOURCE_ADD("resource.strategy.add", 39, true, "low"),
    
    KEY_RESOURCE_UPDATE("resource.strategy.update", 40, true, "medium"),
    
    KEY_RESOURCE_DELETE_ERROR("resource.strategy.delete.error", 1038, true, "high"),
    
    KEY_RESOURCE_ADD_ERROR("resource.strategy.add.error", 1039, true, "low"),
    
    KEY_RESOURCE_UPDATE_ERROR("resource.strategy.update.error", 1040, true, "medium"),
    
    KEY_FILECOPY_SWITCH_CHANGE_OPEN("accessconfig.fileCopy.switch.change.open", 41, true, "low"),
    
    KEY_FILECOPY_SWITCH_CHANGE_OPEN_ERROR("accessconfig.fileCopy.switch.change.open.error", 1041, true, "low"),
    
    KEY_FILECOPY_SWITCH_CHANGE_CLOSE("accessconfig.fileCopy.switch.change.close", 42, true, "low"),
    
    KEY_FILECOPY_SWITCH_CHANGE_CLOSE_ERROR("accessconfig.fileCopy.switch.change.close.error", 1042, true,
        "low"),
    
    KEY_FILECOPY_SWITCH_CHANGE_ERROR("accessconfig.fileCopy.switch.change.error", 1100, true, "low"),
    
    KEY_INIT_CHANGE_PWD("init.change.pwd", 43, true, "high"),
    
    KEY_INIT_CHANGE_PWD_ERROR("init.change.pwd.error", 1043, true, "high"),
    
    KEY_ADMIN_SET_AUTHENTICATION("admin.set.authentication", 44, true, "low"),
    
    KEY_ADMIN_SET_AUTHENTICATION_ERROR("admin.set.authentication.error", 1044, true, "low"),
    
    KEY_ADMIN_CONFIG_NODE_FILTER("admin.config.node.filter", 45, true, "low"),
    
    KEY_ADMIN_CONFIG_NODE_FILTER_ERROR("admin.config.node.filter.error", 1045, true, "low"),
    
    KEY_USER_SEARCH_RULE("admin.user.search.rule", 46, true, "low"),
    
    KEY_USER_SEARCH_RULE_ERROR("admin.user.search.rule.error", 1046, true, "low"),
    
    KEY_ADMIN_FIELD_MAPPING("admin.field.mapping", 47, true, "low"),
    
    KEY_ADMIN_FIELD_MAPPING_ERROR("admin.field.mapping.error", 1047, true, "low"),
    
    KEY_NETWORK_REGION_IP("network.region.ip.export", 48, true, "high"),
    
    KEY_NETWORK_REGION_IP_ERROR("network.region.ip.export.error", 1048, true, "high"),
    
    KEY_SYATEM_ROLE_SET("system.role.set", 49, true, "low"),
    
    KEY_SYATEM_ROLE_SET_ERROR("system.role.set.error", 1049, true, "low"),
    
    KEY_SYATEM_ROLE_DELETE("system.role.delete", 50, true, "high"),
    
    KEY_SYATEM_ROLE_DELETE_ERROR("system.role.delete.error", 1050, true, "high"),
    
    KEY_RECA_ATTRBUTES_EMAIL("account.rece.attributes.email", 51, true, "medium"),
    
    KEY_SEND_ATTRBUTES_EMAIL("account.send.attributes.email", 52, true, "medium"),
    
    KEY_ACCOUNT_ATTRIBUTES_EMAIL_ERROR("account.attributes.email.error", 1051, true, "medium"),
    
    KEY_WATER_MARK_UPLOAD("water.mark.upload", 53, true, "low"),
    
    KEY_WATER_MARK_UPLOAD_ERROR("water.mark.upload.error", 1053, true, "low"),
    
    KEY_SPACE_SWITCH_CHANGE_OPEN("accessconfig.space.switch.change.open", 54, true, "medium"),
    
    KEY_SPACE_SWITCH_CHANGE_OPEN_ERROR("accessconfig.space.switch.change.open.error", 1054, true, "medium"),
    
    KEY_SPACE_SWITCH_CHANGE_CLOSE("accessconfig.space.switch.change.close", 55, true, "medium"),
    
    KEY_SPACE_SWITCH_CHANGE_CLOSE_ERROR("accessconfig.space.switch.change.close.error", 1055, true, "medium"),
    
    KEY_SPACE_SWITCH_CHANGE_ERROR("accessconfig.space.switch.change.error", 1052, true, "medium"),
    
    KEY_ACCESSCONFIG_SPACE_ADD("accessconfig.space.add", 56, true, "low"),
    
    KEY_ACCESSCONFIG_SPACE_ADD_ERROR("accessconfig.space.add.error", 1056, true, "low"),
    
    KEY_ACCESSCONFIG_SPACE_CHANGE("accessconfig.space.change", 57, true, "medium"),
    
    KEY_ACCESSCONFIG_SPACE_CHANGE_ERROR("accessconfig.space.change.error", 1057, true, "medium"),
    
    KEY_ACCESSCONFIG_SPACE_DELETE("accessconfig.space.delete", 58, true, "high"),
    
    KEY_ACCESSCONFIG_SPACE_DELETE_ERROR("accessconfig.space.delete.error", 1058, true, "high"),
    
    KEY_ACCESSCONFIG_SWITCH_CHANGE_OPEN("accessconfig.switch.change.open", 59, true, "low"),
    
    KEY_ACCESSCONFIG_SWITCH_CHANGE_OPEN_ERROR("accessconfig.switch.change.open.error", 1059, true, "low"),
    
    KEY_ACCESSCONFIG_SWITCH_CHANGE_CLOSE("accessconfig.switch.change.close", 60, true, "low"),
    
    KEY_ACCESSCONFIG_SWITCH_CHANGE_CLOSE_ERROR("accessconfig.switch.change.close.error", 1060, true, "low"),
    
    KEY_ACCESSCONFIG_SWITCH_CHANGE_ERROR("accessconfig.switch.change.error", 1101, true, "low"),
    
    KEY_INDIVIDUAL_CONFIG("account.individual.config", 61, true, "low"),
    
    KEY_INDIVIDUAL_CONFIG_ERROR("account.individual.config.error", 1061, true, "low"),
    
    KEY_ACCESSCONFIG_CHANGE("accessconfig.change", 62, true, "low"),
    
    KEY_ACCESSCONFIG_CHANGE_ERROR("accessconfig.change.error", 1062, true, "low"),
    
    KEY_ACCESSCONFIG_DELETE("accessconfig.delete", 63, true, "high"),
    
    KEY_ACCESSCONFIG_DELETE_ERROR("accessconfig.delete.error", 1063, true, "high"),
    
    KEY_ACCESSCONFIG_ADD("accessconfig.add", 64, true, "low"),
    
    KEY_ACCESSCONFIG_ADD_ERROR("accessconfig.add.error", 1064, true, "low"),
    
    KEY_BINDAPP_OPENACCOUNT_OPEN("bindApp.openaccount.open", 65, true, "low"),
    
    KEY_AUTHORIZE_ROUNCECREATEACCOUNT("authorize.rounceCreateAccount", 66, true, "low"),
    
    KEY_CLEAR_LDAP_USER("clear.ldap.user", 67, true, "high"),
    
    KEY_CLEAR_LDAP_USER_ERROR("clear.ldap.user.error", 1067, true, "high"),
    
    KEY_CLEAR_LDAP_ALLUSER("clear.ldap.alluser", 68, true, "high"),
    
    KEY_CLEAR_LDAP_ALLUSER_ERROR("clear.ldap.alluser.error", 1068, true, "high"),
    
    KEY_APP_USER_INTO_UPDATE("app.user.into.update", 69, true, "low"),
    
    KEY_APP_USER_INTO_UPDATE_ERROR("app.user.into.update.error", 1069, true, "low"),
    
    KEY_ACCESSCONFIG_FILECOPY_ADD("accessconfig.filecopy.add", 70, true, "low"),
    
    KEY_ACCESSCONFIG_FILECOPY_ADD_ERROR("accessconfig.filecopy.add.error", 1070, true, "low"),
    
    KEY_ACCESSCONFIG_FILECOPY_DELETE("accessconfig.filecopy.delete", 71, true, "high"),
    
    KEY_ACCESSCONFIG_FILECOPY_DELETE_ERROR("accessconfig.filecopy.delete.error", 1071, true, "high"),
    
    KEY_INVALID_APP_PARAM("invalid.app.param", 1072, true, "low"),
    
    KEY_ADD_USER_SUCCESS("add.user.success", 73, true, "low"),
    
    KEY_CREATE_ACCOUNT_USER("create.account.user", 74, true, "low"),
    
    KEY_UPDATE_ACCOUNT_USER("update.account.user", 75, true, "low"),
    
    KEY_ENTERPRISE_USER_IMPORT_REPORT("enterprise.user.import.report", 76, true, "low"),
    
    KEY_START_IMPORT("start.import", 77, true, "low"),
    
    KEY_LOGIN_NAME_EMAIL_EXIST("login.name.email.exist", 1073, true, "low"),
    
    KEY_IMPORT_COMPLETE("import.complete", 78, true, "low"),
    
    KEY_IMPORT_USER_EXCEL_INVALID_PARAMTER("import.user.excel.invalid.paramter", 1074, true, "low"),
    
    KEY_EXCEL_SERVER_ERROR("excel.server.error", 1075, true, "low"),
    
    KEY_EXCEL_ENCRYPT_ERROR("excel.encrypt.error", 1076, true, "low"),
    
    KEY_EXCEL_NOSUCHREGION_ERROR("excel.noSuchRegion.error", 1077, true, "low"),
    
    KEY_EXCEL_OTHER_ERROR("excel.other.error", 1078, true, "low"),
    
    KEY_EXCEL_INVALID_VERSION("excel.invalid.version", 1079, true, "low"),
    
    KEY_ADAUT_HUSER_CONFLICT_ERROR("adauth.user.conflict.error", 1080, true, "low"),
    
    KEY_IMPROT_USER("improt.user", 79, true, "low"),
    
    KEY_IMPROT_USER_ERROR("improt.user.error", 1081, true, "low"),
    
    KEY_START_IMPROT_USER("start.improt.user", 80, true, "low"),
    
    KEY_EXECL_UPDATE_USER("execl.update.user", 81, true, "low"),
    
    KEY_INVALID_APPID("invalid.appid", 1082, true, "low"),
    
    KEY_EXIST_ACCOUNT_USER_CONFLICT("exist.account.user.conflict", 1083, true, "low"),
    
    KEY_NOT_SUPPORT_LOCAL_AUTH("not.support.local.auth", 1084, true, "low"),
    
    NON_EXISTENT_APP("non.existent.app", 1085, true, "low"),
    
    KEY_USER_TEAM_SPACE_MAX("enterprise.user.max.team", 82, true, "low"),
    
    KEY_USER_TEAM_SPACE_QUOTA("enterprise.user.team.quota", 83, true, "low"),
    
    KEY_USER_TEAM_SPACE_VERSIONS("enterprise.user.team.versions", 84, true, "low"),
    
    KEY_USER_TEAM_SPACE_MAX_ERROR("enterprise.user.max.team.error", 1086, true, "low"),
    
    KEY_USER_TEAM_SPACE_QUOTA_ERROR("enterprise.user.team.quota.error", 1087, true, "low"),
    
    KEY_USER_TEAM_SPACE_VERSIONS_ERROR("enterprise.user.team.versions.error", 1088, true, "low"),
    
    KEY_CHANGE_AUTH_STATUS_ERROR("change.auth.status.error", 85, true, "low"),
    
    KEY_DELETE_NETREGION_IMPORT_RESULT("admin.delete.netregion.config.import.result", 86, true, "high"),
    
    KEY_DELETE_NETREGION_IMPORT_RESULT_ERROR("admin.delete.netregion.config.import.result.error", 1089, true,
        "high"),
    
    KEY_ENABLE_USER_VALIDATE("enable.user.validate", 1090, true, "low"),
    
    KEY_TEST_AUTH("test.auth", 87, true, "low"),
    
    KEY_TEST_AUTH_ERROR("test.auth.error", 1091, true, "low"),
    
    KEY_TEST_FILED_MAPPIN("test.filed.mappin", 88, true, "low"),
    
    KEY_TEST_FILED_MAPPIN_ERROR("test.filed.mappin.error", 1092, true, "low"),
    
    KEY_SYNCUSERS_USER_VALIDATE("syncusers.user.validate", 1093, true, "high"),
    
    KEY_SYNCUSERS_USER_ERROR("syncusers.user.error", 1094, true, "high"),
    
    KEY_EXCEED_MAX_USER_ERROR("excel.exceed.max.user.error", 1095, true, "high"),
    
    KEY_ENTERPRISE_INFO("enterprise.info", 96, true, "medium"),
    
    KEY_NETWORK_REGION("import.network.region", 97, true, "medium"),
    
    KEY_NETWORK_REGION_ERROR("import.network.region.error", 1096, true, "medium"),
    
    KEY_TEST_NTML("test.ntml", 98, true, "low"),
    
    KEY_TEST_NTML_ERROR("test.ntml.error", 1098, true, "low"),
	KEY_IMPORT_UR_DEPT_ERROR("excel.dept.error",1099,true,"low"),
	KEY_IMPORT_UR_DEPT_NOTEXIST("excel.dept.not.exist.error",1100,true,"low");
    
    AdminLogType(String modelName, int value, boolean enable, String operatLevel)
    {
        this.modelName = modelName;
        this.value = value;
        this.enable = enable;
        this.operatLevel = operatLevel;
    }
    
    private static final String USR_LOG_FILE = "enterpriseAdminLog";
    
    private String modelName;
    
    private int value;
    
    private boolean enable;
    
    private int optTypeKey;
    
    private String operatLevel;
    
    public String getDetails(String[] params)
    {
        return BundleUtil.getText(USR_LOG_FILE, LogLanguageHelper.getLanguage(), this.modelName, params);
    }
    
    public String getDetails()
    {
        return BundleUtil.getText(USR_LOG_FILE, LogLanguageHelper.getLanguage(), this.modelName);
    }
    
    public int getTypeCode()
    {
        return this.value;
    }
    
    public int getOptTypeKey()
    {
        return optTypeKey;
    }
    
    public String getOperatLevel()
    {
        return operatLevel;
    }
    
    public static AdminLogType getAdminLogType(int typeCode)
    {
        AdminLogType[] allType = AdminLogType.values();
        for (AdminLogType tmpType : allType)
        {
            if (tmpType.getTypeCode() == typeCode)
            {
                return tmpType;
            }
        }
        return null;
    }
    
    public int getLevel()
    {
        if (this.value > 1000)
        {
            return 1;
        }
        return 0;
    }
    
    static
    {
        BundleUtil.addBundle(USR_LOG_FILE, new Locale[]{Locale.ENGLISH, Locale.CHINESE});
        BundleUtil.setDefaultBundle(USR_LOG_FILE);
        BundleUtil.setDefaultLocale(Locale.CHINESE);
    }
    
    public boolean isEnable()
    {
        return enable;
    }
    
}
