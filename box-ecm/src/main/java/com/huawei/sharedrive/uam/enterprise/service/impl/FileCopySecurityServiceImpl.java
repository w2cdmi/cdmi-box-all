package com.huawei.sharedrive.uam.enterprise.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterprise.dao.FileCopySecurityDao;
import com.huawei.sharedrive.uam.enterprise.dao.SecurityRoleDao;
import com.huawei.sharedrive.uam.enterprise.domain.FileCopyDelRequest;
import com.huawei.sharedrive.uam.enterprise.domain.FileCopySecurity;
import com.huawei.sharedrive.uam.enterprise.domain.SecurityRole;
import com.huawei.sharedrive.uam.enterprise.service.FileCopySecurityService;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

@Component
public class FileCopySecurityServiceImpl implements FileCopySecurityService
{
    
    @Autowired
    private FileCopySecurityDao fileCopySecurityDao;
    
    @Autowired
    private SecurityRoleDao securityDao;
    
    @Override
    public void create(FileCopySecurity copySecurity)
    {
        checkSecurityRoleIdRule(copySecurity.getAccountId(),
            copySecurity.getSrcSafeRoleId(),
            copySecurity.getTargetSafeRoleId());
        Date date = new Date();
        copySecurity.setCreatedAt(date);
        copySecurity.setModifiedAt(date);
        
        fileCopySecurityDao.create(copySecurity);
    }
    
    @Override
    public void modify(FileCopySecurity copySecurity)
    {
        checkSecurityRoleIdRule(copySecurity.getAccountId(),
            copySecurity.getSrcSafeRoleId(),
            copySecurity.getTargetSafeRoleId());
        copySecurity.setModifiedAt(new Date());
        fileCopySecurityDao.modify(copySecurity);
    }
    
    @Override
    public int delete(FileCopyDelRequest fileCopyDelRequest, Long accountId)
    {
        return fileCopySecurityDao.delete(fileCopyDelRequest.getSrcSafeRoleId(),
            fileCopyDelRequest.getTargetSafeRoleId(),
            accountId);
    }
    
    @Override
    public List<FileCopySecurity> query(Limit limit, Order order, FileCopySecurity copySecurity)
    {
        return fileCopySecurityDao.query(limit, order, copySecurity);
    }
    
    @Override
    public int queryCount(Limit limit, FileCopySecurity copySecurity)
    {
        return fileCopySecurityDao.queryCount(limit, copySecurity);
    }
    
    @Override
    public FileCopySecurity get(FileCopySecurity copySecurity)
    {
        return fileCopySecurityDao.get(copySecurity);
    }
    
    @Override
    public List<FileCopySecurity> getByAccountId(long accountId)
    {
        return fileCopySecurityDao.getByAccountId(accountId);
    }
    
    private void checkSecurityRoleIdRule(long accountId, long srcSafeRoleId, long targetSafeRoleId)
    {
        SecurityRole sr = new SecurityRole();
        sr.setAccountId(accountId);
        List<SecurityRole> listSecurityRole = securityDao.getFilterd(sr, null, null);
        
        checkSrcSafeRoleId(srcSafeRoleId, listSecurityRole);
        checkTargetSafeRoleId(targetSafeRoleId, listSecurityRole);
    }
    
    private void checkTargetSafeRoleId(long targetSafeRoleId, List<SecurityRole> listSecurityRole)
    {
        boolean isAllowed = false;
        if (targetSafeRoleId == -1)
        {
            return;
        }
        for (SecurityRole s : listSecurityRole)
        {
            if (s.getId() == targetSafeRoleId)
            {
                isAllowed = true;
                break;
            }
        }
        if (!isAllowed)
        {
            throw new InvalidParamterException();
        }
    }
    
    private void checkSrcSafeRoleId(long srcSafeRoleId, List<SecurityRole> listSecurityRole)
    {
        boolean isAllowed = false;
        if (srcSafeRoleId == -1)
        {
            return;
        }
        
        for (SecurityRole s : listSecurityRole)
        {
            if (s.getId() == srcSafeRoleId)
            {
                isAllowed = true;
                break;
            }
        }
        if (!isAllowed)
        {
            throw new InvalidParamterException();
        }
    }
    
}
