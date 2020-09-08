package com.huawei.sharedrive.uam.cmb.retrieve.dao;

import java.util.List;

import com.huawei.sharedrive.uam.cmb.retrieve.domain.RetrieveUser;
import com.huawei.sharedrive.uam.core.domain.Limit;

public interface RetrieveUserDao
{
    int getRetrieveUserCount(String filter, Integer orgId, Long accountId, Long enterpriseId);
    
    List<RetrieveUser> getRetrieveUserList(String filter, Integer orgId, Long accountId, Long enterpriseId,
        Limit limit);
}
