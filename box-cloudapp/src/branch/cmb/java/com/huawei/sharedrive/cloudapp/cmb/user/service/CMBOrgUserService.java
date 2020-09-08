package com.huawei.sharedrive.cloudapp.cmb.user.service;

import java.util.List;

import com.huawei.sharedrive.cloudapp.cmb.user.domain.OrgTreeNode;
import com.huawei.sharedrive.cloudapp.cmb.user.domain.RetrieveUser;

public interface CMBOrgUserService
{
    List<OrgTreeNode> listOrgTree(String fatherGroupId);
    
    List<RetrieveUser> listCmbUser(Integer orgId, String keyword);
}
