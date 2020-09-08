package com.huawei.sharedrive.isystem.adminlog.domain;

import java.util.Locale;

import com.huawei.sharedrive.isystem.system.LogListener;

import pw.cdmi.core.utils.BundleUtil;

/**
 * 
 * @author s90006125
 * 
 */
public enum OperateDescriptionType
{
    /** 不确定的操作 */
    ENABLE_USER(1, ""), ACCESS_CONFIGURATION(2, "Access.network.configuration"), GREEN_ZONE(3, "Green.zone"), YELLOW_ZONE(
        4, "Yellow.zone"), EXTRANET(5, "Extranet"), DELETE_ACCESS_OF_APPLICATION(6,
        "Delete.access.key.of.application"), CREATE_ACCESS_APPLICATION(7, "Create.access.application"), CREATE_APPLICATION_OFPARAMS(
        8, "Create.application.ofParams"), UPDATE_APPLICATION(9, "Update.application"), REFRESH_APPLCATION_PARAMS(
        10, "Refresh.application.params"), DELETE_ADMINISTRATOR_PARAMS(11, "Delete.administrator.params"), ADMINISTRATOR_LOGIN_NAME(
        12, "Administrator.login.name"), UNDESIGNATED_ROLE(13, "Undesignated.role"), CREATE_ADMINISTRATOR_DESIGNATED_ROLE(
        14, "Create.administrator.designated.role"), MODIFY_ADMINISTRATOR_DESIGNATED_ROLE(15,
        "Modify.administrator.designated.role"), ADD_DATA_CENTER_PARAM(16, "Add.data.center.param"), ENABLE_DATA_CENTER_PARAM(
        17, "Enable.data.center.param"), DELETE_DATA_CENTER_PARAM(18, "Delete.data.center.param"), CREATE_STORAGE_DOMAIN(
        19, "Create.storage.domain"), TYPE_OF_VERSION(20, "Type.of.Version"), ADD_STORAGE_TO_DATA_CENTER(21,
        "Add.storage.to.data.center"), MODIFY_STORAGE_DATA_CENTER(22, "Modify.storage.data.center"), ENABLE_DC(
        23, "Enable.DC"), STOP_DC(24, "Stop.DC"), DELETE_DC(25, "Delete.DC"), STORAGE_PARAM(26,
        "Storage.param"), CONFIGURE_DOMAIN_NAME_OF_DATA_CENTER(27, "Configure.the.domain name.of.data.center"), DISABLE_APPLICATION(
        28, "Disable.application"), DELETE_APPLICATION(29, "Delete.application"),
    /**
     * 设置默认存储区域
     */
    REGION_SET_DEFAULT(30, "region.setDefault"),
    /**
     * 添加存储区域
     */
    REGION_ADD(31, "region.add"),
    /**
     * 更新存储区域名称
     */
    REGION_UPDATE_NAME(32, "region.update"),
    /**
     * 删除存储区域
     */
    REGION_DELETE(33, "region.delete");
    
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
