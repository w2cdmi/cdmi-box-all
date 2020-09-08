package com.huawei.sharedrive.uam.cmb.oa.service;

import java.util.List;

import com.huawei.sharedrive.uam.cmb.oa.domain.CMBSapUser;

public interface CMBSapUserTmpService
{
    void insert(CMBSapUser cmbSapUserTmp);
    
    List<CMBSapUser> getAll();
    
    void deleteAll();
}
