package com.huawei.sharedrive.app.test.openapi.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.test.openapi.share.ShareAPIAddShareTest;

public class MyFileUtils
{

    /**
     * 获取Data文件内容
     * 返回Token对象
     * @param fileName
     * @return
     */
    public static String getDataFromFile(String fileName)
    {
        StringBuilder sb = new StringBuilder();
        String path = ShareAPIAddShareTest.class.getResource("").getPath();
        String classPath = path.substring(0, path.indexOf("/classes/") + "/classes/".length()).replaceAll("%20", " ");
        try
        {
            if(classPath.indexOf("classes")!=-1){
            classPath = URLDecoder.decode(classPath,"utf-8");
            }else{
                classPath=path.substring(0, path.indexOf("com/")).replaceAll("%20", " ");
            }
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
            while( (temp = br.readLine()) != null)
            {
                sb.append(temp.trim());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
           IOUtils.closeQuietly(br);
        }
        System.out.println("file data is:");
        System.out.println(sb.toString());
        return sb.toString();
    }
    
    private final static String FLAG = "@@";
    
    /**
     * 获取Data文件内容
     * 返回Token对象
     * @param fileName
     * @return
     */
    public static String getDataFromFile(String fileName, String flag)
    {
        StringBuilder sb = new StringBuilder();
        String path = ShareAPIAddShareTest.class.getResource("").getPath();
        String classPath = path.substring(0, path.indexOf("/classes/") + "/classes/".length()).replaceAll("%20", " ");
        try
        {
            if(classPath.indexOf("classes")!=-1){
            classPath = URLDecoder.decode(classPath,"utf-8");
        }else{
            classPath=path.substring(0, path.indexOf("com/")).replaceAll("%20", " ");}
        }
        catch (UnsupportedEncodingException e1)
        {
            e1.printStackTrace();
        }
        File file = new File(classPath + fileName);
        BufferedReader br = null;
        boolean beginToRecord = false;
        try
        {
            br = new BufferedReader(new FileReader(file));
            String temp = null;
            while( (temp = br.readLine()) != null)
            {
                
                if(StringUtils.trimToEmpty(temp).startsWith(FLAG))
                {
                    if(StringUtils.trimToEmpty(temp).equals(FLAG + flag))
                    {
                        beginToRecord = true;
                        continue;
                    }
                    else
                    {
                        beginToRecord = false;
                    }
                }
                if(beginToRecord)
                {
                    sb.append(temp.trim());
                }
                
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
           IOUtils.closeQuietly(br);
        }
        System.out.println("file data is:");
        System.out.println(sb.toString());
        return sb.toString();
        
    }
    
    
    
}
