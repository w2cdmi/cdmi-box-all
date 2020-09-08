/**
 * @(#)VerifyCode.java
 *
 *
 * @author weixiang he
 * @version V400R001C09 2013/03/04
 */

package com.huawei.verifycode;

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
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.util.Constants;
import com.huawei.sharedrive.isystem.util.VerifyCodeData;

import pw.cdmi.core.utils.SpringContextUtil;

public final class VerifyCode extends HttpServlet
{
    private static List<String> fontsTypeList;
    
    private static int iCharsLen;
    
    private static int iCodeCount;
    
    private static int iDistance;
    
    private static int iDistort;
    
    private static int iHeight;
    
    private static int iMaxFontSize;
    
    private static int iMinFontSize = 30;
    
    private static int iWidth;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyCode.class);
    
    private static final double PAI = 3.1415926535897932384626433832799;
    
    private static SecureRandom random = new SecureRandom();
    
    private static final long serialVersionUID = -7292509578220207265L;
    
    private static VerifyCodeData verifyCodeData = (VerifyCodeData) SpringContextUtil.getBean("verifyCodeData");
    
    static
    {
        fontsTypeList = new ArrayList<String>(8);
        fontsTypeList.add("Arial");
        fontsTypeList.add("Courier");
        fontsTypeList.add("Courier New");
        fontsTypeList.add("Times New Roman");
        fontsTypeList.add("SansSerif");
        fontsTypeList.add("Monospaced");
        fontsTypeList.add("Verdana");
        fontsTypeList.add("Microsoft Sans Serif");
        fontsTypeList.add("Comic Sans MS");
    }
    
    private static int createRandom(int iSeed)
    {
        int liValue = 0;
        byte lbtTmp;
        byte[] lpbtRand = new byte[4];
        random.nextBytes(lpbtRand);
        for (int ii = 0; ii < 4; ii++)
        {
            lbtTmp = lpbtRand[ii];
            liValue += (lbtTmp & 0xFF) << (8 * ii);
        }
        return Math.abs(liValue % iSeed);
    }
    
    private static List<String> getFontTypeList()
    {
        return fontsTypeList;
    }
    
    private static Color getRandColor(int iFC, int iBC)
    {
        int liMax = Math.max(iFC, iBC);
        int liMin = Math.min(iFC, iBC);
        
        if (liMin == liMax)
        {
            return new Color(liMin, liMin, liMin);
        }
        
        liMax = liMax - liMin + 1;
        int x = liMin + createRandom(liMax);
        int y = liMin + createRandom(liMax);
        int z = liMin + createRandom(liMax);
        
        return new Color(x, y, z);
    }
    
    private static void setCharsLen(int input)
    {
        iCharsLen = input;
    }
    
    private static void setCodeCount(int input)
    {
        iCodeCount = input;
    }
    
    private static void setDistance(int input)
    {
        iDistance = input;
    }
    
    private static void setDistort(int input)
    {
        iDistort = input;
    }
    
    private static void setHeight(int input)
    {
        iHeight = input;
    }
    
    private double[] rotateRange = new double[]{-0.3, 0.3};
    
    public Font[] generateCodeFonts(String[] fontsType)
    {
        Font[] chosenFonts = new Font[iCharsLen];
        String sFont = null;
        int fontSize = -1;
        for (int i = 0; i < iCharsLen; i++)
        {
            sFont = fontsType[random.nextInt(3) % 4];
            fontSize = random.nextInt(iMaxFontSize) % (iMaxFontSize - iMinFontSize + 1) + iMinFontSize;
            chosenFonts[i] = new Font(sFont, Font.PLAIN, fontSize);
        }
        return chosenFonts;
    }
    
    public void init(ServletConfig config) throws ServletException
    {
        verifyCodeData.setbVariableFont(config.getInitParameter("bVariableFont"));
        verifyCodeData.setbVariableFontSize(config.getInitParameter("bVariableFontSize"));
        verifyCodeData.setDictionary(config.getInitParameter("dictionary"));
        verifyCodeData.setbIsRotate(config.getInitParameter("bIsRotate"));
        setDistance(Integer.parseInt(config.getInitParameter("iDistance")));
        verifyCodeData.setbIsSetBackground(config.getInitParameter("bIsSetBackground"));
        verifyCodeData.setbIsSetInterferon(config.getInitParameter("bIsSetInterferon"));
        setDistort(Integer.parseInt(config.getInitParameter("iDistort")));
        setCodeCount(Integer.parseInt(config.getInitParameter("iCodeCount")));
        if (config.getInitParameter("iHeight") != null)
        {
            setHeight(Integer.parseInt(config.getInitParameter("iHeight")));
            if (iHeight <= 0)
            {
                setHeight(35);
            }
        }
        else
        {
            setHeight(35);
        }
        
        if (verifyCodeData.getDictionary() == null)
        {
            verifyCodeData.setDictionary("0123456789");
        }
        
        if (iCodeCount == 0)
        {
            setCharsLen(4);
        }
        else
        {
            setCharsLen(iCodeCount);
        }
        
        if (iDistance == 0)
        {
            setDistance(1);
        }
        
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            processRequest(request, response);
        }
        catch (Exception e)
        {
            LOGGER.error("doGet fail", e);
        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            processRequest(request, response);
        }
        catch (Exception e)
        {
            LOGGER.error("doPost fail", e);
        }
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        HttpSession session = request.getSession();
        String sRand = "";
        ServletOutputStream responseOutputStream = response.getOutputStream();
        sRand = generateCodeString(verifyCodeData.getDictionary());
        BufferedImage image = renderWord(sRand);
        image = distortImage(image, iDistort);
        String index = request.getParameter("index");
        if (null == index || "".equals(index))
        {
            session.setAttribute(Constants.HW_VERIFY_CODE_CONST, sRand);
        }
        else
        {
             session.setAttribute(Constants.HW_VERIFY_CODE_CONST + index.charAt(0), sRand);
        }
        ImageIO.setUseCache(false);
        ImageIO.write(image, "JPEG", responseOutputStream);
        responseOutputStream.flush();
        responseOutputStream.close();
    }
    
    private BufferedImage distortImage(BufferedImage oldImage, int iMargin)
    {
        double dPhase = random.nextInt(6);
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
        double dLen = height;
        double x = 0;
        double dy = 0;
        int oX = 0;
        int oY = 0;
        int rgb = 0;
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                x = (PAI * j) / dLen;
                x += dPhase;
                dy = Math.sin(x);
                oX = i + ((Double) (dy * iMargin)).intValue();
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
    
    /**
     * 修改checkStyle Use "System.arraycopy ()" instead of using a loop to copy arrays.
     * 
     * @param dictionaryChars
     * @param codeChars
     * @param dicLen
     * @param i
     */
    private void doCopy(char[] dictionaryChars, char[] codeChars, int dicLen, int i)
    {
        codeChars[i] = dictionaryChars[random.nextInt(dicLen)];
    }
    
    private String generateCodeString(String dictionary)
    {
        if (iCodeCount < 0)
        {
            int randLen = random.nextInt(4);
            iCharsLen = 4 + randLen;
        }
        iWidth = ((Double) (iHeight * 3.0 * iCharsLen / 4)).intValue();
        iMaxFontSize = 16 * iHeight / 12;
        
        char[] dictionaryChars = dictionary.toCharArray();
        char[] codeChars = new char[iCharsLen];
        int dicLen = dictionaryChars.length;
        
        for (int i = 0; i < iCharsLen; i++)
        {
            doCopy(dictionaryChars, codeChars, dicLen, i);
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
    
    private float getFloatValue(double value)
    {
        return (float)value;
    }
    
    private double getRandomInRange(double[] range)
    {
        // 修改checkstyle MAJOR: Do not compare floating point types.[PB.DCF-2]
        int length = range.length;
        if (length != 2)
        {
            throw new BusinessException("range lenth lenth is not equals 2");
        }
        return Math.random() * (range[1] - range[0]) + range[0];
    }
    
    private int getRandomInRange(int[] range)
    {
        if (range == null || range.length != 2)
        {
            throw new BusinessException("range lenth is null or lenth is not equals 2");
        }
        return random.nextInt((range[1] - range[0]) + range[0]);
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
        double[] charWidths = new double[len];
        int fontSize = 0;
        double widthNeeded = 0;
        String sFont = getFontTypeList().get(0);
        fontSize = iHeight + 10;
        char[] charToDraw = null;
        GlyphVector gv = null;
        for (int i = 0; i < len; i++)
        {
            if ("true".equalsIgnoreCase(verifyCodeData.getbVariableFont()))
            {
                sFont = getFontTypeList().get(random.nextInt(3) % 4);
            }
            if ("true".equalsIgnoreCase(verifyCodeData.getbVariableFontSize()))
            {
                fontSize = random.nextInt(iMaxFontSize) % (iMaxFontSize - iMinFontSize + 1) + iMinFontSize;
            }
            chosenFonts[i] = new Font(sFont, Font.PLAIN, fontSize);
            charToDraw = new char[]{wordChars[i]};
            gv = chosenFonts[i].createGlyphVector(frc, charToDraw);
            charWidths[i] = (float)gv.getVisualBounds().getWidth();
            widthNeeded = widthNeeded + charWidths[i];
        }
        double startPosX = (iWidth - widthNeeded) / 5;
        double rotateX = 0;
        double startPosY = 0;
        Color color = null;
        AffineTransform affineTransform = null;
        for (int i = 0; i < wordChars.length; i++)
        {
            affineTransform = new AffineTransform();
            rotateX = 0;
            if ("true".equals(verifyCodeData.getbIsRotate()))
            {
                rotateX = getRandomInRange(rotateRange);
            }
            affineTransform.rotate(rotateX, 15, 15);
            g2D.setTransform(affineTransform);
            g2D.setFont(chosenFonts[i]);
            color = getRandColor(0, 120);
            g2D.setColor(color);
            startPosY = iHeight / 2.0f
                + chosenFonts[i].getLineMetrics(Character.toString(wordChars[i]), frc).getAscent() / 2.0f;
            g2D.drawString(Character.toString(wordChars[i]),
                getFloatValue(startPosX),
                getFloatValue(startPosY));
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
        int ii = 0;
        int liTmp = 0;
        int liTmp1 = 0;
        int liWidth = iWidth;
        int liHeight = iHeight;
        g2D.fillRect(0, 0, width, height);
        g2D.setColor(Color.white);
        g2D.drawRect(0, 0, width, height);
        if ("true".equals(verifyCodeData.getbIsSetBackground()))
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
        if ("true".equals(verifyCodeData.getbIsSetInterferon()))
        {
            liTmp = liWidth + 1;
            liTmp1 = liHeight + 1;
            int times1 = iHeight / 10;
            int times2 = iWidth / 10;
            BasicStroke bs = null;
            for (ii = 0; ii < times1; ii++)
            {
                bs = new BasicStroke((createRandom(30) + 1) / 16.0f);
                ((Graphics2D) g2D).setStroke(bs);
                g2D.setColor(getRandColor(50, 200));
                g2D.drawLine(0, createRandom(liTmp1), liWidth, createRandom(liTmp1));
            }
            for (ii = 0; ii < times2; ii++)
            {
                bs = new BasicStroke((createRandom(30) + 1) / 16.0f);
                ((Graphics2D) g2D).setStroke(bs);
                g2D.setColor(getRandColor(50, 200));
                g2D.drawLine(createRandom(liTmp), 0, createRandom(liTmp), liHeight);
            }
        }
        
        g2D.dispose();
        
        return image;
    }
}