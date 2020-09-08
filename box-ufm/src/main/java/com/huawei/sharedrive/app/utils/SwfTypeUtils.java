package com.huawei.sharedrive.app.utils;

import java.util.HashMap;
import java.util.Map;

import com.huawei.sharedrive.app.files.domain.INode;

public final class SwfTypeUtils
{
    
    private static Map<String, String> iconMap = new HashMap<String, String>(
        BusinessConstants.INITIAL_CAPACITIES);
    
    private SwfTypeUtils()
    {
    }
    
    static
    {
        iconMap.put(".doc", "doc");
        iconMap.put(".docx", "doc");
        iconMap.put(".ppt", "ppt");
        iconMap.put(".pptx", "ppt");
        
        iconMap.put(".jpg", "img");
        iconMap.put(".jpeg", "img");
        iconMap.put(".bmp", "img");
        iconMap.put(".png", "img");
        
        iconMap.put(".xls", "xls");
        iconMap.put(".xlsx", "xls");
        iconMap.put(".pdf", "pdf");
        
    }
    
    public static boolean getSwfType(INode node)
    {
        if (FilesCommonUtils.isFolderType(node.getType()))
        {
            return false;
        }
        int lostPos = node.getName().lastIndexOf(".");
        if (-1 == lostPos)
        {
            return false;
        }
        
        String res = iconMap.get(node.getName().substring(lostPos));
        if (res == null)
        {
            return false;
        }
        return true;
    }
}
