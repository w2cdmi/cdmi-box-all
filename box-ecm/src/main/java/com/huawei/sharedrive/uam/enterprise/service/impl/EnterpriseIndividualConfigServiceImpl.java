package com.huawei.sharedrive.uam.enterprise.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.enterprise.dao.EnterpriseIndividualConfigDao;
import com.huawei.sharedrive.uam.enterprise.domain.WebIconPcLogo;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseIndividualConfigService;

@Service
public class EnterpriseIndividualConfigServiceImpl implements EnterpriseIndividualConfigService
{
    
    @Autowired
    private EnterpriseIndividualConfigDao enterpriseIndividualConfigDao;
    
    @Override
    public void create(WebIconPcLogo webIconPcLogo)
    {
        WebIconPcLogo webIcon = enterpriseIndividualConfigDao.get(webIconPcLogo);
        if (null == webIcon)
        {
            enterpriseIndividualConfigDao.create(webIconPcLogo);
        }
        else
        {
            enterpriseIndividualConfigDao.update(webIconPcLogo);
        }
        
    }
    
    @Override
    public void updateCorpright(WebIconPcLogo webIconPcLogo)
    {
    	enterpriseIndividualConfigDao.updateCorpright(webIconPcLogo);
    }
    
    
    @Override
    public WebIconPcLogo get(WebIconPcLogo webIconPcLogo)
    {
        return enterpriseIndividualConfigDao.get(webIconPcLogo);
    }
    
    @Override
    public int getAccountId(WebIconPcLogo webIconPcLogo)
    {
        return enterpriseIndividualConfigDao.getAccountId(webIconPcLogo);
    }
    
}
