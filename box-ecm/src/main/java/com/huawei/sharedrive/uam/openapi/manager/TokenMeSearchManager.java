package com.huawei.sharedrive.uam.openapi.manager;

import com.huawei.sharedrive.uam.openapi.domain.user.ResponseSearchUser;

public interface TokenMeSearchManager
{
    @SuppressWarnings("PMD.ExcessiveParameterList")
    ResponseSearchUser listLocalUser(Integer limit, Integer offset, String filter, long accountId,
        long enterpriseId, String appId);
    
    ResponseSearchUser listADUser(String appId, String filter, Integer limit, long accountId,
        long enterpriseId);
    
    ResponseSearchUser listADLocalUser(String appId, String filter, Integer limit, long accountId,
        long enterpriseId);
}
