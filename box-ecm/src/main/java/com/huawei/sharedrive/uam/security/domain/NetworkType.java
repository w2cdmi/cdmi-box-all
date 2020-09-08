package com.huawei.sharedrive.uam.security.domain;

import java.util.Locale;

import com.huawei.sharedrive.uam.adminlog.dao.impl.LogLanguageHelper;

import pw.cdmi.core.utils.BundleUtil;

public enum NetworkType
{
    
    Others(-1, "common.unknown.type"), UGreen(1, "network.user.green"), UYellow(2, "network.user.yello"), UBlue(
        3, "network.user.blue"), SGreen(4, "network.service.green"), SYellow(5, "network.service.yello"), SBlue(
        6, "network.service.blue"), Internet(7, "network.outernet"), IAccess(8, " iAccess"),
    
    RDVM(9, "network.rd"), NOTRDVM(10, "network.unrd"),
    
    BlackListNetwork(99, "network.blacklist"), WhiteListNetwork(88, "network.whitelist");
    
    private int type;
    
    private String description;
    
    private NetworkType(int type, String description)
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
    
    public static NetworkType parseType(int type)
    {
        for (NetworkType s : NetworkType.values())
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
