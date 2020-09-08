/**
 * 
 */
package com.huawei.sharedrive.app.system.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.dataserver.exception.BusinessException;
import com.huawei.sharedrive.app.system.dao.CustomizeLogoDAO;
import com.huawei.sharedrive.app.system.service.CustomizeLogoService;

import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.domain.CustomizeLogo;

/**
 * @author d00199602
 * 
 */
@Component
public class CustomizeLogoServiceImpl implements CustomizeLogoService, ConfigListener
{
    private static Logger logger = LoggerFactory.getLogger(CustomizeLogoServiceImpl.class);
    
    private final static int LOGO_DEFAULT_ID = 0;
    
    @Autowired
    private CustomizeLogoDAO customizeLogoDAO;
    
    private CustomizeLogo localCache;
    
    @Override
    public void configChanged(String key, Object value)
    {
        // TODO Auto-generated method stub
        if (key.equals(CustomizeLogo.class.getSimpleName()))
        {
            logger.info("Reload Icon By Cluster Notify.");
            localCache = (CustomizeLogo) value;
            if (localCache != null && localCache.isNeedRefreshIcon())
            {
                outputIcon(localCache.getIcon());
            }
        }
    }
    
    @Override
    public CustomizeLogo getCustomize()
    {
        // TODO Auto-generated method stub
        if (localCache == null)
        {
            localCache = customizeLogoDAO.get(LOGO_DEFAULT_ID);
        }
        return localCache;
    }
    
    private void outputIcon(byte[] data)
    {
        if (data == null)
        {
            return;
        }
        OutputStream outputStream = null;
        try
        {
            outputStream = new FileOutputStream(getRootPath() + "/static/skins/default/img/logo.ico");
            outputStream.write(data);
        }
        catch (RuntimeException e)
        {
            logger.error("Error in output icon img!", e);
        }
        catch (Exception e)
        {
            logger.error("Error in output icon img!", e);
        }
        finally
        {
            try
            {
                if (outputStream != null)
                {
                    outputStream.close();
                }
            }
            catch (IOException e)
            {
                logger.error("Error in close icon img!", e);
            }
        }
        
    }
    
    public String getRootPath()
    {
        ClassLoader classLoader = CustomizeLogoServiceImpl.class.getClassLoader();
        if (classLoader == null)
        {
            throw new BusinessException("classloader is null");
        }
        URL url = classLoader.getResource("");
        if (url == null)
        {
            throw new BusinessException("url is null");
        }
        String classPath = url.getPath();
        String rootPath = "";
        // windows下
        if ("\\".equals(File.separator))
        {
            rootPath = classPath.substring(1, classPath.indexOf("/WEB-INF/classes"));
        }
        // linux下
        if ("/".equals(File.separator))
        {
            rootPath = classPath.substring(0, classPath.indexOf("/WEB-INF/classes"));
            rootPath = rootPath.replace("\\", "/");
        }
        return rootPath;
    }
}
