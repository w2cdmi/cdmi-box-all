package com.huawei.sharedrive.uam.enterpriseuseraccount.dao;

import java.util.List;

import com.huawei.sharedrive.uam.enterpriseuseraccount.domain.EnterpriseUserAccount;

import pw.cdmi.box.domain.Limit;

public interface EnterpriseUserAccountDao
{
    List<EnterpriseUserAccount> getUser(long accountid, long enterpriseId, String filter, Limit limit);
    
    int getUserCount(long accountid, long enterpriseId, String filter, Limit limit);
}
