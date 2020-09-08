package com.huawei.sharedrive.isystem.util.file;

import org.springframework.util.StringUtils;

import com.huawei.sharedrive.isystem.util.PropertiesUtils;

public final class TempFileUtils
{
    
    private static StringBuffer tempPath;
    
    private static final String TEMP_KEY = "path.temp";
    
    private TempFileUtils()
    {
        
    }
    
    public static String getTempPath()
    {
        if (null == tempPath)
        {
            synchronized (TempFileUtils.class)
            {
                if (null == tempPath)
                {
                    tempPath = new StringBuffer();
                    
                    tempPath.append(PropertiesUtils.getProperty(TEMP_KEY,
                        "/opt/tomcat_isystem/webapps/isystem/temp/"));
                }
                String temp = tempPath.toString();
                if(StringUtils.isEmpty(temp))
                {
                    tempPath.append('/');
                }
                else if (temp.charAt(temp.length() - 1) != '/')
                {
                    tempPath.append('/');
                }
            }
        }
        return tempPath.toString();
    }
    
}
