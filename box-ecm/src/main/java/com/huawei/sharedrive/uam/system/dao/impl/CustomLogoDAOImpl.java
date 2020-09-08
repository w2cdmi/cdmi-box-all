package com.huawei.sharedrive.uam.system.dao.impl;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.system.dao.CustomizeLogoDAO;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.common.domain.CustomizeLogo;

@Service("CustomLogoDAO")
@SuppressWarnings("deprecation")
public class CustomLogoDAOImpl extends AbstractDAOImpl implements CustomizeLogoDAO
{
    
    @Override
    public CustomizeLogo get(int id)
    {
        return (CustomizeLogo) sqlMapClientTemplate.queryForObject("CustomizeLogo.get", id);
    }
    
    @Override
    public void update(CustomizeLogo customizeLogo)
    {
        sqlMapClientTemplate.update("CustomizeLogo.update", customizeLogo);
    }
    
    @Override
    public void insert(CustomizeLogo customizeLogo)
    {
        sqlMapClientTemplate.update("CustomizeLogo.insert", customizeLogo);
    }
}
