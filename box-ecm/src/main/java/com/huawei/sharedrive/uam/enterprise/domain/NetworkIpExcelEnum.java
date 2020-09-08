package com.huawei.sharedrive.uam.enterprise.domain;

import java.util.Locale;

import com.huawei.sharedrive.uam.adminlog.dao.impl.LogLanguageHelper;

import pw.cdmi.core.utils.BundleUtil;

public enum NetworkIpExcelEnum
{
    
    NET_REGION_IP_TEMPLATE(1, "net.region.ip.template"), NETREGIONIP_BULB(2, "netregionip.bulb"), NETREGIONIP_EXPORT_BULB(
        3, "netregionip.export.bulb"), NETREGIONIP_EXPORT_EXISTS_CONFLICT(4, "netregionip.export.conflict"), NETREGIONIP_EXPORT_SUCCEEDED(
        5, "netregionip.export.succeeded"), NETREGIONIP_EXPORT_SUCCEEDED_DESC(6,
        "netregionip.export.succeeded.desc"), NETREGIONIP_EXPORT_FAILED_DESC(7,
        "netregionip.export.failed.desc"), NET_REGION_IP_EXPORT_EXCEL_NAME(8,
        "net.region.ip.export.excel.name"), NET_REGION_IP_IMPORT_RESULT_EXPORT(9,
        "net.region.ip.import.result.export");
    
    private int code;
    
    private String name;
    
    private NetworkIpExcelEnum(int code, String name)
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
    
    public static NetworkIpExcelEnum parseType(int code)
    {
        for (NetworkIpExcelEnum s : NetworkIpExcelEnum.values())
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
