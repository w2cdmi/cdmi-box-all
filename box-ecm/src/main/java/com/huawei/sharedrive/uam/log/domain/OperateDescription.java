package com.huawei.sharedrive.uam.log.domain;

import java.util.Locale;

import com.huawei.sharedrive.uam.adminlog.dao.impl.LogLanguageHelper;

import pw.cdmi.core.utils.BundleUtil;

public enum OperateDescription
{
    ENABLE_USER(1, "description.enable.user"), MODIFY_ADMIN(32, "description.admin.modify"), DELETE_APP(2,
        "sys.app.delete"), ADD_APP(3, "sys.app.add"), MODIFY_APP(4, "sys.app.modify"), DELETE_ADMIN(5,
        "authorize.delete.admin"), CREATE_ADMIN(6, "authorize.create.admin"), DELETE_APP_KEY(7,
        "app.delete.app.key"), CREATE_APP_KEY(8, "app.create.app.key"), ENABLE_BATCH_USER(9,
        "user.batch.enable"), DISABLE_BATCH_USER(10, "user.batch.disable"), DISABLE_USER(11,
        "user.disable.user"), USER_INFORMATION_TEMPLATE(12, "user.information.template"), USER_INFORMATION_LIST(
        13, "user.information.list"), USER_BULB(14, "user.bulb"), EXCEL_PARSE_FAILED(15, "excel.parse.failed"), EXCEL_INVALID_VERSION(
        16, "excel.invalid.version"), EXCEL_INVALID_PARAMTER(17, "excel.invalid.paramter"), EXCEL_SERVER_ERROR(
        18, "excel.server.error"), EXCEL_ENCRYPT_ERROR(19, "excel.encrypt.error"), EXCEL_NOSUCHREGION_FAILED(
        20, "excel.noSuchRegion.error"), EXCEL_OTHER_ERROR(21, "excel.other.error"), EXCEL_CLIENT_UPLOAD_FAILED(
        22, "excel.client.upload.failed"), UPLOAD_CLIENT(23, "upload.client"), CREATE_ENTERPRISE(24,
        "enterprise.create"), AUTHSERVER_TYPE_LOCAL(25, "authserver.type.local"), AUTHSERVER_DESCRIPTION_LOCAL(
        26, "authserver.description.local"), EMPLOYEES_BULB(27, "employees.bulb"), EXCEL_EXIST_USER_CONFILICT(
        28, "excel.exist.user.conflict"), DESCRIPTION_ENABLE_ADMIN(29, "description.enable.admin"), DESCRIPTION_DISABLE_ADMIN(
        30, "description.disable.admin"), DELETE_ADMIN_ID(31, "authorize.delete.admin.id"), SYS_ADMIN_SET_EMAIL(
        33, "sys.admin.set.email"), ADMIN_LOG_SET(34, "admin.log.set"), ADMIN_CHANGE_USERNAME(35,
        "admin.change.username"), SYS_APP_UPDATE(36, "sys.app.update"), APP_ADMIN_SET_EMAIL(37,
        "app.admin.set.email"), APP_BASIC_CONFIG(38, "app.basic.config"), ADMIN_CHANGE_MAX_TEAM(39,
        "admin.change.max.team"), ADMIN_CHANGE_TEAM_QUOTA(40, "admin.change.team.quota"), ADMIN_CHANGE_TEAM_VERSIONS(
        41, "admin.change.team.versions"), ADMIN_MODIFY_SECURITY(42, "admin.modify.security"), ADMIN_SYSTEM_PERSONALIZED(
        43, "admin.system.personalized"), ADMIN_UPLOAD(44, "admin.upload"), UPDATE_ENTERPRISE_STATUS(45,
        "update.enterprise.status"), BIND_APP(46, "bind.app"), ADMIN_SYSLOG(47, "admn.syslog"), ADMIN_SET_ACCESS(
        48, "admn.set.access"), ADMIN_SET_STATISTICS_ACCESSKEY(49, "admin.set.statistics.accesskey"), PUBLISH_ANNOUNCEMENT(
        50, "publish.announcement"), DELETE_ANNOUNCEMENT(51, "delete.announcement"), CONFIG_ANNOUNCEMENT(52,
        "config.announcement"), ASYC_TASK_STOP(53, "asyc.task.stop"), ASYC_TASK_START(54, "asyc.task.start"), ENTERPRISE_INFO(
        55, "enterprise.info"), AUTHSERVER_TYPE_ADD(56, "authserver.type.add"), AUTHSERVER_TYPE_DELETE(57,
        "authserver.type.delete"), AUTHSERVER_TYPE_BIND_APP(58, "authserver.type.bind.app"), ADMIN_SET_AUTHENTICATION(
        59, "admin.set.authentication"), AUTHSERVER_FILTER_NODE(60, "authserver.filter.node"), ADMIN_CONFIG_NODE_FILTER(
        61, "admin.config.node.filter"), ADMIN_USER_SEARCH_RULE(62, "admin.user.search.rule"), ADMIN_FIELD_MAPPING(
        63, "admin.field.mapping"), ENTERPRISE_EMPLOYEES(64, "enterprise.employees.add"), ENTERPRISE_EMPLOYEES_OPEN_ACCOUNT(
        65, "enterprise.employees.open.account"), ENTERPRISE_EMPLOYEES_DELETE(66,
        "enterprise.employees.delete"), ENTERPRISE_EMPLOYEES_EXPORT(67, "enterprise.employees.export"), ENTERPRISE_EMPLOYEES_FAIL_EXPORT(
        68, "enterprise.employees.fail.export"), ENTERPRISE_EMPLOYEES_DELETE_FAIL_EXPORT(69,
        "enterprise.employees.delete.fail.export"), ENTERPRISE_EMPLOYEES_EXPORT_TEMPLATE(70,
        "enterprise.employees.export.template"), ENTERPRISE_EMPLOYEES_IMPORT(71,
        "enterprise.employees.import"), ADMIN_SYNCHRONIZE_USER(72, "admin.synchronize.user"), ENTERPRISE_EMPLOYEES_CLEAR_CHECK(
        73, "enterprise.employees.clear.check"), ENTERPRISE_EMPLOYEES_CLEAR_DELETE(74,
        "enterprise.employees.clear.delete"), ENTERPRISE_EMPLOYEES_CLEAR_DELETE_ALL(75,
        "enterprise.employees.clear.delete.all"), ADMIN_LOGIN_SUCESS(76, "admin.login.sucess"), ADMIN_LOGIN_FAIL(
        77, "admin.login.fail"), ADMIN_LOGOUT(78, "admin.logout"), APP_USER_EXPORT(79, "app.user.export"), APP_USER_STATUS_UPDATE(
        80, "app.user.status.update"), APP_USER_INFO_UPDATE(81, "app.user.into.update"), APP_USER_SET_ROLE(
        82, "app.user.set.role"), ENTERPRISE_USER_CHANGE_MAX_TEAM(83, "enterprise.user.max.team"), ENTERPRISE_USER_TEAM_QUOTA(
        84, "enterprise.user.team.quota"), ENTERPRISE_USER_TEAM_VERSIONS(85, "enterprise.user.team.versions"), SECURITY_ACCESS_CONTROL_ROLE_ADD(
        86, "security.access.control.role.add"), SECURITY_ACCESS_CONTROL_ROLE_UPDATE(87,
        "security.access.control.role.update"), SECURITY_ACCESS_CONTROL_ROLE_DELETE(88,
        "security.access.control.role.delete"), ADMN_CHANGE_DEFAULT_PWD(89, "admin.change.default.pwd"), ADMIN_CHANGE_PWD(
        90, "admn.change.pwd"), ADMIN_CHANGE_EMAIL(91, "admin.change.email"), WATER_MARK_UPLOAD(92,
        "water.mark.upload"), SYSTEM_ROLE_DELETE(93, "system.role.delete"), SYSTEM_ROLE_SET(94,
        "system.role.set"), AUTHSERVER_TYPE_NETWORK_AUTH(95, "authserver.type.network.auth"), ACCOUNT_BASIC_CONFIG(
        299, "account.basic.config"), SECURITY_LEVEL_DELETE(300, "security.level.delete"), SECURITY_LEVEL_ADD(
        301, "security.level.add"), SECURITY_LEVEL_UPDATE(302, "security.level.update"), RESOURCE_STRATEGY_DELETE(
        303, "resource.strategy.delete"), RESOURCE_STRATEGY_ADD(304, "resource.strategy.add"), RESOURCE_STRATEGY_UPDATE(
        305, "resource.strategy.update"), NETWORK_REGION_DELETE(306, "network.region.delete"), NETWORK_REGION_ADD(
        307, "network.region.add"), NETWORK_REGION_UPDATE(308, "network.region.update"), NETWORK_REGION_IP_DELETE(
        309, "network.region.ip.delete"), NETWORK_REGION_IP_UPDATE(310, "network.region.ip.update"), NETWORK_REGION_IP_ADD(
        311, "network.region.ip.add"), ACCESSCONFIG_SWITCH_CHANGE_OPEN(312, "accessconfig.switch.change.open"), ACCESSCONFIG_SWITCH_CHANGE_CLOSE(
        313, "accessconfig.switch.change.close"), ACCESSCONFIG_CHANGE(314, "accessconfig.change"), ACCESSCONFIG_ADD(
        315, "accessconfig.add"), ACCESSCONFIG_DELETE(316, "accessconfig.delete"), ACCESSCONFIG_SPACE_ADD(
        317, "accessconfig.space.add"), ACCESSCONFIG_SPACE_DELETE(318, "accessconfig.space.delete"), ACCESSCONFIG_SPACE_CHANGE(
        319, "accessconfig.space.change"), ACCESSCONFIG_SPACE_SWITCH_CHANGE_OPEN(320,
        "accessconfig.space.switch.change.open"), ACCESSCONFIG_SPACE_SWITCH_CHANGE_CLOSE(321,
        "accessconfig.space.switch.change.close"), ACCESSCONFIG_FILE_COPY_SWITCH_CHANGE_OPEN(322,
        "accessconfig.fileCopy.switch.change.open"), ACCESSCONFIG_FILE_COPY_SWITCH_CHANGE_CLOSE(323,
        "accessconfig.fileCopy.switch.change.close"), ADMIN_RESET_PWD(324, "admin.reset.pwd"), ENTERPRISE_APP_PREVIEW(
        325, "enterprise.app.preview"), ENTERPRISE_AUTHSERVER_STATUS(326, "enterprise.authserver.status"), ACCESSCONFIG_FILE_COPY_ADD(
        327, "accessconfig.filecopy.add"), ACCESSCONFIG_FILE_COPY_ADD_ERROR(328,
        "accessconfig.filecopy.add.error"), ACCESSCONFIG_FILE_COPY_DELETE(329, "accessconfig.filecopy.delete"), ADMIN_LOGIN_LOCKED(
        330, "admin.login.locded"), ADMIN_DELETE_NETREGION_CONFIG_IMPORT_RESULT(331,
        "admin.delete.netregion.config.import.result"), STATISTICS_EXPORT_USER_HISTORY(332,
        "statistics.export.user.history"), STATISTICS_SPACE_SET(333, "statistics.space.set"), ADMIN_IMPORT_NETREGION_CONFIG(
        334, "admin.import.netregion.config"), AUTHSERVER_TYPE_MODIFY_APP(335, "authserver.type.modify.app"), AUTHSERVER_TYPE_DELETE_APP(
        336, "authserver.type.delete.app"), ADMIN_SYNCHRONIZE_USER_FAIL(337, "admin.synchronize.user.fail"), ACCOUNT_ATTRIBUTES_EMAIL(
        338, "account.attributes.email"), LOCK_MODIFY_PWD(340, "lock.modidy.pwd"), UNLOCK_MODIFY_PWD(341,
        "unlock.modify.pwd"), APP_ADMIN_SEND_TEST_EMAIL(342, "app.admin.send.test.email"), SYS_ADMIN_SEND_TEST_EMAIL(
        343, "sys.admin.send.test.email"), ENTERPRISE_USER_IMPORT_REPORT(344, "enterprise.user.import.report"), START_IMPORT(
        345, "start.import"), UPDATE_USER_INFO(346, "execl.update.user"), IMPORT_USER_SUCCESS(347,
        "add.user.success"), CREATE_ACCOUNT_USER(348, "create.account.user"), UPDATE_ACCOUNT_USER(349,
        "update.account.user"), LOGIN_NAME_EMAIL_EXIST(350, "login.name.email.exist"), INVALID_APPID(351,
        "invalid.appid"), INVALID_APP_PARAM(352, "invalid.app.param"), NOT_SUPPORT_LOCAL_AUTH(353,
        "not.support.local.auth"), EXIST_ACCOUNT_USER_CONFLICT(354, "exist.account.user.conflict"), IMPORT_COMPLETE(
        355, "import.complete"), IMPORT_USER_INVALID_PARAMTER(356, "import.user.excel.invalid.paramter"), USER_LOCK(
        100, "user.lock"), USER_UNLOCK(101, "user.unlock"),CHECKCONFIG(357, "check.config");
    
    private int code;
    
    private String name;
    
    private OperateDescription(int code, String name)
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
    
    public static OperateDescription parseDescription(int code)
    {
        for (OperateDescription s : OperateDescription.values())
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
        return BundleUtil.getText(SYSTEM_LOG_FILE, LogLanguageHelper.getLanguage(), this.name, params);
    }
    
    public String getDetails(Locale locale, String[] params)
    {
        return BundleUtil.getText(SYSTEM_LOG_FILE, locale, this.name, params);
    }
    
    static
    {
        BundleUtil.addBundle(SYSTEM_LOG_FILE, new Locale[]{Locale.ENGLISH, Locale.CHINESE});
    }
    
}
