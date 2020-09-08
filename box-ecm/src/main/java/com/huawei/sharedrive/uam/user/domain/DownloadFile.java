package com.huawei.sharedrive.uam.user.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DownloadFile
{
    private DownloadFile()
    {
        
    }
    
    private static Logger logger = LoggerFactory.getLogger(DownloadFile.class);
    
    public static void downloadFile(String templatePath, HttpServletRequest request,
        HttpServletResponse response, String name)
    {
        name = templatePath + name;
        response.reset();
        response.setContentType("application/octet-stream");
        if (StringUtils.isEmpty(name))
        {
            return;
        }
        ServletOutputStream out = null;
        InputStream fileInput = null;
        try
        {
            String fileName = name;
            if (fileName.indexOf("..") >= 0)
            {
                throw new IOException("Invalid file name");
            }
            String showName = fileName;
            if (showName.lastIndexOf(File.separator) > 0)
            {
                showName = showName.substring(showName.lastIndexOf(File.separator) + 1);
            }
            String agent = request.getHeader("User-Agent");
            boolean isIE = (agent != null && agent.toUpperCase(Locale.getDefault()).indexOf("MSIE") != -1);
            if (isIE)
            {
                showName = java.net.URLEncoder.encode(showName, "utf-8");
            }
            else
            {
                showName = new String(showName.getBytes("utf-8"), "ISO8859-1");
            }
            response.addHeader("Content-Disposition", "attachment; filename=\"" + showName + "\"");
            fileInput = new FileInputStream(fileName);
            out = response.getOutputStream();
            IOUtils.copy(fileInput, out);
        }
        catch (FileNotFoundException e)
        {
            logger.error(e.getMessage(), e);
        }
        catch (IOException e)
        {
            logger.error(e.getMessage(), e);
        }
        finally
        {
            IOUtils.closeQuietly(fileInput);
            try
            {
                if (fileInput != null)
                {
                    fileInput.close();
                }
            }
            catch (IOException e)
            {
                logger.error(e.getMessage(), e);
            }
            IOUtils.closeQuietly(out);
        }
    }
}
