package com.huawei.sharedrive.uam.enterpriseuser.manager;

import java.io.IOException;
import java.util.List;

import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;

import pw.cdmi.box.domain.Limit;

public interface SyncEnterpriseUserManager
{
    boolean syncEnterpriseUser(Long authServerId, LogOwner logOwner);
    
    void syncEnterpriseUser(EnterpriseUser enterpriseUser) throws IOException;
    
    int getByLdapStatusCount(Long enterpriseId);
    
    List<EnterpriseUser> listByLdapStatus(Long enterpriseId, Limit limit);
    
    void syncDeleteEnterpriseUser(Long enterpriseId);
}
