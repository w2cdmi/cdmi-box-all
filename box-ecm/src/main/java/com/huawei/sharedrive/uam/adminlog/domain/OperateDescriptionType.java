package com.huawei.sharedrive.uam.adminlog.domain;

import java.util.Locale;

import com.huawei.sharedrive.uam.adminlog.dao.impl.LogLanguageHelper;

import pw.cdmi.core.utils.BundleUtil;

public enum OperateDescriptionType
{
    ENABLE_USER(1, "description.enable.user"), MODIFY_ADMIN(1, "description.admin.modify"), DELETE_APP(2,
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
        28, "excel.exist.user.conflict"), EXECL_UPDATE_USER(29, "execl.update.user"), INVALID_APPID(30,
        "invalid.appid"), EXIST_ACCOUNT_USER_CONFLICT(31, "exist.account.user.conflict"), ADD_USER_SUCCESS(
        32, "add.user.success"), NOT_SUPPORT_LOCAL_AUTH(33, "not.support.local.auth"), INVALID_APP_PARAM(34,
        "invalid.app.param"), CREATE_ACCOUNT_USER(35, "create.account.user"), UPDATE_ACCOUNT_USER(36,
        "update.account.user"), IMPROT_EMPLOYEES_BULB(37, "improt.employees.bulb"),
    	EMPLOYEES_ORG_BULB(38,"employees.organize.bulb"),IMPROT_EMPLOYEES_ORG_BULB(39, "improt.employees.organize.bulb");
    
    private int code;
    
    private String name;
    
    private OperateDescriptionType(int code, String name)
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
    
    private static final String ADMIN_LOG_FILE = "adminLog";
    
    public String getDetails(String[] params)
    {
        return BundleUtil.getText(ADMIN_LOG_FILE, LogLanguageHelper.getLanguage(), this.name, params);
    }
    
    public String getDetails(Locale locale, String[] params)
    {
        return BundleUtil.getText(ADMIN_LOG_FILE, locale, this.name, params);
    }
    
    static
    {
        BundleUtil.addBundle(ADMIN_LOG_FILE, new Locale[]{Locale.ENGLISH, Locale.CHINESE});
    }
}
