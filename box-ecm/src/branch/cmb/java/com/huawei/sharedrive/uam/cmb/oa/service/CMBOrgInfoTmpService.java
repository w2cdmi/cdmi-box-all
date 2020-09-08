package com.huawei.sharedrive.uam.cmb.oa.service;

import java.util.List;

import com.huawei.sharedrive.uam.cmb.oa.domain.CMBOrgInfo;

public interface CMBOrgInfoTmpService
{
    void insert(CMBOrgInfo cmbOrgInfo);
    
    List<CMBOrgInfo> getAll();
    
    void deleteAll();
}
