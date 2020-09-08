package com.huawei.sharedrive.uam.cmb.oa.dao;

import java.util.List;

import com.huawei.sharedrive.uam.cmb.oa.domain.CMBUserRelationInfo;

public interface CMBUserRelationInfoTmpDao
{
    void insert(CMBUserRelationInfo cmbUserRelationInfo);
    
    List<CMBUserRelationInfo> getAll();
    
    void deleteAll();
}
