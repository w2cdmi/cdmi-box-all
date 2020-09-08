package com.huawei.sharedrive.uam.enterprise.dao;

import com.huawei.sharedrive.uam.enterprise.domain.WebIconPcLogo;

public interface EnterpriseIndividualConfigDao
{
    void create(WebIconPcLogo webIconPcLogo);
    
    void update(WebIconPcLogo webIconPcLogo);
    
    WebIconPcLogo get(WebIconPcLogo webIconPcLogo);
    
    int getAccountId(WebIconPcLogo webIconPcLogo);

	void updateCorpright(WebIconPcLogo webIconPcLogo);
}
