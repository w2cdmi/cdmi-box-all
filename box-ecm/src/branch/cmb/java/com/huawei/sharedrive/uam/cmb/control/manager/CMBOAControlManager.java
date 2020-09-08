package com.huawei.sharedrive.uam.cmb.control.manager;

import com.huawei.sharedrive.common.domain.AuthServer;
import com.huawei.sharedrive.common.domain.enterprise.Enterprise;

public interface CMBOAControlManager
{
    Enterprise getEnterprise();
    
    AuthServer getAuthServer(long enterpriseId);
}
