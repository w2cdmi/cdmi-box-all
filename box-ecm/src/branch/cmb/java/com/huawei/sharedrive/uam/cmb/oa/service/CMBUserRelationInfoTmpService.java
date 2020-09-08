package com.huawei.sharedrive.uam.cmb.oa.service;

import java.util.List;

import com.huawei.sharedrive.uam.cmb.oa.domain.CMBUserRelationInfo;

public interface CMBUserRelationInfoTmpService
{
    void insert(CMBUserRelationInfo cmbUserRelationInfo);
    
    List<CMBUserRelationInfo> getAll();
    
    void deleteAll();
}
