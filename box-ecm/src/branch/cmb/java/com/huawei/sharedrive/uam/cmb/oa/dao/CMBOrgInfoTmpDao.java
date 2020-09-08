package com.huawei.sharedrive.uam.cmb.oa.dao;

import java.util.List;

import com.huawei.sharedrive.uam.cmb.oa.domain.CMBOrgInfo;

public interface CMBOrgInfoTmpDao
{
    void insert(CMBOrgInfo cmbOrgInfo);
    
    List<CMBOrgInfo> getAll();
    
    void deleteAll();
}
