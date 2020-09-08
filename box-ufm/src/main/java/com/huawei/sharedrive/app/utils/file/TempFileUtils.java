package com.huawei.sharedrive.app.utils.file;

import com.huawei.sharedrive.app.utils.PropertiesUtils;

public final class TempFileUtils
{
    
    private static StringBuilder tempPath;
    
    private static final String TEMP_KEY = "path.temp";
    
    private static final char SPLIT = '/';
    
    private TempFileUtils()
    {
    }
    
    public static String getTempPath()
    {
        if(null == tempPath)
        {
            synchronized (TempFileUtils.class)
            {
                if(null == tempPath)
                {
                    tempPath = new StringBuilder(PropertiesUtils.getProperty(TEMP_KEY, "/opt/tomcat_ufm/webapps/ufm/temp/"));
                }
                
                if(tempPath.length() == 0 || SPLIT != (tempPath.charAt(tempPath.length() - 1)))
                {
                    tempPath.append(SPLIT);
                }
            }
        }
        return tempPath.toString();
    }
}
