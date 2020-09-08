package com.huawei.sharedrive.uam.enterprise.manager.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.validation.ValidationException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.huawei.sharedrive.uam.enterprise.domain.WebIconPcLogo;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseIndividualConfigManager;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseIndividualConfigService;
import com.huawei.sharedrive.uam.exception.BadRequestException;
import com.huawei.sharedrive.uam.exception.BaseRunTimeException;
import com.huawei.sharedrive.uam.exception.ImageInvalidException;
import com.huawei.sharedrive.uam.exception.ImageSizeException;

import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.domain.CustomizeLogo;

@Component
public class EnterpriseIndividualConfigManagerImpl implements EnterpriseIndividualConfigManager
{
    private static Logger logger = LoggerFactory.getLogger(EnterpriseIndividualConfigManagerImpl.class);
    
    @Autowired
    private EnterpriseIndividualConfigService enterpriseIndividualConfigService;
    
    @Autowired
    private ConfigManager configManager;
    
    @Override
    public void create(WebIconPcLogo webIconPcLogo)
    {
        createByTrans(webIconPcLogo);
        configManager.setConfig(WebIconPcLogo.class.getSimpleName(), webIconPcLogo.getAccountId());
    }
    
    
    @Override
    public void updateCorpright(WebIconPcLogo webIconPcLogo)
    {
    	enterpriseIndividualConfigService.updateCorpright(webIconPcLogo);
        configManager.setConfig(WebIconPcLogo.class.getSimpleName(), webIconPcLogo.getAccountId());
    }
    
