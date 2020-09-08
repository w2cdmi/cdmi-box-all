package pw.cdmi.box.disk.enterpriseindividual.dao;

import java.util.List;

import pw.cdmi.box.disk.enterpriseindividual.domain.WebIconPcLogo;

public interface EnterpriseIndividualConfigDao
{
    void create(WebIconPcLogo webIconPcLogo);
    
    void update(WebIconPcLogo webIconPcLogo);
    
    WebIconPcLogo get(WebIconPcLogo webIconPcLogo);
    
    int getAccountId(WebIconPcLogo webIconPcLogo);
    
    List<WebIconPcLogo> listForUpdate();
}
