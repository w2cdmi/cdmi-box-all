package com.huawei.sharedrive.uam.cmb.retrieve.manager;

import com.huawei.sharedrive.uam.cmb.openapi.domain.ResponseOrgTreeNode;
import com.huawei.sharedrive.uam.core.domain.Limit;

public interface OrgRetrieveManager
{
    ResponseOrgTreeNode getAllOrgInfo(String fatherGroupId, Limit limit);
}
