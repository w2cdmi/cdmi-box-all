package com.huawei.sharedrive.uam.security.domain;

import java.util.Locale;

import com.huawei.sharedrive.uam.adminlog.dao.impl.LogLanguageHelper;

import pw.cdmi.core.utils.BundleUtil;

public enum DeviceType
{
    
    Others(-1, "device.other.device"), Web(0, "device.web"), DesktopOrLaptop(1, "device.company="), BYODAndroid(
        2, "device.andoird="), BYIDIos(3, "device.ios"), BYODOther(4, "device.other.byod"), /**
     * 
     * Blacklist Device
     */
    BlackListDevice(99, "device.blacklist"),
    /**
     * White List Device
     */
    WhiteListDevice(88, "device.whitelist");
    
    private int type;
    
    private String description;
    
    private DeviceType(int type, String description)
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
    
    public static DeviceType parseType(int type)
    {
        for (DeviceType s : DeviceType.values())
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
