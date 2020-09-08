package pw.cdmi.box.uam.authorize.web;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.box.uam.authapp.service.AuthAppService;
import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.box.uam.util.PropertiesUtils;
import pw.cdmi.uam.domain.AuthApp;

@Controller
@RequestMapping(value = "/sys/appmanage/authapp")
public class SysAuthAppController extends AbstractCommonController
{
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    private static final int AUTH_APP_NUM = Integer.parseInt(PropertiesUtils.getProperty("authapp.number.limit"));
    
    /**
     * 
     * @param admin
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> create(AuthApp authApp, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        AuthApp defaultApp = authAppService.getDefaultWebApp();
        if (defaultApp != null && authApp.getType() == Constants.DEFAULT_WEB_APP_TYPE)
        {
            saveValidateLog(request, OperateType.CreateAppAuth);
            return new ResponseEntity<String>("InvalidParamException", HttpStatus.BAD_REQUEST);
        }
        int existAuthAppNum = authAppService.getAuthAppNum();
        if (existAuthAppNum >= AUTH_APP_NUM)
        {
            saveValidateLog(request, OperateType.CreateAppAuth);
            return new ResponseEntity<String>("MethodNotAllowedException", HttpStatus.BAD_REQUEST);
        }
        try
        {
            validateParam(authApp);
        }
        catch (ConstraintViolationException e)
        {
            saveValidateLog(request, OperateType.CreateAppAuth);
            throw e;
        }
        String[] description = new String[]{authApp.getAuthAppId(), authApp.getUfmAccessKeyId(),
            String.valueOf(authApp.getType()), authApp.getDescription()};
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
        checkUfmAccessKeyId(authApp);
        
        checkUfmSecretKey(authApp);
        
        checkAuthAppIdRule(authApp);
        
        if (authApp.getType() != Constants.DEFAULT_WEB_APP_TYPE
            && authApp.getType() != Constants.OTHER_APP_TYPE)
        {
            throw new ConstraintViolationException(null);
        }
    }
    
    private void checkUfmAccessKeyId(AuthApp authApp)
    {
        if (StringUtils.isBlank(authApp.getUfmAccessKeyId()))
        {
            throw new ConstraintViolationException(null);
        }
        if (authApp.getUfmAccessKeyId().length() > 60)
        {
            throw new ConstraintViolationException(null);
        }
        if (!authApp.getUfmAccessKeyId().matches("^[a-zA-Z0-9]+$"))
        {
            throw new ConstraintViolationException(null);
        }
    }
    
    private void checkUfmSecretKey(AuthApp authApp)
    {
        if (StringUtils.isBlank(authApp.getUfmSecretKey()))
        {
            throw new ConstraintViolationException(null);
        }
        if (authApp.getUfmSecretKey().length() > 60)
        {
            throw new ConstraintViolationException(null);
        }
        if (!authApp.getUfmSecretKey().matches("^[a-zA-Z0-9]+$"))
        {
            throw new ConstraintViolationException(null);
        }
        
        if (StringUtils.isNotBlank(authApp.getDescription()) && authApp.getDescription().length() > 255)
        {
            throw new ConstraintViolationException(null);
        }
    }
    
    private void checkAuthAppIdRule(AuthApp authApp)
    {
        if (StringUtils.isBlank(authApp.getAuthAppId()))
        {
            throw new ConstraintViolationException(null);
        }
        if (!authApp.getAuthAppId().matches("^[a-zA-Z]{1}[a-zA-Z0-9]+$"))
        {
            throw new ConstraintViolationException(null);
        }
        
        if (authApp.getAuthAppId().length() > 20 || authApp.getAuthAppId().length() < 4)
        {
            throw new ConstraintViolationException(null);
        }
    }
    
    /**
     * 
     * @param admin
     * @return
     */
    @RequestMapping(value = "modify", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> modify(AuthApp authApp, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        if (Constants.DISPLAY_STAR_VALUE.equals(authApp.getUfmSecretKey()))
        {
            AuthApp defaultApp = authAppService.getByAuthAppID(authApp.getAuthAppId());
            authApp.setUfmSecretKey(defaultApp.getUfmSecretKey());
        }
        try
        {
            validateParam(authApp);
        }
        catch (ConstraintViolationException e)
        {
            saveValidateLog(request, OperateType.UpdateAppAuth);
            throw e;
        }
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
