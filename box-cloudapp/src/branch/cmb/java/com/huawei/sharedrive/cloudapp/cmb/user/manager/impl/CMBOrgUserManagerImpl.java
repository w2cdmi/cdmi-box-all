package com.huawei.sharedrive.cloudapp.cmb.user.manager.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.cloudapp.cmb.user.domain.OrgTreeNode;
import com.huawei.sharedrive.cloudapp.cmb.user.domain.RetrieveUser;
import com.huawei.sharedrive.cloudapp.cmb.user.manager.CMBOrgUserManager;
import com.huawei.sharedrive.cloudapp.cmb.user.service.CMBOrgUserService;

@Component
public class CMBOrgUserManagerImpl implements CMBOrgUserManager
{
    @Autowired
    private CMBOrgUserService cMBOrgUserService;
    
    @Override
    public List<OrgTreeNode> listOrgTree(String fatherGroupId)
    {
        return cMBOrgUserService.listOrgTree(fatherGroupId);
    }
    
    @Override
    public List<RetrieveUser> listCmbUser(Integer orgId, String keyword)
    {
        return cMBOrgUserService.listCmbUser(orgId, keyword);
    }
}
