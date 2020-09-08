package com.huawei.sharedrive.uam.cmb.oa.dao;

import java.util.List;

import com.huawei.sharedrive.uam.cmb.oa.domain.CMBSapUser;

public interface CMBSapUserTmpDao
{
    void insert(CMBSapUser cmbSapUserTmp);
    
    List<CMBSapUser> getAll();
    
    void deleteAll();
}
