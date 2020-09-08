package pw.cdmi.box.disk.system.service;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.SecureRandom;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.box.disk.utils.PropertiesUtils;
import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.core.exception.InternalServerErrorException;
import pw.cdmi.core.utils.CacheParameterUtils;

public class VerifyCodeHelper
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheClient.class);
    
    public static final int VERIFYCODE_CACHE_TIME_OUT = Integer.parseInt(PropertiesUtils.getProperty("verifycode.cache.timeout",
        "300000"));
    
    private static final String[] FONTS_TYPE = new String[]{"Arial", "Courier", "Courier New",
        "Times New Roman", "SansSerif", "Monospaced", "Verdana", "Microsoft Sans Serif", "Comic Sans MS"};
    
    private static final double ROTATE_RANGE_END = 0.3;
    
    private static final double ROTATE_RANGE_START = -0.3;
    
    private static final int IMINFONTSIZE = 30;
    
    private final static double PAI = 3.1415926535897932384626433832799;
    
    private CacheClient cacheClient;
    
    private String bIsRotate;
    
    private String bIsSetBackground;
    
    private String bIsSetInterferon;
    
    private String bVariableFont;
    
    private String bVariableFontSize;
    
    private String dictionary;
    
    private int iCodeCount;
    
    private int iDistance;
    
    private int iDistort;
    
    private int iHeight;
    
    private int iCharsLen;
    
    private int iMaxFontSize;
    
    private int iWidth;
    
    private SecureRandom random = new SecureRandom();
    
    private int createRandom(int iSeed)
    {
        int lIValue = 0;
        byte lBtTmp;
        
        byte[] lPbtRand = new byte[4];
        
        random.nextBytes(lPbtRand);
        
        for (int ii = 0; ii < 4; ii++)
        {
            lBtTmp = lPbtRand[ii];
            lIValue += (lBtTmp & 0xFF) << (8 * ii);
        }
        
        return Math.abs(lIValue % iSeed);
    }
    
    private Color getRandColor(int iFC, int iBC)
    {
        int lIMax = Math.max(iFC, iBC);
        int lIMin = Math.min(iFC, iBC);
        
        if (lIMin == lIMax)
        {
            return new Color(lIMin, lIMin, lIMin);
        }
        
        lIMax -= lIMin - 1;
        
        return new Color(lIMin + createRandom(lIMax), lIMin + createRandom(lIMax), lIMin
            + createRandom(lIMax));
    }
    
    public Font[] generateCodeFonts(String[] fontsType)
    {
        Font[] chosenFonts = new Font[iCharsLen];
        int fontSize = -1;
        String sFont = null;
        Font font = null;
        for (int i = 0; i < iCharsLen; i++)
        {
            sFont = fontsType[random.nextInt(3) % 4];
            fontSize = random.nextInt(iMaxFontSize) % (iMaxFontSize - IMINFONTSIZE + 1) + IMINFONTSIZE;
            font = new Font(sFont, Font.PLAIN, fontSize);
            chosenFonts[i] = font;
        }
        
        return chosenFonts;
    }
    
    public void init()
    {
        if (dictionary == null)
        {
            dictionary = "0123456789";
        }
        
        if (iCodeCount == 0)
        {
            iCharsLen = 4;
        }
        else
        {
            iCharsLen = iCodeCount;
        }
        
        if (iDistance == 0)
        {
            iDistance = 1;
        }
        
    }
    
    public void setbIsRotate(String bIsRotate)
    {
        this.bIsRotate = bIsRotate;
    }
    
    public void setbIsSetBackground(String bIsSetBackground)
    {
        this.bIsSetBackground = bIsSetBackground;
    }
    
    public void setbIsSetInterferon(String bIsSetInterferon)
    {
        this.bIsSetInterferon = bIsSetInterferon;
    }
    
    public void setbVariableFont(String bVariableFont)
    {
        this.bVariableFont = bVariableFont;
    }
    
    public void setbVariableFontSize(String bVariableFontSize)
    {
        this.bVariableFontSize = bVariableFontSize;
    }
    
    public void setDictionary(String dictionary)
    {
        this.dictionary = dictionary;
    }
    
    public void setiCodeCount(int iCodeCount)
    {
        this.iCodeCount = iCodeCount;
    }
    
    public void setiDistance(int iDistance)
    {
        this.iDistance = iDistance;
    }
    
    public void setiDistort(int iDistort)
    {
        this.iDistort = iDistort;
    }
    
    public void setiHeight(int iHeight)
    {
        this.iHeight = iHeight;
    }
    
    public void processRequest(HttpServletRequest request, HttpServletResponse response, String uuid)
        throws IOException
    {
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        ServletOutputStream responseOutputStream = null;
        try
        {
            responseOutputStream = response.getOutputStream();
            
            String sRand = generateCodeString(dictionary);
            
            BufferedImage image = renderWord(sRand);
            
            image = distortImage(image, iDistort);
            
            if (!cacheClient.deleteCache(CacheParameterUtils.VERIFYCODE_CODE_PREFIX + uuid))
            {
                LOGGER.debug("Cache is not exist or expire");
            }
            cacheClient.setCache(CacheParameterUtils.VERIFYCODE_CODE_PREFIX + uuid,
                sRand,
                VERIFYCODE_CACHE_TIME_OUT);
            
            ImageIO.setUseCache(false);
            ImageIO.write(image, "JPEG", responseOutputStream);
            responseOutputStream.flush();
        }
        finally
        {
            IOUtils.closeQuietly(responseOutputStream);
        }
        
    }
    
    private BufferedImage distortImage(BufferedImage oldImage, int iMargin)
    {
        double tempPhase = random.nextInt(6);
        BufferedImage newImage = new BufferedImage(oldImage.getWidth(), oldImage.getHeight(),
            BufferedImage.TYPE_INT_RGB);
        int width = newImage.getWidth();
        int height = newImage.getHeight();
        
        Graphics graphics = newImage.getGraphics();
        
        graphics.setColor(Color.white);
        graphics.fillRect(0, 0, width, height);
        
        graphics.dispose();
        
        if (iMargin > 4)
        {
            iMargin = random.nextInt(iMargin) % (iMargin - 4) + 5;
        }
        
        double dLen = (double) height;
        
        double x = 0;
        double dy = 0;
        int oX = 0;
        int oY = 0;
        int rgb = 0;
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                x = (PAI * (double) j) / dLen;
                x += tempPhase;
                dy = Math.sin(x);
                
                oX = i + (int) (dy * iMargin);
                oY = j;
                rgb = oldImage.getRGB(i, j);
                if (oX >= 0 && oX < width && oY >= 0 && oY < height)
                {
                    newImage.setRGB(oX, oY, rgb);
                }
            }
        }
        
        return newImage;
    }
    
    private String generateCodeString(String dictionary)
    {
        if (iCodeCount < 0)
        {
            int randLen = random.nextInt(4);
            iCharsLen = 4 + randLen;
        }
        
        iWidth = (int) (iHeight * 3.0 * iCharsLen / 4);
        iMaxFontSize = 16 * iHeight / 12;
        
        char[] dictionaryChars = dictionary.toCharArray();
        char[] codeChars = new char[iCharsLen];
        int dicLen = dictionaryChars.length;
        
        int index = 0;
        for (int i = 0; i < iCharsLen; i++)
        {
            index = random.nextInt(dicLen);
            System.arraycopy(dictionaryChars, index, codeChars, i, 1);
        }
        
        return String.valueOf(codeChars, 0, codeChars.length);
    }
    
    private Color getColor(int[] colorRange)
    {
        int r = getRandomInRange(colorRange);
        int g = getRandomInRange(colorRange);
        int b = getRandomInRange(colorRange);
        return new Color(r, g, b);
    }
    
    private double getRandomInRange()
    {
        return new SecureRandom().nextDouble() * (ROTATE_RANGE_END - ROTATE_RANGE_START) + ROTATE_RANGE_START;
    }
    
    private int getRandomInRange(int[] range)
    {
        if (range == null || range.length != 2)
        {
            throw new InternalServerErrorException("getRandomInRange failed.");
        }
        return (int) (new SecureRandom().nextDouble() * (range[1] - range[0]) + range[0]);
    }
    
    private BufferedImage renderWord(String word)
    {
        int charSpace = iDistance - 2;
        
        BufferedImage image = new BufferedImage(iWidth, iHeight, BufferedImage.TYPE_INT_ARGB);
        setBackground(image);
        Graphics2D g2D = image.createGraphics();
        g2D.getDeviceConfiguration().createCompatibleImage(iHeight, iHeight, Transparency.TRANSLUCENT);
        g2D.dispose();
        
        g2D = image.createGraphics();
        
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        hints.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2D.setRenderingHints(hints);
        
        FontRenderContext frc = g2D.getFontRenderContext();
        char[] wordChars = word.toCharArray();
        int len = wordChars.length;
        Font[] chosenFonts = new Font[len];
        int[] charWidths = new int[len];
        int fontSize = 0;
        int widthNeeded = 0;
        String sFont = FONTS_TYPE[0];
        fontSize = iHeight + 10;
        
        char[] charToDraw = null;
        GlyphVector gv = null;
        
        for (int i = 0; i < len; i++)
        {
            if ("true".equalsIgnoreCase(bVariableFont))
            {
                sFont = FONTS_TYPE[random.nextInt(3) % 4];
            }
            
            if ("true".equalsIgnoreCase(bVariableFontSize))
            {
                fontSize = random.nextInt(iMaxFontSize) % (iMaxFontSize - IMINFONTSIZE + 1) + IMINFONTSIZE;
            }
            
            chosenFonts[i] = new Font(sFont, Font.PLAIN, fontSize);
            
            charToDraw = new char[]{wordChars[i]};
            gv = chosenFonts[i].createGlyphVector(frc, charToDraw);
            charWidths[i] = (int) gv.getVisualBounds().getWidth();
            widthNeeded = widthNeeded + charWidths[i];
        }
        
        int startPosX = (iWidth - widthNeeded) / 5;
        
        AffineTransform affineTransform = null;
        Color color = null;
        double rotateX;
        int startPosY = 0;
        for (int i = 0; i < wordChars.length; i++)
        {
            affineTransform = new AffineTransform();
            
            rotateX = 0D;
            
            if ("true".equals(bIsRotate))
            {
                rotateX = getRandomInRange();
            }
            
            affineTransform.rotate(rotateX, 15, 15);
            g2D.setTransform(affineTransform);
            
            g2D.setFont(chosenFonts[i]);
            
            color = getRandColor(0, 120);
            g2D.setColor(color);
            
            startPosY = (int) (iHeight / 2.0f + chosenFonts[i].getLineMetrics(Character.toString(wordChars[i]),
                frc)
                .getAscent() / 2.0f);
            g2D.drawString(Character.toString(wordChars[i]), startPosX, startPosY);
            startPosX = startPosX + charWidths[i] + charSpace;
        }
        
        g2D.dispose();
        return image;
    }
    
    private BufferedImage setBackground(BufferedImage image)
    {
        Graphics2D g2D = image.createGraphics();
        int width = iWidth + 3 * iDistance;
        int height = iHeight;
        
        int ii;
        int liTmp0 = 0;
        int liTmp1 = 0;
        int lIWidth = iWidth;
        int lIHeight = iHeight;
        
        g2D.fillRect(0, 0, width, height);
        
        g2D.setColor(Color.white);
        g2D.drawRect(0, 0, width, height);
        
        if ("true".equals(bIsSetBackground))
        {
            int[] disturbColor = {0, 255};
            
            int xs = 0;
            int ys = 0;
            int xe = 0;
            int ye = 0;
            Color fgColor = null;
            for (int i = 0; i < 150; i++)
            {
                xs = random.nextInt(width);
                ys = random.nextInt(height);
                xe = xs;
                ye = ys;
                fgColor = getColor(disturbColor);
                g2D.setColor(fgColor);
                g2D.drawLine(xs, ys, xe, ye);
            }
            
            disturbColor = null;
        }
        
        if ("true".equals(bIsSetInterferon))
        {
            liTmp0 = lIWidth + 1;
            liTmp1 = lIHeight + 1;
            
            int times1 = iHeight / 10;
            int times2 = iWidth / 10;
            
            BasicStroke basicStroke = null;
            for (ii = 0; ii < times1; ii++)
            {
                basicStroke = new BasicStroke((float) ((double) (createRandom(30) + 1) / 16.0));
                g2D.setStroke(basicStroke);
                
                g2D.setColor(getRandColor(50, 200));
                
                g2D.drawLine(0, createRandom(liTmp1), lIWidth, createRandom(liTmp1));
            }
            
            for (ii = 0; ii < times2; ii++)
            {
                basicStroke = new BasicStroke((float) ((double) (createRandom(30) + 1) / 16.0));
                g2D.setStroke(basicStroke);
                
                g2D.setColor(getRandColor(50, 200));
                
                g2D.drawLine(createRandom(liTmp0), 0, createRandom(liTmp0), lIHeight);
            }
        }
        
        g2D.dispose();
        
        return image;
    }
    
    public CacheClient getCacheClient()
    {
        return cacheClient;
    }
    
    public void setCacheClient(CacheClient cacheClient)
    {
        this.cacheClient = cacheClient;
    }
    
    public void setUserName(String uuid, String loginName)
    {
        if (!cacheClient.deleteCache(CacheParameterUtils.VERIFYCODE_USER_PREFIX + uuid))
        {
            LOGGER.debug("Cache is not exist or expire");
        }
        cacheClient.setCache(CacheParameterUtils.VERIFYCODE_USER_PREFIX + uuid, loginName);
    }
    
    public String getUserName(String uuid)
    {
        return (String) cacheClient.getCache(CacheParameterUtils.VERIFYCODE_USER_PREFIX + uuid);
    }
    
    public boolean isUserNameValid(String uuid, String loginName)
    {
        String value = (String) cacheClient.getCache(CacheParameterUtils.VERIFYCODE_USER_PREFIX + uuid);
        return StringUtils.equals(loginName, value);
    }
    
    public boolean isVeryCodeValid(String uuid, String veryCode)
    {
        String value = (String) cacheClient.getCache(CacheParameterUtils.VERIFYCODE_CODE_PREFIX + uuid);
        return StringUtils.equalsIgnoreCase(veryCode, value);
    }
    
    public void deleteVeryCodeCache(String uuid)
    {
        cacheClient.deleteCache(CacheParameterUtils.VERIFYCODE_CODE_PREFIX + uuid);
        cacheClient.deleteCache(CacheParameterUtils.VERIFYCODE_USER_PREFIX + uuid);
    }
    
}