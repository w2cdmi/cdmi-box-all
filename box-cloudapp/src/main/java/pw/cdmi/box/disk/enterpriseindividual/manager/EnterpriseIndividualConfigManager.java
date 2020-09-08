package pw.cdmi.box.disk.enterpriseindividual.manager;

import pw.cdmi.box.disk.enterpriseindividual.domain.WebIconPcLogo;
import pw.cdmi.common.domain.CustomizeLogo;

public interface EnterpriseIndividualConfigManager
{
    void getExitImageFile(CustomizeLogo customize, WebIconPcLogo webIconPcLogo);
    
    int getAccountId(WebIconPcLogo webIconPcLogo);
}
