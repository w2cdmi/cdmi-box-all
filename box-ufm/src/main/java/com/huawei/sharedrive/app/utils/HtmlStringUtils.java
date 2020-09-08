package com.huawei.sharedrive.app.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.util.HtmlUtils;

public final class HtmlStringUtils
{
    
    private HtmlStringUtils()
    {
        
    }
    
    private static final String PARA_STRING = "<p>";
    
    /**
     * 获取分段字符串
     * 
     * @param old
     * @return
     */
    public static String getParaString(String old)
    {
        if (StringUtils.isBlank(old))
        {
            return "";
        }
        StringReader sr = new StringReader(old);
        BufferedReader reader = new BufferedReader(sr);
        String lineString = null;
        StringBuilder sb = new StringBuilder();
        try
        {
            while ((lineString = reader.readLine()) != null)
            {
                sb.append(PARA_STRING).append(HtmlUtils.htmlEscape(StringUtils.trimToEmpty(lineString)));
            }
            return sb.toString();
        }
        catch (IOException e)
        {
            return "";
        }
        finally
        {
            IOUtils.closeQuietly(sr);
            IOUtils.closeQuietly(reader);
        }
        
    }
    
}
