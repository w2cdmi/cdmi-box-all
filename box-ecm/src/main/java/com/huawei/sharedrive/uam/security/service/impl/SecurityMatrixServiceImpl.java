package com.huawei.sharedrive.uam.security.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.security.dao.SecurityMatrixDAO;
import com.huawei.sharedrive.uam.security.domain.SecurityMatrix;
import com.huawei.sharedrive.uam.security.domain.SecurityMatrixQueryCondition;
import com.huawei.sharedrive.uam.security.service.SecurityMatrixService;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.config.service.ConfigManager;

@Component("securityMatrixService")
public class SecurityMatrixServiceImpl implements SecurityMatrixService
{
    
    @Autowired
    private SecurityMatrixDAO securityMatrixDAO;
    
    @Autowired
    private ConfigManager configManager;
    
    @Override
    public String insert(SecurityMatrix securityMatrix)
    {
        if (securityMatrixDAO.isExist(securityMatrix) == 0)
        {
            securityMatrixDAO.insert(securityMatrix);
            configManager.setConfig(SecurityMatrixServiceImpl.class.getSimpleName(), null);
            return RETURN_SUCCESS;
        }
        return RETURN_EXIST;
    }
    
    @Override
    public String delete(SecurityMatrix securityMatrix)
    {
        securityMatrixDAO.delete(securityMatrix);
        configManager.setConfig(SecurityMatrixServiceImpl.class.getSimpleName(), null);
        return RETURN_SUCCESS;
    }
    
    @Override
    public String update(SecurityMatrix securityMatrix, SecurityMatrix oldSecurityMatrix)
    {
        if (securityMatrixDAO.isExist(securityMatrix) == 0
            || securityMatrix.getRoleName().equals(oldSecurityMatrix.getRoleName()))
        {
            securityMatrixDAO.update(securityMatrix, oldSecurityMatrix);
            configManager.setConfig(SecurityMatrixServiceImpl.class.getSimpleName(), null);
            return RETURN_SUCCESS;
        }
        return RETURN_EXIST;
    }
    
    @Override
    public Page<SecurityMatrix> queryPage(SecurityMatrixQueryCondition queryCondition, PageRequest pageRequest)
    {
        int total = securityMatrixDAO.getFilterdCount(queryCondition);
        List<SecurityMatrix> content = securityMatrixDAO.getAll(queryCondition,
            pageRequest.getOrder(),
            pageRequest.getLimit());
        Page<SecurityMatrix> page = new PageImpl<SecurityMatrix>(content, pageRequest, total);
        return page;
    }
    
    @Override
    public SecurityMatrix getSecurityMatrix(SecurityMatrix securityMatrix)
    {
        return securityMatrixDAO.getSecurityMatrix(securityMatrix);
    }
    
    @Override
    public Integer isExist(SecurityMatrix securityMatrix)
    {
        return securityMatrixDAO.isExist(securityMatrix);
    }
    
    @Override
    public Integer isCiteByPermissionValue(String permissionValue)
    {
        return securityMatrixDAO.isCiteByPermissionValue(permissionValue);
    }
    
    @Override
    public List<SecurityMatrix> queryMatrixByNFactor(SecurityMatrix queryObject)
    {
        return securityMatrixDAO.queryMatrixByNFactor(queryObject);
    }
    
    @Override
    public Integer isCiteByResExtendType(Integer type, Integer code)
    {
        return securityMatrixDAO.isCiteByResExtendType(type, code);
    }
    
    public boolean hasEnabledSecurityMartix()
    {
        return securityMatrixDAO.hasEnabledSecurityMartix();
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public String getSecurityMartixRoleNames()
    {
        List roleNameList = securityMatrixDAO.getSecurityMartixRoleNames();
        if (roleNameList == null)
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        Map map = null;
        for (Object m : roleNameList)
        {
            map = (Map) m;
            if (i > 0)
            {
                sb.append(';');
            }
            sb.append(map.get("roleName"));
            i++;
        }
        
        return sb.toString();
    }
}
