package pw.cdmi.box.disk.system.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.system.dao.CustomizeLogoDAO;
import pw.cdmi.box.disk.system.service.CustomizeLogoService;
import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.domain.CustomizeLogo;
import pw.cdmi.core.exception.BusinessException;

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
        if (data == null || data.length == 0)
        {
            return;
        }
        OutputStream outputStream = null;
        try
        {
            outputStream = new FileOutputStream(getRootPath() + "/static/skins/default/img/temp/logo.ico");
            outputStream.write(data);
        }
        catch (IOException e)
        {
            logger.error("Error in output icon img!", e);
        }
        finally
        {
            IOUtils.closeQuietly(outputStream);
        }
        
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
        if ("\\".equals(File.separator))
        {
            rootPath = classPath.substring(1, classPath.indexOf("/WEB-INF/classes"));
        }
        if ("/".equals(File.separator))
        {
            rootPath = classPath.substring(0, classPath.indexOf("/WEB-INF/classes"));
            rootPath = rootPath.replace("\\", "/");
        }
        return rootPath;
    }
}
