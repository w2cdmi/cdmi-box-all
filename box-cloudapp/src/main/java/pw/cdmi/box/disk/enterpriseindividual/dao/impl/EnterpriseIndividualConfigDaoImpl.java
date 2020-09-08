package pw.cdmi.box.disk.enterpriseindividual.dao.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.disk.enterpriseindividual.dao.EnterpriseIndividualConfigDao;
import pw.cdmi.box.disk.enterpriseindividual.domain.WebIconPcLogo;

@SuppressWarnings({"deprecation", "unchecked"})
@Component
public class EnterpriseIndividualConfigDaoImpl extends AbstractDAOImpl implements
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
    public WebIconPcLogo get(WebIconPcLogo webIconPcLogo)
    {
        return (WebIconPcLogo) sqlMapClientTemplate.queryForObject("WebIconPcLogo.get", webIconPcLogo);
    }
    
    @Override
    public int getAccountId(WebIconPcLogo webIconPcLogo)
    {
        return (Integer) sqlMapClientTemplate.queryForObject("WebIconPcLogo.getAccountId", webIconPcLogo);
    }
    
    @Override
    public List<WebIconPcLogo> listForUpdate()
    {
        return sqlMapClientTemplate.queryForList("WebIconPcLogo.listForUpdate");
    }
    
}
