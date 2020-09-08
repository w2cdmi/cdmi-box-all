package com.huawei.sharedrive.uam.common.web;

import java.security.InvalidParameterException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.exception.ForbiddenException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.log.domain.OperateDescription;
import com.huawei.sharedrive.uam.log.domain.OperateType;
import com.huawei.sharedrive.uam.log.manager.SystemLogManager;
import com.huawei.sharedrive.uam.user.domain.Admin;
import com.huawei.sharedrive.uam.util.CSRFTokenManager;

import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;

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
    private AdminLogManager adminLogManager;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    /**
     * 
     * @param token
     */
    protected void checkToken(String token)
    {
        if (StringUtils.isBlank(token)
            || !token.equals(SecurityUtils.getSubject()
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
    }
    
    protected void permissionCheckEnterprise(Long enterpriseId)
    {
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        if (null == sessAdmin)
        {
            throw new InvalidParameterException("sessAdmin is null");
        }
        Long adminEnterpriseId = sessAdmin.getEnterpriseId();
        if (null != enterpriseId && enterpriseId.longValue() != adminEnterpriseId.longValue())
        {
            throw new InvalidParameterException("enterpriseId is not correct enterpriseId:" + enterpriseId
                + " adminEnterpriseId:" + adminEnterpriseId);
        }
    }
    
    protected Admin checkAdminAndGet()
    {
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        if (null == admin)
        {
            throw new ForbiddenException("sessAdmin is null");
        }

        return admin;
    }
    
    protected long checkAdminAndGetId()
    {
        return checkAdminAndGet().getEnterpriseId();
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
    
    protected void saveValidateLog(LogOwner owner, AdminLogType adminlogType)
    {
        adminLogManager.saveAdminLog(owner, adminlogType, null);
    }
    
    protected void saveValidateLog(HttpServletRequest request)
    {
        systemLogManager.save(request,
            OperateType.ChangeBasicConfig,
            OperateDescription.APP_BASIC_CONFIG,
            null,
            null);
    }
}
