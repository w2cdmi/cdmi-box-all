package pw.cdmi.box.uam.authapp.web;

import javax.servlet.http.HttpServletRequest;

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
import pw.cdmi.box.uam.exception.InvalidParamterException;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.system.service.SecurityService;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.common.domain.SecurityConfig;
import pw.cdmi.uam.domain.AuthApp;

@Controller
@RequestMapping(value = "/app/securityconfig")
public class AppSecurityConfigController extends AbstractCommonController
{
    @Autowired
    private SecurityService securityService;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @Autowired
    private AuthAppService authAppService;
    
    private static final String HTTP = "http";
    
    private static final String HTTPS = "https";
    
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> save(SecurityConfig securityConfig, String token, HttpServletRequest request)
    {
        
        if (!securityConfig.getProtocolType().equalsIgnoreCase(HTTP)
            && !securityConfig.getProtocolType().equalsIgnoreCase(HTTPS))
        {
            saveValidateLog(request, OperateType.ChangeCustom);
            throw new InvalidParamterException("Protocol type error");
        }
        super.checkToken(token);
        if (!checkParam(securityConfig))
        {
            saveValidateLog(request, OperateType.ChangeCustom);
            throw new InvalidParamterException("appId error" + securityConfig.getAppId());
        }
        String[] description = new String[]{securityConfig.getAppId(),
            String.valueOf(securityConfig.getLinkIsAnon()),
            String.valueOf(securityConfig.isDisableSimpleLinkCode()),
            String.valueOf(securityConfig.getProtocolType()), String.valueOf(securityConfig.getForgetPwd())};
        String id = systemLogManager.saveFailLog(request,
            OperateType.ChangeCustom,
            OperateDescription.ADMIN_MODIFY_SECURITY,
            null,
            description);
        securityService.saveSecurityConfig(securityConfig);
        systemLogManager.updateSuccess(id);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    private boolean checkParam(SecurityConfig securityConfig)
    {
        if (StringUtils.isBlank(securityConfig.getAppId()))
        {
            return false;
        }
        AuthApp authApp = authAppService.getByAuthAppID(securityConfig.getAppId());
        if (authApp == null)
        {
            return false;
        }
        if (authApp.getType() != Constants.AUTH_APP_TYPE)
        {
            return false;
        }
        
        return true;
    }
}
