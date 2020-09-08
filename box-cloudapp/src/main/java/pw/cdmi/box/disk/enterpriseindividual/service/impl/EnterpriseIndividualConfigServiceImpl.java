package pw.cdmi.box.disk.enterpriseindividual.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.disk.enterpriseindividual.dao.EnterpriseIndividualConfigDao;
import pw.cdmi.box.disk.enterpriseindividual.domain.WebIconPcLogo;
import pw.cdmi.box.disk.enterpriseindividual.service.EnterpriseIndividualConfigService;

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
    public WebIconPcLogo get(WebIconPcLogo webIconPcLogo)
    {
        return enterpriseIndividualConfigDao.get(webIconPcLogo);
    }
    
    @Override
    public int getAccountId(WebIconPcLogo webIconPcLogo)
    {
        return enterpriseIndividualConfigDao.getAccountId(webIconPcLogo);
    }

    @Override
    public List<WebIconPcLogo> listForUpdate()
    {
        return enterpriseIndividualConfigDao.listForUpdate();
    }
    
}
