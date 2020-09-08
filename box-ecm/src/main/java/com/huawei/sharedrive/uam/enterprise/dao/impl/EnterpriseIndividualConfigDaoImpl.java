package com.huawei.sharedrive.uam.enterprise.dao.impl;

import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterprise.dao.EnterpriseIndividualConfigDao;
import com.huawei.sharedrive.uam.enterprise.domain.WebIconPcLogo;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

@SuppressWarnings({"deprecation"})
@Component
public class EnterpriseIndividualConfigDaoImpl extends CacheableSqlMapClientDAO implements
    EnterpriseIndividualConfigDao
{
    
    @Override
    public void create(WebIconPcLogo webIconPcLogo)
    {
        sqlMapClientTemplate.insert("WebIconPcLogo.insert", webIconPcLogo);
    }
    
    @Override
    public void update(WebIconPcLogo webIconPcLogo)
    {
        sqlMapClientTemplate.insert("WebIconPcLogo.update", webIconPcLogo);
    }
    
    @Override
    public void updateCorpright(WebIconPcLogo webIconPcLogo)
    {
        sqlMapClientTemplate.insert("WebIconPcLogo.updateCorpright", webIconPcLogo);
    }
    
    @Override
    public WebIconPcLogo get(WebIconPcLogo webIconPcLogo)
    {
        return (WebIconPcLogo) sqlMapClientTemplate.queryForObject("WebIconPcLogo.get", webIconPcLogo);
    }
    
    @Override
    public int getAccountId(WebIconPcLogo webIconPcLogo)
    {
        return (Integer) sqlMapClientTemplate.queryForObject("WebIconPcLogo.getAccountId", webIconPcLogo);
    }
}
