package com.huawei.sharedrive.uam.enterprise.manager.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.user.domain.Admin;

import pw.cdmi.common.domain.enterprise.EnterpriseAccount;

@Component
public class EnterpriseAccountManagerImpl implements EnterpriseAccountManager
{
    @Autowired
    private EnterpriseAccountService enterpriseAccountService;
    
    @Override
    public EnterpriseAccount getByAccessKeyId(String accessKeyId)
    {
        return enterpriseAccountService.getByAccessKeyId(accessKeyId);
    }
    
    @Override
    public List<EnterpriseAccount> getByEnterpriseId(long enterpriseId)
    {
        return enterpriseAccountService.getByEnterpriseId(enterpriseId);
    }
    
    @Override
    public EnterpriseAccount getByEnterpriseApp(long enterpriseId, String authAppId)
    {
        return enterpriseAccountService.getByEnterpriseApp(enterpriseId, authAppId);
    }
    
    @Override
    public void bindAppCheck(String authAppId)
    {
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        long enterpriseId = sessAdmin.getEnterpriseId();
        EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByEnterpriseApp(enterpriseId,
            authAppId);
        if (null == enterpriseAccount)
        {
            throw new InvalidParamterException("enterprise is null, current enterprise has not bind the app");
        }
    }
    
    @Override
    public void bindAppCheck()
    {
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        Boolean isBindApp = (Boolean) SecurityUtils.getSubject().getSession().getAttribute("isBindApp");
        if (null != isBindApp && isBindApp)
        {
            throw new InvalidParamterException("enterprise is null, current enterprise has not bind the app");
        }
        List<EnterpriseAccount> enterpriseAccount = enterpriseAccountService.getByEnterpriseId(sessAdmin.getEnterpriseId());
        if (CollectionUtils.isEmpty(enterpriseAccount))
        {
            SecurityUtils.getSubject().getSession().setAttribute("isBindApp", true);
            throw new InvalidParamterException("enterprise is null, current enterprise has not bind the app");
        }
        SecurityUtils.getSubject().getSession().setAttribute("isBindApp", false);
    }
    
    @Override
    public EnterpriseAccount getByAccountId(long accountId)
    {
        return enterpriseAccountService.getByAccountId(accountId);
    }
    
}
