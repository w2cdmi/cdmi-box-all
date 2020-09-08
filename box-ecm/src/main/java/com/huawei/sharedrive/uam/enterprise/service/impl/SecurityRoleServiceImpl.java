package com.huawei.sharedrive.uam.enterprise.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.uam.enterprise.dao.AccessConfigDao;
import com.huawei.sharedrive.uam.enterprise.dao.AccessSpaceConfigDao;
import com.huawei.sharedrive.uam.enterprise.dao.FileCopySecurityDao;
import com.huawei.sharedrive.uam.enterprise.dao.ResourceStrategyDao;
import com.huawei.sharedrive.uam.enterprise.dao.SecurityRoleDao;
import com.huawei.sharedrive.uam.enterprise.domain.AccessConfig;
import com.huawei.sharedrive.uam.enterprise.domain.AccessSpaceConfig;
import com.huawei.sharedrive.uam.enterprise.domain.FileCopySecurity;
import com.huawei.sharedrive.uam.enterprise.domain.ResourceStrategy;
import com.huawei.sharedrive.uam.enterprise.domain.SecurityRole;
import com.huawei.sharedrive.uam.enterprise.service.SecurityRoleService;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;

@Component
public class SecurityRoleServiceImpl implements SecurityRoleService
{
    
    @Autowired
    private SecurityRoleDao securityRoleDao;
    
    @Autowired
    private AccessConfigDao accessConfigDao;
    
    @Autowired
    private ResourceStrategyDao resourceStrategyDao;
    
    @Autowired
    private AccessSpaceConfigDao accessSpaceConfigDao;
    
    @Autowired
    private FileCopySecurityDao fileCopySecurityDao;
    
    @Override
    public long create(SecurityRole securityRole)
    {
        Date now = new Date();
        securityRole.setCreatedAt(now);
        securityRole.setModifiedAt(now);
        return securityRoleDao.create(securityRole);
        
    }
    
    @Override
    public boolean isDuplicateValues(SecurityRole securityRole)
    {
        return securityRoleDao.isDuplicateValues(securityRole);
    }
    
    @Override
    public Page<SecurityRole> getFilterd(SecurityRole securityRole, PageRequest pageRequest)
    {
        
        int total = securityRoleDao.getFilterdCount(securityRole);
        List<SecurityRole> content = securityRoleDao.getFilterd(securityRole,
            pageRequest.getOrder(),
            pageRequest.getLimit());
        
        for (SecurityRole sr : content)
        {
            sr.setRoleName(HtmlUtils.htmlEscape(sr.getRoleName()));
            sr.setRoleDesc(HtmlUtils.htmlEscape(sr.getRoleDesc()));
        }
        Page<SecurityRole> page = new PageImpl<SecurityRole>(content, pageRequest, total);
        return page;
        
    }
    
    @Override
    public SecurityRole getById(long id)
    {
        
        return securityRoleDao.getById(id);
    }
    
    @Override
    public void updateSecurityRole(SecurityRole securityRole)
    {
        Date now = new Date();
        securityRole.setModifiedAt(now);
        securityRoleDao.updateEnterpriseInfo(securityRole);
        
    }
    
    @Override
    public long getByDomainExclusiveId(SecurityRole securityRole)
    {
        return securityRoleDao.getByDomainExclusiveId(securityRole);
    }
    
    @Override
    public List<SecurityRole> getFilterdList(SecurityRole filter)
    {
        
        List<SecurityRole> content = securityRoleDao.getFilterd(filter, null, null);
        return content;
        
    }
    
    @Override
    public void delete(SecurityRole securityRole)
    {
        long accountId = securityRole.getAccountId();
        long securityRoleId = securityRole.getId();
        
        AccessConfig accessConfig = new AccessConfig();
        accessConfig.setAccountId(accountId);
        accessConfig.setSafeRoleId(securityRoleId);
        int accessConfigCount = accessConfigDao.getFilterdCount(accessConfig);
        if (accessConfigCount > 0)
        {
            throw new InvalidParamterException("the role" + securityRoleId
                + " has been configured in access config strategy");
        }
        
        ResourceStrategy resourceStrategy = new ResourceStrategy();
        resourceStrategy.setAccountId(accountId);
        resourceStrategy.setSecurityRoleId(securityRoleId);
        int resourceStrategyCount = resourceStrategyDao.getFilterdCount(resourceStrategy);
        if (resourceStrategyCount > 0)
        {
            throw new InvalidParamterException("the role" + securityRoleId
                + " has been configured in resource strategy");
        }
        
        AccessSpaceConfig accessSpaceConfig = new AccessSpaceConfig();
        accessSpaceConfig.setAccountId(accountId);
        accessSpaceConfig.setSafeRoleId(securityRoleId);
        int accessSpaceConfigCount = accessSpaceConfigDao.getFilterdCount(accessSpaceConfig);
        if (accessSpaceConfigCount > 0)
        {
            throw new InvalidParamterException("the role" + securityRoleId
                + " has been configured in space strategy");
        }
        
        FileCopySecurity fileCopySecurity = new FileCopySecurity();
        fileCopySecurity.setAccountId(accountId);
        fileCopySecurity.setSrcSafeRoleId(securityRoleId);
        fileCopySecurity.setTargetSafeRoleId(securityRoleId);
        int fileCopySecurityCount = fileCopySecurityDao.getFilterdCount(fileCopySecurity);
        if (fileCopySecurityCount > 0)
        {
            throw new InvalidParamterException("the role " + securityRoleId
                + "has been configured in the copy strategy");
        }
        
        securityRoleDao.delete(securityRole);
    }
}
