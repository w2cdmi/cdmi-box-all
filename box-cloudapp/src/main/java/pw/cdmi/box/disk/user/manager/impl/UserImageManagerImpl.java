package pw.cdmi.box.disk.user.manager.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import pw.cdmi.box.disk.user.domain.UserImage;
import pw.cdmi.box.disk.user.manager.UserImageManager;
import pw.cdmi.box.disk.user.service.UserImageService;
import pw.cdmi.core.exception.ImageInvalidException;
import pw.cdmi.core.exception.ImageScaleException;
import pw.cdmi.core.exception.ImageSizeException;

@Component
public class UserImageManagerImpl implements UserImageManager
{
    @Autowired
    private UserImageService userImageService;
    
    private static Logger logger = LoggerFactory.getLogger(UserImageManagerImpl.class);
    
    @Override
    public void create(String sessionId, UserImage userImage)
    {
        userImageService.updateUserImage(sessionId, userImage);
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
            throw new ValidationException();
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
            throw new ImageInvalidException();
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
            if (iis != null)
            {
                IOUtils.closeQuietly(iis);
            }
        }
    }
    
    @SuppressWarnings("PMD.PreserveStackTrace")
    @Override
    public boolean fileToByte(MultipartHttpServletRequest request, UserImage userImage)
    {
        boolean flag = true;
        try
        {
            Map<String, MultipartFile> fileMap = request.getFileMap();
            String fieldName;
            MultipartFile file;
            String formatName;
            byte[] fileBytes;
            BufferedImage image;
            int width;
            int height;
            for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet())
            {
                fieldName = entry.getKey();
                file = entry.getValue();
                
                // 验证图片大小、比例、格式
                
                if (file.getSize() >= 1024 * 1024 || file.getSize() == 0)
                {
                    throw new ImageSizeException();
                }
                
                image = ImageIO.read(file.getInputStream());
                if (image == null)
                {
                    throw new ImageInvalidException();
                }
                width = image.getWidth();
                height = image.getHeight();
//                if (width != height)
//                {
//                    throw new ImageScaleException();
//                }
                
                formatName = getFormatName(file.getInputStream(), file.getOriginalFilename());
                fileBytes = FileCopyUtils.copyToByteArray(file.getInputStream());
                
                if (fieldName != null)
                {
                    userImage.setUserImage(fileBytes);
                    userImage.setImageFormatName(formatName);
                    String[] picTypes = {"png", "jpg"};
                    validPictrueFormat(file.getOriginalFilename(), picTypes);
                }
                
            }
        }
        catch (IOException e)
        {
            logger.error("File upload fail!", e);
            throw new ValidationException("File upload fail!", e);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            logger.error("File upload fail!", e);
            throw new ArrayIndexOutOfBoundsException();
        }
        return flag;
    }
    
    @Override
    public UserImage getUserImage(HttpServletRequest req, UserImage userImage)
    {
        return userImageService.getUserImage(req, userImage);
    }
    
    @Override
    public void checkImageInvalid(MultipartFile file)
    {
        try
        {
            if (file.getSize() > 1024 * 1024 || file.getSize() == 0)
            {
                throw new ImageSizeException();
            }
            
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null)
            {
                logger.error("File upload fail!");
                throw new ValidationException("File upload fail!");
            }
            int width = image.getWidth();
            int height = image.getHeight();
            if (width != height)
            {
                throw new ImageScaleException();
            }
            
            String[] picTypes = {"png", "jpg"};
            validPictrueFormat(file.getOriginalFilename(), picTypes);
            
        }
        catch (IOException e)
        {
            logger.error("File upload fail!", e);
            throw new ValidationException("File upload fail!", e);
        }
    }
    
    @Override
    public UserImage getUserImage(UserImage userImage)
    {
        return userImageService.getUserImage(userImage);
    }
}
