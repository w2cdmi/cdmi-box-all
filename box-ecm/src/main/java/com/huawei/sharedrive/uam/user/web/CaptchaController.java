package com.huawei.sharedrive.uam.user.web;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.uam.util.CaptchaUtil;

@Controller
@RequestMapping(value = "/captcha")
public class CaptchaController
{
    
    public static final String KEY_CAPTCHA = "SE_KEY_MM_CODE";
    
    private final static Logger LOGGER = LoggerFactory.getLogger(CaptchaController.class);
    
    @RequestMapping(method = RequestMethod.GET)
    public void getCaptchaImg(HttpServletRequest req, HttpServletResponse resp)
    {
        resp.setContentType("image/jpeg");
        resp.setHeader("Pragma", "No-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expire", 0);
        try
        {
            Session session = SecurityUtils.getSubject().getSession();
            
            CaptchaUtil tool = new CaptchaUtil();
            StringBuilder code = new StringBuilder();
            BufferedImage image = tool.genRandomCodeImage(code);
            session.removeAttribute(KEY_CAPTCHA);
            session.setAttribute(KEY_CAPTCHA, code.toString());
            
            ImageIO.write(image, "JPEG", resp.getOutputStream());
        }
        catch (IOException e)
        {
            LOGGER.error("Error in output captcha img!", e);
        }
    }
}
