package com.huawei.sharedrive.uam.cmb.oa.manager;

import java.util.List;

import com.huawei.sharedrive.uam.cmb.oa.domain.CMBSapUser;

public interface CMBSapUserManager
{
    void insertCMBSapUser(List<CMBSapUser> list);
    
    List<CMBSapUser> getAll();
}
