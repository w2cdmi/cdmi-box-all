package com.huawei.sharedrive.uam.cmb.oa.service;

import java.util.List;

import com.huawei.sharedrive.uam.cmb.oa.domain.CMBSapUser;

public interface CMBSapUserService
{
    void insert(CMBSapUser cmbSapUser);
    
    void updateById(CMBSapUser cmbSapUser);
    
    CMBSapUser getById(String sapId);
    
    List<CMBSapUser> getAll();
}
