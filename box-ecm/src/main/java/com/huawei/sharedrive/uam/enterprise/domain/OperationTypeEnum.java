package com.huawei.sharedrive.uam.enterprise.domain;

import java.util.Locale;

import com.huawei.sharedrive.uam.adminlog.dao.impl.LogLanguageHelper;

import pw.cdmi.core.utils.BundleUtil;

public enum OperationTypeEnum
{
    
    ACCESS_CONFIG_UPLOAD(1, "accessconfig.upload"), ACCESS_CONFIG_NEW_FOLDER(2, "accessconfig.newfolder"), ACCESS_CONFIG_DOWNLOAD(
        3, "accessconfig.download"), ACCESS_CONFIG_COPY(4, "accessconfig.copy"), ACCESS_CONFIG_MOVE(5,
        "accessconfig.move"), ACCESS_CONFIG_PREVIEW(6, "accessconfig.preview"), ACCESS_CONFIG_SHARE(7,
        "accessconfig.share"), ACCESS_CONFIG_LINK(8, "accessconfig.link"), ACCESS_CONFIG_RENAME(9,
        "accessconfig.rename"), ACCESS_CONFIG_DELETE(10, "accessconfig.delete");
    
    private int code;
    
    private String name;
    
    private OperationTypeEnum(int code, String name)
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
    
    public static OperationTypeEnum parseType(int code)
    {
        for (OperationTypeEnum s : OperationTypeEnum.values())
        {
            if (s.getCode() == code)
            {
                return s;
            }
        }
        return null;
    }
    
    private static final String RESOURCE_FEILE = "messages";
    
    public String getDetails(String[] params)
    {
        String details = BundleUtil.getText(RESOURCE_FEILE,
            LogLanguageHelper.getLanguage(),
            this.name,
            params);
        return details;
    }
    
    public String getDetails(Locale locale, String[] params)
    {
        String details = BundleUtil.getText(RESOURCE_FEILE, locale, this.name, params);
        return details;
    }
    
    static
    {
        BundleUtil.addBundle(RESOURCE_FEILE, new Locale[]{Locale.ENGLISH, Locale.CHINESE});
    }
}
