/**
 * 
 */
package com.huawei.sharedrive.isystem.user.web;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.system.service.CustomizeLogoService;

import pw.cdmi.common.domain.CustomizeLogo;
import pw.cdmi.common.domain.CustomizeLogoVo;

/**
 * 
 * 
 * 系统Logo配置管理
 * 
 * @author d00199602
 * 
 */
@Controller
@RequestMapping(value = "/syscommon")
public class LogoLoadController extends AbstractCommonController
{
    
    private static Logger logger = LoggerFactory.getLogger(LogoLoadController.class);
    
    @Autowired
    private CustomizeLogoService customizeService;
    
    /**
     * 加载系统配置信息
     * 
     * @param
     * @return
     */
    @RequestMapping(value = "loadconfig", method = RequestMethod.GET)
    @ResponseBody
    public CustomizeLogoVo load(HttpServletResponse response)
    {
        response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");
        response.setHeader("Pragma", "no-cache");
        CustomizeLogo customize = customizeService.getCustomize();
        CustomizeLogoVo vo = new CustomizeLogoVo();
        if (customize.getTitle() == null)
        {
            vo.setTitle("");
            vo.setTitleEn("");
            vo.setCopyright("");
            vo.setCopyrightEn("");
        }
        else
        {
            vo.setTitle(customize.getTitle());
            vo.setTitleEn(customize.getTitleEn());
            vo.setCopyright(HtmlUtils.htmlEscape(customize.getCopyright()));
            vo.setCopyrightEn(HtmlUtils.htmlEscape(customize.getCopyrightEn()));
        }
        if (customize.getLogo() != null)
        {
            vo.setExistLogo(true);
        }
        if (customize.getIcon() != null)
        {
            vo.setExistIcon(true);
        }
        return vo;
    }
    
    /**
     * 获取LOGO图片
     * 
     * @param req
     * @param resp
     */
    @RequestMapping(value = "logo", method = RequestMethod.GET)
    public void getLogoImg(HttpServletRequest req, HttpServletResponse resp)
    {
        outputImage(resp, customizeService.getCustomize().getLogo(), "application/octet-stream");
    }
    
    private void outputImage(HttpServletResponse resp, byte[] data, String contentType)
    {
        OutputStream outputStream = null;
        try
        {
            if (data == null)
            {
                return;
            }
            resp.setContentType(contentType);
            // 不缓存此内容
            resp.setHeader("Pragma", "No-cache");
            resp.setHeader("Cache-Control", "no-cache");
            resp.setDateHeader("Expire", 0);
            outputStream = resp.getOutputStream();
            outputStream.write(data);
        }
        catch (Exception e)
        {
            logger.error("Error in output icon img!", e);
        }
        finally
        {
            try
            {
                if (outputStream != null)
                {
                    outputStream.close();
                }
            }
            catch (IOException e)
            {
                logger.error("Error in close icon img!", e);
            }
        }
        
    }
}
