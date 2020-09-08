package com.huawei.sharedrive.uam.enterprise.service.impl;

import com.huawei.sharedrive.uam.enterprise.dao.EnterpriseDao;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.enterprise.Enterprise;

import java.util.List;

@Component
public class EnterpriseServiceImpl implements EnterpriseService
{
    
    @Autowired
    private EnterpriseDao enterpriseDao;
    
    @Override
    public long create(Enterprise enterprise)
    {
        return enterpriseDao.create(enterprise);
        
    }
    
    @Override
    public boolean isDuplicateValues(Enterprise enterprise)
    {
        return enterpriseDao.isDuplicateValues(enterprise);
    }
    
    @Override
    public Page<Enterprise> getFilterd(String filter, String appId, PageRequest pageRequest)
    {
        int total = enterpriseDao.getFilterdCount(filter, appId);
        List<Enterprise> content = null;
        
        if (pageRequest != null)
        {
            content = enterpriseDao.getFilterd(filter, appId, pageRequest.getOrder(), pageRequest.getLimit());
        }
        else
        {
            content = enterpriseDao.getFilterd(filter, appId, null, null);
        }
        
        Page<Enterprise> page = new PageImpl<Enterprise>(content, pageRequest, total);
        return page;
        
    }
    
    @Override
    public Enterprise getById(long id)
    {
        
        return enterpriseDao.getById(id);
    }
    
    @Override
    public void updateStatus(Enterprise enterprise) {
        enterpriseDao.updateStatus(enterprise);
    }
    
    @Override
    public void updateEnterpriseInfo(Enterprise enterprise) {
        enterpriseDao.updateEnterpriseInfo(enterprise);
    }
    
    @Override
    public long getByDomainExclusiveId(Enterprise enterprise)
    {
        return enterpriseDao.getByDomainExclusiveId(enterprise);
    }
    
    @Override
    public void deleteById(long id)
    {
        enterpriseDao.deleteById(id);
        
    }
    
    @Override
    public Enterprise getByDomainName(String domainName)
    {
        return enterpriseDao.getByDomainName(domainName);
    }
    
    @Override
    public void updateNetworkAuthStatus(Byte networkAuthStatus, Long id)
    {
        enterpriseDao.updateNetworkAuthStatus(networkAuthStatus, id);
    }
    
    @Override
    public List<Enterprise> listForUpdate()
    {
        List<Enterprise> list = enterpriseDao.listForUpdate();
        return list;
    }

    @Override
    public Enterprise getByName(String name) {
        return  enterpriseDao.getByName(name);
    }
}
