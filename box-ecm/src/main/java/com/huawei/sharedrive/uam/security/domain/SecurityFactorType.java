package com.huawei.sharedrive.uam.security.domain;

import java.util.Locale;

import com.huawei.sharedrive.uam.adminlog.dao.impl.LogLanguageHelper;

import pw.cdmi.core.utils.BundleUtil;

public enum SecurityFactorType
{
    
    Others(-1, "security.other"), UserType(1, "security.user"), NetworkType(2, "security.network"), DeviceType(
        3, "security.device"), SrcResourceType(4, "security.resource"), ResExtendType(5, "security.extension"), AppType(
        6, "security.app");
    
    private int type;
    
    private String description;
    
    private SecurityFactorType(int type, String description)
    {
        this.type = type;
        this.description = description;
    }
    
    public int getType()
    {
        return type;
    }
    
    public String getDescription()
    {
        return getDetails(null);
    }
    
    public static SecurityFactorType parseType(int type)
    {
        for (SecurityFactorType s : SecurityFactorType.values())
        {
            if (s.getType() == type)
            {
                return s;
            }
        }
        return null;
    }
    
    private static final String USER_DEVICE_FILE = "userDevice";
    
    public String getDetails(String[] params)
    {
        return BundleUtil.getText(USER_DEVICE_FILE, LogLanguageHelper.getLanguage(), this.description, params);
    }
    
    static
    {
        BundleUtil.addBundle(USER_DEVICE_FILE, new Locale[]{Locale.ENGLISH, Locale.CHINESE});
    }
    
}
