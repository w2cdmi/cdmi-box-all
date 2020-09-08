package pw.cdmi.box.disk.enterpriseindividual.manager.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.enterpriseindividual.domain.WebIconPcLogo;
import pw.cdmi.box.disk.enterpriseindividual.domain.WebIconPcLogoFileter;
import pw.cdmi.box.disk.enterpriseindividual.manager.EnterpriseIndividualConfigManager;
import pw.cdmi.box.disk.enterpriseindividual.service.EnterpriseIndividualConfigService;
import pw.cdmi.box.disk.system.service.impl.ClientManageServiceImpl;
import pw.cdmi.box.disk.utils.PropertiesUtils;
import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.domain.CustomizeLogo;
import pw.cdmi.core.exception.BusinessException;

@Component
public class EnterpriseIndividualConfigManagerImpl implements EnterpriseIndividualConfigManager,
    ConfigListener
{
    
    private static Logger logger = LoggerFactory.getLogger(EnterpriseIndividualConfigManagerImpl.class);
    
    @Autowired
    private EnterpriseIndividualConfigService enterpriseIndividualConfigService;
    
    private static final String ICON_FILE_PATH = PropertiesUtils.getProperty("icon.file.path",
        "/static/skins/default/img/temp/");
    
    private static final String ICON_NAME_PREFIX = "logo";
    
    private static final String ICON_NAME_SUFFIX = ".ico";
    
    @Override
    public void getExitImageFile(CustomizeLogo customize, WebIconPcLogo webIconPcLogo)
    {
        int countId = enterpriseIndividualConfigService.getAccountId(webIconPcLogo);
        if (countId == 0)
        {
            return;
        }
        WebIconPcLogo iconPcLogo = enterpriseIndividualConfigService.get(webIconPcLogo);
        if (null != iconPcLogo.getWebLogo() && iconPcLogo.getWebLogo().length > 0)
        {
            customize.setLogo(iconPcLogo.getWebLogo());
        }
        if (null != iconPcLogo.getIcon() && iconPcLogo.getIcon().length > 0)
        {
            customize.setIcon(iconPcLogo.getIcon());
        }
        customize.setTitle(iconPcLogo.getTitleCh());
        customize.setTitleEn(iconPcLogo.getTitleEn());
    }
    
    @Override
    public int getAccountId(WebIconPcLogo webIconPcLogo)
    {
        return enterpriseIndividualConfigService.getAccountId(webIconPcLogo);
    }
    
    @PostConstruct
    public void init()
    {
        syncIcon(null);
    }
    
    @Override
    public void configChanged(String key, Object value)
    {
        if (!WebIconPcLogo.class.getSimpleName().equals(key))
        {
            return;
        }
        Long accountId = (Long) value;
        if (null == accountId || 0 == accountId)
        {
            return;
        }
        
        syncIcon(accountId);
        
    }
    
    public static String getIconName(long accountId)
    {
        File iconFile = new File(getIconFilePath());
        String[] names = iconFile.list(new WebIconPcLogoFileter(accountId));
        if (null == names || names.length == 0)
        {
            return "";
        }
        String nameLast = names[names.length - 1];
        return nameLast.substring(ICON_NAME_PREFIX.length(), nameLast.length() - ICON_NAME_SUFFIX.length());
    }
    
    private void syncIcon(Long accountId)
    {
        String iconFileName = null;
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = format.format(date);
        
        if (null == accountId)
        {
            List<WebIconPcLogo> list = enterpriseIndividualConfigService.listForUpdate();
            for (WebIconPcLogo iter : list)
            {
                iconFileName = ICON_NAME_PREFIX + iter.getAccountId() + time + ICON_NAME_SUFFIX;
                if (StringUtils.isBlank(getIconName(iter.getAccountId())))
                {
                    if (null != iter.getIcon() && iter.getIcon().length > 0)
                    {
                        synIcon(iter.getIcon(), iconFileName, iter.getAccountId());
                    }
                }
            }
        }
        else
        {
            WebIconPcLogo webIconPcLogo = new WebIconPcLogo();
            webIconPcLogo.setAccountId(accountId);
            webIconPcLogo = enterpriseIndividualConfigService.get(webIconPcLogo);
            if (null != webIconPcLogo)
            {
                iconFileName = ICON_NAME_PREFIX + accountId + time + ICON_NAME_SUFFIX;
                if (null != webIconPcLogo.getIcon() && webIconPcLogo.getIcon().length > 0)
                {
                    synIcon(webIconPcLogo.getIcon(), iconFileName, accountId);
                }
            }
        }
    }
    
    private void synIcon(byte[] data, String fileName, long accountId)
    {
        if (null == data)
        {
            return;
        }
        OutputStream outputStream = null;
        try
        {
            String iconPath = getIconFilePath();
            File basePath = new File(iconPath);
            if (!basePath.exists())
            {
                if (!basePath.mkdirs())
                {
                    logger.info(basePath.getCanonicalPath() + "dir make failure");
                }
            }
            
            String[] names = basePath.list(new WebIconPcLogoFileter(accountId));
            if (null != names)
            {
                File oldFile = null;
                for (String iter : names)
                {
                    oldFile = new File(iconPath + iter);
                    if (oldFile.isFile() && oldFile.exists())
                    {
                        FileUtils.forceDelete(oldFile);
                    }
                }
            }
            
            outputStream = new FileOutputStream(iconPath + fileName);
            outputStream.write(data);
        }
        catch (IOException e)
        {
            logger.error("Error in write client!", e);
        }
        finally
        {
            IOUtils.closeQuietly(outputStream);
        }
    }
    
    private static String getIconFilePath()
    {
        StringBuffer filePathBuff = new StringBuffer(getRootPath() + ICON_FILE_PATH);
        if ('/' != filePathBuff.charAt(filePathBuff.length() - 1))
        {
            filePathBuff = filePathBuff.append("/");
        }
        String tmpPaht = filePathBuff.toString();
        try
        {
            tmpPaht = URLDecoder.decode(tmpPaht, "utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            logger.info("Decode ufm-8 icon file path fail.");
        }
        return tmpPaht;
    }
    
    private static String getRootPath()
    {
        ClassLoader loader = ClientManageServiceImpl.class.getClassLoader();
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
        // windows
        if ("\\".equals(File.separator))
        {
            // add by wuzhiyuan ,use in juint env
            boolean isWebApp = classPath.indexOf("/WEB-INF/classes") != -1;
            if (isWebApp)
            {
                rootPath = classPath.substring(1, classPath.indexOf("/WEB-INF/classes"));// ?应该没有"/"
            }
        }
        // linux
        if ("/".equals(File.separator))
        {
            rootPath = classPath.substring(0, classPath.indexOf("/WEB-INF/classes"));
            rootPath = rootPath.replace("\\", "/");
        }
        return rootPath;
    }
    
}
