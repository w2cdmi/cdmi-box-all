package pw.cdmi.box.disk.user.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.disk.enterpriseindividual.domain.WebIconPcLogo;
import pw.cdmi.box.disk.enterpriseindividual.manager.EnterpriseIndividualConfigManager;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.system.service.CustomizeLogoService;
import pw.cdmi.common.domain.CustomizeLogo;
import pw.cdmi.common.domain.CustomizeLogoVo;

@Controller
@RequestMapping(value = "/syscommon")
public class LogoLoadController
{
    @Autowired
    private CustomizeLogoService customizeService;
    
    @Autowired
    private EnterpriseIndividualConfigManager enterpriseIndividualConfigManager;
    
    /**
     * 
     * @param req
     * @param resp
     */
    @RequestMapping(value = "multlogo", method = RequestMethod.GET)
    public void getMultLogoImg(HttpServletRequest req, HttpServletResponse resp)
    {
//        CustomizeLogo customizeLogo = customizeService.getCustomize();
        CustomizeLogo customizeLogo = new CustomizeLogo();
        WebIconPcLogo webIconPcLogo = new WebIconPcLogo();
        UserToken sessUser = (UserToken) SecurityUtils.getSubject().getPrincipal();
        webIconPcLogo.setAccountId(sessUser.getAccountId());
        enterpriseIndividualConfigManager.getExitImageFile(customizeLogo, webIconPcLogo);
        ImageOuter.outputImage(resp, customizeLogo.getLogo(), "application/octet-stream");
    }
    
    /**
     * 
     * @param dc
     * @return
     */
    @RequestMapping(value = "loadconfig", method = RequestMethod.POST)
    @ResponseBody
    public CustomizeLogoVo load(HttpServletResponse response)
    {
        response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");
        response.setHeader("Pragma", "no-cache");
        WebIconPcLogo webIconPcLogo = new WebIconPcLogo();
        UserToken sessUser = (UserToken) SecurityUtils.getSubject().getPrincipal();
        CustomizeLogoVo vo = new CustomizeLogoVo();
        if (sessUser == null)
        {
            vo.setTitle("");
            vo.setTitleEn("");
            vo.setCopyright("");
            vo.setCopyrightEn("");
            return vo;
        }
        webIconPcLogo.setAccountId(sessUser.getAccountId());
        
//        CustomizeLogo customizeLogo = customizeService.getCustomize();
        CustomizeLogo customize = new CustomizeLogo();
        enterpriseIndividualConfigManager.getExitImageFile(customize, webIconPcLogo);
        
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
        if (customize.getIcon() != null && customize.getLogo().length > 0)
        {
            vo.setExistIcon(true);
        }
        return vo;
    }
}
