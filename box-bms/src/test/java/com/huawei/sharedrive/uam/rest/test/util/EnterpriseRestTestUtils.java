package com.huawei.sharedrive.uam.rest.test.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.io.IOUtils;

public class EnterpriseRestTestUtils
{
    /**
     * 获取Data文件内容 返回Token对象
     * 
     * @param fileName
     * @return
     */
    public static String getDataFromFile(String fileName)
    {
        StringBuilder sb = new StringBuilder();
        String path = EnterpriseRestTestUtils.class.getResource("").getPath();
        String classPath = path.substring(0, path.indexOf("/classes/") + "/classes/".length())
            .replaceAll("%20", " ");
        
        try
        {
            classPath = URLDecoder.decode(classPath, "utf-8");
        }
        catch (UnsupportedEncodingException e1)
        {
            e1.printStackTrace();
        }
        
        File file = new File(classPath + fileName);
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader(file));
            String temp = null;
            while ((temp = br.readLine()) != null)
            {
                sb.append(temp);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            IOUtils.closeQuietly(br);
        }
        System.out.println(sb.toString());
        return sb.toString();
    }
}