    @Override
    public void validPictrueFormat(String fileName, String[] picTypes)
    {
        if (fileName == null || "".equals(fileName))
        {
            return;
        }
        if (fileName.indexOf(".") == -1)
        {
            throw new ImageInvalidException();
        }
        String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);
        boolean formatVaid = false;
        for (String picType : picTypes)
        {
            if (picType.equalsIgnoreCase(formatName))
            {
                formatVaid = true;
                break;
            }
        }
        if (!formatVaid)
        {
            throw new ImageInvalidException(fileName);
        }
    }
    
    /**
     * 
     * @param file
     * @return
     * @throws IOException
     */
    @Override
    public String getFormatName(InputStream inputStream, String originalFileName)
    {
        ImageInputStream iis = null;
        try
        {
            iis = ImageIO.createImageInputStream(inputStream);
            Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
            if (!iter.hasNext())
            {
                return originalFileName.substring(originalFileName.indexOf(".") + 1);
            }
            ImageReader reader = iter.next();
            return reader.getFormatName();
        }
        catch (IOException ex)
        {
            return originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        }
        finally
        {
            IOUtils.closeQuietly(iis);
        }
    }
    
    @Override
    public boolean fileToByte(MultipartHttpServletRequest request, WebIconPcLogo webIconPcLogo)
    {
        boolean checkSize = true;
        try
        {
            if (checkWebIconPcLogo(webIconPcLogo))
            {
                throw new BadRequestException("webIconPcLogo is null");
            }
            Map<String, MultipartFile> fileMap = request.getFileMap();
            String fieldName;
            MultipartFile file;
            String formatName;
            byte[] fileBytes;
            getExitImageFile(webIconPcLogo);
            for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet())
            {
                fieldName = entry.getKey();
                file = entry.getValue();
                
                if (file.getSize() == 0)
                {
                    continue;
                }
                if (file.getSize() > 128 * 1024)
                {
                    throw new ImageSizeException(fieldName);
                }
                
                formatName = getFormatName(file.getInputStream(), file.getOriginalFilename());
                fileBytes = FileCopyUtils.copyToByteArray(file.getInputStream());
                
                checkFile(fieldName, file, webIconPcLogo, fileBytes, formatName);
            }
        }
        catch (IOException e)
        {
            logger.error("File upload fail!", e);
            throw new ValidationException(e);
        }
        catch (BaseRunTimeException e)
        {
            logger.error("File upload fail!", e);
            throw new ValidationException(e);
        }
        return checkSize;
    }
    
    private boolean checkWebIconPcLogo(WebIconPcLogo webIconPcLogo)
    {
        return null == webIconPcLogo || webIconPcLogo.getTitleEn().length() == 0
            || webIconPcLogo.getTitleEn().length() > 100 || webIconPcLogo.getTitleCh().length() > 100
            || webIconPcLogo.getTitleCh().length() == 0;
    }
    
    @Override
    public String[] getDescription(MultipartHttpServletRequest request, WebIconPcLogo webIconPcLogo)
    {
        String webLogoName = null;
        String webIcoName = null;
        String pcLogoName = null;
        Map<String, MultipartFile> fileMap = request.getFileMap();
        String fieldName;
        MultipartFile file;
        for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet())
        {
            fieldName = entry.getKey();
            file = entry.getValue();
            if ("webLogoFile".equals(fieldName))
            {
                webLogoName = file.getOriginalFilename();
            }
            if ("pcLogoFile".equals(fieldName))
            {
                pcLogoName = file.getOriginalFilename();
            }
            if ("iconFile".equals(fieldName))
            {
                webIcoName = file.getOriginalFilename();
            }
        }
        return new String[]{String.valueOf(webIconPcLogo.getAccountId()), webIconPcLogo.getTitleCh(),
            webIconPcLogo.getTitleEn(), webLogoName, webIcoName, pcLogoName};
    }
    
    @Override
    public WebIconPcLogo getWebIconPcLogo(WebIconPcLogo webIconPcLogo)
    {
        int countId = enterpriseIndividualConfigService.getAccountId(webIconPcLogo);
        if (countId == 0)
        {
            return null;
        }
        WebIconPcLogo iconPcLogo = enterpriseIndividualConfigService.get(webIconPcLogo);
        return iconPcLogo;
    }
    
    @Override
    public void getAccountTitle(CustomizeLogo customize, WebIconPcLogo webIconPcLogo)
    {
        int countId = enterpriseIndividualConfigService.getAccountId(webIconPcLogo);
        if (countId == 0)
        {
            return;
        }
        WebIconPcLogo iconPcLogo = enterpriseIndividualConfigService.get(webIconPcLogo);
        customize.setTitle(iconPcLogo.getTitleCh());
        customize.setTitleEn(iconPcLogo.getTitleEn());
    }
    
    @Override
    public WebIconPcLogo get(WebIconPcLogo webIconPcLogo)
    {
        return enterpriseIndividualConfigService.get(webIconPcLogo);
    }
    
    @Override
    public int getAccountId(WebIconPcLogo webIconPcLogo)
    {
        return enterpriseIndividualConfigService.getAccountId(webIconPcLogo);
    }
    
    public void getExitImageFile(WebIconPcLogo webIconPcLogo)
    {
        int countId = enterpriseIndividualConfigService.getAccountId(webIconPcLogo);
        if (countId == 0)
        {
            return;
        }
        WebIconPcLogo iconPcLogo = enterpriseIndividualConfigService.get(webIconPcLogo);
        webIconPcLogo.setWebLogo(iconPcLogo.getWebLogo());
        webIconPcLogo.setPcLogo(iconPcLogo.getPcLogo());
        webIconPcLogo.setIcon(iconPcLogo.getIcon());
        webIconPcLogo.setWebLogoFormatName(iconPcLogo.getWebLogoFormatName());
        webIconPcLogo.setPcLogoFormatName(iconPcLogo.getPcLogoFormatName());
        webIconPcLogo.setIconFormatName(iconPcLogo.getIconFormatName());
    }
    
    private void checkFile(String fieldName, MultipartFile file, WebIconPcLogo webIconPcLogo,
        byte[] fileBytes, String formatName) throws IOException
    {
        if ("webLogoFile".equals(fieldName))
        {
            BufferedImage images = ImageIO.read(file.getInputStream());
            if (images == null)
            {
                throw new ImageInvalidException(fieldName);
            }
            webIconPcLogo.setWebLogo(fileBytes);
            webIconPcLogo.setWebLogoFormatName(formatName);
            String[] picTypes = {"png"};
            validPictrueFormat(file.getOriginalFilename(), picTypes);
        }
        if ("pcLogoFile".equals(fieldName))
        {
            
            BufferedImage images = ImageIO.read(file.getInputStream());
            if (images == null)
            {
                throw new ImageInvalidException(fieldName);
            }
            webIconPcLogo.setPcLogo(fileBytes);
            webIconPcLogo.setPcLogoFormatName(formatName);
            String[] picTypes = {"jpg", "jpeg", "png"};
            validPictrueFormat(file.getOriginalFilename(), picTypes);
        }
        if ("iconFile".equals(fieldName))
        {
            webIconPcLogo.setIcon(fileBytes);
            webIconPcLogo.setIconFormatName(formatName);
            String[] picTypes = {"ico", "icon"};
            validPictrueFormat(file.getOriginalFilename(), picTypes);
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void createByTrans(WebIconPcLogo webIconPcLogo)
    {
        enterpriseIndividualConfigService.create(webIconPcLogo);
    }
    
}
