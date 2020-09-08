package pw.cdmi.box.uam.authorize.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pw.cdmi.box.uam.adminlog.service.UserLogConfigService;
import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.exception.InvalidParamterException;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.core.log.LogConstants;

@Controller
@RequestMapping(value = "/sys/userconfig")
public class UserLogConfigController extends AbstractCommonController
{
    
    @Autowired
    private UserLogConfigService userLogConfigService;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String saveConfig(HttpServletRequest request, String token)
    {
        super.checkToken(token);
        String appId = request.getParameter("appId");
        String isConfigLog = request.getParameter("isConfigLog");
        String language = request.getParameter("language");
        if (!"zh".equals(language) && !"en".equals(language))
        {
            saveValidateLog(request, OperateType.AdminLogSet);
            throw new InvalidParamterException("invalid language value");
        }
        
        String logIsConfigLog = isConfigLog;
        if ("1".equals(logIsConfigLog))
        {
            logIsConfigLog = "ture";
        }
        else if ("0".equals(logIsConfigLog))
        {
            logIsConfigLog = "false";
        }
        else
        {
            saveValidateLog(request, OperateType.AdminLogSet);
            throw new InvalidParamterException("invalid logIsConfigLog value");
        }
        String[] description = new String[]{language, logIsConfigLog};
        String id = systemLogManager.saveFailLog(request,
            OperateType.AdminLogSet,
            OperateDescription.ADMIN_LOG_SET,
            null,
            description);
        
        if (StringUtils.isBlank(appId))
        {
            appId = LogConstants.DERAULT_APPID;
        }
        SystemConfig systemConfig = userLogConfigService.queryConfig(LogConstants.DERAULT_APPID,
            LogConstants.USER_LOG_ISCONFIG);
        SystemConfig systemLanguage = userLogConfigService.queryConfig(LogConstants.DERAULT_APPID,
            LogConstants.USER_LOG_CONFIG_LANGUAGE);
        if (systemConfig != null)
        {
            systemConfig.setAppId(appId);
            systemConfig.setValue(isConfigLog);
            userLogConfigService.updateConfig(systemConfig);
        }
        else
        {
            systemConfig = new SystemConfig();
            systemConfig.setAppId(appId);
            systemConfig.setId(LogConstants.DEFAULT_ISCONFIG);
            systemConfig.setValue(isConfigLog);
            userLogConfigService.saveConfig(systemConfig);
        }
        if (systemLanguage != null)
        {
            systemLanguage.setAppId(appId);
            systemLanguage.setValue(language);
            userLogConfigService.updateConfig(systemLanguage);
        }
        else
        {
            systemLanguage = new SystemConfig();
            systemLanguage.setAppId(appId);
            systemLanguage.setId(LogConstants.USER_LOG_CONFIG_LANGUAGE);
            systemLanguage.setValue(language);
            userLogConfigService.saveConfig(systemLanguage);
        }
        systemLogManager.updateSuccess(id);
        return "sys/logManage/userLogConfig";
    }
    
    @RequestMapping(value = "load", method = RequestMethod.GET)
    public String load(Model model, HttpServletRequest request)
    {
        String appId = request.getParameter("appId");
        SystemConfig systemConfig = userLogConfigService.queryConfig(LogConstants.DERAULT_APPID,
            LogConstants.USER_LOG_ISCONFIG);
        SystemConfig systemLanguage = userLogConfigService.queryConfig(LogConstants.DERAULT_APPID,
            LogConstants.USER_LOG_CONFIG_LANGUAGE);
        String isConfig;
        if (systemConfig == null || "".equals(StringUtils.trimToEmpty(systemConfig.getValue())))
        {
            isConfig = LogConstants.DEFAULT_ISCONFIG;
            systemConfig = new SystemConfig();
            if (StringUtils.isNotBlank(appId))
            {
                systemConfig.setAppId(appId);
            }
            else
            {
                systemConfig.setAppId(LogConstants.DERAULT_APPID);
            }
            systemConfig.setId(LogConstants.USER_LOG_ISCONFIG);
            systemConfig.setValue(LogConstants.DEFAULT_ISCONFIG);
            userLogConfigService.saveConfig(systemConfig);
        }
        else
        {
            isConfig = systemConfig.getValue();
        }
        String language;
        if (systemLanguage == null || "".equals(StringUtils.trimToEmpty(systemLanguage.getValue())))
        {
            language = LogConstants.DEFAULT_LANGUAGE;
            systemLanguage = new SystemConfig();
            if (StringUtils.isNotBlank(appId))
            {
                systemLanguage.setAppId(appId);
            }
            else
            {
                systemLanguage.setAppId(LogConstants.DERAULT_APPID);
            }
            systemLanguage.setId(LogConstants.USER_LOG_CONFIG_LANGUAGE);
            systemLanguage.setValue(LogConstants.DEFAULT_LANGUAGE);
            userLogConfigService.saveConfig(systemLanguage);
        }
        else
        {
            language = systemLanguage.getValue();
        }
        if (StringUtils.isBlank(isConfig))
        {
            isConfig = LogConstants.DEFAULT_ISCONFIG;
        }
        if (StringUtils.isBlank(language))
        {
            language = LogConstants.DEFAULT_LANGUAGE;
        }
        model.addAttribute("isConfig", isConfig);
        model.addAttribute("language", language);
        return "sys/logManage/userLogConfig";
    }
    
}
