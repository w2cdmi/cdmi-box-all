package pw.cdmi.box.uam.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.validation.ValidationException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

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
     * 检查后缀名
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
        return suffix.equals(fileSuffix);
    }
    
    /**
     * 检查前两个字节
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
        int v = 0;
        String hv = null;
        for (int i = 0; i < len; i++)
        {
            v = src[i] & 0xFF;
            hv = Integer.toHexString(v);
            if (hv.length() < 2)
            {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    
    /**
     * 检查宽和高
     * 
     * @param imageBytes
     * @return
     */
    public static boolean isImage(byte[] imageBytes)
    {
        if (imageBytes == null || imageBytes.length == 0)
        {
            return false;
        }
        ByteArrayInputStream stream = null;
        try
        {
            stream = new ByteArrayInputStream(imageBytes);
            Image img = ImageIO.read(stream);
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
            IOUtils.closeQuietly(stream);
        }
    }
    
    /**
     * 检查类型
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
            ImageReader reader = null;
            String imageName = null;
            while (itr.hasNext())
            {
                reader = itr.next();
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
     * 以自身为水印添加到自身
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
        ByteArrayInputStream inputStream = null;
        try
        {
            inputStream = new ByteArrayInputStream(imageBytes);
            Image image = ImageIO.read(inputStream);
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
            // 将目标图片加载到内存。
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufferedImage.createGraphics();
            bufferedImage = g.getDeviceConfiguration().createCompatibleImage(width,
                height,
                Transparency.TRANSLUCENT);
            g.dispose();
            g = bufferedImage.createGraphics();
            g.drawImage(image, 0, 0, width, height, null);
            
            // 加载水印图片。
            waterImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            int widt = waterImage.getWidth(null);
            int heigh = waterImage.getHeight(null);
            // 设置水印图片的透明度。
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
            
            // 将水印图片“画”在原有的图片的制定位置。
            g.drawImage(waterImage, 0, 0, widt, heigh, null);
            // 关闭画笔。
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
            IOUtils.closeQuietly(inputStream);
        }
        return b;
        
    }
    
    public static void validPictrueFormat(MultipartFile file) throws IOException
    {
        String fileName = file.getOriginalFilename();
        String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (null == image && !"ico".equalsIgnoreCase(formatName) && !"icon".equalsIgnoreCase(formatName))
        {
            throw new ValidationException();
        }
    }
    
    private static Logger logger = LoggerFactory.getLogger(ImageCheckUtil.class);
    
}
