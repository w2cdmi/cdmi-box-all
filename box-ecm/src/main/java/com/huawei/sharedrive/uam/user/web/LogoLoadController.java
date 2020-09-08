package com.huawei.sharedrive.uam.user.web;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterprise.domain.WebIconPcLogo;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseIndividualConfigManager;
import com.huawei.sharedrive.uam.system.service.CustomizeLogoService;

import pw.cdmi.common.domain.CustomizeLogo;
import pw.cdmi.common.domain.CustomizeLogoVo;

@Controller
@RequestMapping(value = "/syscommon")
public class LogoLoadController extends AbstractCommonController
{
    
    private final static Logger LOGGER = LoggerFactory.getLogger(LogoLoadController.class);
    
    @Autowired
    private CustomizeLogoService customizeService;
    
    @Autowired
    private EnterpriseIndividualConfigManager enterpriseIndividualConfigManager;
    
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
            vo.setTitle(HtmlUtils.htmlEscape(customize.getTitle()));
            vo.setTitleEn(HtmlUtils.htmlEscape(customize.getTitleEn()));
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
     * 
     * @param req
     * @param resp
     */
    @RequestMapping(value = "logo", method = RequestMethod.GET)
    public void getLogoImg(HttpServletRequest req, HttpServletResponse resp)
    {
        outputImage(resp, customizeService.getCustomize().getLogo(), "application/octet-stream");
    }
    
    /**
     * 
     * @param req
     * @param resp
     */
    @RequestMapping(value = "icon", method = RequestMethod.GET)
    public void getIconImg(HttpServletRequest req, HttpServletResponse resp)
    {
        outputImage(resp, customizeService.getCustomize().getIcon(), "image/x-icon");
    }
    
    /**
     * 
     * @param req
     * @param resp
     */
    @RequestMapping(value = "weblogo/{appId}", method = RequestMethod.GET)
    public void getWebLogoImg(@PathVariable String appId, HttpServletRequest req, HttpServletResponse resp)
    {
        WebIconPcLogo webIconPcLogo = new WebIconPcLogo();
        webIconPcLogo.setAccountId(getAccoutId(appId));
        outputImage(resp,
            enterpriseIndividualConfigManager.get(webIconPcLogo).getWebLogo(),
            "application/octet-stream");
    }
    
    /**
     * 
     * @param req
     * @param resp
     */
    @RequestMapping(value = "pclogo/{appId}", method = RequestMethod.GET)
    public void getPcLogoImg(@PathVariable String appId, HttpServletRequest req, HttpServletResponse resp)
    {
        WebIconPcLogo webIconPcLogo = new WebIconPcLogo();
        webIconPcLogo.setAccountId(getAccoutId(appId));
        outputImage(resp,
            enterpriseIndividualConfigManager.get(webIconPcLogo).getPcLogo(),
            "application/octet-stream");
    }
    
    /**
     * 
     * @param req
     * @param resp
     */
    @RequestMapping(value = "webicon/{appId}", method = RequestMethod.GET)
    public void getWebIconImg(@PathVariable String appId, HttpServletRequest req, HttpServletResponse resp)
    {
        WebIconPcLogo webIconPcLogo = new WebIconPcLogo();
        webIconPcLogo.setAccountId(getAccoutId(appId));
        outputImage(resp, enterpriseIndividualConfigManager.get(webIconPcLogo).getIcon(), "image/x-icon");
    }
    
    private void outputImage(HttpServletResponse resp, byte[] data, String contentType)
    {
        OutputStream outputStream = null;
        try
        {
            if (null == data || data.length == 0)
            {
                return;
            }
            resp.setContentType(contentType);
            resp.setHeader("Pragma", "No-cache");
            resp.setHeader("Cache-Control", "no-cache");
            resp.setDateHeader("Expire", 0);
            outputStream = resp.getOutputStream();
            outputStream.write(data);
        }
        catch (IOException e)
        {
            LOGGER.error("Error in output icon img!", e);
        }
        finally
        {
            IOUtils.closeQuietly(outputStream);
        }
    }
}
