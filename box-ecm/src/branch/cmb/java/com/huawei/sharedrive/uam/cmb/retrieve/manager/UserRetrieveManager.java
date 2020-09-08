package com.huawei.sharedrive.uam.cmb.retrieve.manager;

import com.huawei.sharedrive.uam.cmb.openapi.domain.ResponseRetriewUser;
import com.huawei.sharedrive.uam.core.domain.Limit;

public interface UserRetrieveManager
{
    ResponseRetriewUser getUserByOrgId(String filter, Integer orgId, Long accountId, Long enterpriseId,
        Limit limit);
}
