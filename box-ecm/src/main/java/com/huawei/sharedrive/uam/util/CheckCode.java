package com.huawei.sharedrive.uam.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckCode
{
    private int width = 102;
    
    private int height = 28;
    
    private int codeCount = 4;
    
    private static Logger logger = LoggerFactory.getLogger(CheckCode.class);
    
    private SecureRandom random = new SecureRandom();
    
    private BufferedImage buffImg;
    
    private String checkCodeStr;
    
    public CheckCode createCheckCode()
    {
        CheckCode checkCode = new CheckCode();
        checkCode.setCheckCodeStr(createRandomCode());
        checkCode.setBuffImg(disturb());
        return checkCode;
    }
    
    private String createRandomCode()
    {
        StringBuffer randomCode = new StringBuffer();
        int xx = width / (codeCount + 1);
        int codeY = height - 4;
        char[] codeSequence = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9'};
        Graphics2D graphics = graphicsInit();
        graphics.setColor(createColor());
        String strRand;
        for (int i = 0; i < codeCount; i++)
        {
            strRand = String.valueOf(codeSequence[random.nextInt(32)]);
            randomCode.append(strRand);
            
            graphics.drawString(strRand, (i + 1) * xx, codeY);
        }
        
        return randomCode.toString();
    }
    
    private Color createColor()
    {
        Color[] color = new Color[10];
        color[0] = new Color(113, 31, 71);
        color[1] = new Color(37, 0, 37);
        color[2] = new Color(111, 33, 36);
        color[3] = new Color(0, 0, 112);
        color[4] = new Color(14, 51, 16);
        color[5] = new Color(1, 1, 1);
        color[6] = new Color(72, 14, 73);
        color[7] = new Color(65, 67, 29);
        color[8] = new Color(116, 86, 88);
        color[9] = new Color(41, 75, 71);
        
        return color[random.nextInt(10)];
    }
    
    private Graphics2D graphicsInit()
    {
        Graphics2D graphics = buffImgInit().createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        graphics.setFont(new Font("Fixedsys", Font.BOLD, height - 2));
        graphics.drawRect(0, 0, width - 1, height - 1);
        return graphics;
    }
    
    private BufferedImage buffImgInit()
    {
        buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        return buffImg;
    }
    
    private BufferedImage disturb()
    {
        drawDisturbLine(buffImg.createGraphics());
        return twistImage();
    }
    
    private void drawDisturbLine(Graphics2D graphics)
    {
        SecureRandom random = new SecureRandom();
        graphics.setColor(Color.BLACK);
        int x = 0;
        int y = 0;
        int xl = 0;
        int yl = 0;
        for (int i = 0; i < 15; i++)
        {
            x = random.nextInt(width);
            y = random.nextInt(height);
            xl = random.nextInt(20);
            yl = random.nextInt(10);
            graphics.drawLine(x, y, x + xl, y + yl);
        }
    }
    
    private BufferedImage twistImage()
    {
        SecureRandom random = new SecureRandom();
        double dMultValue = (int) random.nextDouble() * (7) + 3;
        double dPhase = (int) random.nextDouble() * (6);
        
        BufferedImage destBi = new BufferedImage(buffImg.getWidth(), buffImg.getHeight(),
            BufferedImage.TYPE_INT_RGB);
        
        int nOldX;
        int nOldY;
        for (int i = 0; i < destBi.getWidth(); i++)
        {
            for (int j = 0; j < destBi.getHeight(); j++)
            {
                nOldX = getXPosition4Twist(dPhase, dMultValue, destBi.getHeight(), i, j);
                nOldY = j;
                if (nOldX >= 0 && nOldX < destBi.getWidth() && nOldY >= 0 && nOldY < destBi.getHeight())
                {
                    destBi.setRGB(nOldX, nOldY, buffImg.getRGB(i, j));
                }
            }
        }
        return destBi;
    }
    
    private int getXPosition4Twist(double dPhase, double dMultValue, int height, int xPosition, int yPosition)
    {
        double pi = 4;
        double dx = pi * yPosition / height + dPhase;
        double dy = Math.sin(dx);
        return xPosition + (int) (dy * dMultValue);
    }
    
    public String createImgFile(String pathName)
    {
        File file = new File(pathName);
        if (file.exists() && file.isFile())
        {
            if (!file.delete())
            {
                logger.error("File delete fail:" + pathName);
            }
        }
        try
        {
            ImageIO.write(buffImg, "jpeg", file);
        }
        catch (IOException e)
        {
            logger.error("File write fail:" + pathName, e);
        }
        return pathName;
    }
    
    public BufferedImage getBuffImg()
    {
        return buffImg;
    }
    
    public void setBuffImg(BufferedImage buffImg)
    {
        this.buffImg = buffImg;
    }
    
    public String getCheckCodeStr()
    {
        return checkCodeStr;
    }
    
    public void setCheckCodeStr(String checkCodeStr)
    {
        this.checkCodeStr = checkCodeStr;
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public void setWidth(int width)
    {
        this.width = width;
    }
    
    public int getHeight()
    {
        return height;
    }
    
    public void setHeight(int height)
    {
        this.height = height;
    }
    
    public int getCodeCount()
    {
        return codeCount;
    }
    
    public void setCodeCount(int codeCount)
    {
        this.codeCount = codeCount;
    }
    
}