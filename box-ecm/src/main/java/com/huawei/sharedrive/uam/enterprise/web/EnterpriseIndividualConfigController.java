package com.huawei.sharedrive.uam.enterprise.web;

import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterprise.domain.WebIconPcLogo;
import com.huawei.sharedrive.uam.enterprise.domain.WebIconPcLogoVo;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseIndividualConfigManager;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.exception.ImageInvalidException;
import com.huawei.sharedrive.uam.exception.ImageScaleException;
import com.huawei.sharedrive.uam.exception.ImageSizeException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.user.domain.Admin;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.HtmlUtils;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.core.utils.IpUtils;
import pw.cdmi.uam.domain.AuthApp;

import java.util.Locale;

@Controller
@RequestMapping(value = "/enterprise/admin/individual/config")
public class EnterpriseIndividualConfigController extends AbstractCommonController
{
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private EnterpriseIndividualConfigManager enterpriseIndividualConfigManager;
    
    @Autowired
    private AdminLogManager adminLogManager;
    
    @Autowired
    private EnterpriseAccountManager enterpriseAccountManager;
    
    @RequestMapping(value = "/versionInfo", method = RequestMethod.GET)
    public String load( Model model)
    {
    	Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
    	AuthApp  authApp =authAppService.getDefaultWebApp();
        String appId = HtmlUtils.htmlEscape(authApp.getAuthAppId());
        permissionCheck(appId);
        fillParam(model, appId);
        return "enterprise/admin/version/version_info";
    }
    
    /**
     * 
     * @param webIconPcLogo
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.POST)
    public String save(MultipartHttpServletRequest request, WebIconPcLogo webIconPcLogo, Model model,
        String token, String appId)
    {
        appId = HtmlUtils.htmlEscape(appId);
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setAppId(appId);
        owner.setIp(IpUtils.getClientAddress(request));
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        owner.setLoginName(admin.getLoginName());
        permissionCheck(appId);
        
        super.checkToken(token);
        
        webIconPcLogo.setAccountId(getAccoutId(appId));
        String[] description = null;
        description = enterpriseIndividualConfigManager.getDescription(request, webIconPcLogo);
        
        boolean flag = true;
        try
        {
            flag = enterpriseIndividualConfigManager.fileToByte(request, webIconPcLogo);
            if (flag)
            {
                enterpriseIndividualConfigManager.create(webIconPcLogo);
                model.addAttribute("saveState", "success");
                adminLogManager.saveAdminLog(owner, AdminLogType.KEY_INDIVIDUAL_CONFIG, description);
            }
        }
        catch (ImageSizeException e)
        {
            model.addAttribute("whichOne", e.getMessage());
            model.addAttribute("saveState", "ImageSizeException");
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_INDIVIDUAL_CONFIG_ERROR, description);
        }
        catch (ImageInvalidException e)
        {
            model.addAttribute("whichOne", e.getMessage());
            model.addAttribute("saveState", "ImageInvalidException");
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_INDIVIDUAL_CONFIG_ERROR, description);
        }
        catch (ImageScaleException e)
        {
            model.addAttribute("whichOne", e.getMessage());
            model.addAttribute("saveState", "ImageScaleException");
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_INDIVIDUAL_CONFIG_ERROR, description);
        }
        catch (Exception e)
        {
            model.addAttribute("saveState", "fail");
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_INDIVIDUAL_CONFIG_ERROR, description);
        }
        
        fillParam(model, appId);
        return "enterprise/admin/version/version_info";
    }
    
    
    /**
     * 保持版本信息
     * @param webIconPcLogo
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/saveCorpright",method = RequestMethod.POST)
    public String saveCorpright(MultipartHttpServletRequest request, WebIconPcLogo webIconPcLogo, Model model,
        String token, String CappId)
    {
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setAppId(CappId);
        owner.setIp(IpUtils.getClientAddress(request));
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        owner.setLoginName(admin.getLoginName());
        permissionCheck(CappId);
        
        super.checkToken(token);
        
        webIconPcLogo.setAccountId(getAccoutId(CappId));
        enterpriseIndividualConfigManager.updateCorpright(webIconPcLogo);
        
       
        fillParam(model, CappId);
        return "enterprise/admin/version/version_info";
    }
    
    
    /**
     * 保持版本信息
     * @param webIconPcLogo
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/getCorpright",method = RequestMethod.POST)
    @ResponseBody
    public String getCorpright(String token, String appId, String lagauge) {
        LogOwner owner = new LogOwner();
        permissionCheck(appId);

        super.checkToken(token);

        WebIconPcLogo webIconPcLogo = new WebIconPcLogo();
        webIconPcLogo.setAccountId(getAccoutId(appId));
        WebIconPcLogo corpright = enterpriseIndividualConfigManager.get(webIconPcLogo);
        if (corpright != null) {
            if (lagauge.trim().equals("zh")) {
                return corpright.getCorprightCN();
            } else if (lagauge.equals("en")) {
                return corpright.getCorprightEN();
            }
        }

        return "";
    }

    @Override
    protected long getAccoutId(String appId)
    {
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        long enterpriseId = sessAdmin.getEnterpriseId();
        EnterpriseAccount enterprise = enterpriseAccountManager.getByEnterpriseApp(enterpriseId, appId);
        if (null == enterprise)
        {
            throw new InvalidParamterException("enterprise is null");
        }
        long accountId = enterprise.getAccountId();
        return accountId;
    }
    
    private void fillParam(Model model, String appId)
    {
        model.addAttribute("appId", appId);
        model.addAttribute("appType", authAppService.getByAuthAppID(appId).getType());
        WebIconPcLogo webIconPcLogo = new WebIconPcLogo();
        webIconPcLogo.setAccountId(getAccoutId(appId));
        int countId = enterpriseIndividualConfigManager.getAccountId(webIconPcLogo);
        if (countId > 0)
        {
            WebIconPcLogo iconPcLogo = enterpriseIndividualConfigManager.get(webIconPcLogo);
            model.addAttribute("customize", convertToVo(iconPcLogo, appId));
        }
        else
        {
            model.addAttribute("customize", convertToVo(null, appId));
        }
    }
    
    private WebIconPcLogoVo convertToVo(WebIconPcLogo webIconPcLogo, String appId)
    {
        WebIconPcLogoVo vo = new WebIconPcLogoVo();
        if (webIconPcLogo == null)
        {
            vo.setTitleCh(messageSource.getMessage(appId, null, Locale.CHINA));
            vo.setTitleEn(messageSource.getMessage(appId, null, Locale.ENGLISH));
            return vo;
        }
        if (webIconPcLogo.getTitleCh() == null)
        {
            vo.setTitleCh(messageSource.getMessage(appId, null, Locale.CHINA));
            vo.setTitleEn(messageSource.getMessage(appId, null, Locale.ENGLISH));
        }
        else
        {
            vo.setTitleCh(webIconPcLogo.getTitleCh());
            vo.setTitleEn(webIconPcLogo.getTitleEn());
        }
        if (webIconPcLogo.getCorprightCN() != null){
        	vo.setCorprightCN(webIconPcLogo.getCorprightCN());
        }
        if (webIconPcLogo.getCorprightEN() != null){
        	vo.setCorprightEN(webIconPcLogo.getCorprightEN());
        }
        
        if (webIconPcLogo.getWebLogo().length > 0)
        {
            vo.setExistWebLogo(true);
        }
        if (webIconPcLogo.getPcLogo().length > 0)
        {
            vo.setExistPcLogo(true);
        }
        if (webIconPcLogo.getIcon().length > 0)
        {
            vo.setExistIcon(true);
        }
        return vo;
    }
    
}
