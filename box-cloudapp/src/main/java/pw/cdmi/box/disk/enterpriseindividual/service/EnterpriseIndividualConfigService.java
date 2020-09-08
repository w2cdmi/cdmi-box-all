package pw.cdmi.box.disk.enterpriseindividual.service;

import java.util.List;

import pw.cdmi.box.disk.enterpriseindividual.domain.WebIconPcLogo;

public interface EnterpriseIndividualConfigService
{
    void create(WebIconPcLogo webIconPcLogo);
    
    WebIconPcLogo get(WebIconPcLogo webIconPcLogo);
    
    int getAccountId(WebIconPcLogo webIconPcLogo);
    
    List<WebIconPcLogo> listForUpdate();
}
