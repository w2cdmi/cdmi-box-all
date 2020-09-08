package com.huawei.sharedrive.uam.security.domain;

import java.util.Locale;

import com.huawei.sharedrive.uam.adminlog.dao.impl.LogLanguageHelper;

import pw.cdmi.core.utils.BundleUtil;

public enum UserSpecialType
{
    
    NORDConfidential(3, "user.confidential"), RDConfidential(4, "user.rd.confidential"),
    /**
     * VIP
     */
    VIP(5, "user.vip"), OUTER(6, "user.outsource"),
    /**
     * Blacklist User
     */
    BlackListUser(99, "user.blacklist"),
    /**
     * White List User
     */
    WhiteListUser(88, "user.whitelist");
    
    private int type;
    
    private String description;
    
    private UserSpecialType(int type, String description)
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
    
    public static UserSpecialType parseType(int type)
    {
        for (UserSpecialType s : UserSpecialType.values())
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
