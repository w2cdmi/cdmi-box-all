package com.huawei.sharedrive.uam.security.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.security.dao.SecurityFactorDAO;
import com.huawei.sharedrive.uam.security.dao.SecurityMatrixDAO;
import com.huawei.sharedrive.uam.security.domain.SecurityFactor;
import com.huawei.sharedrive.uam.security.service.SecurityFactorService;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;

@Component("securityFactorService")
public class SecurityFactorServiceImpl implements SecurityFactorService
{
    
    @Autowired
    private SecurityFactorDAO securityFactorDAO;
    
    @Autowired
    private SecurityMatrixDAO securityMatrixDAO;
    
    @Override
    public String insert(SecurityFactor securityFactor)
    {
        if (securityFactorDAO.isExist(securityFactor) == 0)
        {
            securityFactorDAO.insert(securityFactor);
            return RETURN_SUCCESS;
        }
        return RETURN_EXIST;
    }
    
    @Override
    public String delete(Integer type, Integer code)
    {
        Integer count = securityMatrixDAO.isCiteByResExtendType(type, code);
        if (count == 0)
        {
            securityFactorDAO.delete(type, code);
            return RETURN_SUCCESS;
        }
        return RETURN_QUOTE;
    }
    
    @Override
    public String update(SecurityFactor securityFactor, SecurityFactor oldSecurityFactor)
    {
        if (securityFactor.getType().getType() == oldSecurityFactor.getType().getType()
            && securityFactor.getCode().intValue() == oldSecurityFactor.getCode()
            && securityFactor.getName().equals(oldSecurityFactor.getName()))
        {
            securityFactorDAO.update(securityFactor, oldSecurityFactor);
            return RETURN_SUCCESS;
        }
        
        Integer count = securityMatrixDAO.isCiteByResExtendType(oldSecurityFactor.getType().getType(),
            oldSecurityFactor.getCode());
        if (count == 0)
        {
            if ((oldSecurityFactor.getType().getType() == securityFactor.getType().getType())
                && (oldSecurityFactor.getCode().equals(securityFactor.getCode())))
            {
                securityFactorDAO.update(securityFactor, oldSecurityFactor);
                return RETURN_SUCCESS;
            }
            if (securityFactorDAO.isExist(securityFactor) == 0)
            {
                securityFactorDAO.update(securityFactor, oldSecurityFactor);
                return RETURN_SUCCESS;
            }
            return RETURN_EXIST;
        }
        return RETURN_QUOTE;
    }
    
    @Override
    public Page<SecurityFactor> queryPage(SecurityFactor securityFactor, PageRequest pageRequest)
    {
        int total = securityFactorDAO.getFilterdCount(securityFactor);
        List<SecurityFactor> content = securityFactorDAO.getAll(securityFactor,
            pageRequest.getOrder(),
            pageRequest.getLimit());
        Page<SecurityFactor> page = new PageImpl<SecurityFactor>(content, pageRequest, total);
        return page;
    }
    
    @Override
    public Integer isExist(SecurityFactor securityFactor)
    {
        return securityFactorDAO.isExist(securityFactor);
    }
    
    @Override
    public SecurityFactor getSecurityFactorByCode(Integer type, Integer code)
    {
        return securityFactorDAO.getSecurityFactorByCode(type, code);
    }
    
    @Override
    public SecurityFactor getSecurityFactorByName(Integer type, String name)
    {
        return securityFactorDAO.getSecurityFactorByName(type, name);
    }
}
