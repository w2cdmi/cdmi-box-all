package com.huawei.sharedrive.uam.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ImageCheckUtil
{
    private ImageCheckUtil()
    {
        
    }
    
    public static String getSuffix(String fileName)
    {
        String[] temp = fileName.split("\\.");
        int len = temp.length;
        if (len < 2)
        {
            return "";
        }
        String fileSuffix = temp[len - 1];
        return fileSuffix;
    }
    
    public static String getformatName(String fileName)
    {
        if (null == fileName)
        {
            return null;
        }
        String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);
        return formatName;
    }
    
    /**
     * 
     * @param fileName
     * @param suffix
     * @param iglore
     * @return
     */
    public static boolean checksuffix(String fileName, String suffix, boolean iglore)
    {
        String fileSuffix = getSuffix(fileName);
        
        if (iglore)
        {
            return suffix.equalsIgnoreCase(fileSuffix);
        }
        else
        {
            return suffix.equals(fileSuffix);
        }
    }
    
    /**
     * 
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src, int len)
    {
        StringBuilder stringBuilder = new StringBuilder();
        
        if (src == null || src.length <= len)
        {
            return null;
        }
        String hv;
        for (int i = 0; i < len; i++)
        {
            hv = Integer.toHexString(src[i] & 0xFF);
            if (hv.length() < 2)
            {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    
    /**
     * 
     * @param imageBytes
     * @return
     */
    public static boolean isImage(byte[] imageBytes)
    {
        if (imageBytes == null || !(imageBytes.length > 0))
        {
            return false;
        }
        Image img = null;
        try
        {
            img = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (img == null || img.getWidth(null) <= 0 || img.getHeight(null) <= 0)
            {
                return false;
            }
            return true;
        }
        catch (IOException e)
        {
            return false;
        }
        finally
        {
            img = null;
        }
    }
    
    /**
     * 
     * @param mapObj
     * @return
     */
    public static boolean validateImageType(byte[] mapObj)
    
    {
        
        boolean ret = false;
        
        ByteArrayInputStream bais = null;
        
        MemoryCacheImageInputStream mcis = null;
        
        try
        
        {
            
            bais = new ByteArrayInputStream(mapObj);
            
            mcis = new MemoryCacheImageInputStream(bais);
            
            Iterator<ImageReader> itr = ImageIO.getImageReaders(mcis);
            ImageReader reader;
            String imageName;
            while (itr.hasNext())
            {
                reader = (ImageReader) itr.next();
                imageName = reader.getClass().getSimpleName();
                if ("PNGImageReader".equals(imageName))
                {
                    ret = true;
                    break;
                }
            }
        }
        finally
        {
            IOUtils.closeQuietly(bais);
            IOUtils.closeQuietly(mcis);
        }
        return ret;
    }
    
    /**
     * 
     * @param imageBytes
     * @param x
     * @param y
     * @param alpha
     * @param suffix
     * @return
     */
    public static byte[] addWaterMark(byte[] imageBytes, int x, int y, float alpha, String formatName)
    {
        
        byte[] b = null;
        ByteArrayOutputStream out = null;
        Image waterImage = null;
        Image image = null;
        try
        {
            image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (image == null)
            {
                return null;
            }
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            if (width <= 0 || height <= 0)
            {
                return null;
            }
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufferedImage.createGraphics();
            bufferedImage = g.getDeviceConfiguration().createCompatibleImage(width,
                height,
                Transparency.TRANSLUCENT);
            g.dispose();
            g = bufferedImage.createGraphics();
            g.drawImage(image, 0, 0, width, height, null);
            
            waterImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            int width1 = waterImage.getWidth(null);
            int height1 = waterImage.getHeight(null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
            
            g.drawImage(waterImage, 0, 0, width1, height1, null);
            g.dispose();
            out = new ByteArrayOutputStream();
            boolean flag = ImageIO.write(bufferedImage, formatName, out);
            if (flag)
            {
                b = out.toByteArray();
            }
        }
        catch (IOException e)
        {
            logger.warn("", e);
        }
        finally
        {
            IOUtils.closeQuietly(out);
            waterImage = null;
            image = null;
        }
        return b;
        
    }
    
    /**
     * 
     * @param imageBytes
     * @return
     */
    public static boolean isSquareImage(byte[] imageBytes)
    {
        if (imageBytes == null || !(imageBytes.length > 0))
        {
            return false;
        }
        BufferedImage img = null;
        try
        {
            img = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (img != null && img.getWidth() == img.getHeight())
            {
                return true;
            }
            return false;
        }
        catch (IOException e)
        {
            return false;
        }
        finally
        {
            img = null;
        }
    }
    
    public static boolean isRightSize(byte[] fileBytes, int maxSize, int bufferRest)
    {
        InputStream tempIpt = new ByteArrayInputStream(fileBytes);
        byte[] buffer = new byte[maxSize + bufferRest];
        int off = 0;
        int len = buffer.length;
        int n = 0;
        
        try
        {
            while (off <= maxSize)
            {
                n = tempIpt.read(buffer, off, len);
                if (n < 0)
                {
                    break;
                }
                off += n;
                len -= n;
            }
        }
        catch (IOException e)
        {
            return false;
        }
        finally
        {
            IOUtils.closeQuietly(tempIpt);
        }
        if (off > maxSize)
        {
            return false;
        }
        return true;
    }
    
    /**
     * 
     * @param mapObj
     * @return
     */
    public static boolean validateImageTypeJpng(byte[] mapObj)
    
    {
        
        boolean ret = false;
        
        ByteArrayInputStream bais = null;
        
        MemoryCacheImageInputStream mcis = null;
        
        try
        
        {
            
            bais = new ByteArrayInputStream(mapObj);
            
            mcis = new MemoryCacheImageInputStream(bais);
            
            Iterator<ImageReader> itr = ImageIO.getImageReaders(mcis);
            ImageReader reader;
            String imageName;
            while (itr.hasNext())
            {
                reader = (ImageReader) itr.next();
                imageName = reader.getClass().getSimpleName();
                if ("PNGImageReader".equals(imageName) || "JPEGImageReader".equals(imageName))
                {
                    ret = true;
                    break;
                }
            }
        }
        finally
        {
            IOUtils.closeQuietly(bais);
            IOUtils.closeQuietly(mcis);
        }
        return ret;
    }
    
    public static String getFileFormat(byte[] mapObj)
    {
        String format = null;
        ByteArrayInputStream bais = null;
        MemoryCacheImageInputStream mcis = null;
        try
        {
            bais = new ByteArrayInputStream(mapObj);
            mcis = new MemoryCacheImageInputStream(bais);
            Iterator<ImageReader> itr = ImageIO.getImageReaders(mcis);
            String imageName;
            ImageReader reader;
            while (itr.hasNext())
            {
                reader = (ImageReader) itr.next();
                imageName = reader.getClass().getSimpleName();
                if ("PNGImageReader".equals(imageName))
                {
                    format = "png";
                    break;
                }
                if ("JPEGImageReader".equals(imageName))
                {
                    format = "jpg";
                    break;
                }
            }
        }
        finally
        {
            IOUtils.closeQuietly(bais);
            IOUtils.closeQuietly(mcis);
        }
        return format;
    }
    
    private static Logger logger = LoggerFactory.getLogger(ImageCheckUtil.class);
    
}
