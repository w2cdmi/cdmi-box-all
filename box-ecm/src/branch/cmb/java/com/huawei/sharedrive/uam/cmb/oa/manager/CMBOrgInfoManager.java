package com.huawei.sharedrive.uam.cmb.oa.manager;

import java.util.List;

import com.huawei.sharedrive.uam.cmb.oa.domain.CMBOrgInfo;

public interface CMBOrgInfoManager
{
    void insertCMBOrgInfo(List<CMBOrgInfo> list);
    
    CMBOrgInfo getCMBOrgInfo(Integer orgId);
    
    void getGroupIds(List<String> fatherGroupId, List<String> groupIds);
}
