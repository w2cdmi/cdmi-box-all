package pw.cdmi.box.disk.utils;

import java.util.HashMap;
import java.util.Map;

import pw.cdmi.box.disk.client.domain.node.INode;

public final class FileTypeUtils
{
    
    private static Map<String, String> iconMap = new HashMap<String, String>(
        BusinessConstants.INITIAL_CAPACITIES);
    
    private FileTypeUtils()
    {
        
    }
    
    static
    {
        iconMap.put(".doc", "doc");
        iconMap.put(".dot", "doc");
        iconMap.put(".docx", "doc");
        iconMap.put(".dotx", "doc");
        iconMap.put(".docm", "doc");
        iconMap.put(".dotm", "doc");
        
        iconMap.put(".ppt", "ppt");
        iconMap.put(".pot", "ppt");
        iconMap.put(".pps", "ppt");
        iconMap.put(".pptx", "ppt");
        iconMap.put(".ppsx", "ppt");
        iconMap.put(".pptm", "ppt");
        iconMap.put(".potm", "ppt");
        iconMap.put(".potx", "ppt");
        iconMap.put(".ppam", "ppt");
        
        iconMap.put(".jpg", "img");
        iconMap.put(".jpeg", "img");
        iconMap.put(".gif", "img");
        iconMap.put(".bmp", "img");
        iconMap.put(".png", "img");
        
        iconMap.put(".xls", "xls");
        iconMap.put(".xlt", "xls");
        iconMap.put(".xlsx", "xls");
        iconMap.put(".xltx", "xls");
        iconMap.put(".xlsm", "xls");
        iconMap.put(".xltm", "xls");
        iconMap.put("xlsb", "xls");
        iconMap.put(".xlam", "xls");
        
        iconMap.put(".mp3", "music");
        iconMap.put(".wav", "music");
        iconMap.put(".wma", "music");
        iconMap.put(".wm", "music");
        iconMap.put(".midi", "music");
        iconMap.put(".mid", "music");
        
        iconMap.put(".avi", "video");
        iconMap.put(".mpg", "video");
        iconMap.put(".rm", "video");
        iconMap.put(".wmv", "video");
        iconMap.put(".mpeg", "video");
        iconMap.put(".mp4", "video");
        
        iconMap.put(".pdf", "pdf");
        
        iconMap.put(".txt", "txt");
        
        iconMap.put(".rar", "rar");
        
        iconMap.put(".zip", "zip");
        
    }
    
    public static String getFileIconType(INode node)
    {
        if (FilesCommonUtils.isFolderType(node.getType()))
        {
            return "";
        }
        int lostPos = node.getName().lastIndexOf(".");
        if (-1 == lostPos)
        {
            return "default";
        }
        
        String res = iconMap.get(node.getName().substring(lostPos));
        if (res == null)
        {
            return "default";
        }
        return res;
    }
    
}
