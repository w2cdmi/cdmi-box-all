package pw.cdmi.box.uam.authapp.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.box.uam.authapp.service.AuthAppService;
import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.system.domain.MailServer;
import pw.cdmi.box.uam.system.service.MailServerService;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.box.uam.util.FormValidateUtil;
import pw.cdmi.box.uam.util.PropertiesUtils;
import pw.cdmi.uam.domain.AuthApp;

@Controller
@RequestMapping(value = "/app/appmanage/authapp")
public class AuthAppController extends AbstractCommonController
{
    private static final Boolean CUSTOM_UPLOAD_NETWORK = Boolean.parseBoolean(PropertiesUtils.getProperty("custom.upload.network",
        "false"));
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private MailServerService mailService;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @RequestMapping(method = RequestMethod.GET)
    public String enter()
    {
        return "app/appManageMain";
    }
    
    @RequestMapping(value = "list", method = {RequestMethod.GET})
    public String list(Model model)
    {
        model.addAttribute("customUploadNetwork", CUSTOM_UPLOAD_NETWORK);
        model.addAttribute("authAppList", authAppService.getAuthAppList(null, null, null));
        return "app/appList";
    }
    
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String enterCreate(Model model)
    {
        List<MailServer> mailServerList = mailService.getMailServerList(null, null, null);
        model.addAttribute("mailServerList", mailServerList);
        return "app/createApp";
    }
    
    /**
     * 
     * @param admin
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> create(HttpServletRequest request, AuthApp authApp, String token)
    {
        super.checkToken(token);
        if (StringUtils.isBlank(authApp.getAuthAppId()))
        {
            throw new ConstraintViolationException(null);
        }
        if (authApp.getAuthAppId().length() > 60)
        {
            throw new ConstraintViolationException(null);
        }
        AuthApp defaultApp = authAppService.getDefaultWebApp();
        if (defaultApp != null && authApp.getType() == Constants.DEFAULT_WEB_APP_TYPE)
        {
            return new ResponseEntity<String>("InvalidParamException", HttpStatus.BAD_REQUEST);
        }
        String[] description = new String[]{authApp.getAuthAppId(), authApp.getUfmAccessKeyId(),
            String.valueOf(authApp.getType()), authApp.getDescription()};
        validateParam(authApp);
        String id = systemLogManager.saveFailLog(request,
            OperateType.CreateAppAuth,
            OperateDescription.ADD_APP,
            null,
            description);
        authAppService.create(authApp);
        systemLogManager.updateSuccess(id);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    private void validateParam(AuthApp authApp)
    {
        if (!FormValidateUtil.isValidAppName(authApp.getAuthAppId()))
        {
            throw new ConstraintViolationException(null);
        }
        
        if (!FormValidateUtil.isValidPluginKey(authApp.getUfmAccessKeyId()))
        {
            throw new ConstraintViolationException(null);
        }
        if (!FormValidateUtil.isValidPluginKey(authApp.getUfmSecretKey()))
        {
            throw new ConstraintViolationException(null);
        }
        if (authApp.getDescription() != null && authApp.getDescription().length() > 255)
        {
            throw new ConstraintViolationException(null);
        }
        if (authApp.getType() < Constants.DEFAULT_WEB_APP_TYPE)
        {
            throw new ConstraintViolationException(null);
        }
    }
    
    /**
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "modify", method = RequestMethod.GET)
    public String enterModify(String authAppId, Model model)
    {
        List<MailServer> mailServerList = mailService.getMailServerList(null, null, null);
        model.addAttribute("mailServerList", mailServerList);
        AuthApp autapp = authAppService.getByAuthAppID(authAppId);
        autapp.setUfmSecretKey(Constants.DISPLAY_STAR_VALUE);
        model.addAttribute("authApp", autapp);
        return "app/modifyApp";
    }
    
    /**
     * 
     * @param admin
     * @return
     */
    @RequestMapping(value = "modify", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> modify(HttpServletRequest request, AuthApp authApp, String token)
    {
        super.checkToken(token);
        validateParam(authApp);
        String[] description = new String[]{authApp.getAuthAppId(), authApp.getUfmAccessKeyId(),
            authApp.getDescription()};
        String id = systemLogManager.saveFailLog(request,
            OperateType.UpdateAppAuth,
            OperateDescription.SYS_APP_UPDATE,
            null,
            description);
        authAppService.updateAuthApp(authApp);
        systemLogManager.updateSuccess(id);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
}
