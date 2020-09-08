package pw.cdmi.box.uam.user.web;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.system.service.CustomizeLogoService;
import pw.cdmi.common.domain.CustomizeLogo;
import pw.cdmi.common.domain.CustomizeLogoVo;

@Controller
@RequestMapping(value = "/syscommon")
public class LogoLoadController extends AbstractCommonController
{
    
    private static Logger logger = LoggerFactory.getLogger(LogoLoadController.class);
    
    @Autowired
    private CustomizeLogoService customizeService;
    
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
        if (customize.getLogo() != null && customize.getLogo().length > 0)
        {
            vo.setExistLogo(true);
        }
        if (customize.getIcon() != null && customize.getIcon().length > 0)
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
    
    private void outputImage(HttpServletResponse resp, byte[] data, String contentType)
    {
        OutputStream outputStream = null;
        try
        {
            if (data == null || data.length == 0)
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
            logger.error("Error in output icon img!", e);
        }
        finally
        {
            IOUtils.closeQuietly(outputStream);
        }
    }
}
