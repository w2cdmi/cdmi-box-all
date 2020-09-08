package com.huawei.sharedrive.app.utils.hwcustom;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.utils.PropertiesUtils;

public final class HeaderPacker
{
    private HeaderPacker()
    {
        
    }
    
    public static Map<String, String> getCustomHeaderMap(HttpServletRequest request)
    {
        if(!isHeaderThroghEnable())
        {
            return null;
        }
        Enumeration<String> headerNameIter = request.getHeaderNames();
        String tempHeader;
        Map<String, String> customHeaderMap = null;
        while(headerNameIter.hasMoreElements())
        {
            tempHeader = headerNameIter.nextElement();
            if(isHwCustomHeader(tempHeader))
            {
                if(null == customHeaderMap)
                {
                    customHeaderMap = new HashMap<String, String>(2);
                }
                customHeaderMap.put(tempHeader, request.getHeader(tempHeader));
            }
        }
        return customHeaderMap;
    }
    
    
    private static boolean isHwCustomHeader(String headerName)
    {
        String prefix = PropertiesUtils.getProperty("hwit.header.custom", "x-Tool", PropertiesUtils.BundleName.HWIT);
        if(StringUtils.startsWithIgnoreCase(headerName, prefix))
        {
            return true;
        }
        return false;
    }
    
    private static boolean isHeaderThroghEnable()
    {
        String value = PropertiesUtils.getProperty("hwit.header.through.enable", "false", PropertiesUtils.BundleName.HWIT);
        if(StringUtils.equalsIgnoreCase(value, "true"))
        {
            return true;
        }
        return false;
    }
    
}
