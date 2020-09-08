package com.huawei.sharedrive.uam.cmb.oa.service;

import java.util.List;

import com.huawei.sharedrive.uam.cmb.oa.domain.CMBOrgInfo;
import com.huawei.sharedrive.uam.core.domain.Limit;

public interface CMBOrgInfoService
{
    void insert(CMBOrgInfo cmbOrgInfo);
    
    void updateById(CMBOrgInfo cmbOrgInfo);
    
    CMBOrgInfo getById(Integer orgId);
    
    List<CMBOrgInfo> getByFatherGroupId(String fatherGroupId, Limit limit);
    
    int getByFatherGroupIdCount(String fatherGroupId);
    
    List<String> getByExistsFatherGroupId(String fatherGroupId);
}
