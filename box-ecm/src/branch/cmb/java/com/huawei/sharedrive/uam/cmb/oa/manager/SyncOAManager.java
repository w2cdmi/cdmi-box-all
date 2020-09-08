package com.huawei.sharedrive.uam.cmb.oa.manager;

import java.io.IOException;

import com.huawei.sharedrive.common.domain.AuthServer;
import com.huawei.sharedrive.common.domain.enterprise.Enterprise;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBSapUser;

public interface SyncOAManager
{
    void syncUser();
    
    void createUpdateCMBSapUser(CMBSapUser cmbSapUser, Enterprise enterprise, AuthServer authServer)
        throws IOException;
}
