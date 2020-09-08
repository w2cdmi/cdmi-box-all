package pw.cdmi.box.uam.common.web;

import java.security.InvalidParameterException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import pw.cdmi.box.uam.enterprise.manager.EnterpriseAccountManager;
import pw.cdmi.box.uam.enterprise.manager.EnterpriseManager;
import pw.cdmi.box.uam.exception.BusinessException;
import pw.cdmi.box.uam.exception.ForbiddenException;
import pw.cdmi.box.uam.exception.InvalidParamterException;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.user.domain.Admin;
import pw.cdmi.box.uam.util.CSRFTokenManager;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.log.SystemLog;

public abstract class AbstractCommonController
{
    @Autowired
    private EnterpriseManager enterpriseManager;
    
    @Autowired
    protected MessageSource messageSource;
    
    @Autowired
    protected Validator validator;
    
    @Autowired
    private EnterpriseAccountManager enterpriseAccountManager;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    /**
     * 
     * @param token
     */
    protected void checkToken(String token)
    {
        if (StringUtils.isBlank(token) || !token.equals(SecurityUtils.getSubject()
            .getSession()
            .getAttribute(CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME)))
        {
            throw new BusinessException(401, "invalid token");
        }
    }
    
    /**
     * 
     * @param appId
     */
    protected void permissionCheck(String appId)
    {
        // Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        // List<String> apps =
        // adminAppPermissionService.getAppByAdminId(sessAdmin.getId());
        // if (!apps.contains(appId))
        // {
        // throw new InvalidParameterException();
        // }
    }
    
    protected void permissionCheckEnterprise(Long enterpriseId)
    {
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        if (null == sessAdmin)
        {
            throw new InvalidParameterException("sessAdmin is null");
        }
        Long adminEnterpriseId = sessAdmin.getEnterpriseId();
        if (null != enterpriseId && !enterpriseId.equals(adminEnterpriseId))
        {
            throw new InvalidParameterException("enterpriseId is not correct enterpriseId:" + enterpriseId
                + " adminEnterpriseId:" + adminEnterpriseId);
        }
    }
    
    protected Long checkAdminAndGetId()
    {
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        if (null == sessAdmin)
        {
            throw new ForbiddenException("sessAdmin is null");
        }
        Long adminEnterpriseId = sessAdmin.getEnterpriseId();
        if (null == adminEnterpriseId)
        {
            throw new InvalidParamterException("adminEnterpriseId is null");
        }
        return adminEnterpriseId;
    }
    
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
    
    protected String getEnterpriseName()
    {
        long enterpriseId = checkAdminAndGetId();
        Enterprise enterprise = enterpriseManager.getById(enterpriseId);
        if (enterprise != null)
        {
            return enterprise.getName();
        }
        return null;
    }
    
    protected void saveValidateLog(HttpServletRequest req, OperateType operateType)
    {
        systemLogManager.saveFailLog(req, operateType, OperateDescription.CHECKCONFIG, null, null);
    }
    protected void saveValidateLog(SystemLog systemLog, OperateType operateType)
    {
        systemLogManager.saveFailLog(systemLog, operateType, OperateDescription.CHECKCONFIG, null, null);
    }
}
