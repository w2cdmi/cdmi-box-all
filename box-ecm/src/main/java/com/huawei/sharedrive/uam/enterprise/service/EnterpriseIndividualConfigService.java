package com.huawei.sharedrive.uam.enterprise.service;

import com.huawei.sharedrive.uam.enterprise.domain.WebIconPcLogo;

public interface EnterpriseIndividualConfigService
{
    void create(WebIconPcLogo webIconPcLogo);
    
    WebIconPcLogo get(WebIconPcLogo webIconPcLogo);
    
    int getAccountId(WebIconPcLogo webIconPcLogo);

	void updateCorpright(WebIconPcLogo webIconPcLogo);
}
