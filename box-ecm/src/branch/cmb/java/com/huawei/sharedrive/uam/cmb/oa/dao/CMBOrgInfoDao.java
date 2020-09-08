package com.huawei.sharedrive.uam.cmb.oa.dao;

import java.util.List;

import com.huawei.sharedrive.uam.cmb.oa.domain.CMBOrgInfo;
import com.huawei.sharedrive.uam.core.domain.Limit;

public interface CMBOrgInfoDao
{
    void insert(CMBOrgInfo cmbOrgInfo);
    
    void updateById(CMBOrgInfo cmbOrgInfo);
    
    CMBOrgInfo getById(Integer orgId);
    
    int getByFatherGroupIdCount(String fatherGroupId);
    
    List<CMBOrgInfo> getByFatherGroupId(String fatherGroupId, Limit limit);
    
    List<String> getByExistsFatherGroupId(String fatherGroupId);
}
