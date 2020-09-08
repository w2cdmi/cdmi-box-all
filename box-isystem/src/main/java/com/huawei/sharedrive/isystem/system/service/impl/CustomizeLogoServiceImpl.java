/**
 * 
 */
package com.huawei.sharedrive.isystem.system.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.system.dao.CustomizeLogoDAO;
import com.huawei.sharedrive.isystem.system.service.CustomizeLogoService;

import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.domain.CustomizeLogo;

/**
 * @author d00199602
 * 
 */
@Component
public class CustomizeLogoServiceImpl implements CustomizeLogoService, ConfigListener
{
    private static Logger logger = LoggerFactory.getLogger(CustomizeLogoServiceImpl.class);
    
    private static final int LOGO_DEFAULT_ID = 0;
    
    @Autowired
    private ConfigManager configManager;
    
    @Autowired
    private CustomizeLogoDAO customizeLogoDAO;
    
    private CustomizeLogo localCache;
    
    @Override
    public void configChanged(String key, Object value)
    {
        if (key.equals(CustomizeLogo.class.getSimpleName()))
        {
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
        if (localCache == null)
        {
            CustomizeLogo tmpLogo = customizeLogoDAO.get(LOGO_DEFAULT_ID);
            if (tmpLogo != null)
            {
                localCache = tmpLogo;
                configManager.setConfig(CustomizeLogo.class.getSimpleName(), tmpLogo);
            }
        }
        return localCache;
    }
    
    public String getRootPath()
    {
        ClassLoader loader = CustomizeLogoServiceImpl.class.getClassLoader();
        if (loader == null)
        {
            throw new BusinessException();
        }
        URL url = loader.getResource("");
        if (url == null)
        {
            throw new BusinessException();
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
    
    @Override
    public void updateCustomize(CustomizeLogo customize)
    {
        customize.setId(LOGO_DEFAULT_ID);
        customizeLogoDAO.update(customize);
        localCache = customize;
        configManager.setConfig(CustomizeLogo.class.getSimpleName(), customize);
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
        catch (IOException e)
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
}
