package com.huawei.sharedrive.cloudapp.cmb.user.manager;

import java.util.List;

import com.huawei.sharedrive.cloudapp.cmb.user.domain.OrgTreeNode;
import com.huawei.sharedrive.cloudapp.cmb.user.domain.RetrieveUser;

public interface CMBOrgUserManager
{
    List<OrgTreeNode> listOrgTree(String fatherGroupId);
    
    List<RetrieveUser> listCmbUser(Integer orgId, String keyword);
}
