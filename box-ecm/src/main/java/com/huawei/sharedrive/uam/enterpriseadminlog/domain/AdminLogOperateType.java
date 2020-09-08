package com.huawei.sharedrive.uam.enterpriseadminlog.domain;

import java.util.Locale;

import com.huawei.sharedrive.uam.adminlog.dao.impl.LogLanguageHelper;

import pw.cdmi.core.utils.BundleUtil;

public enum AdminLogOperateType
{
    
    KEY_IMPORT_USER("key.import.user", 1);
    
    private String modelName;
    
    private int value;
    
    private static final String USR_LOG_FILE = "enterpriseAdminLog";
    
    static
    {
        BundleUtil.addBundle(USR_LOG_FILE, new Locale[]{Locale.ENGLISH, Locale.CHINESE});
        BundleUtil.setDefaultBundle(USR_LOG_FILE);
        BundleUtil.setDefaultLocale(Locale.CHINESE);
    }
    
    AdminLogOperateType(String modelName, int value)
    {
        this.modelName = modelName;
        this.value = value;
    }
    
    public String getDetails(String[] params)
    {
        return BundleUtil.getText(USR_LOG_FILE, LogLanguageHelper.getLanguage(), this.modelName, params);
    }
    
    public String getDetails()
    {
        return BundleUtil.getText(USR_LOG_FILE, LogLanguageHelper.getLanguage(), this.modelName);
    }
    
    public String getModelName()
    {
        return modelName;
    }
    
    public int getValue()
    {
        return value;
    }
    
}
